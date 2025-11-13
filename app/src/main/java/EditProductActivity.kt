package com.example.miniproyecto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProductActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    private lateinit var toolbar: Toolbar
    private lateinit var tvId: TextView
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etCantidad: EditText
    private lateinit var btnEditar: Button

    private var originalProduct: Product? = null
    private var productCodigo: String? = null   // este viene desde ProductDetailActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        db = AppDatabase.getDatabase(this)

        // Toolbar HU 6 Criterio 1
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar producto"
            setDisplayHomeAsUpEnabled(true) // flecha atrás
            setDisplayShowHomeEnabled(true)
        }

        // Extra con el código del producto
        productCodigo = intent.getStringExtra("product_codigo")
        if (productCodigo.isNullOrEmpty()) {
            Toast.makeText(this, "Producto no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Referencias UI
        tvId = findViewById(R.id.tvId)
        etNombre = findViewById(R.id.etNombre)
        etPrecio = findViewById(R.id.etPrecio)
        etCantidad = findViewById(R.id.etCantidad)
        btnEditar = findViewById(R.id.btnEditar)

        // Cargar datos del producto
        loadProduct()

        // Validar campos para activar/desactivar botón (Criterio 5)
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etNombre.addTextChangedListener(watcher)
        etPrecio.addTextChangedListener(watcher)
        etCantidad.addTextChangedListener(watcher)

        // Botón Editar (Criterio 4)
        btnEditar.setOnClickListener {
            updateProduct()
        }
    }

    // Flecha Toolbar → volver al detalle (HU 5.0)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            goBackToDetail()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Botón atrás del celular → también al detalle
    override fun onBackPressed() {
        goBackToDetail()
    }

    private fun goBackToDetail() {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product_codigo", productCodigo)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun loadProduct() {
        val codigo = productCodigo ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = db.productDao()
            val product = dao.getByCodigo(codigo)
            originalProduct = product

            if (product == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@EditProductActivity,
                        "No se encontró el producto",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                return@launch
            }

            withContext(Dispatchers.Main) {
                // Criterio 2: Id solo lectura
                tvId.text = "Id: ${product.codigo}"

                // Criterio 3: campos precargados
                etNombre.setText(product.nombre)
                etPrecio.setText(product.precio.toString())
                etCantidad.setText(product.cantidad.toString())

                validateFields()
            }
        }
    }

    // Criterio 5: si algún campo está vacío → botón inactivo
    private fun validateFields() {
        val nombreOk = etNombre.text.toString().isNotBlank()
        val precioOk = etPrecio.text.toString().isNotBlank()
        val cantidadOk = etCantidad.text.toString().isNotBlank()

        btnEditar.isEnabled = nombreOk && precioOk && cantidadOk
    }

    private fun updateProduct() {
        val product = originalProduct
        if (product == null) {
            Toast.makeText(this, "Producto no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val nombre = etNombre.text.toString().trim()
        val precioStr = etPrecio.text.toString().trim()
        val cantidadStr = etCantidad.text.toString().trim()

        if (nombre.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validaciones numéricas básicas
        val cantidad: Int
        val precio: Double
        try {
            cantidad = cantidadStr.toInt()
            precio = precioStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Precio o Cantidad no son válidos", Toast.LENGTH_SHORT).show()
            return
        }

        // Respetar máximos de la HU (ya limitados por maxLength en XML)
        if (nombre.length > 40) {
            Toast.makeText(this, "El nombre no puede tener más de 40 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (precioStr.length > 20) {
            Toast.makeText(this, "El precio no puede tener más de 20 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        if (cantidadStr.length > 4) {
            Toast.makeText(this, "La cantidad no puede tener más de 4 dígitos", Toast.LENGTH_SHORT).show()
            return
        }


        val updatedProduct = Product(
            id = product.id,
            codigo = product.codigo,
            nombre = nombre,
            cantidad = cantidad,
            precio = precio
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.productDao().update(updatedProduct)

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@EditProductActivity,
                    "Producto actualizado",
                    Toast.LENGTH_LONG
                ).show()

                // Criterio 4: volver a la lista Home Inventario (HU 3.0)
                val intent = Intent(this@EditProductActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
}
