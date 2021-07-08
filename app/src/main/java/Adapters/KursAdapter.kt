package Adapters

import Entity.Kurs
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.codialsms.databinding.ItemRvGuruhBinding

class KursAdapter(val context: Context?, val kursList: List<Kurs>, val kursItemClickInterface: KursItemClickInterface)
    :RecyclerView.Adapter<KursAdapter.Vh>(){

    inner class Vh(var itemRv:ItemRvGuruhBinding):RecyclerView.ViewHolder(itemRv.root){
        fun onBind(kurs: Kurs, position: Int){
            itemRv.txtItemRvGuruh.text = kurs.name
            itemRv.cardItemRvGuruh.setOnClickListener {
                kursItemClickInterface.onClickKursItem(kurs, position)
            }
            itemRv.imageItemRvGuruhMore.setOnClickListener {
                kursItemClickInterface.popupMenuKurs(kurs, position, itemRv.imageItemRvGuruhMore)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvGuruhBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(kursList[position], position)
    }

    override fun getItemCount(): Int = kursList.size
}
interface KursItemClickInterface{
    fun onClickKursItem(kurs: Kurs, position:Int)
    fun popupMenuKurs(kurs: Kurs, position: Int, v:ImageView)
}