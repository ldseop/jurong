package com.ssiot.jurong.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.ssiot.jurong.R;

//句容大厅 专用
public class VideoActivity extends ActionBarActivity{
    private static final String tag = "VideoActivity";
    int index = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (null != intent){
            index = intent.getIntExtra("datingvideoindex", 1);
        }
        setContentView(R.layout.activity_video);
        if (savedInstanceState == null) {
            HCLiveFrag frag = new HCLiveFrag();
            Bundle bundle = new Bundle();
            bundle.putInt("currentArea", 1);//虽然没用，但是防止nullpointer
            bundle.putInt("videoindex", index);
            bundle.putBoolean("ishall", true);
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_container, frag)
                    .commit();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
}