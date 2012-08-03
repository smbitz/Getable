package com.codegears.getable.ui;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.codegears.getable.R;

public class MainLayoutTest extends LinearLayout {
	
	public interface IKeyboardChanged {
        void onKeyboardShown();
        void onKeyboardHidden();
    }
	
	private ArrayList<IKeyboardChanged> keyboardListener = new ArrayList<IKeyboardChanged>();
	
	public MainLayoutTest(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.main, this);
	}
	
	public void addKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.add(listener);
    }

    public void removeKeyboardStateChangedListener(IKeyboardChanged listener) {
        keyboardListener.remove(listener);
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/*final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);

        largestHeight = Math.max(largestHeight, getHeight());
        
        System.out.println("largestHeight : "+largestHeight);
        System.out.println("proposedheight : "+proposedheight);
        
        if (largestHeight > proposedheight){
            // Keyboard is shown
        	System.out.println("LayoutKeybordShow!!");
        }else{
            // Keyboard is hidden
        	System.out.println("LayoutKeybordHidden!!");
        }
        
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);*/
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        System.out.println("actualHeight : "+actualHeight);
        System.out.println("proposedheight : "+proposedheight);
        
        if (actualHeight > proposedheight) {
            notifyKeyboardShown();
        } else if (actualHeight < proposedheight) {
            notifyKeyboardHidden();
        } else {
        	notifyKeyboardHidden();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private void notifyKeyboardHidden() {
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardHidden();
        }
    }

    private void notifyKeyboardShown() {
        for (IKeyboardChanged listener : keyboardListener) {
            listener.onKeyboardShown();
        }
    }
	
}
