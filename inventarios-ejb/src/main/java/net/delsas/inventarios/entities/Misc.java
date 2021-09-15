/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author delsas
 */
@Entity
@Table(name = "Misc")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Misc.findAll", query = "SELECT m FROM Misc m"),
    @NamedQuery(name = "Misc.findByIdMisc", query = "SELECT m FROM Misc m WHERE m.idMisc = :idMisc"),
    @NamedQuery(name = "Misc.findByNombre", query = "SELECT m FROM Misc m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "Misc.findByDireccion", query = "SELECT m FROM Misc m WHERE m.direccion = :direccion"),
    @NamedQuery(name = "Misc.findByTelefonos", query = "SELECT m FROM Misc m WHERE m.telefonos = :telefonos"),
    @NamedQuery(name = "Misc.findByMatriz", query = "SELECT m FROM Misc m WHERE m.matriz = :matriz")
})
public class Misc implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    private Integer idMisc;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    private String nombre;
    @Size(max = 255)
    private String direccion;
    @Lob
    private byte[] logo;
    @Size(max = 88)
    private String telefonos;
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarioList;
    @JoinColumn(name = "propietario", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false)
    private Usuario propietario;
    @JoinColumn(name = "matriz", referencedColumnName = "idMisc")
    @ManyToOne
    private Misc matriz;
    @OneToMany(mappedBy = "matriz")
    private List<Misc> sucursales;

    public Misc() {
    }

    public Misc(Integer idMisc) {
        this.idMisc = idMisc;
    }

    public Misc(Integer idMisc, String nombre) {
        this.idMisc = idMisc;
        this.nombre = nombre;
    }

    public Integer getIdMisc() {
        return idMisc;
    }

    public void setIdMisc(Integer idMisc) {
        this.idMisc = idMisc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    @XmlTransient
    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idMisc != null ? idMisc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Misc)) {
            return false;
        }
        Misc other = (Misc) object;
        if ((this.idMisc == null && other.idMisc != null) || (this.idMisc != null && !this.idMisc.equals(other.idMisc))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.Misc[ idMisc=" + idMisc + " ]";
    }

    public Misc getMatriz() {
        return matriz;
    }

    public void setMatriz(Misc matriz) {
        this.matriz = matriz;
    }

    public List<Misc> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Misc> sucursales) {
        this.sucursales = sucursales;
    }

}
