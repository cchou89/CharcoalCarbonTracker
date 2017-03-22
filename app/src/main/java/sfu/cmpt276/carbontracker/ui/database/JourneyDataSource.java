package sfu.cmpt276.carbontracker.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sfu.cmpt276.carbontracker.carbonmodel.Car;
import sfu.cmpt276.carbontracker.carbonmodel.Journey;
import sfu.cmpt276.carbontracker.carbonmodel.Route;

/**
 * A data source from the Journey Database
 */
public class JourneyDataSource {

    private SQLiteDatabase db;
    private JourneyDatabaseHelper dbHelper;

    private String[] columns = {
            JourneyDatabaseHelper.COLUMN_ID,
            JourneyDatabaseHelper.COLUMN_CAR_ID,
            JourneyDatabaseHelper.COLUMN_ROUTE_ID,
            JourneyDatabaseHelper.COLUMN_DATE
    };

    public JourneyDataSource(Context context) {
        dbHelper = new JourneyDatabaseHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Journey insertJourney(Journey journey, Context context) {
        ContentValues values = new ContentValues();

        values.put(JourneyDatabaseHelper.COLUMN_CAR_ID, journey.getCar().getId());
        values.put(JourneyDatabaseHelper.COLUMN_ROUTE_ID, journey.getRoute().getId());
        values.put(JourneyDatabaseHelper.COLUMN_DATE, journey.getDate().getTime());

        long insertId = db.insert(JourneyDatabaseHelper.TABLE_JOURNEYS, null, values);

        Cursor cursor = db.query(JourneyDatabaseHelper.TABLE_JOURNEYS, columns,
                JourneyDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Journey newJourney = cursorToJourney(cursor, context);
        cursor.close();
        return newJourney;
    }

    public void deleteJourney(Journey journey) {
        long id = journey.getId();
        Log.i(JourneyDataSource.class.getName(), "Journey id " + id + " \"" + journey.toString() + "\" deleted from Journey database");
        db.delete(JourneyDatabaseHelper.TABLE_JOURNEYS, JourneyDatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Journey> getAllJourneys(Context context) {
        List<Journey> journeys = new ArrayList<>();

        CarDataSource car_db = new CarDataSource(context);
        RouteDataSource route_db = new RouteDataSource(context);

        Cursor cursor = db.query(JourneyDatabaseHelper.TABLE_JOURNEYS, columns, null, null, null, null, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            Journey journey = cursorToJourney(cursor, car_db, route_db);
            journeys.add(journey);
            cursor.moveToNext();
        }

        cursor.close();
        return journeys;
    }

    private Journey cursorToJourney(Cursor cursor, CarDataSource car_db, RouteDataSource route_db) {
        Journey journey = new Journey();

        journey.setId(cursor.getInt(0));

        // Get Car from Database
        car_db.open();
        Car car = car_db.getCarById(cursor.getInt(1));
        journey.setCar(car);
        car_db.close();

        // Get Route from Database
        route_db.open();
        Route route = route_db.getRouteById(cursor.getInt(2));
        journey.setRoute(route);
        route_db.close();

        // Set Date in epoch time (long)
        long epoch_time = cursor.getLong(3);
        Date date = new Date(epoch_time);
        journey.setDate(date);

        return journey;
    }

    private Journey cursorToJourney(Cursor cursor, Context context) {
        CarDataSource car_db = new CarDataSource(context);
        RouteDataSource route_db = new RouteDataSource(context);

        return cursorToJourney(cursor, car_db, route_db);
    }
}