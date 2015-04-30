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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class random_workout_select_groups extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "edu.baylor.ecs.hci.gymscheduler.MESSAGE";
    private int NUM_CHECKED = 0;
    private CheckBox[] checkboxes = new CheckBox[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_workout_select_groups);

        checkboxes[0] = (CheckBox) findViewById(R.id.cb_random_chest);
        checkboxes[1] = (CheckBox) findViewById(R.id.cb_random_arms);
        checkboxes[2]  = (CheckBox) findViewById(R.id.cb_random_core);
        checkboxes[3]  = (CheckBox) findViewById(R.id.cb_random_back);

        for (int i = 0; i < checkboxes.length; i++){
            checkboxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (NUM_CHECKED < 3) {
                            NUM_CHECKED++;
                        }else {
                            buttonView.setChecked(false);

                            Context context = getApplicationContext();
                            CharSequence text = "You can choose only three (3) groups of muscles";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }else{
                        NUM_CHECKED--;
                    }
                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random_workout_select_groups, menu);
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

    /** Called when the user clicks the Begin Session button */
    public void begin_main_session(View view){
        if (NUM_CHECKED == 3) {
            Intent intent = new Intent(this, main_session.class);

            //TODO get list of exercises from server
            ArrayList<String> chosen_exercises = new ArrayList<>();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isChecked()) {
                    chosen_exercises.add(checkboxes[i].getText().toString());
                }
            }

            intent.putStringArrayListExtra(EXTRA_MESSAGE, chosen_exercises);
            startActivity(intent);
        }else{
            Context context = getApplicationContext();
            CharSequence text = "You need choose three (3) groups of muscles to begin session";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}
