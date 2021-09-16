/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "Usuario")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u"),
    @NamedQuery(name = "Usuario.findByIdUsuario", query = "SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario"),
    @NamedQuery(name = "Usuario.findByNombres", query = "SELECT u FROM Usuario u WHERE u.nombres = :nombres"),
    @NamedQuery(name = "Usuario.findByApellidos", query = "SELECT u FROM Usuario u WHERE u.apellidos = :apellidos"),
    @NamedQuery(name = "Usuario.findByPasswd", query = "SELECT u FROM Usuario u WHERE u.passwd = :passwd")})
public class Usuario implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    private String idUsuario;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    private String nombres;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    private String apellidos;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String passwd;
    @Basic(optional = false)
    @NotNull
    private boolean activo;
    @JoinColumn(name = "tipoUsuario", referencedColumnName = "idTipoUsuario")
    @ManyToOne(optional = false)
    private TipoUsuario tipoUsuario;
    @JoinColumn(name = "empresa", referencedColumnName = "idMisc")
    @ManyToOne
    private Misc empresa;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario1")
    private List<Ventas> ventasList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuario1")
    private List<Compras> comprasList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propietario")
    private List<Misc> miscList;

    public Usuario() {
    }

    public Usuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario(String idUsuario, String nombres, String apellidos, String passwd) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.passwd = passwd;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Misc getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Misc empresa) {
        this.empresa = empresa;
    }

    @XmlTransient
    public List<Ventas> getVentasList() {
        return ventasList;
    }

    public void setVentasList(List<Ventas> ventasList) {
        this.ventasList = ventasList;
    }

    @XmlTransient
    public List<Compras> getComprasList() {
        return comprasList;
    }

    public void setComprasList(List<Compras> comprasList) {
        this.comprasList = comprasList;
    }

    @XmlTransient
    public List<Misc> getMiscList() {
        return miscList;
    }

    public void setMiscList(List<Misc> miscList) {
        this.miscList = miscList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsuario != null ? idUsuario.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Usuario)) {
            return false;
        }
        Usuario other = (Usuario) object;
        if ((this.idUsuario == null && other.idUsuario != null) || (this.idUsuario != null && !this.idUsuario.equals(other.idUsuario))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.Usuario[ idUsuario=" + idUsuario + " ]";
    }

    public Usuario(String idUsuario, String nombres, String apellidos, String passwd, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.passwd = passwd;
        this.activo = activo;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
}
