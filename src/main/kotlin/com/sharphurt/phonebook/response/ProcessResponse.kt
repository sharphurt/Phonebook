package com.sharphurt.phonebook.response

data class ProcessResponse(val isSuccess: Boolean = false, val processResult: String = "", val errorMessage: String = "")