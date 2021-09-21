/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.Converters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import net.delsas.inventarios.Controllers.compraCtr;
import net.delsas.inventarios.entities.Inventario;

/**
 *
 * @author delsas
 */
@Named
@FacesConverter(value = "InventarioConverter", managed = true)
public class InventarioConverter implements Converter<Inventario> {

    
    @Override
    public Inventario getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                Inventario inv = compraCtr.inventarios.stream()
                        .filter(i -> i.getIdInventario().toString().equals(value))
                        .findFirst().orElseGet(() -> null);
                return inv;
            } catch (NumberFormatException e) {
                throw new ConverterException(
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Conversion Error", "Not a valid country."));
            }
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Inventario value) {
        if (value != null) {
            return value.getIdInventario().toString();
        } else {
            return null;
        }
    }
}
