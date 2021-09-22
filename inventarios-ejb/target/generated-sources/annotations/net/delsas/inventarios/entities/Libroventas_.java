package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Inventario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T18:28:20")
@StaticMetamodel(Libroventas.class)
public class Libroventas_ { 

    public static volatile SingularAttribute<Libroventas, BigDecimal> precio;
    public static volatile SingularAttribute<Libroventas, Double> ventas;
    public static volatile SingularAttribute<Libroventas, Integer> idProducto;
    public static volatile SingularAttribute<Libroventas, Double> subTotal;
    public static volatile SingularAttribute<Libroventas, Inventario> inventario;

}