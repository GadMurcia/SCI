package net.delsas.inventarios.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Misc;
import net.delsas.inventarios.entities.Usuario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-17T12:18:49")
@StaticMetamodel(Misc.class)
public class Misc_ { 

    public static volatile ListAttribute<Misc, Misc> sucursales;
    public static volatile ListAttribute<Misc, Usuario> usuarioList;
    public static volatile SingularAttribute<Misc, Integer> idMisc;
    public static volatile SingularAttribute<Misc, Usuario> propietario;
    public static volatile SingularAttribute<Misc, String> direccion;
    public static volatile SingularAttribute<Misc, byte[]> logo;
    public static volatile SingularAttribute<Misc, String> telefonos;
    public static volatile SingularAttribute<Misc, String> nombre;
    public static volatile SingularAttribute<Misc, Misc> matriz;

}