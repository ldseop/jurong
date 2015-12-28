package com.ssiot.jurong.video;

import android.R.integer;
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
import android.widget.TextView;

import com.ssiot.jurong.BaseFragment;
import com.ssiot.jurong.JuRongActivity;
import com.ssiot.jurong.R;

public class AllVideoFrag extends BaseFragment{
    public static final String tag = "AllVideoFragment";
    private FAllVideoBtnClickListener mFAllVideoBtnClickListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFAllVideoBtnClickListener = (FAllVideoBtnClickListener) getActivity();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_all_video, container, false);
        findviews(v);
        return v;
    }
    
    private void findviews(View rootView){
        final int[] ids  = {R.id.vid_1_1,R.id.vid_1_2, R.id.vid_2_1, R.id.vid_3_1 , R.id.vid_4_1, R.id.vid_4_2};
        View.OnClickListener cli= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mFAllVideoBtnClickListener){
                    int areaindex = 1;
                    int index = 1;
                    boolean isHall = false;
                    switch (v.getId()) {
                        case R.id.vid_1_1:
                            areaindex = 1;
                            index = 1;
                            break;
                        case R.id.vid_1_2:
                            areaindex = 1;
                            index =2;
                            break;
                        case R.id.vid_2_1:
                            areaindex = 2;
                            break;
                        case R.id.vid_3_1:
                            areaindex = 3;
                            break;
                        case R.id.vid_4_1:
                            index =1;
                            isHall = true;
                            break;
                        case R.id.vid_4_2:
                            index =2;
                            isHall = true;
                            break;
                        default:
                            break;
                    }
                    mFAllVideoBtnClickListener.onFAllVideoBtnClick(areaindex, index, isHall);
                }
            }
        };
        for (int i = 0;i < ids.length; i ++){
            TextView t = (TextView) rootView.findViewById(ids[i]);
            t.setOnClickListener(cli);
        }
        
    }
    
    @Override
    public void onResume() {
        ((JuRongActivity) getActivity()).setActionTitle("视频监控");
        super.onResume();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
//        inflater.inflate(R.menu.AllVideo, menu);
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
    
    public void setClickListener(FAllVideoBtnClickListener listen){
        mFAllVideoBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FAllVideoBtnClickListener {  
        void onFAllVideoBtnClick(int area, int index, boolean isHall);
    }
}