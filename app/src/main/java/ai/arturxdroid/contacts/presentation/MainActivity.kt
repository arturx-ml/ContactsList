package ai.arturxdroid.contacts.presentation

import ai.arturxdroid.contacts.R
import ai.arturxdroid.contacts.di.DiContainer
import ai.arturxdroid.contacts.presentation.recycler.ContactsRecyclerAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var recycler: RecyclerView? = null
    private var adapter: ContactsRecyclerAdapter? = null
    private val contactViewModel by lazy { DiContainer.contactsViewModel }

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
        requestPermission()
        recycler = findViewById(R.id.contacts_recycler)
        contactViewModel.contactsLiveData.observe(this) { contacts ->
            if(contacts.isNotEmpty()) {
                adapter = ContactsRecyclerAdapter(contacts)
                recycler?.adapter = adapter
            }
        }

    }

    private fun requestPermission() {
        checkContactPermission()
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
                if (granted) {
                    contactViewModel.fetchContacts()
                } else {
                    // request again and explain
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                contactViewModel.fetchContacts()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_CONTACTS
            ) -> {
                Toast.makeText(this, "PLEASE GRANT PERMISSIONS", Toast.LENGTH_LONG).show()
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS
                )
            }
        }
    }

    private fun checkContactPermission() {

    }
}