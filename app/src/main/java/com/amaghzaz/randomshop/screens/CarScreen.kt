package com.amaghzaz.randomshop.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amaghzaz.randomshop.models.Product
import com.amaghzaz.randomshop.viewModels.ShopViewModel

@Composable
fun CartScreen(modifier: Modifier = Modifier, viewModel: ShopViewModel = viewModel()) {
    val products by viewModel.cart.collectAsState()
    Column {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp),

            ) {
            items(products) { (product, quantity) ->
                CartItem(product, quantity, viewModel)
            }
        }
    }

}


@Composable
fun CartItem(product: Product, quantity: Int, viewModel: ShopViewModel = viewModel()) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AsyncImage(
            model = product.image,
            contentDescription = product.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(80.dp)
                .width(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Text(
            text = product.title.slice(0 until 30.coerceAtMost(product.title.length)),
            modifier = Modifier.width(100.dp)
        )
        Column {
            IconButton(onClick = { viewModel.addProduct(product) }) {
                Icon(imageVector = Icons.Outlined.KeyboardArrowUp, contentDescription = "Add")
            }
            IconButton(onClick = { viewModel.popProduct(product) }) {
                Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription = "Remove")
            }

        }
        Column {
            Text("Qty: $quantity")
            Text(
                text = "$${"%.2f".format(product.price * quantity)}", color = Color(0xFF186700)
            )
        }
    }
}



