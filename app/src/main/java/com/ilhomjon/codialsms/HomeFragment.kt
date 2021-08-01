package com.ilhomjon.codialsms

import Adapters.KursAdapter
import Adapters.KursItemClickInterface
import Database.AppDatabase
import Entity.Kurs
import android.app.AlertDialog
import android.content.DialogInterface
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
import com.ilhomjon.codialsms.databinding.FragmentHomeBinding
import com.ilhomjon.codialsms.databinding.ItemAddDialogBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
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

    lateinit var binding:FragmentHomeBinding
    lateinit var appDatabase: AppDatabase
    lateinit var kursAdapter: KursAdapter
    lateinit var kursList:ArrayList<Kurs>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(context))

        binding.btnAddClass.setOnClickListener {
            val alerdDialog = AlertDialog.Builder(context)
            val dialog = alerdDialog.create()
            val itemAddDialogBinding = ItemAddDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setView(itemAddDialogBinding.root)
            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                val name = itemAddDialogBinding.edtKursName.text.toString().trim()

                var hasK = true
                for (kurs in kursList) {
                    if (kurs.name == name) {
                        hasK = false
                        Toast.makeText(
                            context,
                            "Bunday kurs nomi mavjud boshqa nom bilan saqlang...",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }
                if (name != "" && hasK) {
                    val kurs = Kurs()
                    kurs.name = name
                    appDatabase.kursDao().addKurs(kurs)
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
        kursList = ArrayList()
        kursList.addAll(appDatabase.kursDao().getAllKurs())
        kursAdapter = KursAdapter(context, kursList, object : KursItemClickInterface {
            override fun onClickKursItem(kurs: Kurs, position: Int) {
                kurs.id = appDatabase.kursDao().getKursById(kurs.name!!)
                findNavController().navigate(R.id.sinfListFragment, bundleOf("kurs" to kurs))
            }

            override fun popupMenuKurs(kurs: Kurs, position: Int, v: ImageView) {
                val popupMenu = PopupMenu(context, v)
                popupMenu.inflate(R.menu.popup_menu_list)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit_menu -> {
                            kurs.id = appDatabase.kursDao().getKursById(kurs.name!!)
                            val alerdDialog = AlertDialog.Builder(context)
                            val dialog = alerdDialog.create()
                            val itemAddDialogBinding =
                                ItemAddDialogBinding.inflate(LayoutInflater.from(context))
                            dialog.setView(itemAddDialogBinding.root)
                            itemAddDialogBinding.edtKursName.setText(kurs.name)
                            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                                val name = itemAddDialogBinding.edtKursName.text.toString().trim()

                                var hasK = true
                                for (k in kursList) {
                                    if (k.name == name) {
                                        hasK = false
                                        Toast.makeText(
                                            context,
                                            "Bunday kurs nomi mavjud boshqa nom bilan saqlang...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        break
                                    }
                                }
                                if (name != "" && hasK) {
                                    kurs.name = name
                                    appDatabase.kursDao().editKurs(kurs)
                                    onResume()
                                    Toast.makeText(context, "Saqlandi", Toast.LENGTH_SHORT).show()
                                    dialog.cancel()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Avval ma'lumotlarni to'ldiring!!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                            dialog.show()
                        }
                        R.id.delete_menu -> {
                            val dialog = AlertDialog.Builder(context)
                            dialog.setTitle(
                                "${kurs.id} ${kurs.name} o'chirilsinmi?" +
                                        "\nAgar bu kurs o'chirilsa undagi barcha guruhlar, o'quvchilar o'chib ketadi!!!"
                            )
                            dialog.setPositiveButton(
                                "Ha roziman"
                            ) { dialog, which ->
                                val id = appDatabase.kursDao().getKursById(kurs.name!!)
                                kurs.id = id
                                appDatabase.kursDao().deleteKurs(kurs)
                                appDatabase.gurugDao().getAllGuruh().forEach {
                                    if (it.kursId == kurs.id) {
                                        it.id = appDatabase.gurugDao().getGuruhById(it.name!!)
                                        appDatabase.gurugDao().deleteGuruh(it)
                                        for (t in appDatabase.talabaDao().getAllTalaba()) {
                                            if (t.guruhId == it.id){
                                                t.id = appDatabase.talabaDao().getTalabaById(t.phone!!)
                                                appDatabase.talabaDao().deleteTalaba(t)
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(
                                    context,
                                    "$id ${kurs.name} o'chirildi",
                                    Toast.LENGTH_SHORT
                                ).show()
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
        binding.rvKurs.adapter = kursAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}