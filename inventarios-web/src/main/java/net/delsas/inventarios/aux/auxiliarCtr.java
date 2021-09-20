/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.aux;

import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author delsas
 */
@RequestScoped
@Named
public class auxiliarCtr implements Serializable {

    public void onBlour(AjaxBehaviorEvent e) {
    }
    
    public void onCellEdit(CellEditEvent e) {
        System.out.println("V: "+e.getOldValue().toString());
        System.out.println("N: "+e.getNewValue().toString());
        System.out.println("R: "+e.getRowIndex());
    }
}
