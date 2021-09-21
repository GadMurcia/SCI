package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.DetalleVentasPK;
import net.delsas.inventarios.entities.Inventario;
import net.delsas.inventarios.entities.Ventas;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(DetalleVentas.class)
public class DetalleVentas_ { 

    public static volatile SingularAttribute<DetalleVentas, DetalleVentasPK> detalleVentasPK;
    public static volatile SingularAttribute<DetalleVentas, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<DetalleVentas, Ventas> ventas;
    public static volatile SingularAttribute<DetalleVentas, Double> cantidad;
    public static volatile SingularAttribute<DetalleVentas, Inventario> inventario;

}