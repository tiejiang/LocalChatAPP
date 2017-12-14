package com.ty.winchat.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.ty.winchat.WinChatApplication;
import com.ty.winchat.listener.inter.IconReceived;
import com.ty.winchat.listener.inter.OnProgressUpdate;
import com.ty.winchat.listener.inter.OnTCPReceive;
import com.ty.winchat.util.Constant;

/**
 * �ļ����������
 * @author wj
 * @creation 2013-5-7
 */
public class TCPFileListener extends TCPListener  implements OnTCPReceive{

	private int port=Constant.FILE_PORT;
	public static TCPFileListener instance;
	
	private OnProgressUpdate onProgressUpdate;
	private IconReceived iconReceived;
	//����������
	private Socket sendClient;	
	
	ExecutorService executor=Executors.newFixedThreadPool(5);
	private Map<String, Integer> receiveCountMap=new HashMap<String, Integer>();//�������浱ǰ�����ļ��Ľ����ֽ���
	
	private TCPFileListener(){}
	
	public static TCPFileListener getInstance(){
		return instance==null?instance=new TCPFileListener():instance;
	}
	
	@Override
	public void onReceiveFileSucc(File file) {
		if(onProgressUpdate!=null)
		  	onProgressUpdate.onReceiveSucc(file);
	}
	
	@Override
	public void onSendFileSucc(File file) {
		if(onProgressUpdate!=null)
		  	onProgressUpdate.onSendSucc(file);
	}

	@Override
	void init() {
		setPort(port);
	}
	
