/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author delsas
 */
@Embeddable
public class DetalleCompraPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date idCompras;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    private String usuario;
    @Basic(optional = false)
    @NotNull
    private int producto;

    public DetalleCompraPK() {
    }

    public DetalleCompraPK(Date idCompras, String usuario, int producto) {
        this.idCompras = idCompras;
        this.usuario = usuario;
        this.producto = producto;
    }

    public Date getIdCompras() {
        return idCompras;
    }

    public void setIdCompras(Date idCompras) {
        this.idCompras = idCompras;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getProducto() {
        return producto;
    }

    public void setProducto(int producto) {
        this.producto = producto;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCompras != null ? idCompras.hashCode() : 0);
        hash += (usuario != null ? usuario.hashCode() : 0);
        hash += (int) producto;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleCompraPK)) {
            return false;
        }
        DetalleCompraPK other = (DetalleCompraPK) object;
        if ((this.idCompras == null && other.idCompras != null) || (this.idCompras != null && !this.idCompras.equals(other.idCompras))) {
            return false;
        }
        if ((this.usuario == null && other.usuario != null) || (this.usuario != null && !this.usuario.equals(other.usuario))) {
            return false;
        }
        if (this.producto != other.producto) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.DetalleCompraPK[ idCompras=" + idCompras + ", usuario=" + usuario + ", producto=" + producto + " ]";
    }
    
}
