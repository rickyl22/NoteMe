package com.ricardo.noteme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WordRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Word>>
    val allWordItems: LiveData<List<WordItem>>


    init {
        val wordsDao = WordRoomDatabase.getDatabase(application, viewModelScope).wordDao()
        repository = WordRepository(wordsDao)
        allWords = repository.allWords
        allWordItems = repository.allWordItems

    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun updateWord(word: String,id:Int,itemId : Int,isActive:Boolean) = viewModelScope.launch {
        repository.updateWord(word,id,itemId,isActive)
    }

    fun update(word: String,id:Int,time:String,color:String,repeat:String) = viewModelScope.launch {
        repository.update(word,id,time,color,repeat)
    }

    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }

    fun insert(word: WordItem) = viewModelScope.launch {
        repository.insert(word)
    }

    fun deleteWithId(id: Int) = viewModelScope.launch {
        repository.deleteWithId(id)
    }

    fun deleteItemWithId(id: Int) = viewModelScope.launch {
        repository.deleteItemWithId(id)
    }
}
