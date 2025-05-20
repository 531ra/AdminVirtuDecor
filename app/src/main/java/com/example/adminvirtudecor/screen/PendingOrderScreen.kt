package com.example.adminvirtudecor.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.adminvirtudecor.model.OrderDetail
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingOrderScreen(navController: NavHostController) {
    var orders by remember { mutableStateOf<List<OrderDetail>>(emptyList()) }
    val dbRef = FirebaseDatabase.getInstance().getReference("Order_details")
    val context = LocalContext.current

    // Fetch orders from Firebase once
    LaunchedEffect(Unit) {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<OrderDetail>()
                for (userSnap in snapshot.children) {
                    val userId = userSnap.key ?: continue
                    for (orderSnap in userSnap.children) {
                        val order = orderSnap.getValue(OrderDetail::class.java)
                        order?.let {
                            it.user.uid = userId  // adjust field name to your User model
                            tempList.add(it)
                        }
                    }
                }
                orders = tempList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching orders: ${error.message}")
                Toast.makeText(context, "Failed to load orders: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pending Orders",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D3B66)  // Dark Blue for better contrast
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.shadow(elevation = 4.dp)
            )
        },
        containerColor = Color(0xFFF2F4F8) // Light grey-blue background
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF2F4F8))
        ) {
            items(orders) { order ->
                OrderCard(order = order, onAcceptClicked = { selectedOrder ->
                    val db = FirebaseDatabase.getInstance()
                    val userId = selectedOrder.user.uid

                    // Add to Completed_Order
                    db.getReference("Completed_Order")
                        .child(userId)
                        .child(selectedOrder.orderId)
                        .setValue(selectedOrder)
                        .addOnSuccessListener {
                            // Remove from Order_details
                            db.getReference("Order_details")
                                .child(userId)
                                .child(selectedOrder.orderId)
                                .removeValue()
                                .addOnSuccessListener {
                                    orders = orders.filter { it.orderId != selectedOrder.orderId }
                                    Toast.makeText(context, "Order accepted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to remove order: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to complete order: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                })
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderDetail, onAcceptClicked: (OrderDetail) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Order ID and Payment ID
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Order ID: ${order.orderId}",
                    color = Color(0xFF0D3B66),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Payment ID: ${order.paymentId ?: "N/A"}",
                    color = Color(0xFF0D3B66),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User Details
            Text(
                "👤 User Details",
                color = Color(0xFF1B263B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Name:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF415A77))
                    Text(order.user.name, fontSize = 15.sp, color = Color(0xFF1B263B))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Phone:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF415A77))
                    Text(order.user.phone, fontSize = 15.sp, color = Color(0xFF1B263B))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Address:", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF415A77))
            Text(order.user.address, fontSize = 15.sp, color = Color(0xFF1B263B))

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary
            Text(
                "🛒 Order Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B263B)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Total Price: ₹${order.totalPrice}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0D3B66)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Products
            Text(
                "🧾 Products",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B263B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            order.products.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${it.name} (x${it.quantity})",
                        color = Color(0xFF243B55),
                        fontSize = 15.sp
                    )
                    Text(
                        "₹${it.price}",
                        color = Color(0xFF243B55),
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Accept Button
            Button(
                onClick = { onAcceptClicked(order) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "Accept",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
