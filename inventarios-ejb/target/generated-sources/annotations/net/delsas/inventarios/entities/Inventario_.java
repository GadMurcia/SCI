package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.DetalleVentas;
import net.delsas.inventarios.entities.Librocompras;
import net.delsas.inventarios.entities.Libroventas;
import net.delsas.inventarios.entities.Misc;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(Inventario.class)
public class Inventario_ { 

    public static volatile SingularAttribute<Inventario, String> descripcion;
    public static volatile ListAttribute<Inventario, DetalleCompra> detalleCompraList;
    public static volatile ListAttribute<Inventario, DetalleVentas> detalleVentasList;
    public static volatile SingularAttribute<Inventario, Misc> tienda;
    public static volatile SingularAttribute<Inventario, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<Inventario, Libroventas> libroVentas;
    public static volatile SingularAttribute<Inventario, String> producto;
    public static volatile SingularAttribute<Inventario, Librocompras> libroCompras;
    public static volatile SingularAttribute<Inventario, Integer> idInventario;
    public static volatile SingularAttribute<Inventario, Boolean> activo;

}