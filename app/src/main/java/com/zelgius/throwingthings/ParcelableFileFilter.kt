package com.zelgius.throwingthings

import android.os.Parcel
import android.os.Parcelable
import java.io.File
import java.io.FileFilter
import java.security.cert.Extension

class ParcelableFileFilter(
    val showHidden : Boolean = false,
    val extensions: List<String> = listOf()
) : FileFilter, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList()!!
    )

    override fun accept(file: File): Boolean {
        return (!file.isHidden || showHidden)
                && (extensions.isEmpty() || file.isDirectory || extensions.find { file.name.endsWith(".$it") } != null)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (showHidden) 1 else 0)
        parcel.writeStringList(extensions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableFileFilter> {
        override fun createFromParcel(parcel: Parcel): ParcelableFileFilter {
            return ParcelableFileFilter(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableFileFilter?> {
            return arrayOfNulls(size)
        }
    }


}