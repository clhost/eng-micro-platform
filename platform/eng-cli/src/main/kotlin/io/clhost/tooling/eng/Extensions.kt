package io.clhost.tooling.eng

import io.clhost.extension.cli.Shell
import io.clhost.extension.cli.asString

fun Shell.minikubeIp() = invoke(listOf("minikube", "ip")).asString.replace("([\r\n])+".toRegex(), "")
