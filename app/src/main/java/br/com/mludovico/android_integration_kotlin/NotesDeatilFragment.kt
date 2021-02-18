package br.com.mludovico.android_integration_kotlin

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import br.com.mludovico.android_integration_kotlin.database.NotesProvider.Companion.URI_NOTES
import kotlinx.android.synthetic.main.note_detail.*

class NotesDeatilFragment: DialogFragment(), DialogInterface.OnClickListener {

    private var id: Long = 0
    lateinit var titleEdit: EditText
    lateinit var descriptionEdit: EditText

    companion object {
        private const val EXTRA_ID = "id"
        fun newInstance(id: Long): NotesDeatilFragment {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)

            val notesFragment = NotesDeatilFragment()
            notesFragment.arguments = bundle
            return notesFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.note_detail, null)
        titleEdit = view!!.findViewById(R.id.note_edit_title)
        descriptionEdit = view!!.findViewById(R.id.note_edit_description)
        var newNote = true
        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L) {
            id = arguments?.getLong(EXTRA_ID) as Long
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            if (cursor?.moveToNext() as Boolean) {
                newNote = false
                titleEdit.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
                descriptionEdit.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
            }
            cursor.close()
        }
        return AlertDialog.Builder(activity as Activity)
            .setTitle(if (newNote) "Nova nota" else "Editar nota")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if(which == -2)
            return
        val values = ContentValues()
        values.put(TITLE_NOTES, titleEdit.text.toString())
        values.put(DESCRIPTION_NOTES, descriptionEdit.text.toString())

        if (id != 0L) {
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else {
            context?.contentResolver?.insert(URI_NOTES, values)
        }
    }

}