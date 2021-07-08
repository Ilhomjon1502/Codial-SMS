package Adapters

import Entity.Guruh
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.codialsms.databinding.ItemRvGuruhBinding

class GuruhAdapter(val context: Context?, val guruhList: List<Guruh>, val guruhItemClickInterface: GuruhItemClickInterface)
    : RecyclerView.Adapter<GuruhAdapter.Vh>(){

    inner class Vh(var itemRv: ItemRvGuruhBinding): RecyclerView.ViewHolder(itemRv.root){
        fun onBind(guruh: Guruh, position: Int){
            itemRv.txtItemRvGuruh.text = guruh.name
            itemRv.cardItemRvGuruh.setOnClickListener {
                guruhItemClickInterface.onClickGuruhItem(guruh, position)
            }
            itemRv.imageItemRvGuruhMore.setOnClickListener {
                guruhItemClickInterface.popupMenuGuruh(guruh, position, itemRv.imageItemRvGuruhMore)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvGuruhBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(guruhList[position], position)
    }

    override fun getItemCount(): Int = guruhList.size
}
interface GuruhItemClickInterface{
    fun onClickGuruhItem(guruh: Guruh, position:Int)
    fun popupMenuGuruh(guruh: Guruh, position: Int, v: ImageView)
}