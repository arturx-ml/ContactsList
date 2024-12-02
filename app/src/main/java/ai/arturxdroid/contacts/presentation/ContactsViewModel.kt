package ai.arturxdroid.contacts.presentation

import ai.arturxdroid.contacts.data.Contact
import ai.arturxdroid.contacts.data.ContactsRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {

    private val internalContactLiveData: MutableLiveData<List<Contact>> =
        MutableLiveData(listOf<Contact>())
    val contactsLiveData: LiveData<List<Contact>> = internalContactLiveData


    fun fetchContacts() {
        viewModelScope.launch {
            val contactsAsync = async { repository.getPhoneContacts() }
            val numbersAsync = async { repository.getContactNumbers() }

            val contacts = contactsAsync.await()
            val imagesAsync = async { repository.getContactsImage(contacts.map { contact -> contact.id.toLong() }) }

            val images = imagesAsync.await()
            val numbers = numbersAsync.await()

            contacts.forEach{ contact ->
                numbers[contact.id]?.let { number ->
                    contact.number = number
                }
                images[contact.id]?.let { image ->
                    contact.pictureDrawable = image
                }
            }
            internalContactLiveData.postValue(contacts)

        }
    }


}