package com.dagbok;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dagbok.globals.Global;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuPrincipal extends AppCompatActivity {

    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Global.CAMBIO_DATOS) {
                    establecerDatosMenu();
                }
            });

        if(Global.usuario == null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MenuPrincipal.this);
            dialog.setTitle(R.string.configuracion_usuario);
            dialog.setMessage(R.string.no_se_guardo_usuario_correctamente);
            dialog.setPositiveButton(R.string.aceptar, null);
            dialog.show();
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
                    .into((CircleImageView)findViewById(R.id.menuPrincipal_foto_image));
        } else {
            ((CircleImageView)findViewById(R.id.menuPrincipal_foto_image)).setImageResource(R.drawable.ic_no_disponible);
        }
    }

    public void cerrarSesion(View v) {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    public void configuracion(View v) {
        Intent config = new Intent(MenuPrincipal.this, Configuracion.class);
        resultLauncher.launch(config);
    }

}