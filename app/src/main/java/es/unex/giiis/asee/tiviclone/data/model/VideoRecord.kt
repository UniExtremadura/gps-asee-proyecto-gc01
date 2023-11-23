package es.unex.giiis.asee.tiviclone.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class VideoRecord(
    @PrimaryKey(autoGenerate = true) var videoId: Long?,
    val uri: String = ""
) : Serializable