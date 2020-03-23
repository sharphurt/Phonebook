package com.sharphurt.phonebook.localization

class RussianLanguage : ILanguage {
    override val commandInvitation = "> "
    override val clearWarningMessage =
        "Are you sure you want to clear your contact list? Enter \"Yes\" to confirm your action.\n$commandInvitation"
    override val contactNotExist = "Such the contact does not exist\n"
    override val emptyList = "List is empty. Type \"add\" command to add new people =)\n"
    override val enterNew = "Enter new %s:\n$commandInvitation"
    override val hello = "PhoneBook 1.0 by sharphurt\nFollow me on GitHub: https://github.com/sharphurt\n"
    override val incorrectCommand = "Incorrect command\n"
    override val incorrectParameter = "Parameter \"%s\" is incorrect. Try again\n"
    override val personInformation = "%d: %s - %s\n   Email: %s\n"
    override val processBroken = "Process was broken\n"
    override val success = "Success\n"
    override val updateFailed = "Unable to update value\n"
    override val wrongArgument = "Wrong command argument\n"
}