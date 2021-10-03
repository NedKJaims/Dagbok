package com.dagbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dagbok.componentes.ComponenteTarjetaUsuario;
import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;
import com.dagbok.objetos.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AgregarCita extends AppCompatActivity {

    private LinearLayout padreDatosUsuario;

    private EditText fecha;
    private EditText subfechas;
    private EditText correoPaciente;

    private Timestamp fechaLocal;
    private String idPaciente;
    private List<Timestamp> subFechasLocal;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cita);

        fecha = findViewById(R.id.agregarCita_fecha_text);
        subfechas = findViewById(R.id.agregarCita_subFechas_text);
        correoPaciente = findViewById(R.id.agregarCita_correoPaciente_email);

        padreDatosUsuario = findViewById(R.id.agregarCita_paciente_padre_layout);

        subFechasLocal = new ArrayList<>();
        idPaciente = "";

        final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarCita.this);
        builder.setCancelable(false);
        builder.setView(R.layout.popup_cargando_datos);
        cargando = builder.create();

    }

    private void actualizarSubFechas() {
        Collections.sort(subFechasLocal);
        final StringBuilder builder = new StringBuilder();
        for(Timestamp stap : subFechasLocal) {
            final String fecha = obtenerFechaTexto(stap);
            builder.append(fecha.concat("\n"));
        }
        subfechas.setText(builder);
    }

    private boolean compararFechas(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    private boolean comprobarFechaRepetida(@NonNull Calendar fecha) {
        final Calendar comparator = Calendar.getInstance();

        if(fechaLocal != null) {
            comparator.setTime(fechaLocal.toDate());
            if(compararFechas(comparator, fecha)) {
                return true;
            }
        }
        for(int i = 0; i < subFechasLocal.size(); i++) {
            comparator.setTime(subFechasLocal.get(i).toDate());
            if(compararFechas(comparator, fecha)) {
                return true;
            }
        }
        return false;
    }

    private boolean comprobarFechaNoMenor(@NonNull Calendar fecha) {
        //Este metodo verifica que la fecha no sea menor que las demas fechas, para evitar incongurencias
        final Calendar comparator = Calendar.getInstance();
        if(fechaLocal != null) {
            comparator.setTime(fechaLocal.toDate());
            return fecha.before(comparator);
        }
        return false;
    }

    @NonNull
    private String obtenerFechaTexto(Timestamp tiempo) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(tiempo.toDate());
        return Global.crearFormatoTiempo(AgregarCita.this, calendar);
    }

    private boolean algunCampoVacio() {
        boolean result = false;
        if(fechaLocal == null) {
            fecha.setError(getString(R.string.llenar_campo_vacio));
            result = true;
        }

        if(idPaciente.isEmpty()) {
            correoPaciente.setError(getString(R.string.busque_usuario));
            result = true;
        }
        return result;
    }

    public void atras(View v) {
        onBackPressed();
    }

    public void establecerFecha(View v) {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog fechaDialog = new DatePickerDialog(AgregarCita.this, (datePicker, ano1, mes1, dia1) -> {
            calendar.set(ano1, mes1, dia1,0,0);
            if(comprobarFechaRepetida(calendar)) {
                Toast.makeText(AgregarCita.this, R.string.fecha_repetida, Toast.LENGTH_LONG).show();
            } else {
                fechaLocal = new Timestamp(calendar.getTime());
                final String tiempoTexto = obtenerFechaTexto(fechaLocal);
                fecha.setText(tiempoTexto);
                fecha.setError(null);
                subFechasLocal.clear();
                actualizarSubFechas();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fechaDialog.show();
    }

    public void agregarSubFecha(View v) {
        if(fechaLocal == null) {
            fecha.setError(getString(R.string.asigne_fecha));
        } else {
            final Calendar calendar = Calendar.getInstance();
            final DatePickerDialog fechaDialog = new DatePickerDialog(AgregarCita.this, (datePicker, ano1, mes1, dia1) -> {
                calendar.set(ano1, mes1, dia1,0,0);
                if (comprobarFechaRepetida(calendar)) {
                    Toast.makeText(AgregarCita.this, R.string.fecha_repetida, Toast.LENGTH_LONG).show();
                } else {
                    if(comprobarFechaNoMenor(calendar)) {
                        Toast.makeText(AgregarCita.this, R.string.fecha_desfazada_menor, Toast.LENGTH_LONG).show();
                    } else {
                        Timestamp temp = new Timestamp(calendar.getTime());
                        subFechasLocal.add(temp);
                        actualizarSubFechas();
                    }
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            fechaDialog.show();
        }
    }

    public void eliminarSubFecha(View v) {
        if(!subFechasLocal.isEmpty()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarCita.this);
            final CharSequence[] escoger = new CharSequence[subFechasLocal.size()];
            final boolean[] selecciondas = new boolean[subFechasLocal.size()];
            for (int i = 0; i < subFechasLocal.size(); i++) {
                escoger[i] = obtenerFechaTexto(subFechasLocal.get(i));
            }
            builder.setMultiChoiceItems(escoger, selecciondas, (dialogInterface, i, b) -> selecciondas[i] = b);
            builder.setNegativeButton(R.string.cancelar, null);
            builder.setPositiveButton(R.string.aceptar, (dialogInterface, s) -> {
                for (int i = 0; i < subFechasLocal.size(); i++) {
                    if (selecciondas[i]) {
                        subFechasLocal.set(i, null);
                    }
                }
                for (int i = 0; i < subFechasLocal.size(); i++) {
                    if (subFechasLocal.get(i) == null) {
                        subFechasLocal.remove(i);
                        i--;
                    }
                }
                actualizarSubFechas();
            });
            builder.show();
        } else {
            Toast.makeText(AgregarCita.this, R.string.no_hay_sub_fechas, Toast.LENGTH_LONG).show();
        }
    }

    public void buscarUsuario(View v) {
        final String correo = correoPaciente.getText().toString();
        FirebaseFirestore.getInstance().collection("Usuarios").whereEqualTo("correo", correo).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(queryDocumentSnapshots.isEmpty()) {
                Toast.makeText(AgregarCita.this, R.string.no_encontro_usuario, Toast.LENGTH_LONG).show();
            } else {
                idPaciente = queryDocumentSnapshots.getDocuments().get(0).getId();
                //indice 0 ya que no existen correos repetidos, entonces solo habra un usuario en la lista
                Usuario paciente = queryDocumentSnapshots.getDocuments().get(0).toObject(Usuario.class);
                Objects.requireNonNull(paciente);
                int tipoTarjeta = ComponenteTarjetaUsuario.TARJETA_SENCILLA_USUARIO;
                ComponenteTarjetaUsuario tarjetaUsuario = new ComponenteTarjetaUsuario(AgregarCita.this, tipoTarjeta, paciente);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int margen = (int)(10 * getResources().getDisplayMetrics().density);
                params.setMargins(margen,margen,margen,margen);
                padreDatosUsuario.removeAllViews();
                padreDatosUsuario.addView(tarjetaUsuario,params);
            }
        }).addOnFailureListener(e -> Toast.makeText(AgregarCita.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show());
    }

    public void agregarCita(View v) {
        if(!algunCampoVacio()) {
            Cita cita = new Cita(Global.firebaseUsuario.getUid(), idPaciente, fechaLocal, subFechasLocal);
            cargando.show();
            FirebaseFirestore.getInstance().collection("Citas").add(cita).addOnSuccessListener(documentReference -> {

                Intent datos = new Intent();
                datos.putExtra("cita", cita);
                setResult(Global.AGREGO_CITA, datos);

                cargando.dismiss();
                Toast.makeText(AgregarCita.this, R.string.agrego_cita, Toast.LENGTH_LONG).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(AgregarCita.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
                cargando.dismiss();
            });
        }
    }

}