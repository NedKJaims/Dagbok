package com.dagbok.globals;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dagbok.R;
import com.dagbok.objetos.Usuario;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static final int AGREGO_CITA = 102;

    public static String crearFormatoHora(Context activity, int hora, int minutos) {
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

    public static String crearFormatoTiempo(Context activity, @NonNull Calendar calendar) {
        return activity.getResources().getStringArray(R.array.dias)[calendar.get(Calendar.DAY_OF_WEEK) - 1] +
                ", " +
                calendar.get(Calendar.DAY_OF_MONTH) +
                " / " +
                activity.getResources().getStringArray(R.array.meses)[calendar.get(Calendar.MONTH)] +
                " / " +
                calendar.get(Calendar.YEAR);
    }

}
