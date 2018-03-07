package com.fhate.homefood.model

/* Дата класс-модель элемента корзины */
data class CartItem(val name: String, var price: Long, var count: Int, var imageUrl: String)