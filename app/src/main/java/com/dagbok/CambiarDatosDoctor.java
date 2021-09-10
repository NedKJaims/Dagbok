package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Doctor;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class CambiarDatosDoctor extends AppCompatActivity {

    private EditText especialidades;
    private EditText horarios;

    private AlertDialog cargando;

    private List<String> especialidadesLocal;
    private List<String> horariosLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_datos_doctor);

        especialidades = findViewById(R.id.cambiarDatosDoctor_especialidad_text);
        horarios = findViewById(R.id.cambiarDatosDoctor_horarios_text);

        especialidadesLocal = new ArrayList<>();
        horariosLocal = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(CambiarDatosDoctor.this);
        builder.setCancelable(false);
        builder.setView(R.layout.popup_cargando_datos);
        cargando = builder.create();

        establecerDatos();

    }

    private void establecerDatos() {
        if(Global.usuario != null) {
            if(Global.usuario.isEsDoctor()) {
                Doctor doc = (Doctor)Global.usuario;

                if(doc.getEspecialidades() != null) {
                    especialidadesLocal = doc.getEspecialidades();
                    establecerEspecialidades();
                }

                if(doc.getHorarios() != null) {
                    horariosLocal = doc.getHorarios();
                    establecerHorarios();
                }

            }
        } else {
            finish();
        }
    }

    private void establecerEspecialidades() {
        StringBuilder builder = new StringBuilder();
        for(String esp : especialidadesLocal) {
            builder.append(esp.concat("\n"));
        }
        especialidades.setText(builder);
    }

    private void establecerHorarios() {
        StringBuilder builder = new StringBuilder();
        for(String horario : horariosLocal) {
            builder.append(horario.concat("\n"));
        }
        horarios.setText(builder);
    }

    private AlertDialog crearPopUpAgregarHorario() {
        AlertDialog.Builder esp = new AlertDialog.Builder(CambiarDatosDoctor.this);
        esp.setTitle(R.string.agregar_horario);
        esp.setView(R.layout.popup_cambio_datos_doctor);
        esp.setPositiveButton(R.string.aceptar, null);
        esp.setNegativeButton(R.string.cancelar, null);
        return esp.create();
    }

    private AlertDialog crearPopUpAgregarEspecialidad(EditText especialidad) {
        AlertDialog.Builder esp = new AlertDialog.Builder(CambiarDatosDoctor.this);
        esp.setTitle(R.string.agregar_especialidad);
        esp.setView(especialidad);
        esp.setNegativeButton(R.string.cancelar, null);
        esp.setPositiveButton(R.string.agregar, null);
        return esp.create();
    }

    @SuppressLint("NonConstantResourceId")
    private int obtenerDiaSeleccionado(int id) {
        switch (id) {
            case R.id.domingo:
                return 0;
            case R.id.lunes:
                return 1;
            case R.id.martes:
                return 2;
            case R.id.miercoles:
                return 3;
            case R.id.jueves:
                return 4;
            case R.id.viernes:
                return 5;
            case R.id.sabado:
                return 6;
        }
        return -1;
    }

    //evento botones
    public void atras(View v) {
        onBackPressed();
    }

    public void agregarEspecialidad(View v) {
        final EditText key = new EditText(CambiarDatosDoctor.this);
        key.setHint(R.string.especialidad);
        key.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog esp = crearPopUpAgregarEspecialidad(key);
        esp.show();
        esp.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            String texto = key.getText().toString();
            if(texto.isEmpty()) {
                key.setError(getString(R.string.llenar_campo_vacio));
            } else {
                especialidadesLocal.add(texto);
                establecerEspecialidades();
                esp.dismiss();
            }
        });
    }

    public void removerEspecialidad(View v) {
        if(especialidadesLocal.size() != 0) {
            AlertDialog.Builder esp = new AlertDialog.Builder(CambiarDatosDoctor.this);
            esp.setTitle(R.string.remover_especialidad);
            boolean[] seleccionados = new boolean[especialidadesLocal.size()];
            CharSequence[] prov = new CharSequence[especialidadesLocal.size()];
            for (int i = 0; i < especialidadesLocal.size(); i++) {
                prov[i] = especialidadesLocal.get(i);
            }
            esp.setMultiChoiceItems(prov, seleccionados, (dialogInterface, i, b) -> seleccionados[i] = b);
            esp.setNegativeButton(R.string.cancelar, null);
            esp.setPositiveButton(R.string.aceptar, (dialogInterface, i) -> {
                for(int s = 0; s < especialidadesLocal.size(); s++){
                    if(seleccionados[s]) {
                        especialidadesLocal.set(s, "");
                    }
                }
                for(int s = 0; s < especialidadesLocal.size(); s++) {
                    if(especialidadesLocal.get(s).isEmpty()) {
                        especialidadesLocal.remove(s);
                        s--;
                    }
                }
                establecerEspecialidades();
            });
            esp.show();
        } else {
            Toast.makeText(CambiarDatosDoctor.this, R.string.no_hay_especialidades, Toast.LENGTH_LONG).show();
        }

    }

    public void agregarHorario(View v) {
        AlertDialog agregarHorario = crearPopUpAgregarHorario();
        agregarHorario.show();
        final EditText desde = agregarHorario.findViewById(R.id.popup_cambio_datos_doctor_desde_text);
        final EditText hasta = agregarHorario.findViewById(R.id.popup_cambio_datos_doctor_hasta_text);
        final RadioGroup dia = agregarHorario.findViewById(R.id.popup_cambio_datos_doctor_dia_group);
        desde.setOnClickListener(view -> {
            TimePickerDialog tiempo = new TimePickerDialog(CambiarDatosDoctor.this, (timePicker, hora, minutos) -> desde.setText(Global.crearFormatoHora(CambiarDatosDoctor.this, hora, minutos)), 12, 0, false);
            tiempo.show();
        });
        hasta.setOnClickListener(view -> {
            TimePickerDialog tiempo = new TimePickerDialog(CambiarDatosDoctor.this, (timePicker, hora, minutos) -> hasta.setText(Global.crearFormatoHora(CambiarDatosDoctor.this, hora, minutos)), 12, 0, false);
            tiempo.show();
        });
        agregarHorario.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(view -> {
            boolean desdeVacio = desde.getText().toString().isEmpty();
            boolean hastaVacio = hasta.getText().toString().isEmpty();

            if(desdeVacio || hastaVacio) {
                if(desdeVacio) {
                    desde.setError(getString(R.string.llenar_campo_vacio));
                }
                if(hastaVacio) {
                    hasta.setError(getString(R.string.llenar_campo_vacio));
                }
            } else {
                int diaSeleccionado = obtenerDiaSeleccionado(dia.getCheckedRadioButtonId());
                String diafinal = getResources().getStringArray(R.array.dias)[diaSeleccionado] + " " + desde.getText().toString() + " - " + hasta.getText().toString();
                horariosLocal.add(diafinal);
                establecerHorarios();
                agregarHorario.dismiss();
            }
        });

    }

    public void removerHorario(View v) {
        if(horarios.getText().toString().isEmpty()) {
            Toast.makeText(CambiarDatosDoctor.this, R.string.no_hay_horarios, Toast.LENGTH_LONG).show();
        } else {
            AlertDialog.Builder rem = new AlertDialog.Builder(CambiarDatosDoctor.this);
            rem.setTitle(R.string.remover_horario);
            boolean[] seleccionados = new boolean[horariosLocal.size()];
            CharSequence[] prov = new CharSequence[horariosLocal.size()];
            for (int i = 0; i < horariosLocal.size(); i++) {
                prov[i] = horariosLocal.get(i);
            }
            rem.setMultiChoiceItems(prov, seleccionados, (dialogInterface, i, b) -> seleccionados[i] = b);
            rem.setNegativeButton(R.string.cancelar, null);
            rem.setPositiveButton(R.string.aceptar, (dialogInterface, s) -> {
                for(int i = 0; i < horariosLocal.size(); i++) {
                    if (seleccionados[i]) {
                        horariosLocal.set(i, "");
                    }
                }
                for(int i = 0; i < horariosLocal.size(); i++) {
                    if(horariosLocal.get(i).isEmpty()) {
                        horariosLocal.remove(i);
                        i--;
                    }
                }
                establecerHorarios();
            });
            rem.show();
        }
    }

    public void guardarDatosDoctor(View v) {
        String especialidadesText = especialidades.getText().toString();
        String horariosText = horarios.getText().toString();
        if(especialidadesText.isEmpty() || horariosText.isEmpty()) {
            if(especialidadesText.isEmpty()) {
                ((TextInputLayout)especialidades.getParent().getParent()).setError(getString(R.string.llenar_campo_vacio));
            }
            if(horariosText.isEmpty()) {
                ((TextInputLayout)horarios.getParent().getParent()).setError(getString(R.string.llenar_campo_vacio));
            }
        } else {
            cargando.show();
            Doctor doctor = (Doctor) Global.usuario;
            doctor.setEspecialidades(especialidadesLocal);
            doctor.setHorarios(horariosLocal);

            String id = "Usuarios/".concat(Global.firebaseUsuario.getUid());
            FirebaseFirestore.getInstance().document(id).set(doctor, SetOptions.merge()).addOnSuccessListener(unused -> {
                Toast.makeText(CambiarDatosDoctor.this, R.string.cambio_datos_exitoso, Toast.LENGTH_LONG).show();
                cargando.dismiss();
                setResult(Global.CAMBIO_DATOS);
            }).addOnFailureListener(e -> {
                Toast.makeText(CambiarDatosDoctor.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
                cargando.dismiss();
            });

        }
    }
}