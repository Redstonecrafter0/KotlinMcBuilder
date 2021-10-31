package net.redstonecraft.kotlinmc.bungee

import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Plugin

class KotlinBungee : Plugin() {

    companion object {

        @Suppress("SpellCheckingInspection")
        val asciiLogo = """ccccccllllllllllloddddddddddddddddddxxOX
ccccccccclllllloodddddddddddddddxddxOX
ccccccccccllllodddddddddddddddddxxOX
ccccccccccclodddddddddddddddddxxOX
cccccccccclodddddddddddddddxdxOX
cccccccclodddddddddddddddddxOX
cccccclodddddddddddddddddxOX
cccclooddddddddddddddddxOX
cclooddddddddddddddddxOX
cloooooddddddddddddxOK
oooooooooodddddddooodxK
ooooooooooooddooollllloxK
oooooooooooooolllllllllloxK
oooooooooooolllllllllllllloxK
oooooooooollllllllllllllllllokK
ooooooolllccclllllllllllllllllokK
ooooollcccccccclllllllllllllllllokK
lolllcccccccccccclllllllllllllllllokK
llccccccccccccccccclllllllllllllllllokK
lccccccccccccccccccccllllllllllllllllldO"""

        fun createMessage(message: String): List<String> {
            val list = mutableListOf<String>()
            list.add("┏" + "━" * 47 + "┓")
            list.add("┃ " + message + " " * (45 - message.length) + " ┃")
            list.add("┣" + "━" * 47 + "┫")
            asciiLogo.split("\n").forEach {list.add("┃ " + it + " " * (45 - it.length) + " ┃")}
            list.add("┗" + "━" * 47 + "┛")
            return list
        }

        fun sendMessage(message: String) {
            createMessage(message).forEach {ProxyServer.getInstance().console.sendMessage(*TextComponent.fromLegacyText(it))}
        }

    }

    override fun onLoad() {
        sendMessage("Kotlin was loaded.")
    }

    override fun onEnable() {
        sendMessage("Kotlin was enabled.")
    }

    override fun onDisable() {
        sendMessage("Kotlin was disabled.")
    }

}

private operator fun String.times(n: Int): String = repeat(n)
