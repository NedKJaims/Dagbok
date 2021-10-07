package com.dagbok.objetos;

import java.util.List;

public class Doctor extends Usuario {

    private List<String> especialidades;
    private List<String> horarios;

    public Doctor() {

    }

    public void establecerUsuario(Usuario usuario) {
        setEsDoctor(true);
        setNombre(usuario.getNombre());
        setApellidos(usuario.getApellidos());
        setEstatura(usuario.getEstatura());
        setPeso(usuario.getPeso());
        setFechaNacimiento(usuario.getFechaNacimiento());
        setUrlFoto(usuario.getUrlFoto());
        setCorreo(usuario.getCorreo());
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(List<String> especialidades) {
        this.especialidades = especialidades;
    }

    public List<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<String> horarios) {
        this.horarios = horarios;
    }
}
