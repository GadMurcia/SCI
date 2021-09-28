/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.GiroDeCajaFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;
import net.delsas.inventarios.optional.Existencias;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class reportes1Ctr extends auxiliarCtr implements Serializable {

    private Optional<Usuario> user;
    private List<Misc> matrices;
    private Misc matSel;
    private List<Misc> sucursales;
    private Misc sucSel;
    private List<Existencias> existencias;
    private Date inicio;
    private Date fin;
    private GiroDeCaja giro;
    private List<GiroDeCaja> girosUndía;
    private boolean periodo;

    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private InventarioFacadeLocal ifl;
    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;
    @EJB
    private GiroDeCajaFacadeLocal gdcfl;

    @PostConstruct
    public void init() {
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
        existencias = new ArrayList<>();
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

    public void onTabChange(TabChangeEvent event) {
        System.out.println(event.getTab().getTitle());
        switch (event.getTab().getTitle()) {
            case "Existencias":
                generarExistencias(null);
                break;
            default:
        }
    }

    public List<Existencias> getExistencias() {
        return existencias;
    }

    public double costoTotal() {
        return redondeo2decimales(existencias.stream().mapToDouble(Existencias::getCostoTotal).sum());
    }

    public double valorTotal() {
        return redondeo2decimales(existencias.stream().mapToDouble(Existencias::getValorTotal).sum());
    }

    public double utilidadTotal() {
        return redondeo2decimales(existencias.stream().mapToDouble(Existencias::getUtilidad).sum());
    }

    public void generarExistencias(AjaxBehaviorEvent e) {
        existencias.clear();
        if (sucSel != null) {
            ifl.findByTienda(sucSel.getIdMisc()).stream().forEachOrdered(p -> {
                existencias.add(new Existencias(p, dcfl, dvfl));
            });
            Collections.sort(existencias, (Existencias u, Existencias d) -> u.getNombre().compareToIgnoreCase(d.getNombre()));
        }
    }

    public void limpiarFechas() {
        inicio = null;
        fin = null;
        generarRepVentas();
    }

    public void generarRepVentas() {
        if (inicio != null) {
            periodo = !(fin == null || fin.equals(inicio));
            fin = fin == null || fin.before(inicio) ? new Date(inicio.getTime()) : fin;
            fin.setHours(23);
            fin.setMinutes(59);
            fin.setSeconds(59);
            girosUndía = gdcfl.findByPeriodoYSucursal(inicio, fin, sucSel.getIdMisc());
            giro = new GiroDeCaja();
            if (!girosUndía.isEmpty()) {
                giro.setFin(fin);
                giro.setInicio(inicio);
                giro.setCajaInicial(0);
                giro.setCierre(0);
                giro.setRetiros(redondeo2decimales(girosUndía.stream().mapToDouble(GiroDeCaja::getRetiros).sum()));
                giro.setExcedentes(redondeo2decimales(girosUndía.stream().mapToDouble(GiroDeCaja::getExcedentes).sum()));//ventas
                giro.setFaltantes(redondeo2decimales(girosUndía.stream().mapToDouble(GiroDeCaja::getFaltantes).sum()));//faltantes de caja
                giro.setDetalleRetiros("");
                girosUndía.forEach(g -> giro.setDetalleRetiros(giro.getDetalleRetiros()
                        + (g.getDetalleRetiros() == null || g.getDetalleRetiros().isEmpty() ? "" : "\n")
                        + g.getDetalleRetiros() != null ? g.getDetalleRetiros() : ""));
                List<DetalleVentas> dv = new ArrayList<>();
                girosUndía.forEach(g -> sumarizarDetalleGiro(g, giro, dv));
                if (!periodo) {
                    girosUndía.forEach(g -> sumarizarDetalleGiro(g, girosUndía.get(girosUndía.indexOf(g)), new ArrayList<>()));
                }
            }

        } else {
            giro = null;
        }
    }

    public void sumarizarDetalleGiro(GiroDeCaja g, GiroDeCaja g2, List<DetalleVentas> dv) {
        g.getVentasList().forEach(v0 -> v0.getDetalleVentasList().forEach(dv1 -> {
            List<DetalleVentas> col = dv.stream().filter(dv0 -> dv0.getInventario().getIdInventario().equals(dv1.getInventario().getIdInventario())).collect(Collectors.toList());
            if (!col.isEmpty()) {
                int idx = dv.indexOf(col.get(0));
                DetalleVentas dv2 = dv.get(idx);
                dv2.setCantidad(dv2.getCantidad() + dv1.getCantidad());
                dv2.setPrecioUnitario((dv2.getPrecioUnitario().add(dv1.getPrecioUnitario()).divide(new BigDecimal(2.0))));
                dv.remove(idx);
                dv.add(idx, dv2);
            } else {
                dv.add(dv1);
            }
        }));
        g2.setVentasList(new ArrayList<>());
        Ventas v = new Ventas();
        v.setDetalleVentasList(dv);
        g2.getVentasList().add(v);
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    public GiroDeCaja getGiro() {
        return giro;
    }

    public List<DetalleVentas> getDetalleReporte() {
        return getDetalleReporte(giro);
    }

    public List<DetalleVentas> getDetalleReporte(GiroDeCaja g) {
        return g == null || g.getVentasList() == null
                || g.getVentasList().isEmpty()
                || g.getVentasList().get(0).getDetalleVentasList() == null
                ? new ArrayList<>()
                : g.getVentasList().get(0).getDetalleVentasList();
    }

    public double getTotalVendidoPeriodo() {
        return getTotalVendido(getDetalleReporte());
    }

    public double getTotalVendido(List<DetalleVentas> dv) {
        return redondeo2decimales(dv.stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario()
                .doubleValue()).sum());
    }

    public void ValidaFechaFinal() {
        if (inicio != null && fin != null && fin.before(inicio)) {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Verifique la fecha",
                            "La fecha de fin del periodo no puede ser anterior a la fecha de inicio."));
            fin = null;
            PrimeFaces.current().ajax().update(new String[]{"form0:msgs", "form:tw:fin"});
        }
    }

    public List<GiroDeCaja> getGirosUndía() {
        return girosUndía;
    }

    public boolean isPeriodo() {
        return periodo;
    }

}
