package br.com.mludovico.android_integration_kotlin

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import kotlinx.android.synthetic.main.note_item.view.*

class NotesAdapter(private val listener: NoteClieckedListener): RecyclerView.Adapter<NotesViewHolder>() {

    private var cursor: Cursor? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        holder.itemView.note_title.text = cursor?.getString(cursor?.getColumnIndex(TITLE_NOTES) as Int)
        holder.itemView.note_description.text = cursor?.getString(cursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)
        holder.itemView.note_button_remove.setOnClickListener {
            cursor?.moveToPosition(position)
            listener.noteRemovedItem(cursor)
            notifyDataSetChanged()
        }
        holder.itemView.setOnClickListener { listener.noteClieckedItem(cursor as Cursor) }
    }

    override fun getItemCount(): Int = if (cursor != null) cursor?.count as Int else 0

    fun setCursor(newCursor: Cursor?) {
        cursor = newCursor
        notifyDataSetChanged()
    }
}

class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)