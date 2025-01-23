package com.amaghzaz.randomshop.repositories
import com.amaghzaz.randomshop.models.Order
import com.amaghzaz.randomshop.models.OrderDao

class OrderRepository(private val orderDao: OrderDao) {

    suspend fun addOrder(order: Order) {
        orderDao.insertOrder(order)
    }

    suspend fun getIncompleteOrder(): Order? {
        return orderDao.getIncompleteOrder()
    }

    suspend fun getAllOrders():List<Order>{
        return orderDao.getAllOrders()
    }

    suspend fun deleteOneOrderByProduct(productId: Int){
        orderDao.deleteOneOrderByProduct(productId)
    }

    suspend fun getOrdersByCart(cartId: Int):List<Order>{
        return orderDao.getOrdersByCart(cartId)
    }


    suspend fun updatCartStatus(cartId: Int, status: Boolean) {
        orderDao.updateOrderStatus(cartId, status)
    }
}