package com.company.NetSDK;

/**
 * \if ENGLISH_LANG
 * The Video Analysis Rule Configuration Corresponding To Every Video Input
 * \else
 * 每个视频输入通道对应的所有事件规则：缓冲区pRuleBuf填充多个事件规则信息，每个事件规则信息内容为CFG_RULE_INFO+"事件类型对应的规则配置结构体"。
 * \endif
 */
public class CFG_ANALYSERULES_INFO
{
 	/**
	 * \if ENGLISH_LANG
	 * Event Rule Count 
	 * \else
	 * 事件规则个数
	 * \endif
	 */
	public int 						nRuleCount;
	
 	/**
	 * \if ENGLISH_LANG
	 * The Event Analysis Rule Configuration Corresponding To Each Video Input 
	 * \else
	 * 每个视频输入通道对应的视频分析事件规则配置缓冲
	 * \endif
	 */
	public int						nRuleLen;
	
 	/**
	 * \if ENGLISH_LANG
	 * Cache Size 
	 * \else
	 * 缓冲大小
	 * \endif
	 */
	public byte[]					pRuleBuf;
	
	public CFG_ANALYSERULES_INFO(int nRuleLen) {
		this.nRuleLen = nRuleLen;
		this.pRuleBuf = new byte[nRuleLen];
	}
}
