package com.example.adminvirtudecor.model



data class Furniture(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val glbModelUrl: String = "",
    val category: String = ""
)
