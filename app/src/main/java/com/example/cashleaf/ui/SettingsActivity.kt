package com.example.cashleaf.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cashleaf.data.PreferencesManager
import com.example.cashleaf.data.PdfGenerator
import com.example.cashleaf.data.TransactionRepository
import com.example.cashleaf.notification.NotificationManager
import com.example.cashora.R
import com.example.cashora.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var notificationManager: NotificationManager

    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)
        transactionRepository = TransactionRepository(this)
        notificationManager = NotificationManager(this)

        setupBottomNavigation()
        setupCurrencySpinner()
        setupNotificationSettings()
        setupBackupButtons()
        setupDarkModeSwitch()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_settings
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun setupCurrencySpinner() {
        val currencies = arrayOf("USD", "EUR", "GBP", "JPY", "LKR", "AUD", "INR", "CNY")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = adapter

        val currentCurrency = preferencesManager.getCurrency()
        val position = currencies.indexOf(currentCurrency)
        if (position >= 0) {
            binding.spinnerCurrency.setSelection(position)
        }

        binding.spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencies[position]
                if (selectedCurrency != currentCurrency) {
                    preferencesManager.setCurrency(selectedCurrency)
                    Toast.makeText(
                        this@SettingsActivity,
                        getString(R.string.currency_updated),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupNotificationSettings() {
        binding.switchBudgetAlerts.isChecked = preferencesManager.isNotificationEnabled()
        binding.switchBudgetAlerts.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setNotificationEnabled(isChecked)
        }
    }

    private fun setupBackupButtons() {
        binding.btnBackupData.setOnClickListener {
            checkPermissionAndGeneratePdf("backup")
        }

        binding.btnExportData.setOnClickListener {
            checkPermissionAndGeneratePdf("export")
        }
    }

    private fun checkPermissionAndGeneratePdf(type: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            // No need for permission on Android 10 and above for app-specific storage
            generatePdfReport(type)
        } else {
            // Below Android 10, check and request permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                generatePdfReport(type)
            }
        }
    }


    private fun generatePdfReport(type: String) {
        val transactions = transactionRepository.getAllTransactions()
        val pdfGenerator = PdfGenerator(this)
        val fileName = if (type == "backup") "TransactionBackup" else "TransactionExport"
        val filePath = pdfGenerator.generatePdf(transactions, fileName)

        val message = if (type == "backup") getString(R.string.backup_success)
        else getString(R.string.export_success)

        Toast.makeText(this, "$message\nSaved to: $filePath", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Default to "backup" if no context — or refactor to remember action if needed
            generatePdfReport("backup")
        } else {
            Toast.makeText(this, "Storage permission denied. Cannot generate PDF.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDarkModeSwitch() {
        // Check if dark mode is enabled from preferences
        val isDarkModeEnabled = preferencesManager.isDarkModeEnabled()

        // Set the switch state to match the stored preference
        binding.switchDarkMode.isChecked = isDarkModeEnabled

        // Set the AppCompatDelegate's night mode based on the current preference
        val mode = if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO

        AppCompatDelegate.setDefaultNightMode(mode)

        // Set listener to update preferences when user toggles the switch
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO

            AppCompatDelegate.setDefaultNightMode(newMode)
            preferencesManager.setDarkModeEnabled(isChecked) // Save the preference
            recreate() // Recreate the activity to apply the theme change
        }
    }
}
