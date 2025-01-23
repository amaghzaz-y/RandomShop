package com.amaghzaz.randomshop.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.amaghzaz.randomshop.models.Product
import com.amaghzaz.randomshop.viewModels.ShopViewModel

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val products by viewModel.filteredProducts.collectAsState()
    if (products.isEmpty()) {
        Text(text = "Loading...", modifier = modifier)
    } else {
        ProductList(products = products, modifier = modifier, navController = navController)
    }
}

@Composable
fun ProductList(
    products: List<Product>, modifier: Modifier = Modifier, navController: NavController
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(products) { product ->
            ProductItem(product = product, onClick = {
                navController.navigate("product/${product.id}")
            })
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }) {
        Row(Modifier.fillMaxHeight()) {
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(RectangleShape)
                    .size(150.dp),
            )
            Column(
                Modifier
                    .padding(10.dp)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = product.title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${product.price}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF175B00)
                )
            }
        }
    }
}
