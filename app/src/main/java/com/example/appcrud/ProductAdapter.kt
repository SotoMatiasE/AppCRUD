package com.example.appcrud


import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.appcrud.databinding.ItemProductBinding

class ProductAdapter(private var prodList: MutableList<Product>, private val listener: MenuIngreso) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>()
{
    private lateinit var context: Context //extreaemos contexto para poder acceder a los recursos


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        //inflamos la view y la mandamos al return
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    fun getAllProducts(): List<Product> {
        return prodList.toList() // Devuelve una copia de la lista de productos
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val prod = prodList[position]

        //CONFIGURACION DEL LISTENER DE inner class ViewHolder
        holder.setListener(prod)

        //accede a la vista activity_main id tvDecription
        holder.binding.tvDescription.text = prod.productName

        //si tiene el valor cel check en true se auto selecciona
        holder.binding.cbFinished.isChecked = prod.isFinished

        //PREGUNTA SI LA NOTE ESTA FINALIZADA Y MODIFICA EN TIEMPO REAL
        if (prod.isFinished){ //si esta finalizada cambia el tamaño del texto
            holder.binding.tvDescription.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_finished_size).toFloat())
        }else {
            holder.binding.tvDescription.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_default_size).toFloat())
        }
    }


    //VA A INDICAR CUANTOS ELEMENTOS QUEREMOS VER EN EL LISTADO
    override fun getItemCount(): Int = prodList.size

    fun add(prod: Product){
        prodList.add(prod)
        notifyDataSetChanged()
    }

    fun remove(prod: Product){
        prodList.remove(prod)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemProductBinding.bind(view)//llamamos a item_product.xml

        fun setListener(prod: Product){ //implementar Class Product, recibe Note respecto a la que este seleccionada
            binding.cbFinished.setOnClickListener{
                prod.isFinished = (it as CheckBox).isChecked //actualizamos nota dependiendo si esta o no check
                listener.onChecked(prod)//y lo pasamos al listener cambiamos el estado del check en pendientes
            }

            binding.root.setOnLongClickListener {
                listener.onLongClick(prod, this@ProductAdapter)
                true
            }
        }
    }

}