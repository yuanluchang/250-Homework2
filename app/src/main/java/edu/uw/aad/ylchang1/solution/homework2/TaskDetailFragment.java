package edu.uw.aad.ylchang1.solution.homework2;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.util.TypedValue;
import edu.uw.aad.ylchang1.solution.homework2.model.Task;
/**
 * Created by yuanluchang on 15/5/4.
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

    public int getShownIndex() {
        return getArguments().getInt("index");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        ScrollView scroller = new ScrollView(getActivity());
        TextView text = new TextView(getActivity());
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, getActivity().getResources().getDisplayMetrics());
        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText(getArguments().getString("description"));
        return scroller;
    }

}
