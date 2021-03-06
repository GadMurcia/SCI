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
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Ventas;

/**
 *
 * @author delsas
 */
@Stateless
public class GiroDeCajaFacade extends AbstractFacade<GiroDeCaja> implements GiroDeCajaFacadeLocal {

    @PersistenceContext(unitName = "net.delsas_inventarios-ejb_ejb_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GiroDeCajaFacade() {
        super(GiroDeCaja.class);
    }

    @Override
    public GiroDeCaja findNoTerminadas(String idUsuario) {
        List<GiroDeCaja> r = em.createNamedQuery("GiroDeCaja.findNoTerminadas")
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    @Override
    public List<Ventas> findVentas(Integer id) {
        return em.createNamedQuery("Ventas.findByGiroCaja")
                .setParameter("giroCaja", id)
                .getResultList();
    }

    @Override
    public List<GiroDeCaja> findByPeriodoYSucursal(Date inicio, Date fin, Integer idSucursal) {
        return em.createNamedQuery("GiroDeCaja.findByPeriodoYSucursal")
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    @Override
    public List<GiroDeCaja> findTerminadas(String idUsuario, Date inicio, Date fin) {
        return em.createNamedQuery("GiroDeCaja.findTerminadas")
                .setParameter("idUsuario", idUsuario)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    @Override
    public List<GiroDeCaja> findTerminadasTienda(Integer idSucursal, Date inicio, Date fin) {
        return em.createNamedQuery("GiroDeCaja.findTerminadasTienda")
                /*.setParameter("idSucursal", idSucursal)*/
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

}
