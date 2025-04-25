package com.example.cashleaf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashleaf.ui.MainActivity
import com.example.cashora.R

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val loginButton = findViewById<Button>(R.id.login_button)
        val signUpLink = findViewById<TextView>(R.id.sign_up_link)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validation checks
            when {
                email.isEmpty() -> {
                    emailInput.error = "Email is required"
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Enter a valid email"
                }
                password.isEmpty() -> {
                    passwordInput.error = "Password is required"
                }
                password.length < 6 -> {
                    passwordInput.error = "Password must be at least 6 characters"
                }
                else -> {
                    // Retrieve stored credentials
                    val sharedPref = getSharedPreferences("UserCredentials", MODE_PRIVATE)
                    val storedEmail = sharedPref.getString("email", "")
                    val storedPassword = sharedPref.getString("password", "")

                    if (email == storedEmail && password == storedPassword) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }
}

