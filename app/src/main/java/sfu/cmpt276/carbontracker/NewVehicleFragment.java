package sfu.cmpt276.carbontracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NewVehicleFragment extends AppCompatDialogFragment {

    private final String TAG = "NewVehicleDialog";
    private String make;
    private String model;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create the view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_vehicle, null);

        // Add button listener
        DialogInterface.OnClickListener addListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Add button clicked");
            }
        };

        // Cancel button listener
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Cancel button clicked");
            }
        };

        final Spinner makeSpinner = (Spinner)view.findViewById(R.id.make);
        final Spinner modelSpinner = (Spinner)view.findViewById(R.id.model);
        final Spinner yearSpinner = (Spinner)view.findViewById(R.id.year);

        populateSpinner(makeSpinner, getMakeList());

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                make = parent.getItemAtPosition(position).toString();
                populateSpinner(modelSpinner, getModelList(make));
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        //todo uncomment when getYearKeys has been changed
        /*modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                 model = parent.getItemAtPosition(position).toString();
                populateSpinner(yearSpinner, getYearList(make, model));
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });*/

        // Build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Add New Vehicle")
                .setView(view)
                .setPositiveButton("ADD", addListener)
                .setNegativeButton("CANCEL", cancelListener)
                .create();

    }

    private List<String> getMakeList()
    {
        CarDirectory directory = getCarDirectory();
        List<String> makeList = new ArrayList<>(directory.getMakeKeys());
        return makeList;
    }
    private List<String> getModelList(String make)
    {
        CarDirectory directory = getCarDirectory();
        List<String> modelList = new ArrayList<>(directory.getModelKeys(make));
        return modelList;
    }
    //todo uncomment when getYearKeys has been changed
    /*private List<String> getYearList(String model, String make)
    {
        CarDirectory directory = getCarDirectory();
        List<String> yearList = new ArrayList<>(directory.getYearKeys(make, model));
        return yearList;
    }*/

    private CarDirectory getCarDirectory() {
        User user = User.getInstance();
        InputStream inputStream = getResources().openRawResource(
                getResources().getIdentifier("vehicles",
                        "raw", getActivity().getPackageName()));
        user.setUpDirectory(inputStream);
        return user.getMain();
    }

    private void populateSpinner(Spinner makeSpinner, List<String> list) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        makeSpinner.setAdapter(adapter);
    }


}