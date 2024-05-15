package com.example.appcrud

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcrud.databinding.ItemGaleryBinding
import com.example.appcrud.databinding.ItemProductBinding

class ImageAdapter (private var imageList: MutableList<Image>, private val listener: GaleriaApp) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>()
{

    private lateinit var context: Context //extreaemos contexto para poder acceder a los recursos
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ViewHolder {
        context = parent.context

        val view =  LayoutInflater.from(context).inflate(R.layout.item_galery, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageAdapter.ViewHolder, position: Int) {
        val image = imageList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int  = imageList.size
        //VA A INDICAR CUANTOS ELEMENTOS QUEREMOS VER EN EL LISTADO


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemGaleryBinding.bind(view)

        fun bind(image: Image) {
            /*Glide.with(context)
                .load(image.imageCap)
                .placeholder(R.drawable.placeholder_image) // Placeholder image while loading (optional)
                .error(R.drawable.error_image) // Image to display if loading fails (optional)
                .into(binding.ivGalery)

            binding.ivGalery.setOnClickListener {
                listener.onImageClick(image)
            }*/
        }

    }

}