/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.delsas.inventarios.optional;

import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author delsas
 */
public class pprot {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        System.out.println(DigestUtils.md5Hex("delsas"));
        System.out.println(DigestUtils.md5Hex("System.out.println(DigestUtils.md5Hex(\"delsas\"));"));
    }
    
}
