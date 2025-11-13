package com.example.miniproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    private lateinit var tvNombre: TextView
    private lateinit var tvPrecio: TextView
    private lateinit var tvCantidad: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnEliminar: Button
    private lateinit var fabEditar: FloatingActionButton

    private var currentProduct: Product? = null
    private var productCodigo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        db = AppDatabase.getDatabase(this)

        // Toolbar HU5 Criterio 1
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Detalle del producto"
            setDisplayHomeAsUpEnabled(true) // flecha atrás
        }

        // Extra recibido desde el adapter
        productCodigo = intent.getStringExtra("product_codigo")
        if (productCodigo.isNullOrEmpty()) {
            Toast.makeText(this, "Producto no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Referencias UI
        tvNombre = findViewById(R.id.tvNombre)
        tvPrecio = findViewById(R.id.tvPrecio)
        tvCantidad = findViewById(R.id.tvCantidad)
        tvTotal = findViewById(R.id.tvTotal)
        btnEliminar = findViewById(R.id.btnEliminar)
        fabEditar = findViewById(R.id.fabEditar)

        // Cargar datos del producto
        loadProduct()

        // Botón Eliminar HU5 Criterio 3
        btnEliminar.setOnClickListener {
            showDeleteConfirmation()
        }

        // FAB Editar HU5 Criterio 4 → ahora sí edita
        fabEditar.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java)
            intent.putExtra("product_codigo", productCodigo)
            startActivity(intent)
        }
    }

    // Flecha toolbar -> vuelve al menú (HomeActivity)
    override fun onSupportNavigateUp(): Boolean {
        goToHome()
        return true
    }

    // Botón atrás del celular -> también vuelve al menú
    override fun onBackPressed() {
        goToHome()
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun loadProduct() {
        val codigo = productCodigo ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.productDao()
            val product = dao.getByCodigo(codigo)
            currentProduct = product

            if (product == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "No se encontró el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                return@launch
            }

            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }

            val precioUnitario = product.precio
            val cantidad = product.cantidad
            val total = precioUnitario * cantidad

            val precioTexto = format.format(precioUnitario)
            val totalTexto = format.format(total)

            withContext(Dispatchers.Main) {
                tvNombre.text = product.nombre
                tvPrecio.text = "Precio unidad: $precioTexto"
                tvCantidad.text = "Cantidad disponible: $cantidad"
                tvTotal.text = "Total: $totalTexto"
            }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Seguro que deseas eliminar este producto?")
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sí") { _, _ ->
                deleteProduct()
            }
            .show()
    }

    private fun deleteProduct() {
        val product = currentProduct ?: return

        CoroutineScope(Dispatchers.IO).launch {
            db.productDao().delete(product)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Producto eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                // Volver a la lista HU 3 (HomeActivity)
                goToHome()
            }
        }
    }
}
