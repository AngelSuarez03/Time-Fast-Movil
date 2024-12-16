package uv.tc.time_fast

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.databinding.ActivityProfileEditBinding
import uv.tc.time_fast.poko.Colaborador
import uv.tc.time_fast.poko.Mensaje
import uv.tc.time_fast.poko.Posee
import uv.tc.time_fast.util.Constantes
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileEditBinding
    lateinit var colaborador: Colaborador
    var fotoPerfilBytes : ByteArray ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        serializarColaborador(intent.getStringExtra("colaborador").toString())
        cargarDatos()
        actualizarDatos()
    }

    override fun onStart() {
        super.onStart()
        obtenerFotoCliente(colaborador.id)
        binding.ibEditProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            selectorFotoPerfil.launch(intent)
        }
    }

    private fun serializarColaborador(json: String) {
        val gson = Gson()
        colaborador = gson.fromJson(json, Colaborador::class.java)
    }

    private fun cargarDatos(){
        binding.etNombre.setText(colaborador.nombre)
        binding.etApellidoPaterno.setText(colaborador.apellidoPaterno)
        binding.etApellidoMaterno.setText(colaborador.apellidoMaterno)
        binding.etCurp.setText(colaborador.curp)
        binding.tvNumPersonal.text = colaborador.numeroPersonal
        binding.etCorreo.setText(colaborador.correo)
        binding.etPassword.setText(colaborador.password)
    }

    private fun validarDatos(): Boolean{
        var valido = true
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        if (binding.etNombre.text.isEmpty()){
            binding.etNombre.error = "Campo Obligatorio"
            valido = false
        }
        if (binding.etApellidoPaterno.text.isEmpty()){
            binding.etApellidoPaterno.error = "Campo Obligatorio"
            valido = false
        }
        if (binding.etApellidoMaterno.text.isEmpty()){
            binding.etApellidoMaterno.error = "Campo Obligatorio"
            valido = false
        }
        if (binding.etCurp.text.isEmpty()){
            binding.etCurp.error = "Campo Obligatorio"
            valido = false
        }
        if (binding.etCorreo.text.isEmpty()){
            binding.etCorreo.error = "Campo Obligatorio"
            valido = false
        }
        if (binding.etPassword.text.isEmpty()){
            binding.etPassword.error = "Campo Obligatorio"
            valido = false
        }
        if (!binding.etCorreo.text.matches(emailPattern.toRegex())) {
            binding.etCorreo.error = "Formato Inválido"
            valido = false
        }
        return valido
    }

    private fun actualizarDatos() {
            binding.btnActualizar.setOnClickListener {
                if ( validarDatos() ) {
                    val colaboradorEditado = Colaborador(
                        colaborador.id,
                        binding.etNombre.text.toString(),
                        binding.etApellidoPaterno.text.toString(),
                        binding.etApellidoMaterno.text.toString(),
                        binding.etCorreo.text.toString(),
                        binding.etCurp.text.toString(),
                        colaborador.numeroPersonal,
                        binding.etPassword.text.toString(),
                        null,
                        colaborador.idRol,
                        colaborador.idUnidad,
                        null,
                        null,
                        colaborador.rol,
                        null
                    )
                    val gson = Gson()
                    val parametros = gson.toJson(colaboradorEditado)
                    peticionActualizacion(parametros)
                }
        }
    }

    private fun peticionActualizacion(parametros: String){
        Ion.getDefault(this@ProfileEditActivity).conscryptMiddleware.enable(false)
        Ion.with(this@ProfileEditActivity)
            .load("PUT", "${Constantes().URL_WS}colaborador/editarColaborador")
            .setHeader("Content-Type", "application/json")
            .setStringBody(parametros)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    serializarInformacion(result)
                } else {
                    Toast.makeText(this@ProfileEditActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarInformacion(result: String) {
        val gson = Gson()
        val respuestaActualizar = gson.fromJson(result, Mensaje::class.java)
        if( !respuestaActualizar.error ){
            Toast.makeText(this@ProfileEditActivity, respuestaActualizar.mensaje, Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this@ProfileEditActivity, respuestaActualizar.mensaje, Toast.LENGTH_LONG).show()
        }
    }

    //Seleccion foto
    private val selectorFotoPerfil = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result : ActivityResult ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imgURI = data?.data
            if(imgURI != null) {
                //Imagen recibida
                fotoPerfilBytes = uriToByteArray(imgURI)
                if(fotoPerfilBytes != null)
                    subirFotoPerfil(colaborador.id)
            }
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun subirFotoPerfil(idColaborador: Int) {
        Ion.with(this@ProfileEditActivity)
            .load("PUT", "${Constantes().URL_WS}colaborador/subirFoto/${idColaborador}")
            .setByteArrayBody(fotoPerfilBytes)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    val gson = Gson()
                    val msj = gson.fromJson(result, Mensaje::class.java)
                    Toast.makeText(this@ProfileEditActivity, msj.mensaje, Toast.LENGTH_LONG).show()
                    if( !msj.error ) {
                        obtenerFotoCliente(idColaborador)
                    }
                } else {
                    Toast.makeText(this@ProfileEditActivity, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun obtenerFotoCliente(idColaborador: Int) {
        Ion.with(this@ProfileEditActivity)
            .load("GET","${Constantes().URL_WS}colaborador/obtenerFoto/${idColaborador}")
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    cargarFotoColaborador(result)
                } else {
                    Toast.makeText(this@ProfileEditActivity, "Error: " + e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun cargarFotoColaborador(json: String) {
        if(json.isNotEmpty()){
            val gson = Gson()
            val colaboradorFoto = gson.fromJson(json, Colaborador::class.java)
            if(colaboradorFoto.foto != null){
                try {
                    val imgByte = Base64.decode(colaboradorFoto.foto, Base64.DEFAULT)
                    val imgBitMap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
                    binding.ivProfile.setImageBitmap(imgBitMap)
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileEditActivity, "Error img: " + e.message, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@ProfileEditActivity, "No cuentas con foto de perfil", Toast.LENGTH_LONG).show()
            }
        }
    }

}