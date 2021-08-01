package com.ilhomjon.codialsms

import Adapters.GuruhAdapter
import Adapters.GuruhItemClickInterface
import Database.AppDatabase
import Entity.Guruh
import Entity.Kurs
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
import com.ilhomjon.codialsms.databinding.FragmentSinfListBinding
import com.ilhomjon.codialsms.databinding.ItemAddDialogBinding

class SinfListFragment : Fragment() {

    lateinit var binding:FragmentSinfListBinding
    lateinit var kurs: Kurs
    lateinit var guruhAdapter: GuruhAdapter
    lateinit var appDatabase: AppDatabase
    lateinit var guruhList: ArrayList<Guruh>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSinfListBinding.inflate(LayoutInflater.from(context))

        kurs = arguments?.getSerializable("kurs") as Kurs
        binding.courseTitle.text = kurs.name

        binding.addNewClassroom.setOnClickListener {
            val alerdDialog = AlertDialog.Builder(context)
            val dialog = alerdDialog.create()
            val itemAddDialogBinding = ItemAddDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setView(itemAddDialogBinding.root)
            itemAddDialogBinding.edtKursName.hint = "Guruh nomi"
            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                val name = itemAddDialogBinding.edtKursName.text.toString().trim()

                var hasK = true
                for (g in guruhList) {
                    if (g.name == name) {
                        hasK = false
                        Toast.makeText(
                                context,
                                "Bunday guruh nomi mavjud boshqa nom bilan saqlang...",
                                Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
                if (name != "" && hasK) {
                    val guruh = Guruh()
                    guruh.name = name
                    guruh.kursId = kurs.id
                    appDatabase.gurugDao().addGuruh(guruh)
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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        appDatabase = AppDatabase.getInstance(context)
        guruhList = ArrayList()
        var mGuruhList = ArrayList<Guruh>()
        mGuruhList.addAll(appDatabase.gurugDao().getAllGuruh())
        for (guruh in mGuruhList) {
            if (guruh.kursId == kurs.id){
                guruhList.add(guruh)
            }
        }

        guruhAdapter = GuruhAdapter(context, guruhList, object :GuruhItemClickInterface{
            override fun onClickGuruhItem(guruh: Guruh, position: Int) {
                findNavController().navigate(R.id.guruhShowFragment, bundleOf("keyGuruh" to guruh))
            }
            override fun popupMenuGuruh(guruh: Guruh, position: Int, v: ImageView) {
                val popupMenu = PopupMenu(context, v)
                popupMenu.inflate(R.menu.popup_menu_list)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit_menu -> {
                            guruh.id = appDatabase.gurugDao().getGuruhById(guruh.name!!)
                            val alerdDialog = AlertDialog.Builder(context)
                            val dialog = alerdDialog.create()
                            val itemAddDialogBinding = ItemAddDialogBinding.inflate(LayoutInflater.from(context))
                            dialog.setView(itemAddDialogBinding.root)
                            itemAddDialogBinding.edtKursName.setText(guruh.name)
                            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                                val name = itemAddDialogBinding.edtKursName.text.toString().trim()


                                if (name != "") {
                                    guruh.name = name
                                    appDatabase.gurugDao().editGuruh(guruh)
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
                            dialog.setTitle("${guruh.id} ${guruh.name} o'chirilsinmi?" +
                                    "\nAgar bu guruhni o'chirilsa undagi barcha o'quvchilar o'chib ketadi!!!")
                            dialog.setPositiveButton(
                                    "Ha roziman"
                            ) { dialog, which ->
                                val id = appDatabase.gurugDao().getGuruhById(guruh.name!!)
                                guruh.id = id
                                appDatabase.gurugDao().deleteGuruh(guruh)
                                appDatabase.talabaDao().getAllTalaba().forEach {
                                    it.id = appDatabase.talabaDao().getTalabaById(it.phone!!)
                                    if (it.guruhId == guruh.id){
                                        appDatabase.talabaDao().deleteTalaba(it)
                                    }
                                }
                                Toast.makeText(context, "$id ${guruh.name} o'chirildi", Toast.LENGTH_SHORT).show()
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
        binding.rvGuruh.adapter = guruhAdapter
    }

}