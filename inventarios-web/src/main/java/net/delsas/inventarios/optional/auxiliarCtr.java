/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.optional;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.InventarioFacadeLocal;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Usuario;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import org.primefaces.component.export.ExportConfiguration;
import org.primefaces.event.CellEditEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;

/**
 *
 * @author delsas
 */
@RequestScoped
@Named
public class auxiliarCtr implements Serializable {

    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;

    @EJB
    private InventarioFacadeLocal ifl;

    public void onBlour(AjaxBehaviorEvent e) {
    }

    public void onCellEdit(CellEditEvent e) {
        System.out.println("V: " + e.getOldValue().toString());
        System.out.println("N: " + e.getNewValue().toString());
        System.out.println("R: " + e.getRowIndex());
    }

    public double disponibilidad(Inventario i) {
        if (i != null) {
            Existencias e = new Existencias(i, dcfl, dvfl);
            return e.getExistencias();
        }
        return 0;
    }

    public double redondeo2decimales(double o) {
        o = o * 100;
        o = Math.round(o);
        o = o / 100;
        return o;
    }

    public String getDateToString(Date d) {
        return d != null ? new SimpleDateFormat("dd/MM/yyyy").format(d) : "";
    }

    public String getDoc(byte[] doc, String ex) {
        String a = doc == null ? ""
                : "data:" + ex + ";base64, " + new String(Base64.encodeBase64(doc));
        return a;
    }

    public OutputStream getOutputStream(FacesContext fc, ExportConfiguration ec, String tipoArchivo) throws IOException {
        fc.getExternalContext().setResponseContentType(tipoArchivo);
        fc.getExternalContext().setResponseHeader("Expires", "0");
        fc.getExternalContext().setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        fc.getExternalContext().setResponseHeader("Pragma", "public");
        fc.getExternalContext().setResponseHeader("Content-disposition", ComponentUtils.createContentDisposition("attachment", ec.getOutputFileName() + ".pdf"));
        fc.getExternalContext().addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
        return fc.getExternalContext().getResponseOutputStream();
    }

    public PdfPCell getCellWithImagen(FacesContext fc, String nombreImg, int colSpan, int rowSpan, boolean borde, int imgScale, int aligment) {
        PdfPCell c1;
        String logo = fc.getExternalContext().getRealPath("") + File.separator + "resources" + File.separator + "img" + File.separator + nombreImg;
        try {
            InputStream is = new FileInputStream(logo);
            Image i = Image.getInstance(IOUtils.toByteArray(is));
            i.scalePercent(imgScale);
            c1 = new PdfPCell(i);
        } catch (BadElementException | IOException ex) {
            c1 = new PdfPCell(new Phrase(""));
        }
        c1.setColspan(colSpan);
        c1.setRowspan(rowSpan);
        if (!borde) {
            c1.setBorder(0);
        }
        c1.setHorizontalAlignment(aligment);
        return c1;
    }

    public PdfPCell getTextCell(String txt, int cSpan, int rSpan, boolean nWrap, boolean borde, int tSize, int tStyle, int hAligmnet, int vAligment) {
        PdfPCell c1 = new PdfPCell(new Phrase(txt));
        c1.setRowspan(rSpan);
        c1.setColspan(cSpan);
        c1.setNoWrap(nWrap);
        if (!borde) {
            c1.setBorder(0);
        }
        c1.getPhrase().getFont().setSize(tSize);
        c1.getPhrase().getFont().setStyle(tStyle);
        c1.setHorizontalAlignment(hAligmnet);
        c1.setVerticalAlignment(vAligment);
        return c1;
    }

    public String getNombreususario(Usuario u) {
        return u == null ? "" : u.getNombres() + " " + u.getApellidos();
    }
}
