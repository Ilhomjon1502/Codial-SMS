package com.ilhomjon.codialsms

import Adapters.SmsTextItemClickInterface
import Adapters.SmsXabariAdapter
import Database.AppDatabase
import Entity.SmsText
import android.Manifest
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.ilhomjon.codialsms.databinding.FragmentSmsXabarSendBinding
import com.ilhomjon.codialsms.databinding.ItemAddDialogBinding
import java.util.concurrent.TimeUnit

class SmsXabarSendFragment : Fragment() {

    lateinit var binding: FragmentSmsXabarSendBinding
    lateinit var talabaNumber: ArrayList<String>
    lateinit var smsXabari: ArrayList<String>
    lateinit var appDatabase: AppDatabase
    lateinit var smsXabariAdapter: SmsXabariAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentSmsXabarSendBinding.inflate(LayoutInflater.from(context))

        talabaNumber = ArrayList()
        talabaNumber = arguments?.getStringArrayList("phones")!!
        appDatabase = AppDatabase.getInstance(context)

        binding.imageAddSms.setOnClickListener {
            val alerdDialog = AlertDialog.Builder(context)
            val dialog = alerdDialog.create()
            val itemAddDialogBinding = ItemAddDialogBinding.inflate(LayoutInflater.from(context))
            dialog.setView(itemAddDialogBinding.root)
            itemAddDialogBinding.edtKursName.hint = "Sms matni"
            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                val name = itemAddDialogBinding.edtKursName.text.toString().trim()

                if (name != "") {
                    val smsText = SmsText()
                    smsText.text = name
                    appDatabase.smsTextDao().addSmsText(smsText)
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

        binding.btnSendSms.setOnClickListener {
            smsXabari = ArrayList()
            smsXabariAdapter.smsTextSet.forEach {
                smsXabari.add(it)
            }

            askPermission(Manifest.permission.SEND_SMS) {
                //all permissions already granted or just granted
                SendSmsAsyncTask().execute(smsXabari)
            }.onDeclined { e ->
                if (e.hasDenied()) {

                    AlertDialog.Builder(context)
                            .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                            .setPositiveButton("yes") { dialog, which ->
                                e.askAgain();
                            } //ask again
                            .setNegativeButton("no") { dialog, which ->
                                dialog.dismiss();
                            }
                            .show();
                }

                if (e.hasForeverDenied()) {
                    //the list of forever denied permissions, user has check 'never ask again'

                    // you need to open setting manually if you really need it
                    e.goToSettings();
                }
            }

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        appDatabase = AppDatabase.getInstance(context)
        var listSms = ArrayList<SmsText>()
        listSms.addAll(appDatabase.smsTextDao().getAllSmsText())

        smsXabariAdapter = SmsXabariAdapter(context, listSms, object : SmsTextItemClickInterface {
            override fun popupMenuSmsText(smsText: SmsText, position: Int, v: ImageView) {
                val popupMenu = PopupMenu(context, v)
                popupMenu.inflate(R.menu.popup_menu_list)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit_menu -> {
                            smsText.id = appDatabase.smsTextDao().getSmsTextById(smsText.text!!)
                            val alerdDialog = AlertDialog.Builder(context)
                            val dialog = alerdDialog.create()
                            val itemAddDialogBinding = ItemAddDialogBinding.inflate(LayoutInflater.from(context))
                            dialog.setView(itemAddDialogBinding.root)
                            itemAddDialogBinding.edtKursName.setText(smsText.text)
                            itemAddDialogBinding.btnDialogSave.setOnClickListener {
                                val name = itemAddDialogBinding.edtKursName.text.toString().trim()


                                if (name != "") {
                                    smsText.text = name
                                    appDatabase.smsTextDao().editSmsText(smsText)
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
                            dialog.setTitle("${smsText.id} ${smsText.text} o'chirilsinmi?")
                            dialog.setPositiveButton(
                                    "Ha roziman"
                            ) { dialog, which ->
                                val id = appDatabase.smsTextDao().getSmsTextById(smsText.text!!)
                                smsText.id = id
                                appDatabase.smsTextDao().deleteSmsText(smsText)
                                Toast.makeText(context, "$id ${smsText.text} o'chirildi", Toast.LENGTH_SHORT).show()
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
        binding.rvSmsXabari.adapter = smsXabariAdapter
    }

    inner class SendSmsAsyncTask : AsyncTask<ArrayList<String>, Void, Void>() {

        var listError = ArrayList<String>()

        override fun onPreExecute() {
            super.onPreExecute()
            binding.progressSend.visibility = View.VISIBLE
            binding.linerRoot.alpha = 0.5f
        }

        override fun doInBackground(vararg params: ArrayList<String>?): Void? {

            for (s in talabaNumber) {
                try {
                    params[0]?.forEach {
//                        var obj = SmsManager.getDefault()
//                        obj.sendTextMessage(s, null, it, null, null)

                        val sms = SmsManager.getDefault()
                        val parts = sms.divideMessage(it)
                        sms.sendMultipartTextMessage(s, null, parts, null, null)


                        TimeUnit.SECONDS.sleep(1)
                    }
                } catch (e: Exception) {
                    listError.add(s)
                }
            }

            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            binding.linerRoot.alpha = 1.0f
            binding.progressSend.visibility = View.GONE
            if (listError.isNotEmpty()) {
                Toast.makeText(context, "$listError larda xatolik", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Muvaffaqiyatli yuborildi", Toast.LENGTH_SHORT).show()
            }
        }
    }
}