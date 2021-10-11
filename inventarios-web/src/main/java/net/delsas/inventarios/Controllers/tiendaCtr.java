/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author admin
 */
@ViewScoped
@Named
public class tiendaCtr extends auxiliarCtr implements Serializable {

    private Optional<Usuario> us;
    private Misc nmisc;
    private List<Misc> empresas;
    private boolean nueva;

    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private UsuarioFacadeLocal ufl;

    @PostConstruct
    public void init() {
        empresas = new ArrayList<>();
        us = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        us.ifPresent(u -> {
            if (u.getTipoUsuario().getIdTipoUsuario() > 2) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado",
                                    "Usted no está autorizado para ver esa funcionalidad."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
            }

        });
    }

    public List<Misc> getEmpresas() {
        empresas = mfl.findAll();
        if (!us.get().getTipoUsuario().getIdTipoUsuario().equals(1)) {
            empresas.removeAll(empresas.stream().filter(e -> e.getMatriz() == null)
                    .filter(e -> !e.getPropietario().equals(us.get()))
                    .collect(Collectors.toList()));
        }
        return empresas;
    }

    public List<Usuario> getDueños() {
        return ufl.findAll().stream()
                .filter(u -> u.getActivo())
                .filter(u -> u.getTipoUsuario().getIdTipoUsuario().equals(2)
                || u.getTipoUsuario().getIdTipoUsuario().equals(3))
                .collect(Collectors.toList());
    }

    public List<Misc> getMatrices() {
        return getEmpresas().stream()
                .filter(e -> e.getMatriz() == null)
                .collect(Collectors.toList());
    }

    public void nuevaEmpresa() {
        nmisc = new Misc(null, "");
        nueva = true;
    }

    @Override
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            mfl.edit(empresas.get(event.getRowIndex()));
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificaciones realizadas",
                            "Aterior: " + oldValue + ", Actual:" + newValue));
        }
    }

    public void guardar() {
        if (mfl.findAll().stream().filter(m -> m.getNombre().toLowerCase().equals(nmisc.getNombre().toLowerCase())).collect(Collectors.toList()).isEmpty()) {
            System.out.println(nmisc);
        } else {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Nombre repetido",
                            "El nombre de la tienda ingresada debe ser único y ya se ha registrado con anterioridad."));
        }
    }

    public Misc getNmisc() {
        return nmisc;
    }

    public void setNmisc(Misc nmisc) {
        this.nmisc = nmisc;
    }

    public boolean isAdmin() {
        return !us.isPresent() ? false : us.get().getTipoUsuario().getIdTipoUsuario().equals(1);
    }

    public boolean isNueva() {
        return nueva;
    }

}
