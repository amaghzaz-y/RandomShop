package com.amaghzaz.randomshop.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amaghzaz.randomshop.services.FakeStoreApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    val category: String,
    val description: String
)

class ShopViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())

    val products: StateFlow<List<Product>> get() = _products
    val categories: StateFlow<List<String>> get() = _categories
    val filteredProducts: StateFlow<List<Product>> get() = _filteredProducts

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            try {
                val productList = FakeStoreApiService.api.getProducts()
                _products.value = productList
                _filteredProducts.value = productList
                val categoryList = FakeStoreApiService.api.getCategories()
                val uniqueCategories =
                    listOf("All") + categoryList.map { it -> it.replaceFirstChar { it.uppercase() } }
                        .toSet().toList()
                _categories.value = uniqueCategories
            } catch (e: Exception) {
                println("Error fetching products: ${e.message}")
            }
        }
    }

    fun filterProductsByCategory(category: String) {
        val key = category.replaceFirstChar { it.lowercase() }
        if (key == "all") {
            _filteredProducts.value = _products.value
        } else {
            val products = _products.value.filter { it -> it.category == key }
            _filteredProducts.value = products
        }
    }

    private fun loadPlaceholders(): List<Product> {
        return listOf(
            Product(
                1,
                "Product 1",
                19.99,
                "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg",
                "Electronics",
                "Description 1"
            ), Product(
                2,
                "Product 2",
                29.99,
                "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg",
                "Clothing",
                "Description 2"
            ), Product(
                3,
                "Product 3",
                9.99,
                "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg",
                "Home",
                "Description 3"
            )
        )
    }
}