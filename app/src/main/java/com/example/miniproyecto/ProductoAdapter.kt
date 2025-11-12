package com.example.miniproyecto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductoAdapter(
    private val productos: List<Producto>,
    private val onItemClick: (Producto) -> Unit
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre)
        val tvId: TextView = view.findViewById(R.id.tv_id)
        val tvPrecio: TextView = view.findViewById(R.id.tv_precio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvId.text = "ID: ${producto.id}"
        holder.tvPrecio.text = formatearPrecio(producto.precioUnitario)

        holder.itemView.setOnClickListener { onItemClick(producto) }
    }

    override fun getItemCount(): Int = productos.size

    private fun formatearPrecio(valor: Double): String {
        // Ajustable al formato raro de la HU, pero por ahora estándar:
        return "$ " + String.format("%,.2f", valor)
            .replace(",", ".") // 1.234.567,89 estilo latino
    }
}
