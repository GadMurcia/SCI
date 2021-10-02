/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.util.Date;
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
    private double cantidad;
    private double costoU;
    private double subTotal;

    public ReporteDetalleCompra() {
    }

    public ReporteDetalleCompra(DetalleCompra dc, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl) {
    }

    public ReporteDetalleCompra(DetalleCompra dc, DetalleCompraFacadeLocal dcfl, DetalleVentasFacadeLocal dvfl, Date inicio, Date fin) {
    }

    public ReporteDetalleCompra(int id, Inventario inv, double cantidad, double costoU, double subTotal) {
        this.id = id;
        this.inv = inv;
        this.cantidad = cantidad;
        this.costoU = costoU;
        this.subTotal = subTotal;
    }

}
