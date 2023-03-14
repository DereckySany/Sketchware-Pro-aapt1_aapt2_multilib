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

public class ItemRadioGroup extends RadioGroup implements sy, ty {
    public ViewBean viewBean = null;
    public int paddingFactor;
    public boolean hasSelection = false;
    public boolean isFixed = false;
    public Paint paint;
    public int gravity = 0;

    public ItemRadioGroup(Context context) {
        super(context);
        this.setViewBean(context);
    }

    public void a() {
        int var1 = 0;

        int var4;
        for(int var2 = 0; var1 < this.getChildCount(); var2 = var4) {
            View var3 = this.getChildAt(var1);
            var4 = var2;
            if (var3 instanceof sy) {
                ((sy)var3).getBean().index = var2;
                var4 = var2 + 1;
            }

            ++var1;
        }

    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public final void setViewBean(Context context) {
        this.paddingFactor = (int) wB.a(context, 1.0f);
        this.setOrientation(gravity);
        this.setDrawingCacheEnabled(true);
        this.setMinimumWidth((int) wB.a(context, 32.0F));
        this.setMinimumHeight((int)wB.a(context, 32.0F));
        this.paint = new Paint(1);
        this.paint.setStrokeWidth(wB.a(this.getContext(), 2.0F));
    }

    public void addView(View var1, int var2) {
        int var3 = this.getChildCount();
        if (var2 > var3) {
            super.addView(var1);
        } else {
            byte var4 = -1;
            int var5 = 0;

            int var6;
            while(true) {
                var6 = var4;
                if (var5 >= var3) {
                    break;
                }

                if (this.getChildAt(var5).getVisibility() == View.GONE) {
                    var6 = var5;
                    break;
                }

                ++var5;
            }

            if (var6 >= 0 && var2 >= var6) {
                super.addView(var1, var2 + 1);
            } else {
                super.addView(var1, var2);
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

    public void onDraw(Canvas var1) {
        if (!this.isFixed) {
            if (this.hasSelection) {
                this.paint.setColor(-1785080368);
                var1.drawRect(new Rect(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight()), this.paint);
            }

            this.paint.setColor(1610612736);
            int var2 = this.getMeasuredWidth();
            int var3 = this.getMeasuredHeight();
            float var4 = (float)var2;
            var1.drawLine(0.0F, 0.0F, var4, 0.0F, this.paint);
            float var5 = (float)var3;
            var1.drawLine(0.0F, 0.0F, 0.0F, var5, this.paint);
            var1.drawLine(var4, 0.0F, var4, var5, this.paint);
            var1.drawLine(0.0F, var5, var4, var5, this.paint);
        }

        super.onDraw(var1);
    }

    public void setBean(ViewBean var1) {
        this.viewBean = var1;
    }

    public void setChildScrollEnabled(boolean var1) {
        for(int var2 = 0; var2 < this.getChildCount(); ++var2) {
            View var3 = this.getChildAt(var2);
            if (var3 instanceof ty) {
                ((ty)var3).setChildScrollEnabled(var1);
            }

            if (var3 instanceof ItemHorizontalScrollView) {
                ((ItemHorizontalScrollView)var3).setScrollEnabled(var1);
            }

            if (var3 instanceof ItemVerticalScrollView) {
                ((ItemVerticalScrollView)var3).setScrollEnabled(var1);
            }
        }

    }

    public void setFixed(boolean var1) {
        this.isFixed = var1;
    }

    public void setLayoutGravity(int var1) {
        this.gravity = var1;
        super.setGravity(var1);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left * paddingFactor, top * paddingFactor, right * paddingFactor, paddingFactor * bottom);
    }

    public void setSelection(boolean z) {
        this.hasSelection = z;
        this.invalidate();
    }
}
