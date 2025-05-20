package com.example.adminvirtudecor.screen

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.adminvirtudecor.model.Furniture
import com.google.firebase.database.*
import kotlin.math.round


@Composable
fun ViewFurnitureScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dbRef = FirebaseDatabase.getInstance().getReference("furniture")

    var furnitureList by remember { mutableStateOf<List<Furniture>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Furniture>()
                for (categorySnap in snapshot.children) {
                    for (itemSnap in categorySnap.children) {
                        val furniture = itemSnap.getValue(Furniture::class.java)
                        if (furniture != null) {
                            tempList.add(furniture.copy(id = itemSnap.key ?: ""))
                        }
                    }
                }
                furnitureList = tempList
                loading = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorMsg = error.message
                loading = false
            }
        })
    }

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (errorMsg != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $errorMsg", color = MaterialTheme.colorScheme.error)
        }
        return
    }


    Text(
        text = "Furniture",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF333333)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(bottom = 20.dp,top=30.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(furnitureList.size) { index ->
            val furniture = furnitureList[index]

            FurnitureCard(
                furniture = furniture,
                onEdit = { updatedPrice, updatedDesc ->
                    val updateMap = mapOf<String, Any>(
                        "price" to updatedPrice.toString(),
                        "description" to updatedDesc
                    )
                    dbRef.child(furniture.category).child(furniture.id).updateChildren(updateMap)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to update: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                },
                onDelete = {
                    dbRef.child(furniture.category).child(furniture.id).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to delete: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FurnitureCard(
    furniture: Furniture,
    onEdit: (updatedPrice: Double, updatedDescription: String) -> Unit,
    onDelete: () -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    Spacer(Modifier.height(25.dp))

    Spacer(Modifier.height(25.dp))
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                furniture.images.forEach { imgUrl ->
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = furniture.name,
                        modifier = Modifier
                            .width(200.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = furniture.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Spacer(modifier = Modifier.height(4.dp))

            val price = furniture.price.toDoubleOrNull() ?: 0.0
            val roundedPrice = round(price * 100) / 100.0
            Text(
                text = "Price: $${"%.2f".format(roundedPrice)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = furniture.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color.Red)
                }

                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }

    if (showEditDialog) {
        EditFurnitureDialog(
            initialPrice = furniture.price,
            initialDescription = furniture.description,
            onDismiss = { showEditDialog = false },
            onSave = { newPriceStr, newDesc ->
                val newPrice = newPriceStr.toDoubleOrNull()
                if (newPrice != null) {
                    onEdit(newPrice, newDesc)
                    showEditDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFurnitureDialog(
    initialPrice: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var price by remember { mutableStateOf(initialPrice) }
    var description by remember { mutableStateOf(initialDescription) }
    val isSaveEnabled = price.isNotBlank() && description.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Furniture") },
        text = {
            Column {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (isSaveEnabled) onSave(price, description) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
