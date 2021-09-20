/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author delsas
 */
@Entity
@Table(catalog = "LibrosInv", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Librocompras.findAll", query = "SELECT l FROM Librocompras l"),
    @NamedQuery(name = "Librocompras.findByIdProducto", query = "SELECT l FROM Librocompras l WHERE l.idProducto = :idProducto"),
    @NamedQuery(name = "Librocompras.findByCompras", query = "SELECT l FROM Librocompras l WHERE l.compras = :compras"),
    @NamedQuery(name = "Librocompras.findByCosto", query = "SELECT l FROM Librocompras l WHERE l.costo = :costo"),
    @NamedQuery(name = "Librocompras.findBySubtotal", query = "SELECT l FROM Librocompras l WHERE l.subtotal = :subtotal")})
public class Librocompras implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Id
    private int idProducto;
    private BigInteger compras;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 15, scale = 6)
    private BigDecimal costo;
    @Column(precision = 47, scale = 6)
    private BigDecimal subtotal;
    @JoinColumn(name = "idProducto", referencedColumnName = "idInventario", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Inventario inventario;

    public Librocompras() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public BigInteger getCompras() {
        return compras;
    }

    public void setCompras(BigInteger compras) {
        this.compras = compras;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    @Override
    public String toString() {
        return "Librocompras{" + "idProducto=" + idProducto + ", compras=" + compras + ", costo=" + costo + ", subtotal=" + subtotal + ", inventario=" + inventario + '}';
    }
    
}
