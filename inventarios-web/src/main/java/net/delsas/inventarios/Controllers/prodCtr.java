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
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class prodCtr implements Serializable {

    private Optional<Usuario> us;
    private Inventario sel;
    private List<Inventario> inventarios;
    private List<Misc> matrices;
    private Misc matSel;
    private List<Misc> sucursales;
    private Misc sucSel;

    @EJB
    private InventarioFacadeLocal ifl;
    @EJB
    private MiscFacadeLocal mfl;

    @PostConstruct
    public void init() {
        inventarios = new ArrayList<>();
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
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

            }
        });
    }

    public List<Misc> getMatrices() {
        matrices.clear();
        us.ifPresent(u -> {
            switch (u.getTipoUsuario().getIdTipoUsuario()) {
                case 1:
                    mfl.findAll().stream().filter(m -> m.getMatriz() == null).forEachOrdered(matrices::add);
                    break;
                case 2:
                    matrices.addAll(mfl.findAll(us.get().getIdUsuario()));
                    break;
                case 3:
                    Misc mat = us.get().getEmpresa().getMatriz() == null
                            ? us.get().getEmpresa()
                            : us.get().getEmpresa().getMatriz();
                    matrices.add(mat);
            }
        });
        matSel = matrices.isEmpty() ? null : matrices.get(0);
        return matrices;
    }

    public List<Misc> getSucursales() {
        sucursales.clear();
        Optional.ofNullable(matSel).ifPresent(m -> {
            matSel = m = mfl.find(m.getIdMisc());
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
        sucSel = matSel == null || sucursales.isEmpty() ? null : sucursales.get(0);
        return sucursales;
    }

    public List<Inventario> getInventario() {
        inventarios.clear();
        Optional.ofNullable(sucSel).ifPresent(s -> {
            inventarios = ifl.findByTienda(s.getIdMisc());
        });
        return inventarios;
    }

    public Inventario getSel() {
        return sel;
    }

    public void setSel(Inventario sel) {
        this.sel = sel;
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

    public void onCellEdit(CellEditEvent event) {
        if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) {
            Inventario p = inventarios.get(event.getRowIndex());
            ifl.edit(p);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación",
                            "Los nuevos datos han sido guardados para " + p.getProducto()));
        }
    }

    public void nuevo() {
        sel = new Inventario(null, "", BigDecimal.ZERO);
        sel.setDescripcion("");
    }

    public void persist() {
        if (Optional.ofNullable(sucSel).isPresent()) {
            FacesMessage ms;
            sel.setTienda(sucSel);
            sel.setActivo(true);
            if (!Optional.ofNullable(ifl.find(sel.getProducto())).isPresent()) {
                ifl.create(sel);
                ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregación",
                        "El nuevo producto se ha agregado al catálogo. Nombre: " + sel.getProducto());
                PrimeFaces.current().executeInitScript("PF('prodDialog').hide()");
            } else {
                ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "Imposible continuar",
                        "Ya hay un producto agregado al catálogo con el nombre: " + sel.getProducto());
            }
            Optional.ofNullable(ms).ifPresent(m -> FacesContext.getCurrentInstance().addMessage("form0:msgs", m));
        } else {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error",
                            "No se puede agregar el producto al catálogo porque no se seleccionó ninguna tienda."));
        }
    }
}
