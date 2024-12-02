package ai.arturxdroid.contacts.data

import android.graphics.drawable.Drawable
import android.net.Uri

data class Contact(val id: String, val name: String) {
    var pictureDrawable: Drawable? = null
    var number = ""
}
