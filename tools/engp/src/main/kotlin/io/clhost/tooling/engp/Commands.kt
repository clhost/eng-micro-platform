package io.clhost.tooling.engp

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import io.clhost.extension.cli.Shell
import io.clhost.extension.cli.ensureNotNull
import io.clhost.extension.cli.read
import java.io.File

val services = listOf("postgres", "kafka", "redis")
val applications = listOf("eng", "eng-telegram")

class StartupCommand : CliktCommand(
    name = "startup",
    help = "Startup the environment, including minikube with infrastructure services, local registry and applications"
) {

    override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./startup.sh"))
}

class ShutdownCommand : CliktCommand(
    name = "shutdown",
    help = "Shutdown the environment, including minikube with infrastructure services, local registry and applications"
) {

    override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./shutdown.sh"))
}

class AppCommand : CliktCommand(
    name = "app",
    help = "A set of commands to work with applications"
) {

    init {
        subcommands(RecreateCommand(), RedeployCommand(), VersionsCommand())
    }

    override fun run() {}

    class RecreateCommand : CliktCommand(
        name = "recreate",
        help = "Rebuild and deploy an application with new version"
    ) {

        private val app by option("-a", "--app")
            .help("An application name")
            .choice(*applications.toTypedArray())
            .ensureNotNull()

        override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./app-recreate.sh", app))
    }

    class RedeployCommand : CliktCommand(
        name = "redeploy",
        help = "Just redeploy an application (with new version or not)"
    ) {

        private val app by option("-a", "--app")
            .help("An application name")
            .choice(*applications.toTypedArray())
            .ensureNotNull()

        private val newVersion by option("-n", "--new-version")
            .flag(default = false)
            .help("Should be redeployed with new next version")

        override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./app-redeploy.sh", app, "$newVersion"))
    }

    class VersionsCommand : CliktCommand(
        name = "versions",
        help = "Print versions of applications"
    ) {

        override fun run() = echo(File(ENGP_APP_VERSIONS_FILE).read())
    }
}

class EnvCommand : CliktCommand(
    name = "env",
    help = "A set of commands to work with environment"
) {

    init {
        subcommands(DeployCommand(), StopCommand())
    }

    override fun run() {}

    class DeployCommand : CliktCommand(
        name = "deploy",
        help = "Deploy the specified infrastructure service"
    ) {

        private val service by option("-s", "--service")
            .help("An infrastructure service name")
            .choice(*services.toTypedArray())
            .ensureNotNull()

        override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./service-deploy.sh", service))
    }

    class StopCommand : CliktCommand(
        name = "stop",
        help = "Stop the specified infrastructure service"
    ) {

        private val service by option("-s", "--service")
            .help("An infrastructure service name")
            .choice(*services.toTypedArray())
            .ensureNotNull()

        override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./service-stop.sh", service))
    }
}

class RequirementsCommand : CliktCommand(
    name = "requirements",
    help = "Check that all requirements to work with environment is satisfied"
) {

    override fun run() = Shell.on(ENGP_SOURCE_DIR).invokeSilent(listOf("./ensure-requirements.sh"))
}
