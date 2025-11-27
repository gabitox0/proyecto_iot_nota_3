package com.example.proyecto_iot_nota_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_iot_nota_3.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Activity responsable del registro de nuevos usuarios en la aplicación.
 * Permite registrarse con email y contraseña.
 */
class RegisterActivity : AppCompatActivity() {

    // Variable para el View Binding (asocia el código con activity_register.xml)
    private lateinit var binding: ActivityRegisterBinding

    // Instancia de Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa el View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene la instancia de Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configura los listeners de los botones
        setupListeners()
    }

    /**
     * Configura los listeners para los botones de Registro y Volver a Login.
     */
    private fun setupListeners() {

        // 1. Botón de Registrarse
        binding.btnRegister.setOnClickListener {
            registerNewUser()
        }

        // 2. Botón de Volver a Login
        binding.btnGoToLogin.setOnClickListener {
            // Simplemente cierra esta actividad y regresa a LoginActivity (que está abajo en el stack)
            finish()
        }
    }

    /**
     * Lógica principal para validar campos y registrar al usuario en Firebase.
     */
    private fun registerNewUser() {
        val name = binding.etRegisterName.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()

        // Validación básica
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos (Nombre, Email, Contraseña).", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
            return
        }

        // Llamada a Firebase para crear el usuario
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    Log.d("RegisterActivity", "Registro exitoso: ${email}")
                    val user = auth.currentUser

                    // Opcional: Actualizar el perfil del usuario con el nombre
                    user?.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )

                    Toast.makeText(this, "¡Cuenta creada con éxito! Iniciando sesión...", Toast.LENGTH_LONG).show()

                    // Navega directamente a MainActivity
                    updateUI(user)
                } else {
                    // Falla en el registro
                    Log.w("RegisterActivity", "Fallo de registro", task.exception)
                    Toast.makeText(this, "Fallo de registro: El email ya está en uso o es inválido.",
                        Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    /**
     * Navega a la pantalla principal si el registro fue exitoso.
     * @param user El usuario actual de Firebase (o null si falló).
     */
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // El usuario está logueado, navega a la pantalla principal (MainActivity)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Cierra RegisterActivity
        }
    }
}