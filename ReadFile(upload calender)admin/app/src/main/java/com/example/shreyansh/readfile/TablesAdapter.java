package com.example.shreyansh.readfile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Shreyansh on 2/21/2018.
 */

public class TablesAdapter extends RecyclerView.Adapter<TablesAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTableName;
        public ImageView tableDelete;
        public ImageView tableCloudUpload;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtTableName = itemView.findViewById(R.id.txttablename);
            tableDelete = itemView.findViewById(R.id.table_list_delete);
            tableCloudUpload = itemView.findViewById(R.id.upload_table);
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

        holder.tableDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = MainActivity.getInstance();
                mainActivity.deleteTable(data.getTablename());
            }
        });

        holder.tableCloudUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = MainActivity.getInstance();
                mainActivity.uploadTable(data.getTablename());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
