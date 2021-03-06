
package com.ssiot.jurong;

import android.R.integer;
import android.support.v7.app.ActionBarActivity;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

import com.ssiot.jurong.ctr.AllCtrFrag;
import com.ssiot.jurong.monitor.AllMoniFrag;
import com.ssiot.jurong.monitor.MoniDataAndChartFrag;
import com.ssiot.jurong.monitor.MonitorListAdapter;
import com.ssiot.jurong.video.AllVideoFrag;
import com.ssiot.jurong.video.VideoActivity;
import com.ssiot.jurong.UpdateManager;
import com.ssiot.jurong.Utils;
import com.ssiot.remote.data.model.view.NodeView2Model;

import java.util.HashMap;

public class JuRongActivity extends ActionBarActivity implements MainFrag.FMainBtnClickListener ,AllVideoFrag.FAllVideoBtnClickListener
,SettingFrag.FSettingBtnClickListener{
    private static final String tag = "JuRong句容";
    private final static String TAG_HEADER_TAB = "tag_header_tab";
    private final static String TAG_SETTING = "tag_setting";
    private SharedPreferences mPref;
    
    public static String mUniqueID = "";
    public static int AreaID= -1;//TODO
    public static int currentArea = 1;
    public static boolean isFake = false;
    
    public static final int[] APP_TITILES = {R.string.app_dapeng,R.string.app_shuichan,R.string.app_datian};
    public static final String[] AREATITLES = {"大棚","水产","大田"};
    public static final int[] AREA_DRAWABLE_ID = {R.drawable.dapengbig,R.drawable.shuichanbig,R.drawable.datianbig};
    
    
    private UpdateManager mUpdaManager;
    private Notification mNoti;
    public static final int MSG_GETVERSION_END = 1;
    public static final int MSG_DOWNLOADING_PREOGRESS = 2;
    public static final int MSG_DOWNLOAD_FINISH = 3;
    public static final int MSG_SHOWERROR = 4;
    public static final int MSG_DOWNLOAD_CANCEL = 5;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_GETVERSION_END:
                    if (msg.arg1 <= 0){//大多是网络问题
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 0);
                        sendBroadcast(i);
                    } else if (msg.arg1 > msg.arg2){//remoteversion > curVersion
                        HashMap<String, String> mVerMap = (HashMap<String, String>) msg.obj;
                        showUpdateChoseDialog(mVerMap);
                        
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 1);
                        sendBroadcast(i);
                    } else if (msg.arg1 == msg.arg2){
                        Intent i = new Intent(SettingFrag.ACTION_SSIOT_UPDATE);
                        i.putExtra("checkresult", 2);
                        sendBroadcast(i);
                    } else {
                        Toast.makeText(JuRongActivity.this, "本地版本高于服务器上版本", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MSG_DOWNLOADING_PREOGRESS:
                    Log.v(tag, "-------PREOGRESS----" +msg.arg1 + " " + (null != mNoti));
                    int pro = msg.arg1;
                    if (null != mNoti){
                        NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                        mNoti.contentView.setProgressBar(R.id.noti_progress, 100, pro, false);
//                        mNoti.contentView.setTextViewText(R.id.noti_text, "" + pro);
                        mNoti.setLatestEventInfo(JuRongActivity.this, "正在更新", "已下载：" + pro + "%", 
                                PendingIntent.getActivity(JuRongActivity.this, -1, new Intent(""), 0));
                        mnotiManager.notify(UpdateManager.NOTIFICATION_FLAG, mNoti);
                    }
                    break;
                case MSG_DOWNLOAD_FINISH:
                    NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mnotiManager.cancel(UpdateManager.NOTIFICATION_FLAG);
                    mUpdaManager.installApk();
                    break;
                case MSG_SHOWERROR:
                    NotificationManager mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mManager.cancel(UpdateManager.NOTIFICATION_FLAG);
                    Toast.makeText(JuRongActivity.this, "下载出现错误", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
        };
    };
    
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
        } else {
            savedInstanceState.remove("android:support:fragments");//解决getactivity为空的问题？？
            Log.v(tag, "---------------fragcount&&&&&&&&&&:"+getSupportFragmentManager().getBackStackEntryCount());
        }
        
        if (mPref.getBoolean(Utils.PREF_AUTOUPDATE, true) == true){
            mUpdaManager = new UpdateManager(this, mHandler);
            mUpdaManager.startGetRemoteVer();
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
//                final FragmentTransaction transaction = mTransaction;
                mTransaction.replace(R.id.container, amFrag, TAG_HEADER_TAB);
                Bundle bundle4 = new Bundle();
                bundle4.putString("uniqueid", mUniqueID);
                amFrag.setArguments(bundle4);
                MonitorListAdapter.DetailListener mDetailListener = new MonitorListAdapter.DetailListener() {
                    @Override
                    public void showDetail(NodeView2Model n2m) {
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new MoniDataAndChartFrag();
                        Bundle bundle = new Bundle();
                        bundle.putString("nodetitle", n2m._location);
                        bundle.putBoolean("status", n2m._isonline.equals("在线"));
                        bundle.putBoolean("isgprs", "GPRS".equalsIgnoreCase(n2m._onlinetype));
                        bundle.putInt("nodeno", n2m._nodeno);
                        fragment.setArguments(bundle);
                        trans.replace(R.id.container, fragment);
                        trans.addToBackStack(null);
                        trans.commit();  
                    }
                };
                amFrag.setDetailListener(mDetailListener);
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
            case 7:
                SettingFrag settingFragment = new SettingFrag();
                mTransaction.replace(R.id.container, settingFragment, TAG_SETTING);
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
    
    private void showUpdateChoseDialog(HashMap<String, String> mVerMap){
        final HashMap<String, String> tmpMap = mVerMap;
        AlertDialog.Builder builder =new Builder(this);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        builder.setPositiveButton(R.string.soft_update_updatebtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNoti = mUpdaManager.showNotification(JuRongActivity.this);
//                        .setProgressBar(R.id.noti_progress, 100, 0, false);
                mUpdaManager.startDownLoad(tmpMap);
//                showDownloadDialog(tmpMap);
                dialog.dismiss();
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, true);
                e.commit();
                Toast.makeText(JuRongActivity.this, "转向后台下载，可在通知栏中查看进度。", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.soft_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editor e = mPref.edit();
                e.putBoolean(Utils.PREF_AUTOUPDATE, false);
                e.commit();
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    @Override
    public void onFSettingBtnClick() {
        if (mUpdaManager == null){
            mUpdaManager = new UpdateManager(JuRongActivity.this, mHandler);
        }
        mUpdaManager.startGetRemoteVer();
    }
}
