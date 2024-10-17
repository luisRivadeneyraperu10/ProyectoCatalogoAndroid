package com.cibertec.tiendamovil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import LuisRivadeneyra.sdk.payments
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.cibertec.tiendamovil.CarritoManager;
import com.cibertec.tiendamovil.R;
import com.cibertec.tiendamovil.model.Objetos;

public class ObjetosAdapter extends RecyclerView.Adapter<ObjetosAdapter.ViewHolder> {
    private List<Objetos> movies;
    private Context context;
    private List<Objetos> carrito; // Lista para el carrito

    public ObjetosAdapter(List<Objetos> movies, Context context) {
        this.movies = movies;
        this.context = context;
        this.carrito = new ArrayList<>(); // Inicializar el carrito
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Obtener el objeto actual
        Objetos objetoActual = movies.get(position);

        holder.tv_titulo.setText(objetoActual.getTitulo());
        Glide.with(context).load(objetoActual.getPortada()).into(holder.iv_portada);
        holder.tv_precio.setText(String.format("S/ %.2f", objetoActual.getPrecio())); // Muestra el precio con 2 decimales

        // Configurar el botón "Agregar"
        holder.btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDetalles(objetoActual); // Mostrar detalles al hacer clic
            }
        });
    }

    private void agregarAlCarrito(Objetos objeto, int cantidad) {
        // Lógica para agregar el objeto al carrito con la cantidad indicada.
        // Aquí puedes usar una lista global para el carrito o un singleton para manejar los objetos en el carrito.

        CarritoManager.getInstance().agregarProducto(objeto, cantidad);
    }


    private void mostrarDetalles(Objetos objeto) {
        // Crear un AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Detalles de la película");

        // Inflar el diseño personalizado para el AlertDialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_detalles_objeto, null);
        builder.setView(dialogView);

        // Obtener referencias a los elementos del diseño
        ImageView imageViewPortada = dialogView.findViewById(R.id.imageViewPortada);
        TextView textViewDetalles = dialogView.findViewById(R.id.textViewDetalles);
        EditText editTextCantidad = dialogView.findViewById(R.id.editTextCantidad);
        Button buttonAgregarCarrito = dialogView.findViewById(R.id.buttonAgregarCarrito); // Botón para agregar al carrito
        Button buttonCerrar = dialogView.findViewById(R.id.buttonCerrar); // Botón para cerrar
        ImageButton buttonMenos = dialogView.findViewById(R.id.buttonMenos); // Botón para disminuir cantidad
        ImageButton buttonMas = dialogView.findViewById(R.id.buttonMas); // Botón para aumentar cantidad



        // Establecer valor por defecto
        editTextCantidad.setText("1");

        // Configurar el contenido del TextView
        String detalles = "Título: " + objeto.getTitulo() + "\n" +
                "Fecha: " + objeto.getFecha() + "\n" +
                "Duración: " + objeto.getDuracion() + "\n" +
                "Género: " + objeto.getGenero() + "\n" +
                "Precio: S/ " + objeto.getPrecio() + "\n";
        textViewDetalles.setText(detalles);

        // Cargar la imagen de la portada con Glide
        Glide.with(context)
                .load(objeto.getPortada())
                .placeholder(R.drawable.ic_google)
                .error(R.drawable.ic_google)
                .into(imageViewPortada);

        // Crear el AlertDialog
        AlertDialog dialog = builder.create();

        // Configurar el botón "Agregar al carrito"
        buttonAgregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cantidadStr = editTextCantidad.getText().toString();
                if (!cantidadStr.isEmpty()) {
                    int cantidad = Integer.parseInt(cantidadStr);

                    // Validar cantidad mayor a 0
                    if (cantidad > 0) {
                        // Aquí puedes agregar el objeto con la cantidad seleccionada al carrito
                        agregarAlCarrito(objeto, cantidad);
                        Toast.makeText(context, "Agregado al carrito", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Cerrar el diálogo después de agregar
                    } else {
                        Toast.makeText(context, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Ingrese la cantidad", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón "Cerrar"
        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Cerrar el diálogo
            }
        });

        // Configurar botones para aumentar y disminuir
        buttonMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(editTextCantidad.getText().toString());
                if (cantidad > 1) { // Evitar que la cantidad baje de 1
                    cantidad--;
                    editTextCantidad.setText(String.valueOf(cantidad));
                }
            }
        });

        buttonMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(editTextCantidad.getText().toString());
                cantidad++;
                editTextCantidad.setText(String.valueOf(cantidad));
            }
        });

        // Mostrar el AlertDialog
        dialog.show();
    }


    // Método para obtener los elementos en el carrito
    public List<Objetos> getCarrito() {
        return carrito;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_portada;
        private TextView tv_titulo;
        private TextView tv_precio;
        private Button btn_agregar; // Botón para agregar

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_portada = itemView.findViewById(R.id.iv_portada);
            tv_titulo = itemView.findViewById(R.id.tv_titulo);
            tv_precio = itemView.findViewById(R.id.tv_precio);
            btn_agregar = itemView.findViewById(R.id.btn_agregar); // Asignar botón
        }
    }
}