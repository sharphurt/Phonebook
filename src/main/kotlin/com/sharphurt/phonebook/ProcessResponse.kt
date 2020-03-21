package com.sharphurt.phonebook

data class ProcessResponse(val isSuccess: Boolean = false, val processResult: String = "", val errorMessage: String = "")