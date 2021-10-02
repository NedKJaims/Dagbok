package com.dagbok.componentes;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dagbok.R;
import com.dagbok.objetos.Usuario;
import com.squareup.picasso.Picasso;

public class ComponenteTarjetaUsuario extends LinearLayout {

    public ComponenteTarjetaUsuario(Context context) {
        super(context);
    }

    public ComponenteTarjetaUsuario(Context context, @NonNull Usuario usuario) {
        super(context);
        inflate(context, R.layout.componente_tarjeta_usuario, this);

        TextView nombre = findViewById(R.id.componenteTarjetaUsuario_nombre);
        TextView correo = findViewById(R.id.componenteTarjetaUsuario_correoElectronico);
        ImageView imagen = findViewById(R.id.componenteTarjetaUsuario_foto);

        nombre.setText(usuario.getNombre().concat(" ").concat(usuario.getApellidos()));
        correo.setText(usuario.getCorreo());
        if(!usuario.getUrlFoto().isEmpty()) {
            Picasso.get().load(usuario.getUrlFoto()).
                    placeholder(R.drawable.ic_no_disponible).resize(1080, 1080).onlyScaleDown()
                    .into(imagen);
        }

    }

}
