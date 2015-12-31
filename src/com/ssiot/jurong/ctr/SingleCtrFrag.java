package com.ssiot.jurong.ctr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.jurong.BaseFragment;
import com.ssiot.jurong.JuRongActivity;
import com.ssiot.jurong.R;
import com.ssiot.jurong.Utils;
import com.ssiot.jurong.ctr.JuRongCtrAdapter.DeviceBtnListener;
import com.ssiot.jurong.monitor.MonitorListAdapter.ThumnailHolder;
import com.ssiot.remote.data.AjaxGetControlActionInfo;
import com.ssiot.remote.data.AjaxGetNodesDataByUserkey;
import com.ssiot.remote.data.model.ControlActionInfoModel;
import com.ssiot.remote.data.model.view.ControlDeviceView3Model;
import com.ssiot.remote.data.model.view.ControlNodeViewModel;
import com.ssiot.remote.data.model.view.CtrNodeViewJuRongModel;

import java.util.ArrayList;
import java.util.List;

public class SingleCtrFrag extends BaseFragment{
    public static final String tag = "SingleCtrFrag";
    private FSingleCtrBtnClickListener mFSingleCtrBtnClickListener;
    
    ListView mNodeListView;
    private int currentIndex = 1;//哪个area是打开的
    
    List<ControlNodeViewModel> mAllNodes = new ArrayList<ControlNodeViewModel>();
    List<CtrNodeViewJuRongModel> mAllDetailNodes = new ArrayList<CtrNodeViewJuRongModel>(); 
    List<CtrNodeViewJuRongModel> mShowDetailNodes = new ArrayList<CtrNodeViewJuRongModel>(); 
    JuRongCtrAdapter mAdapter;
    
    public static final int[] DaPengNode = AllCtrFrag.DaPengNode;
    public static final int[] DaTianNode = AllCtrFrag.DaTianNode;
    public static final int[] YuTangNode = AllCtrFrag.YuTangNode;
    public static final int[][] AllStaticNode = AllCtrFrag.AllStaticNode;
    
