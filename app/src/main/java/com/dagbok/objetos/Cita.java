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
    /*private String enfermedad;
    private String descripcion;
    private String nombrePaciente;*/

    public Cita() {

    }

    public Cita(String idDoctor, String idPaciente, Timestamp fecha, List<Timestamp> proximasFechas) {
        this.idDoctor = idDoctor;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.activa = true;
        this.idReceta = "";
        this.proximasFechas = proximasFechas;
    }
    /*
    public Cita(String idDoctor, String idPaciente, String enfermedad, String descripcion, String nombrePaciente, Timestamp fecha, List<Timestamp> proximasFechas) {
        this.idDoctor = idDoctor;
        this.idPaciente = idPaciente;
        this.fecha = fecha;
        this.activa = true;
        this.idReceta = "";
        this.proximasFechas = proximasFechas;
        this.enfermedad = enfermedad;
        this.descripcion = descripcion;
        this.nombrePaciente = nombrePaciente;
    }
    */

    protected Cita(Parcel in) {
        activa = in.readByte() != 0;
        idDoctor = in.readString();
        idPaciente = in.readString();
        idReceta = in.readString();
        fecha = in.readParcelable(Timestamp.class.getClassLoader());
        proximasFechas = in.createTypedArrayList(Timestamp.CREATOR);
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
        activa = activa;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (activa ? 1 : 0));
        parcel.writeString(idDoctor);
        parcel.writeString(idPaciente);
        parcel.writeString(idReceta);
        parcel.writeParcelable(fecha, i);
        parcel.writeTypedList(proximasFechas);
    }
}
