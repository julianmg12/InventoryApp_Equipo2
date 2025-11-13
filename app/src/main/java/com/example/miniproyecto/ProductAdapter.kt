package com.example.miniproyecto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

data class Producto(val codigo: String, val nombre: String, val precio: Double)

class ProductAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvCodigo: TextView = itemView.findViewById(R.id.tvCodigo)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvCodigo.text = "ID: ${producto.codigo}"

        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        holder.tvPrecio.text = formato.format(producto.precio)

        // 👇 HU 3 Criterio 8: al hacer clic, ir al detalle (HU 5)
        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("product_codigo", producto.codigo)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productos.size
}
