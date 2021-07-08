package Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Talaba : Serializable{
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    var name:String? = null
    var phone:String? = null
    var guruhId:Int? = null
}