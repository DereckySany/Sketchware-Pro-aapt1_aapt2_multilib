package com.besome.sketch.editor;

import static mod.SketchwareUtil.getDip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.lib.ui.CustomScrollView;
import com.sketchware.remod.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import a.a.a.Us;
import a.a.a.wB;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.Tools;

public class LogicEditorDrawer extends LinearLayout {

    private LinearLayout favorite;
    private CustomScrollView scrollView;
    private SharedPreferences sharedpref;
    private boolean ascendingOrder = true;

    public LogicEditorDrawer(Context context) {
        super(context);
        initialize(context);
        loadPreferences();
    }

    public LogicEditorDrawer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
        loadPreferences();
    }

    public void setDragEnabled(boolean dragEnabled) {
        if (dragEnabled) {
            scrollView.b();
        } else {
            scrollView.a();
        }
    }

    private void initialize(Context context) {
        wB.a(context, this, R.layout.logic_editor_drawer);
        ((TextView) findViewById(R.id.tv_block_collection)).setText(Helper.getResString(R.string.logic_editor_title_block_collection));
        favorite = findViewById(R.id.layout_favorite);
        scrollView = findViewById(R.id.scv);
        sharedpref = getContext().getSharedPreferences("collection_order_pref", 0);

        ImageButton ascendingOrdernation = findViewById(R.id.sort_collection);
        CardView tools = findViewById(R.id.new_button);
        tools.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Tools.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ((LogicEditorActivity) getContext()).startActivityForResult(intent, 463);
        });

        findViewById(R.id.sort_collection).setOnClickListener(v -> {
            sortCollections();
            Drawable drawable = getContext().getDrawable(ascendingOrder ? R.drawable.selector_ic_expand_more_24 : R.drawable.selector_ic_expand_less_24);
            ascendingOrdernation.setImageDrawable(drawable);
            setPreference("ascendingOrder",ascendingOrder);
        });
    }
    private void loadPreferences() {
        sharedpref = getContext().getSharedPreferences("collection_order_pref", Activity.MODE_PRIVATE);
        ascendingOrder = sharedpref.getBoolean("ascendingOrder", false);
    }
    private void setPreference(String key, boolean value) {
        sharedpref.edit().putBoolean(key, value).apply();
    }
    private void sortCollections() {
        ArrayList<Us> collections = new ArrayList<>();
        for (int i = 0; i < favorite.getChildCount(); i++) {
            View childAt = favorite.getChildAt(i);
            if (childAt instanceof Us) {
                collections.add((Us) childAt);
            }
        }

        if (ascendingOrder) {
            Collections.sort(collections, new Comparator<Us>() {
                @Override
                public int compare(Us o1, Us o2) {
                    return o1.T.compareToIgnoreCase(o2.T);
                }
            });
            ascendingOrder = false;
        } else {
            Collections.sort(collections, new Comparator<Us>() {
                @Override
                public int compare(Us o1, Us o2) {
                    return o2.T.compareToIgnoreCase(o1.T);
                }
            });
            ascendingOrder = true;
        }

        favorite.removeAllViews();
        for (Us collection : collections) {
            favorite.addView(collection);
            View view = new View(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    1,
                    (int) getDip(8)));
            favorite.addView(view);
        }
    }
    public void a() {
        favorite.removeAllViews();
    }

    public boolean z(){
        if (ascendingOrder) {
            return scrollView.fullScroll(View.FOCUS_DOWN);
        } else {
            return scrollView.fullScroll(View.FOCUS_UP);
        }
    }

    public View a(String str, ArrayList<BlockBean> arrayList) {
        Us collectionBlock = null;
        if (arrayList.size() > 0) {
            BlockBean blockBean = arrayList.get(0);
            collectionBlock = new Us(getContext(), blockBean.type, blockBean.typeName, blockBean.opCode, str, arrayList);
            favorite.addView(collectionBlock);
            View view = new View(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    1,
                    (int) getDip(8)));
            favorite.addView(view);
        }
//        z();
        return collectionBlock;
    }

    public void a(String str) {
        for (int i = 0; i < favorite.getChildCount(); i++) {
            View childAt = favorite.getChildAt(i);
            if ((childAt instanceof Us) && ((Us) childAt).T.equals(str)) {
                favorite.removeViewAt(i + 1);
                favorite.removeViewAt(i);
            }
        }
    }
}
