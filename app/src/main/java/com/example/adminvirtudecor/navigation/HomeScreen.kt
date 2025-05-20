package com.example.adminvirtudecor.navigation

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.adminvirtudecor.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val userEmail = auth.currentUser?.email ?: "Unknown"
    var pendingOrdersCount by remember { mutableStateOf(0) }
    var completedOrdersCount by remember { mutableStateOf(0) }
    var totalEarnings by remember { mutableStateOf(0.0) }

    // Fetch orders from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance()

        // Pending orders
        db.getReference("Order_details").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        if (order != null) count++
                    }
                }
                pendingOrdersCount = count
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Completed orders & earnings
        db.getReference("Completed_Order").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var total = 0.0
                var count = 0
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        if (order != null) {
                            total += order.totalPrice.toDouble()
                            count++
                        }
                    }
                }
                totalEarnings = total
                completedOrdersCount = count
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Admin App",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF333333)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoCard(
                title = "Pending Orders",
                value = "$pendingOrdersCount",
                gradientColors = listOf(Color(0xFFEF5350), Color(0xFFE57373)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                navController.navigate("pending_orders")
            }

            InfoCard(
                title = "Completed Orders",
                value = "$completedOrdersCount",
                gradientColors = listOf(Color(0xFF66BB6A), Color(0xFFA5D6A7)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                navController.navigate("completedOrder")
            }

            InfoCard(
                title = "Total Earnings",
                value = "₹${"%.2f".format(totalEarnings)}",
                gradientColors = listOf(Color(0xFF42A5F5), Color(0xFF90CAF9)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                navController.navigate("completedOrder")
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        val gridItems = listOf(
            GridItem("Add Furniture") { navController.navigate("add_furniture") },
            GridItem("View Furniture") { navController.navigate("view_furniture") },
            GridItem("Dispatch Order") { navController.navigate("completedOrder") },
            GridItem("Logout") {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 20.dp)
        ) {
            items(gridItems) { item ->
                SmallActionCard(title = item.title, onClick = item.onClick)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Design by Raghav",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF7B1FA2), Color(0xFFE040FB))
                        ),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = Color.White,
            )
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    val gradientBrush = Brush.linearGradient(colors = gradientColors)

    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradientBrush)
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,  // Reduced font size here
                    color = Color.White,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,  // Reduced font size here
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SmallActionCard(title: String, onClick: () -> Unit) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF512DA8), Color(0xFF7E57C2))
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(brush = gradientBrush)
                .fillMaxSize()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class GridItem(val title: String, val onClick: () -> Unit)
