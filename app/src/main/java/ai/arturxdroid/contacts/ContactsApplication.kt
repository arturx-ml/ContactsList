package ai.arturxdroid.contacts

import ai.arturxdroid.contacts.di.DiContainer
import android.app.Application

class ContactsApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        DiContainer.init(this)
    }

}