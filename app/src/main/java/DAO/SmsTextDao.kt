package DAO

import Entity.SmsText
import androidx.room.*

@Dao
interface SmsTextDao {
    @Query("select * from smsText")
    fun getAllSmsText():List<SmsText>

    @Insert
    fun addSmsText(smsText: SmsText)

    @Delete
    fun deleteSmsText(smsText: SmsText)

    @Update
    fun editSmsText(smsText: SmsText)

    @Query("select * from smsText where id=:id")
    fun getSmsTextById(id:Int): SmsText

    @Query("select * from smsText where text=:text")
    fun getSmsTextById(text:String):Int

    @Insert
    fun addAllSmsText(vararg smsText: SmsText)
}