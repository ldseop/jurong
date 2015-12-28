
package com.ssiot.jurong;

import android.R.integer;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.os.Build;
import android.preference.PreferenceManager;

import com.ssiot.jurong.ctr.AllCtrFrag;
import com.ssiot.jurong.monitor.AllMoniFrag;
import com.ssiot.jurong.video.AllVideoFrag;
import com.ssiot.jurong.video.VideoActivity;

public class JuRongActivity extends ActionBarActivity implements MainFrag.FMainBtnClickListener ,AllVideoFrag.FAllVideoBtnClickListener{
    private static final String tag = "JuRong句容";
    private final static String TAG_HEADER_TAB = "tag_header_tab";
    private SharedPreferences mPref;
    
    public static String mUniqueID = "";
    public static int AreaID= -1;//TODO
    public static int currentArea = 1;
    public static boolean isFake = false;
    
    public static final int[] APP_TITILES = {R.string.app_dapeng,R.string.app_shuichan,R.string.app_datian};
    public static final String[] AREATITLES = {"大棚","水产","大田"};
    public static final int[] AREA_DRAWABLE_ID = {R.drawable.dapengbig,R.drawable.shuichanbig,R.drawable.datianbig};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorDrawable cd = new ColorDrawable(0xff087d25);
        getSupportActionBar().setBackgroundDrawable(cd);// 在代码中会延迟 TODO // 网上下一个v7的例子试试
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        Bundle b = getIntent().getExtras();
        if (null != b){ 
            mUniqueID = b.getString("userkey");
            Log.v(tag, "------------mUniqueID:" + mUniqueID);
        }
        setContentView(R.layout.activity_ju_rong);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFrag())
                    .commit();
        }
//        Utils.changePic2(this);
    }
    
    public void setActionTitle(String txt){
        getSupportActionBar().setTitle(txt);
    }
    
    @Override
    protected void onResume() {
//        setActionTitle(getResources().getString(R.string.app_title));
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ju_rong, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                Editor e = mPref.edit();
                e.putString("password", "");
                e.commit();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onFMainBtnClick(int index) {
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        currentArea = 1;
        switch (index) {
            case 1:
                currentArea = 1;
                HeaderTabFrag ht = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht, TAG_HEADER_TAB);
                Bundle bundle = new Bundle();
                bundle.putString("uniqueid", mUniqueID);
                bundle.putInt("defaulttab", 1); 
                bundle.putInt("currentArea", currentArea);
                ht.setArguments(bundle);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 2:
                currentArea = 2;
                HeaderTabFrag ht2 = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht2, TAG_HEADER_TAB);
                Bundle bundle2 = new Bundle();
                bundle2.putString("uniqueid", mUniqueID);
                bundle2.putInt("defaulttab", 1); 
                bundle2.putInt("currentArea", currentArea);
                ht2.setArguments(bundle2);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 3:
                currentArea = 3;
                HeaderTabFrag ht3 = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht3, TAG_HEADER_TAB);
                Bundle bundle3 = new Bundle();
                bundle3.putString("uniqueid", mUniqueID);
                bundle3.putInt("defaulttab", 1); 
                bundle3.putInt("currentArea", currentArea);
                ht3.setArguments(bundle3);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 4:
                AllMoniFrag amFrag = new AllMoniFrag();
                mTransaction.replace(R.id.container, amFrag, TAG_HEADER_TAB);
                Bundle bundle4 = new Bundle();
                bundle4.putString("uniqueid", mUniqueID);
                amFrag.setArguments(bundle4);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 5:
                AllCtrFrag acFrag = new AllCtrFrag();
                mTransaction.replace(R.id.container, acFrag, TAG_HEADER_TAB);
                Bundle bundle5 = new Bundle();
                bundle5.putString("uniqueid", mUniqueID);
                acFrag.setArguments(bundle5);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 6:
                AllVideoFrag avFrag = new AllVideoFrag();
                mTransaction.replace(R.id.container, avFrag, TAG_HEADER_TAB);
                Bundle bundle6 = new Bundle();
                bundle6.putString("uniqueid", mUniqueID);
                avFrag.setArguments(bundle6);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;

            default:
                HeaderTabFrag m = new HeaderTabFrag();
                mTransaction.replace(R.id.container, m, TAG_HEADER_TAB);
                Bundle b = new Bundle();
                b.putString("uniqueid", mUniqueID);
                b.putInt("defaulttab", 1); 
                m.setArguments(b);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
        }
    }

    @Override
    public void onFAllVideoBtnClick(int area, int index, boolean isHall) {
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (isHall){
            Intent intent = new Intent(JuRongActivity.this, VideoActivity.class);
            intent.putExtra("datingvideoindex", index);
            startActivity(intent);
            return;
        }
        switch (area) {
            case 1:
                currentArea = 1;
                HeaderTabFrag ht = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht, TAG_HEADER_TAB);
                Bundle bundle = new Bundle();
                bundle.putString("uniqueid", mUniqueID);
                bundle.putInt("defaulttab", 3); 
                bundle.putInt("currentArea", currentArea);
                bundle.putInt("videoindex", index);
                ht.setArguments(bundle);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 2:
                currentArea = 2;
                HeaderTabFrag ht2 = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht2, TAG_HEADER_TAB);
                Bundle bundle2 = new Bundle();
                bundle2.putString("uniqueid", mUniqueID);
                bundle2.putInt("defaulttab", 3); 
                bundle2.putInt("currentArea", currentArea);
                ht2.setArguments(bundle2);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;
            case 3:
                currentArea = 3;
                HeaderTabFrag ht3 = new HeaderTabFrag();
                mTransaction.replace(R.id.container, ht3, TAG_HEADER_TAB);
                Bundle bundle3 = new Bundle();
                bundle3.putString("uniqueid", mUniqueID);
                bundle3.putInt("defaulttab", 3); 
                bundle3.putInt("currentArea", currentArea);
                ht3.setArguments(bundle3);
                mTransaction.addToBackStack(null);
                mTransaction.commit();
                break;

            default:
                break;
        }
    }
}
