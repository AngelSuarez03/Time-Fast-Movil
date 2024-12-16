package uv.tc.time_fast

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.ActivityNavigatorExtras
import com.google.gson.Gson
import uv.tc.time_fast.databinding.FragmentProfileBinding
import uv.tc.time_fast.poko.Colaborador
import androidx.navigation.fragment.navArgs
import uv.tc.time_fast.poko.Login

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
            } else {
                Log.e("ProfileFragment", "El JSON es null")
            }
        }
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.tvPassword.text = colaborador.password
    }
}

