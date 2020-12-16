package mx.tecnm.tepic.ladm_u3_practica1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name:String?,
    factory: SQLiteDatabase.CursorFactory?,
    version:Int
): SQLiteOpenHelper(context,name,factory,version){
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ACTIVIDADES(id_actividad INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Descripcion VARCHAR(2000),FechaCaptura DATE, FechaEntrega DATE)")
        db.execSQL("CREATE TABLE EVIDENCIAS(idEvidencia INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, id_actividad INTEGER REFERENCES ACTIVIDADES(id_actividad),Foto BLOB)")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

}