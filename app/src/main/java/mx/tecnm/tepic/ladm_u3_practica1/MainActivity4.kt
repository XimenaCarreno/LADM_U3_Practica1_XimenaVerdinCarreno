package mx.tecnm.tepic.ladm_u3_practica1

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
import kotlinx.android.synthetic.main.activity_main4.*

class MainActivity4 : AppCompatActivity() {
    var baseDatos=BaseDatos(this,"bdactividades1",null,1)
    var id=""
    var listaID=ArrayList<String>()
    var idSeleccionadoEnLista=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        despliegueActividades()
        actividades.setOnClickListener {
            var ventana= Intent(this,MainActivity2::class.java)
            mensaje("CAMBIANDO A ACTIVIDADES")
            startActivity(ventana)
            finish()
        }

        evidencias.setOnClickListener {
            var intent= Intent(this,MainActivity3::class.java)
            id=editIdAct.text.toString()
            intent.putExtra("id_asociado",id)
            mensaje("CAMBIANDO A EVIDENCIAS")
            startActivity(intent)
            finish()
        }

        buscar.setOnClickListener {
            resDesc.setText("Descripción: ")
            resCaptura.setText("Fecha de Captura: ")
            resEntrega.setText("Fecha de Entrega: ")
            id=editIdAct.text.toString()
            buscar(id)
        }

        eliminar.setOnClickListener {
            eliminacion()
        }
    }

    private fun eliminacion() {
        var ideliminar = editIdAct.text.toString()
        if(ideliminar=="0")
        {
            mensaje("ERROR. Primero especifica la actividad a eliminar")
        }
        else
        {
            try{
                var trans=baseDatos.writableDatabase
                var borraracti=trans.delete("ACTIVIDADES","id_actividad=?", arrayOf(ideliminar))
                var borrarevid=trans.delete("EVIDENCIAS","id_actividad=?", arrayOf(ideliminar))
                if(borraracti==0 || borrarevid==0)
                {
                    mensaje("ERROR. Sucedio un problema al eliminar la actividad")
                }
                else
                {
                    mensaje("La actividad se eliminó con éxito")
                    resDesc.setText("Descripción: ")
                    resCaptura.setText("Fecha de Captura: ")
                    resEntrega.setText("Fecha de Entrega: ")
                }
                trans.close()
                cargarEvidencias()
            }
            catch (e:SQLiteException){

            }
        }
    }

    private fun cargarEvidencias() {
        try {
            var trans=baseDatos.readableDatabase
            var evidencias=ArrayList<String>()
            var idActividad = editIdAct.text.toString()
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
                Toast.makeText(this,"ACTIVIDAD SELECCIONADA", Toast.LENGTH_LONG).show()}
            trans.close()
        }catch (e:SQLiteException)
        {
            mensaje("ERROR "+e.message!!)
        }
    }


    private fun despliegueActividades() {
        var extra=intent.extras
        id=extra!!.getString("idconsultar")!!
        editIdAct.setText(id)
        buscar(id)
    }

    private fun buscar(id: String) {
        try
        {
            var base=baseDatos.readableDatabase
            var respuesta=base.query("ACTIVIDADES", arrayOf("Descripcion","FechaCaptura","FechaEntrega"),"id_actividad=?",
                arrayOf(id),null,null,null,null)
            if (respuesta.moveToFirst()){
                resDesc.setText(resDesc.text.toString()+respuesta.getString(0))
                resCaptura.setText(resCaptura.text.toString()+respuesta.getString(1))
                resEntrega.setText(resEntrega.text.toString()+respuesta.getString(2))
            }
            else{
                mensaje("ESPECIFICA EL ID DE LA ACTIVIDAD A BUSCAR")
                editIdAct.setText("")
            }
            base.close()
            cargarEvidencias()
        }catch (e:SQLiteException){
            mensaje(e.message!!)
        }

    }

    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
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