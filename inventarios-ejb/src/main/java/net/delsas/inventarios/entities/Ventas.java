/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "Ventas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ventas.findAll", query = "SELECT v FROM Ventas v"),
    @NamedQuery(name = "Ventas.findByIdVentas", query = "SELECT v FROM Ventas v WHERE v.ventasPK.idVentas = :idVentas"),
    @NamedQuery(name = "Ventas.findByGiroCaja", query = "SELECT v FROM Ventas v WHERE v.ventasPK.giroCaja = :giroCaja"),
    @NamedQuery(name = "Ventas.findByValor", query = "SELECT v FROM Ventas v WHERE v.valor = :valor"),
    @NamedQuery(name = "Ventas.findByComentario", query = "SELECT v FROM Ventas v WHERE v.comentario = :comentario"),
    @NamedQuery(name = "Ventas.findByPeriodoFechas", query = "SELECT v FROM Ventas v WHERE v.ventasPK.idVentas >= :inicio AND v.ventasPK.idVentas <= :fin")
})
public class Ventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected VentasPK ventasPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal valor;
    @Size(max = 250)
    @Column(length = 250)
    private String comentario;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ventas")
    private List<DetalleVentas> detalleVentasList;
    @JoinColumn(name = "giroCaja", referencedColumnName = "idGiroDeCaja", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private GiroDeCaja giroDeCaja;

    public Ventas() {
    }

    public Ventas(VentasPK ventasPK) {
        this.ventasPK = ventasPK;
    }

    public Ventas(VentasPK ventasPK, BigDecimal valor) {
        this.ventasPK = ventasPK;
        this.valor = valor;
    }

    public Ventas(Date idVentas, int giroCaja) {
        this.ventasPK = new VentasPK(idVentas, giroCaja);
    }

    public VentasPK getVentasPK() {
        return ventasPK;
    }

    public void setVentasPK(VentasPK ventasPK) {
        this.ventasPK = ventasPK;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @XmlTransient
    public List<DetalleVentas> getDetalleVentasList() {
        return detalleVentasList;
    }

    public void setDetalleVentasList(List<DetalleVentas> detalleVentasList) {
        this.detalleVentasList = detalleVentasList;
    }

    public GiroDeCaja getGiroDeCaja() {
        return giroDeCaja;
    }

    public void setGiroDeCaja(GiroDeCaja giroDeCaja) {
        this.giroDeCaja = giroDeCaja;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ventasPK != null ? ventasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ventas)) {
            return false;
        }
        Ventas other = (Ventas) object;
        if ((this.ventasPK == null && other.ventasPK != null) || (this.ventasPK != null && !this.ventasPK.equals(other.ventasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.Ventas[ ventasPK=" + ventasPK + " ]";
    }

}
