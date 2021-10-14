/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.delsas.inventarios.Converters;

import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import net.delsas.inventarios.Controllers.reportes2Ctr;
import net.delsas.inventarios.entities.Usuario;

/**
 *
 * @author delsas
 */
@Named
@FacesConverter(value = "usuarioConverter", managed = true)
public class UserConverter implements Converter<Usuario> {

    @Override
    public Usuario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                Usuario inv = (context.getExternalContext().getRequestServletPath().contains("reportes")
                        ? reportes2Ctr.userList : new ArrayList<Usuario>())
                        .stream().filter(u -> u.getIdUsuario().equals(value))
                        .findFirst().orElseGet(() -> null);
                return inv;
            } catch (NumberFormatException e) {
                throw new ConverterException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Conversion Error", "Not a valid user."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Usuario value) {
        if (value != null) {
            return value.getIdUsuario();
        } else {
            return null;
        }
    }
}
