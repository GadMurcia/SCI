/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author delsas
 */
@Entity
@Table(name = "Inventario", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"producto"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Inventario.findAll", query = "SELECT i FROM Inventario i"),
    @NamedQuery(name = "Inventario.findByProducto", query = "SELECT i FROM Inventario i WHERE i.producto = :producto"),
    @NamedQuery(name = "Inventario.findByIdInventario", query = "SELECT i FROM Inventario i WHERE i.idInventario = :idInventario"),
    @NamedQuery(name = "Inventario.findByPrecioUnitario", query = "SELECT i FROM Inventario i WHERE i.precioUnitario = :precioUnitario"),
    @NamedQuery(name = "Inventario.findByDescripcion", query = "SELECT i FROM Inventario i WHERE i.descripcion = :descripcion"),
    @NamedQuery(name = "Inventario.findByTienda", query = "SELECT i FROM Inventario i WHERE i.tienda.idMisc = :idTienda")
})
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String producto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer idInventario;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal precioUnitario;
    @Size(max = 250)
    @Column(length = 250)
    private String descripcion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventario")
    private List<DetalleVentas> detalleVentasList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "inventario")
    private List<DetalleCompra> detalleCompraList;
    @JoinColumn(name = "tienda", referencedColumnName = "idMisc", nullable = false)
    @ManyToOne(optional = false)
    private Misc tienda;    
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private boolean activo;

    public Inventario() {
    }

    public Inventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Inventario(Integer idInventario, String producto, BigDecimal precioUnitario) {
        this.idInventario = idInventario;
        this.producto = producto;
        this.precioUnitario = precioUnitario;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<DetalleVentas> getDetalleVentasList() {
        return detalleVentasList;
    }

    public void setDetalleVentasList(List<DetalleVentas> detalleVentasList) {
        this.detalleVentasList = detalleVentasList;
    }

    @XmlTransient
    public List<DetalleCompra> getDetalleCompraList() {
        return detalleCompraList;
    }

    public void setDetalleCompraList(List<DetalleCompra> detalleCompraList) {
        this.detalleCompraList = detalleCompraList;
    }

    public Misc getTienda() {
        return tienda;
    }

    public void setTienda(Misc tienda) {
        this.tienda = tienda;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.producto);
        hash = 23 * hash + Objects.hashCode(this.idInventario);
        hash = 23 * hash + Objects.hashCode(this.precioUnitario);
        hash = 23 * hash + Objects.hashCode(this.descripcion);
        hash = 23 * hash + Objects.hashCode(this.tienda);
        hash = 23 * hash + (this.activo ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Inventario other = (Inventario) obj;
        if (this.activo != other.activo) {
            return false;
        }
        if (!Objects.equals(this.producto, other.producto)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        if (!Objects.equals(this.idInventario, other.idInventario)) {
            return false;
        }
        if (!Objects.equals(this.precioUnitario, other.precioUnitario)) {
            return false;
        }
        return (!Objects.equals(this.tienda, other.tienda));
    }

    @Override
    public String toString() {
        return "Inventario{" + "producto=" + producto + ", idInventario=" + idInventario + ", precioUnitario=" + precioUnitario + ", descripcion=" + descripcion + ", tienda=" + tienda + ", activo=" + activo + '}';
    }
    
    

}
