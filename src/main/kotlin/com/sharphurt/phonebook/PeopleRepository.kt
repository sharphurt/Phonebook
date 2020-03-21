package com.sharphurt.phonebook

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

class PeopleRepository(private val path: String) : IRepository<Person> {
    private val gson: Gson = GsonBuilder().create()

    override fun getRepository(): ArrayList<Person> =
        gson.fromJson(File(path).readText(), PeopleDataStructure::class.java).data
    override fun saveRepository(list: ArrayList<Person>) =
        File(path).writeText(GsonBuilder().create().toJson(PeopleDataStructure(list)))
}
