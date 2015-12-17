package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * INetSDK.ControlDevice's param: SDK_CTRL_SET_BYPASS 
 * \else
 * INetSDK.ControlDevice接口的 SDK_CTRL_SET_BYPASS 命令参数
 * \endif
 */
public class NET_CTRL_SET_BYPASS implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * \if ENGLISH_LANG
	 * Login Device Password
	 * \else
	 * 登入设备的密码
	 * \endif
	 */
	public String				szDevPwd; 
	
	/**
	 * \if ENGLISH_LANG
	 * Channel Statue, NET_BYPASS_MODE
	 * \else
	 * 通道状态, NET_BYPASS_MODE
	 * \endif
	 */
	public int					emMode; 
	
	/**
	 * \if ENGLISH_LANG
	 * Local Alarm Input Channnel Number
	 * \else
	 * 本地报警输入通道个数
	 * \endif
	 */
	public int					nLocalCount; 
	
	/**
	 * \if ENGLISH_LANG
	 * Local Alarm Input Channnel ID 
	 * \else
	 * 本地报警输入通道号
	 * \endif
	 */
	public int					pnLocal[]; 
	
	/**
	 * \if ENGLISH_LANG
	 * Expand Module Alarm Input Channel Number
	 * \else
	 * 扩展模块报警输入通道个数
	 * \endif
	 */
	public int					nExtendedCount; 
	
	/**
	 * \if ENGLISH_LANG
	 * Expand Module Alarm Input Channel ID
	 * \else
	 * 扩展模块报警输入通道号
	 * \endif
	 */
	public int					pnExtended[]; 
}
