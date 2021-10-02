/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import java.io.Serializable;
import java.util.Date;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.optional.auxiliarCtr;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class reportes3Ctr extends auxiliarCtr implements Serializable {

    private Date inicio;
    private Date fin;

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }
    
    

    public void limpiarFechas() {

    }

    public void generarReporteVentas() {

    }

}
