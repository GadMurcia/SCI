package net.delsas.inventarios.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Compras;
import net.delsas.inventarios.entities.GiroDeCaja;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.TipoUsuario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-21T01:22:25")
@StaticMetamodel(Usuario.class)
public class Usuario_ { 

    public static volatile SingularAttribute<Usuario, String> apellidos;
    public static volatile SingularAttribute<Usuario, String> passwd;
    public static volatile ListAttribute<Usuario, Compras> comprasList;
    public static volatile SingularAttribute<Usuario, String> idUsuario;
    public static volatile ListAttribute<Usuario, Misc> miscList;
    public static volatile SingularAttribute<Usuario, TipoUsuario> tipoUsuario;
    public static volatile ListAttribute<Usuario, GiroDeCaja> giroDeCajaList;
    public static volatile SingularAttribute<Usuario, Misc> empresa;
    public static volatile SingularAttribute<Usuario, String> nombres;
    public static volatile SingularAttribute<Usuario, Boolean> activo;

}