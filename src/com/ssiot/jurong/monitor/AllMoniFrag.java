package com.ssiot.jurong.monitor;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ssiot.jurong.BaseFragment;
import com.ssiot.jurong.JuRongActivity;
import com.ssiot.jurong.R;
import com.ssiot.jurong.Utils;
import com.ssiot.jurong.monitor.MonitorListAdapter.DetailListener;
import com.ssiot.jurong.monitor.MonitorListAdapter.ShowAllListener;
import com.ssiot.jurong.monitor.MonitorListAdapter.ThumnailHolder;
import com.ssiot.remote.data.AjaxGetNodesDataByUserkey;
import com.ssiot.remote.data.model.view.NodeView2Model;

import java.util.ArrayList;
import java.util.List;

public class AllMoniFrag extends BaseFragment{
    public static final String tag = "AllMoniFragment";
    private FAllMoniBtnClickListener mFAllMoniBtnClickListener;
    TextView titleDaPeng;
    TextView titleDaTian;
    TextView titleyutang;
    ListView mNodeListView;
    MonitorListAdapter mNodeAdapter;
    View mBottom;
    public static final int[] DaPengNode = {326,327,328,329,330,331,332,333};
    public static final int[] DaTianNode = {339,347};
    public static final int[] YuTangNode = {341,342};
    public static final int[][] AllStaticNode = {DaPengNode,YuTangNode, DaTianNode};
    
    
    private String userKey = "";
    List<NodeView2Model> mNodes = new ArrayList<NodeView2Model>();
    ArrayList<NodeView2Model> mShowNodes = new ArrayList<NodeView2Model>();
    private DetailListener mDetailListener;
    private int currentIndex = 1;
    FragmentManager fragmentManager;
    
