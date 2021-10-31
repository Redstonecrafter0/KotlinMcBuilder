package net.redstonecraft.kotlinmcbuilder

import net.redstonecraft.kotlinmcbuilder.handlers.MavenRequestHandler
import net.redstonecraft.kotlinmcbuilder.handlers.PlainErrorHandler
import net.redstonecraft.redstoneapi.core.utils.NumberUtils
import net.redstonecraft.redstoneapi.webserver.WebServer

object Main {

    lateinit var webserver: WebServer
    val errorHandler = PlainErrorHandler()

}

fun main(args: Array<String>) {
    val port = if (args.size == 1 && NumberUtils.toInt(args[0]) != null) NumberUtils.toInt(args[0]) else 80
    Utils.build.mkdirs()
    Utils.dist.mkdirs()
    Main.webserver = WebServer("", port, true, Main.errorHandler, ".")
    Main.webserver.addHandler(MavenRequestHandler())
    println("Available on http://localhost:$port/")
}
