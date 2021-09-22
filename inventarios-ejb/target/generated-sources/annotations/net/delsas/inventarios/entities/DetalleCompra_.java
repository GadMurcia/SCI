package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Compras;
import net.delsas.inventarios.entities.DetalleCompraPK;
import net.delsas.inventarios.entities.Inventario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T18:28:20")
@StaticMetamodel(DetalleCompra.class)
public class DetalleCompra_ { 

    public static volatile SingularAttribute<DetalleCompra, Compras> compras;
    public static volatile SingularAttribute<DetalleCompra, Integer> cantidad;
    public static volatile SingularAttribute<DetalleCompra, DetalleCompraPK> detalleCompraPK;
    public static volatile SingularAttribute<DetalleCompra, BigDecimal> costoUnitario;
    public static volatile SingularAttribute<DetalleCompra, Inventario> inventario;

}