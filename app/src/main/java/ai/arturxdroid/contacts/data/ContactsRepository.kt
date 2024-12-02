package ai.arturxdroid.contacts.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.res.Resources
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Contacts
import android.provider.ContactsContract
import androidx.core.graphics.drawable.toDrawable
import java.io.ByteArrayInputStream


class ContactsRepository(val contentResolver: ContentResolver, val resources:Resources) {

    suspend fun getContactsImage(list: List<Long>): Map<String, Drawable> {
        val images = mutableMapOf<String, Drawable>()
        list.forEach { contactId ->
            val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
            val photoUri =
                Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)

            val cursor = contentResolver.query(
                photoUri,
                arrayOf<String>(ContactsContract.Contacts.Photo.PHOTO),
                null,
                null,
                null,
                null
            )
            if (cursor == null) {
                return emptyMap<String, Drawable>()
            } else {
                if (cursor.moveToFirst()) {
                    val data = cursor.getBlob(0)
                    if (data != null && data.isNotEmpty()) {
                        val stream = ByteArrayInputStream(data)
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val drawable = bitmap.toDrawable(resources)
                        images[contactId.toString()] = drawable
                    }
                }
                cursor.close()
            }

        }
        return images
    }

    suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val contactsCursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

            val contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, idIndex.toLong())
            val photoUri = Uri.withAppendedPath(contactUri, Contacts.Photos.CONTENT_DIRECTORY)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                if (name != null) {
                    contactsList.add(Contact(id, name))
                }
            }
            contactsCursor.close()
        }
        return contactsList
    }

    suspend fun getContactNumbers(): HashMap<String, String> {
        val contactsNumberMap = HashMap<String, String>()
        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val numberIndex =
                phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                //check if the map contains key or not, if not then create a new array list with number
                if (!contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId] = number
                }
            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

}