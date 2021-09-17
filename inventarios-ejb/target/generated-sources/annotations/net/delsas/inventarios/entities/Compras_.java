package net.delsas.inventarios.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.ComprasPK;
import net.delsas.inventarios.entities.DetalleCompra;
import net.delsas.inventarios.entities.Usuario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-17T12:54:31")
@StaticMetamodel(Compras.class)
public class Compras_ { 

    public static volatile ListAttribute<Compras, DetalleCompra> detalleCompraList;
    public static volatile SingularAttribute<Compras, ComprasPK> comprasPK;
    public static volatile SingularAttribute<Compras, BigDecimal> valor;
    public static volatile SingularAttribute<Compras, Usuario> usuario1;
    public static volatile SingularAttribute<Compras, String> comentario;

}