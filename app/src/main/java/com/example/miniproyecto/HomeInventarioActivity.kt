package com.example.miniproyecto

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeInventarioActivity : AppCompatActivity() {


    private lateinit var db: AppDatabase
    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etCantidad: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnGuardar: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)


        db = AppDatabase.getDatabase(this)


        toolbar = findViewById(R.id.toolbar)
        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        etCantidad = findViewById(R.id.etCantidad)
        etPrecio = findViewById(R.id.etPrecio)
        btnGuardar = findViewById(R.id.btnGuardar)


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        btnGuardar.setOnClickListener {
            saveProduct()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun saveProduct() {

        val codigo = etCodigo.text.toString()
        val nombre = etNombre.text.toString()
        val cantidadStr = etCantidad.text.toString()
        val precioStr = etPrecio.text.toString()


        if (codigo.isEmpty() || nombre.isEmpty() || cantidadStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {

            val cantidad = cantidadStr.toInt()
            val precio = precioStr.toDouble()


            val product = Product(
                codigo = codigo,
                nombre = nombre,
                cantidad = cantidad,
                precio = precio
            )


            CoroutineScope(Dispatchers.IO).launch {
                db.productDao().insert(product)


                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeInventarioActivity, "Producto guardado", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Cantidad o Precio no son válidos", Toast.LENGTH_SHORT).show()
        }
    }
}