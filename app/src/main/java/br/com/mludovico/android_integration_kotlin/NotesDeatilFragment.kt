package br.com.mludovico.android_integration_kotlin

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.DESCRIPTION_NOTES
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.TITLE_NOTES
import br.com.mludovico.android_integration_kotlin.database.NotesProvider.Companion.URI_NOTES
import kotlinx.android.synthetic.main.note_detail.*

class NotesDeatilFragment: DialogFragment(), DialogInterface.OnClickListener {

    private var id: Long = 0

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
        var newNote = true
        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L) {
            id = arguments?.getLong(EXTRA_ID) as Long
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            if (cursor?.moveToNext() as Boolean) {
                newNote = false
                note_edit_title.setText(cursor.getColumnIndex(TITLE_NOTES))
                note_edit_description.setText(cursor.getColumnIndex(DESCRIPTION_NOTES))
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
        val values = ContentValues()
        values.put(TITLE_NOTES, note_edit_title.text.toString())
        values.put(DESCRIPTION_NOTES, note_edit_description.text.toString())

        if (id != 0L) {
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else {
            context?.contentResolver?.insert(URI_NOTES, values)
        }
    }

}