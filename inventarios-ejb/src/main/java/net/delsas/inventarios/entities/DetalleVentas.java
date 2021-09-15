/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author delsas
 */
@Entity
@Table(name = "DetalleVentas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DetalleVentas.findAll", query = "SELECT d FROM DetalleVentas d"),
    @NamedQuery(name = "DetalleVentas.findByIdVentas", query = "SELECT d FROM DetalleVentas d WHERE d.detalleVentasPK.idVentas = :idVentas"),
    @NamedQuery(name = "DetalleVentas.findByUsuario", query = "SELECT d FROM DetalleVentas d WHERE d.detalleVentasPK.usuario = :usuario"),
    @NamedQuery(name = "DetalleVentas.findByProducto", query = "SELECT d FROM DetalleVentas d WHERE d.detalleVentasPK.producto = :producto"),
    @NamedQuery(name = "DetalleVentas.findByCantidad", query = "SELECT d FROM DetalleVentas d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DetalleVentas.findByPrecioUnitario", query = "SELECT d FROM DetalleVentas d WHERE d.precioUnitario = :precioUnitario")})
public class DetalleVentas implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetalleVentasPK detalleVentasPK;
    @Basic(optional = false)
    @NotNull
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    private BigDecimal precioUnitario;
    @JoinColumn(name = "producto", referencedColumnName = "idInventario", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Inventario inventario;
    @JoinColumns({
        @JoinColumn(name = "usuario", referencedColumnName = "usuario", insertable = false, updatable = false),
        @JoinColumn(name = "idVentas", referencedColumnName = "idVentas", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Ventas ventas;

    public DetalleVentas() {
    }

    public DetalleVentas(DetalleVentasPK detalleVentasPK) {
        this.detalleVentasPK = detalleVentasPK;
    }

    public DetalleVentas(DetalleVentasPK detalleVentasPK, int cantidad, BigDecimal precioUnitario) {
        this.detalleVentasPK = detalleVentasPK;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public DetalleVentas(Date idVentas, String usuario, int producto) {
        this.detalleVentasPK = new DetalleVentasPK(idVentas, usuario, producto);
    }

    public DetalleVentasPK getDetalleVentasPK() {
        return detalleVentasPK;
    }

    public void setDetalleVentasPK(DetalleVentasPK detalleVentasPK) {
        this.detalleVentasPK = detalleVentasPK;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public Ventas getVentas() {
        return ventas;
    }

    public void setVentas(Ventas ventas) {
        this.ventas = ventas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detalleVentasPK != null ? detalleVentasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleVentas)) {
            return false;
        }
        DetalleVentas other = (DetalleVentas) object;
        if ((this.detalleVentasPK == null && other.detalleVentasPK != null) || (this.detalleVentasPK != null && !this.detalleVentasPK.equals(other.detalleVentasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.DetalleVentas[ detalleVentasPK=" + detalleVentasPK + " ]";
    }
    
}
