package com.besome.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import a.a.a.nA;

public class TextBean extends nA implements Parcelable {
    public static final Parcelable.Creator<TextBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public TextBean createFromParcel(Parcel source) {
            return new TextBean(source);
        }

        @Override
        public TextBean[] newArray(int size) {
            return new TextBean[size];
        }
    };

    public static final int IME_OPTION_DONE = 6;
    public static final int IME_OPTION_GO = 2;
    public static final int IME_OPTION_NEXT = 5;
    public static final int IME_OPTION_NONE = 1;
    public static final int IME_OPTION_NORMAL = 0;
    public static final int IME_OPTION_SEARCH = 3;
    public static final int IME_OPTION_SEND = 4;
    public static final int INPUT_TYPE_NUMBER_DECIMAL = 8194;
    public static final int INPUT_TYPE_NUMBER_SIGNED = 4098;
    public static final int INPUT_TYPE_NUMBER_SIGNED_DECIMAL = 12290;
    public static final int INPUT_TYPE_PASSWORD = 129;
    public static final int INPUT_TYPE_PHONE = 3;
    public static final int INPUT_TYPE_TEXT = 1;
    public static final String TEXT_FONT = "default_font";
    public static final int TEXT_TYPE_BOLD = 1;
    public static final int TEXT_TYPE_BOLDITALIC = 3;
    public static final int TEXT_TYPE_ITALIC = 2;
    public static final int TEXT_TYPE_NORMAL = 0;
    @Expose
    public String text;
    @Expose
    public int textSize;
    @Expose
    public int textColor;
    @Expose
    public int textType;
    @Expose
    public String textFont;
    @Expose
    public String hint;
    @Expose
    public int hintColor;
    @Expose
    public int singleLine;
    @Expose
    public int line;
    @Expose
    public int inputType;
    @Expose
    public int imeOption;

    public TextBean() {
        this.text = "";
        this.textSize = 12;
        this.textType = TEXT_TYPE_NORMAL;
        this.textColor = 0xff000000;
        this.hint = "";
        this.hintColor = 0xff607d8b;
        this.singleLine = 0;
        this.line = 0;
        this.inputType = INPUT_TYPE_TEXT;
        this.imeOption = IME_OPTION_NORMAL;
        this.textFont = TEXT_FONT;
    }

    public TextBean(Parcel parcel) {
        text = parcel.readString();
        textSize = parcel.readInt();
        textColor = parcel.readInt();
        textType = parcel.readInt();
        textFont = parcel.readString();
        hint = parcel.readString();
        hintColor = parcel.readInt();
        singleLine = parcel.readInt();
        line = parcel.readInt();
        inputType = parcel.readInt();
        imeOption = parcel.readInt();
    }

    public static Parcelable.Creator<TextBean> getCreator() {
        return CREATOR;
    }

    public void copy(TextBean textBean) {
        text = textBean.text;
        textSize = textBean.textSize;
        textColor = textBean.textColor;
        textType = textBean.textType;
        textFont = textBean.textFont;
        hint = textBean.hint;
        hintColor = textBean.hintColor;
        singleLine = textBean.singleLine;
        line = textBean.line;
        inputType = textBean.inputType;
        imeOption = textBean.imeOption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isEqual(TextBean textBean) {
        String str = text;
        if (str != null) {
            String str2 = textBean.text;
            if (str2 == null || !str.equals(str2)) {
                return false;
            }
        } else if (textBean.text != null) {
            return false;
        }
        if (textSize != textBean.textSize || textColor != textBean.textColor || textType != textBean.textType) {
            return false;
        }
        String str3 = textFont;
        if (str3 != null) {
            String str4 = textBean.textFont;
            if (str4 == null || !str3.equals(str4)) {
                return false;
            }
        } else if (textBean.textFont != null) {
            return false;
        }
        String str5 = hint;
        if (str5 != null) {
            String str6 = textBean.hint;
            if (str6 == null || !str5.equals(str6)) {
                return false;
            }
        } else if (textBean.hint != null) {
            return false;
        }
        return hintColor == textBean.hintColor && singleLine == textBean.singleLine && line == textBean.line && inputType == textBean.inputType && imeOption == textBean.imeOption;
    }

    public void print() {
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeInt(textSize);
        parcel.writeInt(textColor);
        parcel.writeInt(textType);
        parcel.writeString(textFont);
        parcel.writeString(hint);
        parcel.writeInt(hintColor);
        parcel.writeInt(singleLine);
        parcel.writeInt(line);
        parcel.writeInt(inputType);
        parcel.writeInt(imeOption);
    }
}
