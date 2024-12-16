package uv.tc.time_fast.poko

data class Colaborador(
    val id: Int,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val curp: String,
    val numeroPersonal: String,
    val password: String ?,
    val foto: String ?,
    val idRol: Int,
    val rol: String
)
