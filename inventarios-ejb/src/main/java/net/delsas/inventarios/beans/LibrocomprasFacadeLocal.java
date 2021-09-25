/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.beans;

import java.util.List;
import javax.ejb.Local;
import net.delsas.inventarios.entities.Librocompras;

/**
 *
 * @author delsas
 */
@Local
public interface LibrocomprasFacadeLocal {

    void create(Librocompras librocompras);

    void edit(Librocompras librocompras);

    void remove(Librocompras librocompras);

    Librocompras find(Object id);

    List<Librocompras> findAll();

    List<Librocompras> findRange(int[] range);

    int count();

    public Librocompras findByIdProducto(Integer idProducto);
    
}
