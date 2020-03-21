package com.sharphurt.phonebook

interface IRepository<T> {
    fun getRepository(): ArrayList<T>
    fun saveRepository(list: ArrayList<T>)
}