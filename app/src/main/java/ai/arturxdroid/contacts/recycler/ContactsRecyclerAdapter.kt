package ai.arturxdroid.contacts.recycler

import ai.arturxdroid.contacts.R
import ai.arturxdroid.contacts.domain.ContactData
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

class ContactsRecyclerAdapter(val contacts: List<ContactData>) :
    RecyclerView.Adapter<ContactsRecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsRecyclerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.contacts_recycler_item, null, false)
        return ContactsRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactsRecyclerViewHolder, position: Int) {
        // noop

    }
}


class ContactsRecyclerViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val contactNameTextView =
        itemView.findViewById<MaterialTextView>(R.id.contact_name_text_view)
    private val contactNumberTextView =
        itemView.findViewById<MaterialTextView>(R.id.contact_number_text_view)
    private val contactImageView = itemView.findViewById<ImageView>(R.id.contact_picture_image_view)

    fun setContactName(name: String) {
        contactNameTextView.text = name
    }

    fun setContactNumber(number: String) {
        contactNumberTextView.text = number
    }

    fun setContactImage(imageUri: Uri) {
        contactImageView.setImageURI(imageUri)
    }
}