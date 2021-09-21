package net.delsas.inventarios.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Usuario;
import net.delsas.inventarios.entities.Ventas;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(GiroDeCaja.class)
public class GiroDeCaja_ { 

    public static volatile SingularAttribute<GiroDeCaja, Double> cierre;
    public static volatile SingularAttribute<GiroDeCaja, Usuario> responsable;
    public static volatile SingularAttribute<GiroDeCaja, Integer> idGiroDeCaja;
    public static volatile SingularAttribute<GiroDeCaja, Double> excedentes;
    public static volatile SingularAttribute<GiroDeCaja, Double> faltantes;
    public static volatile SingularAttribute<GiroDeCaja, Date> inicio;
    public static volatile ListAttribute<GiroDeCaja, Ventas> ventasList;
    public static volatile SingularAttribute<GiroDeCaja, Date> fin;
    public static volatile SingularAttribute<GiroDeCaja, Double> retiros;
    public static volatile SingularAttribute<GiroDeCaja, Double> cajaInicial;
    public static volatile SingularAttribute<GiroDeCaja, String> detalleRetiros;

}