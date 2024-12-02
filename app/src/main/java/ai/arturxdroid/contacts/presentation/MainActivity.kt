package ai.arturxdroid.contacts.presentation

import ai.arturxdroid.contacts.R
import ai.arturxdroid.contacts.di.DiContainer
import ai.arturxdroid.contacts.presentation.recycler.ContactsRecyclerAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private val contactViewModel by lazy { DiContainer.contactsViewModel }
    private var recycler: RecyclerView? = null
    private var actionButton: FloatingActionButton? = null
    private var searchEditText: EditText? = null
    private var adapter: ContactsRecyclerAdapter? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted: Boolean ->
            contactViewModel.permissionUpdate(granted)
            if (granted) {
                contactViewModel.fetchContacts()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.READ_CONTACTS
                    )
                ) showSimpleExplanationDialog()
                else {
                    showSystemSettingsDialog()
                }
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
        checkPermission()
        initUI()
    }

    private fun initUI() {
        recycler = findViewById(R.id.contacts_recycler)
        searchEditText = findViewById(R.id.search_edit_text)
        actionButton = findViewById<FloatingActionButton>(R.id.refresh_button)

        searchEditText?.doOnTextChanged { text, start, before, count ->
            val searchText = text?.toString() ?: return@doOnTextChanged
            contactViewModel.searchContactName(searchText)
        }



        contactViewModel.mainStateLiveData.observe(this) { state ->
            updateRefreshButton(state.permissionGranted)
            val contacts = state.contacts
            if (adapter == null) {
                adapter = ContactsRecyclerAdapter(contacts)
                recycler?.adapter = adapter
            } else {
                adapter?.updateList(contacts)
            }
        }
    }

    private fun updateRefreshButton(permissionGranted: Boolean) {
        val button = actionButton ?: return
        if (permissionGranted) {
            button.setImageResource(android.R.drawable.ic_popup_sync)
            button.setOnClickListener { contactViewModel.fetchContacts() }
        } else {
            button.setImageResource(android.R.drawable.ic_dialog_info)
            button.setOnClickListener { showRequestPermissionDialogue() }
        }
    }

    private fun showRequestPermissionDialogue() {
        requestPermissionLauncher.launch(
            Manifest.permission.READ_CONTACTS
        )
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            contactViewModel.permissionUpdate(true)
            contactViewModel.fetchContacts()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_CONTACTS
            )
        ) {
            Log.e("PERMIESSE", "rationale")
            showSimpleExplanationDialog()
        } else {
            showRequestPermissionDialogue()
        }
    }


    private fun showSimpleExplanationDialog() {
        AlertDialog.Builder(this).setMessage(R.string.permission_grant_msg)
            .setPositiveButton(R.string.ok, { _, _ -> showRequestPermissionDialogue() })
            .setNegativeButton(R.string.cancel, { _, _ -> }).create().show()
    }

    private fun showSystemSettingsDialog() {
        AlertDialog.Builder(this).setMessage(R.string.settings_open_msg)
            .setPositiveButton(R.string.ok, { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            })
            .setNegativeButton(R.string.cancel, { _, _ -> }).create().show()


    }


}