package mx.tecnm.tepic.ladm_u3_practica1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            var ventana= Intent(this,MainActivity2::class.java)
            mensaje("CAMBIANDO A ACTIVIDADES")
            startActivity(ventana)
            finish()

        }

        button2.setOnClickListener {
            var intent= Intent(this,MainActivity3::class.java)
            intent.putExtra("id_asociado","0")
            mensaje("CAMBIANDO A EVIDENCIAS")
            startActivity(intent)
            finish()
        }

        button3.setOnClickListener {
            finish()
        }
    }
    private fun mensaje(s:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCIÓN")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->d.dismiss()}
            .show()
    }
}