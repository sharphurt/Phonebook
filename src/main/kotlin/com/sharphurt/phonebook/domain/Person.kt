package com.sharphurt.phonebook.domain

data class Person(
    val id: Int = 0,
    var name: String = "",
    var phoneNumber: String = "",
    var email: String = ""
)