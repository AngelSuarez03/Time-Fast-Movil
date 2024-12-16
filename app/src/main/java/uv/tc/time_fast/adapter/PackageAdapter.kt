package uv.tc.time_fast.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uv.tc.time_fast.R
import uv.tc.time_fast.adapter.ShippingAdapter.ViewHolderShipping
import uv.tc.time_fast.poko.Paquete

class PackageAdapter(val paquetes: List<Paquete>): RecyclerView.Adapter<PackageAdapter.ViewHolderPackage>() {

    class ViewHolderPackage (itemView : View) : RecyclerView.ViewHolder(itemView) {
        val numeroPaquete: TextView = itemView.findViewById(R.id.tv_title_paquete)
        val descripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        val dimension: TextView = itemView.findViewById(R.id.tv_dimensiones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPackage {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recycle_detail_activity, parent, false)
        return ViewHolderPackage(itemView)
    }

    override fun getItemCount(): Int {
        return paquetes.size
    }

    override fun onBindViewHolder(holder: ViewHolderPackage, position: Int) {
        val paquete = paquetes.get(position)
        holder.numeroPaquete.text = "Paquete " + (position + 1)
        holder.descripcion.text = paquete.descripcion
        holder.dimension.text = paquete.dimensiones
    }

}