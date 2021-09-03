package com.dagbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dagbok.objetos.Usuario;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class Registro extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;
    private EditText correoElectronico;
    private EditText contrasena;
    private EditText contrasenaConfirmada;
    private EditText estatura;
    private EditText peso;
    private EditText fechaNacimiento;
    private RadioGroup sexo;

    private Timestamp fecha;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombre = findViewById(R.id.registro_nombre_text);
        apellidos = findViewById(R.id.registro_apellidos_text);
        correoElectronico = findViewById(R.id.registro_correo_text);
        contrasena = findViewById(R.id.registro_contraseña_text);
        contrasenaConfirmada = findViewById(R.id.registro_confirmar_contraseña_text);
        estatura = findViewById(R.id.registro_estatura_number);
        peso = findViewById(R.id.registro_peso_number);
        fechaNacimiento = findViewById(R.id.registro_fecha_date);
        sexo = findViewById(R.id.registro_sexo_radiogroup);

        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setCancelable(false);
        builder.setView(R.layout.popup_cargando_datos);
        cargando = builder.create();

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
        boolean correoElectronicoVacio = correoElectronico.getText().toString().isEmpty();
        if(correoElectronicoVacio)
            establecerErrorPadre(correoElectronico);
        else vaciarErrorPadre(correoElectronico);
        boolean contrasenaVacio = contrasena.getText().toString().isEmpty();
        if(contrasenaVacio)
            establecerErrorPadre(contrasena);
        else vaciarErrorPadre(contrasena);
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

        boolean contrasenaConfirmadaVacia = !contrasenaConfirmada.getText().toString().matches(contrasena.getText().toString())
                || contrasenaConfirmada.getText().toString().isEmpty();
        if(contrasenaConfirmadaVacia) {
            ((TextInputLayout)contrasenaConfirmada.getParent().getParent()).setError(getString(R.string.contrasena_no_coincide));
        } else vaciarErrorPadre(contrasenaConfirmada);

        return (nombreVacio || apellidosVacio || correoElectronicoVacio || contrasenaVacio
                || estaturaVacio || pesoVacio || fechaNacimientoVacio) || contrasenaConfirmadaVacia;
    }

    private void establecerErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError(getString(R.string.llenar_campo_vacio));
    }
    private void vaciarErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError("");
    }
    private void crearMensajeRegistroExitoso() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
        builder.setTitle(R.string.registro_exitoso);
        builder.setMessage(R.string.se_le_mandara_correo_verificacion);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.aceptar, (dialogInterface, i) -> finish());
        builder.show();
    }

    private void mostrarErrorAutenticacion(@NonNull Exception e) {
        try {
            throw e;
        } catch (FirebaseAuthInvalidCredentialsException e1) {
            Toast.makeText(Registro.this, R.string.credenciales_invalidas, Toast.LENGTH_SHORT).show();
        } catch (FirebaseAuthUserCollisionException e1) {
            Toast.makeText(Registro.this, R.string.correo_usado, Toast.LENGTH_SHORT).show();
            Toast.makeText(Registro.this, R.string.error_conexion_correo_creado, Toast.LENGTH_LONG).show();
        } catch (Exception e1) {
            Toast.makeText(Registro.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
        }
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
        DatePickerDialog dialog = new DatePickerDialog(Registro.this, (datePicker, ano1, mes1, dia1) -> {
            calendar.set(ano1, mes1, dia1, 0,0);
            fecha = new Timestamp(calendar.getTime());
            StringBuilder diaBuilder = new StringBuilder();

            fechaNacimiento.setText(diaBuilder.append(dia1).append("/").
                    append(getResources().getStringArray(R.array.meses)[mes1])
                    .append("/").append(ano1).toString());
        },ano, mes, dia);
        dialog.show();
    }

    public void registrar(View v) {
        if (!algunCampoVacio()) {
            cargando.show();
            int sexoTipo = (sexo.getCheckedRadioButtonId() == R.id.hombre) ? 0 : 1;
            Usuario usuario = new Usuario(
                    nombre.getText().toString(), apellidos.getText().toString(),
                    fecha, Float.parseFloat(estatura.getText().toString()),
                    Float.parseFloat(peso.getText().toString()), sexoTipo
            );

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correoElectronico.getText().toString(), contrasena.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String id = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    FirebaseFirestore.getInstance().collection("Usuarios").document(id).set(usuario).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            cargando.dismiss();
                            crearMensajeRegistroExitoso();
                        } else {
                            cargando.dismiss();
                        }
                    });
                } else {
                    cargando.dismiss();
                    mostrarErrorAutenticacion(Objects.requireNonNull(task.getException()));
                }
            });
        }
    }

}