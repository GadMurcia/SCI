/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPage;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
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
import net.delsas.inventarios.beans.ComprasFacadeLocal;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.GiroDeCajaFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.Compras;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;
import net.delsas.inventarios.optional.Existencias;
import net.delsas.inventarios.optional.auxiliarCtr;
import net.delsas.inventarios.optional.reporteFacturasCompras;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.event.SelectEvent;
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
    private Compras compra;
    private List<reporteFacturasCompras> facturas;
    private reporteFacturasCompras facSelected;

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
    @EJB
    private ComprasFacadeLocal cfl;
    @EJB
    private VentasFacadeLocal vfl;

    @PostConstruct
    public void init() {
        matrices = new ArrayList<>();
        sucursales = new ArrayList<>();
        existencias = new ArrayList<>();
        facturas = new ArrayList<>();
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
                limpiarFechas();
        }
    }

    public List<Existencias> getExistencias() {
        existencias.clear();
        ifl.findByTienda(1).stream().forEachOrdered(p -> {
            existencias.add(new Existencias(p, dcfl, dvfl));
        });
        Collections.sort(existencias, (Existencias u, Existencias d) -> u.getNombre().compareToIgnoreCase(d.getNombre()));
        return existencias;
    }

    public double costoTotal() {
        return redondeo2decimales(getExistencias().stream().mapToDouble(Existencias::getCostoTotal).sum());
    }

    public double valorTotal() {
        return redondeo2decimales(getExistencias().stream().mapToDouble(Existencias::getValorTotal).sum());
    }

    public double utilidadTotal() {
        return redondeo2decimales(getExistencias().stream().mapToDouble(Existencias::getUtilidad).sum());
    }

    public void generarExistencias(AjaxBehaviorEvent e) {
        existencias.clear();
        if (sucSel != null) {
            ifl.findByTienda(1).stream().forEachOrdered(p -> {
                existencias.add(new Existencias(p, dcfl, dvfl));
            });
            Collections.sort(existencias, (Existencias u, Existencias d) -> u.getNombre().compareToIgnoreCase(d.getNombre()));
        }
    }

    public void limpiarFechas() {
        inicio = null;
        fin = null;
        generarRepVentas();
        generarRepCpmra();
    }

    public void generarRepVentas() {
        if (inicio != null) {
            periodo = !(fin == null || fin.equals(inicio));
            fin = fin == null || fin.before(inicio) ? new Date(inicio.getTime()) : fin;
            fin.setHours(23);
            fin.setMinutes(59);
            fin.setSeconds(59);
            girosUndía = gdcfl.findByPeriodoYSucursal(inicio, fin, 1);
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

    public Compras getCompra() {
        return compra;
    }

    public void generarRepCpmra() {
        facturas.clear();
        if (inicio != null) {
            periodo = !(fin == null || fin.equals(inicio));
            fin = fin == null || fin.before(inicio) ? new Date(inicio.getTime()) : fin;
            fin.setHours(23);
            fin.setMinutes(59);
            fin.setSeconds(59);
            List<DetalleCompra> dc = dcfl.findByPeriodoFechas(inicio, fin);
            compra = new Compras();
            compra.setValor(dc.stream()
                    .map(d -> d.getCostoUnitario().multiply(new BigDecimal(d.getCantidad())))
                    .reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO));
            List<DetalleCompra> d = new ArrayList<>();
            dc.stream().forEach(dc1 -> {
                List<DetalleCompra> hay = d.stream()
                        .filter(dc0 -> dc0.getInventario().getIdInventario().equals(dc1.getInventario().getIdInventario()))
                        .collect(Collectors.toList());
                if (hay.isEmpty()) {
                    List<DetalleCompra> dcl = dc.stream()
                            .filter(dc3 -> dc3.getInventario().getIdInventario().equals(dc1.getInventario().getIdInventario()))
                            .collect(Collectors.toList());
                    dc1.setCostoUnitario(dcl.stream().map(DetalleCompra::getCostoUnitario)
                            .reduce(BigDecimal::add)
                            .orElseGet(() -> BigDecimal.ZERO)
                            .divide(new BigDecimal(dcl.isEmpty() ? 1 : dcl.size())));
                    d.add(dc1);
                } else {
                    DetalleCompra dc2 = hay.get(0);
                    int idx = hay.indexOf(dc2);
                    dc2.setCantidad(dc2.getCantidad() + dc1.getCantidad());
                    d.remove(idx);
                    d.add(idx, dc2);
                }
            });
            compra.setDetalleCompraList(d);
            cfl.findConFacturaByPeriodo(inicio, fin).forEach(c -> facturas.add(new reporteFacturasCompras(c)));
        } else {
            compra = null;
        }
    }

    public void onSelectF(SelectEvent<reporteFacturasCompras> e) {
        facSelected = e.getObject();
    }

    public List<reporteFacturasCompras> getFacturas() {
        return facturas;
    }

    public reporteFacturasCompras getFacSelected() {
        return facSelected;
    }

    public void setFacSelected(reporteFacturasCompras facSelected) {
        this.facSelected = facSelected;
    }

    public String getDocFactura() {
        return (facSelected == null || facSelected.getFactura() == null) ? ""
                : (getDoc(facSelected.getFactura(), facSelected.getExt()));
    }

    public void generarRepVentas1() {
        if (inicio != null) {
            periodo = !(fin == null || fin.equals(inicio));
            fin = fin == null || fin.before(inicio) ? new Date(inicio.getTime()) : fin;
            fin.setHours(23);
            fin.setMinutes(59);
            fin.setSeconds(59);
            girosUndía = gdcfl.findByPeriodoYSucursal(inicio, fin, 1);
            giro = new GiroDeCaja();
            girosUndía.forEach(g -> {
                List<Ventas> vs = vfl.findByGiroCaja(g.getIdGiroDeCaja());
                Ventas v = new Ventas();
                v.setValor(vs.stream().map(Ventas::getValor).reduce(BigDecimal::add).orElseGet(() -> BigDecimal.ZERO));
                List<DetalleVentas> dvs = new ArrayList<>();
                vs.forEach(v0 -> {
                    List<DetalleVentas> dvs1 = dvfl.findByVenta(v0.getVentasPK());
                    dvs1.forEach(dvs10 -> {
                        llenarDetallVentas(dvs, dvs10);
                    });
                });
                ordenarPrecioPromedio(dvs);
                v.setDetalleVentasList(dvs);
                int idx = girosUndía.indexOf(g);
                g.setVentasList(new ArrayList<>());
                g.getVentasList().add(v);
                girosUndía.set(idx, g);
            });

            if (periodo) {
                giro.setRetiros(girosUndía.stream().mapToDouble(GiroDeCaja::getRetiros).sum());
                giro.setFaltantes(girosUndía.stream().mapToDouble(GiroDeCaja::getFaltantes).sum());
                giro.setExcedentes(girosUndía.stream().mapToDouble(GiroDeCaja::getExcedentes).sum());
                giro.setDetalleRetiros(girosUndía.stream().map(GiroDeCaja::getDetalleRetiros).collect(Collectors.joining("\n")));
                giro.setVentasList(new ArrayList<>());
                giro.getVentasList().add(new Ventas());
                giro.getVentasList().get(0).setValor(BigDecimal.ZERO);
                girosUndía.stream().forEach(g -> g.getVentasList().stream()
                        .forEach(v -> giro.getVentasList().get(0)
                        .setValor(giro.getVentasList().get(0).getValor()
                                .add(v.getValor()))));
                List<DetalleVentas> d = new ArrayList<>();
                girosUndía.stream().forEach(g -> g.getVentasList().stream().forEach(v -> v.getDetalleVentasList().stream().forEach(gvd -> {
                    llenarDetallVentas(d, gvd);
                })));
                ordenarPrecioPromedio(d);
                giro.getVentasList().get(0).setDetalleVentasList(d);
            }
        } else {
            giro = null;
        }
    }

    private void ordenarPrecioPromedio(List<DetalleVentas> dvs) {
        dvs.forEach(dvs0 -> {
            int idx = dvs.indexOf(dvs0);
            DetalleVentas get = dvs.get(idx);
            get.setPrecioUnitario(new BigDecimal(redondeo2decimales(get.getPrecioUnitario().doubleValue() / get.getDetalleVentasPK().getProducto())));
            get.getDetalleVentasPK().setProducto(get.getInventario().getIdInventario());
            dvs.set(idx, get);
        });
    }

    private void llenarDetallVentas(List<DetalleVentas> dvs, DetalleVentas dvs10) {
        List<DetalleVentas> previo = dvs.stream().filter(dvs0 -> dvs0.getInventario().getIdInventario().equals(dvs10.getInventario().getIdInventario())).collect(Collectors.toList());
        if (previo.isEmpty()) {
            dvs10.getDetalleVentasPK().setProducto(1);
            dvs.add(dvs10);
        } else {
            DetalleVentas get = dvs.get(0);
            int idx = dvs.indexOf(get);
            get.setCantidad(get.getCantidad() + dvs10.getCantidad());
            get.setPrecioUnitario(get.getPrecioUnitario().add(dvs10.getPrecioUnitario()));
            get.getDetalleVentasPK().setProducto(get.getDetalleVentasPK().getProducto() + 1);
            dvs.set(idx, get);
        }
    }

    public Exporter<DataTable> getPdfExporterExistencias() {
        getMatrices();
        getSucursales();
        return new Exporter<DataTable>() {
            @Override
            public void export(FacesContext fc, List<DataTable> list, OutputStream out, ExportConfiguration ec) {
                Document pdf;
                PdfWriter writer;
                try {
                    PdfPTable t = new PdfPTable(8);

                    t.addCell(getTextCell(sucSel.getNombre(), 8, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 8, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 8, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("Reporte de existencias de productos", 8, 1, false, false, 16, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 8, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("Producto", 2, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Cantidad", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo unitario", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo de producto", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Precio unitario", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Valor de venta", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Utilidad", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    List<Existencias> ex = getExistencias();
                    ex.stream().forEachOrdered(e -> {
                        t.addCell(getTextCell(e.getNombre(), 2, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell(e.getExistencias() + "", 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + e.getCostoAVG(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + e.getCostoTotal(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + e.getPrecioAVG(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + e.getValorTotal(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + e.getUtilidad(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    });
                    t.addCell(getTextCell("", 4, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo total del inventario: $", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("" + ex.stream().mapToDouble(Existencias::getCostoTotal).sum(), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 4, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Valor de venta total del inventario: $", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("" + ex.stream().mapToDouble(Existencias::getValorTotal).sum(), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 4, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Utilidad esperada total: $", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("" + ex.stream().mapToDouble(Existencias::getUtilidad).sum(), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("", 8, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 8, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Generado el " + (new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date())) + " /S.C.I./", 2, 1, false, false, 10, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    //OutputStream out = getOutputStream(fc, ec, "application/pdf");
                    pdf = new Document(PageSize.LETTER.rotate());
                    writer = PdfWriter.getInstance(pdf, out);
                    pdf.open();
                    t.setWidthPercentage(100);
                    pdf.add(t);
                    pdf.setMargins(1, 1, 1, 1);
                    pdf.close();
                    writer.flush();
                } catch (DocumentException e) {
                    Logger.getLogger(reportes1Ctr.class.getName()).log(Level.SEVERE, null, e);
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

    public Exporter<DataTable> getPdfExporterCompras() {
        getMatrices();
        getSucursales();
        return new Exporter<DataTable>() {
            @Override
            public void export(FacesContext fc, List<DataTable> list, OutputStream out, ExportConfiguration ec) {
                Document pdf;
                PdfWriter writer;
                try {
                    PdfPTable t = new PdfPTable(5);

                    t.addCell(getTextCell(sucSel.getNombre(), 5, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Reporte de Compras", 5, 1, false, false, 16, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Periodo del reporte: ", 2, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell(new SimpleDateFormat("dd/MM/YYYY").format(inicio) + (fin.equals(inicio) ? "" : " al " + new SimpleDateFormat("dd/MM/YYYY").format(fin)), 3, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("Nombre del roducto", 2, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Cantidad", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo unitario", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo del producto", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    compra.getDetalleCompraList().stream().forEachOrdered(d -> {
                        t.addCell(getTextCell(d.getInventario().getProducto(), 2, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell(d.getCantidad() + "", 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + d.getCostoUnitario().doubleValue(), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + redondeo2decimales((d.getCantidad() * d.getCostoUnitario().doubleValue())), 1, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    });
                    t.addCell(getTextCell("", 2, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Total de compras: $", 2, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("" + compra.getDetalleCompraList().stream().mapToDouble(d -> d.getCantidad() * d.getCostoUnitario().doubleValue()).sum(), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("", 3, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Generado el " + (new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date())) + " /S.C.I./", 2, 1, false, false, 10, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    
                    
                    pdf = new Document(PageSize.LETTER);
                    writer = PdfWriter.getInstance(pdf, out);
                    pdf.open();
                    t.setWidthPercentage(100);
                    pdf.add(t);
                    pdf.setMargins(1, 1, 1, 1);
                    pdf.add(Phrase.getInstance("\n\n\n\nListado de facturas de compras\n\n\n\n"));
                    getFacturas().stream().forEachOrdered(f->{
                        try {                            
                            Image i = Image.getInstance(f.getFactura());
                            i.scalePercent(50);
                            i.setAlignment(Image.ALIGN_CENTER);
                            pdf.add(i);
                            pdf.add(Phrase.getInstance("\n\n\n\n"));
                        } catch (BadElementException | IOException ex) {
                            Logger.getLogger(reportes1Ctr.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DocumentException ex) {
                            Logger.getLogger(reportes1Ctr.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    pdf.close();
                    writer.flush();
                } catch (DocumentException e) {
                    Logger.getLogger(reportes1Ctr.class.getName()).log(Level.SEVERE, null, e);
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
}
