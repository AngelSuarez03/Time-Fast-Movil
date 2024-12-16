package uv.tc.time_fast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.databinding.ActivityLoginBinding
import uv.tc.time_fast.poko.Login
import uv.tc.time_fast.util.Constantes

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

     binding = ActivityLoginBinding.inflate(layoutInflater)
     setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        binding.btnLogin.setOnClickListener{
            val numeroPersonal = binding.etUser.text.toString()
            val password = binding.etPassword.text.toString()
            if(sonCamposValidos(numeroPersonal, password))
                verificarCredenciales(numeroPersonal, password)
        }
    }

    private fun sonCamposValidos(numeroPersonal: String, password: String) : Boolean {
        var camposValido = true
        if(numeroPersonal.isEmpty()){
            camposValido = false
            binding.etUser.error = "Correo Electrónico Obligatorio"
        }
        if(password.isEmpty()){
            camposValido = false
            binding.etPassword.error = "Contraseña Obligatoria"
        }
        return camposValido
    }

    private fun verificarCredenciales(numeroPersonal: String, password: String) {
        //configuracion biblioteca solo la primera vez
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)
        //consumo de ws
        Ion.with(this@LoginActivity)
            .load("POST", "${Constantes().URL_WS}login/validarCredenciales")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBodyParameter("numeroPersonal", numeroPersonal)
            .setBodyParameter("password", password)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    serializarInformacion(result)
                } else {
                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarInformacion(json: String) {
        val gson = Gson()
        val respuestaLogin = gson.fromJson(json, Login::class.java)
        if (!respuestaLogin.error && respuestaLogin.colaborador.rol.equals("Conductor"))
            irPantallaPrincipal(json)
        if(!respuestaLogin.error && respuestaLogin.colaborador.rol != "Conductor")
            Toast.makeText(this@LoginActivity, "No posee los permisos necesarios para acceder", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this@LoginActivity, respuestaLogin.mensaje, Toast.LENGTH_LONG).show()
    }

    private fun irPantallaPrincipal(json: String) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("colaborador", json)
        startActivity(intent)
        finish()
    }

}