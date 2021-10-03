package com.dagbok.componentes;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dagbok.R;
import com.dagbok.objetos.Doctor;
import com.dagbok.objetos.Usuario;
import com.squareup.picasso.Picasso;

public class ComponenteTarjetaUsuario extends LinearLayout {

    public static final int TARJETA_SENCILLA_USUARIO = 0;
    public static final int TARJETA_ESTATUS_CONSULTORIO_DOCTOR = 1;

    public ComponenteTarjetaUsuario(Context context) {
        super(context);
    }

    public ComponenteTarjetaUsuario(Context context, int tipoTarjeta, @NonNull Usuario usuario) {
        super(context);
        switch (tipoTarjeta) {
            case TARJETA_SENCILLA_USUARIO:
                iniciarSencillaUsuario(context, usuario);
                break;
            case TARJETA_ESTATUS_CONSULTORIO_DOCTOR:
                iniciarEstatusConsultorioDoctor(context, (Doctor) usuario);
                break;
        }

    }

    private void iniciarSencillaUsuario(Context context, @NonNull Usuario usuario) {
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

    private void iniciarEstatusConsultorioDoctor(Context context, @NonNull Doctor doctor) {
        inflate(context, R.layout.componente_tarjeta_doctor_estatus_consultorio, this);

        ImageView fotoDoctor = findViewById(R.id.componenteDoctor_estatus_foto);
        TextView nombreDoctor = findViewById(R.id.componenteDoctor_estatus_nombre_text);
        TextView especialidadesDoctor = findViewById(R.id.componenteDoctor_estatus_especialidades_text);
        TextView horariosDoctor = findViewById(R.id.componenteDoctor_estatus_horarios_text);

        if(!doctor.getUrlFoto().isEmpty())
            Picasso.get().load(doctor.getUrlFoto())
                    .placeholder(R.drawable.ic_no_disponible)
                    .resize(1080, 1080)
                    .onlyScaleDown()
                    .into(fotoDoctor);
        nombreDoctor.setText(doctor.getNombre().concat(" ").concat(doctor.getApellidos()));
        StringBuilder especialidades = new StringBuilder();
        for(int i = 0; i < doctor.getEspecialidades().size(); i++) {
            especialidades.append(doctor.getEspecialidades().get(i));
            if(i < doctor.getEspecialidades().size() - 1)
                especialidades.append("\n");
        }
        especialidadesDoctor.setText(especialidades);
        StringBuilder horarios = new StringBuilder();
        for(int i = 0; i < doctor.getHorarios().size(); i++) {
            horarios.append(doctor.getHorarios().get(i));
            if(i < doctor.getHorarios().size() - 1)
                horarios.append("\n");
        }
        horariosDoctor.setText(horarios);

    }

}
