package com.dagbok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dagbok.componentes.ComponenteCita;
import com.dagbok.globals.Global;
import com.dagbok.objetos.Cita;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class PacienteListaCitas extends AppCompatActivity {

    private LinearLayout padre;
    private DocumentSnapshot ultimaCitaCapturada;

    private final View.OnClickListener cargarMas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            padre.removeView(view);
            cargarCitas();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_lista_citas);
        padre = findViewById(R.id.pacienteLitaCitas_citas_lista);

        ultimaCitaCapturada = null;
        cargarCitas();
    }

    @NonNull
    private LinearLayout.LayoutParams crearParametros() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margen = (int)(10 * getResources().getDisplayMetrics().density);
        params.setMargins(margen,margen,margen,margen);
        return params;
    }

    @NonNull
    private TextView crearTexto(CharSequence texto, int colorTexto, boolean bold) {
        LinearLayout.LayoutParams params = crearParametros();
        TextView res = new TextView(PacienteListaCitas.this);
        res.setText(texto);
        if(bold)
            res.setTypeface(ResourcesCompat.getFont(PacienteListaCitas.this, R.font.coolvetica), Typeface.BOLD);
        else
            res.setTypeface(ResourcesCompat.getFont(PacienteListaCitas.this, R.font.coolvetica));
        if(colorTexto != -1)
            res.setTextColor(getColor(colorTexto));
        res.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        res.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        res.setLayoutParams(params);
        return res;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    private CardView crearBotonVerMas() {
        LinearLayout.LayoutParams params = crearParametros();

        TextView res = crearTexto(getText(R.string.ver_mas),R.color.white, false);

        CardView view = new CardView(PacienteListaCitas.this);
        view.setCardBackgroundColor(ColorStateList.valueOf(getColor(R.color.verde_primario)));
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        view.setForeground(getDrawable(outValue.resourceId));
        view.addView(res, params);
        view.setRadius(16 * getResources().getDisplayMetrics().density);
        view.setLayoutParams(params);
        view.setOnClickListener(cargarMas);
        return view;
    }

    private void cargarCitas() {

        Query query = FirebaseFirestore.getInstance().collection("Citas")
                .whereEqualTo("idPaciente", Global.firebaseUsuario.getUid())
                        .orderBy("fecha", Query.Direction.DESCENDING).limit(10);
        if(ultimaCitaCapturada != null) {
            query = query.startAfter(ultimaCitaCapturada);
        } else {
            padre.removeAllViews();
        }
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                for(int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    Cita citaLocal = queryDocumentSnapshots.getDocuments().get(i).toObject(Cita.class);

                    byte tipoTarjeta = ComponenteCita.TARJETA_PACIENTE;
                    ComponenteCita componenteCita = new ComponenteCita(PacienteListaCitas.this, Objects.requireNonNull(citaLocal),tipoTarjeta);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int margen = (int)(10 * getResources().getDisplayMetrics().density);
                    params.setMargins(margen,margen,margen,margen);

                    componenteCita.setOnClickListener(view -> {
                        Intent informacionCita = new Intent(PacienteListaCitas.this, PacienteInformacionCita.class);
                        informacionCita.putExtra("cita", citaLocal);
                        startActivity(informacionCita);
                    });

                    padre.addView(componenteCita, params);
                }
                ultimaCitaCapturada = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                if(queryDocumentSnapshots.size() == 10)
                    padre.addView(crearBotonVerMas());
            } else {
                padre.addView(crearTexto(getText(R.string.no_hay_citas), -1, true));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(PacienteListaCitas.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
            padre.addView(crearTexto(getText(R.string.no_hay_citas), -1, true));
        });

    }

    public void atras(View v) {
        onBackPressed();
    }


}