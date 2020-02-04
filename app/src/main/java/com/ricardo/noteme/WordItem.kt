package com.ricardo.noteme

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "WordItem_table", primaryKeys = ["wordId","itemID","word"])
@Parcelize
data class WordItem(
                    var wordId : Int,
                    var itemID : Int,
                    var word : String,
                    var isActive : Boolean,
                    var isNew: Boolean): Parcelable