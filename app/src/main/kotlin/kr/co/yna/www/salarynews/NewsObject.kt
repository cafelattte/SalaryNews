package kr.co.yna.www.salarynews

import android.os.Parcel
import android.os.Parcelable

class NewsObject(var index: Int, var title: String, var image: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString(), parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeString(title)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsObject> {
        override fun createFromParcel(parcel: Parcel): NewsObject {
            return NewsObject(parcel)
        }

        override fun newArray(size: Int): Array<NewsObject?> {
            return arrayOfNulls(size)
        }
    }

}
