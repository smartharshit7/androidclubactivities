package samples.aalamir.customcalendar.OnlineData;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import samples.aalamir.customcalendar.OfflineData.FeedReaderContract;
import samples.aalamir.customcalendar.OfflineData.FeedReaderDbHelper;
import samples.aalamir.customcalendar.OfflineData.JSONTransReceiver;
import samples.aalamir.customcalendar.R;

/**
 * Created by Shreyansh on 2/21/2018.
 */

public class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTableName;
        public ImageView tableDownload;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtTableName = (TextView)itemView.findViewById(R.id.txttablename);
            tableDownload = (ImageView)itemView.findViewById(R.id.online_table_download);
        }
    }

    //constructor
    public TablesAdapter(ArrayList<DataModel> dataList){
        this.dataSet = dataList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.tables_adapter_list_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DataModel data = dataSet.get(position);
        holder.txtTableName.setText(data.getTablename());

        final OnlineDatabases onlineDatabases = OnlineDatabases.getInstance();

        holder.tableDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(onlineDatabases,""+data.getTablename(),Toast.LENGTH_SHORT).show();

                JSONTransReceiver transReceiver = new JSONTransReceiver();
                transReceiver.execute(data.getTablename());

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
