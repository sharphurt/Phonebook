package com.sharphurt.phonebook


import java.lang.Exception
import kotlin.collections.ArrayList

class IOTask() {
    private val model = ListModel()

    init {
        printList(model.items)
        while (true) {
            prepareForCommand("> ")
            val result = awaitForCommand()
            if (result.isSuccess) {
                val executionResult = tryExecuteCommand(result.processResult)
                if (executionResult.isSuccess)
                    println("Success")
                else
                    println(result.errorMessage)
            }
        }
    }

    private fun printList(list: ArrayList<Person>) {
        list.forEach { println(it.toString()) }
    }

    private fun prepareForCommand(lineStartString: String) {
        print("\nType your command here: \n$lineStartString")
    }

    private fun awaitForCommand(): ProcessResponse {
        val input = readLine()
        return ProcessResponse(input != null, processResult = input ?: "")
    }

    private fun tryExecuteCommand(command: String): ProcessResponse {
        val commandArgs = command.toLowerCase().trimStart().split(" ")
        return when (commandArgs[0]) {
            "see" -> {
                printList(model.items)
                ProcessResponse(true)
            }
            "add" -> addingProcess()
            "remove" -> removingProcess(commandArgs)
            else -> ProcessResponse(false, errorMessage = "Incorrect command")
        }
    }

    private fun addingProcess(): ProcessResponse {
        val inputArgs = arrayListOf<String>()
        val validators: Map<String, (String) -> Boolean> = mapOf(
            "name" to { _: String -> true },
            "phone" to { number: String -> model.validatePhoneNumber(number) },
            "email" to { email: String -> model.validateEmail(email) }
        )
        Person.ClassInformation.getConstructorArguments().forEach {
            print("Enter $it:\n> ")
            val input = readLine() ?: ""
            if (!validators.containsKey(it))
                throw Exception("There is no validate method for parameter \"$it\"")
            if (validators[it]?.invoke(input) == true)
                inputArgs.add(input)
            else
                return ProcessResponse(
                    false,
                    errorMessage = "Parameter \"$it\" is incorrect. Adding process was broken. Try again"
                )
        }
        model.add(Person(model.items.size + 1, inputArgs[0], inputArgs[1], inputArgs[2]))
        return ProcessResponse(true)
    }

    private fun removingProcess(commandArgs: List<String>): ProcessResponse {
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
}
