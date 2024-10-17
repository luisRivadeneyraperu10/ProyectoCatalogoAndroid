package com.cibertec.tiendamovil

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
//import LuisRivadeneyra.sdk.payments
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.cibertec.tiendamovil.adapter.ObjetosAdapter
import com.cibertec.tiendamovil.model.Objetos
import com.cibertec.tiendamovil.network.ApiClient
import com.cibertec.tiendamovil.network.ApiObjeto
import com.cibertec.tiendamovil.network.InicioActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class BuscadorActivity : AppCompatActivity() {

    private lateinit var edtCodigo: EditText
    private lateinit var btnBuscar: Button
    private lateinit var imgPortada: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvPrecio: TextView
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


        setContentView(R.layout.activity_buscador)



        // Inicializa los elementos de la vista
        edtCodigo = findViewById(R.id.edtCodigo)
        btnBuscar = findViewById(R.id.btnBuscar)
        imgPortada = findViewById(R.id.imgPortada)
        tvNombre = findViewById(R.id.tvNombre)
        tvGenero = findViewById(R.id.tvGenero)
        tvPrecio = findViewById(R.id.tvPrecio)

        // Inicializa el BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Configura el listener de navegación
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
                    finish() // Cierra BuscadorActivity si no deseas que permanezca en la pila
                    true
                }
                R.id.nav_item_three -> {
                    // Muestra el carrito de compras
                    val carrito = CarritoManager.getInstance().obtenerCarrito()
                    if (carrito.isNotEmpty()) {
                        mostrarCarrito(carrito)
                    } else {
                        Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        // Configura el listener para el botón de búsqueda
        btnBuscar.setOnClickListener {
            buscarProducto()
        }
    }

    private fun buscarProducto() {
        val codigo = edtCodigo.text.toString().trim()

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un código", Toast.LENGTH_SHORT).show()
            return
        }

        val apiObjeto = ApiClient.getClient().create(ApiObjeto::class.java)
        val call = apiObjeto.getObjetos()

        call.enqueue(object : Callback<List<Objetos>> {
            override fun onResponse(call: Call<List<Objetos>>, response: Response<List<Objetos>>) {
                if (response.isSuccessful && response.body() != null) {
                    val objetos = response.body()!!
                    val objetoEncontrado = objetos.find { it.codigo == codigo }

                    if (objetoEncontrado != null) {
                        tvNombre.text = SpannableString("Título: ${objetoEncontrado.titulo}").apply {
                            setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                        }
                        tvGenero.text = SpannableString("Género: ${objetoEncontrado.genero}").apply {
                            setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                        }
                        tvPrecio.text = SpannableString("Precio: S/ ${objetoEncontrado.precio}").apply {
                            setSpan(StyleSpan(Typeface.BOLD), 0, 7, 0)
                        }

                        val imageUrl = "http://192.168.56.1:8383/movies/cover/${objetoEncontrado.portada}"
                        Glide.with(this@BuscadorActivity)
                            .load(imageUrl)
                            .into(imgPortada)
                    } else {
                        Toast.makeText(this@BuscadorActivity, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BuscadorActivity, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Objetos>>, t: Throwable) {
                Toast.makeText(this@BuscadorActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showMovies() {
        val call = ApiClient.getClient().create(ApiObjeto::class.java).getObjetos()
        call.enqueue(object : Callback<List<Objetos>> {
            override fun onResponse(call: Call<List<Objetos>>, response: Response<List<Objetos>>) {
                if (response.isSuccessful) {
                    val movies = response.body() ?: emptyList()
                    val movieAdapter = ObjetosAdapter(movies, this@BuscadorActivity)
                    findViewById<RecyclerView>(R.id.rv_movies).adapter = movieAdapter
                }
            }

            override fun onFailure(call: Call<List<Objetos>>, t: Throwable) {
                Toast.makeText(this@BuscadorActivity, "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarCarrito(carrito: MutableList<CarritoItem>) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_carrito, null)

        val textViewTotalPagar = dialogView.findViewById<TextView>(R.id.textViewTotalPagar)
        val buttonPagar = dialogView.findViewById<Button>(R.id.buttonPagar)
        val buttonCerrarCarrito = dialogView.findViewById<Button>(R.id.buttonCerrarCarrito)
        val layoutCarritoItems = dialogView.findViewById<LinearLayout>(R.id.layoutCarritoItems)

        fun actualizarCarrito() {
            layoutCarritoItems.removeAllViews()
            var totalPagar = 0.0

            for ((index, item) in carrito.withIndex()) {
                val itemLayout = LinearLayout(this)
                itemLayout.orientation = LinearLayout.HORIZONTAL
                itemLayout.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                itemLayout.setPadding(0, 8, 0, 8)

                val imageViewProducto = ImageView(this)
                Glide.with(this)
                    .load(item.getObjeto().portada)
                    .into(imageViewProducto)
                imageViewProducto.layoutParams = LinearLayout.LayoutParams(200, 200)
                imageViewProducto.scaleType = ImageView.ScaleType.CENTER_CROP

                val textViewDetalles = TextView(this)
                textViewDetalles.text = "Producto: ${item.getObjeto().titulo}\n" +
                        "Cantidad: ${item.getCantidad()}\n" +
                        "Total: S/ ${item.getObjeto().precio * item.getCantidad()}"
                textViewDetalles.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                val imageButtonEliminar = ImageButton(this)
                imageButtonEliminar.setImageResource(R.drawable.ic_eliminar)
                imageButtonEliminar.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageButtonEliminar.background = null
                imageButtonEliminar.setOnClickListener {
                    carrito.removeAt(index)
                    actualizarCarrito()
                }

                itemLayout.addView(imageViewProducto)
                itemLayout.addView(textViewDetalles)
                itemLayout.addView(imageButtonEliminar)
                layoutCarritoItems.addView(itemLayout)

                totalPagar += item.getObjeto().precio * item.getCantidad()
            }

            textViewTotalPagar.text = "Total a Pagar: S/ $totalPagar"
        }

        actualizarCarrito()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Carrito de Compras")
        builder.setView(dialogView)

        val dialog = builder.create()

        buttonPagar.setOnClickListener {
            iniciarPago(carrito)
        }

        buttonCerrarCarrito.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun iniciarPago(carrito: List<CarritoItem>) {
        val totalAPagar = carrito.sumOf { item -> item.getObjeto().precio * item.getCantidad() }

        val payment = PayPalPayment(
            BigDecimal.valueOf(totalAPagar),
            "USD",
            "Descripción del pago",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    val confirm =
                        data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                    confirm?.let {
                        val paymentDetails = it.toJSONObject().toString(4)
                        Log.i("paymentExample", "Pago confirmado: $paymentDetails")
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
    }}
