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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public void onBlour(AjaxBehaviorEvent e) {
    }

    public void onCellEdit(CellEditEvent e) {
        System.out.println("V: " + e.getOldValue().toString());
        System.out.println("N: " + e.getNewValue().toString());
        System.out.println("R: " + e.getRowIndex());
    }

    public double disponibilidad(Inventario i) {
        if (i != null) {
            return new Existencias(i, dcfl, dvfl).getExistencias();
        }
        return 0;
    }

    public double redondeo2decimales(double o) {
        o = o * 100;
        o = Math.round(o);
        o = o / 100;
        return o;
    }

    public double redondeo3decimales(double o) {
        o = o * 1000;
        o = Math.round(o);
        o = o / 1000;
        return o;
    }

    public double redondeo4decimales(double o) {
        o = o * 10000;
        o = Math.round(o);
        o = o / 10000;
        return o;
    }

    public double redondeo5decimales(double o) {
        o = o * 100000;
        o = Math.round(o);
        o = o / 100000;
        return o;
    }

    public String getDateToString(Date d) {
        return d != null ? new SimpleDateFormat("dd/MM/yyyy").format(d) : "";
    }

    public String getDateTimeToString12H(Date d) {
        return d != null ? new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(d) : "";
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
        PdfPCell c1 = new PdfPCell(Phrase.getInstance(txt));
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

    public void arreglarFechas(Date uno, Date dos) {
        uno = uno == null ? dos : uno;
        dos = dos == null ? uno : dos;
        if (dos != null && dos.before(uno)) {
            Date f = new Date(dos.getTime());
            dos = new Date(uno.getTime());
            uno = new Date(f.getTime());
        }
        System.out.println(uno + " **** " + dos);
    }

    public Date CalcularFecha(Date r, int calculo, int valor) {
        Calendar c = Calendar.getInstance();
        c.setTime(r);
        c.add(calculo, valor);
        return c.getTime();
    }

    public Date suma1Dia(Date r0) {
        return CalcularFecha(r0, Calendar.DATE, 1);
    }

    public Date suma1Mes(Date r0) {
        return CalcularFecha(CalcularFecha(r0, Calendar.MONTH, 1), Calendar.DATE, -1);
    }

    public Date suma1Mes0(Date r0) {
        return CalcularFecha(r0, Calendar.MONTH, 1);
    }

    public Date suma1Año(Date r0) {
        return CalcularFecha(CalcularFecha(r0, Calendar.YEAR, 1), Calendar.DATE, -1);
    }

    public Date suma1Año0(Date r0) {
        return CalcularFecha(r0, Calendar.YEAR, 1);
    }

    public Date FechaPrimero(Date r, int v) {
        try {
            switch (v) {
                case 0://poner a cero la hora (00:00:00 del mismo día)
                    return new SimpleDateFormat("dd-MM-yyyy").parse(new SimpleDateFormat("dd-MM-yyyy").format(r));
                case 1://pone la fecha al primero del mismo mes y año
                    return new SimpleDateFormat("dd-MM-yyyy").parse("01-" + new SimpleDateFormat("MM-yyyy").format(r));
                case 2://pone la fecha a primero de enero del mismo año
                    return new SimpleDateFormat("dd-MM-yyyy").parse("01-01-" + new SimpleDateFormat("yyyy").format(r));
                default:
                    return r;
            }
        } catch (ParseException ex) {
            return r;
        }
    }
}
