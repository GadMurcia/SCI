/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
public class Existencias extends auxiliarCtr implements Serializable {

    @Id
    private Integer id;
    private String nombre;
    private Integer existencias;
    private double costoAVG;
    private double precioAVG;
    private double costoTotal;
    private double valorTotal;
    private double utilidad;

    public Existencias() {
    }

    public Existencias(Inventario prod, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl) {
        this.id = prod.getIdInventario();
        this.nombre = prod.getProducto();
        this.existencias = dcfl.findByProducto(this.id).stream().mapToInt(DetalleCompra::getCantidad).sum()
                - dvfl.findByProducto(this.id).stream().mapToInt(DetalleVentas::getCantidad).sum();
        this.costoAVG = redondeo4decimales(getCostoAVGGlobal(prod.getIdInventario(), dcfl)); //redondeo4decimales(prod.getDetalleCompraList().stream().mapToDouble(z -> z.getCantidad() * z.getCostoUnitario().doubleValue()).sum() / prod.getDetalleCompraList().stream().mapToInt(DetalleCompra::getCantidad).sum());
        this.costoTotal = redondeo2decimales(this.existencias * this.costoAVG);
        this.precioAVG = redondeo2decimales(prod.getPrecioUnitario().doubleValue());
        this.valorTotal = redondeo2decimales(this.existencias * this.precioAVG);
        this.utilidad = redondeo2decimales(this.valorTotal - this.costoTotal);
        if (this.id.equals(252)) {
            System.out.println(this);
        }
    }

    public Existencias(String nombre, int existencias, double costoAVG, double precioAVG, int id) {
        this.id = id;
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

    public Integer getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public double getCostoAVG() {
        return costoAVG;
    }

    public void setCostoAVG(double costoAVG) {
        this.costoAVG = costoAVG;
    }

    public static double getCostoAVGGlobal(int id, DetalleCompraFacadeLocal dcfl) {
        List<DetalleCompra> f = dcfl.findByProducto(id);
        return (f.stream().mapToDouble(z -> z.getCantidad() * z.getCostoUnitario().doubleValue()).sum()
                / f.stream().mapToInt(DetalleCompra::getCantidad).sum());
    }

    public static double getCostoAVGPeriodo(int id, DetalleCompraFacadeLocal dcfl, Date inicio, Date fin) {
        List<DetalleCompra> f = dcfl.findByProductoAdnPeriodoFechas(id, inicio, fin);
        return (f.stream().mapToDouble(z -> z.getCantidad() * z.getCostoUnitario().doubleValue()).sum()
                / f.stream().mapToInt(DetalleCompra::getCantidad).sum());
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.nombre);
        hash = 47 * hash + Objects.hashCode(this.existencias);
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.costoAVG) ^ (Double.doubleToLongBits(this.costoAVG) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.precioAVG) ^ (Double.doubleToLongBits(this.precioAVG) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.costoTotal) ^ (Double.doubleToLongBits(this.costoTotal) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.valorTotal) ^ (Double.doubleToLongBits(this.valorTotal) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.utilidad) ^ (Double.doubleToLongBits(this.utilidad) >>> 32));
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
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return Objects.equals(this.existencias, other.existencias);
    }

    @Override
    public String toString() {
        return "Existencias{" + "id=" + id + ", nombre=" + nombre + ", existencias=" + existencias + ", costoAVG=" + costoAVG + ", precioAVG=" + precioAVG + ", costoTotal=" + costoTotal + ", valorTotal=" + valorTotal + ", utilidad=" + utilidad + '}';
    }
}
