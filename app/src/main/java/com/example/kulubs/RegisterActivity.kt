package com.example.kulubs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val emailInput = findViewById<EditText>(R.id.editTextEmail)
        val passwordInput = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordInput = findViewById<EditText>(R.id.editTextConfirmPassword)
        val registerButton = findViewById<Button>(R.id.buttonRegister)
        val loginText = findViewById<TextView>(R.id.textViewLogin)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            Log.d("RegisterActivity", "Register button clicked")

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Log.d("RegisterActivity", "Empty fields")
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Log.d("RegisterActivity", "Password mismatch")
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("RegisterActivity", "Registration successful")
                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        loginText.setOnClickListener {
            Log.d("RegisterActivity", "Login text clicked")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
