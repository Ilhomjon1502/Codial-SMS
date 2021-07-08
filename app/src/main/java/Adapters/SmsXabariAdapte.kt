package Adapters

import Entity.SmsText
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ilhomjon.codialsms.databinding.ItemSmsXabariBinding

class SmsXabariAdapter(val context: Context?, val smsTextList: List<SmsText>, val smsTexttemClickInterface: SmsTextItemClickInterface)
    : RecyclerView.Adapter<SmsXabariAdapter.Vh>(){

    var smsTextSet = HashSet<String>()

    inner class Vh(var itemRv: ItemSmsXabariBinding): RecyclerView.ViewHolder(itemRv.root){
        fun onBind(smsText: SmsText, position: Int){

            itemRv.imageMoreSmsXabari.setOnClickListener {
                smsTexttemClickInterface.popupMenuSmsText(smsText, position, itemRv.imageMoreSmsXabari)
            }
            itemRv.txtItemRvSmsXabariMatni.text = smsText.text
            itemRv.chexSmsXabari.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    smsTextSet.add(smsText.text!!)
                }else{
                    smsTextSet.remove(smsText.text)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemSmsXabariBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(smsTextList[position], position)
    }

    override fun getItemCount(): Int = smsTextList.size
}
interface SmsTextItemClickInterface{
    fun popupMenuSmsText(smsText: SmsText, position: Int, v: ImageView)
}