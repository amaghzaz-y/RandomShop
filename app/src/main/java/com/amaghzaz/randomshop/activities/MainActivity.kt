package com.amaghzaz.randomshop.activities

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.amaghzaz.randomshop.viewModels.Product
import com.amaghzaz.randomshop.viewModels.ShopViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: ShopViewModel

    @SuppressLint("StateFlowValueCalledInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "shop"
                ) {
                    composable("shop") {
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            ProductTopAppBar(
                                onCartClick = { println("Search clicked") },
                                onMenuClick = { category ->
                                    viewModel.filterProductsByCategory(category)
                                }
                            )
                        }) { innerPadding ->
                            ShopScreen(
                                modifier = Modifier.padding(innerPadding),
                                navController = navController
                            )
                        }
                    }
                    composable("product/{productId}") { backStackEntry ->
                        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                            ProductTopAppBar(
                                onCartClick = { println("Search clicked") },
                                onMenuClick = { category ->
                                    viewModel.filterProductsByCategory(category)
                                }
                            )
                        }) { innerPadding ->
                            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                            val product = viewModel.products.value.find { it.id == productId }
                            if (product != null) {
                                ProductScreen(product = product, modifier = Modifier.padding(innerPadding))
                            } else {
                                Text(text = "Product not found", modifier = Modifier.fillMaxSize())
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun ProductScreen(product: Product, modifier: Modifier = Modifier) {
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

        Text(
            text = "$${product.price}",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF175B00)
        )

        Text(
            text = "Category: ${product.category.replaceFirstChar { it.uppercase() }}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Text(
            text = product.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel(),
    navController: NavController
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
    products: List<Product>,
    modifier: Modifier = Modifier,
    navController: NavController
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
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
                    text = product.title, fontSize = 22.sp
                )
                Text(
                    text = "${product.price}$",
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
    onCartClick: () -> Unit,
    onMenuClick: (String) -> Unit,
    viewModel: ShopViewModel = viewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsState()

    TopAppBar(
        title = {
            Text(
                text = "\uD83D\uDECD\uFE0F RandomShop",
                fontWeight = FontWeight.Black,
                color = Color(0xFF009688)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "More")
            }

            IconButton(onClick = onCartClick) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart")
            }

            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onMenuClick(category)
                            showMenu = false
                        }
                    )
                }
            }
        }
    )
}
