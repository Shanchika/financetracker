package com.example.cashleaf

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashora.R
import android.widget.CheckBox

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // View references
        val emailInput = findViewById<EditText>(R.id.email_input)
        val nameInput = findViewById<EditText>(R.id.name_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val termsCheckbox = findViewById<CheckBox>(R.id.terms_checkbox)
        val registerButton = findViewById<Button>(R.id.login_button)
        val signInLink = findViewById<TextView>(R.id.sign_up_link)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val username = nameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validation
            when {
                username.isEmpty() -> {
                    nameInput.error = "Username is required"
                    nameInput.requestFocus()
                }
                username.any { it.isDigit() } -> {
                    nameInput.error = "Username cannot contain numbers"
                    nameInput.requestFocus()
                }
                email.isEmpty() -> {
                    emailInput.error = "Email is required"
                    emailInput.requestFocus()
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Enter a valid email"
                    emailInput.requestFocus()
                }

                password.isEmpty() -> {
                    passwordInput.error = "Password is required"
                    passwordInput.requestFocus()
                }

                password.length < 6 -> {
                    passwordInput.error = "Password must be at least 6 characters"
                    passwordInput.requestFocus()
                }

                !termsCheckbox.isChecked -> {
                    Toast.makeText(this, "Please agree to Terms and Privacy Policy", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    // Save credentials (not secure, just for demo/testing)
                    val sharedPref = getSharedPreferences("UserCredentials", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("email", email)
                        putString("username", username)
                        putString("password", password)
                        apply()
                    }

                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignIn::class.java))
                    finish()
                }
            }
        }

        signInLink.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}