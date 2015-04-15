package edu.baylor.ecs.hci.gymscheduler;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;


public class random_workout_select_groups extends ActionBarActivity {
    public final static String EXTRA_MESSAGE = "edu.baylor.ecs.hci.gymscheduler.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_workout_select_groups);
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
        Intent intent = new Intent(this, main_session.class);
        CheckBox[] checkboxes = new CheckBox[5];
        checkboxes[0] = (CheckBox) findViewById(R.id.cb_random_chest);
        checkboxes[1] = (CheckBox) findViewById(R.id.cb_random_arms);
        checkboxes[2]  = (CheckBox) findViewById(R.id.cb_random_legs);
        checkboxes[3]  = (CheckBox) findViewById(R.id.cb_random_core);
        checkboxes[4]  = (CheckBox) findViewById(R.id.cb_random_back);

        String msg = "";
        for (int i =0; i < checkboxes.length; i++){
            if (checkboxes[i].isChecked()) {
                msg += checkboxes[i].getText().toString();
                msg += "; ";
            }
        }

        intent.putExtra(EXTRA_MESSAGE, msg);
        startActivity(intent);
    }
}