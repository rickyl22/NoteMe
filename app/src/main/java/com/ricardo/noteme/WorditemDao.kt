package com.ricardo.noteme

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface WordItemDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.

    @Query("SELECT * from WordItem_table ")
    fun getWordItems(): LiveData<List<WordItem>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: WordItem)


    @Query("DELETE FROM WordItem_table")
    suspend fun deleteAll()
}