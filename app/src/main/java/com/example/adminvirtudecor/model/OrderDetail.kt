package com.example.adminvirtudecor.model



data class OrderDetail(
    val orderId: String = "",
    val paymentId: String? = null,
    val orderDate: Long = 0L,
    val user: User = User(),
    val products: List<OrderProduct> = emptyList(),
    val totalPrice: Double = 0.0
)
