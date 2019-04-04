/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpmanager.modelo.entidades;

/**
 *
 * @author Dell
 */
public class AgenteSNMP {
    int id;
    String nombre;
    String version;
    String SO;
    String noInterfacesDeRed;
    String ultimoReinicio;
    String ubicacion;
    String infoContacto;
    String ip;
    String puerto;
    String comunidad; 

    public AgenteSNMP(String nombre, String version, String puerto, String comunidad) {
        this.nombre = nombre;
        this.version = version;
        this.puerto = puerto;
        this.comunidad = comunidad;
    }

    public AgenteSNMP(String nombre, String version, String SO, String noInterfacesDeRed, String ultimoReinicio, String ubicacion, String infoContacto, String ip, String puerto) {
        this.nombre = nombre;
        this.version = version;
        this.SO = SO;
        this.noInterfacesDeRed = noInterfacesDeRed;
        this.ultimoReinicio = ultimoReinicio;
        this.ubicacion = ubicacion;
        this.infoContacto = infoContacto;
        this.ip = ip;
        this.puerto = puerto;
    }    

    public AgenteSNMP() {
    }

    public String getNombre() {
        return nombre;
    }

    public String getVersion() {
        return version;
    }

    public String getSO() {
        return SO;
    }

    public String getNoInterfacesDeRed() {
        return noInterfacesDeRed;
    }

    public String getUltimoReinicio() {
        return ultimoReinicio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getInfoContacto() {
        return infoContacto;
    }

    public String getIp() {
        return ip;
    }

    public String getPuerto() {
        return puerto;
    }

    public String getComunidad() {
        return comunidad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setSO(String SO) {
        this.SO = SO;
    }

    public void setNoInterfacesDeRed(String noInterfacesDeRed) {
        this.noInterfacesDeRed = noInterfacesDeRed;
    }

    public void setUltimoReinicio(String ultimoReinicio) {
        this.ultimoReinicio = ultimoReinicio;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setInfoContacto(String infoContacto) {
        this.infoContacto = infoContacto;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public void setComunidad(String comunidad) {
        this.comunidad = comunidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
}
