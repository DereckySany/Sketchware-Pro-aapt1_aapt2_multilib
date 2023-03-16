package mod.agus.jcoderz.editor.view.item;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.RadioGroup;

import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.editor.view.item.ItemHorizontalScrollView;
import com.besome.sketch.editor.view.item.ItemVerticalScrollView;

import a.a.a.sy;
import a.a.a.ty;
import a.a.a.wB;

public class ItemRadioGroup extends RadioGroup  {
    private Paint paint;
    private final Rect rect;
    private float paddingFactor;
    private boolean hasSelection;
    private boolean hasFixed;
    private ViewBean viewBean;
    private int gravity;
    private boolean isFixed;


    public ItemRadioGroup(Context context) {
        super(context);
        this.setViewBean(context);
        paddingFactor = wB.a(context, 1.0f);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x9599d5d0);
        rect = new Rect();

        setDrawingCacheEnabled(true);
    }

    public void a() {
        int viewPosition = 0;

        int beanIndex;
        for(int i = 0; viewPosition < this.getChildCount(); i = beanIndex) {
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

    public final void setViewBean(Context context) {
        this.paddingFactor = wB.a(context, 1.0f);
        this.setOrientation(gravity);
        this.setDrawingCacheEnabled(true);
        this.setMinimumWidth((int) wB.a(context, 32.0F));
        this.setMinimumHeight((int)wB.a(context, 32.0F));
        this.paint = new Paint(1);
        this.paint.setStrokeWidth(wB.a(this.getContext(), 2.0F));
    }

    public void addView(View view, int gravity) {
        int childCount = this.getChildCount();
        if (gravity > childCount) {
            super.addView(view);
        } else {
            byte invalido = -1;
            int position = 0;

            int child;
            while(true) {
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

            if (child >= 0 && gravity >= child) {
                super.addView(view, gravity + 1);
            } else {
                super.addView(view, gravity);
            }
        }
    }

    public ViewBean getBean() {
        return this.viewBean;
    }

    public boolean getFixed() {
        return this.isFixed;
    }

    public int getLayoutGravity() {
        return this.gravity;
    }

    public boolean getSelection() {
        return this.hasSelection;
    }

    public void onDraw(Canvas canvas) {
        if (!this.isFixed) {
            if (this.hasSelection) {
                this.paint.setColor(-1785080368);
                rect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRect(new Rect(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight()), this.paint);
            }

            this.paint.setColor(1610612736);
            int width = this.getMeasuredWidth();
            int height = this.getMeasuredHeight();
            float floatWidth = (float) width;
            canvas.drawLine(0.0F, 0.0F, floatWidth, 0.0F, this.paint);
            float floatHeight = (float) height;
            canvas.drawLine(0.0F, 0.0F, 0.0F, floatHeight, this.paint);
            canvas.drawLine(floatWidth, 0.0F, floatWidth, floatHeight, this.paint);
            canvas.drawLine(0.0F, floatHeight, floatWidth, floatHeight, this.paint);
        }

        super.onDraw(canvas);
    }

    public void setBean(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    public void setChildScrollEnabled(boolean scrollEnabled) {
        for(int position = 0; position < this.getChildCount(); ++position) {
            View view = this.getChildAt(position);
            if (view instanceof ty) {
                ((ty) view).setChildScrollEnabled(scrollEnabled);
            }

            if (view instanceof ItemHorizontalScrollView) {
                ((ItemHorizontalScrollView) view).setScrollEnabled(scrollEnabled);
            }

            if (view instanceof ItemVerticalScrollView) {
                ((ItemVerticalScrollView) view).setScrollEnabled(scrollEnabled);
            }
        }

    }

    public void setFixed(boolean hasFixed) {
        this.isFixed = hasFixed;
    }

    public void setLayoutGravity(int LayoutGravity) {
        this.gravity = LayoutGravity;
        super.setGravity(LayoutGravity);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding((int) (left * paddingFactor), (int) (top * paddingFactor), (int) (right * paddingFactor), (int) (paddingFactor * bottom));
    }

    public void setSelection(boolean z) {
        hasSelection = z;
        invalidate();
    }
}
