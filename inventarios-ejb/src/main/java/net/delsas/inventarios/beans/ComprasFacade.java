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
import net.delsas.inventarios.entities.Compras;

/**
 *
 * @author delsas
 */
@Stateless
public class ComprasFacade extends AbstractFacade<Compras> implements ComprasFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComprasFacade() {
        super(Compras.class);
    }

    @Override
    public List<Compras> findConFacturaByPeriodo(Date inicio, Date fin) {
        return em.createNamedQuery("Compras.findConFacturaByPeriodo")
                .setParameter("fin", fin)
                .setParameter("inicio", inicio)
                .getResultList();
    }

}
