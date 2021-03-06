package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * INetSDK.QueryDevInfo , NET_QUERY_RECENCY_JNNCTION_CAR_INFO port output parameter
 * \else
 * INetSDK.QueryDevInfo , NET_QUERY_RECENCY_JNNCTION_CAR_INFO接口输出参数
 * \endif
 */
public class NET_OUT_GET_RECENCY_JUNCTION_CAR_INFO implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * traffic vehicle info
	 * \else
	 * 交通车辆信息
	 * \endif
	 */
	public DEV_EVENT_TRAFFIC_TRAFFICCAR_INFO stTrafficCar = new DEV_EVENT_TRAFFIC_TRAFFICCAR_INFO();      
}
