package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagbok.objetos.Cita;
import com.google.firebase.Timestamp;

public class DoctorInformacionCita extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_informacion_cita);

        Cita cita = getIntent().getParcelableExtra("cita");
        TextView proceso = findViewById(R.id.informacion_cita_preceso_text);
        TextView fecha = findViewById(R.id.informacion_cita_fecha_text);

        LinearLayout proximasFechasPadre = findViewById(R.id.informacion_cita_proxima_fechas_padre);
        LinearLayout recetaPadre = findViewById(R.id.informacion_cita_receta_padre);

        View informacionPaciente = findViewById(R.id.doctor_informacion_cita_paciente);

    }

    public void atras(View v) {
        onBackPressed();
    }

}