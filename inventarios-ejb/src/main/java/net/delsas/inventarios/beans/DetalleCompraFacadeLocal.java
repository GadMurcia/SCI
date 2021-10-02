/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.DetalleCompra;

/**
 *
 * @author delsas
 */
@Local
public interface DetalleCompraFacadeLocal {

    void create(DetalleCompra detalleCompra);

    void edit(DetalleCompra detalleCompra);

    void remove(DetalleCompra detalleCompra);

    DetalleCompra find(Object id);

    List<DetalleCompra> findAll();

    List<DetalleCompra> findRange(int[] range);

    int count();

    public List<DetalleCompra> findByProducto(Integer idProducto);

    public List<DetalleCompra> findByPeriodoFechas(Date inicio, Date fin);

    public List<DetalleCompra> findByProductoAdnPeriodoFechas(Integer idProducto, Date inicio, Date fin);

}
