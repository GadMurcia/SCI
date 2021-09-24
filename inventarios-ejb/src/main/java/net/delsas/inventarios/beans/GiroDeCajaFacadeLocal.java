/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.GiroDeCaja;

/**
 *
 * @author delsas
 */
@Local
public interface GiroDeCajaFacadeLocal {

    void create(GiroDeCaja giroDeCaja);

    void edit(GiroDeCaja giroDeCaja);

    void remove(GiroDeCaja giroDeCaja);

    GiroDeCaja find(Object id);

    List<GiroDeCaja> findAll();

    List<GiroDeCaja> findRange(int[] range);

    int count();
    
}
