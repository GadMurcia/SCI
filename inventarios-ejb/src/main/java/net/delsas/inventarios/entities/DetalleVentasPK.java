/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author delsas
 */
@Embeddable
public class DetalleVentasPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date idVentas;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int giroCaja;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    private int producto;

    public DetalleVentasPK() {
    }

    public DetalleVentasPK(Date idVentas, int giroCaja, int producto) {
        this.idVentas = idVentas;
        this.giroCaja = giroCaja;
        this.producto = producto;
    }

    public Date getIdVentas() {
        return idVentas;
    }

    public void setIdVentas(Date idVentas) {
        this.idVentas = idVentas;
    }

    public int getGiroCaja() {
        return giroCaja;
    }

    public void setGiroCaja(int giroCaja) {
        this.giroCaja = giroCaja;
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
        hash += (idVentas != null ? idVentas.hashCode() : 0);
        hash += (int) giroCaja;
        hash += (int) producto;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleVentasPK)) {
            return false;
        }
        DetalleVentasPK other = (DetalleVentasPK) object;
        if ((this.idVentas == null && other.idVentas != null) || (this.idVentas != null && !this.idVentas.equals(other.idVentas))) {
            return false;
        }
        if (this.giroCaja != other.giroCaja) {
            return false;
        }
        if (this.producto != other.producto) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.delsas.inventarios.entities.DetalleVentasPK[ idVentas=" + idVentas + ", giroCaja=" + giroCaja + ", producto=" + producto + " ]";
    }
    
}
