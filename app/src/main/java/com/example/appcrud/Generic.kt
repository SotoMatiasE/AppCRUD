package com.example.appcrud

import java.sql.Blob

//ESTA CLASE VA A DEFINIR EL OBJ
/*data class Persona (var id: Long = 0, var name: String = "", var password: Char,
                    var isFinished: Boolean = false)
{

}*/

data class Product(
    var idProduct: Long = 0,
    var productName: String = "",
    var isFinished: Boolean = false
)
{

}

data class Image(var idImg: Long = 0, var image: String = ""){


}

