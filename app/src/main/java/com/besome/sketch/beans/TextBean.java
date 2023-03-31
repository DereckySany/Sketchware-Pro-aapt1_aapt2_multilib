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

    public static int IME_OPTION_DONE = 6;
    public static int IME_OPTION_GO = 2;
    public static int IME_OPTION_NEXT = 5;
    public static int IME_OPTION_NONE = 1;
    public static int IME_OPTION_NORMAL = 0;
    public static int IME_OPTION_SEARCH = 3;
    public static int IME_OPTION_SEND = 4;
    public static int INPUT_TYPE_NUMBER_DECIMAL = 8194;
    public static int INPUT_TYPE_NUMBER_SIGNED = 4098;
    public static int INPUT_TYPE_NUMBER_SIGNED_DECIMAL = 12290;
    public static int INPUT_TYPE_PASSWORD = 129;
    public static int INPUT_TYPE_PHONE = 3;
    public static int INPUT_TYPE_TEXT = 1;
    public static String TEXT_FONT = "default_font";
    public static int TEXT_TYPE_BOLD = 1;
    public static int TEXT_TYPE_BOLDITALIC = 3;
    public static int TEXT_TYPE_ITALIC = 2;
    public static int TEXT_TYPE_NORMAL;
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
        text = "";
        textSize = 12;
        textType = TEXT_TYPE_NORMAL;
        textColor = 0xff000000;
        hint = "";
        hintColor = 0xff607d8b;
        singleLine = 0;
        line = 0;
        inputType = INPUT_TYPE_TEXT;
        imeOption = IME_OPTION_NORMAL;
        textFont = TEXT_FONT;
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

    // public boolean isEqual(TextBean textBean) {
    //     String currentText = text;
    //     if (currentText != null) {
    //         String otherText = textBean.text;
    //         if (otherText == null || !currentText.equals(otherText)) {
    //             return false;
    //         }
    //     } else if (textBean.text != null) {
    //         return false;
    //     }
    //     if (textSize != textBean.textSize || textColor != textBean.textColor || textType != textBean.textType) {
    //         return false;
    //     }
    //     String currentFont = textFont;
    //     if (currentFont != null) {
    //         String otherFont = textBean.textFont;
    //         if (otherFont == null || !currentFont.equals(otherFont)) {
    //             return false;
    //         }
    //     } else if (textBean.textFont != null) {
    //         return false;
    //     }
    //     String currentHint = hint;
    //     if (currentHint != null) {
    //         String otherHint = textBean.hint;
    //         if (otherHint == null || !currentHint.equals(otherHint)) {
    //             return false;
    //         }
    //     } else if (textBean.hint != null) {
    //         return false;
    //     }
    //     return hintColor == textBean.hintColor && singleLine == textBean.singleLine && line == textBean.line && inputType == textBean.inputType && imeOption == textBean.imeOption;
    // }
    public boolean isEqual(TextBean textBean) {
        String currentText = text;
        String otherText = textBean.text;
        if (currentText == null) {
            if (otherText != null) {
                return false;
            }
        } else if (!currentText.equals(otherText)) {
            return false;
        }
        
        String currentFont = textFont;
        String otherFont = textBean.textFont;
        if (currentFont == null) {
            if (otherFont != null) {
                return false;
            }
        } else if (!currentFont.equals(otherFont)) {
            return false;
        }
        
        String currentHint = hint;
        String otherHint = textBean.hint;
        if (currentHint == null) {
            if (otherHint != null) {
                return false;
            }
        } else if (!currentHint.equals(otherHint)) {
            return false;
        }
        
        return textSize == textBean.textSize &&
            textColor == textBean.textColor &&
            textType == textBean.textType &&
            hintColor == textBean.hintColor &&
            singleLine == textBean.singleLine &&
            line == textBean.line &&
            inputType == textBean.inputType &&
            imeOption == textBean.imeOption;
    }


    public void print() {}

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
