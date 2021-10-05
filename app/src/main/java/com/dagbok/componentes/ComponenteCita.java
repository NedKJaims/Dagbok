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
        Global.establecerFormatoColonTextView(fecha, context.getString(R.string.fecha_colon), Global.crearFormatoTiempo(context,calendar));

        TextView enfemedad = findViewById(R.id.componenteCita_enfermedad_text);
        Global.establecerFormatoColonTextView(enfemedad, context.getString(R.string.enfermedad_colon), cita.getEnfermedad());

        TextView estatus = findViewById(R.id.componenteCita_estatus_text);
        Global.establecerFormatoColonTextView(estatus, context.getString(R.string.estatus), (cita.isActiva()) ? context.getString(R.string.tratamiento)
                :  context.getString(R.string.finalizado));

        TextView paciente = findViewById(R.id.componenteCita_paciente_text);
        Global.establecerFormatoColonTextView(paciente, context.getString(R.string.paciente_colon), cita.getNombrePaciente());

    }

}
