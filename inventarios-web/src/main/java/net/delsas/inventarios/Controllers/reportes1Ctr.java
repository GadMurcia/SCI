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
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.beans.MiscFacadeLocal;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.optional.Existencias;
import net.delsas.inventarios.optional.auxiliarCtr;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.component.export.Exporter;
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

    @EJB
    private MiscFacadeLocal mfl;
    @EJB
    private InventarioFacadeLocal ifl;
    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;

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
                    t.addCell(getTextCell("Costo Unitario", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Costo Total", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Precio de Venta", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Valor Total", 1, 1, false, true, 14, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
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
                    t.addCell(getTextCell("Costo total del inventario:", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("$ " + redondeo2decimales(ex.stream().mapToDouble(Existencias::getCostoTotal).sum()), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 4, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Valor de venta total del inventario:", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("$ " + redondeo2decimales(ex.stream().mapToDouble(Existencias::getValorTotal).sum()), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 4, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Utilidad esperada total:", 3, 1, false, true, 12, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("$ " + redondeo2decimales(ex.stream().mapToDouble(Existencias::getUtilidad).sum()), 1, 1, false, true, 12, Font.BOLD, PdfPCell.ALIGN_CENTER, PdfPCell.ALIGN_MIDDLE));

                    t.addCell(getTextCell("", 8, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 8, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("", 6, 1, false, false, 12, Font.NORMAL, PdfPCell.ALIGN_LEFT, PdfPCell.ALIGN_MIDDLE));
                    t.addCell(getTextCell("Generado el " + (new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date())) + " /S.C.I./", 2, 1, false, false, 10, Font.NORMAL, PdfPCell.ALIGN_RIGHT, PdfPCell.ALIGN_MIDDLE));

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
}
