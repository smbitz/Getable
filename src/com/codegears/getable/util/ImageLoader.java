package com.codegears.getable.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {
	
	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private MemoryCache memoryCache = new MemoryCache();
	private PhotosQueue photosQueue = new PhotosQueue();
	private Activity activity;
	private PhotosLoader photoLoaderThread = new PhotosLoader();
	private AsyncHttpClient asyncHttpClient;
	
	public ImageLoader(Context context){
		//Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
	}
	
	public Bitmap DisplayImage( String imageURL, Activity currentAct , ImageView imageView, Boolean isUseCache, AsyncHttpClient getAsyncHttpClient ){
		asyncHttpClient = getAsyncHttpClient;
		imageViews.put( imageView, imageURL );
		Bitmap bitmap = null;
		if( isUseCache ){
			bitmap = memoryCache.get( imageURL );
			if( bitmap != null ){
				imageView.setImageBitmap( bitmap );
				return bitmap;
			}
		}
		
		queuePhoto( imageURL, currentAct, imageView);
		return bitmap;
	}

	public Bitmap DisplayImage( String imageURL, Activity currentAct , ImageView imageView, Boolean isUseCache ){
		imageViews.put( imageView, imageURL );
		Bitmap bitmap = null;
		if( isUseCache ){
			bitmap = memoryCache.get( imageURL );
			if( bitmap != null ){
				imageView.setImageBitmap( bitmap );
				return bitmap;
			}
		}
		
		queuePhoto( imageURL, currentAct, imageView);
		return bitmap;
	}

	private void queuePhoto(String imageURL, Activity currentAct, ImageView imageView) {
		photosQueue.Clean( imageView );
        PhotoToLoad p = new PhotoToLoad( imageURL, imageView );
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
            photoLoaderThread.start();
        
        this.activity = currentAct;
	}
	
	private Bitmap getBitmap(String url) 
    {   
        //from web
        /*try {
            Bitmap bitmap=null;
            URL imageUrl = new URL( url );
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            InputStream is=conn.getInputStream();
            bitmap = BitmapFactory.decodeStream( is );
            return bitmap;
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }*/
		Bitmap bitmap = null;
		HttpClient client = asyncHttpClient.getHttpClient();
		HttpGet httpGet = new HttpGet( url );
		
		HttpResponse res;
		InputStream is;
		try {
			res = client.execute( httpGet, asyncHttpClient.getHttpContext() );
			is = res.getEntity().getContent();
			bitmap = BitmapFactory.decodeStream( is );
            return bitmap;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
	
	public void clearCache() {
        memoryCache.clear();
    }
	
	public void stopThread()
    {
        photoLoaderThread.interrupt();
    }
	
	private class PhotosQueue
    {
        private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
        
        //removes all instances of this ImageView
        public void Clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
	
	//Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
	
    private class PhotosLoader extends Thread {
        public void run() {
            try {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp = getBitmap(photoToLoad.url);
                        memoryCache.put(photoToLoad.url, bmp);
                        String tag = imageViews.get(photoToLoad.imageView);
                        if(tag!=null && tag.equals(photoToLoad.url)){
                            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView);
                        //    Activity a=(Activity)photoToLoad.imageView.getContext();
                            activity.runOnUiThread(bd);
                        }
                    }
                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }
    
    //Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
        public void run()
        {
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }else{
               // imageView.setImageResource(stub_id);
            }
        }
    }
}
