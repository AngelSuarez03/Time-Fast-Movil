package uv.tc.time_fast.interfaces

import uv.tc.time_fast.poko.Envio

interface ListenerRecycleEnvios {

    fun clickDetalleEnvio(envio: Envio, position: Int)

}