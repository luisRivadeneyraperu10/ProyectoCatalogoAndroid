package com.cibertec.tiendamovil.network

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.cibertec.tiendamovil.BuscadorActivity
import com.cibertec.tiendamovil.LoginGoogleActivity
import com.cibertec.tiendamovil.MainActivity
import com.cibertec.tiendamovil.R
import com.cibertec.tiendamovil.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    // Para el login
    private lateinit var binding: ActivityInicioBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Utiliza el binding correcto para activity_inicio.xml
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()  // Verifica si el usuario está autenticado

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_one -> {
                // Iniciar MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // drawer.closeDrawer(GravityCompat.START) // Comenta esta línea
            }
            R.id.nav_item_two -> {
                // Iniciar BuscadorActivity
                val intent = Intent(this, BuscadorActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_item_three -> Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
        }
        // drawer.closeDrawer(GravityCompat.START) // Asegúrate de que esta línea esté comentada o eliminada
        return true
    }



    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Función para comprobar la sesión de usuario
    private fun comprobarSesion() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            // Si no hay usuario autenticado, redirige a LoginGoogleActivity
            startActivity(Intent(applicationContext, LoginGoogleActivity::class.java))
            finishAffinity() // Finaliza las actividades previas
        } else {
            // Si hay usuario autenticado, muestra el correo electrónico
            binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textView_email)
                .text = currentUser.email
        }
    }



}
