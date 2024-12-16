package uv.tc.time_fast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uv.tc.time_fast.R
import uv.tc.time_fast.interfaces.ListenerRecycleEnvios
import uv.tc.time_fast.poko.Envio

class ShippingAdapter(val envios : List<Envio>, val listenerRecycleEnvios : ListenerRecycleEnvios): RecyclerView.Adapter<ShippingAdapter.ViewHolderShipping> (){
    class ViewHolderShipping (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val numeroGuia : TextView = itemView.findViewById(R.id.tv_numero_guia)
        val destino : TextView = itemView.findViewById(R.id.tv_destino)
        val estatus : TextView = itemView.findViewById(R.id.tv_estatus)
        val detalle : Button = itemView.findViewById(R.id.btn_detalle)
        val imgEstatus : ImageView = itemView.findViewById(R.id.iv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderShipping {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycle_home_fragment, parent, false)
        return ViewHolderShipping(itemView)
    }
    override fun getItemCount(): Int {
        return envios.size
    }

    override fun onBindViewHolder(holder: ViewHolderShipping, position: Int) {
        val envio = envios.get(position)
        if ( envio.idEstatus == 2 ) {
            holder.imgEstatus.setImageResource(R.drawable.on_the_way_icon)
        }
        if ( envio.idEstatus == 3) {
            holder.imgEstatus.setImageResource(R.drawable.stopped_icon)
        }
        if ( envio.idEstatus == 4) {
            holder.imgEstatus.setImageResource(R.drawable.delivered_icon)
        }
        if ( envio.idEstatus == 5) {
            holder.imgEstatus.setImageResource(R.drawable.canceled_icon)
        }
        holder.numeroGuia.text = envio.numeroGuia
        holder.destino.text = envio.calleDestino + " #" + envio.numeroDestino + ", " + envio.coloniaDestino + ", " + envio.ciudadDestino + ", " + envio.estadoDestino
        holder.estatus.text = envio.estatus
        holder.detalle.setOnClickListener{
            listenerRecycleEnvios.clickDetalleEnvio(envio, position)
        }
    }

}