package com.ricardo.noteme

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity(), WordListAdapter.DeleteItemListener {


    private val newWordActivityRequestCode = 1
    private lateinit var wordViewModel: WordViewModel
    private lateinit var manager : AlarmManager
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: WordListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager



        // Get a new or existing ViewModel from the ViewModelProvider.
        wordViewModel = ViewModelProviders.of(this).get(WordViewModel::class.java)
        adapter = WordListAdapter(this,wordViewModel,this)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWords.observe(this, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { adapter.setWords(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        val fab2 = findViewById<FloatingActionButton>(R.id.fab2)
        fab2.setOnClickListener {
            val intent = Intent(this@MainActivity, NewListActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    private fun setTimer( year : Int, month : Int,day : Int,hour : Int,minute : Int, id : Int, mes : String,repeat:String){

        val calendar : Calendar = Calendar.getInstance()
        calendar.set(year,
            month,
            day,
            hour,
            minute,
            0)
        val myIntent = Intent(this@MainActivity, AlarmReceiver::class.java)
        myIntent.putExtra("message",mes)
        myIntent.putExtra("year",year)
        myIntent.putExtra("month",month)
        myIntent.putExtra("day",day)
        myIntent.putExtra("hour",hour)
        myIntent.putExtra("minute",minute)
        myIntent.putExtra("id",id)
        myIntent.putExtra("repeat",repeat)
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, id, myIntent, PendingIntent.FLAG_ONE_SHOT)


        manager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun setCancel( id : Int){


        val myIntent = Intent(this@MainActivity, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this@MainActivity, id, myIntent, PendingIntent.FLAG_ONE_SHOT)


        manager.cancel(pendingIntent)
    }

    override fun onDeleteItemClick(position: Int) {
        //list.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.itemCount)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->

                val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
                var note_counter = 0
                if(data.getBooleanExtra("oldItem",false)){
                    note_counter = data.getIntExtra("counter",0)
                }else{
                    with (sharedPref.edit()) {
                        putInt("NOTEME_COUNTER", getPreferences(Context.MODE_PRIVATE).getInt("NOTEME_COUNTER",0)+1)
                        commit()
                    }
                    note_counter = getPreferences(Context.MODE_PRIVATE).getInt("NOTEME_COUNTER",0)
                }

                if(data.getBooleanExtra("isList",false)){
                    if(data.getBooleanExtra("oldItem",false)){
                        var list = data.getParcelableArrayListExtra<WordItem>("list")
                        wordViewModel.deleteWithId(note_counter)
                        for(i in 0..list.size-1){
                            if(list[i].wordId == note_counter)
                                wordViewModel.insert(list[i])
                        }
                        val word = Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY),data.getStringExtra("time"),data.getStringExtra("color"),
                            intent.getIntExtra("counter",-1),true,data.getStringExtra("repeat"))
                        wordViewModel.update(word.word,note_counter,word.time!!,word.color!!,word.repeat!!)
                    }else{
                        var list = data.getParcelableArrayListExtra<WordItem>("list")
                        for(i in 0..list.size-1){
                            list[i].wordId = note_counter
                            wordViewModel.insert(list[i])
                        }
                        val word = Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY),data.getStringExtra("time"),data.getStringExtra("color"),
                            note_counter,true,data.getStringExtra("repeat"))
                        wordViewModel.insert(word)
                    }
                }else{
                    if(data.getBooleanExtra("isUpdate",false)){
                        wordViewModel.update(data.getStringExtra(NewWordActivity.EXTRA_REPLY),note_counter,data.getStringExtra("time"),data.getStringExtra("color"),data.getStringExtra("repeat"))
                    }else{
                        val word = Word(data.getStringExtra(NewWordActivity.EXTRA_REPLY),data.getStringExtra("time"),data.getStringExtra("color"),
                            note_counter,false,data.getStringExtra("repeat"))
                        wordViewModel.insert(word)
                    }

                }

                if(data.getBooleanExtra("timerOn",false) && data.getBooleanExtra("timerUpdate",false)){
                    setTimer(data.getIntExtra("year",1900),
                        data.getIntExtra("month",0),
                        data.getIntExtra("day",0),
                        data.getIntExtra("hour",0),
                        data.getIntExtra("minute",0),note_counter,data.getStringExtra(NewWordActivity.EXTRA_REPLY),data.getStringExtra("repeat"))
                }else{
                    setCancel(note_counter)
                }
            }
        }
    }
}

