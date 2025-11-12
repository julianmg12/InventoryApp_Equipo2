package com.example.miniproyecto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeInventarioActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var ivLogout: ImageView
    private lateinit var prefs: SharedPreferences

    // Lista ficticia de productos para pruebas (puedes cambiarla luego)
    private val productos = listOf(
        Producto("Arroz", "123", 3326.00, 10),
        Producto("Frijoles", "124", 12000.00, 5),
        Producto("Aceite", "125", 8500.00, 3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)

        // Criterio 1 HU 3.0: si no hay sesión, no entra aquí
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)
        if (!isLoggedIn) {
            irALogin()
            return
        }

        setContentView(R.layout.activity_home_inventario)

        recyclerView = findViewById(R.id.rv_productos)
        fabAdd = findViewById(R.id.fab_add)
        ivLogout = findViewById(R.id.iv_logout)

        // Configurar RecyclerView
        adapter = ProductoAdapter(productos) { producto ->
            // Click en item -> HU 5.0 (por ahora solo mensaje)
            Toast.makeText(this, "Detalle: ${producto.nombre}", Toast.LENGTH_SHORT).show()
            // Aquí luego navegas a DetalleProductoActivity
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // FAB -> HU 4.0 Agregar producto (por ahora placeholder)
        fabAdd.setOnClickListener {
            Toast.makeText(this, "Ir a Agregar Producto (HU 4.0)", Toast.LENGTH_SHORT).show()
        }

        // Logout -> limpia sesión y lleva a login
        ivLogout.setOnClickListener {
            prefs.edit().putBoolean("isLoggedIn", false).apply()
            irALogin(clearStack = true)
        }
    }

    private fun irALogin(clearStack: Boolean = false) {
        val intent = Intent(this, LoginActivity::class.java)
        if (clearStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    // Criterio 4 HU 3.0: botón atrás -> escritorio, no al Login
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
