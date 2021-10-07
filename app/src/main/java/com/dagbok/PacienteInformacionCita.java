package com.dagbok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagbok.componentes.ComponenteTarjetaUsuario;
import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;
import com.dagbok.objetos.Doctor;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class PacienteInformacionCita extends AppCompatActivity {

    private TextView enfermedad;
    private TextView descripcion;
    private TextView proceso;
    private TextView fecha;
    private TextView proximasFechasPadre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_informacion_cita);

        enfermedad = findViewById(R.id.informacion_cita_enfermedad_text);
        descripcion = findViewById(R.id.informacion_cita_descripcion_text);
        proceso = findViewById(R.id.informacion_cita_preceso_text);
        fecha = findViewById(R.id.informacion_cita_fecha_text);
        proximasFechasPadre = findViewById(R.id.informacion_cita_proxima_fechas);

        establecerInformacionCita();

        Cita cita = getIntent().getParcelableExtra("cita");
        LinearLayout informacionPaciente = findViewById(R.id.paciente_informacion_cita_doctor);
        FirebaseFirestore.getInstance().document("Usuarios/".concat(cita.getIdDoctor())).get().addOnSuccessListener(documentSnapshot -> {
            Doctor doc = (Doctor) Objects.requireNonNull(documentSnapshot.toObject(Doctor.class));
            int tarjeta = ComponenteTarjetaUsuario.TARJETA_ESTATUS_CONSULTORIO_DOCTOR;
            ComponenteTarjetaUsuario tarjetaUsuario = new ComponenteTarjetaUsuario(PacienteInformacionCita.this, tarjeta, doc);
            informacionPaciente.addView(tarjetaUsuario);
        }).addOnFailureListener(e -> Toast.makeText(PacienteInformacionCita.this, R.string.hubo_error, Toast.LENGTH_LONG).show());

    }

    private void establecerInformacionCita() {
        Cita cita = getIntent().getParcelableExtra("cita");
        Global.establecerFormatoColonTextView(enfermedad, getString(R.string.enfermedad_colon), cita.getEnfermedad());

        Global.establecerFormatoColonTextView(descripcion, getString(R.string.descripcion_colon), cita.getDescripcion());

        String procesoTxt = (cita.isActiva()) ? getString(R.string.tratamiento) : getString(R.string.finalizado);
        Global.establecerFormatoColonTextView(proceso, getString(R.string.proceso_colon), procesoTxt);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cita.getFecha().toDate());
        Global.establecerFormatoColonTextView(fecha, getString(R.string.fecha_colon), Global.crearFormatoTiempo(PacienteInformacionCita.this, calendar));

        if(cita.getProximasFechas() != null) {
            if (cita.getProximasFechas().size() != 0) {
                proximasFechasPadre.setText(obtenerStringCitas(cita, calendar));
                proximasFechasPadre.setGravity(Gravity.START);
                proximasFechasPadre.setTextColor(getColor(R.color.black));
            }
        }
    }

    @Nullable
    private StringBuilder obtenerStringCitas(@NonNull Cita cita, @NonNull Calendar calendar) {
        StringBuilder data = (cita.getProximasFechas().size() != 0) ? new StringBuilder() : null;
        for (int i = 0; i < cita.getProximasFechas().size(); i++) {
            Objects.requireNonNull(data);
            calendar.setTime(cita.getProximasFechas().get(i).toDate());
            data.append(Global.crearFormatoTiempo(PacienteInformacionCita.this, calendar));
            if (i != cita.getProximasFechas().size() - 1)
                data.append("\n");
        }
        return data;
    }


    public void atras(View v) { onBackPressed(); }

}