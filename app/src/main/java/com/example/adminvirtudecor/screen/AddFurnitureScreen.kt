package com.example.adminvirtudecor.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.adminvirtudecor.model.Furniture
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFurnitureScreen(navController: NavHostController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var imageUris by remember { mutableStateOf(listOf<Uri>()) }
    var glbUri by remember { mutableStateOf<Uri?>(null) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val categories = listOf("Chair", "HomeDecor", "Bed", "Sofa")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var expanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris != null && uris.isNotEmpty()) {
            imageUris = uris
        }
    }

    val glbPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        glbUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Add Furniture",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Furniture Name", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price (e.g., 99.99)", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description", color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            textStyle = LocalTextStyle.current.copy(color = Color.Black)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                label = { Text("Select Category", color = Color.DarkGray) },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                textStyle = LocalTextStyle.current.copy(color = Color.Black)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category,
                                color = if (category == selectedCategory) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = "Select Images", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imageUris) { uri ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(uri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { glbPickerLauncher.launch("*/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = if (glbUri == null) "Select .glb File" else "GLB Selected", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        glbUri?.let {
            Text(
                text = "Selected GLB File: ${it.lastPathSegment ?: "File"}",
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isBlank() || price.isBlank() || description.isBlank()) {
                    uploadMessage = "Please fill all fields"
                    return@Button
                }
                if (imageUris.isEmpty()) {
                    uploadMessage = "Please select at least one image"
                    return@Button
                }
                if (glbUri == null) {
                    uploadMessage = "Please select a .glb file"
                    return@Button
                }

                isUploading = true
                uploadMessage = null

                scope.launch {
                    try {
                        val imageUrls = imageUris.map { uri ->
                            uploadFileToFirebaseStorage(uri, "furniture_images")
                        }

                        val glbUrl = uploadFileToFirebaseStorage(glbUri!!, "furniture_models")

                        val dbRef = FirebaseDatabase.getInstance()
                            .getReference("furniture")
                            .child(selectedCategory)

                        val newKey = dbRef.push().key ?: UUID.randomUUID().toString()

                        val furniture = Furniture(
                            id = newKey,
                            name = name.trim(),
                            price = price.trim(),
                            description = description.trim(),
                            images = imageUrls,
                            glbModelUrl = glbUrl,
                            category = selectedCategory
                        )

                        dbRef.child(newKey).setValue(furniture).await()

                        uploadMessage = "Furniture added successfully!"

                        name = ""
                        price = ""
                        description = ""
                        imageUris = emptyList()
                        glbUri = null

                    } catch (e: Exception) {
                        uploadMessage = "Upload failed: ${e.localizedMessage}"
                    } finally {
                        isUploading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!isUploading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = if (isUploading) "Uploading..." else "Add Furniture",
                color = Color.White
            )
        }

        uploadMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = if (it.contains("successfully")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

private suspend fun uploadFileToFirebaseStorage(uri: Uri, folderName: String): String {
    val storageRef = FirebaseStorage.getInstance().reference.child("$folderName/${UUID.randomUUID()}")
    storageRef.putFile(uri).await()
    return storageRef.downloadUrl.await().toString()
}
