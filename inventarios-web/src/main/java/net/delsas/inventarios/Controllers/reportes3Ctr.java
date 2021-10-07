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
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
import net.delsas.inventarios.beans.ComprasFacadeLocal;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.optional.Existencias;
import net.delsas.inventarios.optional.ReporteDetalleCompra;
import net.delsas.inventarios.optional.auxiliarCtr;
import net.delsas.inventarios.optional.reporteFacturasCompras;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
import org.primefaces.model.charts.optionconfig.title.Title;

/**
 *
 * @author delsas
 */
@ViewScoped
@Named
public class reportes3Ctr extends auxiliarCtr implements Serializable {

    private Date inicio;
    private Date fin;
    private Date ginicio;
    private Date gfin;
    private Date ginicio0;
    private Date gfin0;
    private Date ginicio1;
    private Date gfin1;
    private List<reporteFacturasCompras> facturas;
    private reporteFacturasCompras selected;
    private List<ReporteDetalleCompra> detalle;
    private Optional<Usuario> user;
    private Integer sel;
    private Inventario invSel;
    private Integer sel0;
    private Inventario invSel0;
    private Integer sel1;
    private Inventario invSel1;
    private List<Inventario> invs;

    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;
    @EJB
    private ComprasFacadeLocal cfl;
    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private VentasFacadeLocal vfl;
    @EJB
    private InventarioFacadeLocal ifl;

    @PostConstruct
    public void init() {
        facturas = new ArrayList<>();
        detalle = new ArrayList<>();
        sel = 1;
        sel0 = 1;
        sel1 = 1;
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
            if (user.get().getTipoUsuario().getIdTipoUsuario() > 3) {
                try {
                    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("msg",
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Acceso Denegado",
                                    "Usted no está autorizado para ver esa funcionalidad."));
                    FacesContext.getCurrentInstance().getExternalContext().redirect("home.app");
                } catch (IOException ex) {
                    Logger.getLogger(homeCtr.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
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

    public double getCompraTotal() {
        return redondeo2decimales(detalle.stream().mapToDouble(ReporteDetalleCompra::getSubTotal).sum());
    }

    public void onSelectFactura(SelectEvent<reporteFacturasCompras> e) {
        selected = e.getObject();
    }

    public String docFacrura() {
        return selected == null ? "" : getDoc(selected.getFactura(), selected.getExt());
    }

    public void limpiarFechas() {
        inicio = null;
        fin = null;
        detalle.clear();
        facturas.clear();
    }

    public void generarReporteVentas() {
        detalle.clear();
        facturas.clear();
        if (inicio != null) {
            arreglarFechas(inicio, fin);
            try {
                fin = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(fin != null ? fin : inicio) + " 23:59:59");
            } catch (ParseException ex) {
                System.out.println("Un error ha ocurrido al procesar la fecha de fin. reporte3Crt");
                fin = new Date();
            }
            dcfl.findByPeriodoFechas(inicio, fin).stream().forEachOrdered(dc -> {
                ReporteDetalleCompra rdc = new ReporteDetalleCompra(dc.getInventario().getIdInventario(), dc.getInventario(), dc.getCantidad(), 0, 0);
                if (!detalle.contains(rdc)) {
                    rdc = new ReporteDetalleCompra(dc, dcfl, dvfl, inicio, fin);
                    detalle.add(rdc);
                } else {
                    int idx = detalle.indexOf(rdc);
                    ReporteDetalleCompra detalle0 = detalle.get(idx);
                    detalle0.setCantidad(detalle0.getCantidad() + rdc.getCantidad());
                    detalle0.setSubTotal(redondeo2decimales(detalle0.getCantidad() * detalle0.getCostoU()));
                    detalle.set(idx, detalle0);
                }
            });
            if (detalle.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("form0:msgs",
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Registros",
                                "No se encontraron Compras para las fechas especificadas."));
                PrimeFaces.current().ajax().update("form0:msgs");
            }
            cfl.findConFacturaByPeriodo(inicio, fin).stream()
                    .filter(c -> c.getFactura() != null)
                    .forEach(c -> facturas.add(new reporteFacturasCompras(
                    c.getComprasPK().getIdCompras(),
                    c.getValor(),
                    c.getFactura(),
                    c.getExtencion())));

        } else {
            FacesContext.getCurrentInstance().addMessage("form0:msgs",
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Adventencia",
                            "Para generar un reporte debe seleccionar, al menos, una fecha de inicio del reporte."));
            PrimeFaces.current().ajax().update("form0:msgs");
        }
    }

