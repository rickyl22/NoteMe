package com.ricardo.noteme

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_new_list.*
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.google.android.material.floatingactionbutton.FloatingActionButton


class NewListActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var editWordView: EditText

    private var list = ArrayList<WordItem>()
    private var cur_item = 0
    private lateinit var dropdown : Spinner
    private var timerOn = false
    private var timerUpdate = false
    private var time : String? = null
    private lateinit var timePickerDialog : TimePickerDialog
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_list)
        editWordView = findViewById(R.id.edit_word)
        var datePickerDialog = DatePickerDialog(this, this@NewListActivity,Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        timePickerDialog = TimePickerDialog(this, this@NewListActivity,Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false)
        setSpiner(intent.getStringExtra("color"))
        time = intent.getStringExtra("time")
        repeat.text = "Repeat: "+intent.getStringExtra("repeat")
        setTime( if(time == null) "No Reminder" else time )
        if(time == null || time.equals("No Reminder",true)){
            reminder_ll.visibility = View.GONE
            timerOn = false
        }else{
            reminder_ll.visibility = View.VISIBLE
            timerOn = true
        }
        btn_back.setOnClickListener {
            finish()
        }
        var listnew : ArrayList<WordItem>? = intent.getParcelableArrayListExtra("list")
        if(listnew != null) list = listnew
        cur_item = getLast(list)
        if(list != null) initList()
        val button = findViewById<FloatingActionButton>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                replyIntent.putParcelableArrayListExtra("list", list)
                replyIntent.putExtra("isList",true)
                replyIntent.putExtra("color", dropdown.selectedItem.toString())
                if(timerOn && year != 0){
                    replyIntent.putExtra("timerOn",true)
                    replyIntent.putExtra("time",""+(month+1)+"/"+day+"/"+year+" - "+hour+":"+ (if(minute < 10) "0"+minute else minute))
                    replyIntent.putExtra("year",year)
                    replyIntent.putExtra("month",month)
                    replyIntent.putExtra("day",day)
                    replyIntent.putExtra("hour",hour)
                    replyIntent.putExtra("minute",minute)

                }else{
                    replyIntent.putExtra("time",if(time==null) "No Reminder" else time)
                }
                replyIntent.putExtra("repeat","No Repeat")
                replyIntent.putExtra("timerUpdate",timerUpdate)
                if(intent.getIntExtra("listID",-1) != -1){
                    replyIntent.putExtra("oldItem", true)
                    replyIntent.putExtra("counter", intent.getIntExtra("listID",-1))
                }
                setResult(Activity.RESULT_OK, replyIntent)
            }
            //setTimer()
            finish()
        }
        val placeholder = intent.getStringExtra("pos")
        if(placeholder != null){
            editWordView.setText(placeholder)
        }
        val add = findViewById<RelativeLayout>(R.id.add_new)

        set_timer.setOnClickListener {
            datePickerDialog.show()
        }
        timer_cb.setOnClickListener {
            timerOn = false
            if(!timerOn){
                setTime("No Reminder")
                reminder_ll.visibility = View.GONE
                timerUpdate = false
                time = "No Reminder"
            }
        }

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    editWordView.setBackgroundColor(Color.WHITE)
                    et_cardview.setCardBackgroundColor(Color.WHITE)
                }else if(position == 1){
                    editWordView.setBackgroundColor(getColor(R.color.colorBlue))
                    et_cardview.setCardBackgroundColor(getColor(R.color.colorBlue))
                }else if(position == 2){
                    editWordView.setBackgroundColor(getColor(R.color.colorRed))
                    et_cardview.setCardBackgroundColor(getColor(R.color.colorRed))
                }else if(position == 3){
                    editWordView.setBackgroundColor(getColor(R.color.colorGreen))
                    et_cardview.setCardBackgroundColor(getColor(R.color.colorGreen))
                }else if(position == 4){
                    editWordView.setBackgroundColor(getColor(R.color.colorYellow))
                    et_cardview.setCardBackgroundColor(getColor(R.color.colorYellow))
                }
            }

        }

        add.setOnClickListener {
            var rel =  LayoutInflater.from(this).inflate(R.layout.item_new, null)
            var et = rel.findViewById<EditText>(R.id.et)
            var deletebtn = rel.findViewById<ImageView>(R.id.delete)
            val cur = cur_item
            deletebtn.setOnClickListener(){
                rel.visibility = View.GONE
                for(i in 0..list.size-1){
                    if(list[i].itemID ==  cur){
                        list.removeAt(i)
                        break
                    }
                }

            }

            var nextWord = WordItem(intent.getIntExtra("listID",0),cur_item,et.text.toString(),true,true)
            et.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    nextWord.word = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    nextWord.word = s.toString()
                }
            })
            var cross = rel.findViewById<ImageView>(R.id.cross)
            cross.setOnClickListener {
                if(nextWord.isActive){
                    et.setPaintFlags(et.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    nextWord.isActive = false
                }else{
                    et.setPaintFlags(0)
                    nextWord.isActive = true
                }

            }
            list.add(nextWord)
            cur_item++
            list_item.addView(rel)
            et.requestFocus()

        }


    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        year = p0!!.year
        month = p0!!.month
        day = p0!!.dayOfMonth
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        timerOn = true
        timerUpdate = true
        hour = p0!!.hour
        minute = p0!!.minute
        setTime(""+(month+1)+"/"+day+"/"+year+" - "+hour+":"+(if(minute < 10) "0"+minute else minute))
        reminder_ll.visibility = View.VISIBLE
    }


    private fun setSpiner(color : String?){
        dropdown = findViewById(R.id.spinner1)
        val items = arrayOf("White", "Blue", "Red","Green","Yellow")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, items)
        dropdown.setAdapter(adapter)
        if(color != null){
            dropdown.setSelection(items.indexOf(color))
            if(color.equals("Blue",true)){
                et_cardview.setCardBackgroundColor(getColor(R.color.colorBlue))
                editWordView.setBackgroundColor(getColor(R.color.colorBlue))
            }else if(color.equals("Yellow",true)){
                et_cardview.setCardBackgroundColor(getColor(R.color.colorYellow))
                editWordView.setBackgroundColor(getColor(R.color.colorYellow))
            }else if(color.equals("Red",true)){
                et_cardview.setCardBackgroundColor(getColor(R.color.colorRed))
                editWordView.setBackgroundColor(getColor(R.color.colorRed))
            }else if(color.equals("Green",true)){
                et_cardview.setCardBackgroundColor(getColor(R.color.colorGreen))
                editWordView.setBackgroundColor(getColor(R.color.colorGreen))
            }else{
                et_cardview.setCardBackgroundColor(Color.WHITE)
                editWordView.setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun setTime(text : String?){
        if(text.equals("No Reminder",true)){
            reminder_ll.visibility = View.GONE
        }else{
            reminder_ll.visibility = View.VISIBLE
            reminder.setText("Reminder: "+text)
        }    }

    private fun initList(){
        val listid = intent.getIntExtra("listID",0)
        for(i in 0..list.size-1){
            if(listid == list[i].wordId) {
                var rel = LayoutInflater.from(this).inflate(R.layout.item_new, null)
                var et = rel.findViewById<EditText>(R.id.et)
                var deletebtn = rel.findViewById<ImageView>(R.id.delete)
                val cur = list[i].itemID
                deletebtn.setOnClickListener {
                    rel.visibility = View.GONE
                    for (i in 0..list.size-1) {
                        if (list.get(i).itemID == cur) {
                            list.removeAt(i)
                            break
                        }
                    }

                }
                if(!list[i].isActive){
                    et.setPaintFlags(et.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                }else{
                    et.setPaintFlags(0)
                }
                et.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(s: Editable) {
                        list[i].word = s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence, start: Int,
                                                   count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence, start: Int,
                                               before: Int, count: Int) {
                    }
                })
                var cross = rel.findViewById<ImageView>(R.id.cross)
                cross.setOnClickListener {
                    if(list[i].isActive){
                        et.setPaintFlags(et.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                        list[i].isActive = false
                    }else{
                        et.setPaintFlags(0)
                        list[i].isActive = true
                    }

                }
                et.setText(list.get(i).word)
                list_item.addView(rel)
            }
        }
    }

    private fun getLast(list : List<WordItem>) : Int{
        var max = 0
        for(i in 0..list.size-1){
            max = Math.max(max,list.get(i).itemID)
        }
        return max + 1
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}