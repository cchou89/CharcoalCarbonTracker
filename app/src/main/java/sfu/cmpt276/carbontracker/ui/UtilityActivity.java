package sfu.cmpt276.carbontracker.ui;

import android.support.v4.app.FragmentManager;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import sfu.cmpt276.carbontracker.R;
import sfu.cmpt276.carbontracker.carbonmodel.User;
import sfu.cmpt276.carbontracker.carbonmodel.Utility;
import sfu.cmpt276.carbontracker.carbonmodel.UtilityList;
import sfu.cmpt276.carbontracker.ui.database.UtilityDataSource;
/*Main utility activity to show utilities and allow use to add new ones*/
public class UtilityActivity extends AppCompatActivity {
    private ListView list;
    private int edit_position;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);

        populateListView();
        setupAddBtn();
        registerClickCallback();

    }

    private void tipDialogue() {
        if (!User.getInstance().getJourneyList().isEmpty() || !User.getInstance().getUtilityList().getUtilities().isEmpty()) {
            FragmentManager manager = getSupportFragmentManager();
            TipDialogFragment tipDialog = new TipDialogFragment();
            tipDialog.show(manager, "TipsDialog");
        }
    }

    private void setupAddBtn() {
        Button addBtn = (Button) findViewById(R.id.addBillList);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = BillActivity.makeIntent(UtilityActivity.this);
                startActivity(intent);
            }
        });
    }

    private void populateListView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,                       // Context for the activity
                R.layout.bill_item,          // Layout to use (create)
                User.getInstance().getUtilityList().getUtilityDescription());            // Items to be displayed

        // Configure the list view
        list = (ListView) findViewById(R.id.utilityList);
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateListView();
        User.getInstance().resetTips();
        tipDialogue();

    }

    private void registerClickCallback() {
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                edit_position = position;
                mode = 10;
                Intent intent = BillActivity.makeIntent(UtilityActivity.this);
                intent.putExtra("pos", edit_position);
                intent.putExtra("mode", mode);
                startActivity(intent);
                return true;
            }
        });

    }

}
