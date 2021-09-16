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

/**
 *
 * @author delsas
 */
@RequestScoped
@Named
public class auxiliarCtr implements Serializable {

    public void onBlour(AjaxBehaviorEvent e) {
    }
}
