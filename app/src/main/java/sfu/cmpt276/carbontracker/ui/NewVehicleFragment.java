package sfu.cmpt276.carbontracker.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sfu.cmpt276.carbontracker.R;
import sfu.cmpt276.carbontracker.carbonmodel.User;
import sfu.cmpt276.carbontracker.carbonmodel.Car;
import sfu.cmpt276.carbontracker.carbonmodel.CarDirectory;
import sfu.cmpt276.carbontracker.carbonmodel.CarListener;

/* Fragment for adding a new car to car list when creating a journey
* */
public class NewVehicleFragment extends AppCompatDialogFragment {

    private final int DEFAULT_EDIT_CAR_POSITION = -1;
    private final String TAG = "NewVehicleDialog";
    private Car car;
    private List<Car> detailedCarList;
    private CarListener detailedCarListener;

    private boolean editing = false;
    private int editCarPosition = DEFAULT_EDIT_CAR_POSITION;

    private DetailedCarAdapter detailedCarArrayAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        car = new Car();

        if(getArguments() != null)
            editCarPosition = getArguments().getInt("car", DEFAULT_EDIT_CAR_POSITION); // defaults to -1

        if(editCarPosition != DEFAULT_EDIT_CAR_POSITION)
        {
            car = User.getInstance().getCarList().get(editCarPosition);
            Log.i(TAG, "Editing car " + car.getShortDecription());
            editing = true;
        }

        // Create the view
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_vehicle, null);

        detailedCarList = new ArrayList<>();

        detailedCarArrayAdapter = new DetailedCarAdapter(getActivity());
        detailedCarListener = (CarListener) detailedCarArrayAdapter;
        ListView detailedCarListView = (ListView) view.findViewById(R.id.detailedCarList);
        detailedCarListView.setAdapter(detailedCarArrayAdapter);

        detailedCarListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // User has selected a vehicle
                detailedCarList.get(i);
                Log.i(TAG, "User selected vehicle \"" + car.getNickname()
                        + "\" " + car.getMake() + " " + car.getModel());
                detailedCarArrayAdapter.setSelectedIndex(i);
                detailedCarArrayAdapter.notifyDataSetChanged();
            }
        });


        // Add/Save button listener
        DialogInterface.OnClickListener addListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                car = detailedCarArrayAdapter.getSelectedCar();
                EditText nickname = (EditText) view.findViewById(R.id.name);
                car.setNickname(String.valueOf(nickname.getText()).trim());

                if(editing) {
                    Log.i(TAG, "Save button clicked");
                    User.getInstance().editCarFromCarList(editCarPosition, car);
                } else {
                    Log.i(TAG, "Add button clicked");
                    User.getInstance().addCarToCarList(car);
                }
            }
        };

        // Delete button listener
        DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                User.getInstance().removeCarFromCarList(editCarPosition);
            }
        };

        // Use button listener
        DialogInterface.OnClickListener useListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Use button clicked");
                car = detailedCarArrayAdapter.getSelectedCar();
                EditText nickname = (EditText) view.findViewById(R.id.name);
                car.setNickname(String.valueOf(nickname.getText()).trim());

                // Set current Journey to use the selected car
                User.getInstance().setCurrentJourneyCar(car);

                Intent intent = new Intent(getActivity(), RouteActivity.class);
                startActivityForResult(intent, 0);
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

        populateSpinner(makeSpinner, getMakeList(), car.getMake());

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                car.setMake(parent.getItemAtPosition(position).toString());
                populateSpinner(modelSpinner, getModelList(car.getMake()), String.valueOf(car.getModel()));
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                car.setModel(parent.getItemAtPosition(position).toString());
                populateSpinner(yearSpinner, getYearList(car.getMake(), car.getModel()), String.valueOf(car.getYear()));
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                car.setYear(Integer.parseInt(parent.getItemAtPosition(position).toString()));
                //populateSpinner(transmissionDisplacement, getCarList(car.getMake(), car.getModel(), car.getYear()));
                List<Car> carList = getCarList(car.getMake(), car.getModel(), String.valueOf(car.getYear()));
                detailedCarList.clear();
                detailedCarList.addAll(carList);
                detailedCarListener.carListWasEdited();
                //transmissionDisplacement.setEnabled(true);
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if(editing){
            // Build the dialog
            final String title;
            if(car.getNickname().equals(new Car().getNickname()))
                title = "Edit Vehicle";
            else {
                title = "Edit \"" + car.getNickname() + "\"";
                TextView name = (TextView) view.findViewById(R.id.name);
                name.setText(car.getNickname());
            }

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setView(view)
                    .setNeutralButton("DELETE", deleteListener)
                    .setPositiveButton("SAVE", addListener)
                    .setNegativeButton("CANCEL", cancelListener)
                    .create();
        } else {
            // Build the dialog
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Add New Vehicle")
                    .setView(view)
                    .setPositiveButton("ADD", addListener)
                    .setNeutralButton("USE", useListener)
                    .setNegativeButton("CANCEL", cancelListener)
                    .create();
        }
    }

    private class DetailedCarAdapter extends ArrayAdapter<Car> implements CarListener{

        private int selectedIndex = 0;

        DetailedCarAdapter(Context context) {
            super(context, R.layout.car_listview_item_dialog, detailedCarList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            // Ensure we have a view (could have been passed a null)
            View itemView = convertView;
            if(itemView == null) {
                itemView = LayoutInflater.from(NewVehicleFragment.this.getContext()).inflate(R.layout.car_listview_item_dialog, parent, false);
            }

            // Get the current car
            Car car = detailedCarList.get(position);

            // Fill the TextView
            final RadioButton selected = (RadioButton) itemView.findViewById(R.id.selectedRadioButton);
            selected.setText(car.getTransmissionFuelTypeDispacementDescription());

            // Set the radiobutton

            if(position == selectedIndex) {
                selected.setChecked(true);
            }
            else
                selected.setChecked(false);

            return itemView;
        }

        public void setSelectedIndex(int index){
            selectedIndex = index;
        }

        public Car getSelectedCar(){
            return detailedCarList.get(selectedIndex);
        }

        @Override
        public void carListWasEdited() {
            Log.i(TAG, "Car List changed, updating listview");
            selectedIndex = 0;
            notifyDataSetChanged();
        }
    }

    @NonNull
    private List<Car> getCarList(String make, String model, String year) {
        String data = make+","+model+","+year;
        List<Car> carList = User.getInstance().getMain().carList(data); //returns list of cars fitting the chosen make, model, year
        return carList;
    }

    private List<String> getMakeList()
    {
        User user = User.getInstance();
        CarDirectory directory = user.getMain();
        List<String> makeList = new ArrayList<>(directory.getMakeKeys());
        Collections.sort(makeList);
        return makeList;
    }

    private List<String> getModelList(String make)
    {
        User user = User.getInstance();
        CarDirectory directory = user.getMain();
        List<String> modelList = new ArrayList<>(directory.getModelKeys(make));
        Collections.sort(modelList);
        return modelList;
    }

    private List<String> getYearList(String make, String model)
    {
        User user = User.getInstance();
        CarDirectory directory = user.getMain();
        List<String> yearList = new ArrayList<>(directory.getYearKeys(make, model));
        Collections.sort(yearList, Collections.reverseOrder());
        return yearList;
    }

    private void populateSpinner(Spinner spinner, List<String> list, String compareValue) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
        if(!compareValue.equals(null))
        {
            int position = adapter.getPosition(compareValue);
            spinner.setSelection(position);
        }
    }
}