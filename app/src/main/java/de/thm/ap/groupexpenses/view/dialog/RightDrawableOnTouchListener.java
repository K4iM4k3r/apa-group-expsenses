package de.thm.ap.groupexpenses.view.dialog;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

abstract class RightDrawableOnTouchListener implements View.OnTouchListener {
    private Drawable drawable;
    private final int FUZZ = 10;

    RightDrawableOnTouchListener(TextView view) {
        super();
        final Drawable[] drawables = view.getCompoundDrawables();
        if (drawables.length == 4)
            this.drawable = drawables[2];
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final Rect bounds = drawable.getBounds();
            if (x >= (v.getRight() - bounds.width() - FUZZ) && x <= (v.getRight() - v.getPaddingRight() + FUZZ)
                    && y >= (v.getPaddingTop() - FUZZ) && y <= (v.getHeight() - v.getPaddingBottom()) + FUZZ) {
                return onDrawableTouch(event);
            }
        }
        return false;
    }

    public abstract boolean onDrawableTouch(final MotionEvent event);

}