    public List<reporteFacturasCompras> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<reporteFacturasCompras> facturas) {
        this.facturas = facturas;
    }

    public reporteFacturasCompras getSelected() {
        return selected;
    }

    public void setSelected(reporteFacturasCompras selected) {
        this.selected = selected;
    }

    public List<ReporteDetalleCompra> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<ReporteDetalleCompra> detalle) {
        this.detalle = detalle;
    }

    public Exporter<DataTable> getPdfExporterCompras() {
        return new Exporter<DataTable>() {
            @Override
            public void export(FacesContext fc, List<DataTable> list, OutputStream out, ExportConfiguration ec) {
                Document pdf;
                PdfWriter writer;
                try {
                    PdfPTable t = new PdfPTable(5);

                    t.addCell(getTextCell(mfl.find(1).getNombre(), 5, 1, false, false, 18, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Reporte de Compras", 5, 1, false, false, 16, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Periodo del reporte: ", 2, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell(new SimpleDateFormat("dd/MM/YYYY").format(inicio) + (fin.equals(inicio) ? "" : " al " + new SimpleDateFormat("dd/MM/YYYY").format(fin)), 3, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("\n", 5, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("Detalle de las compras de productos", 5, 1, true, true, 13, Font.BOLDITALIC, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Producto", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Cantidad", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo Unitario", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo Total", 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    detalle.stream().forEachOrdered(d -> {
                        t.addCell(getTextCell(d.getInv().getProducto(), 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("" + d.getCantidad(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + d.getCostoU(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                        t.addCell(getTextCell("$ " + d.getSubTotal(), 1, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    });

                    t.addCell(getTextCell("", 2, 1, true, false, 12, Font.NORMAL, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Total", 2, 1, true, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("$ " + getCompraTotal(), 1, 1, true, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    pdf = new Document(PageSize.LETTER);
                    writer = PdfWriter.getInstance(pdf, out);
                    pdf.open();
                    if (!facturas.isEmpty()) {
                        t.addCell(getTextCell("\n\n\n LISTADO DE FACTURAS \n\n\n", 5, 1, false, false, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    }
                    t.setWidthPercentage(100);
                    pdf.add(t);
                    facturas.stream().forEachOrdered(f -> {
                        Image i;
                        try {
                            i = Image.getInstance(f.getFactura());
                            i.scalePercent(35);
                            i.setAlignment(Image.ALIGN_CENTER);
                            pdf.add(i);
                            pdf.add(Phrase.getInstance("\n\n"));
                        } catch (BadElementException | IOException ex) {
                            Logger.getLogger(reportes3Ctr.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (DocumentException ex) {
                            Logger.getLogger(reportes3Ctr.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
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

    public LineChartModel getLineModel() {
        LineChartModel lineModel = sel == 0 ? null : new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        LineChartOptions options = new LineChartOptions();

        if (ginicio != null && gfin != null) {
            if (gfin.before(ginicio)) {
                Date f = new Date(gfin.getTime());
                gfin = new Date(ginicio.getTime());
                ginicio = new Date(f.getTime());
            }
            try {
                gfin = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(gfin) + " 23:59:59");
            } catch (ParseException ex) {
                System.out.println("Un error ha ocurrido al procesar la fecha de fin. reporte3Crt");
                gfin = new Date();
            }
            Date r0, r1;
            r0 = new Date(ginicio.getTime());
            do {
                try {
                    r1 = sel == 1 ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(r0) + " 23:59:59")
                            : sel == 2 ? suma1Mes(r0)
                                    : sel == 3 ? suma1Año(r0) : suma1Dia(gfin);
                } catch (ParseException ex) {
                    System.out.println("Un error ha ocurrido al procesar la fecha de gfin. reporte3Crt");
                    r1 = new Date();
                }
                labels.add(new SimpleDateFormat(sel == 1 ? "dd/MM/YY" : sel == 2 ? "MMM/YY" : "YYYY").format(r0));
                values.add(redondeo2decimales(vfl.findByPeriodoFechas(r0, r1).stream().mapToDouble(v -> v.getValor().doubleValue()).sum()));
                System.out.println(getDateTimeToString12H(r0) + " al " + getDateTimeToString12H(r1));//////////////////////
                r0 = sel == 1 ? suma1Dia(r0) : sel == 2 ? suma1Mes(r0) : sel == 3 ? suma1Año(r0) : suma1Dia(gfin);
            } while (!r0.after(gfin));

            if (lineModel != null) {
                Title title = new Title();
                title.setDisplay(true);
                title.setText("Ventas en USD");
                title.setFontColor("rgb(255, 255, 255)");
                title.setFontSize(18);
                dataSet.setLineTension(0.1);
                options.setTitle(title);
                dataSet.setData(values);
                dataSet.setFill(false);
                dataSet.setLabel("Estimación de la variacion de ventas");
                dataSet.setBorderColor("rgb(127, 255, 0)");
                data.addChartDataSet(dataSet);
                data.setLabels(labels);
                lineModel.setOptions(options);
                lineModel.setData(data);
            }
        }
        return lineModel;
    }

    public Date getGinicio() {
        return ginicio;
    }

    public void setGinicio(Date ginicio) {
        this.ginicio = ginicio;
    }

    public Date getGfin() {
        return gfin;
    }

    public void setGfin(Date gfin) {
        this.gfin = gfin;
    }

    public Integer getSel() {
        return sel;
    }

    public void setSel(Integer sel) {
        this.sel = sel;
    }

    private Date suma1Dia(Date r0) {
        Calendar c = Calendar.getInstance();
        c.setTime(r0);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private Date suma1Mes(Date r0) {
        Calendar c = Calendar.getInstance();
        c.setTime(r0);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    private Date suma1Año(Date r0) {
        Calendar c = Calendar.getInstance();
        c.setTime(r0);
        c.add(Calendar.YEAR, 1);
        return c.getTime();
    }

    public LineChartModel getLineModelToProduct() {
        LineChartModel lineModel = sel0 == 0 ? null : new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        LineChartOptions options = new LineChartOptions();

        if (ginicio0 != null && gfin0 != null && invSel0 != null) {
            if (gfin0.before(ginicio0)) {
                Date f = new Date(gfin0.getTime());
                gfin0 = new Date(ginicio0.getTime());
                ginicio0 = new Date(f.getTime());
            }
            try {
                gfin0 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(gfin0) + " 23:59:59");
            } catch (ParseException ex) {
                System.out.println("Un error ha ocurrido al procesar la fecha de fin. reporte3Crt");
                gfin0 = new Date();
            }
            Date r0, r1;
            r0 = new Date(ginicio0.getTime());
            do {
                try {
                    r1 = sel0 == 1 ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(r0) + " 23:59:59")
                            : sel0 == 2 ? suma1Mes(r0)
                                    : sel0 == 3 ? suma1Año(r0) : suma1Dia(gfin0);
                } catch (ParseException ex) {
                    System.out.println("Un error ha ocurrido al procesar la fecha de gfin. reporte3Crt");
                    r1 = new Date();
                }
                labels.add(new SimpleDateFormat(sel0 == 1 ? "dd/MM/YY" : sel0 == 2 ? "MMM/YY" : "YYYY").format(r0));
                values.add(redondeo2decimales(dvfl.findByProductoAndPeriodo(invSel0.getIdInventario(), r0, r1).stream().mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario().doubleValue()).sum()));
                System.out.println(getDateTimeToString12H(r0) + " al " + getDateTimeToString12H(r1));//////////////////////
                r0 = sel0 == 1 ? suma1Dia(r0) : sel0 == 2 ? suma1Mes(r0) : sel0 == 3 ? suma1Año(r0) : suma1Dia(gfin0);
            } while (!r0.after(gfin0));

            if (lineModel != null) {
                Title title = new Title();
                title.setDisplay(true);
                title.setText("Ventas en USD");
                title.setFontColor("rgb(255, 255, 255)");
                title.setFontSize(18);
                dataSet.setLineTension(0.1);
                options.setTitle(title);
                dataSet.setData(values);
                dataSet.setFill(false);
                dataSet.setLabel("Estimación de la variacion de ventas de " + invSel0.getProducto());
                dataSet.setBorderColor("rgb(255, 215, 0)");
                data.addChartDataSet(dataSet);
                data.setLabels(labels);
                lineModel.setOptions(options);
                lineModel.setData(data);
            }
        }
        return lineModel;
    }

    public LineChartModel getLineModelToCostProduct() {
        LineChartModel lineModel = sel1 == 0 ? null : new LineChartModel();
        ChartData data = new ChartData();
        LineChartDataSet dataSet = new LineChartDataSet();
        List<Object> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        LineChartOptions options = new LineChartOptions();

        if (ginicio1 != null && gfin1 != null && invSel1 != null) {
            if (gfin1.before(ginicio1)) {
                Date f = new Date(gfin1.getTime());
                gfin1 = new Date(ginicio1.getTime());
                ginicio1 = new Date(f.getTime());
            }
            try {
                gfin1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(gfin1) + " 23:59:59");
            } catch (ParseException ex) {
                System.out.println("Un error ha ocurrido al procesar la fecha de fin. reporte3Crt");
                gfin1 = new Date();
            }
            Date r0, r1;
            r0 = new Date(ginicio1.getTime());
            double anterior = 0;
            do {
                try {
                    r1 = sel1 == 1 ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(r0) + " 23:59:59")
                            : sel1 == 2 ? suma1Mes(r0)
                                    : sel1 == 3 ? suma1Año(r0) : suma1Dia(gfin1);
                } catch (ParseException ex) {
                    System.out.println("Un error ha ocurrido al procesar la fecha de gfin. reporte3Crt");
                    r1 = new Date();
                }
                labels.add(new SimpleDateFormat(sel1 == 1 ? "dd/MM/YY" : sel1 == 2 ? "MMM/YY" : "YYYY").format(r0));
                double v = redondeo2decimales(Existencias.getCostoAVGPeriodo(invSel1.getIdInventario(), dcfl, r0, r1));
                anterior = v == 0 ? anterior : v;
                values.add(anterior);
                System.out.println(getDateTimeToString12H(r0) + " al " + getDateTimeToString12H(r1));//////////////////////
                r0 = sel1 == 1 ? suma1Dia(r0) : sel1 == 2 ? suma1Mes(r0) : sel1 == 3 ? suma1Año(r0) : suma1Dia(gfin1);
            } while (!r0.after(gfin1));

            if (lineModel != null) {
                Title title = new Title();
                title.setDisplay(true);
                title.setText("Costos en USD");
                title.setFontColor("rgb(255, 255, 255)");
                title.setFontSize(18);
                dataSet.setLineTension(0.1);
                options.setTitle(title);
                dataSet.setData(values);
                dataSet.setFill(false);
                dataSet.setLabel("Estimación de la variacion del costo de " + invSel1.getProducto());
                dataSet.setBorderColor("rgb(178, 34, 34)");
                data.addChartDataSet(dataSet);
                data.setLabels(labels);
                lineModel.setOptions(options);
                lineModel.setData(data);
            }
        }
        return lineModel;
    }

    public void limpiarGrafico() {
        ginicio = null;
        gfin = null;
        sel = 1;
        invSel = null;
    }

    public void limpiarGrafico0() {
        ginicio0 = null;
        gfin0 = null;
        sel0 = 1;
        invSel0 = null;
    }

    public void limpiarGrafico1() {
        ginicio1 = null;
        gfin1 = null;
        sel1 = 1;
        invSel1 = null;
    }

    public Inventario getInvSel() {
        return invSel;
    }

    public void setInvSel(Inventario invSel) {
        this.invSel = invSel;
    }

    public List<Inventario> getInvs() {
        invs = ifl.findAll();
        Collections.sort(invs, (Inventario i0, Inventario i1) -> i0.getProducto().compareToIgnoreCase(i1.getProducto()));
        return invs;
    }

    public Date getGinicio0() {
        return ginicio0;
    }

    public void setGinicio0(Date ginicio0) {
        this.ginicio0 = ginicio0;
    }

    public Date getGfin0() {
        return gfin0;
    }

    public void setGfin0(Date gfin0) {
        this.gfin0 = gfin0;
    }

    public Date getGinicio1() {
        return ginicio1;
    }

    public void setGinicio1(Date ginicio1) {
        this.ginicio1 = ginicio1;
    }

    public Date getGfin1() {
        return gfin1;
    }

    public void setGfin1(Date gfin1) {
        this.gfin1 = gfin1;
    }

    public Integer getSel0() {
        return sel0;
    }

    public void setSel0(Integer sel0) {
        this.sel0 = sel0;
    }

    public Inventario getInvSel0() {
        return invSel0;
    }

    public void setInvSel0(Inventario invSel0) {
        this.invSel0 = invSel0;
    }

    public Integer getSel1() {
        return sel1;
    }

    public void setSel1(Integer sel1) {
        this.sel1 = sel1;
    }

    public Inventario getInvSel1() {
        return invSel1;
    }

    public void setInvSel1(Inventario invSel1) {
        this.invSel1 = invSel1;
    }

}
