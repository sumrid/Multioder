package com.g05.itkmitl.multioder.food;

import android.view.MotionEvent;
import android.view.View;

public interface ItemClickListener {
    void onClick (View view, int position, boolean isLongClick, MotionEvent motionEvent);
}