	@Override
	public void onReceiveData(Socket socket) throws IOException {
		InputStream in=socket.getInputStream();
		
		byte[] name=new byte[100];//�����洢ͷ��Ϣ
		byte[] byteCount=new byte[20];//�����洢�ļ�����
		byte[] filePath=new byte[200];//���������ļ��洢·��
		
		in.read(name, 0, name.length);
		in.read(byteCount, 0, byteCount.length);
		in.read(filePath,0,filePath.length);
		
		String fileName=new String(name).trim();//�õ��ļ���
		long fileSize=Long.valueOf(new String(byteCount).trim());
		String filedir=new String(filePath).trim();//�õ��ļ�����·��
		
		if(fileName==null ) throw new IOException("δ���յ��ļ���");
		if(filedir==null ) throw new IOException("δ���յ��ļ��洢·��");
		if(fileSize<0) throw new IOException("�ļ�����С��0");
		
		File file;
		if(filedir.indexOf("com.ty")==-1){
			file=new File(WinChatApplication.mainInstance.getFilePath()+fileName);
		}else {
			file=new File(filedir+fileName);
			file.delete();//ɾ��ԭ����ͷ��ͼƬ
		}
		
		if(!file.exists())
			file.createNewFile();
		FileOutputStream out=new FileOutputStream(file,true);
		byte[] data=new byte[1024*4];
		int len;
		Integer receiveCount=receiveCountMap.get(file.getName());
		if(receiveCount==null){
			receiveCount=0;
			receiveCountMap.put(file.getName(), receiveCount);
		}
		
		while((len=in.read(data))!=-1){//��ȡ�ֽ�
			out.write(data, 0, len);
			receiveCount+=len;
			onReceiveProgressIncrease(receiveCount*100./fileSize,file.getPath());
			receiveCountMap.put(file.getName(), receiveCount);
		}
		out.flush();
		out.close();
		in.close();
		socket.close();
//		Log.d("==========", "�����ֽڣ�"+receiveCount);
		if(receiveCount==fileSize)
			onReceiveFileSucc(file);
		if(WinChatApplication.iconPath.equals(filedir)&&iconReceived!=null)
			iconReceived.iconReceived(fileName);
	}

	
	/**
	 * �����ļ�
	 * @param dstName  Ŀ��ip
	 * @param file  �ļ�����
	 * @param filePath	Ŀ���ļ�����·��
	 * @param specifyFileName  Ŀ��ָ���������֣�Ϊ���򱣴�file����
	 */
	public void sendFile(final String dstName,final File file,final String filePath,final String specifyFileName)   {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					byte nullByte=32;//����ո��ַ���
					//�Զ���ͷ��Ϣ������Ϊ120���ֽڣ�
					byte[] head=new byte[100];//100�ֽ���������ļ���
					byte[] fileLength=new byte[20];//20�ֽڴ���ļ�����
					byte[] filep=new byte[200];//200�ֽڴ���ļ��洢·��
					for(int i=0;i<head.length;i++){
						head[i]=nullByte;
					}
					for(int i=0;i<fileLength.length;i++){
						fileLength[i]=nullByte;
					}
					for(int i=0;i<filep.length;i++){
						filep[i]=nullByte;
					}
					String temp=file.getName();
					if(specifyFileName!=null)
						temp=specifyFileName;
					if(temp.length()>50){//һ������ռ2���ֽ�
						throw new IOException("�ļ�������");
					}else {
						byte[] name=temp.getBytes();
						System.arraycopy(name, 0, head, 0, name.length);//���ļ����Ž��Զ����ͷ��Ϣ��
					}
					
					long length=file.length();
					byte[] leng=(length+"").getBytes();
					System.arraycopy(leng, 0, fileLength, 0, leng.length);//���ļ����ȷŽ�ͷ��Ϣ
					
					byte[] l=filePath.getBytes();
					System.arraycopy(l, 0, filep, 0,l.length);
					//TODO  
					/*����һ��socket����ֻ����bufferSize��С�����ݣ������˻���������������һ������
					 * ����Բ��ã�socketӦ��ֻ����һ�ν��������ݷ������,�ȴ��޸�
					 */
					long bufferSize=1024*88*4;//һ���Է���352k�ֽڣ����Է������400k������
					int socketNum=1;//���͵�socket������Ĭ��1��
					if(length>bufferSize){//�ļ����ȴ���һ���Է��͵��ֽڴ�С����׼����������
						BigDecimal a = new BigDecimal(length);
						BigDecimal b = new BigDecimal(bufferSize);
						socketNum = a.divide(b,BigDecimal.ROUND_UP).intValue();//����
					}
					Log.d("TCPListener", "��ʼ�����ļ����ܴ�С:"+length);
					FileInputStream in=new FileInputStream(file);
					byte[] data=new byte[1024*4];
					int len;
					int sendCount = 0;
					for(int i=0;i<socketNum;i++){
						sendClient=new Socket(dstName, port);
						OutputStream out=sendClient.getOutputStream();
						out.write(head);//д��ͷ��Ϣ
						out.write(fileLength);
						out.write(filep);
						while((len=in.read(data))!=-1){
							out.write(data, 0, len);
							sendCount+=len;
							onSendProgressIncrease(sendCount*100./length,file.getPath());
							Log.d("TCPListener", "�����ֽڣ�"+sendCount);
							if(sendCount%bufferSize==0){
								break;//������ǰ���ͣ�׼����һ��
							}
						}
						out.flush();
						out.close();
						sendClient.close();
					}
					in.close();//���ر�
					onSendFileSucc(file);
					Log.d("TCPListener", "�����ļ�����");
				} catch (IOException e) {
					e.printStackTrace();
					noticeSendFileError(e);
				}
				
			}
		});
		
	}

	@Override
	public void noticeReceiveError(IOException e) {
		
	}
	
	@Override
	public void noticeSendFileError(IOException e) {
		
	}

	@Override
	public void close() throws IOException {
		super.close();
		instance=null;
	}
	

	public OnProgressUpdate getOnProgressUpdate() {
  	return onProgressUpdate;
  }

	public void setOnProgressUpdate(OnProgressUpdate onProgressUpdate) {
  	this.onProgressUpdate = onProgressUpdate;
  }

	@Override
  public void onSendProgressIncrease(double percent,String filePath) {
	  if(onProgressUpdate!=null)
	  	onProgressUpdate.onSendProgressIncrease(percent,filePath);
  }

	@Override
  public void onReceiveProgressIncrease(double percent,String filePath) {
		if(onProgressUpdate!=null)
	  	onProgressUpdate.onReceiveProgressIncrease(percent,filePath);
  }

	public IconReceived getIconReceived() {
		return iconReceived;
	}

	public void setIconReceived(IconReceived iconReceived) {
		this.iconReceived = iconReceived;
	}

}
