package com.example.appcrud


//ESTA CLASE VA A DEFINIR EL OBJ
data class Persona (var id: Long, var name: String = "", var password: Char,
                     var product: String = "", var price: Int, var isFinished: Boolean = false)
{

}