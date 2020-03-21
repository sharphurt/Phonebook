package com.sharphurt.phonebook

class ListModel {
    private val repository = PeopleRepository("phonebook.json")
    val items = repository.getRepository()

    fun add(item: Person) {
        items.add(item)
        repository.saveRepository(items)
    }

    fun delete(index: Int) {
        items.removeAt(index)
        repository.saveRepository(items)
    }

    fun updateName(index: Int, newName: String) {
        items[index].name = newName
    }

    fun validatePhoneNumber(number: String): Boolean = number.matches("((\\+7|7|8)+([0-9]){10})\$".toRegex())

    fun tryUpdatePhoneNumber(index: Int, number: String): Boolean {
        if (!validatePhoneNumber(number))
            return false
        items[index].phoneNumber = number
        return true
    }

    fun validateEmail(email: String): Boolean =
        email.matches("^([a-zA-Z0-9_\\-.]+)@([a-zA-Z0-9_\\-.]+)\\.([a-zA-Z]{2,5})\$".toRegex())

    fun tryUpdateEmail(index: Int, email: String): Boolean {
        if (!validateEmail(email))
            return false
        items[index].email = email
        return true

    }
}