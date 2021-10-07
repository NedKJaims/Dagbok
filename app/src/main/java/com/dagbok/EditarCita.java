package com.dagbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EditarCita extends AppCompatActivity {

    private EditText enfermedad;
    private EditText descripcion;
    private EditText fecha;
    private EditText subfechas;

    private Timestamp fechaLocal;
    private List<Timestamp> subFechasLocal;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_cita);

        enfermedad = findViewById(R.id.editarCita_enfermedad_text);
        descripcion = findViewById(R.id.editarCita_descripcion_text);
        fecha = findViewById(R.id.editarCita_fecha_text);
        subfechas = findViewById(R.id.editarCita_subFechas_text);

        subFechasLocal = new ArrayList<>();

        Cita cita = getIntent().getParcelableExtra("cita");
        enfermedad.setText(cita.getEnfermedad());
        descripcion.setText(cita.getDescripcion());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cita.getFecha().toDate());
        fechaLocal = cita.getFecha();
        fecha.setText(Global.crearFormatoTiempo(EditarCita.this,calendar));
        if(cita.getProximasFechas() != null) {
            subFechasLocal.addAll(cita.getProximasFechas());
            actualizarSubFechas();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(EditarCita.this);
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
        return Global.crearFormatoTiempo(EditarCita.this, calendar);
    }

    private boolean algunCampoVacio() {
        boolean result = false;
        if(fechaLocal == null) {
            fecha.setError(getString(R.string.llenar_campo_vacio));
            result = true;
        }

        if(enfermedad.getText().toString().isEmpty()){
            enfermedad.setError(getString(R.string.llenar_campo_vacio));
            result = true;
        }
        return result;
    }

    public void establecerFecha(View v) {
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog fechaDialog = new DatePickerDialog(EditarCita.this, (datePicker, ano1, mes1, dia1) -> {
            calendar.set(ano1, mes1, dia1,0,0);
            if(comprobarFechaRepetida(calendar)) {
                Toast.makeText(EditarCita.this, R.string.fecha_repetida, Toast.LENGTH_LONG).show();
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
            final DatePickerDialog fechaDialog = new DatePickerDialog(EditarCita.this, (datePicker, ano1, mes1, dia1) -> {
                calendar.set(ano1, mes1, dia1,0,0);
                if (comprobarFechaRepetida(calendar)) {
                    Toast.makeText(EditarCita.this, R.string.fecha_repetida, Toast.LENGTH_LONG).show();
                } else {
                    if(comprobarFechaNoMenor(calendar)) {
                        Toast.makeText(EditarCita.this, R.string.fecha_desfazada_menor, Toast.LENGTH_LONG).show();
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditarCita.this);
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
            Toast.makeText(EditarCita.this, R.string.no_hay_sub_fechas, Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Cita obtenerResultado() {

        Cita anterior = getIntent().getParcelableExtra("cita");
        Cita resultado = new Cita();

        resultado.setActiva(anterior.isActiva());
        resultado.setIdDoctor(anterior.getIdDoctor());
        resultado.setIdPaciente(anterior.getIdPaciente());
        resultado.setNombrePaciente(anterior.getNombrePaciente());

        resultado.setEnfermedad(enfermedad.getText().toString());
        resultado.setDescripcion(descripcion.getText().toString());
        resultado.setFecha(fechaLocal);
        resultado.setProximasFechas(subFechasLocal);

        return resultado;
    }

    public void atras(View v) { onBackPressed(); }

    public void eliminarCita(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarCita.this);
        builder.setTitle(R.string.pregunta_eliminar_cita);
        builder.setNegativeButton(R.string.cancelar, null);
        builder.setPositiveButton(R.string.aceptar, (dialogInterface, i) -> {
            String id = getIntent().getStringExtra("idCita");
            cargando.show();
            FirebaseFirestore.getInstance().document("Citas/".concat(id)).delete().addOnSuccessListener(unused -> {
                cargando.dismiss();
                setResult(Global.ELIMINO_CITA);
                finish();
            });
        });
        builder.show();
    }

    public void actualizarCita(View v) {
        if(!algunCampoVacio()) {
            cargando.show();
            Cita nueva = obtenerResultado();
            String id = getIntent().getStringExtra("idCita");
            FirebaseFirestore.getInstance().document("Citas/".concat(id)).set(nueva, SetOptions.merge()).addOnSuccessListener(unused -> {
                cargando.dismiss();
                Intent resultado = new Intent();
                resultado.putExtra("cita",nueva);
                setResult(Global.MODIFICO_CITA,resultado);
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(EditarCita.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
                cargando.dismiss();
            });

        }
    }

}