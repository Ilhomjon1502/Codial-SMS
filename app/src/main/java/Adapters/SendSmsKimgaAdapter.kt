package Adapters

import Entity.Talaba
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.codialsms.databinding.ItemRvSendSmsKimgaBinding

class SendSmsKimgaAdapter(val context: Context?, val list:List<Talaba>)
    :RecyclerView.Adapter<SendSmsKimgaAdapter.Vh>(){

    var chackList = HashSet<Talaba>()

    inner class Vh(var itemRv:ItemRvSendSmsKimgaBinding):RecyclerView.ViewHolder(itemRv.root){
        fun onBind(talaba: Talaba, position: Int){
            itemRv.txtRvItemNameSmsKimga.text = talaba.name
            itemRv.txtItemRvNumberSmsKimga.text = talaba.phone
            itemRv.chexItemRvSendSmsKimga.isChecked = chackList.contains(talaba)
            itemRv.chexItemRvSendSmsKimga.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    chackList.add(talaba)
                }else{
                    chackList.remove(talaba)
                }
            }
        }
    }
    fun update(mChexList: HashSet<Talaba>){
        this.chackList = mChexList
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
       return Vh(ItemRvSendSmsKimgaBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}