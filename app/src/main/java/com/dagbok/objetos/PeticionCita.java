package com.dagbok.objetos;

public class PeticionCita {

    private boolean activa;
    private String idPaciente;
    private String descripcion;

    public PeticionCita() {

    }

    public PeticionCita(String idPaciente, String descripcion) {
        this.activa = false;
        this.idPaciente = idPaciente;
        this.descripcion = descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
