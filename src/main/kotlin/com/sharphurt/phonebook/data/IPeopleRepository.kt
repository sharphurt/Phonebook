package com.sharphurt.phonebook.data

import com.sharphurt.phonebook.domain.Person

interface IPeopleRepository {
    fun getPeople(): ArrayList<Person>
    fun savePeople(list: ArrayList<Person>)
}