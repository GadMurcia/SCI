/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Controllers;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import net.delsas.inventarios.beans.DetalleCompraFacadeLocal;
import net.delsas.inventarios.beans.DetalleVentasFacadeLocal;
import net.delsas.inventarios.beans.VentasFacadeLocal;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.optional.Existencias;
import net.delsas.inventarios.optional.auxiliarCtr;
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
public class graficoCtr extends auxiliarCtr implements Serializable {

    private Date ginicio;
    private Date gfin;
    private Date ginicio0;
    private Date gfin0;
    private Date ginicio1;
    private Date gfin1;
    private Integer sel;
    private Integer sel0;
    private Inventario invSel0;
    private Integer sel1;
    private Inventario invSel1;

    @EJB
    private VentasFacadeLocal vfl;
    @EJB
    private DetalleCompraFacadeLocal dcfl;
    @EJB
    private DetalleVentasFacadeLocal dvfl;

    @PostConstruct
    public void init() {
        sel = 1;
        sel0 = 1;
        sel1 = 1;
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

    public void onInvSelect(SelectEvent<Inventario> e) {
        switch (e.getComponent().getId()) {
            case "p0":
                invSel0 = e.getObject();
                break;
            case "p1":
                invSel1 = e.getObject();
                break;
            default:
        }
    }

    public void limpiarGrafico() {
        ginicio = null;
        gfin = null;
        getLineModel();
    }

    public void limpiarGrafico0() {
        ginicio0 = null;
        gfin0 = null;
        invSel0 = null;
        getLineModelToProduct();
    }

    public void limpiarGrafico1() {
        ginicio1 = null;
        gfin1 = null;
        invSel1 = null;
        getLineModelToCostProduct();
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

    public Integer getSel() {
        return sel;
    }

    public void setSel(Integer sel) {
        this.sel = sel;
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
