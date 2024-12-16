package uv.tc.time_fast.poko

data class Colaborador(
    var id: Int,
    var nombre: String,
    var apellidoPaterno: String,
    var apellidoMaterno: String,
    var correo: String,
    var curp: String,
    var numeroPersonal: String,
    var password: String?,
    var foto: String?,
    var idRol: Int?,
    var idUnidad: Int?,
    var vin: String?,
    var nii: String?,
    var rol: String?,
    var numLicencia: String?
)
