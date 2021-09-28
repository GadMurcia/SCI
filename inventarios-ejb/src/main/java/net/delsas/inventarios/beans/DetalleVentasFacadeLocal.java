/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.DetalleVentas;

/**
 *
 * @author delsas
 */
@Local
public interface DetalleVentasFacadeLocal {

    void create(DetalleVentas detalleVentas);

    void edit(DetalleVentas detalleVentas);

    void remove(DetalleVentas detalleVentas);

    DetalleVentas find(Object id);

    List<DetalleVentas> findAll();

    List<DetalleVentas> findRange(int[] range);

    int count();
    
    public List<DetalleVentas> findByProducto(Integer idProducto);
}
