/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Controllers;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.TipoUsuarioFacadeLocal;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.TipoUsuario;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author admin
 */
@ViewScoped
@Named("dtTiendaCtr")
public class tiendaCtr implements Serializable {

    private Optional<Usuario> us;
    private Misc nmisc;
    private List<Misc> tiendas;
    private List<Usuario> usuarios;
   
    private List<TipoUsuario> tiposU;
    @EJB
    private UsuarioFacadeLocal ufl;

    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private TipoUsuarioFacadeLocal tufl;

    @PostConstruct
    public void init() {

        nmisc = new Misc();
        usuarios = new ArrayList<>();

        tiposU = new ArrayList<>();
        us = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        us.ifPresent(u -> {
            if (u.getTipoUsuario().getIdTipoUsuario() > 2) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso fallido",
                                    "Usted no está autorizado para ver esa funcionalidad. Loguéese."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                nmisc = Optional.ofNullable((Misc) mfl.find(1)).orElseGet(() -> new Misc());
                ufl.findAll().stream().filter(t -> t.getTipoUsuario().getIdTipoUsuario().equals(2)).forEachOrdered(usuarios::add);
            }

        });
    }

    public void onRowEdit(RowEditEvent<Misc> event) {
        mfl.edit(event.getObject());
        FacesMessage msg = new FacesMessage("Tienda editada", event.getObject().getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void onRowCancel(RowEditEvent<Misc> event) {
        nmisc = mfl.find(1);
        FacesMessage msg = new FacesMessage("Se canceló la operación", event.getObject().getNombre());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public void onSelect(SelectEvent<Misc> e){
        System.out.println(e.getObject().toString());
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        if (newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public Misc getNmisc() {
        return nmisc;
    }

    public void setNmisc(Misc nmisc) {
        this.nmisc = nmisc;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Misc> getEmpresas() {
        List<Misc> listaMisc = new ArrayList<>();
        listaMisc.add(nmisc);
        return listaMisc;
    }

}
