package com.jetbrains.handson.httpapi.routes

import com.jetbrains.handson.httpapi.models.Customer
import com.jetbrains.handson.httpapi.models.customerStorage
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.registerCustomerRoutes() {
    routing {
        customerRouting()
    }
}

fun Route.customerRouting() {
    route("/customers") {
        get {
            if (customerStorage.isNotEmpty()) {
                call.respond(customerStorage)
            } else {
                call.respond(HttpStatusCode.NotFound, "No customers found")
            }
        }

        get("{id}") {
            val id = call.parameters["id"] ?: return@get call.respondText("Missing or malformed id", status = HttpStatusCode.BadRequest)
            val customer = customerStorage.find { customer -> customer.id == id } ?:
            return@get call.respondText("No customer with id $id", status = HttpStatusCode.NotFound)

            call.respond(customer)
        }

        post {
            val customer = call.receive<Customer>()
            customerStorage.add(customer)
            call.respondText("Customer stored correctly", status = HttpStatusCode.Created)
        }

        delete("{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (customerStorage.removeIf { customer -> customer.id == id}) {
                call.respondText("Customer removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}