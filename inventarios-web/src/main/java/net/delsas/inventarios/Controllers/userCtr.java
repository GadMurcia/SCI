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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.TipoUsuarioFacadeLocal;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.TipoUsuario;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class userCtr implements Serializable {

    private Optional<Usuario> us;
    private Usuario nus;
    private List<Usuario> usuarios;
    private List<TipoUsuario> tiposU;
    private List<Misc> matrices;
    private List<Misc> sucursales;
    private boolean editable;
    private boolean editId;

    @EJB
    private UsuarioFacadeLocal ufl;
    @EJB
    private TipoUsuarioFacadeLocal tufl;
    @EJB
    private MiscFacadeLocal mfl;

    @PostConstruct
    public void init() {
        nus = new Usuario();
        usuarios = new ArrayList<>();
        tiposU = new ArrayList<>();
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
        us = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        us.ifPresent(u -> {
            if (u.getTipoUsuario().getIdTipoUsuario() > 3) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso fallido",
                                    "Usted no está autorizado para ver esa funcionalidad. Loguéese."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                switch (u.getTipoUsuario().getIdTipoUsuario()) {
                    case 1:
                        tiposU = tufl.findAll();
                        mfl.findAll().stream().filter(m -> m.getMatriz() == null).forEachOrdered(matrices::add);
                        usuarios = ufl.findAll();
                        break;
                    case 2:
                        tufl.findAll().stream().filter(t -> !t.getIdTipoUsuario().equals(1)).forEachOrdered(tiposU::add);
                        mfl.findAll().stream().filter(m -> m.getMatriz() == null
                                && m.getPropietario().getIdUsuario().equals(u.getIdUsuario())).forEachOrdered(matrices::add);
                        matrices.forEach(m -> {
                            usuarios.addAll(m.getUsuarioList());
                            m.getSucursales().forEach(s -> usuarios.addAll(s.getUsuarioList()));
                        });
                        break;
                    case 3:
                        tufl.findAll().stream().filter(t -> !t.getIdTipoUsuario().equals(1)
                                && !t.getIdTipoUsuario().equals(2)).forEachOrdered(tiposU::add);
                        matrices.add(u.getEmpresa().getMatriz() == null ? u.getEmpresa() : u.getEmpresa().getMatriz());
                        if (u.getEmpresa().getMatriz() != null) {
                            sucursales.add(u.getEmpresa());
                        }
                        usuarios.addAll(u.getEmpresa().getUsuarioList());
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public Usuario getNus() {
        return nus;
    }

    public void setNus(Usuario nus) {
        this.nus = nus;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<TipoUsuario> getTiposU() {
        return tiposU;
    }

    public void setTiposU(List<TipoUsuario> tiposU) {
        this.tiposU = tiposU;
    }

    public List<Misc> getMatrices() {
        return matrices;
    }

    public void setMatrices(List<Misc> matrices) {
        this.matrices = matrices;
    }

    public List<Misc> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Misc> sucursales) {
        this.sucursales = sucursales;
    }

    public void Selecion(SelectEvent e) {
        System.out.println(e.getObject());
    }

    public List<Misc> getEmpresas() {
        List<Misc> v = new ArrayList<>();
        v.addAll(matrices);
        v.addAll(sucursales);
        return v;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void persist() {
        FacesMessage ms;
        if (isEditable()) {
            ufl.edit(nus);
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Editado", "Los datos han sido aguardados");
        } else {
            ufl.create(nus);
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Nuevo", "El nuevo usuario ha sido agregado al sistema");
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public boolean isEditId() {
        return editId;
    }

    public void setEditId(boolean editId) {
        this.editId = editId;
    }

    public void nuevo() {
        nus = new Usuario("", "", "", "");
        setEditable(true);
        setEditId(true);
    }

}
