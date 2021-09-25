/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import net.delsas.inventarios.beans.LibrocomprasFacadeLocal;
import net.delsas.inventarios.beans.LibroventasFacadeLocal;
import net.delsas.inventarios.entities.Inventario;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author delsas
 */
@RequestScoped
@Named
public class auxiliarCtr implements Serializable {

    @EJB
    private LibrocomprasFacadeLocal lcfl;
    @EJB
    private LibroventasFacadeLocal lvfl;

    public void onBlour(AjaxBehaviorEvent e) {
    }

    public void onCellEdit(CellEditEvent e) {
        System.out.println("V: " + e.getOldValue().toString());
        System.out.println("N: " + e.getNewValue().toString());
        System.out.println("R: " + e.getRowIndex());
    }

    public double disponibilidad(Inventario i) {
        if (i != null) {
            //getLibrosActualizados(i);
            double compra = i.getLibroCompras() == null ? 0
                    : i.getLibroCompras().getCompras().doubleValue();
            double ventas = i.getLibroVentas() == null ? 0
                    : i.getLibroVentas().getVentas();
            return compra - ventas;
        }
        return 0;
    }

    public void getLibrosActualizados(Inventario i) {
        System.out.println("Controladores:\n"+lcfl+"\n"+lvfl);
        i.setLibroCompras(lcfl.find(i.getIdInventario()));
        i.setLibroVentas(lvfl.find(i.getIdInventario()));
    }
}
