/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author delsas
 */
@Entity
@Table(name = "GiroDeCaja")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GiroDeCaja.findAll", query = "SELECT g FROM GiroDeCaja g"),
    @NamedQuery(name = "GiroDeCaja.findByIdGiroDeCaja", query = "SELECT g FROM GiroDeCaja g WHERE g.idGiroDeCaja = :idGiroDeCaja"),
    @NamedQuery(name = "GiroDeCaja.findByInicio", query = "SELECT g FROM GiroDeCaja g WHERE g.inicio = :inicio"),
    @NamedQuery(name = "GiroDeCaja.findByFin", query = "SELECT g FROM GiroDeCaja g WHERE g.fin = :fin"),
    @NamedQuery(name = "GiroDeCaja.findByCajaInicial", query = "SELECT g FROM GiroDeCaja g WHERE g.cajaInicial = :cajaInicial"),
    @NamedQuery(name = "GiroDeCaja.findByFaltantes", query = "SELECT g FROM GiroDeCaja g WHERE g.faltantes = :faltantes"),
    @NamedQuery(name = "GiroDeCaja.findByExcedentes", query = "SELECT g FROM GiroDeCaja g WHERE g.excedentes = :excedentes"),
    @NamedQuery(name = "GiroDeCaja.findByCierre", query = "SELECT g FROM GiroDeCaja g WHERE g.cierre = :cierre"),
    @NamedQuery(name = "GiroDeCaja.findByRetiros", query = "SELECT g FROM GiroDeCaja g WHERE g.retiros = :retiros"),
    @NamedQuery(name = "GiroDeCaja.findByDetalleRetiros", query = "SELECT g FROM GiroDeCaja g WHERE g.detalleRetiros = :detalleRetiros")})
public class GiroDeCaja implements Serializable {

    @Id
    @Basic(optional = false)
    @NotNull
    private Integer idGiroDeCaja;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicio;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date fin;
    @Basic(optional = false)
    @NotNull
    private double cajaInicial;
    @Basic(optional = false)
    @NotNull
    private double faltantes;
    @Basic(optional = false)
    @NotNull
    private double excedentes;
    @Basic(optional = false)
    @NotNull
    private double cierre;
    @Basic(optional = false)
    @NotNull
    private double retiros;
    @Size(max = 300)
    private String detalleRetiros;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "giroDeCaja")
    private List<Ventas> ventasList;
    @JoinColumn(name = "responsable", referencedColumnName = "idUsuario")
    @ManyToOne(optional = false)
    private Usuario responsable;

    public GiroDeCaja() {
    }

    public GiroDeCaja(Integer idGiroDeCaja) {
        this.idGiroDeCaja = idGiroDeCaja;
    }

    public GiroDeCaja(Integer idGiroDeCaja, Date inicio, Date fin, double cajaInicial, double faltantes, double excedentes, double cierre, double retiros) {
        this.idGiroDeCaja = idGiroDeCaja;
        this.inicio = inicio;
        this.fin = fin;
        this.cajaInicial = cajaInicial;
        this.faltantes = faltantes;
        this.excedentes = excedentes;
        this.cierre = cierre;
        this.retiros = retiros;
    }

    public Integer getIdGiroDeCaja() {
        return idGiroDeCaja;
    }

    public void setIdGiroDeCaja(Integer idGiroDeCaja) {
        this.idGiroDeCaja = idGiroDeCaja;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public double getCajaInicial() {
        return cajaInicial;
    }

    public void setCajaInicial(double cajaInicial) {
        this.cajaInicial = cajaInicial;
    }

    public double getFaltantes() {
        return faltantes;
    }

    public void setFaltantes(double faltantes) {
        this.faltantes = faltantes;
    }

    public double getExcedentes() {
        return excedentes;
    }

    public void setExcedentes(double excedentes) {
        this.excedentes = excedentes;
    }

    public double getCierre() {
        return cierre;
    }

    public void setCierre(double cierre) {
        this.cierre = cierre;
    }

    public double getRetiros() {
        return retiros;
    }

    public void setRetiros(double retiros) {
        this.retiros = retiros;
    }

    public String getDetalleRetiros() {
        return detalleRetiros;
    }

    public void setDetalleRetiros(String detalleRetiros) {
        this.detalleRetiros = detalleRetiros;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idGiroDeCaja != null ? idGiroDeCaja.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GiroDeCaja)) {
            return false;
        }
        GiroDeCaja other = (GiroDeCaja) object;
        if ((this.idGiroDeCaja == null && other.idGiroDeCaja != null) || (this.idGiroDeCaja != null && !this.idGiroDeCaja.equals(other.idGiroDeCaja))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.GiroDeCaja[ idGiroDeCaja=" + idGiroDeCaja + " ]";
    }

    @XmlTransient
    public List<Ventas> getVentasList() {
        return ventasList;
    }

    public void setVentasList(List<Ventas> ventasList) {
        this.ventasList = ventasList;
    }
    
}
