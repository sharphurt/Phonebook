package com.sharphurt.phonebook.viewController

import com.sharphurt.phonebook.enums.Commands
import com.sharphurt.phonebook.domain.Person
import com.sharphurt.phonebook.response.ProcessResponse
import com.sharphurt.phonebook.viewModel.ListModel
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ViewController(val writeFunc: (String) -> Unit, val readFunc: () -> String, private val lineStarting: String) {
    private val model = ListModel()
    private val validators: Map<String, (String) -> Boolean> = mapOf(
        "name" to { _: String -> true },
        "phone" to { number: String -> model.validatePhoneNumber(number) },
        "email" to { email: String -> model.validateEmail(email) }
    )

    private val updaters: Map<String, (Int, String) -> Boolean> = mapOf(
        "name" to { index: Int, newName: String -> model.tryUpdateName(index, newName) },
        "phone" to { index: Int, newPhone: String -> model.tryUpdatePhoneNumber(index, newPhone) },
        "email" to { index: Int, newEmail: String -> model.tryUpdateEmail(index, newEmail) }
    )

    init {
        model.loadItems()
    }

    fun run() {
        printList(model.items)
        while (true) {
            prepareForCommand()
            val result = awaitForCommand()
            if (result.isSuccess) {
                val executionResult = tryExecuteCommand(result.processResult)
                if (executionResult.isSuccess)
                    writeFunc("Success\n${if (executionResult.processResult != "") executionResult.processResult else ""}")
                else
                    writeFunc("${executionResult.errorMessage}\n")
            }
        }
    }

    private fun printList(list: ArrayList<Person>) {
        if (model.items.isNotEmpty())
            list.forEachIndexed { index, it ->
            writeFunc("${index + 1}: ${it.name} - ${it.phoneNumber}\n   Email: ${it.email}\n")
        }
        else
            writeFunc("List is empty. Type \"add\" command to add new people =)\n")
    }

    private fun prepareForCommand() {
        writeFunc("Type your command here: \n$lineStarting")
    }

    private fun awaitForCommand(): ProcessResponse = ProcessResponse(true, processResult = readFunc())

    private fun tryExecuteCommand(command: String): ProcessResponse {
        val commandArgs = command.toLowerCase().split(" ")
        return when (commandArgs[0]) {
            Commands.SEE.command -> seeProcess()
            Commands.ADD.command -> addProcess()
            Commands.REMOVE.command -> removeProcess(commandArgs)
            Commands.DELETE.command -> removeProcess(commandArgs)
            Commands.CLEAR.command -> clearProcess()
            Commands.UPDATE.command -> updateProcess(commandArgs)
            Commands.EXIT.command -> exitProcess(0)
            else -> ProcessResponse(false, errorMessage = "Incorrect command")
        }
    }

    private fun addProcess(): ProcessResponse {
        val inputArgs = arrayListOf<String>()
        validators.forEach {
            writeFunc("Enter ${it.key}:\n$lineStarting")
            val input = readFunc()
            if (input == "\\break")
                return ProcessResponse(false, errorMessage = "Process was broken")
            if (it.value(input))
                inputArgs.add(input)
            else
                return ProcessResponse(false, errorMessage = "Parameter \"${it.key}\" is incorrect. Try again")
        }
        model.add(Person(inputArgs[0], inputArgs[1], inputArgs[2]))
        return ProcessResponse(true)
    }

    private fun removeProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 1)
            return ProcessResponse(false, errorMessage = "Wrong command argument")

        val indexToRemove = (commandArgs[1].toIntOrNull()?.minus(1)
            ?: return ProcessResponse(false, errorMessage = "Wrong command argument"))

        if (indexToRemove < 0 || indexToRemove >= model.items.size)
            return ProcessResponse(false, errorMessage = "Such the contact does not exist")

        model.delete(indexToRemove)
        return ProcessResponse(true)
    }

    private fun clearProcess(): ProcessResponse {
        writeFunc("Are you sure you want to clear your contact list? Enter \"Yes\" to confirm your action.\n$lineStarting")
        val input = readFunc()
        return if (input == "yes") {
            model.clear()
            ProcessResponse(true)
        } else
            ProcessResponse(false, errorMessage = "Process was broken")
    }

    private fun seeProcess(): ProcessResponse {
        printList(model.items)
        return ProcessResponse(true)
    }

    private fun updateProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 2)
            return ProcessResponse(false, errorMessage = "Wrong command argument")
        if (!validators.containsKey(commandArgs[1]) || commandArgs[1].isEmpty())
            return ProcessResponse(false, errorMessage = "Such the field does not exist")

        val indexToUpdate = (commandArgs[2].toIntOrNull()?.minus(1)
            ?: return ProcessResponse(false, errorMessage = "Wrong command argument"))
        if (indexToUpdate < 0 || indexToUpdate >= model.items.size)
            return ProcessResponse(false, errorMessage = "Such the contact does not exist")

        writeFunc("Enter new ${commandArgs[1]}:\n$lineStarting")
        val input = readFunc()
        var updateResult = false
        if (validators[commandArgs[1]]?.invoke(input)!!)
            updateResult = updaters[commandArgs[1]]?.invoke(indexToUpdate, input)!!

        return if (updateResult) ProcessResponse(true) else ProcessResponse(
            false, errorMessage = "Failed to update value"
        )
    }
}
