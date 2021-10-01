/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.GiroDeCajaFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class reportes2Ctr extends auxiliarCtr implements Serializable {

    private List<GiroDeCaja> giros;
    private Date inicio;
    private Date fin;
    private boolean periodo;

    @EJB
    private GiroDeCajaFacadeLocal gcfl;
    @EJB
    private VentasFacadeLocal vfl;

    @PostConstruct
    public void init() {
        giros = new ArrayList<>();
    }

    public boolean isPeriodo() {
        return periodo;
    }

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

    public List<GiroDeCaja> getGiros() {
        return giros;
    }

    public String getEstadoCaja(GiroDeCaja g) {
        return g == null ? "" : (g.getFaltantes() < 0 ? "Faltante" : "Excedente") + " de caja";
    }

    public void limpiarFechas() {
        inicio = null;
        fin = null;
        giros.clear();
        //generarReporteVentas();
    }

    public double getUtilidad(GiroDeCaja g) {
        return g == null ? 0 : g.getExcedentes() + g.getFaltantes() - g.getRetiros();
    }

    public void generarReporteVentas() {
        if (inicio != null) {
            periodo = !(fin == null || fin.equals(inicio));
            try {
                fin = fin == null || fin.before(inicio) ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(inicio) + " 23:59:59") : fin;
            } catch (ParseException ex) {
                System.out.println("Un error ha ocurrido al procesar la fecha de fin. reporte2Crt");
                fin = new Date();
            }
            giros = gcfl.findByPeriodoYSucursal(inicio, fin, 1);

        } else {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Adventencia",
                            "Para generar un reporte debe seleccionar, al menos, una fecha de inicio del reporte."));
            PrimeFaces.current().ajax().update("form0:msgs");
        }
    }

}
