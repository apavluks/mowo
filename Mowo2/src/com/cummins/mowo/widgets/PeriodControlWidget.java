package com.cummins.mowo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cummins.mowo.R;

public class PeriodControlWidget extends RelativeLayout

{

	private TextView valueTV;
	private TextView labelTV;

	public PeriodControlWidget(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_period_control_button, this, true);

		valueTV = (TextView) findViewById(R.id.value_textview);
		labelTV = (TextView) findViewById(R.id.label_textview);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ClockButtonWidget);

		String valueText = a.getString(R.styleable.ClockButtonWidget_valueText);
		setValueText(valueText);
		

        int textSize = a.getDimensionPixelOffset(R.styleable.ClockButtonWidget_valueTextSize, 0);
        if (textSize > 0) {
            setValueTextSize(textSize);
        }		
        
        String clockBtnFont = context.getResources().getString(R.string.clock_button_font);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), clockBtnFont);
        setTypeFace(tf);
        
		a.recycle();
	}

	public void setTypeFace(Typeface tf) {
		valueTV.setTypeface(tf);
	}

	public void setValueText(String param) {
		valueTV.setText(param);
	}
	
	public void setValueTextSize(int param) {
		valueTV.setTextSize(param);
	}

	public void setLabelText(String param) {
		labelTV.setText(param);
	}
	
}
