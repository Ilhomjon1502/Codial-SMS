package DAO

import Entity.Guruh
import Entity.Kurs
import androidx.room.*

@Dao
interface GuruhDao {

    @Query("select * from guruh")
    fun getAllGuruh():List<Guruh>

    @Insert
    fun addGuruh(guruh: Guruh)

    @Delete
    fun deleteGuruh(guruh: Guruh)

    @Update
    fun editGuruh(guruh: Guruh)

    @Query("select * from guruh where id=:id")
    fun getGuruhById(id:Int): Guruh

    @Query("select * from guruh where name=:name")
    fun getGuruhById(name:String):Int

    @Insert
    fun addAllGuruh(guruh: Guruh)
}