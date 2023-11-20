package es.unex.giiis.asee.tiviclone.data.model

import java.io.Serializable

data class User(
    val cod: Int = -1,
    val userName: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val userPassword: String = "",
    val addres: String = "",
    val city: String = "",
    val country: String = "",
    val telephone: String = ""
) : Serializable
