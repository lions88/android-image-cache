package com.imagecache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ImageView iv = (ImageView)findViewById(R.id.iv);
        String imgUrl = "http://...";
        
        //for test
        //从网络端下载图片,是否缓存在内存中？是否缓存至某目录下?
        AsyncImageLoader loader = new AsyncImageLoader(getApplicationContext());
        
        //将图片缓存至文件夹下
        loader.setCache2File(true);	//false
        loader.setCachePath(this.getCacheDir().getAbsolutePath());
        
        loader.downloadImage(iv, imgUrl, true/*false*/, new AsyncImageLoader.ImageCallback() {
			@Override
			public void onImageLoaded(Bitmap bitmap, String imageUrl) {
				if(bitmap != null){
					iv.setImageBitmap(bitmap);
				}else{
					//下载失败，设置默认图片
				}
			}
		});
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	AsyncImageLoader.stopThreadPool();
    }
}