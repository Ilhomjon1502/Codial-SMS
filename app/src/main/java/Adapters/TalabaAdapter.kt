package Adapters

import Entity.Talaba
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.codialsms.databinding.ItemRvSinfBinding

class TalabaAdapter(val context: Context?, val talabaList: List<Talaba>, val talabatemClickInterface: TalabaItemClickInterface)
    : RecyclerView.Adapter<TalabaAdapter.Vh>(){

    inner class Vh(var itemRv: ItemRvSinfBinding): RecyclerView.ViewHolder(itemRv.root){
        fun onBind(talaba: Talaba, position: Int){
            itemRv.cardItemSinf.setOnClickListener {
                talabatemClickInterface.onClickTalabaItem(talaba, position)
            }
            itemRv.imageItemRvSinfMore.setOnClickListener {
                talabatemClickInterface.popupMenuTalaba(talaba, position, itemRv.imageItemRvSinfMore)
            }
            itemRv.txtItemRvNameSinfPurple.text = talaba.name
            itemRv.txtItemRvSinfNumberPurple.text = talaba.phone
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvSinfBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(talabaList[position], position)
    }

    override fun getItemCount(): Int = talabaList.size
}
interface TalabaItemClickInterface{
    fun onClickTalabaItem(talaba: Talaba, position:Int)
    fun popupMenuTalaba(talaba: Talaba, position: Int, v: ImageView)
}