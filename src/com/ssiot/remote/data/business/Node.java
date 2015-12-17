package com.ssiot.remote.data.business;

import com.ssiot.remote.data.DbHelperSQL;
import com.ssiot.remote.data.model.NodeModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Node{
    private static final String tag = "com.ssiot.remote.data.business.Node";
    
    public Node(){
        
    }
    
//    public List<NodeModel> GetNodeList(String strwhere){
//        ResultSet rs = GetList(strwhere);
//        List<NodeModel> nodelist = new ArrayList<NodeModel>();
//        try {
//            while(rs.next()){
//                nodelist.add(FromDataRow(rs));
//            }
//            return nodelist;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    
    public List<NodeModel> GetModelList(String strWhere){
//        string cacheKey =( "NodeGetModelList+" + strWhere).GetHashCode().ToString();
//        object objModel = Angel.Common.Web.DataCache.GetCache(cacheKey);
        List<NodeModel> objModel = null;
        if (objModel == null){
            StringBuilder strSql = new StringBuilder();
//            strSql.append("select NodeID,UniqueID,NodeNo,NodeCategoryNo,ProductID,GatewayNo,AreaID,Longitude,Latitude," +
//                    "Location,Image,OnlineType,Color,Expression,Remark ");//jingbo 不知为何写这么复杂，测试发现耗时多
            strSql.append("select * ");
            strSql.append(" FROM Node ");
            if (strWhere.trim() != ""){
                strSql.append(" where " + strWhere);
            }
            ResultSet rs = DbHelperSQL.Query(strSql.toString());
            if (null != rs){
                objModel= DataTableToList(rs);
            }
        }
        return objModel;
    }
    
    public List<NodeModel> GetModelListByAreaIDs(String areaids){
        String strWhere = "";
        if (areaids == "0"){
            strWhere = "1=1";
        } else {
            strWhere = "AreaID in (" + areaids + ")";
        }
        return GetModelList(strWhere);
    }
    
    private List<NodeModel> DataTableToList(ResultSet c){
        List<NodeModel> modelList = new ArrayList<NodeModel>();
//        int rowsCount = dt.Rows.Count;
        NodeModel model = new NodeModel();
        try {
            while(c.next()){
                model = DataRowToModel(c);
                if (model != null){
                    modelList.add(model);
                }
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelList;
//        if (rowsCount > 0){
//            Angel.IOT2.Model.Node model;
//            for (int n = 0; n < rowsCount; n++){
//                for (int n = 0; n < rowsCount; n++){
//                    if (model != null){
//                        modelList.Add(model);
//                    }
//                }
//            }
//        }
//        return modelList;
    }
    
    private NodeModel DataRowToModel(ResultSet s){
        NodeModel m = new NodeModel();
        try {
            m._nodeid = Integer.parseInt(s.getString("NodeID"));
            m._uniqueid = s.getString("UniqueID");
            m._nodeno = Integer.parseInt(s.getString("NodeNo"));
            m._nodecategoryno = Integer.parseInt(s.getString("NodeCategoryNo"));
            m._productid = Integer.parseInt(s.getString("ProductID"));
            m._gatewayno = Integer.parseInt(s.getString("GatewayNo"));
            m._areaid = Integer.parseInt(s.getString("AreaID"));
            //
            
            m._onlinetype = s.getString("OnlineType");
            if (null != m._onlinetype){
                m._onlinetype.trim();
            }
            m._location = s.getString("Location");
            m._image = s.getString("Image");
            return m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //----------------------------------------
//    public DataSet GetList(String strWhere){
//        StringBuilder strSql = new StringBuilder();
//        strSql.append("select NodeID,UniqueID,NodeNo,NodeCategoryNo,ProductID,GatewayNo,AreaID,Longitude,Latitude," +
//        		"Location,Image,OnlineType,Color,Expression,Remark ");
//        strSql.append(" FROM Node ");
//        if (strWhere.trim() != ""){
//            strSql.append(" where " + strWhere);
//        }
//        return DbHelperSQL.Query(strSql.toString());
//    }
    
}