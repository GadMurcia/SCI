/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.PrimeFaces;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class homeCtr implements Serializable {

    private Optional<Usuario> user;

    @PostConstruct
    public void init() {
        user = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        if (!user.isPresent()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso fallido",
                                "Usted no está autorizado para ver esa funcionalidad. Loguéese."));
                FacesContext.getCurrentInstance().getExternalContext().redirect("./../");
            } catch (IOException ex) {
                Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Optional.ofNullable((FacesMessage) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("msg")).ifPresent(ms -> {
                FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
                PrimeFaces.current().ajax().update("form0:msgs");
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("msg");
            });
        }
    }

    public String getNombreUs() {
        return !user.isPresent() ? "" : user.get().getNombres() + " " + user.get().getApellidos();
    }

    public void CerrarSesion() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("./../");
        } catch (IOException ex) {
            Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
