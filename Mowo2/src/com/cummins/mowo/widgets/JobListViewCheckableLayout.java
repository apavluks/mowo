package com.cummins.mowo.widgets;

import com.cummins.mowo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class JobListViewCheckableLayout extends RelativeLayout implements Checkable {

	private boolean checked = false;
	private RadioButton radioBtn;
	
	public JobListViewCheckableLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	
	public JobListViewCheckableLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}


	public JobListViewCheckableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        radioBtn = (RadioButton)findViewById(R.id.selector); // optimisation - you don't need to search for image view every time you want to reference it
    }

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	public void setChecked(boolean checked) {
        this.checked = checked;
        if (radioBtn.getVisibility() == View.VISIBLE) {
        	radioBtn.setChecked(checked);
        }
		
	}

	@Override
	public void toggle() {
		setChecked(!checked);
		
	}
	

}
