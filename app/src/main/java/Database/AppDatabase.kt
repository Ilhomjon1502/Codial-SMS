package Database

import DAO.GuruhDao
import DAO.KursDao
import DAO.SmsTextDao
import DAO.TalabaDao
import Entity.Guruh
import Entity.Kurs
import Entity.SmsText
import Entity.Talaba
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Kurs::class, Guruh::class, Talaba::class, SmsText::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun kursDao():KursDao
    abstract fun gurugDao():GuruhDao
    abstract fun talabaDao():TalabaDao
    abstract fun smsTextDao():SmsTextDao

    companion object{
        private var instance:AppDatabase? = null
        @Synchronized
        fun getInstance(context: Context?):AppDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context!!, AppDatabase::class.java, "codial_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}