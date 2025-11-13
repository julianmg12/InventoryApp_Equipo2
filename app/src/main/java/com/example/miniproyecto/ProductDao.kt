package com.example.miniproyecto

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: Product)

    @Query("SELECT * FROM Product")
    suspend fun getAll(): List<Product>


    @Query("SELECT SUM(cantidad * precio) FROM Product")
    suspend fun getInventoryTotal(): Double?


    @Query("SELECT * FROM Product WHERE codigo = :codigo LIMIT 1")
    suspend fun getByCodigo(codigo: String): Product?

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)
}
