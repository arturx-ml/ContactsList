package ai.arturxdroid.contacts.presentation

import ai.arturxdroid.contacts.R
import ai.arturxdroid.contacts.di.DiContainer
import ai.arturxdroid.contacts.presentation.recycler.ContactsRecyclerAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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


    private val contactViewModel by lazy { DiContainer.contactsViewModel }
    private var recycler: RecyclerView? = null
    private var adapter: ContactsRecyclerAdapter? = null

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val requestPermissionDelayMs = 3500L
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
            if (granted) {
                contactViewModel.fetchContacts()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.permission_grant_msg),
                    Toast.LENGTH_LONG
                ).show()
                mainThreadHandler.postDelayed({
                    showRequestPermissionDialogue()
                }, requestPermissionDelayMs)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestPermission()
        initUI()
    }

    private fun initUI() {
        recycler = findViewById(R.id.contacts_recycler)
        contactViewModel.contactsLiveData.observe(this) { contacts ->
            if (contacts.isNotEmpty()) {
                if (adapter == null) {
                    adapter = ContactsRecyclerAdapter(contacts)
                    recycler?.adapter = adapter
                } else {
                    adapter?.updateList(contacts)
                }

            }
        }
    }

    private fun showRequestPermissionDialogue() {
        requestPermissionLauncher.launch(
            Manifest.permission.READ_CONTACTS
        )
    }

    private fun requestPermission() {

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
                Toast.makeText(this, getString(R.string.permission_grant_msg), Toast.LENGTH_LONG)
                    .show()
            }

            else -> {
                showRequestPermissionDialogue()
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.

            }
        }
    }

}