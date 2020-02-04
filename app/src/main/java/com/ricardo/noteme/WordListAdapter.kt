package com.ricardo.noteme

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import androidx.cardview.widget.CardView
import android.app.AlertDialog
import android.content.DialogInterface




class WordListAdapter internal constructor(
   val context: Context,
   val wordViewModel : WordViewModel,
   val deleteItemListener : DeleteItemListener
) : RecyclerView.Adapter<WordListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var words = emptyList<Word>() // Cached copy of words
    private lateinit var list : ArrayList<WordItem>

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        list = ArrayList()
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = words[position]
        notify
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        wordViewModel.allWordItems.observe(context as LifecycleOwner, Observer { words ->
            // Update the cached copy of the words in the adapter.
            words?.let { list.addAll(it) }
        })
        holder.wordItemView.text = current.word
        holder.itemView.setOnClickListener {
            if(!current.isList) {
                var intent = Intent(context, NewWordActivity::class.java)
                intent.putExtra("pos", current.word)
                intent.putExtra("listID",current.listID)
                intent.putExtra("color",current.color)
                intent.putExtra("time",current.time)
                intent.putExtra("repeat",current.repeat)
                startActivityForResult(context as Activity, intent, 1,null)
            }else{
                list.clear()
                wordViewModel.allWordItems.observe(context as LifecycleOwner, Observer { words ->
                    // Update the cached copy of the words in the adapter.
                    words?.let { list.addAll(it) }
                })
                var intent = Intent(context, NewListActivity::class.java)
                intent.putExtra("pos", current.word)
                intent.putExtra("listID",current.listID)
                intent.putExtra("color",current.color)
                intent.putExtra("time",current.time)
                intent.putExtra("repeat",current.repeat)
                intent.putParcelableArrayListExtra("list",list)
                startActivityForResult(context as Activity,intent, 1,null)
            }
        }
        holder.wordItemDel.setOnClickListener {
            val builder1 = AlertDialog.Builder(context)
            builder1.setMessage("Are you sure you want to delete this note?")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, id ->
                    wordViewModel.deleteItemWithId(current.listID)
                    deleteItemListener.onDeleteItemClick(position)
                })

            builder1.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

            val alert11 = builder1.create()
            alert11.show()

        }
        if(current.color.equals("Blue",true)){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.colorBlue))
        }else if(current.color.equals("Yellow",true)){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.colorYellow))
        }else if(current.color.equals("Red",true)){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.colorRed))
        }else if(current.color.equals("Green",true)){
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.colorGreen))
        }else{
            holder.cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    internal fun setWords(words: List<Word>) {
        this.words = words
        notifyDataSetChanged()
    }

    override fun getItemCount() = words.size

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordItemView: TextView = itemView.findViewById(R.id.textView)
        val wordItemDel: ImageView = itemView.findViewById(R.id.delete)
        val cardView : CardView = itemView.findViewById(R.id.card_item_cardview)

    }
    interface DeleteItemListener {

        fun onDeleteItemClick(position : Int)
    }
}
