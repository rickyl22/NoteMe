package com.ricardo.noteme

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "word_table")
@Parcelize
data class Word(@PrimaryKey @ColumnInfo(name = "word") val word: String,
                var time : String?,
                var color : String?,
                var listID : Int,
                var isList: Boolean,
                var repeat: String):Parcelable


