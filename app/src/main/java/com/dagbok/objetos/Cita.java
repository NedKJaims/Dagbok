package com.dagbok.objetos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class Cita implements Parcelable {

    private boolean activa;
    private String idDoctor;
    private String idPaciente;
    private String idReceta;
    private Timestamp fecha;
    private List<Timestamp> proximasFechas;
    private String enfermedad;
    private String descripcion;
    private String nombrePaciente;

    public Cita() {

    }

    protected Cita(Parcel in) {
        activa = in.readByte() != 0;
        idDoctor = in.readString();
        idPaciente = in.readString();
        idReceta = in.readString();
        fecha = in.readParcelable(Timestamp.class.getClassLoader());
        proximasFechas = in.createTypedArrayList(Timestamp.CREATOR);
        enfermedad = in.readString();
        descripcion = in.readString();
        nombrePaciente = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (activa ? 1 : 0));
        dest.writeString(idDoctor);
        dest.writeString(idPaciente);
        dest.writeString(idReceta);
        dest.writeParcelable(fecha, flags);
        dest.writeTypedList(proximasFechas);
        dest.writeString(enfermedad);
        dest.writeString(descripcion);
        dest.writeString(nombrePaciente);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cita> CREATOR = new Creator<Cita>() {
        @Override
        public Cita createFromParcel(Parcel in) {
            return new Cita(in);
        }

        @Override
        public Cita[] newArray(int size) {
            return new Cita[size];
        }
    };

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getIdDoctor() {
        return idDoctor;
    }

    public void setIdDoctor(String idDoctor) {
        this.idDoctor = idDoctor;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(String idReceta) {
        this.idReceta = idReceta;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public List<Timestamp> getProximasFechas() {
        return proximasFechas;
    }

    public void setProximasFechas(List<Timestamp> proximasFechas) {
        this.proximasFechas = proximasFechas;
    }

    public String getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(String enfermedad) {
        this.enfermedad = enfermedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }


}
