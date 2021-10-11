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
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.optional.ReporteDetalleCompra;
import net.delsas.inventarios.optional.auxiliarCtr;
import net.delsas.inventarios.optional.reporteFacturasCompras;
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
public class reportes3Ctr extends auxiliarCtr implements Serializable {

    private Date inicio;
    private Date fin;
    private List<reporteFacturasCompras> facturas;
    private reporteFacturasCompras selected;
    private List<ReporteDetalleCompra> detalle;
    private Optional<Usuario> user;
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
    private InventarioFacadeLocal ifl;

    @PostConstruct
    public void init() {
        facturas = new ArrayList<>();
        detalle = new ArrayList<>();
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

    public void generarReporteCompras() {
        detalle.clear();
        facturas.clear();
        if (inicio != null) {
            arreglarFechas(inicio, fin);
            try {
                fin = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(getDateToString(fin != null ? fin : inicio) + " 23:59:59");
            } catch (ParseException ex) {
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
        Collections.sort(detalle, (ReporteDetalleCompra r1, ReporteDetalleCompra r2) -> r1.getInv().getProducto().toLowerCase().compareToIgnoreCase(r2.getInv().getProducto().toLowerCase()));
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
                    t.addCell(getTextCell("\n\nGenerado en " + getDateTimeToString12H(new Date()) + " /S.C.I./\n", 5, 1, false, false, 9, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
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

    public List<Inventario> getInvs() {
        invs = ifl.findAll();
        Collections.sort(invs, (Inventario i0, Inventario i1) -> i0.getProducto().toLowerCase().compareToIgnoreCase(i1.getProducto().toLowerCase()));
        return invs;
    }

}
