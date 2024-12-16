package uv.tc.time_fast.poko

data class Envio(
    val id: Int? = null,
    val numeroGuia: String,
    val calleOrigen: String,
    val coloniaOrigen: String,
    val numeroOrigen: Int?,
    val cpOrigen: String,
    val ciudadOrigen: String,
    val idEstadoOrigen: String,
    val estadoOrigen: String?,
    val calleDestino: String,
    val coloniaDestino: String,
    val numeroDestino: Int?,
    val cpDestino: String,
    val ciudadDestino: String,
    val idEstadoDestino: String,
    val estadoDestino: String?,
    val costo: Float,
    val idColaborador: Int,
    val colaborador: String?,
    val idCliente: Int,
    val cliente: String?,
    val correo: String?,
    val telefono: String?,
    val idEstatus: Int,
    val estatus: String?
)



