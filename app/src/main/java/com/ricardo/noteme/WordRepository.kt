package com.ricardo.noteme

import androidx.lifecycle.LiveData

class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Word>> = wordDao.getAlphabetizedWords()
    val allWordItems: LiveData<List<WordItem>> = wordDao.getWordItems()


    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    // This ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.

    suspend fun updateWord(word: String, id: Int,itemId: Int,isActive : Boolean){
        wordDao.updateWord(word,id,itemId,isActive)
    }

    suspend fun update(word: String, id: Int, time : String, color: String, repeat:String){
        wordDao.update(word,id,time,color,repeat)
    }
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

    suspend fun insert(word: WordItem) {
        wordDao.insert(word)
    }

    suspend fun deleteWithId(id : Int){
        wordDao.deleteWithId(id)
    }

    suspend fun deleteItemWithId(id : Int){
        wordDao.deleteItemWithId(id)
    }
}
