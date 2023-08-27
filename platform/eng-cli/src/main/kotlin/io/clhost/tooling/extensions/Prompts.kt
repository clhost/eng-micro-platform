package io.clhost.tooling.extensions

val String?.isY: Boolean
    get() = "y".equals(this, ignoreCase = true)

val String?.isN: Boolean
    get() = "n".equals(this, ignoreCase = true)
