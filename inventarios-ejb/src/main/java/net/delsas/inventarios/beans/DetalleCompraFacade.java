/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.delsas.inventarios.entities.DetalleCompra;

/**
 *
 * @author delsas
 */
@Stateless
public class DetalleCompraFacade extends AbstractFacade<DetalleCompra> implements DetalleCompraFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetalleCompraFacade() {
        super(DetalleCompra.class);
    }

    @Override
    public List<DetalleCompra> findByProducto(Integer odProducto) {
        return em.createNamedQuery("DetalleCompra.findByProducto")
                .setParameter("producto", odProducto)
                .getResultList();
    }

    @Override
    public List<DetalleCompra> findByPeriodoFechas(Date inicio, Date fin) {
        return em.createNamedQuery("DetalleCompra.findByPeriodoFechas")
                .setParameter("fin", fin)
                .setParameter("inicio", inicio)
                .getResultList();
    }

}
