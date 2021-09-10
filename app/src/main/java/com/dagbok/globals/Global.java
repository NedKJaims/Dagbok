package com.dagbok.globals;

import android.app.Activity;
import android.widget.Toast;

import com.dagbok.R;
import com.dagbok.objetos.Usuario;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public class Global {

    public static boolean sesionReciente;
    public static Usuario usuario;
    public static FirebaseUser firebaseUsuario;

    public static final int CONSULTORIO_CERRADO = 0;
    public static final int CONSULTORIO_DISPONIBLE = 1;
    public static final int CONSULTORIO_OCUPADO = 2;

    public static final int CAMBIO_DATOS = 100;
    public static final int ELIMINAR_CUENTA = 101;

    public static String crearFormatoHora(Activity activity, int hora, int minutos) {
        String result;
        SimpleDateFormat f = new SimpleDateFormat("HH:mm", activity.getResources().getConfiguration().locale);
        SimpleDateFormat newf = new SimpleDateFormat("hh:mm a", activity.getResources().getConfiguration().locale);
        try {
            String data = hora + ":" + minutos;
            result = newf.format(Objects.requireNonNull(f.parse(data)));
        } catch (ParseException e) {
            Toast.makeText(activity, R.string.hubo_algo_raro_horas, Toast.LENGTH_LONG).show();
            result = null;
        }
        return result;
    }

}
