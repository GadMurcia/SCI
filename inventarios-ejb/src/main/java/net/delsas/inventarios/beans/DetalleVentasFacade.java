/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.VentasPK;

/**
 *
 * @author delsas
 */
@Stateless
public class DetalleVentasFacade extends AbstractFacade<DetalleVentas> implements DetalleVentasFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleVentasFacade() {
        super(DetalleVentas.class);
    }

    @Override
    public List<DetalleVentas> findByProducto(Integer idProducto) {
        return em.createNamedQuery("DetalleVentas.findByProducto")
                .setParameter("producto", idProducto)
                .getResultList();
    }

    @Override
    public List<DetalleVentas> findByVenta(VentasPK ventasPK) {
        return em.createNamedQuery("DetalleVentas.findByIdVentasAndGiro")
                .setParameter("giroCaja", ventasPK.getGiroCaja())
                .setParameter("idVentas", ventasPK.getIdVentas())
                .getResultList();
    }

}
