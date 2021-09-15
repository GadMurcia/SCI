/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.Misc;

/**
 *
 * @author delsas
 */
@Local
public interface MiscFacadeLocal {

    void create(Misc misc);

    void edit(Misc misc);

    void remove(Misc misc);

    Misc find(Object id);

    List<Misc> findAll();

    List<Misc> findRange(int[] range);

    int count();
    
}
