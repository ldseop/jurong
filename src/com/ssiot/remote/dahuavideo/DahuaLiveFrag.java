package com.ssiot.remote.dahuavideo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssiot.jurong.JuRongActivity;
import com.ssiot.jurong.R;

public class DahuaLiveFrag extends Fragment{
    public static final String tag = "MainFragment";
    private FMainBtnClickListener mFMainBtnClickListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_single_video, container, false);    
        setBigTitle(v);
        return v;
    }
    
    private void setBigTitle(View rootView){
        RelativeLayout headerView = (RelativeLayout) rootView.findViewById(R.id.video_big_top);
        headerView.setBackgroundResource(JuRongActivity.AREA_DRAWABLE_ID[JuRongActivity.currentArea - 1]);
        TextView t = (TextView) rootView.findViewById(R.id.big_title);
        t.setText(JuRongActivity.AREATITLES[JuRongActivity.currentArea - 1]);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
//        inflater.inflate(R.menu.main, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Log.v(tag, "----------------action-settting");
//                break;
//
//            default:
//                break;
//        }
        return true;
    }
    
    public void setClickListener(FMainBtnClickListener listen){
        mFMainBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FMainBtnClickListener {  
        void onFMainBtnClick();  
    }
}