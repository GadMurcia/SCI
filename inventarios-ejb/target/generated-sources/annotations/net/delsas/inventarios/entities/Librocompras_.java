package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Inventario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(Librocompras.class)
public class Librocompras_ { 

    public static volatile SingularAttribute<Librocompras, BigInteger> compras;
    public static volatile SingularAttribute<Librocompras, BigDecimal> costo;
    public static volatile SingularAttribute<Librocompras, BigDecimal> subtotal;
    public static volatile SingularAttribute<Librocompras, Integer> idProducto;
    public static volatile SingularAttribute<Librocompras, Inventario> inventario;

}