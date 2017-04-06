package com.rosa.swift.core.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by inurlikaev on 31.05.2016.
 */
public class ExpandedListView extends ListView {
    private android.view.ViewGroup.LayoutParams params;
    private int old_count = 0;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getCount() != old_count) {
            params = getLayoutParams();
            old_count = getCount();
            int totalHeight = 0;
            for (int i = 0; i < getCount(); i++) {
                this.measure(0, 0);
                totalHeight += getMeasuredHeight();
            }

            params = getLayoutParams();
            params.height = totalHeight + (getDividerHeight() * (getCount() - 1));
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }
}
