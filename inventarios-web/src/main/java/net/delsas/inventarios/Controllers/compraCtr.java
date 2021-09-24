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
import java.util.Date;
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
import net.delsas.inventarios.beans.ComprasFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.entities.Compras;
import net.delsas.inventarios.entities.ComprasPK;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleCompraPK;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class compraCtr implements Serializable {

    private Compras nCompra;
    private DetalleCompra ndCompra;
    private List<DetalleCompra> detalles;
    private List<DetalleCompra> selected;
    private Optional<Usuario> user;
    private Inventario invSel;
    private boolean comprando;
    public static List<Inventario> inventarios;
    private List<Misc> matrices;
    private Misc matSel;
    private List<Misc> sucursales;
    private Misc sucSel;

    @EJB
    private ComprasFacadeLocal cfl;
    @EJB
    private InventarioFacadeLocal ifl;
    @EJB
    private MiscFacadeLocal mfl;

    @PostConstruct
    public void init() {
        comprando = false;
        nCompra = new Compras();
        ndCompra = new DetalleCompra();
        inventarios = new ArrayList<>();
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
        detalles = new ArrayList<>();
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

        }
    }

    public void nuevaCompra() {
        nCompra = new Compras(new ComprasPK(new Date(), user.get().getIdUsuario()), BigDecimal.ZERO);
        nCompra.setDetalleCompraList(new ArrayList<>());
        nCompra.setUsuario1(user.get());
        detalles.clear();
        nuevoDetalleCompras();
    }

    public void nuevoDetalleCompras() {
        ndCompra = new DetalleCompra(new DetalleCompraPK(nCompra.getComprasPK().getIdCompras(),
                nCompra.getComprasPK().getUsuario(), 0), 0, BigDecimal.ZERO);
    }

    public void agregarDetalleCompra() {
        FacesMessage ms;
        if (ndCompra.getDetalleCompraPK().getProducto() == 0) {
            //no ha seleccionado producto
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay producto seleccionado",
                    "Antes de continuar debe seleccionar un producto del catálogo.");
        } else if (ndCompra.getCantidad() == 0) {
            //no ha establecido la cantidad
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "No ha ingresado una cantidad válida",
                    "Antes de continuar debe indicar una cantidad de producto que se agregarán al inventario.");
        } else if (ndCompra.getCostoUnitario().doubleValue() <= 0) {
            //no ha establecido bien le costo unitario
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "Costo unitario del producto no es válido",
                    "El costo por unidad del producto seleccionado no puede ser cero (0) o un número negativo.");
        } else if (!detalles.stream().filter(dc -> dc.getInventario().equals(ndCompra.getInventario()))
                .collect(Collectors.toList()).isEmpty()) {
            //el producto ya se ha agregado a la compra
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "El producto duplicado",
                    "El producto \"" + ndCompra.getInventario().getProducto() + "\" ya está en la lista de compras. "
                    + "Sólo se puede agregar una vez en cada factura. ");
        } else {
            nCompra.setValor(nCompra.getValor().add(ndCompra.getCostoUnitario().multiply(new BigDecimal(ndCompra.getCantidad()))));
            ndCompra.setCompras(nCompra);
            detalles.add(ndCompra);
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto agregado",
                    ndCompra.getCantidad() + " unidades del producto "
                    + ndCompra.getInventario().getProducto() + " se han agregado a la lista de compras.");
            nuevoDetalleCompras();
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public void quitarDetalleCompra() {
        selected.forEach(s -> {
            nCompra.setValor(nCompra.getValor().add(s.getCostoUnitario().multiply(new BigDecimal(s.getCantidad())).negate()));
            detalles.remove(s);
            nuevoDetalleCompras();
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminación",
                            "El producto \"" + s.getInventario().getProducto()
                            + "\" ha sido quitado de la lista de compras."));
            PrimeFaces.current().ajax().update("form0:msgs");
        });
        selected.clear();
    }

    public List<Inventario> completeInventario(String query) {
        return getInventario().stream()
                .filter(i -> i.getProducto().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void onItemSelect(SelectEvent<Inventario> event) {
        invSel = event.getObject();
        ndCompra.setInventario(invSel);
        ndCompra.getDetalleCompraPK().setProducto(invSel.getIdInventario());
    }

    public void guardarCompra() {
        FacesMessage ms;
        if (detalles.size() > 0) {
            nCompra.setDetalleCompraList(detalles);
            cfl.create(nCompra);
            comprando = false;
            nuevaCompra();
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Compra guardada",
                    "Los datos de esta compra han sido guardados con éxito.");
        } else {
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error",
                    "Para poder registrar una compra se debe detallar "
                    + "al menos un producto comprado en la factura.");
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public Compras getNueCompra() {
        return nCompra;
    }

    public void setNueCompra(Compras nCompra) {
        this.nCompra = nCompra;
    }

    public DetalleCompra getNdCompra() {
        return ndCompra;
    }

    public void setNdCompra(DetalleCompra ndCompra) {
        this.ndCompra = ndCompra;
    }

    public Inventario getInvSel() {
        return invSel;
    }

    public void setInvSel(Inventario invSel) {
        this.invSel = invSel;
    }

    public boolean isComprando() {
        return comprando && sucSel != null;
    }

    public void setComprando(boolean comprando) {
        this.comprando = comprando;
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Session de compras",
                        "Se ha "+(this.comprando?"iniciado una nueva ":"finalizado la ")+"sesión de compras."));
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public List<Misc> getMatrices() {
        matrices.clear();
        user.ifPresent(u -> {
            switch (u.getTipoUsuario().getIdTipoUsuario()) {
                case 1:
                    mfl.findAll().stream().filter(m -> m.getMatriz() == null).forEachOrdered(matrices::add);
                    break;
                case 2:
                    matrices.addAll(mfl.findAll(user.get().getIdUsuario()));
                    break;
                case 3:
                    Misc mat = user.get().getEmpresa().getMatriz() == null
                            ? user.get().getEmpresa()
                            : user.get().getEmpresa().getMatriz();
                    matrices.add(mat);
            }
        });
        matSel = matrices.isEmpty() ? null : matrices.get(0);
        sucSel = null;
        return matrices;
    }

    public List<Misc> getSucursales() {
        sucursales.clear();
        Optional.ofNullable(matSel).ifPresent(m -> {
            switch (user.get().getTipoUsuario().getIdTipoUsuario()) {
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
        inventarios = ifl.findByTienda(Optional.ofNullable(sucSel).orElseGet(() -> new Misc(0)).getIdMisc());
        Collections.sort(inventarios, (Inventario uno, Inventario dos) -> uno.getProducto().toLowerCase().compareTo(dos.getProducto().toLowerCase()));
        return inventarios;
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

    public List<DetalleCompra> getDetalles() {
        return detalles;
    }

    public List<DetalleCompra> getSelected() {
        return selected;
    }

    public void setSelected(List<DetalleCompra> selected) {
        this.selected = this.selected == null ? new ArrayList<>() : this.selected;
    }

    public void onRowSelect(SelectEvent<DetalleCompra> event) {
        DetalleCompra o = event.getObject();
        if (!selected.contains(o)) {
            selected.add(o);
        } else {
            selected.remove(o);
        }
    }

    public void onRowUnselect(UnselectEvent<DetalleCompra> event) {
        DetalleCompra o = event.getObject();
        if (o != null && selected.contains(o)) {
            selected.remove(o);
        }
    }

}
