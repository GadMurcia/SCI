/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.delsas.inventarios.entities.Compras;

/**
 *
 * @author delsas
 */
@Entity
public class reporteFacturasCompras implements Serializable {

    @Id
    private Date fecha;
    private BigDecimal valor;
    private byte[] factura;
    private String ext;

    public reporteFacturasCompras() {
    }

    public reporteFacturasCompras(Date fecha, BigDecimal valor, byte[] factura, String extencion) {
        this.fecha = fecha;
        this.valor = valor;
        this.factura = factura;
        this.ext = extencion;
    }

    public reporteFacturasCompras(Compras c) {
        if (c != null) {
            this.fecha = c.getComprasPK().getIdCompras();
            this.valor = c.getValor();
            this.factura = c.getFactura();
            this.ext = c.getExtencion();
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.fecha);
        hash = 89 * hash + Objects.hashCode(this.valor);
        hash = 89 * hash + Arrays.hashCode(this.factura);
        hash = 89 * hash + Objects.hashCode(this.ext);
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
        final reporteFacturasCompras other = (reporteFacturasCompras) obj;
        if (!Objects.equals(this.ext, other.ext)) {
            return false;
        }
        if (!Objects.equals(this.fecha, other.fecha)) {
            return false;
        }
        if (!Objects.equals(this.valor, other.valor)) {
            return false;
        }
        return Arrays.equals(this.factura, other.factura);
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public byte[] getFactura() {
        return factura;
    }

    public void setFactura(byte[] factura) {
        this.factura = factura;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        return "reporteFacturasCompras{" + "fecha=" + fecha + ", valor=" + valor + ", ext=" + ext + '}';
    }
    
    

}
