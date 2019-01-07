/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.mp;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author jrcorrea
 */
public class Estudo implements Serializable{
    
    private HashMap Movimentos = new HashMap();    
    private HashMap Mapeamento = new HashMap(); 
    private HashMap Limpezas = new HashMap();
    private HashMap Dados = new HashMap();
    private HashMap Logs = new HashMap();
    private int CacambaMaximoNivel;
    private int BateriaMaximoNivel;
    private byte[][] ValoresIniciais;
    
    public void setCacambaMaximoNivel(int nivel){
        CacambaMaximoNivel = nivel;
    }
    
    public void setBateriaMaximoNivel(int nivel){
        BateriaMaximoNivel = nivel;
    }
    
     public int getCacambaMaximoNivel(int nivel){
        return CacambaMaximoNivel;
    }
    
    public int getBateriaMaximoNivel(){
        return BateriaMaximoNivel;
    }
    
    public void addMovimento(int indece, int[]movimento){
        Movimentos.put(indece, movimento);
    }
     
    public void addLimpeza(int indece, byte[][] limpeza){
        Limpezas.put(indece, limpeza);
    }
    public void addDados(int indice, int[] dados){
        Dados.put(indice, dados);
    }
    public void addLog(int indice, String log){
        Logs.put(indice, log);
    }
    
    public int[]getMovimento(int indice){
        int[]inTemp = (int[])Movimentos.get(indice);        
        return inTemp;
    }
         
    public byte[][] getLimpeza(int indice){
        byte[][] inTemp = (byte[][])Limpezas.get(indice);        
        return inTemp;
    }
    
    public int[] getDados(int indice){
        int[] inTemp = (int[])Dados.get(indice);        
        return inTemp;
    }
    
    public String getLog(int indice){
        String log = (String)Logs.get(indice);
        return log;
    }

    public int qtdMovimentos() {
        return Movimentos.size();
    }

    public int qtdLogs() {
        return Logs.size();
    }
    
    boolean temMovimentos(int inIndiceApresentacao) {
        return Movimentos.containsKey(inIndiceApresentacao);
    }

    boolean temLog(int inIndiceApresentacao) {
        return Logs.containsKey(inIndiceApresentacao);
    }

    void addMapeamento(int inOperacoes, byte[][] mapeamento) {
        Mapeamento.put(inOperacoes, mapeamento);
    }
     public byte[][] getMapeamento(int indice){
        byte[][] inTemp = (byte[][])Mapeamento.get(indice);        
        return inTemp;
    }

    public void setValoresInciais(byte[][] valoresIniciais) {
        ValoresIniciais = valoresIniciais;
    }
    
    public byte getValorInicial(int linha, int coluna){
        return  ValoresIniciais[linha][coluna];
    }
}
