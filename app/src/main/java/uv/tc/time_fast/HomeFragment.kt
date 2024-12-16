package uv.tc.time_fast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.adapter.ShippingAdapter
import uv.tc.time_fast.databinding.FragmentHomeBinding
import uv.tc.time_fast.databinding.FragmentProfileBinding
import uv.tc.time_fast.interfaces.ListenerRecycleEnvios
import uv.tc.time_fast.poko.Colaborador
import uv.tc.time_fast.poko.Envio
import uv.tc.time_fast.poko.Login
import uv.tc.time_fast.util.Constantes

class HomeFragment : Fragment(R.layout.fragment_home), ListenerRecycleEnvios {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var colaborador: Colaborador
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel.colaborador.observe(viewLifecycleOwner) { json ->
            if (json != null) {
                serializarDatosColaborador(json)
                obtenerEnvios()
            }
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecyclerEnvios()
        listenerLogout()
    }

    override fun onResume() {
        super.onResume()
        obtenerEnvios()
    }

    private fun configurarRecyclerEnvios() {
        binding.rvEnvios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEnvios.setHasFixedSize(true)
    }

    private fun obtenerEnvios() {
        Ion.getDefault(requireContext()).conscryptMiddleware.enable(false)
        Ion.with(requireContext())
            .load("GET", "${Constantes().URL_WS}envio/obtenerEnviosColaborador/${colaborador.id}")
            .asString()
            .setCallback { e, result ->
                if (e == null) {
                    serializarEnvios(result)
                } else {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarDatosColaborador(json: String) {
        val gson = Gson()
        val respuestaLogin = gson.fromJson(json, Login::class.java)
        colaborador = respuestaLogin.colaborador
    }

    private fun serializarEnvios(json: String) {
        val gson = Gson()
        val type = object : TypeToken<List<Envio>>() {}.type
        val listaEnvios: List<Envio> = gson.fromJson(json, type)
        if (listaEnvios.isNotEmpty()) {
            cargarEnvios(listaEnvios)
        } else {
            mostrarMensajeSinEnvios()
        }
    }

    private fun cargarEnvios(envios: List<Envio>) {
        binding.tvSinEnvios.visibility = View.INVISIBLE
        binding.ivSinEnvios.visibility = View.INVISIBLE
        binding.rvEnvios.visibility = View.VISIBLE
        binding.rvEnvios.adapter = ShippingAdapter(envios, this@HomeFragment)
    }

    private fun mostrarMensajeSinEnvios() {
        binding.tvSinEnvios.visibility = View.VISIBLE
        binding.ivSinEnvios.visibility = View.VISIBLE
        binding.rvEnvios.visibility = View.INVISIBLE
    }

    override fun clickDetalleEnvio(envio: Envio, position: Int) {
        val gson = Gson()
        val intent = Intent(requireContext(), DetailActivity::class.java)
        val datosEnvio = gson.toJson(envio)
        val datosColaborador = gson.toJson(colaborador)
        intent.putExtra("envio", datosEnvio)
        intent.putExtra("colaborador", datosColaborador)
        startActivity(intent)
    }

    private fun listenerLogout() {
        binding.ivLogout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

}