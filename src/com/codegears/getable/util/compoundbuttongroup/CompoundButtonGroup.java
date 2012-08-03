package com.codegears.getable.util.compoundbuttongroup;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

public class CompoundButtonGroup implements OnClickListener {

	private ArrayList< CompoundButton > button;
	private CompoundButtonGroupListener listener;
	private boolean isDefaultFirstClicked;

	public CompoundButtonGroup( Boolean setDefaultFirstClicked  ) {
		button = new ArrayList< CompoundButton >();
		isDefaultFirstClicked = setDefaultFirstClicked;
	}

	public void addButton( CompoundButton button ) {
		if( isDefaultFirstClicked ){
			if ( this.button.size() == 0 ) {
				button.setChecked( true );
			} else {
				button.setChecked( false );
			}
		}else{
			button.setChecked( false );
		}
		button.setOnClickListener( this );
		this.button.add( button );
	}

	public void setCompoundButtonGroupListener(CompoundButtonGroupListener listener){
		this.listener = listener;
	}
	
	@Override
	public void onClick( View view ) {
		if( isDefaultFirstClicked ){
			CompoundButton currentCheck = null;
			CompoundButton clicked = (CompoundButton) view;
			for ( CompoundButton cButton : button ) {
				if ( (cButton.isChecked()) && (cButton != clicked) ) {
					currentCheck = cButton;
				}
				cButton.setChecked( false );
			}
			clicked.setChecked( true );
			if ( (currentCheck != null) && (listener != null) ) {
				listener.onButtonGroupClick( clicked );
			}
		}else{
			CompoundButton clicked = (CompoundButton) view;
			for ( CompoundButton cButton : button ) {
				cButton.setChecked( false );
			}
			clicked.setChecked( true );
			if ( listener != null ) {
				listener.onButtonGroupClick( clicked );
			}
		}
	}
}
