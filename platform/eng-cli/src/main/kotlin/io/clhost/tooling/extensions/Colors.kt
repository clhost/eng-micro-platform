package io.clhost.tooling.extensions

val String?.red: String
    get() = "\u001B[31m$this\u001B[0m"

val String?.green: String
    get() = "\u001B[32m$this\u001B[0m"

val String?.yellow: String
    get() = "\u001B[33m$this\u001B[0m"

val String?.lightCyan: String
    get() = "\u001B[96m$this\u001B[0m"
