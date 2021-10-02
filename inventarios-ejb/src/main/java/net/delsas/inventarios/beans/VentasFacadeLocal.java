/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.Ventas;

/**
 *
 * @author delsas
 */
@Local
public interface VentasFacadeLocal {

    void create(Ventas ventas);

    void edit(Ventas ventas);

    void remove(Ventas ventas);

    Ventas find(Object id);

    List<Ventas> findAll();

    List<Ventas> findRange(int[] range);

    int count();

    List<Ventas> findByGiroCaja(Integer idGiroDeCaja);

    List<Ventas> findByPeriodoFechas(Date i, Date f);

}
