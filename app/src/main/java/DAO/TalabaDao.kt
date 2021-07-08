package DAO

import Entity.Talaba
import androidx.room.*

@Dao
interface TalabaDao {
    @Query("select * from talaba")
    fun getAllTalaba():List<Talaba>

    @Insert
    fun addTalaba(talaba: Talaba)

    @Delete
    fun deleteTalaba(talaba: Talaba)

    @Update
    fun editTalaba(talaba: Talaba)

    @Query("select * from talaba where id=:id")
    fun getTalabaById(id:Int): Talaba

    @Query("select * from talaba where phone=:phone")
    fun getTalabaById(phone:String):Int

    @Insert
    fun addAllTalaba(vararg talaba: Talaba)
}