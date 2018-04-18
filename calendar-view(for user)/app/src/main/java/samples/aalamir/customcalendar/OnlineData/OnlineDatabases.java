package samples.aalamir.customcalendar.OnlineData;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import samples.aalamir.customcalendar.R;

/**
 * Created by Shreyansh on 2/26/2018.
 */

public class OnlineDatabases extends ActionBarActivity {
    TextView txtDisplay;
    static OnlineDatabases instance;
    public static ProgressDialog dialog;
    ArrayList<DataModel> onlineTableList;
    public static TablesAdapter tablesAdapter;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_databases);

        instance=this;

        //RECYCLER VIEW
        recyclerView = (RecyclerView)findViewById(R.id.online_tables_recycler_view);
        onlineTableList = new ArrayList<>();


        //SET ADAPTER
        tablesAdapter = new TablesAdapter(onlineTableList);
        RecyclerView.LayoutManager mLayoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutmanager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tablesAdapter);

        JSONReceiver transmitter = new JSONReceiver();
        transmitter.execute();

    }

    public void addToList(ArrayList<DataModel> list){
        //SET ADAPTER
        tablesAdapter = new TablesAdapter(list);
        recyclerView.setAdapter(tablesAdapter);
    }

    public static OnlineDatabases getInstance(){
        return instance;
    }

    public  void showPDialog(){
        dialog = new ProgressDialog(OnlineDatabases.this);
        dialog.setMessage("Downloading Data.Please wait.");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissPDialog(){
        dialog.dismiss();
    }
}
