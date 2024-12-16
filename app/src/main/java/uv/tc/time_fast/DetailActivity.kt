package uv.tc.time_fast

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.adapter.PackageAdapter
import uv.tc.time_fast.adapter.ShippingAdapter
import uv.tc.time_fast.databinding.ActivityDetailBinding
import uv.tc.time_fast.poko.Colaborador
import uv.tc.time_fast.poko.Envio
import uv.tc.time_fast.poko.Mensaje
import uv.tc.time_fast.poko.Paquete
import uv.tc.time_fast.poko.Posee
import uv.tc.time_fast.util.Constantes
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var envio: Envio
    private lateinit var colaborador: Colaborador
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        serializarDatosEnvio(intent.getStringExtra("envio"))
        cargarDatosEnvio()
        serializarDatosColaborador(intent.getStringExtra("colaborador"))
        editarEstatus()
        configurarRecyclerEnvios()
    }

    override fun onStart() {
        super.onStart()
        cargarDatosEnvio()
        obtenerPaquetes()
    }

    private fun serializarDatosEnvio(json: String?){
        val gson = Gson()
        envio = gson.fromJson(json, Envio::class.java)
    }

    private fun serializarDatosColaborador(json: String?){
        val gson = Gson()
        colaborador = gson.fromJson(json, Colaborador::class.java)
    }

    private fun cargarDatosEnvio() {
        binding.tvTitleEnvio.text = "Envio: " + " '" + envio.numeroGuia + "'"
        binding.tvOrigen.text = envio.calleOrigen + " #" + envio.numeroOrigen + ", " + envio.coloniaOrigen + ", " + envio.ciudadOrigen + ", " + envio.estadoOrigen
        binding.tvDestino.text = envio.calleDestino + " #" + envio.numeroDestino + ", " + envio.coloniaDestino + ", " + envio.ciudadDestino + ", " + envio.estadoDestino
        binding.tvEstatus.text = envio.estatus
        binding.tvNombreCliente.text = envio.cliente
        binding.tvCorreoCliente.text = envio.correo
        binding.tvTelefonoCliente.text = envio.telefono
    }

    private fun editarEstatus(){
        binding.ivEditarEstatus.setOnClickListener {
            val inflater = layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_estatus, null)

            dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            val ivEnTransito = dialogView.findViewById<ImageView>(R.id.iv_en_transito)
            val ivEntregado = dialogView.findViewById<ImageView>(R.id.iv_entregado)
            val ivCancelado = dialogView.findViewById<ImageView>(R.id.iv_cancelado)
            val ivDetenido = dialogView.findViewById<ImageView>(R.id.iv_detenido)
            val btnActualizar = dialogView.findViewById<Button>(R.id.btn_actualilzar)
            val etMotivo = dialogView.findViewById<EditText>(R.id.et_motivo)
            var idEstatus: Int = -1

            ivEnTransito.setOnClickListener {
                idEstatus = 2
                ivEnTransito.setBackgroundResource(R.drawable.depth_background)
                ivEntregado.setBackgroundResource(R.drawable.iv_not_selected)
                ivCancelado.setBackgroundResource(R.drawable.iv_not_selected)
                ivDetenido.setBackgroundResource(R.drawable.iv_not_selected)
            }

            ivEntregado.setOnClickListener {
                idEstatus = 4
                ivEnTransito.setBackgroundResource(R.drawable.iv_not_selected)
                ivEntregado.setBackgroundResource(R.drawable.depth_background)
                ivCancelado.setBackgroundResource(R.drawable.iv_not_selected)
                ivDetenido.setBackgroundResource(R.drawable.iv_not_selected)
            }

            ivCancelado.setOnClickListener {
                idEstatus = 5
                ivEnTransito.setBackgroundResource(R.drawable.iv_not_selected)
                ivEntregado.setBackgroundResource(R.drawable.iv_not_selected)
                ivCancelado.setBackgroundResource(R.drawable.depth_background)
                ivDetenido.setBackgroundResource(R.drawable.iv_not_selected)
            }

            ivDetenido.setOnClickListener {
                idEstatus = 3
                ivEnTransito.setBackgroundResource(R.drawable.iv_not_selected)
                ivEntregado.setBackgroundResource(R.drawable.iv_not_selected)
                ivCancelado.setBackgroundResource(R.drawable.iv_not_selected)
                ivDetenido.setBackgroundResource(R.drawable.depth_background)
            }

            btnActualizar.setOnClickListener {
                if(idEstatus > 0) {
                    var valido = true
                    if ((idEstatus == 5 && etMotivo.text.isEmpty()) || (idEstatus == 3 && etMotivo.text.isEmpty())) {
                        etMotivo.error = "Este campo es obligatorio"
                        valido = false
                    }
                    if(valido){
                        val posee = Posee(null,
                        etMotivo.text.toString(),
                        colaborador.nombre + " " + colaborador.apellidoPaterno + " " + colaborador.apellidoMaterno,
                        obtenerFechaHoraActual(), envio.id.toString().toInt(), idEstatus)
                        actualizarEstatusEnvio(posee)
                    }
                }else
                    Toast.makeText(this@DetailActivity, "Seleccione un Estatus", Toast.LENGTH_LONG).show()
            }
            dialog?.show()
        }
    }

    private fun actualizarEstatusEnvio(posee: Posee) {
        Ion.getDefault(this@DetailActivity).conscryptMiddleware.enable(false)
        Ion.with(this@DetailActivity)
            .load("PUT", "${Constantes().URL_WS}envio/actualizarEstatus")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBodyParameter("idEstatus", posee.idEstatus.toString())
            .setBodyParameter("id", posee.idEnvio.toString())
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    serializarInformacion(result, posee)
                } else {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarInformacion(result: String, posee: Posee) {
        val gson = Gson()
        val respuestaActualizarEstatus = gson.fromJson(result, Mensaje::class.java)
        if( !respuestaActualizarEstatus.error ){
            Toast.makeText(this@DetailActivity, respuestaActualizarEstatus.mensaje, Toast.LENGTH_LONG).show()
            val json = gson.toJson(posee)
            historialEstatus(json)
            dialog?.dismiss()
        } else {
            Toast.makeText(this@DetailActivity, respuestaActualizarEstatus.mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private fun historialEstatus(parametros: String) {
        Log.i("Error", parametros)
        Ion.getDefault(this@DetailActivity).conscryptMiddleware.enable(false)
        Ion.with(this@DetailActivity)
            .load("POST", "${Constantes().URL_WS}posee/historialEstatus")
            .setHeader("Content-Type", "application/json")
            .setStringBody(parametros)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    Log.i("Historial", "Almacenado")
                } else {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun obtenerFechaHoraActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        formato.timeZone = TimeZone.getTimeZone("America/Mexico_City")
        val fechaHora = Date()
        return formato.format(fechaHora)
    }

    private fun obtenerPaquetes() {
        Ion.getDefault(this@DetailActivity).conscryptMiddleware.enable(false)
        Ion.with(this@DetailActivity)
            .load("GET", "${Constantes().URL_WS}paquete/obtenerPaquetesEnvio/${envio.id}")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    serializarPaquetes(result)
                } else {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarPaquetes(json: String) {
        val gson = Gson()
        val type = object : TypeToken<List<Paquete>>() {}.type
        val listaPaquetes: List<Paquete> = gson.fromJson(json, type)
        if (listaPaquetes.isNotEmpty()) {
            cargarEnvios(listaPaquetes)
        } else {
            mostrarMensajeSinPaquetes()
        }
    }

    private fun configurarRecyclerEnvios() {
        binding.rvContenidoEnvio.layoutManager = LinearLayoutManager(this@DetailActivity)
        binding.rvContenidoEnvio.setHasFixedSize(true)
    }

    private fun cargarEnvios(paquetes: List<Paquete>) {
        binding.tvSinPaquetes.visibility = View.INVISIBLE
        binding.tvSinPaquetes.visibility = View.INVISIBLE
        binding.rvContenidoEnvio.visibility = View.VISIBLE
        binding.rvContenidoEnvio.adapter = PackageAdapter(paquetes)
    }

    private fun mostrarMensajeSinPaquetes() {
        binding.tvSinPaquetes.visibility = View.VISIBLE
        binding.tvSinPaquetes.visibility = View.VISIBLE
        binding.rvContenidoEnvio.visibility = View.INVISIBLE
    }


}