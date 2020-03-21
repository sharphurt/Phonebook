package com.sharphurt.phonebook

data class Person(
    val id: Int = 0,
    var name: String = "",
    var phoneNumber: String = "",
    var email: String = ""
) {
    object ClassInformation {
        fun getConstructorArguments(): ArrayList<String> = arrayListOf("name", "phone", "email")
    }

    override fun toString(): String =
        "$id: $name - $phoneNumber\n   Email: $email\n"

}