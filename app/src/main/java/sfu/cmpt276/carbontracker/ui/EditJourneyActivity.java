package sfu.cmpt276.carbontracker.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import sfu.cmpt276.carbontracker.R;
import sfu.cmpt276.carbontracker.carbonmodel.Car;
import sfu.cmpt276.carbontracker.carbonmodel.Journey;
import sfu.cmpt276.carbontracker.carbonmodel.Route;
import sfu.cmpt276.carbontracker.carbonmodel.RouteList;
import sfu.cmpt276.carbontracker.carbonmodel.User;

public class EditJourneyActivity extends AppCompatActivity {

    String nameSaved;
    double citySaved;
    double highwaySaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        User user = User.getInstance();
        setupSelectModeTxt();
        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);
        Journey currentJourney = user.getJourney(index);
        user.setCurrentJourney(currentJourney);
        setUpRouteSpinner(index);
        setUpCarSpinner(index);
        setUpAddRouteButton(index);
        setUpAddCar();
        setUpChangeModeToCar();
    }

    private void setUpCarSpinner(int index) {
        //Create a String list
        List<Car> carList = User.getInstance().getCarList();
        List<String> list = getCarNames(carList);
        Spinner routeSpin = (Spinner) findViewById(R.id.routeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        routeSpin.setAdapter(adapter);
        if (index >= 0) {
            Route originalRoute = User.getInstance().getJourney(index).getRoute();
            int routeIndex = carList.indexOf(originalRoute);
            routeSpin.setSelection(routeIndex);
        }
    }

    private void setUpChangeModeToCar() {
        Button changeVehicle = (Button) findViewById(R.id.journey_edit_carButton);
        changeVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = User.getInstance();
                setCurrentCar(user);
                setCurrentRoute(user);
                finish();
            }
        });
    }

    private void setCurrentCar(User user) {
        Spinner carSpin = (Spinner) findViewById(R.id.edit_journey_car_spinner);
        List<Car> carList = user.getCarList();
        Car selectedCar = carList.get(carSpin.getSelectedItemPosition());
        user.setCurrentJourneyCar(selectedCar);
    }

    private void setCurrentRoute(User user) {
        Spinner routeSpin = (Spinner) findViewById(R.id.routeList);
        RouteList routeList = user.getRouteList();
        Route selectedRoute = routeList.getRoute(routeSpin.getSelectedItemPosition());
        user.setCurrentJourneyRoute(selectedRoute);
    }

    private void setUpAddCar() {
        Button editVehicle = (Button) findViewById(R.id.edit_journey_addNewCar);
        editVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchNewVehicleDialog();
            }
        });
    }

    private void setUpAddRouteButton(final int index) {
        Button addRoute = (Button) findViewById(R.id.journey_edit_addRoute);
        addRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog viewDialog = new Dialog(EditJourneyActivity.this);
                viewDialog.setContentView(R.layout.new_route_layout);
                viewDialog.show();

                final EditText routeName = (EditText) viewDialog.findViewById(R.id.routeName);
                final EditText routeCity = (EditText) viewDialog.findViewById(R.id.routeCity);
                final EditText routeHighway = (EditText) viewDialog.findViewById(R.id.routeHighway);

                Button saveButton = (Button) viewDialog.findViewById(R.id.saveRoute);
                Button useButton = (Button) viewDialog.findViewById(R.id.useRoute);
                Button cancelButton = (Button) viewDialog.findViewById(R.id.cancelRoute);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (routeName.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter a name",
                                    Toast.LENGTH_SHORT).show();
                        } else if (routeCity.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter the city distance",
                                    Toast.LENGTH_SHORT).show();
                        } else if (routeHighway.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter the highway distance",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            nameSaved = routeName.getText().toString();
                            String str_citySaved = routeCity.getText().toString();
                            citySaved = Double.valueOf(str_citySaved);
                            String str_highwaySaved = routeHighway.getText().toString();
                            highwaySaved = Double.valueOf(str_highwaySaved);

                            if (citySaved == 0) {
                                Toast.makeText(EditJourneyActivity.this, "Please enter an positive city distance", Toast.LENGTH_SHORT).show();
                            } else if (highwaySaved == 0) {
                                Toast.makeText(EditJourneyActivity.this, "Please enter an positive highway distance", Toast.LENGTH_SHORT).show();
                            } else {
                                Route newRoute = new Route(nameSaved, citySaved, highwaySaved);
                                User.getInstance().getRouteList().addRoute(newRoute);
                                setUpRouteSpinner(index);
                                viewDialog.cancel();
                            }
                        }
                    }

                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewDialog.cancel();
                    }
                });

                useButton.setOnClickListener(new View.OnClickListener() {

                    Route newRoute = new Route();

                    @Override
                    public void onClick(View v) {
                        if (routeName.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter a name",
                                    Toast.LENGTH_SHORT).show();
                        } else if (routeCity.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter the city distance",
                                    Toast.LENGTH_SHORT).show();
                        } else if (routeHighway.length() == 0) {
                            Toast.makeText(EditJourneyActivity.this,
                                    "Please enter the highway distance",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            nameSaved = routeName.getText().toString();
                            String str_citySaved = routeCity.getText().toString();
                            citySaved = Double.valueOf(str_citySaved);
                            String str_highwaySaved = routeHighway.getText().toString();
                            highwaySaved = Double.valueOf(str_highwaySaved);

                            if (citySaved == 0) {
                                Toast.makeText(EditJourneyActivity.this, "Please enter an positive city distance", Toast.LENGTH_SHORT).show();
                            } else if (highwaySaved == 0) {
                                Toast.makeText(EditJourneyActivity.this, "Please enter an positive highway distance", Toast.LENGTH_SHORT).show();
                            } else {
                                newRoute = new Route(nameSaved, citySaved, highwaySaved);
                                User.getInstance().setCurrentJourneyRoute(newRoute);

                                Log.i("MyApp", "User selected route \"" + newRoute.getRouteName() + "\"");

                                // Set current Journey to use the selected route
                                User.getInstance().setCurrentJourneyRoute(newRoute);

                                Journey journey = User.getInstance().getCurrentJourney();
                                journey.setTotalDistance(citySaved + highwaySaved);
                                //double emission = journey.calculateCarbonEmission();
                                //journey.setCarbonEmitted(emission);
                                User.getInstance().resetCurrentJourneyEmission();

                                User.getInstance().addJourney(User.getInstance().getCurrentJourney());

                                Intent intent = new Intent(EditJourneyActivity.this, JourneyEmissionActivity.class);
                                startActivityForResult(intent, 0);

                                viewDialog.cancel();
                            }
                        }
                    }
                });
            }
        });
    }

    private void setupSelectModeTxt() {
        TextView selectTxt = (TextView) findViewById(R.id.edit_journey_selectTransportationMode);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Peter.ttf");
        selectTxt.setTypeface(face);
    }

    private void setUpRouteSpinner(int index) {
        //Create a String list
        RouteList routeList = User.getInstance().getRouteList();
        List<String> list = getRouteNames(routeList);
        Spinner routeSpin = (Spinner) findViewById(R.id.routeList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        routeSpin.setAdapter(adapter);
        if (index >= 0) {
            Route originalRoute = User.getInstance().getJourney(index).getRoute();
            int routeIndex = routeList.getRoutes().indexOf(originalRoute);
            routeSpin.setSelection(routeIndex);
        }
    }

    @NonNull
    private List<String> getRouteNames(RouteList routeList) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < routeList.countRoutes(); i++) {
            list.add(i, routeList.getRoute(i).getRouteName());
        }
        return list;
    }

    @NonNull
    private List<String> getCarNames(List<Car> Cars) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < Cars.size(); i++) {
            list.add(i, Cars.get(i).getNickname());
        }
        return list;
    }


    private void launchNewVehicleDialog(){
        FragmentManager manager = getSupportFragmentManager();
        NewVehicleFragment dialog = new NewVehicleFragment();
        dialog.show(manager, "NewVehicleDialog");
    }
}