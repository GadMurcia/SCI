/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "Compras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compras.findAll", query = "SELECT c FROM Compras c"),
    @NamedQuery(name = "Compras.findByIdCompras", query = "SELECT c FROM Compras c WHERE c.comprasPK.idCompras = :idCompras"),
    @NamedQuery(name = "Compras.findByUsuario", query = "SELECT c FROM Compras c WHERE c.comprasPK.usuario = :usuario"),
    @NamedQuery(name = "Compras.findByValor", query = "SELECT c FROM Compras c WHERE c.valor = :valor"),
    @NamedQuery(name = "Compras.findByComentario", query = "SELECT c FROM Compras c WHERE c.comentario = :comentario"),
    @NamedQuery(name = "Compras.findConFacturaByPeriodo", query = "SELECT c FROM Compras c WHERE c.comprasPK.idCompras >= :inicio AND c.comprasPK.idCompras <= :fin AND c.factura != NULL")
})
public class Compras implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComprasPK comprasPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal valor;
    @Size(max = 250)
    @Column(length = 250)
    private String comentario;
    @Lob
    private byte[] factura;
    @Size(max = 10)
    @Column(length = 10)
    private String extencion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @JoinColumn(name = "usuario", referencedColumnName = "idUsuario", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuario usuario1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compras")
    private List<DetalleCompra> detalleCompraList;

    public Compras() {
    }

    public Compras(ComprasPK comprasPK) {
        this.comprasPK = comprasPK;
    }

    public Compras(ComprasPK comprasPK, BigDecimal valor) {
        this.comprasPK = comprasPK;
        this.valor = valor;
    }

    public Compras(Date idCompras, String usuario) {
        this.comprasPK = new ComprasPK(idCompras, usuario);
    }

    public ComprasPK getComprasPK() {
        return comprasPK;
    }

    public void setComprasPK(ComprasPK comprasPK) {
        this.comprasPK = comprasPK;
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

    public Usuario getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Usuario usuario1) {
        this.usuario1 = usuario1;
    }

    @XmlTransient
    public List<DetalleCompra> getDetalleCompraList() {
        return detalleCompraList;
    }

    public void setDetalleCompraList(List<DetalleCompra> detalleCompraList) {
        this.detalleCompraList = detalleCompraList;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.Compras[ comprasPK=" + comprasPK + " ]";
    }

    public byte[] getFactura() {
        return factura;
    }

    public void setFactura(byte[] factura) {
        this.factura = factura;
    }

    public String getExtencion() {
        return extencion;
    }

    public void setExtencion(String extencion) {
        this.extencion = extencion;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.comprasPK);
        hash = 79 * hash + Objects.hashCode(this.valor);
        hash = 79 * hash + Objects.hashCode(this.comentario);
        hash = 79 * hash + Arrays.hashCode(this.factura);
        hash = 79 * hash + Objects.hashCode(this.extencion);
        hash = 79 * hash + Objects.hashCode(this.usuario1);
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
        final Compras other = (Compras) obj;
        if (!Objects.equals(this.comentario, other.comentario)) {
            return false;
        }
        if (!Objects.equals(this.extencion, other.extencion)) {
            return false;
        }
        if (!Objects.equals(this.comprasPK, other.comprasPK)) {
            return false;
        }
        if (!Objects.equals(this.valor, other.valor)) {
            return false;
        }
        if (!Arrays.equals(this.factura, other.factura)) {
            return false;
        }
        return Objects.equals(this.usuario1, other.usuario1);
    }

}
