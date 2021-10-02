package com.dagbok;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.lang.reflect.Type;

public class PacienteListaCitas extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_lista_citas);




    }

    public void agregarPeticion(View v) {

    }

    public void atras(View v) {
        onBackPressed();
    }


}