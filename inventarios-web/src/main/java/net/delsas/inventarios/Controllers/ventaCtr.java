/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.DetalleVentasPK;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;
import net.delsas.inventarios.entities.VentasPK;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class ventaCtr extends auxiliarCtr implements Serializable{

    private Optional<Usuario> user;
    private Inventario invSel;
    public static List<Inventario> inventarios;
    private List<Misc> matrices;
    private Misc matSel;
    private List<Misc> sucursales;
    private Misc sucSel;
    private Ventas nuevaVenta;
    private List<DetalleVentas> selected;
    private DetalleVentas nuevoDetalleV;
    private boolean vendiendo;

    @EJB
    private InventarioFacadeLocal ifl;
    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private VentasFacadeLocal vfl;

    @PostConstruct
    public void init() {
        inventarios = new ArrayList<>();
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
        nuevaVenta = new Ventas();
        user = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
        if (!user.isPresent()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso fallido",
                                "Usted no est?? autorizado para ver esa funcionalidad. Logu??ese."));
                FacesContext.getCurrentInstance().getExternalContext().redirect("./../");
            } catch (IOException ex) {
                Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

        }
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
        inventarios = ifl.findByTienda(1);
        Collections.sort(inventarios, (Inventario uno, Inventario dos) -> uno.getProducto().toLowerCase().compareTo(dos.getProducto().toLowerCase()));
        return inventarios;
    }

    public void onItemSelect(SelectEvent<Inventario> event) {
            invSel = event.getObject();
            nuevoDetalleV.setInventario(invSel);
            nuevoDetalleV.getDetalleVentasPK().setProducto(invSel.getIdInventario());
            nuevoDetalleV.setPrecioUnitario(invSel.getPrecioUnitario());
    }

    public Inventario getInvSel() {
        return invSel;
    }

    public void setInvSel(Inventario invSel) {
        this.invSel = invSel;
    }

    public Ventas getNuevaVenta() {
        return nuevaVenta;
    }

    public void setNuevaVenta(Ventas nuevaVenta) {
        this.nuevaVenta = nuevaVenta;
    }

    public List<DetalleVentas> getSelected() {
        return selected;
    }

    public void setSelected(List<DetalleVentas> selected) {
        this.selected = this.selected == null ? new ArrayList<>() : this.selected;
    }

    public DetalleVentas getNuevoDetalleV() {
        return nuevoDetalleV;
    }

    public void setNuevoDetalleV(DetalleVentas nuevoDetalleV) {
        this.nuevoDetalleV = nuevoDetalleV;
    }

    public String getNombreVendedor() {
        return nuevaVenta.getGiroDeCaja() == null ? ""
                : nuevaVenta.getGiroDeCaja().getResponsable().getNombres() + " "
                + nuevaVenta.getGiroDeCaja().getResponsable().getApellidos();
    }

    public boolean isVendiendo() {
        return vendiendo;
    }

    public void setVendiendo(boolean vendiendo) {
        this.vendiendo = vendiendo;
        FacesContext.getCurrentInstance().addMessage("form0:msgs",
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Session de ventas",
                        "Se ha " + (this.vendiendo ? "iniciado una nueva " : "finalizado la ") + "sesi??n de ventas."));
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public List<Inventario> completeInventario(String query) {
        return getInventario().stream()
                .filter(Inventario::isActivo)
                .filter(i -> i.getProducto().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void nuevaVentas(GiroDeCaja giro) {
        nuevaVenta = new Ventas(new VentasPK(new Date(), giro.getIdGiroDeCaja()), BigDecimal.ZERO);
        nuevaVenta.setGiroDeCaja(giro);
        nuevaVenta.setDetalleVentasList(new ArrayList<>());
        nuevoDetalleVentas();
    }

    public void nuevoDetalleVentas() {
        nuevoDetalleV = new DetalleVentas(new DetalleVentasPK(nuevaVenta.getVentasPK().getIdVentas(),
                nuevaVenta.getVentasPK().getGiroCaja(), 0), 0, BigDecimal.ZERO);
    }

    public void agregarDetalleVenta() {
        FacesMessage ms;
        if (nuevoDetalleV.getDetalleVentasPK().getProducto() == 0) {
            //no ha seleccionado producto
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "No hay producto seleccionado",
                    "Antes de continuar debe seleccionar un producto del cat??logo.");
        } else if (nuevoDetalleV.getCantidad() == 0) {
            //no ha establecido la cantidad
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "No ha ingresado una cantidad v??lida",
                    "Antes de continuar debe indicar una cantidad de producto que se agregar??n al inventario.");
        } else if (disponibilidad(nuevoDetalleV.getInventario()) < nuevoDetalleV.getCantidad()) {
            //no hay disponiblidad del producto
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "La venta no puede continuar",
                    "Usted intenta vender " + nuevoDetalleV.getCantidad() + " unidades del producto \""
                    + nuevoDetalleV.getInventario().getProducto() + "\"; pero esa cantidad excede el inventario "
                    + "actual que es de " + disponibilidad(nuevoDetalleV.getInventario()) + " unidades.");
        } else if (!nuevaVenta.getDetalleVentasList().stream().filter(dc -> dc.getInventario().equals(nuevoDetalleV.getInventario()))
                .collect(Collectors.toList()).isEmpty()) {
            //el producto ya se ha agregado a la venta
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "El producto duplicado",
                    "El producto \"" + nuevoDetalleV.getInventario().getProducto() + "\" ya est?? en la lista de ventas. "
                    + "S??lo se puede agregar una vez en cada factura. ");
        } else {
            nuevaVenta.setValor(nuevaVenta.getValor().add(nuevoDetalleV.getPrecioUnitario().multiply(new BigDecimal(nuevoDetalleV.getCantidad()))));
            nuevoDetalleV.setVentas(nuevaVenta);
            nuevaVenta.getDetalleVentasList().add(nuevoDetalleV);
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto agregado",
                    nuevoDetalleV.getCantidad() + " unidades del producto "
                    + nuevoDetalleV.getInventario().getProducto() + " se han agregado a la lista de ventas.");
            nuevoDetalleVentas();
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }

    public void quitarDetalleVenta() {
        selected.forEach(s -> {
            nuevaVenta.setValor(nuevaVenta.getValor().add(s.getPrecioUnitario().multiply(new BigDecimal(s.getCantidad())).negate()));
            nuevaVenta.getDetalleVentasList().remove(s);
            nuevoDetalleVentas();
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Eliminaci??n",
                            "El producto \"" + s.getInventario().getProducto()
                            + "\" ha sido quitado de la lista de compras."));
            PrimeFaces.current().ajax().update("form0:msgs");
        });
        selected.clear();
    }

    public void onRowSelect(SelectEvent<DetalleVentas> event) {
        DetalleVentas o = event.getObject();
        if (!selected.contains(o)) {
            selected.add(o);
        } else {
            selected.remove(o);
        }
    }

    public void onRowUnselect(UnselectEvent<DetalleVentas> event) {
        DetalleVentas o = event.getObject();
        if (o != null && selected.contains(o)) {
            selected.remove(o);
        }
    }

    public void guardarVenta() {
        FacesMessage ms;
        List<Inventario> noHayExistencias = new ArrayList<>();//agregar ultima validaci??n para las existencias en las ventas
        nuevaVenta.getDetalleVentasList().stream().filter(d -> {
            return (disponibilidad(ifl.find(d.getInventario().getIdInventario()))) < d.getCantidad();
        }).forEach(d -> noHayExistencias.add(d.getInventario()));
        if (!noHayExistencias.isEmpty()) {
            String nhe = "";
            for (Inventario i : noHayExistencias) {
                nhe = (nhe.isEmpty() ? "" : "   \n ") + i.getProducto();
            }
            ms = new FacesMessage(FacesMessage.SEVERITY_FATAL, "El inventario no puede suplir la venta actual",
                    "Los Siguientes productos no tienen suficientes existencias:    \n" + nhe + ".    \nNo se contin??a.");
        } else if (nuevaVenta.getDetalleVentasList().size() > 0) {
            vfl.create(nuevaVenta);
            vendiendo = false;
            nuevaVentas(nuevaVenta.getGiroDeCaja());
            ms = new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta guardada",
                    "Los datos de esta venta han sido guardados con ??xito.");
        } else {
            ms = new FacesMessage(FacesMessage.SEVERITY_WARN, "Error",
                    "Para poder registrar una venta se debe detallar "
                    + "al menos un producto vendido en la factura.");
        }
        FacesContext.getCurrentInstance().addMessage("form0:msgs", ms);
        PrimeFaces.current().ajax().update("form0:msgs");
    }
}
