/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.delsas.inventarios.entities.Librocompras;

/**
 *
 * @author delsas
 */
@Stateless
public class LibrocomprasFacade extends AbstractFacade<Librocompras> implements LibrocomprasFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LibrocomprasFacade() {
        super(Librocompras.class);
    }
    
}
