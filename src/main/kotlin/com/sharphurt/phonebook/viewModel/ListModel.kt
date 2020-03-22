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
        repository.savePeople(items)
        return true
    }

    fun validatePhoneNumber(number: String): Boolean =
        number.matches("^([+]?[\\s0-9]+)?(\\d{3}|[(]?[0-9]+[)])?([-]?[\\s]?[0-9])+$".toRegex())

    fun tryUpdatePhoneNumber(index: Int, number: String): Boolean {
        if (!validatePhoneNumber(number))
            return false
        items[index].phoneNumber = number
        repository.savePeople(items)
        return true
    }

    fun validateEmail(email: String): Boolean =
        email.matches("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+\$)".toRegex())

    fun tryUpdateEmail(index: Int, email: String): Boolean {
        if (!validateEmail(email))
            return false
        items[index].email = email
        repository.savePeople(items)
        return true
    }
}