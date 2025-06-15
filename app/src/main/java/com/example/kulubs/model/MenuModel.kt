package com.example.kulubs.model

import android.os.Parcel
import android.os.Parcelable

data class MenuModel(
    val id: Int = 0,
    val nama: String = "",
    val deskripsi: String = "",
    val harga: Double = 0.0,
    val kategori: String = "",
    val tersedia: Boolean = true,
    val gambarPath: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nama)
        parcel.writeString(deskripsi)
        parcel.writeDouble(harga)
        parcel.writeString(kategori)
        parcel.writeByte(if (tersedia) 1 else 0)
        parcel.writeString(gambarPath)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<MenuModel> {
        override fun createFromParcel(parcel: Parcel): MenuModel = MenuModel(parcel)
        override fun newArray(size: Int): Array<MenuModel?> = arrayOfNulls(size)
    }
}