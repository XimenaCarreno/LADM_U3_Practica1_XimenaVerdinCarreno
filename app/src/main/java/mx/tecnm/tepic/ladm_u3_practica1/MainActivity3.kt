package mx.tecnm.tepic.ladm_u3_practica1

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.listaEvidencias
import kotlinx.android.synthetic.main.activity_main4.*

class MainActivity3 : AppCompatActivity() {
    var baseDatos=BaseDatos(this,"bdactividades1",null,1)
    var listaID=ArrayList<String>()
    var idSeleccionadoEnLista=-1
    private var imageUri: Uri?=null
    private val SELECT_ACTIVITY = 50
    var id = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        despliegueEvidencias()

        imageButton.setOnClickListener {
            cargarImagen()
        }

        agregar.setOnClickListener {
            insertar()
        }

        volver.setOnClickListener {
            var intent= Intent(this,MainActivity4::class.java)
            intent.putExtra("idconsultar",editidActivi.text.toString())
            startActivity(intent)
            mensaje("CAMBIANDO A DETALLES")
            startActivity(intent)
            finish()
        }
        volverPrincipal.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            mensaje("MENÚ PRINCIPAL")
            startActivity(ventana)
            finish()
        }
    }

    private fun despliegueEvidencias(){
        var extra=intent.extras
        id=extra!!.getString("id_asociado")!!
        editidActivi.setText(id)
        buscar(id)
    }

    private fun buscar(id: String) {
        var evidencias=ArrayList<String>()
        try
        {
            var base=baseDatos.readableDatabase
            var respuesta=base.query("EVIDENCIAS", arrayOf("*"),"id_actividad=?",
                arrayOf(id),null,null,null,null)
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion="ID: ${respuesta.getInt(0)}" +
                            "\nACTIVIDAD: ${respuesta.getString(1)}" +
                            "\nFOTO: ${respuesta.getBlob(2)}"
                    evidencias.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())
            }
            else
            {
                evidencias.add("NO HAY EVIDENCIAS DISPONIBLES")
            }
            listaEvidencias.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,evidencias)
            this.registerForContextMenu(listaEvidencias)
            listaEvidencias.setOnItemClickListener{ adapterView , view, i, l -> idSeleccionadoEnLista=i
                Toast.makeText(this,"ACTIVIDAD SELECCIONADA", Toast.LENGTH_LONG).show()}
            base.close()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }

    }

    private fun cargarEvidencias() {
        try {
            var trans=baseDatos.readableDatabase
            var evidencias=ArrayList<String>()
            var idActividad = editidActivi.text.toString()
            var respuesta=trans.query("EVIDENCIAS", arrayOf("*"),"id_actividad=?",
                arrayOf(idActividad),null,null,null)
            listaID.clear()
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion="ID: ${respuesta.getInt(0)}" +
                            "\nACTIVIDAD: ${respuesta.getString(1)}" +
                            "\nFOTO: ${respuesta.getBlob(2)}"
                    evidencias.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())
            }
            else
            {
                evidencias.add("NO HAY EVIDENCIAS DISPONIBLES")
            }

            listaEvidencias.adapter=
                ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,evidencias)
            this.registerForContextMenu(listaEvidencias)

            listaEvidencias.setOnItemClickListener{ adapterView , view, i, l -> idSeleccionadoEnLista=i
                Toast.makeText(this,"EVIDENCIA SELECCIONADA", Toast.LENGTH_LONG).show()}
            trans.close()
        }catch (e:SQLiteException)
        {
            mensaje("ERROR "+e.message!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertar() {
        var actividad=editidActivi.text.toString()
        if(actividad=="0")
        {
            mensaje("ACTIVIDAD NO ENCONTRADA")
        }
        else
        {
            try {
                var trans=baseDatos.writableDatabase
                var variables=ContentValues()
                variables.put("id_actividad",editidActivi.text.toString())
                val bytes=this.contentResolver.openInputStream(imageUri!!)?.readBytes()!!
                variables.put("Foto",bytes)
                var resultados=trans.insert("EVIDENCIAS",null,variables)
                if(resultados==-1L)
                {
                    mensaje("FALLO AL INSERTAR EVIDENCIA")
                }
                else
                {
                    mensaje("EVIDENCIA AGREGADA CON ÉXITO")
                }
                trans.close()
                cargarEvidencias()
            }catch (e:SQLiteException)
            {
                mensaje(e.message!!)
            }
        }
    }

    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }

    private fun cargarImagen() {
        ImageController.selectPhotoFromGallery(this,SELECT_ACTIVITY)
        foto.setImageURI(imageUri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when{
            requestCode==SELECT_ACTIVITY && resultCode==Activity.RESULT_OK->{
                imageUri=data!!.data
                foto.setImageURI(imageUri)
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        var inflaterOB=menuInflater
        inflaterOB.inflate(R.menu.menuevid,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(idSeleccionadoEnLista==-1){
            mensaje("POR FAVOR, SELECCIONA UNA EVIDENCIA")
            return true
        }

        when(item.itemId){
            R.id.itemeliminar->{
                var idEliminar=listaID.get(idSeleccionadoEnLista)
                AlertDialog.Builder(this)
                    .setTitle("ATENCIÓN")
                    .setMessage("ESTAS SEGURO DE ELIMINAR LA EVIDENCIA: "+idEliminar+"?")
                    .setPositiveButton("ELIMINAR"){d,i->eliminar(idEliminar)}
                    .setNeutralButton("NO"){d,i->}
                    .show()
            }
            R.id.itemsalir->{
            }
        }
        idSeleccionadoEnLista=-1
        return true
    }
    private fun eliminar(idEliminar:String){
        try {
            var trans=baseDatos.writableDatabase
            var resultado=trans.delete("EVIDENCIAS","idEvidencia=?",
                arrayOf(idEliminar))
            if (resultado==0){
                mensaje("ERROR! FALLO DE EVIDENCIAS")
            }else{
                mensaje("Se logro eliminar con éxito la evidencia")
            }
            trans.close()
            cargarEvidencias()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }
    }
}