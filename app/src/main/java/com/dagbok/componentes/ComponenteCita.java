package com.dagbok.componentes;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dagbok.R;
import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;

import java.util.Calendar;

public class ComponenteCita extends LinearLayout {


    public ComponenteCita(Context context) {
        super(context);
        inflate(context, R.layout.componente_cita, this);
    }

    public ComponenteCita(Context context, Cita cita) {
        super(context);
        inflate(context, R.layout.componente_cita, this);
        TextView fecha = findViewById(R.id.componenteCita_fecha_text);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cita.getFecha().toDate());
        fecha.setText(context.getString(R.string.fecha_colon).concat(" ").concat(Global.crearFormatoTiempo(context,calendar)));
        TextView enfemedad = findViewById(R.id.componenteCita_enfermedad_text);
        enfemedad.setText(context.getString(R.string.enfermedad_colon).concat(" ").concat(" Enfermedad"));
        TextView paciente = findViewById(R.id.componenteCita_paciente_text);
        paciente.setText(context.getString(R.string.paciente_colon).concat(" ").concat("Paciente"));

    }
}
