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
import net.delsas.inventarios.entities.Libroventas;

/**
 *
 * @author delsas
 */
@Stateless
public class LibroventasFacade extends AbstractFacade<Libroventas> implements LibroventasFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LibroventasFacade() {
        super(Libroventas.class);
    }

    @Override
    public Libroventas findByIdProducto(Integer idProducto) {
        List<Libroventas> r = em.createNamedQuery("Libroventas.findByIdProducto")
                .setParameter("idProducto", idProducto)
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

}
