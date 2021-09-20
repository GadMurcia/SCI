/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
    @NamedQuery(name = "Libroventas.findAll", query = "SELECT l FROM Libroventas l"),
    @NamedQuery(name = "Libroventas.findByIdProducto", query = "SELECT l FROM Libroventas l WHERE l.idProducto = :idProducto"),
    @NamedQuery(name = "Libroventas.findByVentas", query = "SELECT l FROM Libroventas l WHERE l.ventas = :ventas"),
    @NamedQuery(name = "Libroventas.findByPrecio", query = "SELECT l FROM Libroventas l WHERE l.precio = :precio"),
    @NamedQuery(name = "Libroventas.findBySubTotal", query = "SELECT l FROM Libroventas l WHERE l.subTotal = :subTotal")})
public class Libroventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Id
    private int idProducto;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(precision = 22)
    private Double ventas;
    @Column(precision = 15, scale = 6)
    private BigDecimal precio;
    @Column(precision = 22)
    private Double subTotal;
    @JoinColumn(name = "idProducto", referencedColumnName = "idInventario", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Inventario inventario;

    public Libroventas() {
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Double getVentas() {
        return ventas;
    }

    public void setVentas(Double ventas) {
        this.ventas = ventas;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    @Override
    public String toString() {
        return "Libroventas{" + "idProducto=" + idProducto + ", ventas=" + ventas + ", precio=" + precio + ", subTotal=" + subTotal + ", inventario=" + inventario + '}';
    }
    
}
