package com.sgmobile.earthquake

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.bson.Document

fun main() {
    embeddedServer(
        factory = Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

private fun Application.module() {
    connectToMongoDB()
    configureSerialization()
    configureRouting()
}

private fun connectToMongoDB() {
    // Replace the placeholders with your credentials and hostname
    val connectionString = "mongodb+srv://serkangurel07:tOQUtTM7VADCSFst@cluster0.8bsiv0m.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()
    // Create a new client and connect to the server
    MongoClient.create(mongoClientSettings).use { mongoClient ->
        val database = mongoClient.getDatabase("admin")
        runBlocking {
            database.runCommand(Document("ping", 1))
            mongoClient.close()
        }
        println("Pinged your deployment. You successfully connected to MongoDB!")
    }
}

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

private fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Ktor")
        }
    }
}