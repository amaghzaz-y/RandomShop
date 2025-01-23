package com.amaghzaz.randomshop.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amaghzaz.randomshop.models.Order
import com.amaghzaz.randomshop.models.Product
import com.amaghzaz.randomshop.repositories.OrderRepository
import com.amaghzaz.randomshop.services.FakeStoreApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

class ShopViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    private val _cart = MutableStateFlow<List<Pair<Product, Int>>>(emptyList())

    private var cartId = 0

    val products: StateFlow<List<Product>> get() = _products
    val categories: StateFlow<List<String>> get() = _categories
    val filteredProducts: StateFlow<List<Product>> get() = _filteredProducts
    val cart: StateFlow<List<Pair<Product, Int>>> get() = _cart

    init {
        fetchProducts()
        updateCart()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = FakeStoreApiService.api.getProducts()
                _products.value = productList
                _filteredProducts.value = productList
                val categoryList = FakeStoreApiService.api.getCategories()
                val uniqueCategories =
                    listOf("All") + categoryList.map { it.replaceFirstChar { it.uppercase() } }
                        .toSet().toList()
                _categories.value = uniqueCategories
            } catch (e: Exception) {
                Log.d("ERROR", "Error fetching products: ${e.message}")
            }
        }

    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                _orders.value = orderRepository.getAllOrders()
                val incompleteOrder = orderRepository.getIncompleteOrder()
                if (incompleteOrder != null) {
                    cartId = incompleteOrder.cartId
                }
            } catch (e: Exception) {
                println("Error fetching orders: ${e.message}")
            }
        }
    }

    fun filterProductsByCategory(category: String) {
        val key = category.replaceFirstChar { it.lowercase() }
        if (key == "all") {
            _filteredProducts.value = _products.value
        } else {
            val products = _products.value.filter { it.category == key }
            _filteredProducts.value = products
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                updateCart()
                val order = Order(
                    cartId = cartId, product = product.id, complete = false
                )
                orderRepository.addOrder(order)
                updateCart()
            } catch (e: Exception) {
                println("Error adding order: ${e.message}")
            }
        }

    }

    fun popProduct(product: Product) {
        viewModelScope.launch {
            try {
                updateCart()
                orderRepository.deleteOrderByProduct(product.id)
                updateCart()
            } catch (e: Exception) {
                println("Error adding order: ${e.message}")
            }
        }
    }

    private fun getProductById(id: Int): Product? {
        return _products.value.find { it.id == id }
    }

    fun updateCart() {
        viewModelScope.launch {
            try {
                val orders = orderRepository.getAllOrders()
                val productQuantityPairs = orders
                    .groupBy { it.product }
                    .mapNotNull { (id, ordersForProduct) ->
                        val product = getProductById(id)
                        if (product != null) {
                            Pair(product, ordersForProduct.size)
                        } else {
                            null
                        }
                    }
                _cart.value = productQuantityPairs
            } catch (e: Exception) {
                println("Error fetching orders: ${e.message}")
            }
        }
    }
}