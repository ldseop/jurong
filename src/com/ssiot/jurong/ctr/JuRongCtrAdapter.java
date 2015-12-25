package com.ssiot.jurong.ctr;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssiot.jurong.R;
import com.ssiot.jurong.view.CtrUIView;
import com.ssiot.remote.data.model.view.ControlActionViewModel;
import com.ssiot.remote.data.model.view.ControlDeviceView3Model;
import com.ssiot.remote.data.model.view.CtrNodeViewJuRongModel;

import java.util.ArrayList;
import java.util.List;

public class JuRongCtrAdapter extends BaseAdapter{
    private static final String tag = "JuRongCtrAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<CtrNodeViewJuRongModel> mDatas;
    private DeviceBtnListener mDeviceBtnListener;
    private Handler uiHandler;
    
    public JuRongCtrAdapter(Context c,List<CtrNodeViewJuRongModel> lis,Handler h,DeviceBtnListener d){
        mContext = c;
        mInflater = LayoutInflater.from(c);
        mDatas = lis;
        mDeviceBtnListener = d;
        uiHandler = h;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.ctr_node_jr_view, null);
            holder.ctr_title = (TextView) convertView.findViewById(R.id.ctr_jr_title);
            holder.ctr_grid = (GridView) convertView.findViewById(R.id.ctr_jr_grid);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CtrNodeViewJuRongModel m = mDatas.get(position);
        if (TextUtils.isEmpty(m._ControlNodeViewModel._nodename)){
            holder.ctr_title.setText("节点名");
        } else {
            holder.ctr_title.setText(m._ControlNodeViewModel._nodename);
        }
        
        //TODO grid
        CtrDeviceAdapter gridAdapter = new CtrDeviceAdapter(mContext, m);
        holder.ctr_grid.setAdapter(gridAdapter);
        //itemclickListener放在此处无效，可能是listview gridview嵌套导致
        return convertView;
    }
    
    private class ViewHolder{
        TextView ctr_title;
        ImageView ctr_status;
        GridView ctr_grid;
    }
    
    //-----------------------------------------------------------------
    //控制设备的gridview
    public class CtrDeviceAdapter extends ArrayAdapter<ControlDeviceView3Model>{
        private LayoutInflater inflater;
        List<ControlDeviceView3Model> devices;
        private CtrNodeViewJuRongModel superModel;
        private int nodeno = 0;
        private final int[] draws = {R.drawable.ctr_air,R.drawable.ctr_shadow,R.drawable.ctr_oxygen,R.drawable.ctr_water,R.drawable.ctr_feed};
        
        public CtrDeviceAdapter(Context context, CtrNodeViewJuRongModel superModel){
            super(context, 0, superModel._detailList);
            nodeno = superModel._ControlNodeViewModel._nodeno;
            if (isIn(AllCtrFrag.DaPengNode, nodeno)){//大棚的控制节点
                devices = superModel._detailList;//TODO
                for (int i = 0 ; i < superModel._detailList.size(); i ++){
                    int k = superModel._detailList.get(i).DeviceNo;
                    if (k != 1 && k != 2 && k != 3 && k != 4){//TODO 具体的设备 问刘建群 TODO icon
//                        superModel._detailList.remove(i);
//                        i --;//添加8个莲蓬头 2015-12-25
                    }
                }
                devices = superModel._detailList;
            } else if (isIn(AllCtrFrag.YuTangNode, nodeno)){//鱼塘的控制节点
                devices = superModel._detailList;
            } else {
                devices = superModel._detailList;
            }
            inflater = LayoutInflater.from(context);
            this.superModel = superModel;
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
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView){
                convertView = inflater.inflate(R.layout.device, parent, false);
            }
            ControlDeviceView3Model model = devices.get(position);
            final CtrUIView view = (CtrUIView) convertView;
            
            view.setImageResource(draws[position % 5]);
            if (nodeno == 343){//大棚
                switch (model.DeviceNo) {
                    case 1:
                        view.setImageResource(R.drawable.ctr_air);
                        view.setText("风机1");
                        break;
                    case 2:
                        view.setImageResource(R.drawable.ctr_air);
                        view.setText("风机2");
                        break;
                    case 3:
                        view.setImageResource(R.drawable.ctr_water);
                        view.setText("湿帘控制");
                        break;
                    case 4:
                        view.setImageResource(R.drawable.ctr_shadow);
                        view.setText("遮阳网控制");
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        view.setImageResource(R.drawable.ctrl_shower);
                        view.setText("莲蓬头" + (model.DeviceNo -4));
                        break;
                    default:
                        break;
                }
            } else if (nodeno == 341 || nodeno == 342){//鱼塘
                switch (model.DeviceNo) {
                    case 1:
                        view.setImageResource(R.drawable.ctr_oxygen);
                        view.setText("增氧机");
                        break;
                    case 2:
                        view.setImageResource(R.drawable.ctr_feed);
                        view.setText("投食机");
                        break;
                    default:
                        break;
                }
            }
            
//            if (TextUtils.isEmpty(model.DeviceName)){
//                view.setText("设备名");
//            } else {
//                view.setText(model.DeviceName);
//            }
            final boolean checkStatus = isOn(model);
            view.setChecked(checkStatus);
            if (checkStatus && leftOnTime(model) > 0){
                uiHandler.sendEmptyMessageDelayed(AllCtrFrag.MSG_NOTIFY, leftOnTime(model) * 1000);
                Log.v(tag, "-------leftontime:" + leftOnTime(model));
            }
            final int positionFinal = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDeviceBtnListener){
                        mDeviceBtnListener.onDeviceBtnClick(superModel, positionFinal,checkStatus);
                    }
//                    view.setChecked(!view.isChecked());
                }
            });
            return convertView;
        }
        
        private boolean isOn(ControlDeviceView3Model m){
            for (ControlActionViewModel action : m.ActionList){
                if (action.ControlType == 1){//立即开启的
                    int i = 0;
                    try {
                        i = Integer.parseInt(action.ControlCondition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (i > 0 && ((action.OperateTime.getTime() + i * 60 * 1000) - System.currentTimeMillis()) > 0){
                        return true;
                    }
                }
            }
            return false;
        }
        
        private int leftOnTime(ControlDeviceView3Model m){
            for (ControlActionViewModel action : m.ActionList){
                if (action.ControlType == 1){//立即开启的
                    int i = 0;
                    try {
                        i = Integer.parseInt(action.ControlCondition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long leftTimeMills = (action.OperateTime.getTime() + i * 60 * 1000) - System.currentTimeMillis();
                    if (i > 0 && (leftTimeMills) > 0){
                        return (int) leftTimeMills/1000 + 1;
                    }
                }
            }
            return 0;
        }
    }
    
    
    
    public interface DeviceBtnListener{
        public void onDeviceBtnClick(CtrNodeViewJuRongModel superModel, int position, boolean lastStatus);
    }
    
}
