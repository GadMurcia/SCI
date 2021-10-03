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
public class homeCtr implements Serializable {

    private Optional<Usuario> user;
    private String p0, p1;

    @EJB
    private UsuarioFacadeLocal ufl;

    @PostConstruct
    public void init() {
        p0 = "";
        p1 = "";
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

    public void cambioPass() {
        FacesMessage ms;
        String id;
        if (user.get().getPasswd().equals(DigestUtils.md5Hex(p0))) {
            user.get().setPasswd(DigestUtils.md5Hex(p1));
            ufl.edit(user.get());
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cambio efectuado",
                    "Su contraseña ha sido reestablecida con éxito. "
                    + "Esta nueva contraseña es la que usará para su siguiente acceso al sistema.");
            PrimeFaces.current().executeInitScript("PF('contraDialog').hide();");
            id = "form0:msgs";
            p0 = "";
            p1 = "";
        } else {
            ms = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cambio Rechazado",
                    "Su contraseña no se cambió. Verifique que "
                    + "haya proporcionado la contraseña actual correcta.");
            id = "h:messages";
        }
        FacesContext.getCurrentInstance().addMessage(id, ms);
        PrimeFaces.current().ajax().update(id);
    }

    public String getP0() {
        return p0;
    }

    public void setP0(String p0) {
        this.p0 = p0;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getNombreEmpresa() {
        return !user.isPresent()
                ? ""
                : (!user.get().getMiscList().isEmpty()
                ? "PROPIETARIO"
                : (user.get().getEmpresa() != null
                ? user.get().getEmpresa().getNombre()
                : "SISTEMA"));
    }

    public boolean getVerMenu() {
        return user.isPresent() && user.get().getTipoUsuario().getIdTipoUsuario() < 4;
    }

}
