package edu.admu.cs295s37.watchout;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class ReportAdapter extends RealmBaseAdapter<HazardReport> implements ListAdapter {

    OrderedRealmCollection<HazardReport> realmResults;
    Activity activity;

    public ReportAdapter (Activity activity, OrderedRealmCollection<HazardReport> realmResults) {
        super(realmResults);
        this.activity = activity;
        this.realmResults = realmResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Make your view
        View view = null;
        if (convertView==null) {
            view = activity.getLayoutInflater().inflate(R.layout.row_report, null    );
        }
        else {
            view = convertView;
        }
        // Get your data
        HazardReport hr = realmResults.get(position);
        // Fill in/binding the view
        TextView title = (TextView) view.findViewById(R.id.tvTitle);
        TextView status = (TextView) view.findViewById(R.id.tvStatus);
        ImageView image = (ImageView) view.findViewById(R.id.ivReport);
        Button edit = (Button) view.findViewById(R.id.bEditReport);
        Button delete = (Button) view.findViewById(R.id.bDeleteReport);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) activity).editHazardReport(view);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) activity).deleteHazardReport(view);
            }
        });
        edit.setTag(hr.getHrid());
        delete.setTag(hr);

        title.setText(hr.getTitle());
        StringBuilder statusText = new StringBuilder();
        if(hr.isResponded()){
            if(hr.isConfirmed()){
                statusText.append(hr.getConfirmedUsers().size() + " user/s confirmed resolution!");
            }
            else{
                statusText.append("Resolved!");
            }
        }
        else {
            if(hr.isConfirmed()){
                statusText.append(hr.getConfirmedResolvedUsers().size() + " user/s confirmed!");
            }
            else{
                statusText.append("Reported!");
            }
        }

        status.setText(statusText);
        System.out.println(hr.getImgPath());
        File savedImage = new File(hr.getImgPath());
        if(savedImage!=null){
            Picasso.with(activity).load(savedImage).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);
        }
        return view;
    }

}