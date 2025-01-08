package uv.tc.time_fast

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.databinding.ActivityForgotPasswordBinding
import uv.tc.time_fast.poko.Mensaje
import uv.tc.time_fast.util.Constantes
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding
    lateinit var password: String
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recuperarPassword()
        irPantallaLogin()
    }

    private fun obtenerPassword(email: String, onPasswordReceived: (String) -> Unit) {
        Ion.getDefault(this@ForgotPasswordActivity).conscryptMiddleware.enable(false)
        Ion.with(this@ForgotPasswordActivity)
            .load("GET", "${Constantes().URL_WS}colaborador/recuperarContrasenia/$email")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    onPasswordReceived(result)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Error al obtener la contraseña: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun recuperarPassword() {
        binding.btnForgotPassword.setOnClickListener {
            if (validarCampo()) {
                verificarCorreoExistente()
            }
        }
    }

    private fun irPantallaLogin() {
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validarCampo(): Boolean {
        var valido = true
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        if( binding.etEmail.text.isEmpty()){
            binding.etEmail.error = "Campo Obligatorio"
            valido = false
        }
        if (!binding.etEmail.text.matches(emailPattern.toRegex())) {
            binding.etEmail.error = "Formato Inválido"
            valido = false
        }
        return valido
    }

    private fun validarCorreoExistente(mensaje: String) {
        val gson = Gson()
        val respuesta = gson.fromJson(mensaje, Mensaje::class.java)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_forgot_password, null)

        dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
            .setView(dialogView)
            .create()

        val ivIconoEstadoPeticion = dialogView.findViewById<ImageView>(R.id.iv_icono_estado_petición)
        val tvTextoPeticion = dialogView.findViewById<TextView>(R.id.tv_texto_peticion)

        if(respuesta.mensaje == "Colaborador existente") {
            ivIconoEstadoPeticion.setImageResource(R.drawable.check_icon)
            tvTextoPeticion.text = "Correo enviado con éxito."
            val email = binding.etEmail.text.toString()
            obtenerPassword(email) { password ->
                enviarCorreoSMTP(email, "Recuperar Contraseña", password)
            }
        } else {
            ivIconoEstadoPeticion.setImageResource(R.drawable.canceled_icon)
            tvTextoPeticion.text = "El correo ingresado no esta registrado."
        }
        dialog?.show()
    }

    private fun verificarCorreoExistente() {
        Ion.getDefault(this@ForgotPasswordActivity).conscryptMiddleware.enable(false)
        Ion.with(this@ForgotPasswordActivity)
            .load("GET", "${Constantes().URL_WS}colaborador/correoExistente/${binding.etEmail.text}")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    validarCorreoExistente(result)
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun enviarCorreoSMTP(destinatario: String, asunto: String, password: String) {
        val correo = "angnusa11@gmail.com"
        val contraseña = "drah qmqz potd xqmy"

        val propiedades = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val sesion = Session.getInstance(propiedades, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(correo, contraseña)
            }
        })

        try {
            val message = MimeMessage(sesion)
            message.setFrom(InternetAddress("angnusa11@gmail.com"))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(destinatario)
            )
            message.subject = asunto
            message.setContent(mensajeCorreo(password), "text/html; charset=utf-8")

            thread {
                Transport.send(message)
            }
        } catch (e: MessagingException) {
            e.printStackTrace()
            println("Error al enviar el correo: ${e.message}")
        }
    }

    private fun mensajeCorreo(password: String): String {
        val mensajeHTML = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperación de Contraseña</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 600px;
            margin: 30px auto;
            background: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        .header {
            background-color: #203F59;
            color: white;
            text-align: center;
            padding: 20px;
            font-size: 24px;
        }
        .body {
            padding: 20px;
            color: #333333;
            line-height: 1.5;
        }
        .footer {
            background-color: #f1f1f1;
            text-align: center;
            padding: 15px;
            font-size: 12px;
            color: #888888;
        }
        .button {
            display: inline-block;
            margin: 20px 0;
            padding: 10px 20px;
            font-size: 16px;
            color: #ffffff;
            background-color: #4CAF50;
            text-decoration: none;
            border-radius: 5px;
        }
        .button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            Recuperación de Contraseña
        </div>
        <div class="body">
            <p>Hola,</p>
            <p>Recibimos una solicitud para recuperar tu contraseña. A continuación, te proporcionamos el dato:</p>
            <p><strong>Correo electrónico:</strong> ${binding.etEmail.text}</p>
            <p><strong>Contraseña:</strong> ${password}</p>
            <p>Si no realizaste esta solicitud, por favor ignora este correo.</p>
            <p>¡Gracias por confiar en nosotros!</p>
        </div>
        <div class="footer">
            &copy; 2025 - TIME - FAST. Todos los derechos reservados.
        </div>
    </div>
</body>
</html>
""".trimIndent()
    return mensajeHTML
    }
}