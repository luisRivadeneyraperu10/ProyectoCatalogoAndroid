package com.cibertec.tiendamovil

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
//import LuisRivadeneyra.sdk.payments
import com.cibertec.tiendamovil.R
import com.cibertec.tiendamovil.adapter.ObjetosAdapter

import com.cibertec.tiendamovil.model.Objetos
import com.cibertec.tiendamovil.network.ApiClient
import com.cibertec.tiendamovil.network.ApiObjeto
import com.cibertec.tiendamovil.network.InicioActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {

    private lateinit var movies: List<Objetos>
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: ObjetosAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    private val config = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Usar sandbox para pruebas
        .clientId("AVn-Qoduh286YpfoJ3MLoxNaoaauarVJ5lHKpe4I-6xY-9qu4LuNvFHegCN5OuKmfRzlnrgftqxtIVJV")

    private val REQUEST_CODE_PAYMENT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciar el servicio de PayPal
        Intent(this, PayPalService::class.java).also {
            it.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
            startService(it)
        }



        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rv_movies)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Inicializa el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configura el listener para manejar la navegación
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_one -> {
                    // Acciones para el catálogo de productos
                    Toast.makeText(this, "Catálogo seleccionado", Toast.LENGTH_SHORT).show()
                    showMovies() // Muestra los objetos en el RecyclerView
                    true
                }

                R.id.nav_item_two -> {
                    // Regresar a InicioActivity
                    val intent = Intent(this, InicioActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra MainActivity si no deseas que permanezca en la pila
                    true
                }

                R.id.nav_item_three -> {
                    // Muestra el carrito de compras
                    val carrito = CarritoManager.getInstance().obtenerCarrito()
                    if (carrito.isNotEmpty()) {
                        // Muestra el contenido del carrito
                        mostrarCarrito(carrito)
                    } else {
                        Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    }
                    true
                }

                else -> false
            }
        }

        showMovies() // Muestra los objetos en el RecyclerView
    }
    override fun onDestroy() {
        stopService(Intent(this, PayPalService::class.java))
        super.onDestroy()
    }
    private fun showMovies() {
        val call = ApiClient.getClient().create(ApiObjeto::class.java)
            .getObjetos()
        call.enqueue(object : Callback<List<Objetos>> {
            override fun onResponse(call: Call<List<Objetos>>, response: Response<List<Objetos>>) {
                if (response.isSuccessful) {
                    movies = response.body() ?: emptyList()
                    movieAdapter = ObjetosAdapter(movies, this@MainActivity)
                    recyclerView.adapter = movieAdapter
                }
            }

            override fun onFailure(call: Call<List<Objetos>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Asegúrate de haber agregado Glide en tu archivo build.gradle
    private fun mostrarCarrito(carrito: MutableList<CarritoItem>) {
        // Inflar el diseño personalizado para el AlertDialog
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_carrito, null)

        // Obtener referencias a los elementos de la vista
        val textViewTotalPagar = dialogView.findViewById<TextView>(R.id.textViewTotalPagar)
        val buttonPagar = dialogView.findViewById<Button>(R.id.buttonPagar)
        val buttonCerrarCarrito = dialogView.findViewById<Button>(R.id.buttonCerrarCarrito)
        val layoutCarritoItems = dialogView.findViewById<LinearLayout>(R.id.layoutCarritoItems)

        // Función para actualizar la vista del carrito y el total a pagar
        fun actualizarCarrito() {
            layoutCarritoItems.removeAllViews() // Limpiar los elementos actuales en el layout
            var totalPagar = 0.0

            for ((index, item) in carrito.withIndex()) {
                // Crear un nuevo LinearLayout para cada ítem del carrito
                val itemLayout = LinearLayout(this)
                itemLayout.orientation = LinearLayout.HORIZONTAL
                itemLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                itemLayout.setPadding(0, 8, 0, 8)

                // ImageView para mostrar la portada del producto
                val imageViewProducto = ImageView(this)
                Glide.with(this)
                    .load(item.getObjeto().portada) // Asumiendo que portada es una URL
                    .into(imageViewProducto)
                imageViewProducto.layoutParams = LinearLayout.LayoutParams(200, 200) // Tamaño de la imagen
                imageViewProducto.scaleType = ImageView.ScaleType.CENTER_CROP // Ajustar la imagen

                // TextView para mostrar detalles del producto
                val textViewDetalles = TextView(this)
                textViewDetalles.text = "Producto: ${item.getObjeto().titulo}\n" +
                        "Cantidad: ${item.getCantidad()}\n" +
                        "Total: S/ ${item.getObjeto().precio * item.getCantidad()}"
                textViewDetalles.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)



// ImageButton para eliminar el producto
                val imageButtonEliminar = ImageButton(this)
                imageButtonEliminar.setImageResource(R.drawable.ic_eliminar) // Asigna el ícono de eliminación
                imageButtonEliminar.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageButtonEliminar.background = null // Elimina el fondo por defecto del botón

                // Configurar el botón de eliminar
                imageButtonEliminar.setOnClickListener {
                    // Eliminar el producto del carrito
                    carrito.removeAt(index)
                    // Actualizar la vista del carrito
                    actualizarCarrito()
                }

                // Añadir el ImageView, TextView y el botón al layout del ítem
                itemLayout.addView(imageViewProducto)
                itemLayout.addView(textViewDetalles)
                itemLayout.addView(imageButtonEliminar)

                // Añadir el layout del ítem al layout principal
                layoutCarritoItems.addView(itemLayout)

                // Calcular el total a pagar
                totalPagar += item.getObjeto().precio * item.getCantidad()
            }

            // Actualizar el total a pagar
            textViewTotalPagar.text = "Total a Pagar: S/ $totalPagar"
        }

        // Inicializar la vista del carrito
        actualizarCarrito()

        // Crear un AlertDialog Builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Carrito de Compras")
        builder.setView(dialogView)

        // Crear el AlertDialog
        val dialog = builder.create()

        // Configurar el botón de pagar
        buttonPagar.setOnClickListener {
            // Aquí puedes llamar al método para iniciar el pago
            iniciarPago(carrito) // Implementa este método para manejar el pago
        }

        // Configurar el botón de cerrar
        buttonCerrarCarrito.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Mostrar el AlertDialog
        dialog.show()
    }




    private fun iniciarPago(carrito: List<CarritoItem>) {
        // Calcular el total a pagar
        val totalAPagar = carrito.sumOf { item -> item.getObjeto().precio * item.getCantidad() }

        // Crear un objeto de PayPalPayment
        val payment = PayPalPayment(
            BigDecimal.valueOf(totalAPagar), // Total a pagar
            "USD", // Moneda
            "Descripción del pago1", // Descripción del pago
            PayPalPayment.PAYMENT_INTENT_SALE // Intención de pago
        )

        // Crear un intent para iniciar la actividad de pago
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config) // Configuración de PayPal
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment) // Pasar el pago

        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val confirm = data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    if (confirm != null) {
                        // Aquí puedes manejar la confirmación del pago
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        Log.i("paymentExample", "Pago confirmado: $paymentDetails")
                        // Aquí puedes mostrar un mensaje de éxito al usuario
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("paymentExample", "Pago cancelado")
                    // Aquí puedes mostrar un mensaje al usuario de que el pago fue cancelado
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i("paymentExample", "Datos inválidos")
                    // Manejar datos inválidos
                }
            }
        }
    }




}

