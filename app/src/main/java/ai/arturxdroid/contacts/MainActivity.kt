package ai.arturxdroid.contacts

import ai.arturxdroid.contacts.domain.ContactsInteractor
import ai.arturxdroid.contacts.recycler.ContactsRecyclerAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var recycler: RecyclerView? = null
    private var adapter: ContactsRecyclerAdapter? = null
    private val contactInteractor by lazy { ContactsInteractor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {
        val contactsList = contactInteractor.getContacts()
        recycler = findViewById<RecyclerView>(R.id.contacts_recycler)
        adapter = ContactsRecyclerAdapter(contactsList)
        recycler?.adapter = adapter

    }
}