    private static final int MSG_GETNODES_END = 1;
    public static final int MSG_GET_ONEIMAGE_END = 2;
    private static final int MSG_REFRESH = 3;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (!isVisible()){
                Log.e(tag, "----fragment is not visible----!!!!" + msg.what);
                return;
            }
            Log.v(tag, "---handleMessage----" + msg.what + " allsize:" + mNodes.size());
            switch (msg.what) {
                case MSG_GETNODES_END:
                    if (JuRongActivity.isFake){
                        mNodeListView.setAdapter(new FakeAdapter(getActivity(), null));
                        return;
                    }
                    if (null != mNodes && mNodes.size() > 0 && null != mNodeAdapter){
                        Log.v(tag, "----------refresh node list");
                        mShowNodes = checkOutNodes(mNodes, currentIndex);
                        mNodeAdapter = new MonitorListAdapter(getActivity(), mShowNodes,mShowAllListener,mDetailListener,mHandler);
                        mNodeListView.setAdapter(mNodeAdapter);
                        mNodeAdapter.notifyDataSetChanged();
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
                    new GetAllMoniNodeThread().start();
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
        userKey = getArguments().getString("uniqueid");
        fragmentManager = getChildFragmentManager();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((JuRongActivity) getActivity()).setActionTitle("实时监测");
        View v = inflater.inflate(R.layout.frag_all_moni, container, false);
        titleDaPeng = (TextView) v.findViewById(R.id.area_dapeng);
        titleDaTian = (TextView) v.findViewById(R.id.area_datian);
        titleyutang = (TextView) v.findViewById(R.id.area_water);
        mNodeListView = (ListView) v.findViewById(R.id.moni_seperate_list);
        mBottom = (View) v.findViewById(R.id.mybottom);
        setClickListen();
        setListShow(1);
        
        mNodeAdapter = new MonitorListAdapter(getActivity(), mNodes,null,null,mHandler);
        mNodeListView.setAdapter(mNodeAdapter);
        mNodeAdapter.notifyDataSetChanged();
        if (!Utils.isNetworkConnected(getActivity())){
            Toast.makeText(getActivity(), R.string.please_check_net, Toast.LENGTH_LONG).show();
        } else {
            new GetAllMoniNodeThread().start();
        }
        return v;
    }
    
    private void setListShow(int index){
        if (null != titleDaPeng && null != titleDaTian && null != titleyutang && null != mNodeListView){
            RelativeLayout.LayoutParams rl2 = (RelativeLayout.LayoutParams) titleDaTian.getLayoutParams();
            RelativeLayout.LayoutParams rl3 = (RelativeLayout.LayoutParams) titleyutang.getLayoutParams();
            RelativeLayout.LayoutParams rl4 = (RelativeLayout.LayoutParams) mNodeListView.getLayoutParams();
            switch (index) {
                case 1:
                    rl2.addRule(RelativeLayout.ABOVE, R.id.mybottom);
                    rl2.addRule(RelativeLayout.BELOW);
                    rl3.addRule(RelativeLayout.ABOVE, R.id.area_datian);
                    rl3.addRule(RelativeLayout.BELOW);
                    rl4.addRule(RelativeLayout.BELOW, R.id.area_dapeng);
                    rl4.addRule(RelativeLayout.ABOVE, R.id.area_water);
                    currentIndex = 1;
                    break;
                case 2:
                    rl2.addRule(RelativeLayout.ABOVE, R.id.mybottom);
                    rl2.addRule(RelativeLayout.BELOW);
                    rl3.addRule(RelativeLayout.BELOW, R.id.area_dapeng);
                    rl3.addRule(RelativeLayout.ABOVE);
                    rl4.addRule(RelativeLayout.BELOW, R.id.area_water);
                    rl4.addRule(RelativeLayout.ABOVE, R.id.area_datian);
                    currentIndex =2;
                    break;
                case 3:
                    rl3.addRule(RelativeLayout.BELOW, R.id.area_dapeng);
                    rl3.addRule(RelativeLayout.ABOVE);
                    rl2.addRule(RelativeLayout.BELOW, R.id.area_water);
                    rl2.addRule(RelativeLayout.ABOVE);
                    rl4.addRule(RelativeLayout.BELOW, R.id.area_datian);
                    rl4.addRule(RelativeLayout.ABOVE, R.id.mybottom);
                    currentIndex = 3;
                    break;

                default:
                    break;
            }
            titleDaTian.setLayoutParams(rl2);
            titleyutang.setLayoutParams(rl3);
            mNodeListView.setLayoutParams(rl4);
        }
    }
    
    private void setClickListen() {
        View.OnClickListener cli = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.area_dapeng:
                        setListShow(1);
                        break;
                    case R.id.area_water:
                        setListShow(2);
                        break;
                    case R.id.area_datian:
                        setListShow(3);
                        break;

                    default:
                        break;
                }
                if (null != mNodes && mNodes.size() > 0 && null != mNodeAdapter){
                    Log.v(tag, "----------refresh node list");
                    mShowNodes = checkOutNodes(mNodes, currentIndex);
                    mNodeAdapter = new MonitorListAdapter(getActivity(), mShowNodes,mShowAllListener,mDetailListener,mHandler);
                    mNodeListView.setAdapter(mNodeAdapter);
                    mNodeAdapter.notifyDataSetChanged();
                } else {
                    Log.e(tag, "----------MSG_GETNODES_END-error!!!!!!!!!!!!!!");
                }
            }
        };
        titleDaPeng.setOnClickListener(cli);
        titleDaTian.setOnClickListener(cli);
        titleyutang.setOnClickListener(cli);
    }
    
    private ArrayList<NodeView2Model> checkOutNodes(List<NodeView2Model> nodes, int currentHolderIndex){
        ArrayList<NodeView2Model> datas = new ArrayList<NodeView2Model>();
        for(NodeView2Model m : nodes){
            if (isIn(AllStaticNode[currentHolderIndex - 1], m._nodeno)){
                datas.add(m);
            }
       }
       return datas;
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
    
    ShowAllListener mShowAllListener = new ShowAllListener() {

        @Override
        public void onShowAll(int index) {
            // TODO Auto-generated method stub
            mShowNodes.get(index).showAll = !mNodes.get(index).showAll;
            mNodeAdapter.notifyDataSetChanged();
        }
    };
    
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
                    showRefreshAnimation(item,titleDaPeng);
                    mHandler.sendEmptyMessage(MSG_REFRESH);
                }
                break;

            default:
                break;
        }
        return true;
    }
    
    public void setDetailListener(DetailListener lis){
        mDetailListener = lis;
    }
    
    public void setClickListener(FAllMoniBtnClickListener listen){
        mFAllMoniBtnClickListener = listen;
    }
    
    //回调接口，留给activity使用
    public interface FAllMoniBtnClickListener {  
        void onFAllMoniBtnClick();  
    }
    
    private class GetAllMoniNodeThread extends Thread{
        @Override
        public synchronized void run() {
            
            if (JuRongActivity.isFake){
                mHandler.sendEmptyMessage(MSG_GETNODES_END);
            } else {
                sendShowMyDlg("正在查询");
                mNodes = new AjaxGetNodesDataByUserkey().GetAllNodesDataByUserkey(userKey);
                sendDismissDlg();
                mHandler.sendEmptyMessage(MSG_GETNODES_END);
            }
        }
    }
}