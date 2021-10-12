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
import java.util.Collections;
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
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class prodCtr extends auxiliarCtr implements Serializable {

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
            if (u.getTipoUsuario().getIdTipoUsuario() > 3) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado",
                                    "Usted no está autorizado para ver esa funcionalidad."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            switch (us.get().getTipoUsuario().getIdTipoUsuario()) {
                case 1:
                case 2:
                    sucursales.add(m);
                    sucursales.addAll(mfl.find(m.getIdMisc()).getMiscList());
                    break;
                case 3:
                    sucursales.add(m);
            }
        });
        sucSel = matSel == null || sucursales.isEmpty() ? null : sucursales.get(0);
        return sucursales;
    }

    public List<Inventario> getInventario() {
        inventarios = ifl.findByTienda(1);
        Collections.sort(inventarios, (Inventario uno, Inventario dos) -> uno.getProducto().toLowerCase().compareTo(dos.getProducto().toLowerCase()));
        return inventarios;
    }

    public Inventario getSel() {
        return sel;
    }

    public void setPreciounitario(String pu) {
        sel.setPrecioUnitario(new BigDecimal(pu));
    }

    public String getPreciounitario() {
        double v = sel == null ? 0 : sel.getPrecioUnitario().doubleValue();
        return (v >= 10 ? "" : "0") + v;
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

    public void nuevo() {
        sel = new Inventario(null, "", BigDecimal.ZERO);
        sel.setDescripcion("");
    }

    public void persist() {
        FacesMessage ms;
        if (sel.getIdInventario() == null) {
            sel.setTienda(mfl.find(1));
            sel.setActivo(true);
            if (!Optional.ofNullable(ifl.find(sel.getProducto())).isPresent()) {
                ifl.create(sel);
                ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregación",
                        "El catálogo de productos se ha modificado. "
                        + "Nombre de producto agregado: " + sel.getProducto());

                PrimeFaces.current().executeInitScript("PF('prodDialog').hide()");
            } else {
                ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "Imposible continuar",
                        "Ya hay un producto agregado al catálogo con el nombre: " + sel.getProducto());
            }
        } else {
            ifl.edit(sel);
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Modificación",
                    "El catálogo de productos se ha modificado. "
                    + "Nombre de producto Modificado: " + sel.getProducto());
            PrimeFaces.current().executeInitScript("PF('prodDialog').hide()");
        }
        Optional.ofNullable(ms).ifPresent(m -> FacesContext.getCurrentInstance().addMessage("form0:msgs", m));
    }
}
