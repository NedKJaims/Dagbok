package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Doctor;
import com.dagbok.objetos.EstadoConsultorio;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import javax.annotation.Nullable;

public class PacienteEstatusConsultorio extends AppCompatActivity {

    private ImageView estatusFoto;
    private TextView estatusTexto;
    private ImageView fotoDoctor;
    private TextView nombreDoctor;
    private TextView especialidadesDoctor;
    private TextView horariosDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatus_consultorio);

        DocumentReference estatus = FirebaseFirestore.getInstance().document("Consultorio/Estado");

        Button cambiarEstatus = findViewById(R.id.doctorEstatusConsultorio_cambio_boton);
        Button tomarTurno = findViewById(R.id.doctorEstatusConsultorio_tomar_boton);
        Button terminarTurno = findViewById(R.id.doctorEstatusConsultorio_terminar_boton);

        LinearLayout padre = (LinearLayout) cambiarEstatus.getParent();
        padre.removeView(cambiarEstatus);
        padre.removeView(tomarTurno);
        padre.removeView(terminarTurno);

        estatusFoto = findViewById(R.id.estatusConsultorio_estado_foto);
        estatusTexto = findViewById(R.id.estatusConsultorio_estado_texto);
        fotoDoctor = findViewById(R.id.estatusConsultorio_doctor_foto);
        nombreDoctor = findViewById(R.id.estatusConsultorio_doctorNombre_texto);
        especialidadesDoctor = findViewById(R.id.estatusConsultorio_doctorEspecialidades_texto);
        horariosDoctor = findViewById(R.id.estatusConsultorio_doctorHorarios_texto);

        estatus.addSnapshotListener((value, error) -> {
            if(error == null) {
                if(Objects.requireNonNull(value).exists()) {
                    EstadoConsultorio estado = Objects.requireNonNull(value.toObject(EstadoConsultorio.class));
                    establecerEstatusConsultorio(estado.getEstatus());
                    if(estado.getIdDoctor().matches(Global.firebaseUsuario.getUid())) {
                        Doctor doctor = (Doctor) Global.usuario;
                        establecerDatosDoctor(doctor);
                    } else {
                        if(!estado.getIdDoctor().isEmpty()) {
                            FirebaseFirestore.getInstance().document("Usuarios/".concat(estado.getIdDoctor())).get().addOnSuccessListener(documentSnapshot -> {
                                if(documentSnapshot.exists()) {
                                    Doctor doctor = Objects.requireNonNull(documentSnapshot.toObject(Doctor.class));
                                    establecerDatosDoctor(doctor);
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(PacienteEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                                finish();
                            });
                        } else {
                            establecerDatosDoctor(null);
                        }
                    }
                } else {
                    Toast.makeText(PacienteEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Toast.makeText(PacienteEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }

    private void establecerEstatusConsultorio(int estatus) {
        switch (estatus) {
            case Global.CONSULTORIO_CERRADO:
                estatusFoto.setImageResource(R.drawable.ic_estatus_no_disponible);
                estatusTexto.setText(R.string.cerrado);
                break;
            case Global.CONSULTORIO_DISPONIBLE:
                estatusFoto.setImageResource(R.drawable.ic_estatus_disponible);
                estatusTexto.setText(R.string.disponible);
                break;
            case Global.CONSULTORIO_OCUPADO:
                estatusFoto.setImageResource(R.drawable.ic_estatus_ocupado);
                estatusTexto.setText(R.string.ocupado);
                break;
        }
    }

    private void establecerDatosDoctor(@Nullable Doctor doctor) {
        if(doctor != null) {
            Picasso.get().load(doctor.getUrlFoto())
                    .placeholder(R.drawable.ic_no_disponible)
                    .resize(1080, 1080)
                    .onlyScaleDown()
                    .into(fotoDoctor);
            nombreDoctor.setText(doctor.getNombre().concat(doctor.getApellidos()));
            StringBuilder especialidades = new StringBuilder();
            for(String especialidad : doctor.getEspecialidades()) {
                especialidades.append(especialidad.concat("\n"));
            }
            especialidadesDoctor.setText(especialidades);
            StringBuilder horarios = new StringBuilder();
            for(String horario : doctor.getHorarios()) {
                horarios.append(horario.concat("\n"));
            }
            horariosDoctor.setText(horarios);
        } else {
            nombreDoctor.setText("");
            fotoDoctor.setImageResource(R.drawable.ic_no_disponible);
            especialidadesDoctor.setText("");
            horariosDoctor.setText("");
        }

    }


    public void atras(View v) {
        onBackPressed();
    }

}