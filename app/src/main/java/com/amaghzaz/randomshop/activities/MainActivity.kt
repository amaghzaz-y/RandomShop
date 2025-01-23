package com.amaghzaz.randomshop.activities
import androidx.compose.material3.Icon
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amaghzaz.randomshop.models.AppDatabase
import com.amaghzaz.randomshop.repositories.OrderRepository
import com.amaghzaz.randomshop.screens.CartScreen
import com.amaghzaz.randomshop.screens.ProductScreen
import com.amaghzaz.randomshop.screens.ShopScreen
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
                }
            }
        }
    }
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
