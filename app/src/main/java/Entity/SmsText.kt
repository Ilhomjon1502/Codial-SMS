package Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SmsText {
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    var text:String? = null
}