package com.lming.minichat.bean;

public class MessageBean {

	private int msgId;
	private long msgDate;
	private String msgContent;
	private int isSend;//send 0,receive 1;
	
	private int msgType;//文字0,本地图片1,拍照图片2，语音3
	private String msgToLoginName;
	
	private String msgImageUrl;
	private String msgImageId;
	private String msgImageFile;
	
	private String msgVoiceUrl;
	private int isRead;
	/**
	 * @return the msgId
	 */
	public int getMsgId() {
		return msgId;
	}
	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	/**
	 * @return the msgDate
	 */
	public long getMsgDate() {
		return msgDate;
	}
	/**
	 * @param msgDate the msgDate to set
	 */
	public void setMsgDate(long msgDate) {
		this.msgDate = msgDate;
	}
	/**
	 * @return the msgContent
	 */
	public String getMsgContent() {
		return msgContent;
	}
	/**
	 * @param msgContent the msgContent to set
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	/**
	 * @return the isSend
	 */
	public int getIsSend() {
		return isSend;
	}
	/**
	 * @param isSend the isSend to set
	 */
	public void setIsSend(int isSend) {
		this.isSend = isSend;
	}
	/**
	 * @return the msgType
	 */
	public int getMsgType() {
		return msgType;
	}
	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	/**
	 * @return the msgToUserId
	 */
	public String getMsgToLoginName() {
		return msgToLoginName;
	}
	/**
	 * @param msgToUserId the msgToUserId to set
	 */
	public void setMsgToLoginName(String msgToLoginName) {
		this.msgToLoginName = msgToLoginName;
	}
	/**
	 * @return the msgImageUrl
	 */
	public String getMsgImageUrl() {
		return msgImageUrl;
	}
	/**
	 * @param msgImageUrl the msgImageUrl to set
	 */
	public void setMsgImageUrl(String msgImageUrl) {
		this.msgImageUrl = msgImageUrl;
	}
	/**
	 * @return the msgImageId
	 */
	public String getMsgImageId() {
		return msgImageId;
	}
	/**
	 * @param msgImageId the msgImageId to set
	 */
	public void setMsgImageId(String msgImageId) {
		this.msgImageId = msgImageId;
	}
	/**
	 * @return the msgImageFile
	 */
	public String getMsgImageFile() {
		return msgImageFile;
	}
	/**
	 * @param msgImageFile the msgImageFile to set
	 */
	public void setMsgImageFile(String msgImageFile) {
		this.msgImageFile = msgImageFile;
	}
	/**
	 * @return the msgVoiceUrl
	 */
	public String getMsgVoiceUrl() {
		return msgVoiceUrl;
	}
	/**
	 * @param msgVoiceUrl the msgVoiceUrl to set
	 */
	public void setMsgVoiceUrl(String msgVoiceUrl) {
		this.msgVoiceUrl = msgVoiceUrl;
	}
	/**
	 * @return the isRead
	 */
	public int getIsRead() {
		return isRead;
	}
	/**
	 * @param isRead the isRead to set
	 */
	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}
}
