package com.sharphurt.phonebook.viewController

import com.sharphurt.phonebook.enums.Commands
import com.sharphurt.phonebook.domain.Person
import com.sharphurt.phonebook.localization.RussianLanguage
import com.sharphurt.phonebook.response.ProcessResponse
import com.sharphurt.phonebook.viewModel.ListModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class ViewController(val writeFunc: (String) -> Unit, val readFunc: () -> String) {
    private val model = ListModel()
    private val lang = RussianLanguage()
    private val formatter = Formatter()
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
        writeFunc(lang.hello)
        printList(model.items)
        while (true) {
            prepareForCommand()
            val result = awaitForCommand()
            if (result.isSuccess) {
                val executionResult = tryExecuteCommand(result.processResult)
                if (executionResult.isSuccess) {
                    writeFunc(lang.success)
                    if (executionResult.processResult != "")
                        writeFunc(executionResult.processResult)
                } else
                    writeFunc(executionResult.errorMessage)
            }
        }
    }

    private fun printList(list: ArrayList<Person>) {
        if (model.items.isNotEmpty())
            list.forEachIndexed { index, it ->
                writeFunc(lang.personInformation.format(index + 1, it.name, it.phoneNumber, it.email))
            }
        else
            writeFunc(lang.emptyList)
    }

    private fun prepareForCommand() {
        writeFunc(lang.commandInvitation)
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
            else -> ProcessResponse(false, errorMessage = lang.incorrectCommand)
        }
    }

    private fun addProcess(): ProcessResponse {
        val inputArgs = arrayListOf<String>()
        validators.forEach {
            writeFunc(lang.enterNew.format(it.key))
            val input = readFunc()
            if (input == Commands.BREAK.command)
                return ProcessResponse(false, errorMessage = lang.processBroken)
            if (it.value(input))
                inputArgs.add(input)
            else
                return ProcessResponse(false, errorMessage = lang.incorrectParameter.format(it.key))
        }
        model.add(Person(inputArgs[0], inputArgs[1], inputArgs[2]))
        return ProcessResponse(true)
    }

    private fun removeProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 1)
            return ProcessResponse(false, errorMessage = lang.wrongArgument)

        val indexToRemove = (commandArgs[1].toIntOrNull()?.minus(1)
            ?: return ProcessResponse(false, errorMessage = lang.wrongArgument))

        if (indexToRemove < 0 || indexToRemove >= model.items.size)
            return ProcessResponse(false, errorMessage = lang.contactNotExist)

        model.delete(indexToRemove)
        return ProcessResponse(true)
    }

    private fun clearProcess(): ProcessResponse {
        writeFunc(lang.clearWarningMessage)
        val input = readFunc()
        return if (input == Commands.YES.command) {
            model.clear()
            ProcessResponse(true)
        } else
            ProcessResponse(false, errorMessage = lang.processBroken)
    }

    private fun seeProcess(): ProcessResponse {
        printList(model.items)
        return ProcessResponse(true)
    }

    private fun updateProcess(commandArgs: List<String>): ProcessResponse {
        if (commandArgs.size <= 2)
            return ProcessResponse(false, errorMessage = lang.wrongArgument)
        if (!validators.containsKey(commandArgs[1]) || commandArgs[1].isEmpty())
            return ProcessResponse(false, errorMessage = lang.contactNotExist)

        val indexToUpdate = (commandArgs[2].toIntOrNull()?.minus(1)
            ?: return ProcessResponse(false, errorMessage = lang.wrongArgument))
        if (indexToUpdate < 0 || indexToUpdate >= model.items.size)
            return ProcessResponse(false, errorMessage = lang.contactNotExist)

        writeFunc(lang.enterNew.format(commandArgs[1]))
        val input = readFunc()
        var updateResult = false
        if (validators[commandArgs[1]]?.invoke(input)!!)
            updateResult = updaters[commandArgs[1]]?.invoke(indexToUpdate, input)!!

        return if (updateResult) ProcessResponse(true) else ProcessResponse(
            false, errorMessage = lang.updateFailed
        )
    }
}
