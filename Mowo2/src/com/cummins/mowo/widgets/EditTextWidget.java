package com.cummins.mowo.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cummins.mowo.R;

public class EditTextWidget extends LinearLayout

{

	private TextView labelTextView;
	private TextView textTextView;
	private Context context;

	public EditTextWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_labeled_textview, this, true);

		labelTextView = (TextView) findViewById(R.id.formitem_label_textview);
		textTextView = (TextView) findViewById(R.id.formitem_label_edittext);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.EditTextWidget);

		// set text value 
		String inputText = a.getString(R.styleable.EditTextWidget_inputText);
		textTextView.setText(inputText);
		
		// set text value 
		String labelText = a.getString(R.styleable.EditTextWidget_labelText);
		labelTextView.setText(labelText);

		//set input text size 
		int textSize = a.getDimensionPixelOffset(
				R.styleable.EditTextWidget_inputTextSize, 0);
		if (textSize > 0) {
			textTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
		}
		
		//set input text size 
		int labelPadding = a.getDimensionPixelOffset(
				R.styleable.EditTextWidget_labelPadding, 0);
		if (labelPadding > 0) {
		    labelTextView.setPadding(0, 0, labelPadding, 0);
		}
		
		

		a.recycle();
	}

	public TextView getLabel() {
		return labelTextView;
	}

	public void setLabel(TextView label) {
		this.labelTextView = label;
	}

	public TextView getText() {
		return textTextView;
	}

	public void setText(TextView text) {
		this.textTextView = text;
	}

}
