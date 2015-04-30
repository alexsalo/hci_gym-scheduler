package edu.baylor.ecs.hci.gymscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class define_workout extends ActionBarActivity {
    public static Map<String, ArrayList<String>> all_exercises_set = new HashMap<String, ArrayList<String>>();
    public String set_of_exercises = "chest:bench press,push ups;legs:squads,jumps,steps;" +
            "arms:pull ups,bicep curls,tricep,dips;core:abs,ball rotation,bent";
    public ArrayList<String> choices = new ArrayList<String>();

    public final static String EXTRA_MESSAGE = "edu.baylor.ecs.hci.gymscheduler.MESSAGE";
    Spinner[] spinners_exs = new Spinner[9];
    static int spinner_names[]={R.id.cb_ex1,R.id.cb_ex2,R.id.cb_ex3,R.id.cb_ex4
            ,R.id.cb_ex5,R.id.cb_ex6,R.id.cb_ex7,R.id.cb_ex8,R.id.cb_ex9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_define_workout);

        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Socket server = new Socket("wind.ecs.baylor.edu", 4444);
            PrintWriter out = new PrintWriter(server.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));

            out.println("ANDROID DEVICE");
            String accepted = in.readLine();
            if(accepted.compareTo("ANDROID ACCEPTED") == 0) {
                Toast.makeText(define_workout.this, "Connection to the server established", Toast.LENGTH_SHORT).show();
                out.println("GET ALL EXERCISES");
                out.flush();
                set_of_exercises = in.readLine();
                out.println("OK");
                out.flush();
//parse the string
            }
            else
                Toast.makeText(define_workout.this, "Not android device", Toast.LENGTH_SHORT).show();
            server.close();
        }
        catch (Exception e){
            Log.d("e", e.toString());
            Toast.makeText(define_workout.this, "Can't reach the server", Toast.LENGTH_SHORT).show();
        }
        */

        // Init all the exercises
        String[] groups = set_of_exercises.split(";");
        for (int i = 0; i < groups.length; i++){
            int column = groups[i].indexOf(":");
            String [] exs_strings = groups[i].substring(column+1).split(",");
            ArrayList<String> exs = new ArrayList<String>(Arrays.asList(exs_strings));
            all_exercises_set.put(groups[i].substring(0, column), exs);
        }


        // Create an ArrayAdapter using the string array and a default spinner layout
        choices.add("Exercise");
        for (String key : all_exercises_set.keySet()){
            for (String s : all_exercises_set.get(key)){
                choices.add(s);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, choices);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        for (int k = 0; k < 9; k++){
            spinners_exs[k] = (Spinner) findViewById(spinner_names[k]);
            spinners_exs[k].setAdapter(adapter);

            /*spinners_exs[k].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String ex = (String) parent.getItemAtPosition(position);
                    choices.remove(ex);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_define_workout, menu);
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

    /** Called when the user clicks the Start Workout button */
    public void begin_main_session(View view) {
        if (true) {
            Intent intent = new Intent(this, main_session.class);

            ArrayList<String> chosen_exercises = new ArrayList<>();
            for (int i = 0; i < spinners_exs.length; i++) {
                chosen_exercises.add(spinners_exs[i].getSelectedItem().toString());
            }

            //intent.putStringArrayListExtra(EXTRA_MESSAGE, chosen_exercises);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Something wrong";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

}
