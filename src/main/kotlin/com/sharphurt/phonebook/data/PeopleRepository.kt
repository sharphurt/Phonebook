package com.sharphurt.phonebook.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sharphurt.phonebook.domain.PeopleDataStructure
import com.sharphurt.phonebook.domain.Person
import java.io.File

class PeopleRepository(private val path: String) : IPeopleRepository {
    private val gson: Gson = GsonBuilder().create()

    override fun getPeople(): ArrayList<Person> =
        gson.fromJson(File(path).readText(), PeopleDataStructure::class.java).data

    override fun savePeople(list: ArrayList<Person>) =
        File(path).writeText(GsonBuilder().create().toJson(PeopleDataStructure(list)))
}
