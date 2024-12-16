package uv.tc.time_fast

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class ProfileEditActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileEditBinding
    lateinit var colaborador: Colaborador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        serializarColaborador(intent.getStringExtra("colaborador").toString())
        cargarDatos()
        actualizarDatos()
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

}