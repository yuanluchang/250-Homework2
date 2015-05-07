package edu.uw.aad.ylchang1.solution.homework2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import edu.uw.aad.ylchang1.solution.homework2.data.TaskDbHelper;
import edu.uw.aad.ylchang1.solution.homework2.model.Task;

/**
 * Created by Margaret on 3/7/2015
 * Android 210 - Winter 2015 - Homework 4 Solution
 *
 * 1. Create a main screen to show list of tasks
 * 2. There are two menu items on ActionBar: Add & Delete
 * 3. Click on Add to go to a screen for adding a new task
 *  - Create AddTaskActivity.java for taking user input
 *  - Clicking on OK button in AddTaskActivity inserts a task to DB
 *  - Back to the main screen to show list of tasks
 * 4. The ListView is set to select multiple choices or a single choice (code commented out)
 * 5. Click on Delete to delete the selected task(s)
 */
public class MainActivity extends ActionBarActivity {

    public final static String TASK_INDEX = "index";
    public final static String TASK_NAME = "name";
    public final static String TASK_DETAIL = "description";

    private TaskDbHelper mTaskDbHelper;
    private ListView mListViewTasks;
    private ArrayAdapter<Task> mTaskArrayAdapter;
    private List<Task> tasks;


    public List<String> getTaskList() {
        List<String> taskNameList = new ArrayList<String>();
        for(int i=0; i < tasks.size(); i++) {
            taskNameList.add(tasks.get(i).getName());
        }
        return taskNameList;
    }

    public Task getTaskDetail(int index) {
        return tasks.get(index);
    }
    private boolean mDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get task list data from DB
        mTaskDbHelper = new TaskDbHelper(this);
        tasks = mTaskDbHelper.getTasks();


        // Set up UI ListView
        mListViewTasks = (ListView)findViewById(android.R.id.list);
        mTaskArrayAdapter = (ArrayAdapter<Task>)mListViewTasks.getAdapter();

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshUI(true);
    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_add:
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivity(intent);
                break;
            case R.id.action_delete:
/*                // Delete single task
                int position = mListViewTasks.getCheckedItemPosition();
                Task task = mTaskArrayAdapter.getItem(position);
                deleteSingleTask(position, task.getId());
*/
                // Delete multiple tasks
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
     * A very ugly way to refresh the UI
     */
    private void refreshUI(boolean activityOnPause) {

        tasks = mTaskDbHelper.getTasks();           // Get all the tasks from db
        mTaskArrayAdapter = (ArrayAdapter<Task>)mListViewTasks.getAdapter();
        if (mTaskArrayAdapter!=null) {
            mTaskArrayAdapter.clear();                  // Clear the ArrayAdapter
            mTaskArrayAdapter.addAll(tasks);            // Add all tasks
            if (mListViewTasks !=null) {
                int pos = tasks.size();
                if (pos > 0) {
                    mListViewTasks.setSelection(pos-1);
                    showDetails(pos-1, activityOnPause);
                }
                else {
                    mListViewTasks.clearChoices();              // Clear all selections
                    clearDetails(activityOnPause);
                }
            }
            mTaskArrayAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */

    public void onTaskSelected(int index) {
        if (mDualPane) {
            showDetails(index, false);
        } else {
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

    private void showDetails(int index, boolean activityOnPause) {

        if (index >= tasks.size()) return;

        // We can display everything in-place with fragments, so update
        // the list to highlight the selected item and show the data.

        mListViewTasks.setItemChecked(index, true);

        // Check what fragment is currently shown, replace if needed.

        TaskDetailFragment details = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.details);

//        if (details == null || details.getShownIndex() != index) {

                // Make new fragment to show this selection.

                details = TaskDetailFragment.newInstance(index, getTaskDetail(index));

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                if (activityOnPause)
                    ft.commitAllowingStateLoss();
                else
                    ft.commit();
//        }
    }

    private void clearDetails(boolean activityOnPause) {

        // Check what fragment is currently shown, replace if needed.


    }

    public List<Task> getTasks() {
        return tasks;
    }

    public static class TaskListFragment extends ListFragment {

        boolean mDualPane;
        int mCurCheckPosition = 0;
        MainActivity m_activity;
        /*

                OnTaskSelectedListener mListener;

                public interface OnTaskSelectedListener {
                    public void onTaskSelected(int index);
                }

                @Override
                public void onAttach(Activity activity) {
                    super.onAttach(activity);
                    try {
                        mListener = (OnTaskSelectedListener) activity;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(activity.toString() + " must implement OnTaskSelectedListener");
                    }

                }
        */
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            // Populate list with our static array of titles.
            MainActivity m_activity = (MainActivity)getActivity();
//            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, m_activity.getTaskList()));
            setListAdapter(new ArrayAdapter<Task>(getActivity(), android.R.layout.simple_list_item_multiple_choice, m_activity.getTasks()));

            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            // Check to see if we have a frame in which to embed the details
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.details);
            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
            }

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                m_activity = (MainActivity)getActivity();
                m_activity.onTaskSelected(mCurCheckPosition);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            mCurCheckPosition = position;
            m_activity = (MainActivity)getActivity();
            m_activity.onTaskSelected(position);
/*
            // Append the clicked item's row ID with the content provider Uri
            Uri noteUri = ContentUris.withAppendedId(ArticleColumns.CONTENT_URI, id);
            // Send the event and Uri to the host activity
            mListener.onTaskSelected(noteUri);
*/
        }


    }

}
