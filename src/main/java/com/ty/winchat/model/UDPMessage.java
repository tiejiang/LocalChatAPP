package com.ty.winchat.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

/**
 * ������Ϣ��ʵ����
 *
 */
public class UDPMessage {
	private String senderName;	//��Ϣ�����ߵ�����
	private String msg;			//��Ϣ����
	private String sendTime;		//����ʱ��
	private String deviceCode;//�ֻ�Ψһʶ���
	private int type;//��ǰ��Ϣ������
	private boolean own;//��ʶ������Ϣ�Ƿ��Լ�����
	
	public UDPMessage(){
		sendTime=System.currentTimeMillis()+"";
	}
	
	public UDPMessage(String msg,boolean own){
		this();
		this.msg=msg;
		this.own=own;
	}
	
	public UDPMessage(JSONObject object) throws JSONException{
		senderName=new String(Base64.decode(object.getString("senderName").getBytes(), Base64.DEFAULT));
		msg=new String(Base64.decode(object.getString("msg").getBytes(), Base64.DEFAULT));
		
		sendTime=object.getString("sendTime");
		type=object.getInt("type");
		deviceCode=object.getString("deviceCode");
		object=null;
	}
	
	/**
	 * ����JSONObject���ݽṹ
	 */
	public String toString(){
//		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		JSONObject object=new JSONObject();
		try {
			object.put("senderName", Base64.encodeToString(senderName.getBytes(), Base64.DEFAULT));
			object.put("msg", Base64.encodeToString(msg.getBytes(), Base64.DEFAULT));
			object.put("sendTime", System.currentTimeMillis()+"");
			object.put("type", type);
			object.put("deviceCode", deviceCode);
			return object.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public String getSenderName() {
  	return senderName;
  }
	public void setSenderName(String senderName) {
  	this.senderName = senderName;
  }
	public String getMsg() {
  	return msg;
  }
	public void setMsg(String msg) {
  	this.msg = msg;
  }
	public String getSendTime() {
  	return sendTime;
  }
	public int getType() {
  	return type;
  }

	public void setType(int type) {
  	this.type = type;
  }

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public boolean isOwn() {
		return own;
	}

	public void setOwn(boolean own) {
		this.own = own;
	}
	
}
