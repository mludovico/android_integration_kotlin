package br.com.mludovico.android_integration_kotlin.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns._ID
import androidx.annotation.RequiresApi
import br.com.mludovico.android_integration_kotlin.database.NotesDatabaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)
        if (context != null) {
            dbHelper = NotesDatabaseHelper(context as Context)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffected = db.delete(TABLE_NOTES, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffected
        } else {
            throw UnsupportedSchemeException("Uri inválida para esclusão")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Uri não implementado")

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mUriMatcher.match(uri) == NOTES) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(TABLE_NOTES, null, values)
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return  insertUri
        } else {
            throw UnsupportedSchemeException("Uri inválida para inserção")
        }
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when {
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(
                    TABLE_NOTES,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
                cursor.setNotificationUri(context?.contentResolver, uri)
                return cursor
            }
            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor = db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                return cursor
            }
            else -> throw UnsupportedSchemeException("Uri não implementado")
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffected = db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffected
        } else throw UnsupportedSchemeException("Uri não implementada")
    }

    companion object {
        const val AUTHORITY = "br.com.mludovico.android_integration_kotlin.provider"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY")
        val URI_NOTES: Uri = Uri.withAppendedPath(BASE_URI, "notes")

        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}