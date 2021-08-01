package com.ilhomjon.codialsms

import Adapters.SendSmsKimgaAdapter
import Database.AppDatabase
import Entity.Guruh
import Entity.Talaba
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ilhomjon.codialsms.databinding.FragmentSendSmsKimgaBinding

class SendSmsKimgaFragment : Fragment() {

    lateinit var binding:FragmentSendSmsKimgaBinding
    lateinit var guruh: Guruh
    lateinit var talabaGuruh:ArrayList<Talaba>
    lateinit var appDatabase: AppDatabase
    lateinit var sendSmsKimgaAdapter: SendSmsKimgaAdapter
    lateinit var selectSmsmSendTalaba:HashSet<Talaba>
    lateinit var talabaNumberList:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendSmsKimgaBinding.inflate(LayoutInflater.from(context))

        guruh = arguments?.getSerializable("keyGuruh") as Guruh

        appDatabase = AppDatabase.getInstance(context)
        val listGT = ArrayList<Talaba>()
        listGT.addAll(appDatabase.talabaDao().getAllTalaba())
        talabaGuruh = ArrayList()
        for (talaba in listGT) {
            if (talaba.guruhId == guruh.id){
                talabaGuruh.add(talaba)
            }
        }

        selectSmsmSendTalaba = HashSet()

        binding.chexAllGuruh.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                selectSmsmSendTalaba.addAll(talabaGuruh)
            }else{
                selectSmsmSendTalaba.clear()
            }
            sendSmsKimgaAdapter.update(selectSmsmSendTalaba)
        }

        binding.btnSendKimga.setOnClickListener {
            println(sendSmsKimgaAdapter.chackList)
            talabaNumberList = ArrayList()
            sendSmsKimgaAdapter.chackList.forEach {
                talabaNumberList.add(it.phone!!)
            }
            if (talabaNumberList.isNotEmpty()) {
                findNavController().navigate(
                    R.id.smsXabarSendFragment,
                    bundleOf("phones" to talabaNumberList)
                )
            }else{
                Toast.makeText(context, "Birorta ham talaba tanlanmadi", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sendSmsKimgaAdapter = SendSmsKimgaAdapter(context, talabaGuruh)
        binding.rvSmsKimga.adapter = sendSmsKimgaAdapter
    }
}