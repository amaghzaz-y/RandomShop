package com.amaghzaz.randomshop.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amaghzaz.randomshop.viewModels.Product
import com.amaghzaz.randomshop.viewModels.ShopViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ShopViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShopScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShopScreen(modifier: Modifier = Modifier, viewModel: ShopViewModel = viewModel()) {
    val products by viewModel.products.collectAsState()
    if (products.isEmpty()) {
        Text(text = "Loading...", modifier = modifier)
    } else {
        ProductList(products = products, modifier = modifier)
    }
}

@Composable
fun ProductList(products: List<Product>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(products) { product ->
            ProductItem(product = product)
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RectangleShape)
                    .size(180.dp),
            )
            Column {
                Text(
                    text = product.name,
                    fontSize = 24.sp
                )
                Text(
                    text = "${product.price}"
                )
            }
        }
    }
}
