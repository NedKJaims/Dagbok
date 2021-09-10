package com.dagbok;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.dagbok.globals.Global;
import com.dagbok.objetos.Doctor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Configuracion extends AppCompatActivity {

    private ActivityResultLauncher<Intent> resultLauncher;
    private OnFailureListener falloConexion;

    private AlertDialog cargando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        activarBotonesNecesarios();

        falloConexion = e -> {
            Toast.makeText(Configuracion.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
            cargando.dismiss();
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Configuracion.this);
        builder.setCancelable(false);
        builder.setView(R.layout.popup_cargando_datos);
        cargando = builder.create();

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                this::resultadoActividad);

    }

    private void resultadoActividad(ActivityResult result) {
        if(result.getResultCode() == Global.CAMBIO_DATOS) {
            setResult(Global.CAMBIO_DATOS);
            activarBotonesNecesarios();
        } else if(result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            if(result.getData().getData() != null) {
                InputStream stream;
                try {
                    stream = getContentResolver().openInputStream(result.getData().getData());
                    Bitmap foto = BitmapFactory.decodeStream(stream);
                    ByteArrayOutputStream streamByte = new ByteArrayOutputStream();
                    foto.compress(Bitmap.CompressFormat.PNG, 100, streamByte);
                    byte[] byteArray = streamByte.toByteArray();
                    foto.recycle();
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    subirFoto(byteArray);
                }  catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (result.getData().getExtras().get("data") != null) {
                Bitmap foto = (Bitmap) result.getData().getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                foto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                foto.recycle();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                subirFoto(byteArray);
            }
        }
    }

    private void activarBotonesNecesarios() {
        if(Global.usuario == null) {
            findViewById(R.id.configuracion_soyDoctor_button).setEnabled(false);
            findViewById(R.id.configuracion_cambioDatosDoc_button).setEnabled(false);
            findViewById(R.id.configuracion_cambioFoto_button).setEnabled(false);
        } else {
            if(Global.usuario.isEsDoctor()) {
                findViewById(R.id.configuracion_soyDoctor_button).setEnabled(false);
                findViewById(R.id.configuracion_cambioDatosDoc_button).setEnabled(true);
            } else {
                findViewById(R.id.configuracion_cambioDatosDoc_button).setEnabled(false);
            }
            findViewById(R.id.configuracion_cambioFoto_button).setEnabled(true);
        }
    }

    private void subirFoto(byte[] data) {
        cargando.show();
        String id = "Usuarios/".concat(Global.firebaseUsuario.getUid());
        FirebaseStorage.getInstance().getReference().child(id).putBytes(data).addOnSuccessListener(taskSnapshot ->
                Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl().addOnSuccessListener(uri ->
                        FirebaseFirestore.getInstance().document(id).update("urlFoto", uri.toString()).addOnSuccessListener(unused -> {
                            Global.usuario.setUrlFoto(uri.toString());
                            setResult(Global.CAMBIO_DATOS);
                            Toast.makeText(Configuracion.this, R.string.cambio_datos_exitoso, Toast.LENGTH_LONG).show();
                            cargando.dismiss();
                        }).addOnFailureListener(falloConexion))
            .addOnFailureListener(falloConexion)
        ).addOnFailureListener(falloConexion);
    }

    private void eliminarFoto() {
        cargando.show();
        String id = "Usuarios/".concat(Global.firebaseUsuario.getUid());
        FirebaseFirestore.getInstance().document(id).update("urlFoto", "").addOnSuccessListener(unused -> {
            Global.usuario.setUrlFoto("");
            setResult(Global.CAMBIO_DATOS);
            FirebaseStorage.getInstance().getReference().child(id).delete().addOnSuccessListener(unused1 -> {
                Toast.makeText(Configuracion.this, R.string.cambio_datos_exitoso, Toast.LENGTH_LONG).show();
                cargando.dismiss();
            }).addOnFailureListener(falloConexion);
        }).addOnFailureListener(falloConexion);
    }

    private void mostrarErrorKeyInvalida() {
        cargando.dismiss();
        Toast mensaje = Toast.makeText(Configuracion.this, R.string.key_invalida, Toast.LENGTH_LONG);
        mensaje.setGravity(Gravity.BOTTOM, 0, 0);
        mensaje.show();

    }

    //evento de botones de interfaz
    public void atras(View v) {
        onBackPressed();
    }

    public void cambiarDatos(View v) {
        Intent cambiarDatos = new Intent(Configuracion.this, CambiarDatos.class);
        resultLauncher.launch(cambiarDatos);
    }

    @SuppressLint("NonConstantResourceId")
    public void cambiarFoto(View v) {
        PopupMenu menu = new PopupMenu(Configuracion.this, v);
        menu.getMenuInflater().inflate(R.menu.menu_subir_foto, menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_subirfoto_tomar:
                    Intent tomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    resultLauncher.launch(tomarFoto);
                    return true;
                case R.id.menu_subirfoto_seleccionar:
                    Intent seleccionarFoto = new Intent();
                    seleccionarFoto.setType("image/*");
                    seleccionarFoto.setAction(Intent.ACTION_GET_CONTENT);
                    resultLauncher.launch(Intent.createChooser(seleccionarFoto, getString(R.string.seleccionar)));
                    return true;
                case R.id.menu_subirfoto_eliminar:
                    eliminarFoto();
                    return true;
                default:
                    return false;
            }
        });
        menu.show();
    }

    public void cambiarContrasena(View v) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(Objects.requireNonNull(Global.firebaseUsuario.getEmail())).addOnSuccessListener(unused ->
                Toast.makeText(Configuracion.this, R.string.se_mando_correo_contraseña, Toast.LENGTH_LONG).show())
                .addOnFailureListener(falloConexion);
    }

    public void soyDoctor(View v) {
        if(Global.usuario == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(Configuracion.this);
        builder.setTitle(R.string.ingrese_key);
        final EditText key = new EditText(Configuracion.this);
        key.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(key);
        builder.setPositiveButton(R.string.aceptar, (dialogInterface, i) -> {
            cargando.show();
            String texto = key.getText().toString();
            if(texto.isEmpty()) {
                key.setError(getString(R.string.llenar_campo_vacio));
            } else {
                FirebaseFirestore.getInstance().document("Consultorio/TODO").get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String keyDoc = documentSnapshot.get("soyDoctor", String.class);
                        if(keyDoc != null) {
                            if (keyDoc.matches(texto)) {
                                Doctor doctor = new Doctor(Global.usuario);
                                FirebaseFirestore.getInstance().document("Usuarios/".concat(Global.firebaseUsuario.getUid())).set(doctor, SetOptions.merge()).addOnSuccessListener(unused -> {
                                    Global.usuario = doctor;
                                    cargando.dismiss();
                                    Toast.makeText(Configuracion.this, R.string.cambio_datos_exitoso, Toast.LENGTH_LONG).show();
                                    setResult(Global.CAMBIO_DATOS);
                                    activarBotonesNecesarios();
                                }).addOnFailureListener(falloConexion);

                            } else {
                                mostrarErrorKeyInvalida();
                            }
                        } else {
                            mostrarErrorKeyInvalida();
                        }
                    } else {
                        mostrarErrorKeyInvalida();
                    }
                }).addOnFailureListener(falloConexion);
            }

        });
        builder.setNegativeButton(R.string.cancelar, null);
        builder.show();
    }

    public void cambioDatosDoctor(View v) {
        Intent cambioDatosDoctor = new Intent(Configuracion.this, CambiarDatosDoctor.class);
        resultLauncher.launch(cambioDatosDoctor);
    }

    public void eliminarCuenta(View v) {
        if(Global.sesionReciente) {
            cargando.show();
            Global.firebaseUsuario.delete().addOnSuccessListener(unused -> {
                setResult(Global.ELIMINAR_CUENTA);
                String id = "Usuarios/".concat(Global.firebaseUsuario.getUid());
                FirebaseStorage.getInstance().getReference().child(id).delete().addOnSuccessListener(unused1 -> {
                    cargando.dismiss();
                    Toast.makeText(Configuracion.this, R.string.eliminar_cuenta_exitosa, Toast.LENGTH_LONG).show();
                    finish();
                }).addOnFailureListener(e -> {
                    cargando.dismiss();
                    Toast.makeText(Configuracion.this, R.string.eliminar_cuenta_exitosa, Toast.LENGTH_LONG).show();
                    finish();
                });
            }).addOnFailureListener(falloConexion);
        } else {
            Toast.makeText(Configuracion.this, R.string.inicie_sesion_borrar_cuenta, Toast.LENGTH_LONG).show();
        }
    }

}