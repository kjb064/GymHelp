package com.example.android.gymhelp;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChestFragment extends Fragment {

    private static final int DELETE_ID = Menu.FIRST;
    private static final int EDIT_ID = Menu.FIRST + 1;
    ArrayList<Exercise> ex;
    private static final int CHEST_GROUP_ID = Constants.CHEST;

    public ChestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.exercise_list, container, false);

        final DatabaseHelper db = new DatabaseHelper(getActivity());
        ex = db.getChestExercises();

        // Create an {@link ArrayAdapter}, whose data source is a list of Strings. The
        // adapter knows how to create layouts for each item in the list, using the
        // simple_list_item_1.xml layout resource defined in the Android framework.
        // This list item layout contains a single {@link TextView}, which the adapter will set to
        // display a single word.
        final ExerciseAdapter adapter = new ExerciseAdapter(getActivity(), ex, R.color.title_color);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml file.
        final ListView listView = (ListView) rootView.findViewById(R.id.list);

        registerForContextMenu(listView);

        // Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the
        // {@link ListView} will display list items for each word in the list of words.
        // Do this by calling the setAdapter method on the {@link ListView} object and pass in
        // 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                final Exercise exercise = (Exercise) listView.getAdapter().getItem(position);

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(exercise.getExerciseName());

                LayoutInflater inflater = getLayoutInflater();
                final View dialoglayout = inflater.inflate(R.layout.custom_alertdialog, null);

                builder.setView(dialoglayout);
                final NumberPicker picker = (NumberPicker) dialoglayout.findViewById(R.id.weight_picker);
                picker.setMaxValue(1000);
                picker.setMinValue(0);
                picker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return value + " lbs.";
                    }
                });

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int weight = picker.getValue();
                        db.updateExerciseWeight(exercise.getExerciseID(), weight);
                        getActivity().finish();
                        startActivity(getActivity().getIntent());
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        return rootView;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(CHEST_GROUP_ID, DELETE_ID, 0, R.string.menu_delete);
        menu.add(CHEST_GROUP_ID, EDIT_ID, 0, R.string.menu_edit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getGroupId() == CHEST_GROUP_ID){

            DatabaseHelper db = new DatabaseHelper(getActivity());

            switch (item.getItemId()){
                case DELETE_ID:
                    AdapterView.AdapterContextMenuInfo info =
                            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                    int id = ex.get((int) info.id).getExerciseID();
                    db.deleteExercise(id);

                    Toast.makeText(getActivity(),
                            "DELETED " + ex.get((int) info.id).getExerciseID(),
                            Toast.LENGTH_SHORT).show();
                    ex.remove((int) info.id);

                    // "Refresh" the Fragment once the exercise has been deleted
                    Fragment currentFragment = getFragmentManager().findFragmentByTag(getTag());
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.detach(currentFragment);
                    fragmentTransaction.attach(currentFragment);
                    fragmentTransaction.commit();
                    return true;
                case EDIT_ID:
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }

        }
        return false;
    } // end onContextItemSelected

}
