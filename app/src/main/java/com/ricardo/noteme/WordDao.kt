package com.ricardo.noteme

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface WordDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from word_table ORDER BY word ASC")
    fun getAlphabetizedWords(): LiveData<List<Word>>

    @Query("SELECT * from WordItem_table ")
    fun getWordItems(): LiveData<List<WordItem>>

    @Query("UPDATE word_table SET word=:word,time = :time, color = :color, repeat = :repeat WHERE listID = :id")
    suspend fun update(word: String, id: Int, time : String, color: String, repeat: String)

    @Query("UPDATE WordItem_table SET word=:word,isActive=:isActive WHERE wordId = :id AND itemID = :itemId")
    suspend fun updateWord(word: String, id: Int,itemId: Int,isActive : Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: WordItem)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()

    @Query("DELETE FROM WordItem_table")
    suspend fun deleteAllItems()

    @Query("DELETE FROM WordItem_table WHERE wordId = :id")
    suspend fun deleteWithId(id : Int)

    @Query("DELETE FROM word_table WHERE listID = :id")
    suspend fun deleteItemWithId(id : Int)
}