package uv.tc.time_fast

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.ActivityNavigatorExtras
import com.google.gson.Gson
import uv.tc.time_fast.databinding.FragmentProfileBinding
import uv.tc.time_fast.poko.Colaborador
import androidx.navigation.fragment.navArgs
import com.koushikdutta.ion.Ion
import uv.tc.time_fast.poko.Login
import uv.tc.time_fast.util.Constantes

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var colaborador: Colaborador
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedViewModel.colaborador.observe(viewLifecycleOwner) { json ->
            if (json != null) {
                serializarDatosColaborador(json)
                cargarDatosColaborador()
                editarDatos()
            } else {
                Log.e("ProfileFragment", "El JSON es null")
            }
        }
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        verificarCambios()
    }

    private fun serializarDatosColaborador(json: String) {
        val gson = Gson()
        val respuestaLogin = gson.fromJson(json, Login::class.java)
            colaborador = respuestaLogin.colaborador
    }

    private fun cargarDatosColaborador() {
        binding.tvNombre.text = colaborador.nombre
        binding.tvApellidoPaterno.text = colaborador.apellidoPaterno
        binding.tvApellidoMaterno.text = colaborador.apellidoMaterno
        binding.tvCurp.text = colaborador.curp
        binding.tvNumPersonal.text = colaborador.numeroPersonal
        binding.tvCorreo.text = colaborador.correo
    }

    private fun editarDatos(){
        val gson = Gson()
        val json = gson.toJson(colaborador)
        binding.ibEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            intent.putExtra("colaborador", json)
            startActivity(intent)
        }
    }

    private fun verificarCambios() {
        Ion.getDefault(requireContext()).conscryptMiddleware.enable(false)
        Ion.with(requireContext())
            .load("POST", "${Constantes().URL_WS}login/validarCredenciales")
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBodyParameter("numeroPersonal", colaborador.numeroPersonal)
            .setBodyParameter("password", colaborador.password)
            .asString()
            .setCallback { e, result ->
                if(e == null){
                    serializarInformacion(result)
                } else {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun serializarInformacion(json: String) {
        val gson = Gson()
        val respuestaLogin = gson.fromJson(json, Login::class.java)
        if(!respuestaLogin.error ) {
            colaborador = respuestaLogin.colaborador
            cargarDatosColaborador()
        }else
            Toast.makeText(requireContext(), respuestaLogin.mensaje, Toast.LENGTH_LONG).show()
    }
}

