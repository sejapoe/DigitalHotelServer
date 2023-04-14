package ru.sejapoe.application

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import kotlinx.html.*
import org.slf4j.event.Level
import ru.sejapoe.application.db.DatabasesFactory
import ru.sejapoe.application.plugins.configureRouting
import ru.sejapoe.application.plugins.configureSerialization
import java.io.File
import java.security.KeyStore

fun HTML.index() {
    head {
        title("Hello from Ktor!")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "/static/DigitalHotelServer.js") {}
    }
}

fun main() {
    val isProduction = System.getenv().getOrDefault("KOTLIN_ENV", "production") == "production"
    val keyStorePassword = System.getenv().getOrDefault("KEYSTORE_PASSWORD", "")


    val environment = applicationEngineEnvironment {
        if (isProduction) {
            val keyStoreFile = File("/keys/keystore.jks")
            val keyStore = KeyStore.getInstance(keyStoreFile, keyStorePassword.toCharArray())
            sslConnector(
                keyStore = keyStore,
                keyAlias = "server",
                keyStorePassword = { keyStorePassword.toCharArray() },
                privateKeyPassword = { keyStorePassword.toCharArray() }
            ) {
                port = 443
                keyStorePath = keyStoreFile
            }
        } else {
            developmentMode = true
            connector {
                port = 8080
            }
        }
        module(Application::module)
    }

    DatabasesFactory.init(isProduction)
    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    configureRouting()
    configureSerialization()
}
