package com.example.appcrud


interface OnClickListeners {
    fun onChecked(product: Product, appAdapter: AppAdapter) //evento del click normal
    fun onLongClick(product: Product, currentAdapter: AppAdapter) //Click largo para eliminar
}