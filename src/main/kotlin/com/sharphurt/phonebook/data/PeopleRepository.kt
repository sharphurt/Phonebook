package com.sharphurt.phonebook.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.sharphurt.phonebook.domain.PeopleDataStructure
import com.sharphurt.phonebook.domain.Person
import java.io.File

class PeopleRepository(val path: String) : IPeopleRepository {
    init {
        if (!File(path).exists())
            File(path).createNewFile()
    }

    private val gson: Gson = GsonBuilder().create()
    override fun getPeople(): ArrayList<Person> {
        try {
            val jsonFile = File(path).readText()
            JsonParser().parse(jsonFile)
        } catch (e: Exception) {
            if (!File(path).exists())
                File(path).createNewFile()
            File(path).writeText("")
        }
        if (File(path).readText() == "")
            return ArrayList()
        return gson.fromJson(File(path).readText(), PeopleDataStructure::class.java).data
    }

    override fun savePeople(list: ArrayList<Person>) =
        File(path).writeText(GsonBuilder().create().toJson(PeopleDataStructure(list)))
}
