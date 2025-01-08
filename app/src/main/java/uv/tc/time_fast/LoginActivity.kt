package uv.tc.time_fast

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
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
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        accionForgotPassword()
        setupPasswordToggle()
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
        Ion.getDefault(this@LoginActivity).conscryptMiddleware.enable(false)
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
                    Toast.makeText(this@LoginActivity, "Error al Iniciar Sesion", Toast.LENGTH_LONG).show()
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

    private fun accionForgotPassword(){
        binding.tvForgotPassword.setOnClickListener {
            irPantallaPassword()
        }
    }

    private fun irPantallaPassword() {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupPasswordToggle() {
        val passwordEditText = findViewById<EditText>(R.id.et_password)
        val toggleImageView = findViewById<ImageView>(R.id.iv_toggle_password)

        toggleImageView.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleImageView.setImageResource(R.drawable.closed_eye_icon)
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleImageView.setImageResource(R.drawable.open_eye_icon)
            }
            isPasswordVisible = !isPasswordVisible

            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

}