package com.amaghzaz.randomshop.activities

import androidx.compose.material3.Icon
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.amaghzaz.randomshop.models.AppDatabase
import com.amaghzaz.randomshop.models.Product
import com.amaghzaz.randomshop.repositories.OrderRepository
import com.amaghzaz.randomshop.viewModels.ShopViewModel
import com.amaghzaz.randomshop.viewModels.ShopViewModelFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(this)
        val orderRepository = OrderRepository(database.orderDao())
        setContent {
            val shopViewModel: ShopViewModel = viewModel(
                factory = ShopViewModelFactory(orderRepository)
            )
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController, startDestination = "shop"
                ) {

                    composable("shop") {
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            ProductTopAppBar(onCartClick = {
                                navController.navigate("cart")
                            }, onMenuClick = { category ->
                                shopViewModel.filterProductsByCategory(category)
                            }, viewModel = shopViewModel)
                        }) { innerPadding ->
                            ShopScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController,
                                viewModel = shopViewModel,
                            )
                        }
                    }

                    composable("product/{productId}") { backStackEntry ->
                        val productId =
                            backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                        val product = shopViewModel.products.value.find { it.id == productId }
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            ProductTopAppBar(onCartClick = {
                                navController.navigate("cart")
                            }, onMenuClick = { category ->
                                shopViewModel.filterProductsByCategory(category)
                            },viewModel = shopViewModel)
                        }) { innerPadding ->
                            if (product != null) {
                                ProductScreen(
                                    product = product,
                                    addToCart = { shopViewModel.addProduct(product) },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                )
                            } else {
                                Text(
                                    text = "Product not found", modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    composable("cart") {
                        shopViewModel.updateCart()
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            ProductTopAppBar(onCartClick = {
                                navController.navigate("cart")
                            }, onMenuClick = { category ->
                                shopViewModel.filterProductsByCategory(category)
                            },viewModel = shopViewModel)
                        }) { innerPadding ->
                            CartScreen(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                viewModel = shopViewModel
                            )
                        }
                        OrderSummary(onBuy = {
                            navController.navigate("cart")
                        },viewModel = shopViewModel
                        )
                    }
//                    composable("orders/{cartId}") { backStackEntry ->
//                        val productId = backStackEntry.arguments?.getString("cartId")?.toIntOrNull()
//                        val product = shopViewModel.products.value.find { it.id == productId }
//                        if (product != null) {
//                            ProductScreen(
//                                product = product,
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(top = 42.dp)
//                            )
//                        } else {
//                            Text(
//                                text = "Product not found", modifier = Modifier.fillMaxSize()
//                            )
//                        }
//                    }
                }
            }
        }
    }
}

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

@Composable
fun ProductScreen(
    product: Product, addToCart: () -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = product.image,
            contentDescription = product.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = product.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$${product.price}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF186700)
            )
            Button(onClick = {
                addToCart()
                Toast.makeText(context, "Added to Cart !", Toast.LENGTH_SHORT).show()

            }) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
                Text("Add to Cart")
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Category: ${product.category.replaceFirstChar { it.uppercase() }}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Rating: ${product.rating.rate}/5 (${product.rating.count})",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary
            )

        }
        Text(
            text = product.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}


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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductTopAppBar(
    onCartClick: () -> Unit, onMenuClick: (String) -> Unit,
    viewModel: ShopViewModel
) {
    var showMenu by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsState()

    TopAppBar(title = {
        Text(
            text = "\uD83D\uDECD\uFE0F RandomShop",
            fontWeight = FontWeight.Black,
            color = Color(0xFF009688)
        )
    }, modifier = Modifier.fillMaxWidth(), actions = {
        IconButton(onClick = { showMenu = !showMenu }) {
            Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Filter")
        }

        IconButton(onClick = onCartClick) {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
        }

        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            categories.forEach { category ->
                DropdownMenuItem(text = { Text(category) }, onClick = {
                    onMenuClick(category)
                    showMenu = false
                })
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummary(
    onBuy: () -> Unit, viewModel: ShopViewModel = viewModel()
) {
    val products by viewModel.cart.collectAsState()
    val total = products.sumOf { (product, quantity) ->
        product.price * quantity
    }


    TopAppBar(title = {
        Text(
            fontSize = 24.sp,
            text = "\uD83D\uDED2 Cart",
            fontWeight = FontWeight.Black,
            color = Color(0xFF4500FC)
        )
    }, modifier = Modifier.fillMaxWidth(), actions = {
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                text = "$${"%.2f".format(total)}",
                color = Color(0xFF186700),
                modifier = Modifier.padding(end = 16.dp)
            )
            Button(onClick = onBuy){
                Text("BUY")
            }
        }

    })
}
