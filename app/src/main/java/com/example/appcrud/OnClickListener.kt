package com.example.appcrud


interface OnClickListener {
    fun onChecked(prod: Product, appAdapter: ProductAdapter) //evento del click normal
    fun onLongClick(prod: Product, currentAdapter: ProductAdapter) //Click largo para eliminar
}