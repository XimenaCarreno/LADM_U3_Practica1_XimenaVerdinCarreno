package mx.tecnm.tepic.ladm_u3_practica1

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var baseDatos=BaseDatos(this,"bdactividades1",null,1)
    var listaID=ArrayList<String>()
    var idSeleccionadoEnLista=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        agregar.setOnClickListener {
            insertar()
        }

        consultar.setOnClickListener {
            var intent= Intent(this,MainActivity4::class.java)
            intent.putExtra("idconsultar","0")
            mensaje("CAMBIANDO A DETALLES")
            startActivity(intent)
            finish()
        }

        volver.setOnClickListener {
            var ventana= Intent(this,MainActivity::class.java)
            mensaje("MENU PRINCIPAL")
            startActivity(ventana)
            finish()
        }
        cargarActividades()
    }

    private fun insertar() {
        try{
            var trans=baseDatos.writableDatabase
            var variables=ContentValues()
            variables.put("Descripcion",editDescripcion.text.toString())
            variables.put("FechaCaptura",editFechaCaptura.text.toString())
            variables.put("FechaEntrega",editFechaEntrega.text.toString())

            var respuesta=trans.insert("ACTIVIDADES",null,variables)
            if(respuesta==-1L){
                mensaje("ERROR AL AGREGAR LA ACTIVIDAD")
            }
            else
            {
                mensaje("ACTIVIDAD AGREGADA CON EXITO")
                limpiarCampos()
            }
            trans.close()

        }catch (e:SQLiteException)
        {
            mensaje(e.message!!)
        }
        cargarActividades()
    }

    private fun limpiarCampos() {
        editDescripcion.setText("")
        editFechaCaptura.setText("")
        editFechaEntrega.setText("")
    }

    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }

    private fun cargarActividades() {
        try {
            var trans=baseDatos.readableDatabase
            var actividades=ArrayList<String>()
            var respuesta=trans.query("ACTIVIDADES", arrayOf("*"),null,null,null,null,null)
            listaID.clear()
            if(respuesta.moveToFirst()){
                do{
                    var concatenacion="ID: ${respuesta.getInt(0)}" +
                            "\nDESCRIPCIÓN: ${respuesta.getString(1)}" +
                            "\nFECHA DE CAPTURA: ${respuesta.getString(2)}" +
                            "\nFECHA DE ENTREGA: ${respuesta.getString(3)}"
                    actividades.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())
                }while (respuesta.moveToNext())
            }
            else
            {
                actividades.add("NO HAY ACTIVIDADES DISPONIBLES")
            }

            listaActividades.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,actividades)
            this.registerForContextMenu(listaActividades)

            listaActividades.setOnItemClickListener{ adapterView , view, i, l -> idSeleccionadoEnLista=i
            Toast.makeText(this,"ACTIVIDAD SELECCIONADA",Toast.LENGTH_LONG).show()}
            trans.close()
        }catch (e:SQLiteException)
        {
            mensaje("ERROR "+e.message!!)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        var inflaterOB=menuInflater
        inflaterOB.inflate(R.menu.menuppal,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if(idSeleccionadoEnLista==-1){
            mensaje("POR FAVOR, SELECCIONA UNA ACTIVIDAD")
            return true
        }

        when(item.itemId){
            R.id.itemVerEvidencias->{
                var intent= Intent(this,MainActivity4::class.java)
                intent.putExtra("idconsultar",listaID.get(idSeleccionadoEnLista))
                startActivity(intent)
            }
            R.id.itemsalir->{
            }
        }
        idSeleccionadoEnLista=-1
        return true
    }
}