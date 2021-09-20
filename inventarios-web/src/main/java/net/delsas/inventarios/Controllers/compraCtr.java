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
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.entities.Compras;
import net.delsas.inventarios.entities.ComprasPK;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleCompraPK;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Usuario;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class compraCtr implements Serializable {

    private Compras nCompra;
    private DetalleCompra ndCompra;
    private Optional<Usuario> user;
    private Inventario invSel;
    private boolean comprando;

    @EJB
    private ComprasFacadeLocal cfl;
    @EJB
    private InventarioFacadeLocal ifl;

    @PostConstruct
    public void init() {
        comprando = false;
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
        nuevoDetalleCompras();
        comprando = true;
    }

    public void nuevoDetalleCompras() {
        ndCompra = new DetalleCompra(new DetalleCompraPK(nCompra.getComprasPK().getIdCompras(),
                nCompra.getComprasPK().getUsuario(), 0), 0, BigDecimal.ZERO);
    }

    public void agregarDetalleCompra() {
        if (ndCompra.getDetalleCompraPK().getProducto() == 0) {
            //no ha seleccionado producto
        } else if (ndCompra.getCantidad() == 0) {
            //no ha establecido la cantidad
        } else if (ndCompra.getCostoUnitario().doubleValue() <= 0) {
            //no ha establecido bien le costo unitario
        } else {
            nCompra.setValor(nCompra.getValor().add(ndCompra.getCostoUnitario().multiply(new BigDecimal(ndCompra.getCantidad()))));
            ndCompra.setCompras(nCompra);
            nCompra.getDetalleCompraList().add(ndCompra);
            nuevoDetalleCompras();
        }
    }

    public void quitarDetalleCompra() {
        if (nCompra.getDetalleCompraList().contains(ndCompra)) {
            nCompra.setValor(nCompra.getValor().add(ndCompra.getCostoUnitario().multiply(new BigDecimal(ndCompra.getCantidad())).negate()));
            nCompra.getDetalleCompraList().remove(ndCompra);
            nuevoDetalleCompras();
        }
    }

    public List<Inventario> completeInventario(String query) {
        return ifl.findAll().stream()
                .filter(i -> i.getProducto().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void onItemSelect(SelectEvent<Inventario> event) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Country Selected", event.getObject().toString()));
        ndCompra.setInventario(invSel);
        ndCompra.getDetalleCompraPK().setProducto(invSel.getIdInventario());
    }

    public void guardarCompra() {
        comprando = false;
    }
}
