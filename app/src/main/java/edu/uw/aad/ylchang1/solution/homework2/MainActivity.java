package edu.uw.aad.ylchang1.solution.homework2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import edu.uw.aad.ylchang1.solution.homework2.data.TaskDbHelper;
import edu.uw.aad.ylchang1.solution.homework2.model.Task;

/**
 * Created by Margaret on 2/23/2015.
 * Updated by Yuan-Lu Chang on 5/13/2015
 * Android 250 - Spring 2015 - Homework 2 Solution
 *
 * 1. Create a task list application USING FRAGMENTS.
 * 2. There are two menu items on ActionBar: Add & Delete.
 * 3. Click on Add to go to a screen for adding a new task:
 *  - Create AddTaskActivity.java for taking user input.
 *  - Clicking on OK button in AddTaskActivity inserts a task to DB.
 *  - Back to the main screen to show list of tasks.
 * 4. The ListView is set to select multiple choices or a single choice based on if it's single pane or dual pane.
 * 5. Click on Delete to delete the selected task(s).
 */

public class MainActivity extends ActionBarActivity {

    public final static String TASK_INDEX = "index";
    public final static String TASK_NAME = "name";
    public final static String TASK_DETAIL = "description";

    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     * mTaskDbHelper: the helper class to persists data with a Sqlite database, and access the data via CursorLoader and Content provider.
     * mListViewTasks: the list view class underlying to display a list of tasks
     * mTaskArrayAdapter: the list adapter class underlying to detect any change to a list of tasks
     * mTasks: the data model underlying to store a list of tasks
     * mDualPane: if the user launches the app in single pane or dual pane
     */

    private TaskDbHelper mTaskDbHelper;
    private ListView mListViewTasks;
    private ArrayAdapter<Task> mTaskArrayAdapter;
    private List<Task> mTasks;
    private boolean mDualPane;

    /**
     * Return the selected task detail.
     * @param index
     * @return
     */

    public Task getTaskDetail(int index) {
        return mTasks.get(index);
    }

    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     *  Users launches the application, a blank page is presented with a TextView "Task List".
     *  Check if the application should launch in single or dual pane.
     *  If device is with a screen width greater than 600dp, the application should launch in dual pane (i.e. show a split pane).
     *  Otherwise if device is in portrait mode, the application should launch in single pane (i.e. show a list).
     *  When in dual pane, the application will show a list of tasks on the left pane and the task details on the right pane.
     *  When in single pane, the application will show only a list of tasks; a user can click on an item to see task details in a separate view.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get task list data from DB
        mTaskDbHelper = new TaskDbHelper(this);
        mTasks = mTaskDbHelper.getTasks();


