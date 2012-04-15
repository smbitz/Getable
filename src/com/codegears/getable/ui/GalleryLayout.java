package com.codegears.getable.ui;

import com.codegears.getable.BodyLayoutStackListener;
import com.codegears.getable.GalleryFilterActivity;
import com.codegears.getable.R;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GalleryLayout extends LinearLayout implements OnClickListener, OnItemClickListener {

	public static final int LAYOUTCHANGE_PRODUCTDETAIL = 1;
	private Button filterButton;
	private GridView galleryGrid;
	private GalleryAdapter galleryAdapter;
	private BodyLayoutStackListener listener;
	
	public GalleryLayout( Context context ) {
		super( context );
		View.inflate( context, R.layout.gallerylayout, this );
		filterButton = (Button)this.findViewById( R.id.FilterButton );
		filterButton.setOnClickListener( this );
		galleryGrid = (GridView)this.findViewById( R.id.GalleryGrid );
		galleryAdapter = new GalleryAdapter();
		galleryGrid.setAdapter( galleryAdapter );
		galleryGrid.setOnItemClickListener( this );
	}

	public void setBodyLayoutChangeListener(BodyLayoutStackListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View view ) {
		if(filterButton.equals( view )){
			Intent intent = new Intent(this.getContext(), GalleryFilterActivity.class);
			this.getContext().startActivity( intent );
		}
	}
	
	@Override
	public void onItemClick( AdapterView< ? > arg0, View arg1, int arg2, long arg3 ) {
		if(listener != null){
			listener.onRequestBodyLayoutStack(LAYOUTCHANGE_PRODUCTDETAIL);
		}
	}

	private class GalleryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem( int position ) {
			return null;
		}

		@Override
		public long getItemId( int position ) {
			return 1;
		}

		@Override
		public View getView( int arg0, View arg1, ViewGroup arg2 ) {
			ImageView view = new ImageView(GalleryLayout.this.getContext());
			view.setImageResource( R.drawable.ic_launcher );
			return view;
		}
	}

}