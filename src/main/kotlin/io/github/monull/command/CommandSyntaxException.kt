package io.github.monull.command

class CommandSyntaxException(val syntaxMessage: String) : Exception(syntaxMessage)