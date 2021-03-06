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
import net.delsas.inventarios.beans.TipoUsuarioFacadeLocal;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.TipoUsuario;
import net.delsas.inventarios.entities.Usuario;
import org.apache.commons.codec.digest.DigestUtils;
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
    private Misc matSel;
    private List<Misc> sucursales;
    private Misc sucSel;
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
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado",
                                    "Usted no est?? autorizado para ver esa funcionalidad."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                switch (u.getTipoUsuario().getIdTipoUsuario()) {
                    case 1:
                        tiposU = tufl.findAll();
                        break;
                    case 2:
                        tufl.findAll().stream().filter(t -> !t.getIdTipoUsuario().equals(1)).forEachOrdered(tiposU::add);
                        break;
                    case 3:
                        tufl.findAll().stream().filter(t -> !t.getIdTipoUsuario().equals(1)
                                && !t.getIdTipoUsuario().equals(2)).forEachOrdered(tiposU::add);
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
        usuarios = ufl.findAll();
        us.ifPresent(u -> {
            if (!u.getTipoUsuario().getIdTipoUsuario().equals(1)) {
                usuarios.removeAll(usuarios.stream().filter(p -> p.getTipoUsuario().getIdTipoUsuario().equals(1)).collect(Collectors.toList()));
            }
        });
        return usuarios;
    }

    public List<TipoUsuario> getTiposU() {
        return tiposU;
    }

    public void setTiposU(List<TipoUsuario> tiposU) {
        this.tiposU = tiposU;
    }

    public List<Misc> getMatrices() {
        matrices.clear();
        us.ifPresent(u -> {
            switch (u.getTipoUsuario().getIdTipoUsuario()) {
                case 1:
                    mfl.findAll().stream().filter(m -> m.getMatriz() == null).forEachOrdered(matrices::add);
                    break;
                case 2:
                    matrices.addAll(us.get().getMiscList());
                    break;
                case 3:
                    Misc mat = us.get().getEmpresa().getMatriz() == null
                            ? us.get().getEmpresa()
                            : us.get().getEmpresa().getMatriz();
                    matrices.add(mat);
            }
        });
        return matrices;
    }

    public List<Misc> getSucursales() {
        sucursales.clear();
        Optional.ofNullable(matSel).ifPresent(m -> {
            switch (us.get().getTipoUsuario().getIdTipoUsuario()) {
                case 1:
                case 2:
                    sucursales.add(m);
                    sucursales.addAll(m.getMiscList());
                    break;
                case 3:
                    sucursales.add(m);
            }
        });
        return sucursales;
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
        if (!validarSNumero(nus.getNombres()) || !validarSNumero(nus.getApellidos())) {
            ms = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datos inv??lidos",
                    "Los nombres y apellidos de las personas no deben contener n??meros.");
        } else if (nus.getIdUsuario().isEmpty() || nus.getIdUsuario().split("").length < 4) {
            ms = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datos inv??lidos",
                    "El ID del usuario debe ser de almenos 4 caracteres. No se agrega.");
        } else {
            if (!isEditId()) {
                ufl.edit(nus);
                ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Editado", "Los datos han sido aguardados");
            } else {
                nus.setIdUsuario(nus.getIdUsuario().toLowerCase());
                if (ufl.find(nus.getIdUsuario()) == null) {
                    nus.setActivo(true);
                    nus.setPasswd(DigestUtils.md5Hex(nus.getIdUsuario()));
                    nus.setEmpresa(mfl.find(1));
                    ufl.create(nus);
                    ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Nuevo",
                            "El nuevo usuario ha sido agregado al sistema");
                    PrimeFaces.current().executeScript("PF('userDialog').hide()");
                } else {
                    ms = new FacesMessage(FacesMessage.SEVERITY_FATAL, "No se agrega",
                            "Este ID de usuario ya est?? registrado. Rectifique");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public void resetPass() {
        FacesMessage ms;
        nus.setPasswd(DigestUtils.md5Hex(nus.getIdUsuario()));
        ufl.edit(nus);
        ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Editado",
                "La contrase??a del usuario ha sido reestablecida con ??xito");
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
        nus = new Usuario("", "", "", "", true);
        setEditable(true);
        setEditId(true);
    }

    public Misc getMatSel() {
        return matSel;
    }

    public void setMatSel(Misc matSel) {
        this.matSel = matSel;
    }

    public Misc getSucSel() {
        return sucSel;
    }

    public void setSucSel(Misc sucSel) {
        this.sucSel = sucSel;
    }

    private boolean validarSNumero(String nombres) {
        if (nombres != null) {
            for (String g : nombres.split("")) {
                switch (g) {
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                        return false;
                    default:
                }
            }
        }
        return true;
    }

}
