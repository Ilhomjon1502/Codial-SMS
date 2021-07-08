package com.ilhomjon.codialsms

import Adapters.TalabaAdapter
import Adapters.TalabaItemClickInterface
import Database.AppDatabase
import Entity.Guruh
import Entity.Kurs
import Entity.Talaba
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ilhomjon.codialsms.databinding.FragmentGuruhShowBinding
import com.ilhomjon.codialsms.databinding.ItemAddDialogBinding
import com.ilhomjon.codialsms.databinding.ItemAddTalabaDialogBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GuruhShowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding:FragmentGuruhShowBinding
    lateinit var talabaAdapter: TalabaAdapter
    lateinit var appDatabase: AppDatabase
    lateinit var talabaList:ArrayList<Talaba>
    lateinit var guruh: Guruh
    lateinit var kurs: Kurs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuruhShowBinding.inflate(LayoutInflater.from(context))
        guruh = arguments?.getSerializable("keyGuruh") as Guruh
        appDatabase = AppDatabase.getInstance(context)
        kurs = appDatabase.kursDao().getKursById(guruh.kursId!!)
        binding.txtGuruhName.text = guruh.name

        binding.imagePopupMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, binding.imagePopupMenu)
            popupMenu.inflate(R.menu.popup_menu)

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.add_menu ->{
                        val alerdDialog = AlertDialog.Builder(context)
                        val dialog = alerdDialog.create()
                        val itemAddDialogBinding = ItemAddTalabaDialogBinding.inflate(LayoutInflater.from(context))
                        dialog.setView(itemAddDialogBinding.root)
                        itemAddDialogBinding.edtTalabaName.hint = "Talaba ismi"
                        itemAddDialogBinding.btnDialogSave.setOnClickListener {
                            val name = itemAddDialogBinding.edtTalabaName.text.toString().trim()
                            val number = itemAddDialogBinding.edtTalabaNumber.text.toString().trim()

                            var hasK = true
                            for (g in talabaList) {
                                if (g.phone == number) {
                                    hasK = false
                                    Toast.makeText(
                                            context,
                                            "Bu tlefon raqam bilan boshqa talaba ro'yhatdan o'tgan...",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                    break
                                }
                            }
                            if (number != "" && name!="" && hasK) {
                                val talaba = Talaba()
                                talaba.name = name
                                talaba.guruhId = guruh.id
                                talaba.phone = number
                                appDatabase.talabaDao().addAllTalaba(talaba)
                                onResume()
                                Toast.makeText(context, "Saqlandi", Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            } else {
                                Toast.makeText(context, "Avval ma'lumotlarni to'ldiring!!!", Toast.LENGTH_SHORT)
                                        .show()
                            }
                        }
                        dialog.show()
                    }
                    R.id.send_sms ->{
                        findNavController().navigate(R.id.sendSmsKimgaFragment, bundleOf("keyGuruh" to guruh))
                    }
                }
                true
            }

            popupMenu.show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        val mTalabaList = ArrayList<Talaba>()
        mTalabaList.addAll(appDatabase.talabaDao().getAllTalaba())

        talabaList = ArrayList()
        for (talaba in mTalabaList) {
            if (talaba.guruhId == guruh.id){
                talabaList.add(talaba)
            }
        }
        talabaAdapter = TalabaAdapter(context, talabaList, object : TalabaItemClickInterface{
            override fun onClickTalabaItem(talaba: Talaba, position: Int) {

            }

            override fun popupMenuTalaba(talaba: Talaba, position: Int, v: ImageView) {
                val popupMenu = PopupMenu(context, v)
                popupMenu.inflate(R.menu.popup_menu_list)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit_menu -> {
                            talaba.id = appDatabase.talabaDao().getTalabaById(talaba.phone!!)
                            val alerdDialog = AlertDialog.Builder(context)
                            val dialog = alerdDialog.create()
                            val itemAddDialogBinding = ItemAddTalabaDialogBinding.inflate(LayoutInflater.from(context))
                            dialog.setView(itemAddDialogBinding.root)
                            itemAddDialogBinding.edtTalabaName.setText(talaba.name)
                            itemAddDialogBinding.edtTalabaNumber.setText(talaba.phone)
                            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                                val name = itemAddDialogBinding.edtTalabaName.text.toString().trim()
                                val number = itemAddDialogBinding.edtTalabaNumber.text.toString().trim()

                                if (name != "" && number!="") {
                                    talaba.name = name
                                    talaba.phone = number

                                    appDatabase.talabaDao().editTalaba(talaba)
                                    onResume()
                                    Toast.makeText(context, "Saqlandi", Toast.LENGTH_SHORT).show()
                                    dialog.cancel()
                                } else {
                                    Toast.makeText(context, "Avval ma'lumotlarni to'ldiring!!!", Toast.LENGTH_SHORT)
                                            .show()
                                }
                            }
                            dialog.show()
                        }
                        R.id.delete_menu -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.setTitle("${talaba.id} ${talaba.name} o'chirilsinmi?")
                            dialog.setPositiveButton(
                                    "Ha roziman"
                            ) { dialog, which ->
                                val id = appDatabase.talabaDao().getTalabaById(talaba.phone!!)
                                talaba.id = id
                                appDatabase.talabaDao().deleteTalaba(talaba)
                                Toast.makeText(context, "$id ${talaba.name} o'chirildi", Toast.LENGTH_SHORT).show()
                                onResume()
                            }
                            dialog.setNegativeButton(
                                    "Yo'q"
                            ) { dialog, which -> }
                            dialog.show()
                        }
                    }
                    true
                }
                popupMenu.show()
            }
        })
        binding.rvClass.adapter = talabaAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GuruhShowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}