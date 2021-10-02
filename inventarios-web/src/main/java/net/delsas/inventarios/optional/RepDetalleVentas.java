/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author delsas
 */
public class RepDetalleVentas implements Serializable {
    
    private Integer id;
    private List<ReporteVentas> detalle;

    public RepDetalleVentas() {
    }

    public RepDetalleVentas(Integer id, List<ReporteVentas> detalle) {
        this.id = id;
        this.detalle = detalle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ReporteVentas> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<ReporteVentas> detalle) {
        this.detalle = detalle;
    }
    
    
}
