package com.amaghzaz.randomshop.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val image: String,
    val category: String,
    val description: String
)

class ShopViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> get() = _products

    init {
        viewModelScope.launch {
            _products.value = loadProducts()
        }
    }

    private fun loadProducts(): List<Product> {
        return listOf(
            Product(
                1,
                "Product 1",
                19.99,
                "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg",
                "Electronics",
                "Description 1"
            ),
            Product(
                2,
                "Product 2",
                29.99,
                "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg",
                "Clothing",
                "Description 2"
            ),
            Product(
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