package com.sharphurt.phonebook.viewModel

import com.sharphurt.phonebook.data.PeopleRepository
import com.sharphurt.phonebook.domain.Person

class ListModel {
    private val repository = PeopleRepository("phonebook.json")
    private var mutableItems = ArrayList<Person>()
    val items
        get() = mutableItems

    fun loadItems() {
        mutableItems = repository.getPeople()
    }

    fun add(item: Person) {
        items.add(item)
        repository.savePeople(items)
    }

    fun delete(index: Int) {
        items.removeAt(index)
        repository.savePeople(items)
    }

    fun tryUpdateName(index: Int, newName: String): Boolean {
        items[index].name = newName
        return true
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