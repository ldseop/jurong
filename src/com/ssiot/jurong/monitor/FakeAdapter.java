package com.ssiot.jurong.monitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ssiot.jurong.R;

import java.util.List;

public class FakeAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> datas;
    LayoutInflater inflater;
    public FakeAdapter(Context c,List<String> d){
        mContext = c;
        inflater = LayoutInflater.from(c);
        datas = d;
    }

    @Override
    public int getCount() {
        return 8;//datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.monitor_node_view, null);
        
        ImageView imageView = (ImageView) convertView.findViewById(R.id.moni_img);
        imageView.setImageResource(MonitorListAdapter.pics[position%3]);
        
        ImageView status = (ImageView) convertView.findViewById(R.id.moni_status);
        status.setImageResource((position % 2 == 0) ? R.drawable.online_3 : R.drawable.offline_2);
        return convertView;
    }
    
}
