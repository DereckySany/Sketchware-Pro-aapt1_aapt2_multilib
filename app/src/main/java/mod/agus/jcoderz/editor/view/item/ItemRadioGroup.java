package mod.agus.jcoderz.editor.view.item;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import com.besome.sketch.beans.ViewBean;

import a.a.a.sy;
import a.a.a.wB;

public class ItemRadioGroup extends LinearLayout implements sy {
    private ViewBean viewBean;
    private Paint paint;
    private Rect rect;
    private int paddingFactor;
    private int gravity;
    private boolean hasSelection;
    private boolean isFixed;

    public ItemRadioGroup(Context context) {
        super(context);
        this.a(context);
    }

    public final void a(Context context) {
        this.setOrientation(0);
        this.setDrawingCacheEnabled(true);
        this.paint = new Paint(1);
        this.paddingFactor = (int) wB.a(context, 1.0f);
    }

    public void a() {
        int viewPosition = 0;
        int beanIndex;
        for (int i = 0; viewPosition < this.getChildCount(); i = beanIndex) {
            View view = this.getChildAt(viewPosition);
            beanIndex = i;
            if (view instanceof sy) {
                ((sy) view).getBean().index = i;
                beanIndex = i + 1;
            }
            ++viewPosition;
        }
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public void setLayoutGravity(int LayoutGravity) {
        this.gravity = LayoutGravity;
        super.setGravity(LayoutGravity);
    }

    public int getLayoutGravity() {
        return this.gravity;
    }

    public void addView(View view, int num) {
        int childCount = this.getChildCount();
        if (num > childCount) {
            super.addView(view);
        } else {
            byte invalido = -1;
            int position = 0;

            int child;
            while (true) {
                child = invalido;
                if (position >= childCount) {
                    break;
                }

                if (this.getChildAt(position).getVisibility() == View.GONE) {
                    child = position;
                    break;
                }

                ++position;
            }

            if (child >= 0 && num >= child) {
                super.addView(view, num + 1);
            } else {
                super.addView(view, num);
            }
        }
    }

    @Override
    public ViewBean getBean() {
        return viewBean;
    }

    @Override
    public void setBean(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    @Override
    public boolean getFixed() {
        return isFixed;
    }

    public void setFixed(boolean z) {
        isFixed = z;
    }

    public boolean getSelection() {
        return hasSelection;
    }

    @Override
    public void setSelection(boolean z) {
        hasSelection = z;
        invalidate();
    }
    @Override
    public void onDraw(Canvas canvas) {
        if (hasSelection) {
            rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRect(rect, paint);
        }
        super.onDraw(canvas);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left * paddingFactor, top * paddingFactor,right * paddingFactor, paddingFactor * bottom);
    }
}