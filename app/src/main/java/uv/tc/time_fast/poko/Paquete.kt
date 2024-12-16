package uv.tc.time_fast.poko

data class Paquete(
    val id: Int?,
    val descripcion: String,
    val peso: Float,
    val dimensiones: String,
    val idEnvio: Int,
    val noGuiaEnvio: String?
)
