package edu.uw.aad.ylchang1.solution.homework2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.uw.aad.ylchang1.solution.homework2.data.TaskDbHelper;

/**
 * Created by Margaret on 2/23/2015.
 * Updated by Yuan-Lu Chang on 5/13/2015
 * Android 250 - Spring 2015 - Homework 2 Solution
 * 1. User can create a new task.
 * 2. User can enter task name and task description on screen.
 * 3. After user clicks on the OK button, it should validate user data input (i.e. each field needs to be filled out).
 * 4. Persist data in a sqlite database.
*/

public class AddTaskActivity extends ActionBarActivity {

    private EditText mEditTextTaskName;
    private EditText mEditTextTaskDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mEditTextTaskName = (EditText)findViewById(R.id.editTextTaskName);
        mEditTextTaskDesc = (EditText)findViewById(R.id.editTextTaskDesc);
        Button buttonOk = (Button)findViewById(R.id.buttonOk);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTaskToDb();
            }
        });

    }

    private void insertTaskToDb() {

        if (mEditTextTaskName.getText().toString().isEmpty()) {
            mEditTextTaskName.setError("You must enter data here.");
            return;
        }
        if (mEditTextTaskDesc.getText().toString().isEmpty()) {
            mEditTextTaskDesc.setError("You must enter data here.");
            return;
        }
        TaskDbHelper taskDbHelper = new TaskDbHelper(this);
        taskDbHelper.addTask(getInput(mEditTextTaskName), getInput(mEditTextTaskDesc));
        finish(); // go back to main screen
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_task, menu);
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

    /**
     * Get the user input in EditText
     * @param editText
     * @return
     */
    private String getInput(EditText editText) {
        return editText.getText().toString().trim();
    }

}
