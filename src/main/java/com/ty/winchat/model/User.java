package com.ty.winchat.model;

import java.io.Serializable;

/**
 * �û�ʵ����
 *
 */
public class User implements Serializable{
	
	private static final long serialVersionUID = -5062775818842005386L;
	
	private String userName;	// �û���
	private String ip;			//ip��ַ
	private String mac;			//MAC��ַ
	private String deviceCode;//�ֻ��豸��
	private String heartTime;//��¼�����������һ��ʱ��
	private boolean refreshIcon;//��¼�Ƿ�ˢ��ͷ�񣨵�¼��һ�λ�ˢ��ͷ��
	
	public static final int INTERVAL=10*1000;//���������ʱ��
	public static final int TIMEOUT=(int) (2.1*INTERVAL);//��ʱʱ��
	
	public User(){
		heartTime=System.currentTimeMillis()+"";
	}
	
	public String getUserName() {
  	return userName;
  }
	public void setUserName(String userName) {
  	this.userName = userName;
  }
	public String getIp() {
  	return ip;
  }
	public void setIp(String ip) {
  	this.ip = ip;
  }
	public String getMac() {
  	return mac;
  }
	public void setMac(String mac) {
  	this.mac = mac;
  }
	public String getDeviceCode() {
		return deviceCode;
	}
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getHeartTime() {
		return heartTime;
	}

	public void setHeartTime(String heartTime) {
		this.heartTime = heartTime;
	}
	
	public boolean isRefreshIcon() {
		return refreshIcon;
	}

	public void setRefreshIcon(boolean refreshIcon) {
		this.refreshIcon = refreshIcon;
	}

	/**
	 * ������֤�Է��Ƿ�����
	 * @return
	 */
	public boolean checkOnline(){
		return !(System.currentTimeMillis()-Long.valueOf(heartTime)>TIMEOUT);
	}
}
