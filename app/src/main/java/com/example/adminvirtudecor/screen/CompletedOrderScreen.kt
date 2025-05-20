package com.example.adminvirtudecor.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.adminvirtudecor.model.OrderDetail
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedOrderScreen(navController: NavHostController) {
    var completedOrders by remember { mutableStateOf(listOf<OrderDetail>()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch completed orders from Firebase
    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Completed_Order")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<OrderDetail>()
                for (userSnapshot in snapshot.children) {
                    for (orderSnapshot in userSnapshot.children) {
                        val order = orderSnapshot.getValue(OrderDetail::class.java)
                        order?.let { orders.add(it) }
                    }
                }
                completedOrders = orders
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Completed Orders", color = Color.Black) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White // background color of the whole screen
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
            ) {
                items(completedOrders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF9F9F9) // light gray background for cards
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Customer: ${order.user?.name ?: "N/A"}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF222222) // dark text for good contrast
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Phone: ${order.user?.phone ?: "N/A"}",
                                color = Color(0xFF444444)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Address: ${order.user?.address ?: "N/A"}",
                                color = Color(0xFF444444)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Total Price: ₹${order.totalPrice ?: 0.0}",
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Order Status: Completed",
                                color = Color(0xFF4CAF50) // green for completed status
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // List ordered furniture items
                            order.products?.forEach { item ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• ${item.name ?: "Furniture"} - ₹${item.price ?: 0.0}",
                                    color = Color(0xFF555555)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
