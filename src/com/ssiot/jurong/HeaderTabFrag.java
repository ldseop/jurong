package com.ssiot.jurong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ssiot.jurong.ctr.SingleCtrFrag;
import com.ssiot.jurong.monitor.MoniDataAndChartFrag;
import com.ssiot.jurong.monitor.MoniNodeListFrag;
import com.ssiot.jurong.monitor.MonitorListAdapter;
import com.ssiot.jurong.video.HCLiveFrag;
import com.ssiot.remote.dahuavideo.DahuaLiveFrag;
import com.ssiot.remote.data.model.view.ControlNodeViewModel;
import com.ssiot.remote.data.model.view.NodeView2Model;

public class HeaderTabFrag extends BaseFragment{
    public static final String tag = "HeaderTabFragment";
    private FHeaderTabBtnClickListener mFHeaderTabBtnClickListener;
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup; 
    private RadioButton radioButtonMoni;
    private RadioButton radioButtonCtr;
    private RadioButton radioButtonVideo;
    String userKey;
    int defaultTab = 1;
    private int currentArea = 1;
    private int videoIndex = 1;//只在大棚中区分 1 和2
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fragmentManager = getChildFragmentManager();
        userKey = getArguments().getString("uniqueid");
        defaultTab = getArguments().getInt("defaulttab", 1);
        currentArea = getArguments().getInt("currentArea", 1);
        videoIndex = getArguments().getInt("videoindex" ,1);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_header_tab, container, false);
        
        radioGroup = (RadioGroup) v.findViewById(R.id.rg_tab);
        radioButtonMoni = (RadioButton) v.findViewById(R.id.radiobutton_moni);
        radioButtonCtr = (RadioButton) v.findViewById(R.id.radiobutton_control);
        radioButtonVideo = (RadioButton) v.findViewById(R.id.radiobutton_video);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.v(tag, "----onCheckedChanged----" + checkedId);
                refreshCheckedTextColor(group, checkedId);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Fragment fragment = getInstanceByIndex(checkedId);
                transaction.replace(R.id.detail_content, fragment);
                fragmentManager.popBackStackImmediate();//TODO 是否是全部弹出还是单个弹出
//                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        switch (defaultTab) {
            case 1:
//                radioGroup.check(R.id.radiobutton_moni);
                radioButtonMoni.setChecked(true);
                break;
            case 2:
//                radioGroup.check(R.id.radiobutton_control);
                radioButtonCtr.setChecked(true);
                break;
            case 3:
//                radioGroup.check(R.id.radiobutton_video);
                radioButtonVideo.setChecked(true);//不用check方法因为 check方法会触发两次oncheckchanged 导致HCVideo sdk有问题
                break;
            default:
                radioGroup.check(R.id.radiobutton_moni);
                break;
        }
        return v;
    }
    
    private void refreshCheckedTextColor(View rootView, int checkId){
        RadioButton radioMoni = (RadioButton)  rootView.findViewById(R.id.radiobutton_moni);
        RadioButton radioCtr = (RadioButton)  rootView.findViewById(R.id.radiobutton_control);
        RadioButton radioVideo = (RadioButton) rootView.findViewById(R.id.radiobutton_video);
        radioMoni.setTextColor(getResources().getColor(R.color.black));
        radioCtr.setTextColor(getResources().getColor(R.color.black));
        radioVideo.setTextColor(getResources().getColor(R.color.black));

        RadioButton rb = (RadioButton) rootView.findViewById(checkId);
        rb.setTextColor(getResources().getColor(R.color.radio_on));
    }
    
    @Override
    public void onResume() {
        int res = JuRongActivity.APP_TITILES[JuRongActivity.currentArea - 1];
        ((JuRongActivity) getActivity()).setActionTitle(getResources().getString(R.string.app_title) + "-" + getResources().getString(res));
        super.onResume();
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.HeaderTab, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Log.v(tag, "----------------action-settting");
//                break;
//
//            default:
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }
    
    public void setClickListener(FHeaderTabBtnClickListener listen){
        mFHeaderTabBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FHeaderTabBtnClickListener {  
        void onFHeaderTabBtnClick();
    }
    
    @Override
    public boolean canGoback() {
        if (fragmentManager.getBackStackEntryCount() != 0){
            return true;
        }
        return false;
    }
    
    @Override
    public void onMyBackPressed() {
        Log.v(tag, "----onMyBackPressed----");
        fragmentManager.popBackStack();
    }
    
    private Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.radiobutton_moni:
                fragment = new MoniNodeListFrag();
                Bundle bundle = new Bundle();
                bundle.putString("uniqueid", userKey);
                bundle.putInt("currentArea", currentArea);
                fragment.setArguments(bundle);
                ((MoniNodeListFrag) fragment).setDetailListener(mDetailListener);
                break;
            case R.id.radiobutton_control:
                fragment = new SingleCtrFrag();
                Bundle bun = new Bundle();
                bun.putString("uniqueid", userKey);
                bun.putInt("currentArea", currentArea);
                fragment.setArguments(bun);
//                ((ControlNodeListFrag) fragment).setCtrDetailListener(mCtrDetailListener);
                break;
            case R.id.radiobutton_video:
                fragment = new HCLiveFrag();
                Bundle bund = new Bundle();
                bund.putString("uniqueid", userKey);
                bund.putInt("currentArea", currentArea);
                bund.putInt("videoindex", videoIndex);
                fragment.setArguments(bund);
                break;
            default:
                fragment = new MoniNodeListFrag();
                Bundle bundle4 = new Bundle();
                bundle4.putString("uniqueid", userKey);
                fragment.setArguments(bundle4);
                break;
        }
        return fragment;
    }
    
    MonitorListAdapter.DetailListener mDetailListener = new MonitorListAdapter.DetailListener() {
        @Override
        public void showDetail(NodeView2Model n2m) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            Fragment fragment = new MoniDetailHolderFrag();
            Fragment fragment = new MoniDataAndChartFrag();
//            transaction.replace(R.id.detail_content, fragment);
//            transaction.addToBackStack(null);
//            transaction.commit();  
            Bundle bundle = new Bundle();
            bundle.putString("nodetitle", n2m._location);
            bundle.putBoolean("status", n2m._isonline.equals("在线"));
            bundle.putBoolean("isgprs", "GPRS".equalsIgnoreCase(n2m._onlinetype));
            bundle.putInt("nodeno", n2m._nodeno);
            fragment.setArguments(bundle);
            transaction.replace(R.id.detail_content, fragment);
            transaction.addToBackStack(null);
            transaction.commit();  
        }
    };
//    ControlDetailListener mCtrDetailListener = new ControlDetailListener() {
//        @Override
//        public void showDetail(int position, ControlNodeViewModel model) {
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            Fragment fragment = new ControlDetailHolderFrag();
//            transaction.replace(R.id.detail_content, fragment);
//            Bundle bundle = new Bundle();
//            bundle.putString("userkey", userKey);
//            bundle.putString("controlnodeuniqueid", model._uniqueid);
//            bundle.putString("controlnodeid", ""+model._id);
//            bundle.putString("controlnodename", model._nodename);
//            fragment.setArguments(bundle);
//            transaction.addToBackStack(null);
//            transaction.commit();  
//        }
//    };
}