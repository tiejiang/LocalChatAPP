package com.ty.winchat.listener.inter;


public interface OnUDPReceiveMessage {
	/**�������ݱ��Ž���Ϣ���к�Ļص�����*/
	void onReceive(int type);
	void sendFailure();
}
