package samples.aalamir.customcalendar.OfflineData;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import samples.aalamir.customcalendar.MainActivity;
import samples.aalamir.customcalendar.OnlineData.DataModel;
import samples.aalamir.customcalendar.OnlineData.TablesAdapter;
import samples.aalamir.customcalendar.R;

/**
 * Created by Shreyansh on 3/14/2018.
 */

public class OfflineTablesAdapter extends RecyclerView.Adapter<OfflineTablesAdapter.MyOfflineViewHolder>{

    private ArrayList<DataModel> dataSet = new ArrayList<>();

    @Override
    public MyOfflineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.tables_adapter_offline_list_item, parent, false);

        return new MyOfflineViewHolder(itemView);
    }

    //constructor
    public OfflineTablesAdapter(ArrayList<DataModel> dataList){
        this.dataSet = dataList;
    }


    @Override
    public void onBindViewHolder(MyOfflineViewHolder holder, int position) {
        final DataModel data = dataSet.get(position);
        holder.txtTableName.setText(data.getTablename());

        final MyDatabases myDatabases = MyDatabases.getInstance();

        holder.tableDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDatabases.deleteTable(data.getTablename());
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class MyOfflineViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTableName;
        public ImageView tableDelete;
        public MyOfflineViewHolder(View itemView) {
            super(itemView);
            txtTableName = (TextView)itemView.findViewById(R.id.txttablename);
            tableDelete = (ImageView)itemView.findViewById(R.id.offline_table_delete);
        }
    }
}
