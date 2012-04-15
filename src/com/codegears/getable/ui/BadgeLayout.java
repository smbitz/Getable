package com.codegears.getable.ui;

import com.codegears.getable.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BadgeLayout extends LinearLayout {

	private ListView badgeList;

	public BadgeLayout( Context context ) {
		super( context );
		View.inflate( context, R.layout.badgelayout, this );
		badgeList = (ListView) this.findViewById( R.id.BadgeList );
		badgeList.setAdapter( new BadgeAdapter() );
	}

	private class BadgeAdapter extends BaseAdapter {

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
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			BadgeListItem view = new BadgeListItem(BadgeLayout.this.getContext());
			return view;
		}
	}
}
