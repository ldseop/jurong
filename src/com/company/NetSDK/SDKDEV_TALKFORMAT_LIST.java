package com.company.NetSDK;

import java.io.Serializable;

/**
 * \if ENGLISH_LANG
 * The bidirectional talk type device supported
 * \else
 * 设备支持的语音对讲类型
 * \endif
 */
public class SDKDEV_TALKFORMAT_LIST implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * \if ENGLISH_LANG
	 * Amount 
	 * \else
	 * 个数
	 * \endif
	 */
	public int						nSupportNum;							
	/**
	 * \if ENGLISH_LANG
	 * Encode type 
	 * \else
	 * 编码类型
	 * \endif
	 */
	public SDKDEV_TALKDECODE_INFO type[] = new SDKDEV_TALKDECODE_INFO[64];	
	
	public SDKDEV_TALKFORMAT_LIST() {
		for(int i=0 ; i < 64 ; i++) {
			type[i] = new SDKDEV_TALKDECODE_INFO();
		}
	}
}
