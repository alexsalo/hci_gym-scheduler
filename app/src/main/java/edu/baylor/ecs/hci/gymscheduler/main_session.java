package edu.baylor.ecs.hci.gymscheduler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class main_session extends ActionBarActivity {
    public static final String RESULT_FILENAME = "time_stamps.csv";
    public static final String RESULT_DIR = "/hci_test";
    public ArrayList<String> groups = new ArrayList<>();
    public static int NUM_EXERCISES = 9;
    public int current_ex = 0;
    TextView[] tv_exs = new TextView[9];
    static int tv_ex_names[]={R.id.tv_ex_1,R.id.tv_ex_2,R.id.tv_ex_3,R.id.tv_ex_4
            ,R.id.tv_ex_5,R.id.tv_ex_6,R.id.tv_ex_7,R.id.tv_ex_8,R.id.tv_ex_9};
    HashMap group_ex_left = new HashMap<String, Integer>();
    TextView tv_cur_ex;
    TextView tv_ex_desc;
    TextView tv_ex_station;
    boolean Started = false;
    Button btn_start_stop;
    ArrayList<Long> time_stamps = new ArrayList<>();
    long mStartTime = System.currentTimeMillis();
    boolean mode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_session);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv_cur_ex = (TextView) findViewById(R.id.tv_cur_ext);
        tv_ex_desc = (TextView) findViewById(R.id.tv_ex_desc);
        tv_ex_station = (TextView) findViewById(R.id.tv_ex_station);
        btn_start_stop = (Button) findViewById(R.id.btn_next);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long millis = System.currentTimeMillis() - mStartTime;
                                int seconds = (int) (millis / 1000);
                                int minutes = seconds / 60;
                                seconds     = seconds % 60;
                                String baseText;
                                if (Started){
                                    baseText = "Stop";
                                }else{
                                    baseText = "Start";
                                }
                                if (seconds < 10) {
                                    btn_start_stop.setText(baseText +"\n" + minutes + ":0" + seconds);
                                } else {
                                    btn_start_stop.setText(baseText +"\n" + minutes + ":" + seconds);
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        Intent intent = getIntent();
        groups  = intent.getStringArrayListExtra(random_workout_select_groups.EXTRA_MESSAGE);
        if (groups == null)
            mode = false;

        for (int k = 0; k < 9; k++) {
            tv_exs[k] = (TextView) findViewById(tv_ex_names[k]);
        }
        tv_exs[0].setBackgroundColor(Color.parseColor("#ff4444"));
        time_stamps.add(System.currentTimeMillis());

        if (mode) {
            for (String group : groups){
                group_ex_left.put(group, 3);
            }
            try {
                Socket server = new Socket("wind.ecs.baylor.edu", 4444);
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

                out.println("ANDROID DEVICE");
                String accepted = in.readLine();
                if (accepted.compareTo("ANDROID ACCEPTED") == 0) {
                    Toast.makeText(main_session.this, "Next Exercise is given", Toast.LENGTH_SHORT).show();
                    out.println("GET EXERCISE FOR");
                    String group = groups.get(0 + (int) (Math.random() * 3));
                    out.println(group);
                    group_ex_left.put(group, 2);
                    out.flush();
                    String exerciseStation = in.readLine();
                    String[] ex_station = exerciseStation.split(":");
                    tv_exs[current_ex].setText(tv_exs[current_ex].getText().toString() + ": " + ex_station[0]);
                    tv_cur_ex.setText(ex_station[0]);
                    if (ex_station.length > 1)
                        tv_ex_station.setText("Station: " + ex_station[1]);
                    else
                        tv_ex_station.setText("Station: ");
                    out.println("OK");
                    out.flush();
                } else
                    Toast.makeText(main_session.this, "Not android device", Toast.LENGTH_SHORT).show();
                server.close();
            } catch (Exception e) {
                Log.d("e", e.toString());
                Toast.makeText(main_session.this, "Can't reach the server", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Next Exercise button */
    public void next_exercise(View view) {
        time_stamps.add(System.currentTimeMillis());
        mStartTime = System.currentTimeMillis();

        if (Started) {
            Started = !Started;
            btn_start_stop.setText("Start");
            btn_start_stop.setBackgroundColor(Color.parseColor("#33b5e5"));

            if (current_ex < NUM_EXERCISES - 1) {
                tv_exs[current_ex].setBackgroundColor(getResources().getColor(R.color.highlighted_text_material_light));
                current_ex++;
                tv_exs[current_ex].setBackgroundColor(Color.parseColor("#ff4444"));

                if (mode) {
                    //randomize next group
                    boolean notNext = true;
                    while (notNext) {
                        int randomGroup = (int) (Math.random() * 3);
                        String group = groups.get(randomGroup);
                        int ex_left = (int) group_ex_left.get(group);
                        if (ex_left > 0) {
                            group_ex_left.put(group, ex_left - 1);
                            notNext = false;
                            try {
                                Socket server = new Socket("wind.ecs.baylor.edu", 4444);
                                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

                                out.println("ANDROID DEVICE");
                                String accepted = in.readLine();
                                if (accepted.compareTo("ANDROID ACCEPTED") == 0) {
                                    Toast.makeText(main_session.this, "Next Exercise is given", Toast.LENGTH_SHORT).show();
                                    out.println("GET EXERCISE FOR");
                                    out.println(group);
                                    out.flush();
                                    String exerciseStation = in.readLine();
                                    String[] ex_station = exerciseStation.split(":");
                                    tv_exs[current_ex].setText(tv_exs[current_ex].getText().toString() + ": " + ex_station[0]);
                                    tv_cur_ex.setText(ex_station[0]);
                                    if (ex_station.length > 1)
                                        tv_ex_station.setText("Station: " + ex_station[1]);
                                    else
                                        tv_ex_station.setText("Station: ");
                                    out.println("OK");
                                    out.flush();
                                } else
                                    Toast.makeText(main_session.this, "Not android device", Toast.LENGTH_SHORT).show();
                                server.close();
                            } catch (Exception e) {
                                Log.d("e", e.toString());
                                Toast.makeText(main_session.this, "Can't reach the server", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(main_session.this, "Workout is finished", Toast.LENGTH_SHORT).show();
                SaveResults(time_stamps.toString());
            }
        }else{
            Started = !Started;
            btn_start_stop.setText("Stop");
            btn_start_stop.setBackgroundColor(Color.parseColor("#ff4444"));
        }
    }

    public void SaveResults(String text){
        if (isExternalStorageWritable()){
            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File (root.getAbsolutePath() + RESULT_DIR);
            dir.mkdirs();
            File file = new File(dir, RESULT_FILENAME);

            try {
                FileOutputStream f = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(f);
                pw.print(text);
                pw.flush();
                pw.close();
                f.close();
                Toast.makeText(main_session.this, "Your result has been saved.", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i("i", "******* File not found. Did you add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            Toast.makeText(main_session.this, "Can't save results - no external drive", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
