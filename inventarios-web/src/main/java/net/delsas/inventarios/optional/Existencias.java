/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.Inventario;

/**
 *
 * @author delsas
 */
@Entity
public class Existencias implements Serializable {

    @Id
    private String nombre;
    private double existencias;
    private double costoAVG;
    private double precioAVG;
    private double costoTotal;
    private double valorTotal;
    private double utilidad;

    public Existencias() {
    }

    public Existencias(Inventario prod, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl) {
        prod.setDetalleCompraList(dcfl.findByProducto(prod.getIdInventario()));
        prod.setDetalleVentasList(dvfl.findByProducto(prod.getIdInventario()));
        this.nombre = prod.getProducto();
        this.existencias = redondeo2decimales(prod.getDetalleCompraList().stream().mapToDouble(DetalleCompra::getCantidad).sum()
                - prod.getDetalleVentasList().stream().mapToDouble(DetalleVentas::getCantidad).sum());
        this.costoAVG = redondeo2decimales(prod.getDetalleCompraList().stream().map(DetalleCompra::getCostoUnitario)
                .reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO).doubleValue());
        this.costoAVG = redondeo2decimales(this.costoAVG != 0 ? this.costoAVG / prod.getDetalleCompraList().size() : 0);
        this.costoTotal = redondeo2decimales(this.existencias * this.costoAVG);
        this.precioAVG = redondeo2decimales(prod.getPrecioUnitario().doubleValue());
        this.valorTotal = redondeo2decimales(this.existencias * this.precioAVG);
        this.utilidad = redondeo2decimales(this.valorTotal - this.costoTotal);

    }

    public Existencias(String nombre, double existencias, double costoAVG, double precioAVG) {
        this.nombre = nombre;
        this.existencias = existencias;
        this.costoAVG = costoAVG;
        this.precioAVG = precioAVG;
        this.costoTotal = existencias * costoAVG;
        this.valorTotal = existencias * precioAVG;
        this.utilidad = this.valorTotal - this.costoTotal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getExistencias() {
        return existencias;
    }

    public void setExistencias(double existencias) {
        this.existencias = existencias;
    }

    public double getCostoAVG() {
        return costoAVG;
    }

    public void setCostoAVG(double costoAVG) {
        this.costoAVG = costoAVG;
    }

    public double getPrecioAVG() {
        return precioAVG;
    }

    public void setPrecioAVG(double precioAVG) {
        this.precioAVG = precioAVG;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(double utilidad) {
        this.utilidad = utilidad;
    }

    private double redondeo2decimales(double o) {
        o = o * 100;
        o = Math.round(o);
        o = o / 100;
        return o;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.nombre);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.existencias) ^ (Double.doubleToLongBits(this.existencias) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.costoAVG) ^ (Double.doubleToLongBits(this.costoAVG) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.precioAVG) ^ (Double.doubleToLongBits(this.precioAVG) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.costoTotal) ^ (Double.doubleToLongBits(this.costoTotal) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.valorTotal) ^ (Double.doubleToLongBits(this.valorTotal) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.utilidad) ^ (Double.doubleToLongBits(this.utilidad) >>> 32));
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
        final Existencias other = (Existencias) obj;
        if (Double.doubleToLongBits(this.existencias) != Double.doubleToLongBits(other.existencias)) {
            return false;
        }
        if (Double.doubleToLongBits(this.costoAVG) != Double.doubleToLongBits(other.costoAVG)) {
            return false;
        }
        if (Double.doubleToLongBits(this.precioAVG) != Double.doubleToLongBits(other.precioAVG)) {
            return false;
        }
        if (Double.doubleToLongBits(this.costoTotal) != Double.doubleToLongBits(other.costoTotal)) {
            return false;
        }
        if (Double.doubleToLongBits(this.valorTotal) != Double.doubleToLongBits(other.valorTotal)) {
            return false;
        }
        if (Double.doubleToLongBits(this.utilidad) != Double.doubleToLongBits(other.utilidad)) {
            return false;
        }
        return Objects.equals(this.nombre, other.nombre);
    }

    @Override
    public String toString() {
        return "Existencias{" + "nombre=" + nombre + ", existencias=" + existencias + ", costoAVG=" + costoAVG + ", precioAVG=" + precioAVG + ", costoTotal=" + costoTotal + ", valorTotal=" + valorTotal + ", utilidad=" + utilidad + '}';
    }

}
