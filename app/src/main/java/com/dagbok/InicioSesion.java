package com.dagbok;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Usuario;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class InicioSesion extends AppCompatActivity {

    private EditText correo;
    private EditText contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        inicioPrevio();
        correo = findViewById(R.id.iniciosesion_correo_text);
        contrasena = findViewById(R.id.iniciosesion_contraseña_text);

    }

    //Otras funciones
    private void inicioPrevio() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            if(user.isEmailVerified()) {
                Global.firebaseUsuario = user;
                cargarDatosUsuarioMenuPrincipal(user.getUid());
            }
        }
    }

    private void cargarDatosUsuarioMenuPrincipal(String id) {
        FirebaseFirestore.getInstance().document("Usuarios/".concat(id)).get().addOnSuccessListener(documentSnapshot -> {
            Usuario usuario = null;
            if(documentSnapshot.exists()) {
                usuario = documentSnapshot.toObject(Usuario.class);
            }
            Global.usuario = usuario;
            Intent menuPrincipal = new Intent(InicioSesion.this, MenuPrincipal.class);
            startActivity(menuPrincipal);
        }).addOnFailureListener(e -> Toast.makeText(InicioSesion.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show());
    }

    private boolean algunCampoVacio() {
        boolean correoVacio = correo.getText().toString().isEmpty();
        if(correoVacio)
            establecerErrorPadre(correo);
        else vaciarErrorPadre(correo);
        boolean contrasenaVacia = contrasena.getText().toString().isEmpty();
        if(contrasenaVacia)
            establecerErrorPadre(contrasena);
        else vaciarErrorPadre(contrasena);
        return correoVacio || contrasenaVacia;
    }

    private void establecerErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError(getString(R.string.llenar_campo_vacio));
    }
    private void vaciarErrorPadre(EditText editText) {
        ((TextInputLayout)editText.getParent().getParent()).setError("");
    }

    private void mostrarMensajeVerificacionCorreo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioSesion.this);
        builder.setTitle(R.string.verifique_su_correo);
        builder.setMessage(R.string.se_mando_correo_verificacion);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.aceptar, null);
        builder.show();
    }

    private void mostrarErrorDeInicioSesion(Exception e) {
        try {
            throw e;
        } catch (FirebaseAuthInvalidUserException e1) {
            Toast.makeText(InicioSesion.this, R.string.correo_incorrecto, Toast.LENGTH_LONG).show();
        } catch (FirebaseAuthInvalidCredentialsException e1) {
            Toast.makeText(InicioSesion.this, R.string.contraseña_incorrecta, Toast.LENGTH_LONG).show();
        } catch (Exception e1) {
            Toast.makeText(InicioSesion.this, R.string.corre_mal_formato, Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarErrorDeRecuperarContrasena(Exception e) {
        try {
            throw e;
        } catch (FirebaseAuthInvalidUserException e1) {
            Toast.makeText(InicioSesion.this, R.string.correo_incorrecto, Toast.LENGTH_LONG).show();
        } catch (Exception exception) {
            Toast.makeText(InicioSesion.this, R.string.corre_mal_formato, Toast.LENGTH_LONG).show();
        }
    }

    //Botones
    public void iniciarSesion(View v) {
        if(!algunCampoVacio()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser());
                        if(user.isEmailVerified()) {
                            Global.firebaseUsuario = user;
                            cargarDatosUsuarioMenuPrincipal(user.getUid());
                        } else {
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    mostrarMensajeVerificacionCorreo();
                                } else {
                                    Toast.makeText(InicioSesion.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        mostrarErrorDeInicioSesion(Objects.requireNonNull(task.getException()));
                    }
                });
        }
    }

    public void registrarse(View v) {
        Intent registro = new Intent(InicioSesion.this, Registro.class);
        startActivity(registro);
    }

    public void olvidoContrasena(View v) {
        boolean correoVacio = correo.getText().toString().isEmpty();
        if(correoVacio)
            establecerErrorPadre(correo);
        else {
            vaciarErrorPadre(correo);
            FirebaseAuth.getInstance().sendPasswordResetEmail(correo.getText().toString()).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(InicioSesion.this, R.string.se_mando_correo_contraseña, Toast.LENGTH_LONG).show();
                } else {
                    mostrarErrorDeRecuperarContrasena(Objects.requireNonNull(task.getException()));
                }
            });

        }
    }


}