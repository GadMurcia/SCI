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
import net.delsas.inventarios.entities.Misc;

/**
 *
 * @author delsas
 */
@Stateless
public class MiscFacade extends AbstractFacade<Misc> implements MiscFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MiscFacade() {
        super(Misc.class);
    }

    @Override
    public List<Misc> findAll(String idUsuario) {
        return em.createNamedQuery("Misc.findByPropietario")
                .setParameter("idUsuario", idUsuario)
                .getResultList();
    }
}