    private static final int MSG_GETNODES_END = 1;
    public static final int MSG_GET_ONEIMAGE_END = 2;
    private static final int MSG_REFRESH = 3;
    private static final int MSG_NOTIFY = AllCtrFrag.MSG_NOTIFY;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (!isVisible()){
                Log.e(tag, "----fragment is not visible----!!!!" + msg.what);
                return;
            }
            Log.v(tag, "---handleMessage----" + msg.what + " allsize:" + mAllDetailNodes.size());
            switch (msg.what) {
                case MSG_GETNODES_END:
                    if (JuRongActivity.isFake){
                        List<CtrNodeViewJuRongModel> fakelist = new ArrayList<CtrNodeViewJuRongModel>();
                        List<ControlDeviceView3Model> lis = new ArrayList<ControlDeviceView3Model>();
                        lis.add(new ControlDeviceView3Model());
                        lis.add(new ControlDeviceView3Model());
                        lis.add(new ControlDeviceView3Model());
                        lis.add(new ControlDeviceView3Model());
                        fakelist.add(new CtrNodeViewJuRongModel(new ControlNodeViewModel(), lis));
                        mAdapter = new JuRongCtrAdapter(getActivity(), fakelist, mHandler,deviceBtnListener);
                        mNodeListView.setAdapter(mAdapter);
                        return;
                    }
                    if (null != mAllDetailNodes && mAllDetailNodes.size() > 0){
                        Log.v(tag, "----------refresh node list,currentIndex;" + currentIndex);
                        mShowDetailNodes = checkOutNodes(mAllDetailNodes, currentIndex);
                        mAdapter = new JuRongCtrAdapter(getActivity(), mShowDetailNodes, mHandler,deviceBtnListener);
                        mNodeListView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(tag, "----------MSG_GETNODES_END-error!!!!!!!!!!!!!!");
                    }
                    break;
                case MSG_GET_ONEIMAGE_END:
//                    mNodeAdapter.notifyDataSetChanged();//不能notify，会无限循环
                    ThumnailHolder thumb = (ThumnailHolder) msg.obj;
                    thumb.imageView.setImageBitmap(thumb.bitmap);
                    break;
                case MSG_REFRESH:
                    new GetCtrJuRongActionThread().start();
                    break;
                case MSG_NOTIFY:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        };
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        currentIndex = getArguments().getInt("currentArea" , 1);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_single_ctr, container, false);
        mNodeListView = (ListView) v.findViewById(R.id.ctr_seperate_list);
        setBigTitle(v);
        if (!Utils.isNetworkConnected(getActivity())){
            Toast.makeText(getActivity(), R.string.please_check_net, Toast.LENGTH_LONG).show();
        } else {
            new GetCtrJuRongActionThread().start();
        }
        return v;
    }
    
    private class GetCtrJuRongActionThread extends Thread{
        @Override
        public synchronized void run() {
            if (JuRongActivity.isFake){
                mHandler.sendEmptyMessage(MSG_GETNODES_END);
                return;
            }
            if (!TextUtils.isEmpty(JuRongActivity.mUniqueID)){
                sendShowMyDlg("正在查询");
                mAllNodes = new AjaxGetNodesDataByUserkey().GetControlNodesByUserkey(JuRongActivity.mUniqueID);
                if (null != mAllNodes){
                    mAllDetailNodes.clear();
                    for(int i = 0; i < mAllNodes.size(); i ++){
                        ControlNodeViewModel m = mAllNodes.get(i);
                        List<ControlDeviceView3Model> ddd =new AjaxGetNodesDataByUserkey().GetDeviceActionInfo(""+m._id, m._uniqueid);
//                        m._nodename;
                        mAllDetailNodes.add(new CtrNodeViewJuRongModel(m, ddd));
                    }
                    mHandler.sendEmptyMessage(MSG_GETNODES_END);
                } else {
                    Log.e(tag, "----mAllNodes == null");
                }
                sendDismissDlg();
            } else {
                Log.e(tag, "----!!!! mUniqueID = null");
            }
        }
    }
    
    DeviceBtnListener deviceBtnListener = new DeviceBtnListener() {
        @Override
        public void onDeviceBtnClick(final CtrNodeViewJuRongModel sm, final int position, final boolean lastStatus) {
            if (sm._ControlNodeViewModel._nodeno == 341 || sm._ControlNodeViewModel._nodeno == 342){
                new Utils().createMsgDialog(getActivity(), "暂未安装").show();
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Utils.isNetworkConnected(getActivity())){
                        showToastMSG("无网络");
                        return;
                    }
                    if ((!lastStatus) && (AllCtrFrag.shadowCloseWorking || AllCtrFrag.shadowOpenWorking)){
                        showToastMSG("打开与收起不能同时执行,请先关闭另一个动作！");
                        return;
                    }
                    if (lastStatus) {
                        sendShowMyDlg("正在设置");
                        String ret = new AjaxGetNodesDataByUserkey().ControlDevice(
                                sm._ControlNodeViewModel._uniqueid,
                                "" + sm._detailList.get(position).DeviceNo, "无", "close");
                        if ("true".equalsIgnoreCase(ret)) {

                        } else {
                            Log.e(tag, "---------close device fail__!!!!!!!!!!!!!!!!!"
                                    + sm._ControlNodeViewModel._uniqueid);
                        }
                        sendDismissDlg();
                        mHandler.sendEmptyMessage(MSG_REFRESH);
                    } else {
                        new OpenDiaFrag("" + sm._detailList.get(position).DeviceNo, "",
                                sm._ControlNodeViewModel._uniqueid).show(getFragmentManager(),
                                "tag-allctrfrag");
                    }

                }
            }).start();
        }
    };
    
    private String getExistIDInControlActionInfo(String controlNode, String controlDeviceNo) {
        List<ControlActionInfoModel> data = new AjaxGetControlActionInfo()
                .GetControlActionInfo(controlNode, ""
                        + controlDeviceNo);// TODO  thread
        String id = "";
        if (null != data) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i)._controltype == 1) {
                    id = "" + data.get(i)._id;// 找出已有的
                }
            }
        }
        return id;
    }
    
    private boolean isIn(int[] js,int j){
        if (js != null && js.length > 0){
            for (int i = 0; i < js.length; i ++){
                if (j == js[i]){
                    return true;
                }
            }
        }
        return false;
    }
    
    private ArrayList<CtrNodeViewJuRongModel> checkOutNodes(List<CtrNodeViewJuRongModel> nodes, int currentHolderIndex){
        ArrayList<CtrNodeViewJuRongModel> datas = new ArrayList<CtrNodeViewJuRongModel>();
        for(CtrNodeViewJuRongModel m : nodes){
            if (isIn(AllStaticNode[currentHolderIndex - 1], m._ControlNodeViewModel._nodeno)){
                datas.add(m);
            }
       }
       return datas;
    }
    
    private void setBigTitle(View rootView){
        RelativeLayout headerView = (RelativeLayout) rootView.findViewById(R.id.ctr_big_top);
        headerView.setBackgroundResource(JuRongActivity.AREA_DRAWABLE_ID[JuRongActivity.currentArea - 1]);
        TextView t = (TextView) rootView.findViewById(R.id.big_title);
        t.setText(JuRongActivity.AREATITLES[JuRongActivity.currentArea - 1]);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_monitor, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_monitor_refresh:
                if (!Utils.isNetworkConnected(getActivity())){
                    Toast.makeText(getActivity(), R.string.please_check_net, Toast.LENGTH_LONG).show();
                } else {
                    showRefreshAnimation(item,mNodeListView);
                    mHandler.sendEmptyMessage(MSG_REFRESH);
                }
                break;

            default:
                break;
        }
        return true;
    }
    
    @Override
    public void onDestroy() {
        mHandler.removeMessages(MSG_GET_ONEIMAGE_END);
        mHandler.removeMessages(MSG_GETNODES_END);
        mHandler.removeMessages(MSG_NOTIFY);
        mHandler.removeMessages(MSG_REFRESH);
        super.onDestroy();
    }
    
    public void setClickListener(FSingleCtrBtnClickListener listen){
        mFSingleCtrBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FSingleCtrBtnClickListener {  
        void onFSingleCtrBtnClick();  
    }
    
    public class OpenDiaFrag extends DialogFragment{
        private Spinner spinner;
        private int selectedTime = 5;
        String deviceNos;
        private String ID;
        private String controlnodeuniqueid;
        
        public OpenDiaFrag(String deviceNos,String id,String controlnodeuniqueid){
            this.deviceNos = deviceNos;
            this.ID = id;
            this.controlnodeuniqueid = controlnodeuniqueid;
        }
        
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.dia_ctr_open, container);  
//            getDialog().setTitle("Hello");  
//            
//            return view;  
//        }
        
        private void initSpinner(View rootView){
            spinner = (Spinner) rootView.findViewById(R.id.d_c_o_spinner);
            String[] spinnerDatas = {"5分钟","10分钟","15分钟","20分钟","25分钟","30分钟","35分钟","40分钟","45分钟","50分钟","55分钟"}; 
            ArrayAdapter<String> arr_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,spinnerDatas);
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arr_adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedTime = (1 + position) * 5;//minites
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
            LayoutInflater inflater = getActivity().getLayoutInflater();  
            View view = inflater.inflate(R.layout.dia_ctr_open, null);
            builder.setView(view)
                    .setTitle(R.string.opennow)
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final String userkey = JuRongActivity.mUniqueID;
                            Log.v(tag, "---onClickPositive---" + userkey + " "+selectedTime + " " +controlnodeuniqueid + " " + deviceNos);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendShowMyDlg("正在设置");
                                    ID = getExistIDInControlActionInfo(controlnodeuniqueid, deviceNos);
                                    boolean ret = new AjaxGetNodesDataByUserkey().SaveControlAdd(userkey, ""+selectedTime, controlnodeuniqueid, 1, deviceNos, ID);
                                    sendDismissDlg();
                                    mHandler.sendEmptyMessage(MSG_REFRESH);
                                }
                            }).start();
                        }
                    }).setNegativeButton(R.string.cancel, null);
            initSpinner(view);
            return builder.create();
        }
    }
}