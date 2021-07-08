package DAO

import Entity.Kurs
import androidx.room.*

@Dao
interface KursDao {
    @Query("select * from kurs")
    fun getAllKurs():List<Kurs>

    @Insert
    fun addKurs(kurs: Kurs)

    @Delete
    fun deleteKurs(kurs: Kurs)

    @Update
    fun editKurs(kurs: Kurs)

    @Query("select * from kurs where id=:id")
    fun getKursById(id:Int):Kurs

    @Query("select * from kurs where name=:name")
    fun getKursById(name:String):Int

    @Insert
    fun addAllKurs(kurs: Kurs)
}