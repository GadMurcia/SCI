package net.delsas.inventarios.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import net.delsas.inventarios.entities.Usuario;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2021-09-19T22:29:10")
@StaticMetamodel(TipoUsuario.class)
public class TipoUsuario_ { 

    public static volatile SingularAttribute<TipoUsuario, String> tpComentario;
    public static volatile SingularAttribute<TipoUsuario, Integer> idTipoUsuario;
    public static volatile ListAttribute<TipoUsuario, Usuario> usuarioList;
    public static volatile SingularAttribute<TipoUsuario, String> nombre;

}