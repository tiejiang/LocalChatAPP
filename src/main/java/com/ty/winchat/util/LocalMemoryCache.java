package com.ty.winchat.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * �ڴ������Ҫ��������ͼƬ
 * @author wj
 * @creation 2013-5-22
 */
public class LocalMemoryCache {
	private static LocalMemoryCache instance;
	
	////�����С 10M
	private final int CACHESIZE=1024*1024*10;
	
	private LruCache<String, Bitmap> lruCache;
	private List<String> keys;//��¼keyֵ
	
	private LocalMemoryCache(){
		lruCache=new LruCache<String, Bitmap>(CACHESIZE){
				@Override
				protected int sizeOf(String key, Bitmap value) {
					return value.getRowBytes()*value.getHeight();
				}
			};
		keys=new ArrayList<String>();
	}
	
	public static LocalMemoryCache getInstance(){
		return instance==null?instance=new LocalMemoryCache():instance;
	}
	
	public void put(String key,Bitmap bitmap){
		if(bitmap!=null){
			lruCache.put(key, bitmap);
			keys.add(key);
		}
	}
	
	public Bitmap get(String key){
		return lruCache.get(key);
	}
	
	public void remove(String key){
		lruCache.remove(key);
	}
	
	public void removeAll(){
		for(String k:keys){
			lruCache.remove(k);
		}
		keys.clear();
		Log.d("LocalMemoryCache", "���ͼƬ����");
	}
	
}
