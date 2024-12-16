package uv.tc.time_fast

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _colaborador = MutableLiveData<String>()
    val colaborador: LiveData<String> get() = _colaborador

    fun setColaborador(json: String) {
        _colaborador.value = json
    }
}
