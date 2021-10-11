/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.Inventario;

/**
 *
 * @author delsas
 */
@Entity
public class ReporteDetalleCompra extends auxiliarCtr implements Serializable {

    @Id
    private int id;
    private Inventario inv;
    private Integer cantidad;
    private double costoU;
    private double subTotal;

    public ReporteDetalleCompra() {
    }

    public ReporteDetalleCompra(DetalleCompra dc, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl) {
        this.id = dc.getDetalleCompraPK().getProducto();
        this.inv = dc.getInventario();
        this.cantidad = dc.getCantidad();
        this.costoU = redondeo4decimales(Existencias.getCostoAVGGlobal(id, dcfl));
        this.subTotal = redondeo2decimales(this.costoU * this.cantidad);
    }

    public ReporteDetalleCompra(DetalleCompra dc, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl, Date inicio, Date fin) {
        this.id = dc.getDetalleCompraPK().getProducto();
        this.inv = dc.getInventario();
        this.cantidad = dc.getCantidad();
        this.costoU = redondeo4decimales(Existencias.getCostoAVGPeriodo(id, dcfl, inicio, fin));
        this.costoU = this.costoU != 0 ? this.costoU : redondeo4decimales(Existencias.getCostoAVGGlobal(id, dcfl));
        this.subTotal = redondeo2decimales(this.costoU * this.cantidad);
    }

    public ReporteDetalleCompra(int id, Inventario inv, Integer cantidad, double costoU, double subTotal) {
        this.id = id;
        this.inv = inv;
        this.cantidad = cantidad;
        this.costoU = costoU;
        this.subTotal = subTotal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inventario getInv() {
        return inv;
    }

    public void setInv(Inventario inv) {
        this.inv = inv;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoU() {
        return costoU;
    }

    public void setCostoU(double costoU) {
        this.costoU = costoU;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.id;
        hash = 41 * hash + Objects.hashCode(this.inv);
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
        final ReporteDetalleCompra other = (ReporteDetalleCompra) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.inv, other.inv);
    }

    @Override
    public String toString() {
        return "ReporteDetalleCompra{" + "id=" + id + ", inv=" + inv + ", cantidad=" + cantidad + ", costoU=" + costoU + ", subTotal=" + subTotal + '}';
    }

}
