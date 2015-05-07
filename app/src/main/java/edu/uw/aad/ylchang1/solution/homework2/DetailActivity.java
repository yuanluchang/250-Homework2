package edu.uw.aad.ylchang1.solution.homework2;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.content.res.Configuration;
import android.content.Intent;
import edu.uw.aad.ylchang1.solution.homework2.model.Task;
/**
 * Created by yuanluchang on 15/5/4.
 */
public class DetailActivity extends ActionBarActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            int index = Integer.parseInt(intent.getStringExtra(MainActivity.TASK_INDEX));
            String task_name = intent.getStringExtra(MainActivity.TASK_NAME);
            String task_detail = intent.getStringExtra(MainActivity.TASK_DETAIL);

            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }
            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                // Check what fragment is currently shown, replace if needed.
                TaskDetailFragment details = TaskDetailFragment.newInstance(index, task_name, task_detail);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.commit();

            }
        }
}
