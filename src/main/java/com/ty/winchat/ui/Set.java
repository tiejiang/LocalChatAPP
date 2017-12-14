package com.ty.winchat.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ty.winchat.R;
import com.ty.winchat.util.LocalMemoryCache;
import com.ty.winchat.util.Util;
/**
 * ���ý���
 * @author wj
 * @creation 2013-6-6
 */
public class Set extends Base implements OnClickListener{
	
	private EditText nickNameEdt,optionEdt;
	private Button nickNameBtn,optionBtn,iconBtn;
	private ImageView icon;
	
	private final int CUT_PHOTO_REQUEST_CODE=201;
	private final int SELECT_PHOTO_REQUEST_CODE=200;
	
	private String iconPath;//ͷ�񱣴�·��
	public static String iconName="me";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		iconPath=getFilesDir()+File.separator+iconName;
		findViews();
	}
	
	private void findViews(){
		nickNameEdt=(EditText) findViewById(R.id.set_nick_name_edt);
		optionEdt=(EditText) findViewById(R.id.set_option_edt);
		nickNameBtn=(Button) findViewById(R.id.set_nick_name_btn);
		optionBtn=(Button) findViewById(R.id.set_option_btn);
		iconBtn=(Button) findViewById(R.id.set_icon_btn);
		icon=(ImageView) findViewById(R.id.set_icon);
		//����ͷ��
		Bitmap bitmap=LocalMemoryCache.getInstance().get(iconName);
		if(bitmap==null){
			 bitmap=BitmapFactory.decodeFile(iconPath);
			 if(bitmap!=null){
				 icon.setImageBitmap(Util.getRoundedCornerBitmap(bitmap));
				 LocalMemoryCache.getInstance().put(iconName, bitmap);
			 }else {
				 icon.setImageResource(R.drawable.ic_launcher);
			}
		}else {
			 icon.setImageBitmap(Util.getRoundedCornerBitmap(bitmap));
		}
		
		nickNameEdt.setText(getSharedPreferences("me", 0).getString("name", "����"));
		TextView view=(TextView) findViewById(R.id.toptextView);
		view.setText("����");
		nickNameBtn.setOnClickListener(this);
		optionBtn.setOnClickListener(this);
		iconBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.set_nick_name_btn:
			String nickName=nickNameEdt.getText().toString().trim();
			if("".equals(nickName)){
				showToast("�������ǳ�");
				return ;
			}
			Editor editor=getSharedPreferences("me", 0).edit();
			editor.putString("name", nickName);
			editor.commit();
			showToast("����ɹ�");
			break;
			
		case R.id.set_option_btn:
			String option=optionEdt.getText().toString().trim();
			if("".equals(option)){
				showToast("���������");
				return ;
			}
			showToast("���ͳɹ�");
			break;
			
		case R.id.set_icon_btn://����ϵͳͼƬ��
			try {
				Intent i = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
				i.setType("image/*");
				startActivityForResult(i, SELECT_PHOTO_REQUEST_CODE);
			} catch (Exception e) {
				showToast("��Ǹ�������ֻ���֧��ͷ������");
			}
			
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==SELECT_PHOTO_REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
			Uri uri=data.getData();
			if (uri != null) {
			    final Intent intent = new Intent("com.android.camera.action.CROP"); 
			      intent.setDataAndType(uri, "image/*");
			      intent.putExtra("crop", "true");
			      intent.putExtra("aspectX", 1);
			      intent.putExtra("aspectY", 1);
			      intent.putExtra("outputX", 100);
			      intent.putExtra("outputY", 100);
			      intent.putExtra("return-data", true);
			      startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
			}
		}else if(requestCode==CUT_PHOTO_REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
			try {
				Bitmap bitmap= data.getParcelableExtra("data");
				bitmap=Util.getRoundedCornerBitmap(bitmap);
				icon.setImageBitmap(bitmap);
				File file=new File(iconPath);
				file.delete();
				file.createNewFile();
				FileOutputStream outputStream=new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
				outputStream.flush();
				outputStream.close();
				LocalMemoryCache.getInstance().put(iconName, bitmap);
				showToast("ͷ�񱣴�ɹ�");
			} catch (IOException e) {
				e.printStackTrace();
				showToast("ͷ�񱣴�ʧ��");
			}
		}
	}
}
