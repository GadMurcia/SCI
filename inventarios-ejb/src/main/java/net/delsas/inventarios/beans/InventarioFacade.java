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
import net.delsas.inventarios.entities.Inventario;

/**
 *
 * @author delsas
 */
@Stateless
public class InventarioFacade extends AbstractFacade<Inventario> implements InventarioFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InventarioFacade() {
        super(Inventario.class);
    }

    @Override
    public Inventario find(String nombre) {
        List<Inventario> r = em.createNamedQuery("Inventario.findByProducto")
                .setParameter("producto", nombre)
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public List<Inventario> findByTienda(int idTienda) {
        return em.createNamedQuery("Inventario.findByTienda")
                .setParameter("idTienda", idTienda)
                .getResultList();
    }

}
