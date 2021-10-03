package com.dagbok;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Objects;

public class DoctorListaCitas extends AppCompatActivity {

    private static final byte NUEVAS_ACTIVAS = 0;
    private static final byte VIEJAS_NO_ACTIVAS = 1;

    private ActivityResultLauncher<Intent> resultLauncher;

    private byte sentidoCarga;
    private DocumentSnapshot ultimaCitaCapturada;

    private LinearLayout citasPadre;

    private final View.OnClickListener cargarMas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            citasPadre.removeView(view);
            cargarListaCitas();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_lista_citas);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == Global.AGREGO_CITA) {
                Intent informacionCita = new Intent(DoctorListaCitas.this, DoctorInformacionCita.class);
                informacionCita.putExtra("cita", Objects.requireNonNull(result.getData()).getSerializableExtra("cita"));
                startActivity(informacionCita);
                ultimaCitaCapturada = null;
                cargarListaCitas();
            }
        });

        sentidoCarga = NUEVAS_ACTIVAS;
        ultimaCitaCapturada = null;

        citasPadre = findViewById(R.id.doctor_lista_citas_citas_padre);
        cargarListaCitas();
    }

    @NonNull
    private LinearLayout.LayoutParams crearParametros() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margen = (int)(10 * getResources().getDisplayMetrics().density);
        params.setMargins(margen,margen,margen,margen);
        return params;
    }
    @NonNull
    private TextView crearTexto(CharSequence texto, int colorTexto, int alignment) {
        LinearLayout.LayoutParams params = crearParametros();
        TextView res = new TextView(DoctorListaCitas.this);
        res.setText(texto);
        res.setTypeface(ResourcesCompat.getFont(DoctorListaCitas.this, R.font.coolvetica));
        if(colorTexto != -1)
            res.setTextColor(getColor(colorTexto));
        if(alignment != -1)
            res.setTextAlignment(alignment);
        res.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        res.setLayoutParams(params);
        return res;
    }
    @NonNull
    private View crearSeparador() {
        View sep = new View(DoctorListaCitas.this);
        sep.setBackgroundColor(getColor(R.color.gray));
        LinearLayout.LayoutParams params = crearParametros();
        params.height = (int)(2 * getResources().getDisplayMetrics().density);
        sep.setLayoutParams(params);
        return sep;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    public CardView crearBotonVerMas() {
        LinearLayout.LayoutParams params = crearParametros();

        TextView res = crearTexto(getText(R.string.ver_mas),R.color.white,TextView.TEXT_ALIGNMENT_CENTER);

        CardView view = new CardView(DoctorListaCitas.this);
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

    private void cargarListaCitas() {
        Calendar calendar = Calendar.getInstance();
        //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0);
        calendar.set(2021,8,16,0,0);
        Timestamp fechaPrueba = new Timestamp(calendar.getTime());

        Query ref = FirebaseFirestore.getInstance().collection("Citas")
                .whereEqualTo("idDoctor", Global.firebaseUsuario.getUid());
        if(sentidoCarga == NUEVAS_ACTIVAS) {
            ref = ref.whereEqualTo("activa", true).whereGreaterThanOrEqualTo("fecha", fechaPrueba)
                    .limit(10).orderBy("fecha");
        } else if(sentidoCarga == VIEJAS_NO_ACTIVAS) {
            ref = ref.whereLessThanOrEqualTo("fecha", fechaPrueba)
                    .limit(10).orderBy("fecha", Query.Direction.DESCENDING);
        }

        if(ultimaCitaCapturada == null) {
            citasPadre.removeAllViews();
        } else {
            ref = ref.startAfter(ultimaCitaCapturada);
        }

        ref.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if(!queryDocumentSnapshots.isEmpty()) {
                if(ultimaCitaCapturada == null){
                    CharSequence textoDireccion = (sentidoCarga == NUEVAS_ACTIVAS) ? getText(R.string.nuevas_activas) : getText(R.string.viejas_no_activas);
                    citasPadre.addView(crearTexto(textoDireccion, R.color.black, -1));
                    citasPadre.addView(crearSeparador());
                }
                for(int i = 0; i < queryDocumentSnapshots.size(); i++) {
                    Cita citaLocal = queryDocumentSnapshots.getDocuments().get(i).toObject(Cita.class);

                    ComponenteCita componenteCita = new ComponenteCita(DoctorListaCitas.this, Objects.requireNonNull(citaLocal));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int margen = (int)(10 * getResources().getDisplayMetrics().density);
                    params.setMargins(margen,margen,margen,margen);

                    componenteCita.setOnClickListener(view -> {
                        Intent informacionCita = new Intent(DoctorListaCitas.this, DoctorInformacionCita.class);
                        informacionCita.putExtra("cita", citaLocal);
                        startActivity(informacionCita);
                    });

                    citasPadre.addView(componenteCita, params);
                }
                ultimaCitaCapturada = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                if(queryDocumentSnapshots.size() == 10)
                    citasPadre.addView(crearBotonVerMas());
            } else {
                citasPadre.addView(crearTexto(getText(R.string.no_hay_citas), -1, TextView.TEXT_ALIGNMENT_CENTER));
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DoctorListaCitas.this, R.string.no_hay_conexion, Toast.LENGTH_LONG).show();
            citasPadre.addView(crearTexto(getText(R.string.no_hay_citas), -1, TextView.TEXT_ALIGNMENT_CENTER));
        });


    }

    public void cambioLecturaCitas(View v) {
        sentidoCarga = (sentidoCarga == NUEVAS_ACTIVAS) ? VIEJAS_NO_ACTIVAS : NUEVAS_ACTIVAS;
        ultimaCitaCapturada = null;
        cargarListaCitas();
    }

    public void atras(View v) {
        onBackPressed();
    }

    public void agregarCita(View v) {
        Intent agregarCita = new Intent(DoctorListaCitas.this, AgregarCita.class);
        resultLauncher.launch(agregarCita);
    }


}