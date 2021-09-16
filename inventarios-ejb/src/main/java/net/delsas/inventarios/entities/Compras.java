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
@Table(name = "Compras")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Compras.findAll", query = "SELECT c FROM Compras c"),
    @NamedQuery(name = "Compras.findByIdCompras", query = "SELECT c FROM Compras c WHERE c.comprasPK.idCompras = :idCompras"),
    @NamedQuery(name = "Compras.findByUsuario", query = "SELECT c FROM Compras c WHERE c.comprasPK.usuario = :usuario"),
    @NamedQuery(name = "Compras.findByValor", query = "SELECT c FROM Compras c WHERE c.valor = :valor"),
    @NamedQuery(name = "Compras.findByComentario", query = "SELECT c FROM Compras c WHERE c.comentario = :comentario")})
public class Compras implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ComprasPK comprasPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    private BigDecimal valor;
    @Size(max = 250)
    private String comentario;
    @JoinColumn(name = "usuario", referencedColumnName = "idUsuario", insertable = false, updatable = false)
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
    public int hashCode() {
        int hash = 0;
        hash += (comprasPK != null ? comprasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Compras)) {
            return false;
        }
        Compras other = (Compras) object;
        if ((this.comprasPK == null && other.comprasPK != null) || (this.comprasPK != null && !this.comprasPK.equals(other.comprasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.Compras[ comprasPK=" + comprasPK + " ]";
    }
    
}