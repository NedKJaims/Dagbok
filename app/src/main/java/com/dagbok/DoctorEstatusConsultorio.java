package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class DoctorEstatusConsultorio extends AppCompatActivity {

    private DocumentReference estatus;
    private EstadoConsultorio estadoActual;

    private Button cambiarEstatus;
    private Button tomarTurno;
    private Button terminarTurno;

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

        estatus = FirebaseFirestore.getInstance().document("Consultorio/Estado");
        estadoActual = null;

        cambiarEstatus = findViewById(R.id.doctorEstatusConsultorio_cambio_boton);
        tomarTurno = findViewById(R.id.doctorEstatusConsultorio_tomar_boton);
        terminarTurno = findViewById(R.id.doctorEstatusConsultorio_terminar_boton);

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
                    estadoActual = estado;
                    establecerEstatusConsultorio(estado.getEstatus());
                    if(estado.getIdDoctor().matches(Global.firebaseUsuario.getUid())) {
                        Doctor doctor = (Doctor) Global.usuario;
                        establecerDatosDoctor(doctor);
                        actualizarBotones(false, true, true);
                    } else {
                        if(!estado.getIdDoctor().isEmpty()) {
                            FirebaseFirestore.getInstance().document("Usuarios/".concat(estado.getIdDoctor())).get().addOnSuccessListener(documentSnapshot -> {
                                if(documentSnapshot.exists()) {
                                    Doctor doctor = Objects.requireNonNull(documentSnapshot.toObject(Doctor.class));
                                    establecerDatosDoctor(doctor);
                                    actualizarBotones(true, false, false);
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                                finish();
                            });
                        } else {
                            actualizarBotones(true, false, false);
                            establecerDatosDoctor(null);
                        }
                    }
                } else {
                    Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show();
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

    private void actualizarBotones(boolean tomarTurnoEnable, boolean cambiarEstatusEnable, boolean terminarTurnoEnable) {
        tomarTurno.setEnabled(tomarTurnoEnable);
        cambiarEstatus.setEnabled(cambiarEstatusEnable);
        terminarTurno.setEnabled(terminarTurnoEnable);
    }

    private void establecerDatosDoctor(@Nullable Doctor doctor) {
        if(doctor != null) {
            if(!doctor.getUrlFoto().isEmpty())
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

    @SuppressLint("NonConstantResourceId")
    public void cambiarEstatus(View v) {
        PopupMenu menu = new PopupMenu(DoctorEstatusConsultorio.this, v, Gravity.CENTER);
        menu.getMenuInflater().inflate(R.menu.menu_estado_consultorio, menu.getMenu());
        menu.show();
        switch (estadoActual.getEstatus()) {
            case Global.CONSULTORIO_DISPONIBLE:
                menu.getMenu().removeItem(R.id.disponible);
                break;
            case Global.CONSULTORIO_OCUPADO:
                menu.getMenu().removeItem(R.id.ocupado);
                break;
            case Global.CONSULTORIO_CERRADO:
                menu.getMenu().removeItem(R.id.cerrado);
                break;
        }
        menu.setOnMenuItemClickListener(menuItem -> {
            int nuevoEstado = -1;
            switch (menuItem.getItemId()) {
                case R.id.disponible:
                    nuevoEstado = Global.CONSULTORIO_DISPONIBLE;
                    break;
                case R.id.ocupado:
                    nuevoEstado = Global.CONSULTORIO_OCUPADO;
                    break;
                case R.id.cerrado:
                    nuevoEstado = Global.CONSULTORIO_CERRADO;
                    break;
            }
            estatus.update("estatus", nuevoEstado).addOnFailureListener(e -> Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show());
            return false;
        });

    }

    public void tomarTurno(View v) {
        estatus.update("idDoctor",Global.firebaseUsuario.getUid(), "estatus", Global.CONSULTORIO_DISPONIBLE).addOnFailureListener(e -> Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show());
    }

    public void terminarTurno(View v) {
        if(estadoActual.getIdDoctor().matches(Global.firebaseUsuario.getUid())) {
            estatus.update("idDoctor", "", "estatus", Global.CONSULTORIO_CERRADO).addOnFailureListener(e -> Toast.makeText(DoctorEstatusConsultorio.this, R.string.hubo_error, Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(DoctorEstatusConsultorio.this, R.string.no_puede_terminar_turno_no_suyo, Toast.LENGTH_LONG).show();
        }
    }

}