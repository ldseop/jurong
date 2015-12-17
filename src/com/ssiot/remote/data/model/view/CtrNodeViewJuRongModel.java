package com.ssiot.remote.data.model.view;

import java.util.List;

//这个类是for jurong 包含每个控制节点下的控制设备
public class CtrNodeViewJuRongModel{
    public ControlNodeViewModel _ControlNodeViewModel;
    public List<ControlDeviceView3Model> _detailList;
    
    public CtrNodeViewJuRongModel(ControlNodeViewModel c, List<ControlDeviceView3Model> lis){
        _ControlNodeViewModel = c;
        _detailList = lis;
    }
}