package mx.tecnm.tepic.ladm_u3_practica1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

object ImageController {
    fun selectPhotoFromGallery(activity: Activity, code:Int){
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/"
        activity.startActivityForResult(intent,code)
    }
    fun saveImage(context: Context, uri: Uri){
        val file= File(context.filesDir,null)
        val bytes=context.contentResolver.openInputStream(uri)?.readBytes()!!
        return file.writeBytes(bytes)
    }
}