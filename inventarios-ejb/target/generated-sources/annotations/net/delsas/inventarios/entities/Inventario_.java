package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleVentas;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-17T12:18:49")
@StaticMetamodel(Inventario.class)
public class Inventario_ { 

    public static volatile SingularAttribute<Inventario, String> descripcion;
    public static volatile ListAttribute<Inventario, DetalleCompra> detalleCompraList;
    public static volatile ListAttribute<Inventario, DetalleVentas> detalleVentasList;
    public static volatile SingularAttribute<Inventario, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<Inventario, String> producto;
    public static volatile SingularAttribute<Inventario, Integer> idInventario;

}