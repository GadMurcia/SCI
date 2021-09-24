/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Usuario;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class loginCtr implements Serializable {

    private String us;
    private String pas;
    private Optional<Usuario> user = Optional.ofNullable(null);

    @EJB
    private UsuarioFacadeLocal ufl;

    @PostConstruct
    public void init() {
        Optional.ofNullable((FacesMessage) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("msg")).ifPresent(ms -> {
            FacesContext.getCurrentInstance().addMessage("form:msgs", ms);
            PrimeFaces.current().ajax().update("form:msgs");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("msg");
        });
        Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .get("user")).ifPresent(u -> {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("pages/home.app");
            } catch (IOException ex) {
                Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        us = "";
        pas = "";
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public String getPas() {
        return pas;
    }

    public void setPas(String pas) {
        this.pas = pas;
    }

    public String getAñoActual() {
        return new SimpleDateFormat("YYYY").format(new Date());
    }

    public void login() {
        user = Optional.ofNullable(ufl.find(us));
        if (!user.isPresent()) {
            FacesContext.getCurrentInstance().addMessage("form:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
                            "Este usuario no existe. Intente de nuevo"));
        }else if(!user.get().getActivo()){
            FacesContext.getCurrentInstance().addMessage("form:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Usted no está habilitado para entrar al sistema. Consulte con el representante."));
        } else if (DigestUtils.md5Hex(pas).equals(user.get().getPasswd())) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", user.get());
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Acceso",
                                "Bienvenid@ " + user.get().getNombres() + "."));
                FacesContext.getCurrentInstance().getExternalContext().redirect("pages/home.app");
            } catch (IOException ex) {
                Logger.getLogger(loginCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            FacesContext.getCurrentInstance().addMessage("form:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
                            "Las credenciales no son correctas. Intente de nuevo"));
        }
    }

}
