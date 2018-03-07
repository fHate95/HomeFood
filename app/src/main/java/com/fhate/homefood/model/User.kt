package com.fhate.homefood.model

/* Дата класс-модель пользователя */
data class User(var name: String, var address: String, var number: String, var orderCount: Long, var totalPrice: Long)