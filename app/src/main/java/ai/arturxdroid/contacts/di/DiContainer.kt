package ai.arturxdroid.contacts.di

import ai.arturxdroid.contacts.data.ContactsRepository
import ai.arturxdroid.contacts.presentation.ContactsViewModel
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources

object DiContainer {

    private lateinit var contentResolver: ContentResolver
    private lateinit var resources: Resources

    fun init(context: Context) {
        resources = context.resources
        contentResolver = context.contentResolver
    }

    private val contactsRepository: ContactsRepository by lazy {
        ContactsRepository(
            contentResolver,
            resources
        )
    }
    val contactsViewModel: ContactsViewModel by lazy { ContactsViewModel(contactsRepository) }
}