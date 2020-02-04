package com.ricardo.noteme


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_new_word.*
import android.icu.util.Calendar
import android.view.View
import android.widget.*
import android.app.*
import android.graphics.Color
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_new_word.reminder
import kotlinx.android.synthetic.main.activity_new_word.set_timer
import kotlinx.android.synthetic.main.activity_new_word.timer_cb
import android.content.DialogInterface
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




/**
 * Activity for entering a word.
 */

class NewWordActivity : AppCompatActivity() , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private lateinit var editWordView: EditText
    private lateinit var dropdown : Spinner
    private var timerOn = false
    private var time : String? = null
    private var timerUpdate = false
    private lateinit var timePickerDialog : TimePickerDialog
    private lateinit var alertDialog : AlertDialog.Builder
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0
    private var freq = "No Repeat"



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        editWordView = findViewById(R.id.edit_word)
        var datePickerDialog = DatePickerDialog(this, this@NewWordActivity,Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
        timePickerDialog = TimePickerDialog(this, this@NewWordActivity,Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false)
        alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Frequency")
        val types = arrayOf("No Repeat","Daily","Weekly","Monthly")
        alertDialog.setItems(types, object : DialogInterface.OnClickListener {

            override fun onClick(dialog: DialogInterface, which: Int) {

                dialog.dismiss()
                timerOn = true
                timerUpdate = true
                setTime(""+(month+1)+"/"+day+"/"+year+" - "+hour+":"+(if(minute < 10) "0"+minute else minute))
                reminder_ll.visibility = View.VISIBLE
                when (which) {
                    0 -> freq = "No Repeat"
                    1 -> freq = "Daily"
                    2 -> freq = "Weekly"
                    3 -> freq = "Monthly"

                }
                repeat.text = "Repeat: "+freq
            }

        })
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
        val button = findViewById<FloatingActionButton>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val word = editWordView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, word)
                replyIntent.putExtra("color", dropdown.selectedItem.toString())
                if(timerOn && year != 0){
                    replyIntent.putExtra("timerOn",true)
                    replyIntent.putExtra("time",""+(month+1)+"/"+day+"/"+year+" - "+hour+":"+(if(minute < 10) "0"+minute else minute))
                    replyIntent.putExtra("year",year)
                    replyIntent.putExtra("month",month)
                    replyIntent.putExtra("day",day)
                    replyIntent.putExtra("hour",hour)
                    replyIntent.putExtra("minute",minute)
                    replyIntent.putExtra("repeat",freq)

                }else{
                    replyIntent.putExtra("time",if(time==null) "No Reminder" else time)
                }
                replyIntent.putExtra("repeat","No Repeat")
                replyIntent.putExtra("timerUpdate",timerUpdate)
                if(intent.getStringExtra("pos") != null){
                    replyIntent.putExtra("isUpdate",true)
                    replyIntent.putExtra("oldItem",true)
                    replyIntent.putExtra("counter",intent.getIntExtra("listID",0))
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
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        year = p0!!.year
        month = p0!!.month
        day = p0!!.dayOfMonth
        timePickerDialog.show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        hour = p0!!.hour
        minute = p0!!.minute
        //alertDialog.show()
        timerOn = true
        timerUpdate = true
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
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}