        // Set up UI ListView
        mListViewTasks = (ListView)findViewById(android.R.id.list);
        mTaskArrayAdapter = (ArrayAdapter<Task>)mListViewTasks.getAdapter();

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;


    }

    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     *  Activity is on restart.
     *  On restart / resume activity need to refresh task list / detail UI, such as adding a new list / deleting an existing task
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        refreshUI(true);
    }


    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     *  Activity is on resume.
     *  On restart / resume activity need to refresh task list / detail UI, such as adding a new list / deleting an existing task
     */
    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     *  Users clicks on Add icon on the ActionBar to add a new task.
     *  Users clicks on Delete icon on the ActionBar to delete one or multiple tasks.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_add:
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
                break;
            case R.id.action_delete:
                ArrayList<Integer> ids = new ArrayList<Integer>();
                SparseBooleanArray checkedItems = mListViewTasks.getCheckedItemPositions();
                mTaskArrayAdapter = (ArrayAdapter<Task>)mListViewTasks.getAdapter();
                if(checkedItems.size()>0) {
                    for (int i = 0; i < checkedItems.size(); i++) {
                        int position = checkedItems.keyAt(i);
                        Task task = mTaskArrayAdapter.getItem(position);
                        ids.add((int)task.getId());
                    }
                    mTaskDbHelper.deleteTasks(ids);
                }
                refreshUI(false);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete a single task from db
     * @param pos
     * @param id
     */
    private void deleteSingleTask(int pos, long id) {

        if (pos>=0) {
            int rowsDeleted = mTaskDbHelper.deleteTask(id);
            if(rowsDeleted >0) {
                refreshUI(false);
            }
        }
        Toast.makeText(this, "Item Deleted @position #" + pos + " rowId is #" + id, Toast.LENGTH_LONG).show();
    }

    /**
     *  Android 250 - Spring 2015 - Homework 2 Solution
     *  When restarting the application, the current list of added tasks is retained. Data persistence is done via a sqlite database.
     *  The application should refresh task list and details UI on restarting from following scenarios:
     *  1. Added a new task
     *  2. Deleted an existing task
     */
    private void refreshUI(boolean activityOnPause) {

        mTasks = mTaskDbHelper.getTasks();           // Get all the tasks from db
        mTaskArrayAdapter = (ArrayAdapter<Task>)mListViewTasks.getAdapter();
        if (mTaskArrayAdapter!=null) {
            mTaskArrayAdapter.clear();                  // Clear the ArrayAdapter
            mTaskArrayAdapter.addAll(mTasks);            // Add all tasks
            if (mListViewTasks !=null) {
                int pos = mTasks.size();
                if (pos > 0) {
                    mListViewTasks.setSelection(pos-1);
                    if (mDualPane)
                        showDetails(pos-1, activityOnPause);
                }
                else {
                    mListViewTasks.clearChoices();              // Clear all selections
                    if (mDualPane)
                        clearDetails(activityOnPause);
                }
            }
            mTaskArrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Android 250 - Spring 2015 - Homework 2 Solution
     * Used by TaskDetailFragment.
     * Helper function to show the details of a selected item, either by displaying a fragment in-place in the current UI (i.e. dual pane);
     *  or starting a whole new activity in which it is displayed (i.e. single pane).
     * When in dual pane, the details screen will be shown in the right frame of the dual pane layout.
     * When in single pane, start a new activity / screen to show the details screen.
     */

    public void onTaskSelected(int index, int current_selected) {
        if (mDualPane) {
//            if (index != current_selected)
                showDetails(index, false);
        } else {
            if (index == current_selected) {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(this, DetailActivity.class);
                intent.putExtra(TASK_INDEX, index);
                intent.putExtra(TASK_NAME, getTaskDetail(index).getName());
                intent.putExtra(TASK_DETAIL, getTaskDetail(index).getDesc());
                startActivity(intent);
            }
        }
    }

    /**
     * Android 250 - Spring 2015 - Homework 2 Solution
     * Helper function to update task detail fragment to show the details of a selected item
     * Fragment back stack should be modified to replace previous task detail fragment with the selected task (specified by index).
     */

    private void showDetails(int index, boolean activityOnPause) {

        if (index >= mTasks.size()) return;

        // We can display everything in-place with fragments, so update
        // the list to highlight the selected item and show the data.

        mListViewTasks.setItemChecked(index, true);

        // Check what fragment is currently shown, replace if needed.

        TaskDetailFragment details = TaskDetailFragment.newInstance(index, getTaskDetail(index));

        // Execute a transaction, replacing any existing fragment
        // with this one inside the frame.

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, details);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (activityOnPause)
            ft.commitAllowingStateLoss();
        else
            ft.commit();
    }

    private void clearDetails(boolean activityOnPause) {

        // Check what fragment is currently shown, remove as there is no more task.
        TaskDetailFragment details = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.details);

        if (details != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(details);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (activityOnPause)
                ft.commitAllowingStateLoss();
            else
                ft.commit();
        }


    }

    public List<Task> getTasks() {
        return mTasks;
    }

    /**
     * Android 250 - Spring 2015 - Homework 2 Solution
     * TaskDetailFragment is underlying list fragment to display task list view for users to select / deselect a task.
     * TaskDetailFragment calls helper function in main activity to show the details of a selected item, either by displaying a fragment in-place in the current UI (i.e. dual pane);
     *  or starting a whole new activity in which it is displayed (i.e. single pane).
     * When in dual pane, users can single select a task to view details in the right frame of the dual pane layout.
     * When in single pane, users can multi-select tasks (for deletion).
     */

    public static class TaskListFragment extends ListFragment {

        private static final String TAG = "MyActivity";
        private int mCurCheckPosition;
        private MainActivity m_activity;
        private boolean mDualPane;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            m_activity = (MainActivity)getActivity();
            setListAdapter(new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_multiple_choice, m_activity.getTasks()));

            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice");
            }

            mDualPane = (detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE);

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                m_activity = (MainActivity)getActivity();
                m_activity.onTaskSelected(mCurCheckPosition, mCurCheckPosition);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            /*
             created a customized view for displaying task list
            */
            return inflater.inflate(R.layout.fragment_list_task, container);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
//            Log.v(TAG, "before-->position=" + position + ", mCurCheckPosition = " + mCurCheckPosition);
//            mCurCheckPosition = position;
            Log.v(TAG, "position=" + position + ", mCurCheckPosition = " + mCurCheckPosition);
            if (!mDualPane) {
                // for fixing a bug not able to select the first task in single pane mode
                if ((position == 0) && (mCurCheckPosition == 0)) {
                    mCurCheckPosition = -1;
                } else if (mCurCheckPosition == -1) {
                    // for fixing a bug not able to show detail for the first task in dual pane mode
                    mCurCheckPosition = position;
                    if (position == 0) m_activity.onTaskSelected(position, mCurCheckPosition);
                } else {
                    m_activity.onTaskSelected(position, mCurCheckPosition);
                    mCurCheckPosition = position;
                }
            }
            else {
                m_activity.onTaskSelected(position, mCurCheckPosition);
                mCurCheckPosition = position;
            }
        }


    }

}
