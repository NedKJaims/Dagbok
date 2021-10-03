package com.dagbok.objetos;

import com.google.firebase.Timestamp;

public class Usuario {

    private boolean esDoctor;
    private String nombre;
    private String apellidos;
    private String correo;
    private String urlFoto;
    private Timestamp fechaNacimiento;
    private float estatura; //MTS
    private float peso; //KG
    private int sexo; //0: hombre 1: mujer

    public Usuario() {

    }

    public boolean isEsDoctor() {
        return esDoctor;
    }

    public void setEsDoctor(boolean esDoctor) {
        this.esDoctor = esDoctor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Timestamp getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Timestamp fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public float getEstatura() {
        return estatura;
    }

    public void setEstatura(float estatura) {
        this.estatura = estatura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        this.sexo = sexo;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }


}
