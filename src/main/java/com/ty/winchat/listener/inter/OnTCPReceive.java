package com.ty.winchat.listener.inter;

import java.io.File;

public interface OnTCPReceive {
	/**���յ��ļ� �Ļص�����*/
	void onReceiveFileSucc(File file);
	/**�����ļ� �Ļص�����*/
	void onSendFileSucc(File file);
	/**���ط����ļ��������İٷֱ�*/
	void onSendProgressIncrease(double percent,String filePath);
	/**���ؽ��ս������İٷֱ�*/
	void onReceiveProgressIncrease(double percent,String filePath);
}
