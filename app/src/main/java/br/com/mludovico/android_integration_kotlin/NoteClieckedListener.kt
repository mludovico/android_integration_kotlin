package br.com.mludovico.android_integration_kotlin

import android.database.Cursor

interface NoteClieckedListener {

    fun noteClieckedItem(cursor: Cursor)
    fun noteremovedItem(cursor: Cursor?)

}