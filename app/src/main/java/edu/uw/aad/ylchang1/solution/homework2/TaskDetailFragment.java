package edu.uw.aad.ylchang1.solution.homework2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.uw.aad.ylchang1.solution.homework2.model.Task;

/**
 * Android 250 - Spring 2015 - Homework 2 Solution
 * Created by yuanluchang on 15/5/4.
 * Task Details Fragment displays the details of the task.
 * Make this a fragment so it can be reusable to be shown in both single and dual pane screens for different device sizes.
 */

public class TaskDetailFragment extends Fragment{

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static TaskDetailFragment newInstance(int index, String name, String details) {
        TaskDetailFragment f = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("description", details);
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }
    public static TaskDetailFragment newInstance(int index, Task details) {
        TaskDetailFragment f = new TaskDetailFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("name", details.getName());
        args.putString("description", details.getDesc());
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
         created a customized view for displaying task detail
        */
        View taskDetailView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (getArguments()!=null) {
            TextView taskNameView = (TextView) taskDetailView.findViewById(R.id.textViewTaskName);
            TextView taskDescView = (TextView) taskDetailView.findViewById(R.id.textViewTaskInstr);
            taskNameView.setText(getArguments().getString("name"));
            taskDescView.setText(getArguments().getString("description"));
        }
        return taskDetailView;

    }

}
