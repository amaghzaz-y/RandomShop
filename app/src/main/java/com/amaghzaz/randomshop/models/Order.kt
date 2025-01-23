package com.amaghzaz.randomshop.models

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0,
    val cartId: Int,
    val product: Int,
    val complete: Boolean
)

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Query("SELECT * FROM orders")
    suspend fun getAllOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Long): Order?

    @Query("SELECT * FROM orders WHERE cartId = :cartId")
    suspend fun getOrdersByCart(cartId: Int): List<Order>

    @Query("SELECT * FROM orders WHERE complete = :complete LIMIT 1")
    suspend fun getIncompleteOrder(complete: Boolean = false): Order?

    @Query("UPDATE orders SET complete = :complete WHERE cartId = :cartId")
    suspend fun updateOrderStatus(cartId: Int, complete: Boolean)

    @Query("DELETE FROM orders  WHERE cartId = :cartId")
    suspend fun deleteOrdersByCart(cartId: Int)

    @Query("DELETE FROM orders WHERE rowid = (SELECT rowid FROM orders WHERE product = :productId LIMIT 1)")
    suspend fun deleteOrderByProduct(productId: Int)
}