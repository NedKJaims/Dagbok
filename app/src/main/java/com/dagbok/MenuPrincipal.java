package com.dagbok;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dagbok.globals.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class MenuPrincipal extends AppCompatActivity {

    private ActivityResultLauncher<Intent> resultLauncher;
    private AlertDialog usuarioNoConfigurado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Global.CAMBIO_DATOS) {
                    establecerDatosMenu();
                } else if (result.getResultCode() == Global.ELIMINAR_CUENTA) {
                    Global.sesionReciente = false;
                    Global.usuario = null;
                    Global.firebaseUsuario = null;
                    Intent inicioSesion = new Intent(MenuPrincipal.this, InicioSesion.class);
                    finish();
                    startActivity(inicioSesion);
                }
            });

        AlertDialog.Builder dialog = new AlertDialog.Builder(MenuPrincipal.this);
        dialog.setTitle(R.string.configuracion_usuario);
        dialog.setMessage(R.string.no_se_guardo_usuario_correctamente);
        dialog.setPositiveButton(R.string.aceptar, null);
        usuarioNoConfigurado = dialog.create();

        if(Global.usuario == null) {
            usuarioNoConfigurado.show();
        } else {
            establecerDatosMenu();
        }

    }

    private void establecerDatosMenu() {
        ((TextView)findViewById(R.id.menuPrincipal_nombre_text))
                .setText(Global.usuario.getNombre().concat(" ").concat(Global.usuario.getApellidos()));
        if(!Global.usuario.getUrlFoto().isEmpty()) {
            Picasso.get().load(Global.usuario.getUrlFoto()).
                    placeholder(R.drawable.ic_no_disponible).resize(1080, 1080).onlyScaleDown()
                    .into((ImageView) findViewById(R.id.menuPrincipal_foto_image));
        } else {
            ((ImageView)findViewById(R.id.menuPrincipal_foto_image)).setImageResource(R.drawable.ic_no_disponible);
        }
    }

    public void cerrarSesion(View v) {
        FirebaseAuth.getInstance().signOut();
        Global.usuario = null;
        Global.firebaseUsuario = null;
        Intent inicioSesion = new Intent(MenuPrincipal.this, InicioSesion.class);
        finish();
        startActivity(inicioSesion);
    }

    public void configuracion(View v) {
        Intent config = new Intent(MenuPrincipal.this, Configuracion.class);
        resultLauncher.launch(config);
    }

    public void estatusConsultorio(View v) {
        if(Global.usuario == null) {
            usuarioNoConfigurado.show();
        } else {
            boolean isDoctor = Global.usuario.isEsDoctor();
            Intent estatus = new Intent(MenuPrincipal.this,
                    (isDoctor) ? DoctorEstatusConsultorio.class : PacienteEstatusConsultorio.class);
            startActivity(estatus);
        }
    }

    public void citas(View v) {
        if(Global.usuario == null) {
            usuarioNoConfigurado.show();
        } else {
            Intent citas = new Intent(MenuPrincipal.this, DoctorListaCitas.class);
            startActivity(citas);
        }
    }

}