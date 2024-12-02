package ai.arturxdroid.contacts.presentation

import ai.arturxdroid.contacts.data.Contact
import ai.arturxdroid.contacts.data.ContactsRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ContactsViewModel(private val repository: ContactsRepository) : ViewModel() {

    private val internalContactLiveData: MutableLiveData<List<Contact>> =
        MutableLiveData(listOf<Contact>())
    val contactsLiveData: LiveData<List<Contact>> = internalContactLiveData

    private val loggingExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(
            "Coroutine exception",
            throwable.message,
            throwable
        )
    }


    fun fetchContacts() {
        viewModelScope.launch {
            val contactsAsync = async(loggingExceptionHandler) { repository.getPhoneContacts() }
            val numbersAsync = async(loggingExceptionHandler) { repository.getContactNumbers() }

            val contacts = contactsAsync.await()
            val imagesAsync =
                async(loggingExceptionHandler) { repository.getContactsImage(contacts.map { contact -> contact.id.toLong() }) }

            val images = imagesAsync.await()
            val numbers = numbersAsync.await()

            contacts.forEach { contact ->
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