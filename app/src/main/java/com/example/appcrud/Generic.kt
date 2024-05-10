package com.example.appcrud


//ESTA CLASE VA A DEFINIR EL OBJ
data class Persona (var id: Long, var name: String = "", var password: Char,
                     var isFinished: Boolean = false)
{

}

data class Product (var idProd: Long, var product: String = "", var isFinished: Boolean = false){

}