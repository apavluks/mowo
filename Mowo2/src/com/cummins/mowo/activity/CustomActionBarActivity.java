package com.cummins.mowo.activity;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CustomActionBarActivity extends ActionBarActivity {

	/*
	 * I need this behavior for all of my activities, so I created a class
	 * CustomActivity inheriting from the class Activity and "hooked" the
	 * dispatchTouchEvent function. There are mainly two conditions to take care
	 * of: 1. If focus is unchanged and someone is tapping outside of the
	 * current input field, then dismiss the IME 2. If focus has changed and the
	 * next focused element isn't an instance of any kind of an input field,
	 * then dismiss the IME
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_UP) {
			final View view = getCurrentFocus();

			if (view != null) {
				final boolean consumed = super.dispatchTouchEvent(ev);

				final View viewTmp = getCurrentFocus();
				final View viewNew = viewTmp != null ? viewTmp : view;

				if (viewNew.equals(view)) {
					final Rect rect = new Rect();
					final int[] coordinates = new int[2];

					view.getLocationOnScreen(coordinates);

					rect.set(coordinates[0], coordinates[1], coordinates[0]
							+ view.getWidth(),
							coordinates[1] + view.getHeight());

					final int x = (int) ev.getX();
					final int y = (int) ev.getY();

					if (rect.contains(x, y)) {
						return consumed;
					}
				} else if (viewNew instanceof EditText) {
					return consumed;
				}

				final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputMethodManager.hideSoftInputFromWindow(
						viewNew.getWindowToken(), 0);

				viewNew.clearFocus();

				return consumed;
			}
		}

		return super.dispatchTouchEvent(ev);
	}

}
