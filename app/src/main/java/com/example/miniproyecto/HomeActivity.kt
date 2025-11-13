package com.example.miniproyecto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_inventario)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        fabAdd = findViewById(R.id.fabAdd)
        toolbar = findViewById(R.id.toolbar)

        // 🔹 Configurar Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Inventario"

        recyclerView.layoutManager = LinearLayoutManager(this)

        // FAB para agregar producto (HU 4.0)
        fabAdd.setOnClickListener {
            val intent = Intent(this, HomeInventarioActivity::class.java)
            startActivity(intent)
        }

        // Botón atrás físico → mandar app al escritorio (no volver al login)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    // 🔹 Inflar el menú con el botón de cerrar sesión
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    // 🔹 Manejar clic en el botón de cerrar sesión
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadProducts() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            val productEntities = db.productDao().getAll()
            val productList = productEntities.map { productEntity ->
                Producto(
                    codigo = productEntity.codigo,
                    nombre = productEntity.nombre,
                    precio = productEntity.precio
                )
            }

            withContext(Dispatchers.Main) {
                val adapter = ProductAdapter(productList)
                recyclerView.adapter = adapter

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    // 🔹 Cerrar sesión y volver al login con huella (MainActivity)
    private fun logoutUser() {
        // Si estás usando SharedPreferences para sesión, aquí las borras
        val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
        prefs.edit().clear().apply()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
