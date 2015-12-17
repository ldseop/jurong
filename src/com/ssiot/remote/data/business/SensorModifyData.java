package com.ssiot.remote.data.business;

import com.ssiot.remote.data.model.SensorModifyDataModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SensorModifyData{
    
    
    
    
    private List<SensorModifyDataModel> DataTableToList(ResultSet c){
        List<SensorModifyDataModel> modelList = new ArrayList<SensorModifyDataModel>();
//        int rowsCount = dt.Rows.Count;
        SensorModifyDataModel model = new SensorModifyDataModel();
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
    }
    
    private SensorModifyDataModel DataRowToModel(ResultSet s){
        SensorModifyDataModel m = new SensorModifyDataModel();
        try {
            m._id = Integer.parseInt(s.getString("ID"));
            m._sensorno = Integer.parseInt(s.getString("SensorNo"));
            m._type = Integer.parseInt(s.getString("Type"));
            m._other = Integer.parseInt(s.getString("Other"));
            m._value = Float.parseFloat(s.getString("Value"));
            m._remark = s.getString("Remark");
            return m;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}