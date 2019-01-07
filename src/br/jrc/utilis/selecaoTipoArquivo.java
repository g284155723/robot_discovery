/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.utilis;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author jrcorrea
 */
public class selecaoTipoArquivo  extends FileFilter {
   
    String[] Extencoes;
   
    public selecaoTipoArquivo(String[] extencoes ){
        Extencoes = extencoes;
    }
     
    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        for (int i=0; i<Extencoes.length;i++)
            if (f.getName().indexOf(Extencoes[i])!=-1) {
                return true;
            } 
        
        return false;
    }
 
    //The description of this filter
    public String getDescription() {
        String stExtensoes = "";
    
         for (int i=0; i<Extencoes.length;i++)
            stExtensoes+= "(" + Extencoes[i] +")";
        return stExtensoes;
    }

}
