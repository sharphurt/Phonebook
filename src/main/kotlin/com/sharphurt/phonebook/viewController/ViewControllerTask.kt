package com.sharphurt.phonebook.viewController

import com.sharphurt.phonebook.enums.Commands
import com.sharphurt.phonebook.domain.Person
import com.sharphurt.phonebook.response.ProcessResponse
import com.sharphurt.phonebook.viewModel.ListModel
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ViewControllerTask {
    private val lineStartString = "> "
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
                    println("Success")
                else
                    println(executionResult.errorMessage)
            }
        }
    }

    private fun printList(list: ArrayList<Person>) {
        list.forEach { println("${it.id}: ${it.name} - ${it.phoneNumber}\n   Email: ${it.email}\n") }
    }

    private fun prepareForCommand() {
        print("Type your command here: \n$lineStartString")
    }

    private fun awaitForCommand(): ProcessResponse =
        ProcessResponse(true, processResult = readLine() ?: "")

    private fun tryExecuteCommand(command: String): ProcessResponse {
        val commandArgs = command.toLowerCase().trimStart().split(" ")
        return when (commandArgs[0]) {
            Commands.SEE.command -> seeProcess()
            Commands.ADD.command -> addProcess()
            Commands.REMOVE.command -> removeProcess(commandArgs)
            Commands.UPDATE.command -> updateProcess(commandArgs)
            Commands.EXIT.command -> exitProcess(0)
            else -> ProcessResponse(
                false,
                errorMessage = "Incorrect command"
            )
        }
    }

    private fun addProcess(): ProcessResponse {
        val inputArgs = arrayListOf<String>()
        validators.forEach {
            print("Enter ${it.key}:\n$lineStartString")
            val input = readLine() ?: ""
            if (it.value(input))
                inputArgs.add(input)
            else
                return ProcessResponse(
                    false,
                    errorMessage = "Parameter \"${it.key}\" is incorrect. Adding process was broken. Try again"
                )
        }
        model.add(Person(model.items.size + 1, inputArgs[0], inputArgs[1], inputArgs[2]))
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

    private fun seeProcess(): ProcessResponse {
        printList(model.items)
        return ProcessResponse(true)
    }

    private fun updateProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 2)
            return ProcessResponse(false, errorMessage = "Wrong command argument")
        if (!validators.containsKey(commandArgs[1]) || commandArgs[1].isEmpty())
            return ProcessResponse(false, "Such the field does not exist")

        val indexToUpdate = (commandArgs[2].toIntOrNull()?.minus(1)
            ?: return ProcessResponse(false, errorMessage = "Wrong command argument"))
        if (indexToUpdate < 0 || indexToUpdate >= model.items.size)
            return ProcessResponse(false, errorMessage = "Such the contact does not exist")

        print("Enter new ${commandArgs[1]}:\n$lineStartString")
        val input = readLine() ?: ""
        var updateResult = false
        if (validators[commandArgs[1]]?.invoke(input)!!)
            updateResult = updaters[commandArgs[1]]?.invoke(indexToUpdate, input)!!

        return if (updateResult) ProcessResponse(true) else ProcessResponse(false,
            errorMessage = "Failed to update value")

    }
}
