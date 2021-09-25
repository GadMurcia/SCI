/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.Libroventas;

/**
 *
 * @author delsas
 */
@Local
public interface LibroventasFacadeLocal {

    void create(Libroventas libroventas);

    void edit(Libroventas libroventas);

    void remove(Libroventas libroventas);

    Libroventas find(Object id);

    List<Libroventas> findAll();

    List<Libroventas> findRange(int[] range);

    int count();
    
}
