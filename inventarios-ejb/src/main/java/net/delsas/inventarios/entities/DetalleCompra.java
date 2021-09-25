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
import javax.persistence.Column;
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
@Table(name = "DetalleCompra")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DetalleCompra.findAll", query = "SELECT d FROM DetalleCompra d"),
    @NamedQuery(name = "DetalleCompra.findByIdCompras", query = "SELECT d FROM DetalleCompra d WHERE d.detalleCompraPK.idCompras = :idCompras"),
    @NamedQuery(name = "DetalleCompra.findByUsuario", query = "SELECT d FROM DetalleCompra d WHERE d.detalleCompraPK.usuario = :usuario"),
    @NamedQuery(name = "DetalleCompra.findByProducto", query = "SELECT d FROM DetalleCompra d WHERE d.detalleCompraPK.producto = :producto"),
    @NamedQuery(name = "DetalleCompra.findByCantidad", query = "SELECT d FROM DetalleCompra d WHERE d.cantidad = :cantidad"),
    @NamedQuery(name = "DetalleCompra.findByCostoUnitario", query = "SELECT d FROM DetalleCompra d WHERE d.costoUnitario = :costoUnitario")})
public class DetalleCompra implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DetalleCompraPK detalleCompraPK;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal costoUnitario;
    @JoinColumns({
        @JoinColumn(name = "usuario", referencedColumnName = "usuario", nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "idCompras", referencedColumnName = "idCompras", nullable = false, insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Compras compras;
    @JoinColumn(name = "producto", referencedColumnName = "idInventario", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Inventario inventario;

    public DetalleCompra() {
    }

    public DetalleCompra(DetalleCompraPK detalleCompraPK) {
        this.detalleCompraPK = detalleCompraPK;
    }

    public DetalleCompra(DetalleCompraPK detalleCompraPK, int cantidad, BigDecimal costoUnitario) {
        this.detalleCompraPK = detalleCompraPK;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public DetalleCompra(Date idCompras, String usuario, int producto) {
        this.detalleCompraPK = new DetalleCompraPK(idCompras, usuario, producto);
    }

    public DetalleCompraPK getDetalleCompraPK() {
        return detalleCompraPK;
    }

    public void setDetalleCompraPK(DetalleCompraPK detalleCompraPK) {
        this.detalleCompraPK = detalleCompraPK;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public Compras getCompras() {
        return compras;
    }

    public void setCompras(Compras compras) {
        this.compras = compras;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (detalleCompraPK != null ? detalleCompraPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleCompra)) {
            return false;
        }
        DetalleCompra other = (DetalleCompra) object;
        if ((this.detalleCompraPK == null && other.detalleCompraPK != null) || (this.detalleCompraPK != null && !this.detalleCompraPK.equals(other.detalleCompraPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.DetalleCompra[ detalleCompraPK=" + detalleCompraPK + " ]";
    }
    
}
