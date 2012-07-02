package com.codegears.getable;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.codegears.getable.R;
import android.view.View.OnClickListener;

public class HelpLayout extends Activity implements OnClickListener {
	
	private ImageButton okayButton;
	private ImageButton closeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView( R.layout.profilehelplayout );
		
		okayButton = (ImageButton) findViewById( R.id.profileHelpLayoutOkayButton );
		closeButton = (ImageButton) findViewById( R.id.profileHelpLayoutCloseButton );
		
		okayButton.setOnClickListener( this );
		closeButton.setOnClickListener( this );
	}

	@Override
	public void onClick(View v) {
		if( v.equals( okayButton ) ){
			this.finish();
		}else if( v.equals( closeButton ) ){
			this.finish();
		}
	}

}
