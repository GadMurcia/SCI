/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
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
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.GiroDeCajaFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.UsuarioFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;
import net.delsas.inventarios.optional.RepDetalleVentas;
import net.delsas.inventarios.optional.ReporteVentas;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class reportes2Ctr extends auxiliarCtr implements Serializable {

    private List<GiroDeCaja> giros;
    private GiroDeCaja giroGlobal;
    private Date inicio;
    private Date fin;
    private boolean periodo;
    private List<RepDetalleVentas> detalle;
    private Optional<Usuario> user;
    public static List<Usuario> userList;
    private Usuario userSelected;

    @EJB
    private GiroDeCajaFacadeLocal gcfl;
    @EJB
    private VentasFacadeLocal vfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;
    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private UsuarioFacadeLocal ufl;

    @PostConstruct
    public void init() {
        giros = new ArrayList<>();
        detalle = new ArrayList<>();
        userSelected = null;
        user = Optional.ofNullable((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("user"));
//        userList = ufl.findAll().stream().filter(u -> {
//            if (user.isPresent()) {
//                switch (user.get().getTipoUsuario().getIdTipoUsuario()) {
//                    case 1:
//                        return true;
//                    case 2:
//                        return u.getTipoUsuario().getIdTipoUsuario() >= 2;
//                    case 3:
//                        return u.getTipoUsuario().getIdTipoUsuario() >= 3;
//                    default:
//                        return false;
//                }
//            }
//            return false;
//        }).collect(Collectors.toList());
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
            if (user.get().getTipoUsuario().getIdTipoUsuario() > 3) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado",
                                    "Usted no est?? autorizado para ver esa funcionalidad."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean isPeriodo() {
        return periodo;
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

    public List<GiroDeCaja> getGiros() {
        return giros;
    }

    public GiroDeCaja getGiroGlobal() {
        return giroGlobal;
    }

    public void setGiroGlobal(GiroDeCaja giroGlobal) {
        this.giroGlobal = giroGlobal;
    }

    public List<RepDetalleVentas> getDetalle() {
        return detalle;
    }

    public List<ReporteVentas> getDetalleVentas(GiroDeCaja g) {
        List<ReporteVentas> detalleVentas = g == null ? new ArrayList<>()
                : detalle.stream().filter(d -> d.getId().equals(g.getIdGiroDeCaja()))
                        .findFirst().orElseGet(() -> new RepDetalleVentas(0, new ArrayList<>()))
                        .getDetalle();
        Collections.sort(detalleVentas, (ReporteVentas r1, ReporteVentas r2) -> r1.getInv().getProducto().toLowerCase().compareToIgnoreCase(r2.getInv().getProducto().toLowerCase()));
        return detalleVentas;
    }

    public double getCostoVendido(GiroDeCaja g) {
        return g == null ? 0
                : redondeo2decimales(detalle.stream().filter(d -> d.getId().equals(g.getIdGiroDeCaja()))
                        .findFirst().orElseGet(() -> new RepDetalleVentas(0, new ArrayList<>()))
                        .getDetalle().stream().mapToDouble(r -> r.getCantidad() * r.getCostoU()).sum());
    }

    public double getValorVendido(GiroDeCaja g) {
        return g == null ? 0
                : redondeo2decimales(detalle.stream().filter(d -> d.getId().equals(g.getIdGiroDeCaja()))
                        .findFirst().orElseGet(() -> new RepDetalleVentas(0, new ArrayList<>()))
                        .getDetalle().stream().mapToDouble(ReporteVentas::getSubTotal).sum());
    }

    public double getutilidadVendido(GiroDeCaja g) {
        return g == null ? 0
                : redondeo2decimales(detalle.stream().filter(d -> d.getId().equals(g.getIdGiroDeCaja()))
                        .findFirst().orElseGet(() -> new RepDetalleVentas(0, new ArrayList<>()))
                        .getDetalle().stream().mapToDouble(ReporteVentas::getUtilidad).sum());
    }

    public String getEstadoCaja(GiroDeCaja g) {
        return g == null ? "" : (g.getFaltantes() <= 0 ? "Faltante" : "Excedente") + " de caja";
    }

    public void limpiarFechas() {
        inicio = null;
        fin = null;
        giros.clear();
        userSelected = null;
    }

    public double getUtilidad(GiroDeCaja g) {
        return g == null ? 0 : g.getExcedentes() + g.getFaltantes() - g.getRetiros();
    }

    public void generarReporteVentas() {
        detalle.clear();
        giroGlobal = new GiroDeCaja();
        if (inicio != null) {
            arreglarFechas(inicio, fin);
            periodo = !(fin == null || fin.equals(inicio));
            try {
                fin = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(periodo ? fin : inicio) + " 23:59:59");
            } catch (ParseException ex) {
                fin = new Date();
            }
            giros = userSelected == null
                    ? gcfl.findByPeriodoYSucursal(inicio, fin, 1)
                    : gcfl.findTerminadas(userSelected.getIdUsuario(), inicio, fin);
            if (giros.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("form0:msgs",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Registros",
                                "No se encontraron Ventas para las fechas especificadas."));
                PrimeFaces.current().ajax().update("form0:msgs");
            }
            giros.stream().forEach(g -> {
                RepDetalleVentas rdv = new RepDetalleVentas(g.getIdGiroDeCaja(), new ArrayList<>());
                vfl.findByGiroCaja(rdv.getId()).stream().map(Ventas::getDetalleVentasList).forEachOrdered(dl -> dl.forEach(d -> {
                    ReporteVentas rv = new ReporteVentas(d.getInventario().getIdInventario(), d.getInventario(), 0, 0, 0, 0, 0);
                    if (!rdv.getDetalle().contains(rv)) {
                        rv = new ReporteVentas(d, vfl.findByGiroCaja(rdv.getId()), dcfl, dvfl, inicio, fin);
                        rdv.getDetalle().add(rv);
                    } else {
                        int idx = rdv.getDetalle().indexOf(rv);
                        ReporteVentas get = rdv.getDetalle().get(idx);
                        get.setCantidad(get.getCantidad() + d.getCantidad());
                        get.setSubTotal(redondeo2decimales(get.getCantidad() * get.getPrecioU()));
                        get.setUtilidad(redondeo2decimales(get.getSubTotal() - (get.getCantidad() * get.getCostoU())));
                        rdv.getDetalle().set(idx, get);
                    }
                }));
                detalle.add(rdv);
            });
            giroGlobal.setIdGiroDeCaja(0);
            giroGlobal.setCajaInicial(0);
            giroGlobal.setCierre(0);
            giroGlobal.setDetalleRetiros("");
            giros.stream().map(GiroDeCaja::getDetalleRetiros).collect(Collectors.toList()).stream().filter(dr -> !dr.isEmpty()).forEachOrdered(dr -> {
                giroGlobal.setDetalleRetiros((!giroGlobal.getDetalleRetiros().isEmpty() ? giroGlobal.getDetalleRetiros().isEmpty() + "\n" : "") + dr);
            });
            giroGlobal.setExcedentes(redondeo2decimales(giros.stream().mapToDouble(GiroDeCaja::getExcedentes).sum()));
            giroGlobal.setFaltantes(redondeo2decimales(giros.stream().mapToDouble(GiroDeCaja::getFaltantes).sum()));
            giroGlobal.setFin(fin);
            giroGlobal.setInicio(inicio);
            giroGlobal.setResponsable(userSelected == null ? new Usuario("", "Reporte Global", "De Ventas Por Periodo", "", false) : new Usuario("", "Reporte De Ventas", "Para " + getNombreususario(userSelected), "", false));
            giroGlobal.setRetiros(redondeo2decimales(giros.stream().mapToDouble(GiroDeCaja::getRetiros).sum()));
            RepDetalleVentas dg = new RepDetalleVentas(giroGlobal.getIdGiroDeCaja(), new ArrayList<>());
            giros.stream().forEach(g -> {
                RepDetalleVentas rdv = new RepDetalleVentas(g.getIdGiroDeCaja(), new ArrayList<>());
                vfl.findByGiroCaja(rdv.getId()).stream().map(Ventas::getDetalleVentasList).forEachOrdered(dl -> dl.forEach(d -> {
                    ReporteVentas rv = new ReporteVentas(d.getInventario().getIdInventario(), d.getInventario(), 0, 0, 0, 0, 0);
                    if (!dg.getDetalle().contains(rv)) {
                        rv = new ReporteVentas(d, dcfl, dvfl, inicio, fin);
                        dg.getDetalle().add(rv);
                    } else {
                        int idx = dg.getDetalle().indexOf(rv);
                        ReporteVentas get = dg.getDetalle().get(idx);
                        get.setCantidad(get.getCantidad() + d.getCantidad());
                        get.setSubTotal(redondeo2decimales(get.getCantidad() * get.getPrecioU()));
                        get.setUtilidad(redondeo2decimales(get.getSubTotal() - (get.getCantidad() * get.getCostoU())));
                        dg.getDetalle().set(idx, get);
                    }
                }));
            });
            if (periodo && !giros.isEmpty()) {
                detalle.clear();
                giros.clear();
                giros.add(giroGlobal);
            }
            detalle.add(dg);
        } else {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Adventencia",
                            "Para generar un reporte debe seleccionar, al menos, una fecha de inicio del reporte."));
            PrimeFaces.current().ajax().update("form0:msgs");
        }
    }

    public Exporter<DataTable> getPdfExporterVentas() {
        return new Exporter<DataTable>() {
            @Override
            public void export(FacesContext fc, List<DataTable> list, OutputStream out, ExportConfiguration ec) {
                Document pdf;
                PdfWriter writer;
                try {
                    PdfPTable t = new PdfPTable(6);

                    t.addCell(getTextCell(mfl.find(1).getNombre(), 6, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Reporte de Ventas", 6, 1, false, false, 16, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    if (userSelected != null) {
                        t.addCell(getTextCell("Vendedor: " + getNombreususario(userSelected), 6, 1, false, false, 14, Font.ITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    }
                    t.addCell(getTextCell("", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Periodo del reporte: ", 2, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell(new SimpleDateFormat("dd/MM/YYYY").format(inicio) + (fin.equals(inicio) ? "" : " al " + new SimpleDateFormat("dd/MM/YYYY").format(fin)), 4, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("\n\n\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                    if (giros.size() > 1) {
                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Resumen Global de Saldos", 4, 1, false, false, 16, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Ventas Globales:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + giroGlobal.getExcedentes(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Total de Retiros:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + giroGlobal.getRetiros(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell(getEstadoCaja(giroGlobal) + ":", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + giroGlobal.getFaltantes(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Sumatoria de Recaudaci??n:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + getUtilidad(giroGlobal), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        if (!giroGlobal.getDetalleRetiros().isEmpty()) {
                            t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Detalle de retiros", 4, 1, false, true, 13, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                            for (String dr : giroGlobal.getDetalleRetiros().split("\n")) {
                                String drr[] = dr.split("   ->  ");
                                if (drr.length == 2) {
                                    t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell(drr[0], 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell(drr[1], 3, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                }
                            }
                        }
                        t.addCell(getTextCell("\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Detalle global de productos vendidos", 6, 1, false, true, 12, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                        detalle.stream().filter(dt -> dt.getId().equals(giroGlobal.getIdGiroDeCaja())).findFirst().ifPresent(dt -> {

                            t.addCell(getTextCell("Producto", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Cantidad", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Costo Unitario", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Precio Unitario", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Valor de venta", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                            Collections.sort(dt.getDetalle(), (ReporteVentas r1, ReporteVentas r2) -> r1.getInv().getProducto().toLowerCase().compareToIgnoreCase(r2.getInv().getProducto().toLowerCase()));

                            dt.getDetalle().stream().forEachOrdered(dt0 -> {
                                t.addCell(getTextCell(dt0.getInv().getProducto(), 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("" + dt0.getCantidad(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getCostoU(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getPrecioU(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getSubTotal(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            });
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Costo de lo vendido:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getCostoVendido(giroGlobal), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Ventas:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getValorVendido(giroGlobal), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Utilidad de Venta:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getutilidadVendido(giroGlobal), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                        });

                        t.addCell(getTextCell("\n\n\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Resumen Por Caja", 4, 1, false, false, 16, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("\n\n\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    }

                    giros.forEach(g -> {
                        t.addCell(getTextCell(g.getResponsable().getNombres() + " " + g.getResponsable().getApellidos(), 6, 1, false, false, 13, Font.BOLD, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Desde " + getDateTimeToString12H(g.getInicio()) + " hasta " + getDateTimeToString12H(g.getFin()), 6, 1, false, false, 13, Font.BOLD, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Resumen de Saldos", 4, 1, false, false, 13, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        if (!periodo) {
                            t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Caja Inicial:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$" + g.getCajaInicial(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        }

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Ventas:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$" + g.getExcedentes(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Retiros:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$" + g.getRetiros(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        if (!periodo) {
                            t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Cierre de caja:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$" + g.getCierre(), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        }

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell(getEstadoCaja(g) + ":", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$" + redondeo2decimales(g.getFaltantes()), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("Recaudaci??n:", 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$" + redondeo2decimales(getUtilidad(g)), 1, 1, false, false, 13, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        t.addCell(getTextCell("\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        if (!g.getDetalleRetiros().isEmpty()) {
                            t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Detalle de retiros", 4, 1, false, true, 13, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                            for (String dr : g.getDetalleRetiros().split("\n")) {
                                String drr[] = dr.split("   ->  ");
                                if (drr.length == 2) {
                                    t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell("$ " + drr[0], 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell(drr[1], 3, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                    t.addCell(getTextCell("", 1, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                }
                            }
                        }
                        t.addCell(getTextCell("\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                        detalle.stream().filter(dt -> dt.getId().equals(g.getIdGiroDeCaja())).findFirst().ifPresent(dt -> {
                            t.addCell(getTextCell("Detalle de productos vendidos", 6, 1, false, true, 12, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                            t.addCell(getTextCell("Producto", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Cantidad", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Costo Unitario", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Precio Unitario", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Valor de venta", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                            dt.getDetalle().stream().forEachOrdered(dt0 -> {
                                t.addCell(getTextCell(dt0.getInv().getProducto(), 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("" + dt0.getCantidad(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getCostoU(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getPrecioU(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                                t.addCell(getTextCell("$ " + dt0.getSubTotal(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            });
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Costo de lo vendido:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getCostoVendido(g), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Ventas:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getValorVendido(g), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("Utilidad de Venta:", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                            t.addCell(getTextCell("$ " + getutilidadVendido(g), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                        });

                        t.addCell(getTextCell("\n\n\n", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    });
                    t.addCell(getTextCell("\n\nGenerado en " + getDateTimeToString12H(new Date()) + " /S.C.I./", 6, 1, false, false, 9, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));

                    pdf = new Document(PageSize.LETTER);
                    writer = PdfWriter.getInstance(pdf, out);
                    pdf.open();
                    t.setWidthPercentage(100);
                    pdf.add(t);
                    pdf.setMargins(1, 1, 1, 1);
                    pdf.close();
                    writer.flush();
                } catch (DocumentException e) {
                    Logger.getLogger(reportes1Ctr.class
                            .getName()).log(Level.SEVERE, null, e);
                    fc.addMessage("form0:msgs",
                            new FacesMessage(FacesMessage.SEVERITY_FATAL, "Eror!!!", "No se pudo generar el reporte."));
                }
            }

            @Override
            public String getContentType() {
                return "application/pdf";
            }

            @Override
            public String getFileExtension() {
                return "pdf";
            }
        };
    }

    public Usuario getUserSelected() {
        return userSelected;
    }

    public void setUserSelected(Usuario userSelected) {
        this.userSelected = userSelected;
    }

    public void onUserSelect(SelectEvent<Usuario> event) {
        userSelected = event.getObject();
    }

    public List<Usuario> completeUsario(String query) {;//agregar c??digo para el filtrado
        return userList.stream()
                .filter(u -> getNombreususario(u).toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Usuario> getUsuarioList() {
        return ufl.findAll().stream().filter(u -> {
            if (user.isPresent()) {
                switch (user.get().getTipoUsuario().getIdTipoUsuario()) {
                    case 1:
                        return true;
                    case 2:
                        return u.getTipoUsuario().getIdTipoUsuario() >= 2;
                    case 3:
                        return u.getTipoUsuario().getIdTipoUsuario() >= 3;
                    default:
                        return false;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }
}
