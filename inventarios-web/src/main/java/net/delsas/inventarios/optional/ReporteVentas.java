/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.Inventario;

/**
 *
 * @author delsas
 */
@Entity
public class ReporteVentas extends auxiliarCtr implements Serializable {

    @Id
    private int id;
    private Inventario inv;
    private Integer cantidad;
    private double precioU;
    private double costoU;
    private double subTotal;
    private double utilidad;

    public ReporteVentas() {
    }

    public ReporteVentas(DetalleVentas dt, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl) {
        this.id = dt.getInventario().getIdInventario();
        this.inv = dt.getInventario();
        this.cantidad = dt.getCantidad();
        this.costoU = redondeo4decimales(Existencias.getCostoAVGGlobal(id, dcfl));
        this.precioU = redondeo4decimales(getPrecioAVGGlobal(id, dvfl));
        this.subTotal = redondeo2decimales(this.cantidad * this.precioU);
        this.subTotal = redondeo3decimales(this.cantidad * this.precioU);
        this.utilidad = redondeo3decimales(subTotal - (cantidad * costoU));
    }

    public ReporteVentas(DetalleVentas dt, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl, Date inicio, Date fin) {
        this.id = dt.getInventario().getIdInventario();
        this.inv = dt.getInventario();
        this.cantidad = dt.getCantidad();
        this.costoU = redondeo4decimales(Existencias.getCostoAVGPeriodo(id, dcfl, inicio, fin));
        this.costoU = this.costoU > 0 ? this.costoU : redondeo4decimales(Existencias.getCostoAVGGlobal(id, dcfl));
        this.precioU = redondeo4decimales(getPrecioAVGPeriodo(id, dvfl, inicio, fin));
        this.precioU = this.precioU > 0 ? this.precioU : getPrecioAVGGlobal(id, dvfl);
        this.subTotal = redondeo3decimales(this.cantidad * this.precioU);
        this.utilidad = redondeo3decimales(subTotal - (cantidad * costoU));
    }

    public ReporteVentas(int id, Inventario inv, Integer cantidad, double precioU, double costoU, double subTotal, double utilidad) {
        this.id = id;
        this.inv = inv;
        this.cantidad = cantidad;
        this.precioU = precioU;
        this.costoU = costoU;
        this.subTotal = subTotal;
        this.utilidad = utilidad;
    }

    public static double getPrecioAVGGlobal(int id, DetalleVentasFacadeLocal dvfl) {
        List<DetalleVentas> f = dvfl.findByProducto(id);
        return f.stream().mapToDouble(f0 -> f0.getCantidad() * f0.getPrecioUnitario().doubleValue()).sum()
                / f.stream().mapToDouble(DetalleVentas::getCantidad).sum();
    }

    public static double getPrecioAVGPeriodo(int id, DetalleVentasFacadeLocal dvfl, Date inicio, Date fin) {
        List<DetalleVentas> f = dvfl.findByProductoAndPeriodo(id, inicio, fin);
        return f.stream().mapToDouble(f0 -> f0.getCantidad() * f0.getPrecioUnitario().doubleValue()).sum()
                / f.stream().mapToDouble(DetalleVentas::getCantidad).sum();
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

    public double getPrecioU() {
        return precioU;
    }

    public void setPrecioU(double precioU) {
        this.precioU = precioU;
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

    public double getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(double utilidad) {
        this.utilidad = utilidad;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
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
        final ReporteVentas other = (ReporteVentas) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "ReporteVentas{" + "id=" + id + ", inv=" + inv.getProducto() + ", cantidad=" + cantidad + ", precioU=" + precioU + ", costoU=" + costoU + ", subTotal=" + subTotal + '}';
    }

}
