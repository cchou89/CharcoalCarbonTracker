package sfu.cmpt276.carbontracker.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import sfu.cmpt276.carbontracker.R;
import sfu.cmpt276.carbontracker.carbonmodel.User;
import sfu.cmpt276.carbontracker.carbonmodel.Journey;
/* Activity that displays carbon footprint in form of table of journeys or pie graph*/

//Array of options --> Array Adapter --> ListView

// ListView: {views: journeys.xml}

public class JourneyActivity extends AppCompatActivity {
    List<Journey> JourneyList = User.getInstance().getJourneyList();
    //Journey journey = User.getInstance().getCurrentJourney();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
        
        populateListView(JourneyList);
        //setupTable();
        setupPieButton();
    }

    private void populateListView(List<Journey> journeyList) {
        //create list
        //Build adapter
        ArrayAdapter<Journey> adapter = new MyListAdapter(); // items to be displayed
        //configure items;
        ListView list = (ListView) findViewById(R.id.listJourney);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Journey> {
        public MyListAdapter() {
            super(JourneyActivity.this, R.layout.journeys, JourneyList);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View journeyView = convertView;
            if(journeyView == null){
                journeyView = getLayoutInflater().inflate(R.layout.journeys, parent, false);
            }
            //populate the list
            //find the journey
            Journey thisJourney = User.getInstance().getJourneyList().get(position);
            //Initialize TextViews
            TextView date = (TextView) journeyView.findViewById(R.id.viewDate);
            TextView vehicle = (TextView) journeyView.findViewById(R.id.viewVehicle);
            TextView route = (TextView) journeyView.findViewById(R.id.viewRoute);
            TextView distance = (TextView) journeyView.findViewById(R.id.viewDistance);
            TextView emission = (TextView) journeyView.findViewById(R.id.viewEmission);
            //fill the view
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            String str_date = sdf.format(thisJourney.getDate());
            date.setText(str_date);
            String nickname = thisJourney.getCar().getNickname();
            vehicle.setText(nickname);
            String routeName = thisJourney.getRoute().getRouteName();
            route.setText(routeName);
            String totalDistance = Double.toString(thisJourney.getTotalDistance());
            distance.setText(totalDistance);
            String totalEmissions = Double.toString(thisJourney.getCarbonEmitted());
            emission.setText(totalEmissions);

            return journeyView;
        }
    }

    private void setupTable() {
        TableLayout journeyTable = (TableLayout) findViewById(R.id.table);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(0, 0, 0, 0);

        for (Journey journey : User.getInstance().getJourneyList()) {

            Log.i("TABLETEST", journey.getRouteName());

            String carName = journey.getCar().getNickname();
            String routeName = journey.getRouteName();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            String str_date = sdf.format(journey.getDate());
            double distance = journey.getRoute().getRouteDistanceCity() + journey.getRoute().getRouteDistanceHighway();
            String str_distance = String.format("%.2f", distance);
            double emission = journey.getCarbonEmitted();
            String str_emission = String.format("%.2f", emission);

            TextView textDate = new TextView(this);
            textDate.setText(str_date);
            textDate.setPadding(0,0,0,0);

            TextView textCar = new TextView(this);
            textCar.setText(carName);
            textCar.setPadding(0,0,0,0);

            TextView textRoute = new TextView(this);
            textRoute.setText(routeName);
            textRoute.setPadding(0,0,0,0);

            TextView textDistance = new TextView(this);
            textDistance.setText(str_distance);
            textDistance.setPadding(0,0,0,0);

            TextView textEmission = new TextView(this);
            textEmission.setText(str_emission);
            textEmission.setPadding(0,0,0,0);

            TableRow row = new TableRow(this);

            row.addView(textDate, lp);
            row.addView(textCar, lp);
            row.addView(textRoute, lp);
            row.addView(textDistance, lp);
            row.addView(textEmission, lp);
            journeyTable.addView(row);
        }

    }

    private void setupPieButton() {
        Button pieButton = (Button) findViewById(R.id.pieButton);
        pieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JourneyActivity.this, PieGraphActivity.class);
                startActivity(intent);
            }
        });
    }
}