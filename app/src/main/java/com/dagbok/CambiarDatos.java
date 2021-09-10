package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Usuario;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;

public class CambiarDatos extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;
    private EditText estatura;
    private EditText peso;
    private EditText fechaNacimiento;
    private RadioGroup sexo;
    private Timestamp fecha;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_datos);

        AlertDialog.Builder builder = new AlertDialog.Builder(CambiarDatos.this);
        builder.setCancelable(false);
        builder.setView(R.layout.popup_cargando_datos);
        cargando = builder.create();

        nombre = findViewById(R.id.cambiarDatos_nombre_text);
        apellidos = findViewById(R.id.cambiarDatos_apellidos_text);
        estatura = findViewById(R.id.cambiarDatos_estatura_number);
        peso = findViewById(R.id.cambiarDatos_peso_number);
        fechaNacimiento = findViewById(R.id.cambiarDatos_fecha_date);
        sexo = findViewById(R.id.cambiarDatos_sexo_radiogroup);

        establecerDatos();

    }

    //otras funciones
    private boolean algunCampoVacio() {
        boolean nombreVacio = nombre.getText().toString().isEmpty();
        if(nombreVacio)
            establecerErrorPadre(nombre);
        else vaciarErrorPadre(nombre);
        boolean apellidosVacio = apellidos.getText().toString().isEmpty();
        if(apellidosVacio)
            establecerErrorPadre(apellidos);
        else vaciarErrorPadre(apellidos);
        boolean estaturaVacio = estatura.getText().toString().isEmpty();
        if(estaturaVacio)
            establecerErrorPadre(estatura);
        else vaciarErrorPadre(estatura);
        boolean pesoVacio = peso.getText().toString().isEmpty();
        if(pesoVacio)
            establecerErrorPadre(peso);
        else vaciarErrorPadre(peso);
        boolean fechaNacimientoVacio = fechaNacimiento.getText().toString().isEmpty();
        if(fechaNacimientoVacio)
            establecerErrorPadre(fechaNacimiento);
        else vaciarErrorPadre(fechaNacimiento);

        return nombreVacio || apellidosVacio || estaturaVacio || pesoVacio || fechaNacimientoVacio;
    }

    private void establecerDatos() {
        if(Global.usuario != null) {
            nombre.setText(Global.usuario.getNombre());
            apellidos.setText(Global.usuario.getApellidos());
            estatura.setText(String.valueOf(Global.usuario.getEstatura()));
            peso.setText(String.valueOf(Global.usuario.getPeso()));
            if(Global.usuario.getSexo() == 0) {
                ((RadioButton)findViewById(R.id.hombre)).setChecked(true);
            } else {
                ((RadioButton)findViewById(R.id.mujer)).setChecked(true);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Global.usuario.getFechaNacimiento().toDate());
            StringBuilder diaBuilder = new StringBuilder();

            fecha = Global.usuario.getFechaNacimiento();

            fechaNacimiento.setText(diaBuilder.append(calendar.get(Calendar.DAY_OF_MONTH)).append("/").
                    append(getResources().getStringArray(R.array.meses)[calendar.get(Calendar.MONTH)])
                    .append("/").append(calendar.get(Calendar.YEAR)).toString());

        }
    }

    private void establecerErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError(getString(R.string.llenar_campo_vacio));
    }
    private void vaciarErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError("");
    }

    //evento de botones de interfaz
    public void atras(View v) {
        onBackPressed();
    }

    public void establecerFechaCampo(View v) {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(CambiarDatos.this, (datePicker, ano1, mes1, dia1) -> {
            calendar.set(ano1, mes1, dia1, 0,0);
            fecha = new Timestamp(calendar.getTime());
            StringBuilder diaBuilder = new StringBuilder();

            fechaNacimiento.setText(diaBuilder.append(dia1).append("/").
                    append(getResources().getStringArray(R.array.meses)[mes1])
                    .append("/").append(ano1).toString());
        },ano, mes, dia);
        dialog.show();
    }

    public void cambiarDatos(View v) {
        if(!algunCampoVacio()) {
            cargando.show();
            int sex = sexo.getCheckedRadioButtonId() == R.id.hombre ? 0 : 1;
            if(Global.usuario == null) {
                Global.usuario = new Usuario(nombre.getText().toString(), apellidos.getText().toString(),
                        fecha, Float.parseFloat(estatura.getText().toString()),
                        Float.parseFloat(peso.getText().toString()), sex);
            } else {
                Global.usuario.setNombre(nombre.getText().toString());
                Global.usuario.setApellidos(apellidos.getText().toString());
                Global.usuario.setFechaNacimiento(fecha);
                Global.usuario.setEstatura(Float.parseFloat(estatura.getText().toString()));
                Global.usuario.setPeso(Float.parseFloat(peso.getText().toString()));
                Global.usuario.setSexo(sex);
            }
            FirebaseFirestore.getInstance().document("Usuarios/".concat(Global.firebaseUsuario.getUid())).set(Global.usuario, SetOptions.merge()).addOnSuccessListener(unused -> {
                setResult(Global.CAMBIO_DATOS);
                cargando.dismiss();
                Toast.makeText(CambiarDatos.this, R.string.cambio_datos_exitoso, Toast.LENGTH_LONG).show();
                finish();
            }). addOnFailureListener(e -> {
                Toast.makeText(CambiarDatos.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
                cargando.dismiss();
            });

        }
    }

}