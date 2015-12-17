package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * tour enable configuration
 * \else
 * 轮巡使能配置
 * \endif
 */
public class SDKDEV_TOUR_ENABLE_CFG implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * enable
	 * \else
	 * 轮巡使能
	 * \endif
	 */
	public boolean                bEnable; 
	
	/**
	 * \if ENGLISH_LANG
	 * is touring or not(read only). bStart=FALSE when tour is able but doesn't set
	 * \else
	 * 是否正在轮巡(只读). 轮巡使能打开但没有配置轮巡输入源时, bStart=FALSE
	 * \endif
	 */
	public boolean                bStart; 
}
