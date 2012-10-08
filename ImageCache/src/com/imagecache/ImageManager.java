package com.imagecache;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 
 * @author Administrator
 * @desc 异步加载图片管理器
 *
 */
public class ImageManager {
	private Map<String, SoftReference<Bitmap>> imageCache;
	
	//是否缓存图片至本地文件
	private boolean cacheFileFlag = false;
	
	//缓存目录,默认是/data/data/package/cache/目录
	private String cacheDir;
	
	public ImageManager(Context context, Map<String, SoftReference<Bitmap>> imageCache){
		this.imageCache = imageCache;
		this.cacheDir = context.getCacheDir().getAbsolutePath();
	}
	
	public void setCachePath(String cacheDir){
		this.cacheDir = cacheDir;
	}
	
	/**
	 * 从网络端下载图片
	 * @param url
	 * @return bitmap
	 * @throws IOException
	 */
	public Bitmap getBitmapFromUrl(String url){
		return getBitmapFromUrl(url, true);
	}
	
	/**
	 * 从网络端下载图片
	 * @param url
	 * @param cacheFlag是否缓存
	 * @return bitmap
	 * @throws IOException
	 */
	public Bitmap getBitmapFromUrl(String url, boolean cacheMemory){
		Bitmap bitmap = null;
		try{
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)u.openConnection();  
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			
			if(cacheMemory){
				//1.缓存bitmap至内存软引用中
				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
				if(cacheFileFlag){
					//2.缓存bitmap至/data/data/packageName/cache/文件夹中
					String fileName = Md5Util.getMD5Str(url);
					String filePath = this.cacheDir + "/" +fileName;
					FileOutputStream fos = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				}
			}
			
			is.close();
			conn.disconnect();
			return bitmap;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 设置是否缓存图片至本地文件夹
	 * @param flag
	 */
	public void setCacheFile(boolean flag){
		cacheFileFlag = flag;
	}

	
	/**
	 * 从缓存中获取bitmap
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapCache(String url){
		Bitmap bitmap = null;
		if(imageCache.containsKey(url)){
			synchronized(imageCache){
				SoftReference<Bitmap> bitmapRef = imageCache.get(url);
				if(bitmapRef != null){
					//System.out.println("###从内存缓存读取bitmap成功");
					bitmap = bitmapRef.get();
					return bitmap;
				}
			}
		}
		//从cache文件夹中获取
		if(cacheFileFlag){
			bitmap = getBitmapFromCacheDir(url);
			if(bitmap != null)
				imageCache.put(url, new SoftReference<Bitmap>(bitmap));
		}
		
		return bitmap;
	}
	
	/**
	 * 从/data/data/packageName/cache/目录获取bitmap
	 */
	private Bitmap getBitmapFromCacheDir(String url){
		Bitmap bitmap = null;
		String fileName = Md5Util.getMD5Str(url);
		String filePath = this.cacheDir + "/" + fileName;
		if(fileName != null){
			try {
				FileInputStream fis = new FileInputStream(filePath);
				bitmap = BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return bitmap;
	}
}
