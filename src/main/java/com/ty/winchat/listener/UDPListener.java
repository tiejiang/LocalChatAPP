package com.ty.winchat.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.ty.winchat.util.Constant;

/**
 * ��������ʹ��UDPЭ�鷢�͵�����
 * @author wj
 * @creation 2013-5-7
 */
public abstract class UDPListener extends Listener{
	protected volatile boolean isOnline;
	//���ڽ��պͷ������ݵ�socket ��DatagramSocketֻ����ָ����ַ���ͣ�MulticastSocket��ʵ�ֶ��㲥
	private MulticastSocket  socket;
	private DatagramPacket packet;
	
	//TODO  ������������ʼֵ
	private int port=Constant.MESSAGE_PORT;
	private int default_bufferSize=1024*2;
	private byte[] bufferData;//��������UDP���͵�����,���Ƿ�����Ϣ���������������С
	
	private ExecutorService executor=Executors.newFixedThreadPool(20);//����������Ϣ
	
	/**
	 * ��������ǰ��һЩ��ʼ���������˿ڣ���������С
	 */
	abstract void init();
	
	/**
	 * �󶨶˿ڣ�����ͨ��
	 * @throws IOException 
	 */
	private void createSocket() throws IOException{
		init();
		socket=new MulticastSocket(port);
		bufferData=new byte[default_bufferSize];
		packet=new DatagramPacket(bufferData, bufferData.length);
		isOnline=true;
	}
	
	@Override
	public void run() {
		while(isOnline){
		try {
		      socket.receive(packet);//ʵʱ��������
//		      Log.d("UDPListener", "���ճ��ȣ�"+packet.getLength());
		      if(packet.getLength()==0) continue;//û����Ϣ�����ѭ��
		      onReceive(bufferData,packet);//������յ�����
		      //ÿ�ν�����UDP���ݺ����ó��ȡ�������ܻᵼ���´��յ����ݰ����ضϡ�
		      packet.setLength(default_bufferSize);
		 } catch (IOException e) {
		     Log.e("UDPListener", "UDP��Ϣ������������ֹͣ");
		   }
		}
	}
	
	/**�˿���������ʱ�Ļص�����*/
	public abstract void onReceive(byte[] data,DatagramPacket packet);
	
	/**
	 * ����UDP���ݰ�
	 * @param msg  ��Ϣ
	 * @param destIp  Ŀ���ַ
	 * @param destPort  Ŀ��˿�
	 * @throws IOException 
	 */
	protected void send(final String msg, final InetAddress destIp, final int destPort){
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					DatagramPacket packet=new DatagramPacket(msg.getBytes(Constant.ENCOD), msg.length(), destIp, destPort);
					socket.send(packet);
					if(!isOnline) socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					sendMsgFailure();
				}
			}
		});
	}
	
	/**
	 * ����UDP���ݰ�
	 * @param msg  ��Ϣ
	 * @param destIp  Ŀ���ַ
	 * @param destPort  Ŀ��˿�
	 * @throws IOException 
	 */
	protected void send(final byte[] data,final int length, final InetAddress destIp, final int destPort){
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					DatagramPacket packet=new DatagramPacket(data, length, destIp, destPort);
					socket.send(packet);
					if(!isOnline) socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					sendMsgFailure();
				}
			}
		});
	}
	
	/**֪ͨ����*/
	abstract void noticeOffline() throws IOException;
	/**֪ͨ����*/
	abstract void noticeOnline()  throws IOException;
	/**������Ϣʧ��*/
	abstract void sendMsgFailure();
	
	
	/**
	 * ��������
	 * @throws IOException 
	 */
	@Override
	public   void open() throws IOException {
		createSocket();
		setPriority(MAX_PRIORITY);
		start();
		noticeOnline();
		Log.d("UDPListener", "UDP�˿ڰ󶨳ɹ����������");
	}
	
	/**
	 * �رռ���
	 * @throws IOException 
	 */
	@Override
	public  void close() throws IOException{
		isOnline=false;
		interrupt();//���������״̬����;
		noticeOffline();
//		if(executor!=null) executor.shutdown();
		Log.d("UDPListener", "ֹͣ���");
	}
	
	/**
	 * ���ð󶨵Ķ˿ں�
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
  }

	/**
	 * ���û������Ĵ�С
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		this.default_bufferSize = bufferSize;
  }
	
}
