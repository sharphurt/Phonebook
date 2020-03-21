package com.sharphurt.phonebook.viewController

import com.sharphurt.phonebook.enums.Commands
import com.sharphurt.phonebook.domain.Person
import com.sharphurt.phonebook.response.ProcessResponse
import com.sharphurt.phonebook.viewModel.ListModel
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ViewControllerTask {
    private val model = ListModel()

    init {
        model.loadItems()
    }

    fun run() {
        printList(model.items)
        while (true) {
            prepareForCommand("> ")
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

    private fun prepareForCommand(lineStartString: String) {
        print("\nType your command here: \n$lineStartString")
    }

    private fun awaitForCommand(): ProcessResponse =
        ProcessResponse(true, processResult = readLine() ?: "")

    private fun tryExecuteCommand(command: String): ProcessResponse {
        val commandArgs = command.toLowerCase().trimStart().split(" ")
        return when (commandArgs[0]) {
            Commands.SEE.command -> seeProcess()
            Commands.ADD.command -> addProcess()
            Commands.REMOVE.command -> removeProcess(commandArgs)
            Commands.EXIT.command -> exitProcess(0)
            else -> ProcessResponse(
                false,
                errorMessage = "Incorrect command"
            )
        }
    }

    private fun addProcess(): ProcessResponse {
        val inputArgs = arrayListOf<String>()
        val validators: Map<String, (String) -> Boolean> = mapOf(
            "name" to { _: String -> true },
            "phone" to { number: String -> model.validatePhoneNumber(number) },
            "email" to { email: String -> model.validateEmail(email) }
        )
        validators.forEach {
            print("Enter ${it.key}:\n> ")
            val input = readLine() ?: ""
            if (it.value(input))
                inputArgs.add(input)
            else
                return ProcessResponse(
                    false, errorMessage =
                    "Parameter \"${it.key}\" is incorrect. Adding process was broken. Try again"
                )
        }
        model.add(Person(model.items.size + 1, inputArgs[0], inputArgs[1], inputArgs[2]))
        return ProcessResponse(true)
    }

    private fun removeProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 1)
            return ProcessResponse(
                false,
                errorMessage = "Wrong command argument"
            )

        val indexToRemove = (commandArgs[1].toIntOrNull()?.minus(1) ?: return ProcessResponse(
            false,
            errorMessage = "Wrong command argument"
        ))

        if (indexToRemove <= 0 || indexToRemove >= model.items.size)
            return ProcessResponse(
                false,
                errorMessage = "Such the contact does not exist"
            )
        model.delete(indexToRemove)
        return ProcessResponse(true)
    }

    private fun seeProcess(): ProcessResponse {
        printList(model.items)
        return ProcessResponse(true)
    }
}
