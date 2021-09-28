/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.optional;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
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
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;

    @EJB
    private InventarioFacadeLocal ifl;

    public void onBlour(AjaxBehaviorEvent e) {
    }

    public void onCellEdit(CellEditEvent e) {
        System.out.println("V: " + e.getOldValue().toString());
        System.out.println("N: " + e.getNewValue().toString());
        System.out.println("R: " + e.getRowIndex());
    }

    public double disponibilidad(Inventario i) {
        if (i != null) {
            Existencias e = new Existencias(i, dcfl, dvfl);
            return e.getExistencias();
        }
        return 0;
    }

    public double redondeo2decimales(double o) {
        o = o * 100;
        o = Math.round(o);
        o = o / 100;
        return o;
    }

    public String getDateToString(Date d) {
        return d != null ? new SimpleDateFormat("dd/MM/yyyy").format(d) : "";
    }
}
