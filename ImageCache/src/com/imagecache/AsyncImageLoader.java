package com.imagecache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * @author Administrator
 * @desc 异步图片下载
 */
public class AsyncImageLoader {
	private static final String TAG = "AsyncImageDownloader";
	
	private static  HashSet<String> sDownloadingSet;
	private static Map<String,SoftReference<Bitmap>> sImageCache; 
	private static ImageManager mManager;
	private static ExecutorService sThreadPool;
	private static Handler sHandler; 
	
	/**
	 * 异步加载图片完毕的回调函数
	 */
	public interface ImageCallback{
		public void onImageLoaded(Bitmap bitmap, String imageUrl);
	}
	
	static{
		sDownloadingSet = new HashSet<String>();
		sImageCache = new HashMap<String,SoftReference<Bitmap>>();
		sThreadPool = Executors.newFixedThreadPool(5);
		sHandler = new Handler();
	}

	public AsyncImageLoader(Context context){
		mManager = new ImageManager(context, sImageCache);
	}
	
	/**
	 * 是否缓存图片至/data/data/package/cache/目录
	 */
	public void setCacheFile(boolean flag){
		mManager.setCacheFile(flag);
	}
	
	//设置缓存路径，默认为cache目录
	public void setCachePath(String cacheDir){
		mManager.setCachePath(cacheDir);
	}
	
	//关闭线程池
	/*public static void shutDownThreadPool(){
		if( !sThreadPool.isShutdown() )
			sThreadPool.shutdown();
	}*/
	
	/**
	 * 异步下载图片
	 * @param url	
	 * @param callback	see ImageCallback interface
	 */
	public void downloadImage(ImageView imageView, final String url, final ImageCallback callback){
		downloadImage(imageView, url, true, callback);
	}
	
	public void downloadImage(ImageView imageView, final String url, final boolean cache2Memory, final ImageCallback callback){
		if(sDownloadingSet.contains(url)){
			Log.i(TAG, "该图片正在下载，不能重复下载！");
			return ;
		}
		
		Bitmap bitmap = mManager.getBitmapCache(url);
		if(bitmap != null){
			if(imageView != null){
				imageView.setImageBitmap(bitmap);
			}
		}else{
			//从网络端下载图片
			sDownloadingSet.add(url);
			sThreadPool.submit(new Runnable(){
				@Override
				public void run() {
					final Bitmap bitmap = mManager.getBitmapFromUrl(url, cache2Memory);
					sHandler.post(new Runnable(){
						@Override
						public void run(){
							callback.onImageLoaded(bitmap, url);
							sDownloadingSet.remove(url);
						}
					});
				}
			});
		}
	}
	
}
