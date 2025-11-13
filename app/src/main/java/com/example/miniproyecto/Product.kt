package com.example.miniproyecto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codigo: String = "",
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)
