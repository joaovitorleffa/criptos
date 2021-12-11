package com.lumertz.cripto

import android.os.Parcel
import android.os.Parcelable

data class CryptoTransaction(
    val id: Int?,
    val name: String?,
    val code: String?,
    val amount: Double,
    val value: Double,
    val total: Double,
    val date: String?
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeDouble(amount)
        parcel.writeDouble(value)
        parcel.writeDouble(total)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CryptoTransaction> {
        override fun createFromParcel(parcel: Parcel): CryptoTransaction {
            return CryptoTransaction(parcel)
        }

        override fun newArray(size: Int): Array<CryptoTransaction?> {
            return arrayOfNulls(size)
        }
    }

}