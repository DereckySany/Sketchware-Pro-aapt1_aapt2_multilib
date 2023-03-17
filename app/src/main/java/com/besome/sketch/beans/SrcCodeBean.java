package com.besome.sketch.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class SrcCodeBean implements Parcelable {

    public static final Parcelable.Creator<SrcCodeBean> CREATOR = new Parcelable.Creator<>() {
        @Override
        public SrcCodeBean createFromParcel(Parcel source) {
            return new SrcCodeBean(source);
        }

        @Override
        public SrcCodeBean[] newArray(int size) {
            return new SrcCodeBean[size];
        }
    };

    public final String pkgName;
    public final String source;
    public final String srcFileName;

    public SrcCodeBean() {}

    public SrcCodeBean(String srcFileName, String content) {
        this.srcFileName = srcFileName;
        this.source = content;
    }

    public SrcCodeBean(Parcel source) {
        this.pkgName = source.readString();
        this.srcFileName = source.readString();
        this.source = source.readString();
    }

    public static Parcelable.Creator<SrcCodeBean> getCreator() {
        return CREATOR;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pkgName);
        dest.writeString(srcFileName);
        dest.writeString(source);
    }
}
