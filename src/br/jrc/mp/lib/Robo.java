/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.jrc.mp.lib;

/**
 *
 * @author jrcorrea
 */
public interface Robo extends Runnable{
  
    
    public void setAmbiente(Ambiente amb);    
    public String getNome();
    public void finalizar();
    
    
}
