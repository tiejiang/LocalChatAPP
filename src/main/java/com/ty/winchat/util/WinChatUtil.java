package com.ty.winchat.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;

import com.ty.winchat.R;
import com.ty.winchat.ui.FaceDialog;

public class WinChatUtil {
	/**
	 * ��ú�׺����Ӧ��ͼƬ��Դ
	 * @param exte
	 * @return
	 */
	public static int getResourceId(String exte){
		for(int i=0;i<extensions.length;i++){
			String[] extension=extensions[i];
			for(String a:extension){
				if(a.equals(exte))
					return extensions_imgId[i];
			}
		}
		return R.drawable.file_default;
	}
	
	
	public static String[][] extensions={
			{"txt"},//�ı�
			{"doc","docx","dotx"},//word
			{"gif","jpg","png","bmp"},//ͼƬ
			{"mp3","wma"},
			{"pdf"},//
			{"xls"},//���
			{"rar","zip"},//ѹ���ļ�
			{"avi","rmvb","3gp","flv","wav"},//����
			{"ppt"},//
	};
	public static	int[] extensions_imgId={
			R.drawable.file_txt,
			R.drawable.file_word,
			R.drawable.file_jpg,
			R.drawable.file_mp3,
			R.drawable.file_pdf,
			R.drawable.file_xls,
			R.drawable.file_zip,
			R.drawable.file_avi,
			R.drawable.file_ppt
			};
	
	/**
	 * ���ļ�
	 * 
	 * @param file
	 */
	public static void openFile(Context context,File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// ����intent��Action����
		intent.setAction(Intent.ACTION_VIEW);
		// // ��ȡ�ļ�file��MIME����
		String type = getMIMEType(file);
		// ����intent��data��Type���ԡ�
		intent.setDataAndType(Uri.fromFile(file), type);
		// ��ת
		context.startActivity(intent);
	}
	
	/**
	 * �����ļ���׺����ö�Ӧ��MIME����
	 * 
	 * @param file
	 */
	public static String getMIMEType(File file) {
		String type = getFileType(file.getName());
		// ��MIME���ļ����͵�ƥ������ҵ���Ӧ��MIME���͡�
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (type.equals(MIME_MapTable[i][0]))
				return MIME_MapTable[i][1];
		}
		return type;
	}
	
	/**
	 * ��ȡ�ļ��ĺ�׺��
	 * 
	 * @param filaName
	 * @return
	 */
	public static String getFileType(String fName) {
		String type = "";
		// ��ȡ��׺��ǰ�ķָ���"."��fName�е�λ�á�
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* ��ȡ�ļ��ĺ�׺�� */
		return fName.substring(dotIndex, fName.length()).toLowerCase();
	}
	
/**
 * ��spanableString���������ж�
 * @param context
 * @param spannableString
 * @param patten
 * @param start
 * @throws SecurityException
 * @throws NoSuchFieldException
 * @throws NumberFormatException
 * @throws IllegalArgumentException
 * @throws IllegalAccessException
 */
    public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {
    	Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            if (matcher.start() < start) {
                continue;
            }
//            Field field = R.drawable.class.getDeclaredField(key);
//			int resId = Integer.parseInt(field.get(null).toString());		//ͨ������ƥ��õ����ַ���������ͼƬ��Դid
            int a=Integer.valueOf(key.replace("f", ""));
            int resId=FaceDialog.imageIds[a%107];
            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);	
                 ImageSpan imageSpan = new ImageSpan(bitmap);//ͨ��ͼƬ��Դid���õ�bitmap����һ��ImageSpan����װ
                int end = matcher.start() + key.length();					//�����ͼƬ���ֵĳ��ȣ�Ҳ����Ҫ�滻���ַ����ĳ���
                spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);	//����ͼƬ�滻�ַ����й涨��λ����
                if (end < spannableString.length()) {						//��������ַ�����δ��֤�꣬���������
                    dealExpression(context,spannableString,  patten, end);
                }
                break;
            }
        }
    }
	
	  /**
     * �õ�һ��SpanableString����ͨ��������ַ���,�����������ж�
     * @param context
     * @param str
     * @return
     */
    public static SpannableString getExpressionString(Context context,String str,String zhengze){
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//ͨ�������������ʽ������һ��pattern
        try {
            dealExpression(context,spannableString, sinaPatten, 0);
        } catch (Exception e) {
            Log.e("dealExpression", e.getMessage());
        }
        return spannableString;
    }
	
	// {��׺����MIME����}
	private static final String[][] MIME_MapTable = {
		{ ".3gp", "video/3gpp" }, { ".apk", "application/vnd.android.package-archive" }, { ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" },
		{ ".bin", "application/octet-stream" }, { ".bmp", "image/bmp" }, { ".c", "text/plain" }, { ".class", "application/octet-stream" },
		{ ".conf", "text/plain" }, { ".cpp", "text/plain" }, { ".doc", "application/msword" },
		{ ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, { ".xls", "application/vnd.ms-excel" },
		{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }, { ".exe", "application/octet-stream" }, { ".gif", "image/gif" },
		{ ".gtar", "application/x-gtar" }, { ".gz", "application/x-gzip" }, { ".h", "text/plain" }, { ".htm", "text/html" }, { ".html", "text/html" },
		{ ".jar", "application/java-archive" }, { ".java", "text/plain" }, { ".jpeg", "image/jpeg" }, { ".jpg", "image/jpeg" },
		{ ".js", "application/x-javascript" }, { ".log", "text/plain" }, { ".m3u", "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" },
		{ ".m4b", "audio/mp4a-latm" }, { ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" },
		{ ".mov", "video/quicktime" }, { ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" },
		{ ".mpc", "application/vnd.mpohun.certificate" }, { ".mpe", "video/mpeg" }, { ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg" },
		{ ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" }, { ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" },
		{ ".pdf", "application/pdf" }, { ".png", "image/png" }, { ".pps", "application/vnd.ms-powerpoint" }, { ".ppt", "application/vnd.ms-powerpoint" },
		{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" }, { ".prop", "text/plain" }, { ".rc", "text/plain" },
		{ ".rmvb", "audio/x-pn-realaudio" }, { ".rtf", "application/rtf" }, { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
		{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
		{ ".wmv", "audio/x-ms-wmv" }, { ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" }, { ".z", "application/x-compress" },
		{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };
}
