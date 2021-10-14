/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import net.delsas.inventarios.beans.GiroDeCajaFacadeLocal;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;

/**
 *
 * @author delsas
 */
@SessionScoped
@Named
public class cajaCtr extends auxiliarCtr implements Serializable {

    private GiroDeCaja giro;
    private List<GiroDeCaja> historial;
    private Optional<Usuario> us;
    private boolean iniciada;
    private String Descr, infoCD;
    private BigDecimal valor;

    @EJB
    private GiroDeCajaFacadeLocal gdcfl;

    @PostConstruct
    public void init() {
        giro = new GiroDeCaja();
        historial = new ArrayList<>();
        us = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        if (!us.isPresent()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso fallido",
                                "Usted no está autorizado para ver esa funcionalidad. Loguéese."));
                FacesContext.getCurrentInstance().getExternalContext().redirect("./../");
            } catch (IOException ex) {
                Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            giro = Optional.ofNullable(gdcfl.findNoTerminadas(us.get().getIdUsuario()))
                    .orElseGet(() -> new GiroDeCaja(null, null, null, 0, 0, 0, 0, 0));
            giro.setResponsable(us.get());
            giro.setDetalleRetiros("");
            iniciada = giro.getIdGiroDeCaja() != null;
            historial = gdcfl.findTerminadas(us.get().getIdUsuario(), CalcularFecha(new Date(), Calendar.MONTH, -1), new Date());
        }
    }

    public void abrirGiro() {
        giro.setInicio(new Date());
        gdcfl.create(giro);
        iniciada = true;
        PrimeFaces.current().ajax().update("formVenta");
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage("Apertura de caja",
                        "Se ha creado con exito el registro de la apertura de su caja. "
                        + "Ticket: " + giro.getIdGiroDeCaja()));        
        infoCD = "";
    }

    public void modificarCaja() {
        gdcfl.edit(giro);
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage("Registro de Pagos a proveedores",
                        "Se ha registrado con éxito una pago a proveedor. "
                        + "Ticket : " + giro.getIdGiroDeCaja()));
    }

    public void cerrarCaja() {
        giro.setFin(new Date());
        giro.setFaltantes(redondeo2decimales(giro.getCierre() - (getVentas() - giro.getRetiros() + giro.getCajaInicial())));
        gdcfl.edit(giro);
        PrimeFaces.current().ajax().update("formVenta");
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage("Cierre de caja",
                        "Su caja ha cerrado. "
                        + "Ticket: " + giro.getIdGiroDeCaja()));
        if (giro.getFaltantes() != 0) {
            infoCD = "La caja ha cerrado con un " + (giro.getFaltantes() > 0 ? "excedente" : "faltante") + " de $" + giro.getFaltantes() + " dólares.";
            PrimeFaces.current().ajax().update("h5");
            PrimeFaces.current().executeScript("PF('infoCD').show();");
        }
        init();
    }

    public GiroDeCaja getGiro() {
        return giro;
    }

    public void setGiro(GiroDeCaja giro) {
        this.giro = giro;
    }

    public boolean isIniciada() {
        return iniciada;
    }

    public void setIniciada(boolean iniciada) {
        this.iniciada = iniciada;
    }

    public String getDescr() {
        return Descr;
    }

    public void setDescr(String Descr) {
        this.Descr = Descr;
    }

    public BigDecimal getValor() {
        valor = Optional.ofNullable(valor).orElseGet(() -> BigDecimal.ZERO);
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public double getVentas() {
        giro.setExcedentes(gdcfl.findVentas(giro.getIdGiroDeCaja()).stream().map(Ventas::getValor).reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO).doubleValue());
        return giro.getExcedentes();
    }

    public void registrarRetiro() {
        giro.setRetiros(giro.getRetiros() + this.valor.doubleValue());
        String t = Optional.ofNullable(giro.getDetalleRetiros()).orElseGet(() -> "");
        giro.setDetalleRetiros((t.isEmpty() ? "" : t + "\n") + (valor.doubleValue() + "   ->  " + Descr + ""));
        gdcfl.edit(giro);
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage("Retiro",
                        "El retiro para " + Descr + " ha quedado registrado."));
        Descr = "";
        valor = BigDecimal.ZERO;
    }

    public List<String> getRetiros() {
        List<String> r = new ArrayList<>();
        String rr = Optional.ofNullable(giro.getDetalleRetiros()).orElseGet(() -> "");
        for (String r0 : rr.split("\n")) {
            if (!r0.isEmpty()) {
                r.add(r0);
            }
        }
        return r;
    }

    public boolean isAdmin() {
        return !us.isPresent() ? false : us.get().getTipoUsuario().getIdTipoUsuario() < 4;
    }

    public List<GiroDeCaja> getHistorial() {
        return historial;
    }

    public String getInfoCD() {
        return infoCD;
    }

    public String getEstadoCaja(double d) {
        return d < 0 ? "F " : "OK";
    }

    public boolean isVerEstadoCjaj(double d) {
        return d < 0;
    }

}
