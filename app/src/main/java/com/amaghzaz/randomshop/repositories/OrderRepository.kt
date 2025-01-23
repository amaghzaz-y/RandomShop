package com.amaghzaz.randomshop.repositories
import com.amaghzaz.randomshop.models.Order
import com.amaghzaz.randomshop.models.OrderDao

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun addOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun clearCart(cartId: Int) {
        orderDao.deleteOrdersByCart(cartId)
    }

    suspend fun getIncompleteOrder(): Order? {
        return orderDao.getIncompleteOrder()
    }

    suspend fun getAllOrders():List<Order>{
        return orderDao.getAllOrders()
    }

    suspend fun deleteOrderByProduct(productId: Int){
        orderDao.deleteOrderByProduct(productId)
    }

    suspend fun getOrdersByCart(cartId: Int):List<Order>{
        return orderDao.getOrdersByCart(cartId)
    }

    suspend fun getOrderById(orderId: Long): Order? {
        return orderDao.getOrderById(orderId)
    }

    suspend fun updateOrderStatus(cartId: Int, status: Boolean) {
        orderDao.updateOrderStatus(cartId, status)
    }
}