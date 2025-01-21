package com.amaghzaz.randomshop.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    val category: String,
    val description: String
)

class ShopViewModel: ViewModel(){
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        val initialProducts = listOf(
            Product(1, "Product 1", 19.99, "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg", "Electronics", "Description 1"),
            Product(2, "Product 2", 29.99, "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg", "Clothing", "Description 2"),
            Product(3, "Product 3", 9.99, "https://img.freepik.com/psd-premium/t-shirt-noir-fond-blanc-tourne-studio_1153121-10726.jpg", "Home", "Description 3")
        )
        _products.value = initialProducts
    }

    fun filterProductsByCategory(category: String): LiveData<List<Product>> {
        val filteredProducts = _products.value?.filter { it.category == category }
        return MutableLiveData(filteredProducts)
    }
}