package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.VentasPK;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(Ventas.class)
public class Ventas_ { 

    public static volatile ListAttribute<Ventas, DetalleVentas> detalleVentasList;
    public static volatile SingularAttribute<Ventas, GiroDeCaja> giroDeCaja;
    public static volatile SingularAttribute<Ventas, VentasPK> ventasPK;
    public static volatile SingularAttribute<Ventas, BigDecimal> valor;
    public static volatile SingularAttribute<Ventas, String> comentario;

}