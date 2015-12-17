package com.ssiot.remote.data;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;

import com.ssiot.jurong.Utils;
import com.ssiot.remote.data.business.ControlActionInfo;
import com.ssiot.remote.data.business.ControlDevice;
import com.ssiot.remote.data.business.ControlLog;
import com.ssiot.remote.data.business.Sensor;
import com.ssiot.remote.data.model.ControlActionInfoModel;
import com.ssiot.remote.data.model.ControlDeviceModel;
import com.ssiot.remote.data.model.ControlLogModel;
import com.ssiot.remote.data.model.ControlNodeModel;
import com.ssiot.remote.data.model.NodeModel;
import com.ssiot.remote.data.model.SensorModel;
import com.ssiot.remote.data.model.view.ControlActionViewModel;
import com.ssiot.remote.data.model.view.ControlDeviceView2Model;
import com.ssiot.remote.data.model.view.ControlDeviceView3Model;
import com.ssiot.remote.data.model.view.ControlDeviceViewModel;
import com.ssiot.remote.data.model.view.ControlNodeViewModel;
import com.ssiot.remote.data.model.view.NodeView2Model;
import com.ssiot.remote.data.model.view.NodeViewModel;
import com.ssiot.remote.data.model.view.SensorViewModel;

import java.sql.Timestamp;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AjaxGetNodesDataByUserkey{
    private static final String tag = "AjaxGetNodesDataByUserkey";
    private Sensor sensorBll = new Sensor();
    private ControlActionInfo controlActionInfoBll = new ControlActionInfo();
    private ControlDevice controlDeviceBll = new ControlDevice();
    private ControlLog controllogbll = new ControlLog();
    
    public List<NodeView2Model> GetAllNodesDataByUserkey(String userkey){
        try {
            if (!TextUtils.isEmpty(userkey)){
                String areaids = DataAPI.GetAreaIDsByUserKey(userkey);
                List<NodeModel> node_list = DataAPI.GetNodeListByAreaIDs(areaids); 
              //根据节点对象列表获取由","分隔的节点编号字符串
                String nodenostr = DataAPI.GetNodeNoStringByNodeList(node_list);
                
                List<NodeView2Model> nodeView2_list = MobileAPI.GetNodesInfoAndDataByNodenos(nodenostr);
                
                
                return nodeView2_list;
                
            } else {
                Log.e(tag, "------------no userkey!!!!!!!!!!!!!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<NodeView2Model>();
    }
    
    public List<NodeViewModel> GetMapDataByUserkey(String userkey){
        try {
            String userAreaids = "";//(string)Session["Mobiles_Areaids"];
            if (!TextUtils.isEmpty(userAreaids)){
                
            } else {
                userAreaids = DataAPI.GetAreaIDsByUserKey(userkey);
            }
            String userIDs = DataAPI.GetSelfAndChildrenUserIDsByAreaIDs(userAreaids);
            Log.v(tag, "-----GetMapDataByUserkey------userAreaids:" + userAreaids + " userIDs:" + userIDs);
            List<NodeViewModel> nodestate = DataAPI.GetNodesStateByUserIDs(userIDs);
            
//            String result = JsonHelper.JsonSerializer(nodestate);
            return nodestate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(tag, "------GetMapDataByUserkey---null !!!!!!!!!!!!");
        return new ArrayList<NodeViewModel>();
    }
    
    public List<NodeView2Model> GetNodesDataByUserkeyAndType(String userkey, String nodeno, String grainsize){//详细数据 TODO
        String beginTime = "";
//        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = formatter.format(now);
        try {
            if (TextUtils.isEmpty(userkey) || TextUtils.isEmpty(grainsize) || TextUtils.isEmpty(nodeno)) {
                return null;
            }
            else {
                if ("十分钟".equals(grainsize)){
                    grainsize = "10分钟";
                    beginTime = buildTime(-2 * 3600 * 1000);
                } else if ("小时".equals(grainsize)){
                    grainsize = "逐小时"; beginTime = buildTime(-24 * 3600 * 1000);
                } else if ("天".equals(grainsize)){
                    grainsize = "逐日"; beginTime = buildTime(-15 * 24 * 3600 * 1000);
                } else if ("月".equals(grainsize)){
                    grainsize = "逐月"; beginTime = buildTime(-365 * 24 * 3600 * 1000);
                } else if ("年".equals(grainsize)){
                    grainsize = "逐年"; beginTime = buildTime(-10 * 365 * 24 * 3600 * 1000);
                } else {
                    grainsize = "逐小时"; beginTime = buildTime(-24 * 3600 * 1000);
                }
                //获取节点数据
                List<NodeView2Model> nodeView2_list = MobileAPI.GetNodesDataByNodenosAndQueryType(nodeno, grainsize, "平均值", beginTime, endTime);
                return nodeView2_list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //根据Usekey获取控制节点的扩展信息
    public List<ControlNodeViewModel> GetControlNodesByUserkey(String userkey){
        if (TextUtils.isEmpty(userkey)) {
            return null;
        }
        try
        {
            String areaIds = "";
//            if (Session["Mobiles_Areaids"] != null)//TODO
//            {
//                areaIds = Session["Mobiles_Areaids"].ToString();
//            } else {
                areaIds = DataAPI.GetAreaIDsByUserKey(userkey);
//            }
            List<ControlNodeViewModel> controlNodes_list = MobileAPI.GetControlNodesInfoByAreaIds(areaIds);
            return controlNodes_list;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    
    //this is in ControlController.cs web端使用的接口
    public List<ControlNodeModel> ControlNodeList(String nodeno,String userIDs,int userrole){//(String id, int pageindex = 1)
        String where = "1=1 ";
        if (!TextUtils.isEmpty(nodeno)){//正常是空的表示查找所有
            where += "AND NodeNo ='" + nodeno + "'";
        }

        if (userrole != 1) {//管理员权限
//            String userIDs = "";
//            userIDs = (string)System.Web.HttpContext.Current.Session["UserIDs"];
            where += "AND AreaID in(" + DataAPI.GetAreaIDByUserIDs(userIDs) + ")";
        }
//        ViewBag.UserRole = User.UserRole;
        List<ControlNodeModel> list = (List<ControlNodeModel>) DataAPI.mControlNodeService.GetModelList(where);
//        List<ControlNodeModel> list2 = list.OrderByDescending(c => c.NodeNo).ToList();
//        PagedList<ControlNodeModel> clist = list2.ToPagedList(pageindex, 8);
        return list;
    }
    
    public List<ControlDeviceView3Model> GetDeviceActionInfo(String controlNodeId,String controlUnique){//获取控制节点下的所有设备及其关联的控制动作
        try {
            if (!TextUtils.isEmpty(controlNodeId) && !TextUtils.isEmpty(controlUnique))
            {
                HashMap<String, String> sensor_dic = new HashMap<String, String>();
                List<SensorModel> sensorList = sensorBll.GetModelList("1=1");
                for(SensorModel sensor : sensorList){
                    sensor_dic.put(""+sensor._sensorno, sensor._shortname);
                }
                List<ControlDeviceViewModel> controlView_list = DataAPI.GetDeviceActionInfo(Integer.parseInt(controlNodeId), controlUnique);
                List<ControlDeviceView2Model> controlView2_list = new ArrayList<ControlDeviceView2Model>();
                
                for (ControlDeviceViewModel controlDeviceView : controlView_list){
                    ControlDeviceView2Model controlView2 = new ControlDeviceView2Model();
                    controlView2.DeviceID = controlDeviceView.DeviceID;
                    controlView2.ControlNodeID = controlDeviceView.ControlNodeID;
                    controlView2.DeviceNo = controlDeviceView.DeviceNo;
                    controlView2.DeviceName = controlDeviceView.DeviceName;
                    if (controlDeviceView.RunTime > 0 && !TextUtils.isEmpty(controlDeviceView.StartTime)){
                        int runTime_val = controlDeviceView.RunTime;//jingbo 貌似是秒数
                        Timestamp createTime = Timestamp.valueOf(controlDeviceView.StartTime);
                        if (System.currentTimeMillis() > (createTime.getTime() + controlDeviceView.RunTime * 1000)) {
                            controlView2.DeviceStateNow = "关闭";
                        } else {
                            controlView2.DeviceStateNow = "开启";
                        }
                    } else {
                        controlView2.DeviceStateNow = "关闭";
                    }
                    controlView2.ControlActionID = controlDeviceView.ControlActionID;
                    controlView2.ControlType = controlDeviceView.ControlType;
                    controlView2.CollectUniqueIDs = controlDeviceView.CollectUniqueIDs;

                    controlView2.ControlCondition = controlDeviceView.ControlCondition;
                    controlView2.OperateTime = controlDeviceView.OperateTime;
                    controlView2.Operate = controlDeviceView.Operate;
                    
                  //匹配触发条件 把触发条件名称改为看得懂的
                    if (controlView2.ControlType == 6) {
                        String condition = controlView2.ControlCondition;
                        condition = jingboRegex(condition, sensor_dic);
                        controlView2.ControlCondition = condition;
                        controlView2_list.add(controlView2);
                    } else {
                        //不是触发
                        controlView2_list.add(controlView2);
                    }
                }
                List<ControlDeviceView3Model> controlView3_list = new ArrayList<ControlDeviceView3Model>();
                if (controlView2_list.size() > 0){
                    for (int i = 0; i < controlView2_list.size(); i ++){
                        ControlDeviceView2Model view2 = controlView2_list.get(i);
                        ControlDeviceView3Model view3 = getExistCDV3Model(controlView3_list, view2.DeviceID);
                        boolean isNewCreated = false;
                        if (view3 == null){//这个device不存在，就新添加 例如：设备2
                            view3 = new ControlDeviceView3Model();
                            view3.DeviceID = view2.DeviceID;
                            view3.DeviceName = view2.DeviceName;
                            view3.DeviceNo = view2.DeviceNo;
                            view3.ControlNodeID = view2.ControlNodeID;
                            view3.DeviceStateNow = view2.DeviceStateNow;
                            isNewCreated = true;
                        }
                        
                        if (view3.ActionList == null){
                            view3.ActionList = new ArrayList<ControlActionViewModel>();
                        }
                        
                        
                        if (3 == view2.ControlType){//数据表中定时规则可能一行多条
                            ArrayList<String> multiTimingConditions = Utils.parseJSON_MultiTiming(view2.ControlCondition);
                            for (String condi : multiTimingConditions){
                                ControlActionViewModel actionView = new ControlActionViewModel();
                                actionView.ControlActionID = view2.ControlActionID;
                                actionView.CollectUniqueIDs = view2.CollectUniqueIDs;
                                actionView.ControlCondition = condi;
                                actionView.ControlType = view2.ControlType;
                                actionView.Operate = view2.Operate;
                                actionView.OperateTime = view2.OperateTime;
                                actionView.StateNow = view2.StateNow;   
                                view3.ActionList.add(actionView);
                            }
                        } else if (view2.ControlType > 0){//jingbo把空的过滤掉；GetDeviceActionInfo中早就把空的添加了，也是有必要的
                            ControlActionViewModel actionView = new ControlActionViewModel();
                            actionView.ControlActionID = view2.ControlActionID;
                            actionView.CollectUniqueIDs = view2.CollectUniqueIDs;
                            actionView.ControlCondition = view2.ControlCondition;
                            actionView.ControlType = view2.ControlType;
                            actionView.Operate = view2.Operate;
                            actionView.OperateTime = view2.OperateTime;
                            actionView.StateNow = view2.StateNow;
                            view3.ActionList.add(actionView);
                        }
                        
                        if (isNewCreated){
                            controlView3_list.add(view3);
                        }
                    }
                }
                reOrder(controlView3_list);//by jingbo
                return controlView3_list;
                //TODO
            } else {
                Log.e(tag, "--------string is null:"+controlNodeId + controlUnique);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //根据节点标识（UniqueID）和设备编号（DeviceNo）控制设备 jingbo 代码中好像只用到了close部分
    public String ControlDevice(String uniqueID,String deviceNo,String openTime, String isOpen){
        if ("open".equalsIgnoreCase(isOpen)){
            if (!TextUtils.isEmpty(uniqueID)){
                if (!TextUtils.isEmpty(deviceNo)){
                    String rtnStr = DataAPI.SetTimeControl(uniqueID, deviceNo, openTime);
                    if (rtnStr.contains("done")){
                        return "true";
                    } else {
                        return "false";
                    }
                } else {//deviceNo为空，表示全部打开
                    String returnInfo = "";
                    List<ControlDeviceModel> controlDevice_list = controlDeviceBll.GetModelList("ControlNodeID in (SELECT ID FROM ControlNode WHERE UniqueID='" + uniqueID + "')");
                    if (controlDevice_list != null && controlDevice_list.size() > 0)  {
                        for (ControlDeviceModel controlDevice : controlDevice_list){//TODO
                            String rtnStr = DataAPI.SetTimeControl(uniqueID, ""+controlDevice._deviceno, openTime);//log ？？写入数据库
                            if (rtnStr.contains("done")) {
                                returnInfo += controlDevice._deviceno + "号设备:true,";
                            } else {
                                returnInfo += controlDevice._deviceno + "号设备:false,";
                                Log.e(tag, "-------returnInfo--!!!!!!!!!!");
                            }
                        }
                    }
                    if (returnInfo.contains("false")) {
                        returnInfo = "false";
                    } else {
                        returnInfo = "true";
                    }
                    return returnInfo;
                }
            } else {
                Log.e(tag, "-------uniqueid is null !!!!!");
                return "";
            }
        } else if ("close".equalsIgnoreCase(isOpen)){
            Log.v(tag, "-------------" + uniqueID + " " + deviceNo);
            if (!TextUtils.isEmpty(uniqueID)){
                if (!TextUtils.isEmpty(deviceNo)){//单个关闭
                    Log.v(tag, "单个关闭");
                    String rtnStr = DataAPI.ControlControlCloseNow(uniqueID, deviceNo);//ControlLog表
                    List<ControlActionInfoModel> actioninfo = controlActionInfoBll
                            .GetModelList(" UniqueID='" + uniqueID + "' and DeviceNo=" + deviceNo + " and ControlType=1");
                    if (actioninfo != null && actioninfo.size() > 0){
                        for (ControlActionInfoModel opens : actioninfo){
                            try {
                                controlActionInfoBll.Delete(opens._id);//ControlActionInfo表
                            } catch (Exception e) {
                                e.printStackTrace();
                                return "false";
                            }
                        }
                    }
                    
                    if (rtnStr.contains("done")){
                        return "true";
                    } else {
                        return "false";
                    }
                } else {//全部关闭？？
                    Log.v(tag, "全部关闭");
                    String returnInfo = "";
                    List<ControlDeviceModel> controlDevice_list = controlDeviceBll
                            .GetModelList("ControlNodeID in (SELECT ID FROM ControlNode WHERE UniqueID='" + uniqueID + "')");
                    if (controlDevice_list != null && controlDevice_list.size() > 0){
                        for (ControlDeviceModel controlDevice : controlDevice_list){
                            String rtnStr = DataAPI.ControlControlCloseNow(uniqueID, ""+controlDevice._deviceno);
                            if (rtnStr.contains("done")){
                                returnInfo += controlDevice._deviceno + "号设备:true,";
                            } else {
                                returnInfo += controlDevice._deviceno + "号设备:false,";
                            }
                            List<ControlActionInfoModel> actioninfo = controlActionInfoBll
                                    .GetModelList(" UniqueID='" + uniqueID + "' and DeviceNo=" + controlDevice._deviceno + " and ControlType=1" );
                            if (actioninfo != null && actioninfo.size() > 0){
                                for (ControlActionInfoModel opens : actioninfo){
                                    try {
                                        controlActionInfoBll.Delete(opens._id);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        returnInfo = "false";
                                    }
                                }
                            }
                        }
                    }
                    if (returnInfo.contains("false")){
                        returnInfo = "false";
                    } else {
                        returnInfo = "true";    
                    }
                    return returnInfo;
                }
            } else {
                Log.e(tag, "!!!!!!!!!!!!!!!");
                return "";
            }
        }
        return "";
    }
    
    public boolean SaveControlAdd(String userkey, String timeCondition,String UniqueID, int controlTypes,String DeviceNo,String updateid){
        String names = "";
        try {
            if (!TextUtils.isEmpty(timeCondition)){//必须有规则字符串
                ControlActionInfoModel controlActionInfo = new ControlActionInfoModel();
                if (TextUtils.isEmpty(DeviceNo)){//全部立即开启
                    List<ControlDeviceModel> controlDevice_list = controlDeviceBll
                            .GetModelList("ControlNodeID in (SELECT ID FROM ControlNode WHERE UniqueID='" + UniqueID + "')");
                    if (controlDevice_list != null && controlDevice_list.size() > 0){
                        for(ControlDeviceModel controlDevice : controlDevice_list){
                          //1.controlDevice.DeviceNo  判断设备是否已经有立即开启数据  有就查找出要修改的ID
                            List<ControlActionInfoModel> actioninfo = controlActionInfoBll
                                    .GetModelList(" UniqueID='" + UniqueID + "' and DeviceNo=" + controlDevice._deviceno + " and ControlType=" + controlTypes);
                            if (actioninfo != null && actioninfo.size() > 0){
                                for (ControlActionInfoModel open : actioninfo){
                                    controlActionInfo = controlActionInfoBll.GetModel(open._id);
                                    controlActionInfo._areaid = Integer.parseInt(DataAPI.GetAreaIDsByUserKeys(userkey));
                                    names = "立即开启";
                                    controlActionInfo._controlname = names;
                                    controlActionInfo._uniqueid = UniqueID;
                                    controlActionInfo._deviceno = controlDevice._deviceno;
                                    controlActionInfo._controltype = controlTypes;
                                    controlActionInfo._controlcondition = timeCondition;
                                    controlActionInfo._operatetime = new Timestamp(System.currentTimeMillis());
                                    controlActionInfo._statenow = 0;
                                    controlActionInfo._operate = "打开";
                                    if (false == updateControlActionInfoAndAddLog(controlActionInfo)){
                                        return false;
                                    }
                                }
                            } else {//没有已存的数据
                                controlActionInfo._areaid = Integer.parseInt(DataAPI.GetAreaIDsByUserKeys(userkey));
                                if (controlTypes == 1) {
                                    names = "立即开启";
                                } else {
                                    return false;
                                }
                                controlActionInfo._controlname = names;// (string)Session["controlActionName"];
                                controlActionInfo._uniqueid = UniqueID;
                                controlActionInfo._deviceno = controlDevice._deviceno;
                                controlActionInfo._controltype = controlTypes;
                                controlActionInfo._controlcondition = timeCondition;
                                controlActionInfo._operatetime = new Timestamp(System.currentTimeMillis());
                                controlActionInfo._statenow = 0;
                                controlActionInfo._operate = "打开";
                                if (false == addControlActionInfoAndAddLog(controlActionInfo)){
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }//全部立即开启结束
                
                //修改
                if (!TextUtils.isEmpty(updateid)){
                    controlActionInfo = controlActionInfoBll.GetModel(Integer.parseInt(updateid));
                    controlActionInfo._areaid = Integer.parseInt(DataAPI.GetAreaIDsByUserKeys(userkey));
                    if (controlTypes == 1) {
                        names = "立即开启";
                    } else if (controlTypes == 3) {
                        names = "定时";
                    } else if (controlTypes == 5) {
                        names = "循环";
                    }
                    controlActionInfo._controlname = names;// (string)Session["controlActionName"];
                    controlActionInfo._uniqueid = UniqueID;
                    controlActionInfo._deviceno = Integer.parseInt(DeviceNo);
                    controlActionInfo._controltype = controlTypes;
                    controlActionInfo._controlcondition = timeCondition;
                    controlActionInfo._operatetime = new Timestamp(System.currentTimeMillis());
                    controlActionInfo._statenow = 0; 
                    controlActionInfo._operate = "打开";
                    if (false == updateControlActionInfoAndAddLog(controlActionInfo)){
                        return false;
                    }
                } else {//新增
                    if (!TextUtils.isEmpty(DeviceNo)){
                        if (DeviceNo.endsWith(",")){
                            DeviceNo = DeviceNo.substring(0, DeviceNo.length()-1);
                        }
                        String[] nos = DeviceNo.split(",");
                        for(String devicesel : nos){
                            controlActionInfo._areaid = Integer.parseInt(DataAPI.GetAreaIDsByUserKeys(userkey));
                            if (controlTypes == 1) {
                                names = "立即开启";
                            } else if (controlTypes == 3) {
                                names = "定时";
                            } else if (controlTypes == 5) {
                                names = "循环";
                            }
                            controlActionInfo._controlname = names;// (string)Session["controlActionName"];
                            controlActionInfo._uniqueid = UniqueID;
                            controlActionInfo._deviceno = Integer.parseInt(devicesel);
                            controlActionInfo._controltype = controlTypes;
                            controlActionInfo._controlcondition = timeCondition;
                            controlActionInfo._operatetime = new Timestamp(System.currentTimeMillis());
                            controlActionInfo._statenow = 0;
                            controlActionInfo._operate = "打开";
                            
                            if (false == addControlActionInfoAndAddLog(controlActionInfo)){
                                return false;
                            }
                        }
                        return true;
                        
                    } else {
                        Log.e(tag, "新增规则，但deviceno 为空");
                        return false;
                    }
                }
            } else {
                Log.e(tag, "timeCondition 字符串为空");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private boolean updateControlActionInfoAndAddLog(ControlActionInfoModel controlActionInfo){//ControlActionInfo表 ControlLog表
        if (controlActionInfoBll.Update(controlActionInfo)) {
            List<ControlLogModel> controlLog_list = DataAPI.ConvertControlActionInfoToControlLog(controlActionInfo);
            try {
                int rtnCount = controllogbll.AddManyCount(controlLog_list);
                if (rtnCount == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    
    private boolean addControlActionInfoAndAddLog(ControlActionInfoModel controlActionInfo){//ControlActionInfo表 ControlLog表
        if (controlActionInfoBll.Add(controlActionInfo) > 0) {
            List<ControlLogModel> controlLog_list = DataAPI.ConvertControlActionInfoToControlLog(controlActionInfo);
            try {
                int rtnCount = controllogbll.AddManyCount(controlLog_list);
                if (rtnCount == 0) {
                    return false;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
    
    public boolean DelControl(int id){//TODO
        try {
            ControlActionInfoModel controlActionInfo = controlActionInfoBll.GetModel(id);
            List<ControlLogModel> controlLog_list_before = DataAPI.ConvertControlActionInfoToControlLog(controlActionInfo);//
            if (controlActionInfoBll.Delete(id)) {
                if (controlLog_list_before.size() == 0) {
                    return true;
                }
                List<ControlLogModel> controlLog_list_after = new ArrayList<ControlLogModel>();
                for (ControlLogModel controlLog : controlLog_list_before) {
                    ControlLogModel controlLogModel = new ControlLogModel();
                    controlLogModel._logtype = 0; 
                    controlLogModel._uniqueid = controlLog._uniqueid;
                    controlLogModel._deviceno = controlLog._deviceno;
                    controlLogModel._starttype = controlLog._starttype;
                    controlLogModel._startvalue = 0; 
                    controlLogModel._runtime = 0; 
                    controlLogModel._endtype = 0; 
                    controlLogModel._endvalue = 0; 
                    controlLogModel._sendstate = controlLog._sendstate;
                    controlLogModel._createtime = controlLog._createtime;
                    controlLogModel._edittime = controlLog._edittime;
                    controlLogModel._timespan = controlLog._timespan;
                    controlLog_list_after.add(controlLogModel);
                }
                if (controllogbll.AddManyCount(controlLog_list_after) < 1)  {
                    return false;
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String jingboRegex(String condition,HashMap<String, String> sensor_dic){
        String str = condition;
        String reg = "\\d+-\\d";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(reg);
        java.util.regex.Matcher matcher=pattern.matcher(str);
        Log.v("AjaxGertddddddddddd", "--------------count:"+matcher.groupCount());
        while(matcher.find()){
            String sensorNo = matcher.group().split("-")[0];
            Log.v("d", "-------------------sensorNo:" +sensorNo);
            String sensorName = sensor_dic.get(sensorNo);
            str = str.replace(matcher.group(), sensorName + "-" + matcher.group().split("-")[1]);
        }
        Log.v("--------=-", str);
        return str;
    }
    
    private boolean deviceExists(List<ControlDeviceView3Model> cdv3m, int deviceId){
        if (cdv3m != null){
            for (int i = 0; i < cdv3m.size(); i ++){
                if (cdv3m.get(i).DeviceID == deviceId && deviceId > 0){
                    return true;
                }
            }
        }
        return false;
    }
    
    private ControlDeviceView3Model getExistCDV3Model(List<ControlDeviceView3Model> cdv3m, int deviceId){
        if (cdv3m != null){
            for (int i = 0; i < cdv3m.size(); i ++){
                if (cdv3m.get(i).DeviceID == deviceId && deviceId > 0){
                    return cdv3m.get(i);
                }
            }
        }
        return null;
    }

    
    /*
    //根据节点编号和传感器加载查询的数据，并以曲线图的形式返回
    public String GetMobileChart(String nodeno,String linetype,String type,String starttime,String sensorChannel){
        String query_nodeno = nodeno;
        try {
            if (!TextUtils.isEmpty(query_nodeno)){
                List<SensorViewModel> sensorView_list = DataAPI.GetSensorListByNodeNoString(query_nodeno);
                if (TextUtils.isEmpty(sensorChannel)){//根据sensorChannel是否为空来确定是否是第一次加载
                    if (sensorView_list.get(0)._channel > 0){
                        sensorChannel = sensorView_list.get(0)._shortname + sensorView_list.get(0)._channel;
                    } else {
                        sensorChannel = sensorView_list.get(0)._shortname;
                    }
                }
                String dataChart = new AjaxNodesData().AjaxGetJsonChart(query_nodeno, linetype, type, starttime, sensorChannel);
                
                //将NodeNo的Sensor+Channel传给客户端
                String sensorChannel_list = "";
                for (SensorViewModel sensorview : sensorView_list){
                    if (sensorview._channel > 0){
                        sensorChannel_list += sensorview._shortname + sensorview._channel + ";";
                    } else {
                        sensorChannel_list += sensorview._shortname + ";";
                    }
                }
                if (null != sensorChannel_list && sensorChannel_list.endsWith(",")){
                    sensorChannel_list = sensorChannel_list.substring(0, sensorChannel_list.length()-1);
                }
                return dataChart + "|" + sensorChannel_list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }*/
    
    private String buildTime(long seconds){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(System.currentTimeMillis() + seconds * 1000);
        return formatter.format(d);
        
    }
    
    private void reOrder(List<ControlDeviceView3Model> list){
//        final Collator sCollator = Collator.getInstance();
        Collections.sort(list, new Comparator<ControlDeviceView3Model>() {
            @Override
            public int compare(ControlDeviceView3Model lhs, ControlDeviceView3Model rhs) {
                // TODO Auto-generated method stub
                return lhs.DeviceNo - rhs.DeviceNo;
            }
            
        });
    }
}