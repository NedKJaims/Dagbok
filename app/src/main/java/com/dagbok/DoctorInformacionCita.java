package com.dagbok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagbok.componentes.ComponenteTarjetaUsuario;
import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;
import com.dagbok.objetos.Usuario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class DoctorInformacionCita extends AppCompatActivity {

    private TextView enfermedad;
    private TextView descripcion;
    private TextView proceso;
    private TextView fecha;
    private TextView proximasFechasPadre;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_informacion_cita);

        AlertDialog.Builder builder = new AlertDialog.Builder(DoctorInformacionCita.this);
        builder.setView(R.layout.popup_cargando_datos);
        builder.setCancelable(false);
        cargando = builder.create();

        enfermedad = findViewById(R.id.informacion_cita_enfermedad_text);
        descripcion = findViewById(R.id.informacion_cita_descripcion_text);
        proceso = findViewById(R.id.informacion_cita_preceso_text);
        fecha = findViewById(R.id.informacion_cita_fecha_text);
        proximasFechasPadre = findViewById(R.id.informacion_cita_proxima_fechas);

        establecerInformacionCita();

        LinearLayout recetaPadre = findViewById(R.id.informacion_cita_receta_padre);

        Cita cita = getIntent().getParcelableExtra("cita");
        LinearLayout informacionPaciente = findViewById(R.id.doctor_informacion_cita_paciente);
        FirebaseFirestore.getInstance().document("Usuarios/".concat(cita.getIdPaciente())).get().addOnSuccessListener(documentSnapshot -> {
            Usuario paciente = Objects.requireNonNull(documentSnapshot.toObject(Usuario.class));
            int tarjeta = ComponenteTarjetaUsuario.TARJETA_SENCILLA_USUARIO;
            ComponenteTarjetaUsuario tarjetaUsuario = new ComponenteTarjetaUsuario(DoctorInformacionCita.this, tarjeta, paciente);
            informacionPaciente.addView(tarjetaUsuario);
        }).addOnFailureListener(e -> Toast.makeText(DoctorInformacionCita.this, R.string.hubo_error, Toast.LENGTH_LONG).show());

    }

    private void establecerInformacionCita() {
        Cita cita = getIntent().getParcelableExtra("cita");
        Global.establecerFormatoColonTextView(enfermedad, getString(R.string.enfermedad_colon), cita.getEnfermedad());

        Global.establecerFormatoColonTextView(descripcion, getString(R.string.descripcion_colon), cita.getDescripcion());

        String procesoTxt = (cita.isActiva()) ? getString(R.string.tratamiento) : getString(R.string.finalizado);
        Global.establecerFormatoColonTextView(proceso, getString(R.string.proceso_colon), procesoTxt);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cita.getFecha().toDate());
        Global.establecerFormatoColonTextView(fecha, getString(R.string.fecha_colon), Global.crearFormatoTiempo(DoctorInformacionCita.this, calendar));

        if(cita.getProximasFechas().size() != 0) {

            proximasFechasPadre.setText(obtenerStringCitas(cita, calendar));
            proximasFechasPadre.setGravity(Gravity.START);
            proximasFechasPadre.setTextColor(getColor(R.color.black));

            LinearLayout padre = (LinearLayout) proximasFechasPadre.getParent();
            CardView boton = crearBotonVerMas(getString(R.string.establecer_proxima_fecha));
            boton.setOnClickListener(view -> {
                Timestamp prox = cita.getProximasFechas().get(0);
                cita.setFecha(prox);
                cita.getProximasFechas().remove(0);

                cargando.show();
                String id = getIntent().getStringExtra("idCita");
                Task<Void> ref = FirebaseFirestore.getInstance().document("Citas/".concat(id))
                        .update("fecha", cita.getFecha(), "proximasFechas", cita.getProximasFechas());
                ref.addOnSuccessListener(unused -> {
                    setResult(Global.MODIFICO_CITA);
                    cargando.dismiss();
                    calendar.setTime(cita.getFecha().toDate());
                    Global.establecerFormatoColonTextView(fecha, getString(R.string.fecha_colon), Global.crearFormatoTiempo(DoctorInformacionCita.this, calendar));

                    StringBuilder data = obtenerStringCitas(cita, calendar);
                    if(data != null) {
                        proximasFechasPadre.setText(data);
                        proximasFechasPadre.setGravity(Gravity.START);
                        proximasFechasPadre.setTextColor(getColor(R.color.black));
                    } else {
                        proximasFechasPadre.setText(R.string.no_hay_proximas_citas);
                        proximasFechasPadre.setGravity(Gravity.CENTER_HORIZONTAL);
                        proximasFechasPadre.setTextColor(getColor(R.color.gray_default));
                        padre.removeViewAt(padre.getChildCount() - 1);
                    }
                }).addOnFailureListener(e -> {
                    cargando.dismiss();
                    Toast.makeText(DoctorInformacionCita.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                });
            });
            padre.addView(boton);
        }
    }

    @Nullable
    private StringBuilder obtenerStringCitas(@NonNull Cita cita, @NonNull Calendar calendar) {
        StringBuilder data = (cita.getProximasFechas().size() != 0) ? new StringBuilder() : null;
        for (int i = 0; i < cita.getProximasFechas().size(); i++) {
            Objects.requireNonNull(data);
            calendar.setTime(cita.getProximasFechas().get(i).toDate());
            data.append(Global.crearFormatoTiempo(DoctorInformacionCita.this, calendar));
            if (i != cita.getProximasFechas().size() - 1)
                data.append("\n");
        }
        return data;
    }

    @NonNull
    private LinearLayout.LayoutParams crearParametros() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margen = (int)(10 * getResources().getDisplayMetrics().density);
        params.setMargins(margen,margen,margen,margen);
        return params;
    }

    @NonNull
    private TextView crearTexto(CharSequence texto, int colorTexto) {
        LinearLayout.LayoutParams params = crearParametros();
        TextView res = new TextView(DoctorInformacionCita.this);
        res.setText(texto);
        res.setTypeface(ResourcesCompat.getFont(DoctorInformacionCita.this, R.font.coolvetica));
        if(colorTexto != -1)
            res.setTextColor(getColor(colorTexto));
        res.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        res.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        res.setLayoutParams(params);
        return res;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    private CardView crearBotonVerMas(CharSequence texto) {
        LinearLayout.LayoutParams params = crearParametros();

        TextView res = crearTexto(texto,R.color.black);

        CardView view = new CardView(DoctorInformacionCita.this);
        view.setCardBackgroundColor(ColorStateList.valueOf(getColor(R.color.verde_primario)));
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        view.setForeground(getDrawable(outValue.resourceId));
        view.addView(res, params);
        view.setRadius(6 * getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
        return view;
    }

    public void atras(View v) { onBackPressed(); }

}