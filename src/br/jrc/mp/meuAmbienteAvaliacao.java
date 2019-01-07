/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.mp;

import br.jrc.mp.lib.Ambiente;
import static br.jrc.mp.lib.Ambiente.COSTA;
import static br.jrc.mp.lib.Ambiente.DIAGONAL_COSTA_DIREITA;
import static br.jrc.mp.lib.Ambiente.DIAGONAL_COSTA_ESQUERDA;
import static br.jrc.mp.lib.Ambiente.DIAGONAL_FRENTE_DIREITA;
import static br.jrc.mp.lib.Ambiente.DIAGONAL_FRENTE_ESQUERDA;
import static br.jrc.mp.lib.Ambiente.DIREITA;
import static br.jrc.mp.lib.Ambiente.ESQUERDA;
import static br.jrc.mp.lib.Ambiente.FRENTE;
import br.jrc.mp.lib.Robo;
import br.jrc.utilis.Funcoes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author jrcorrea
 */
public class meuAmbienteAvaliacao implements Ambiente, Runnable{
            
    private int inLinhaRobo, inColunaRobo;
    private int inQtdLinhas, inQtdColunas, inNivelBateria, inNivelMaximoBateria, inNivelMaximoCacamba;    
    
    private Robo objRobo= null;
            
    private int inOperacoes = 0;
    
    private MyTela[][] MapaCopia=null;
    private byte inValorAnterior;
    private int inPapelPego=0;
    private int inPlasticoPego=0;
    private int inOrganicoPego=0;
    private int inMetalPego=0;
    private int inVidroPego=0;    
    
    private int inPapelDescarregado=0;
    private int inPlasticoDescarregado=0;
    private int inOrganicoDescarregado=0;
    private int inMetalDescarregado=0;
    private int inVidroDescarregado=0;
    
    private boolean boFinalizar=false;                
    
    private boolean boRoboFinalizou=false;
    private final int inLinhaCarregador;
    private final int inColunaCarregador;
    
    public int getDistanciaRoboCarregador(){
        int distancia = 0;                
        distancia =(int) Math.sqrt(Math.pow(inLinhaCarregador-inLinhaRobo, 2)+Math.pow(inColunaCarregador-inColunaRobo, 2));        
        return  distancia;
    }
    
    public int getQtdPapel(){
        return  inPapelDescarregado;
    } 
    
    public int getQtdMetal(){
        return  inMetalDescarregado;
    }
    
    public int getQtdPlastico(){
        return  inPlasticoDescarregado;
    }

    public int getQtdVidro(){
        return  inVidroDescarregado;
    }

    public int getQtdOrganico(){
        return  inOrganicoDescarregado;
    }    
    
    public int getQtdOperacoes(){
        return inOperacoes;
    }
    
    public boolean isRoboFinalizou(){
        return boRoboFinalizou;
    }
        
    public meuAmbienteAvaliacao(MyTela[][] mapa,  int linhaRobo, int colunaRobo,  int nivelMaximoBateria, int nivelMaximoCacamba) {
                
        inLinhaRobo = linhaRobo;
        inColunaRobo = colunaRobo;
        inLinhaCarregador = linhaRobo;
        inColunaCarregador = colunaRobo;
        
        inQtdLinhas = mapa.length;
        inQtdColunas = mapa[0].length;
        
        inNivelBateria = nivelMaximoBateria;
        inNivelMaximoBateria = nivelMaximoBateria;
        inNivelMaximoCacamba = nivelMaximoCacamba;
        inValorAnterior = Ambiente.CARREGADOR;        
                                 
        MapaCopia = new MyTela[inQtdLinhas][inQtdColunas];
        byte[][] valoresIniciais = new byte[inQtdLinhas][inQtdColunas];
        
        inOperacoes=0;        
        
        for (int i=0; i<inQtdLinhas; i++)
            for (int j=0; j<inQtdColunas; j++){
                MapaCopia[i][j] = new MyTela(null);
                MapaCopia[i][j].setSize(mapa[i][j].getSize());
                MapaCopia[i][j].setValor( mapa[i][j].getValor());
                valoresIniciais[i][j] = mapa[i][j].getValor();
            }                                
    }
       
    public synchronized boolean andar(byte direcao, byte[][] mapeamento, byte[][] mapeamentoLimpeza){
        boolean boRetorno = false;
        inOperacoes++;
        if (inNivelBateria==0){                                   
            objRobo.finalizar();     
            return false;
        }
        inNivelBateria--;        
        switch (direcao){
            case FRENTE:
                boRetorno = andarFrente( mapeamento, mapeamentoLimpeza);
                break;
            case DIREITA:
                boRetorno = andarDireita(mapeamento, mapeamentoLimpeza);
                break;
            case ESQUERDA:
                boRetorno = andarEsquerda(mapeamento, mapeamentoLimpeza);
                break;
            case COSTA:
                boRetorno = andarCosta(mapeamento, mapeamentoLimpeza);
                break;
           case DIAGONAL_COSTA_DIREITA:
                boRetorno = andarDiagonalCostaDireita(mapeamento, mapeamentoLimpeza);
                break;
            case DIAGONAL_COSTA_ESQUERDA:
                boRetorno = andarDiagonalCostaEsquerda(mapeamento, mapeamentoLimpeza);
                break;
             case DIAGONAL_FRENTE_DIREITA:
                boRetorno = andarDiagonalFrenteDireita(mapeamento, mapeamentoLimpeza);
                break;
             case DIAGONAL_FRENTE_ESQUERDA:
                boRetorno = andarDiagonoFrenteEsquerda(mapeamento, mapeamentoLimpeza);
                break;   
        }
        return boRetorno;
    }
    
    public synchronized byte ver(byte direcao){
        byte inRetorno = Ambiente.DESCONHECIDO;
        inOperacoes++;
        if (inNivelBateria==0){                                   
            objRobo.finalizar();     
            return Ambiente.DESCONHECIDO;
        }
        inNivelBateria--;
        switch (direcao){
            case FRENTE:
                inRetorno = verFrente();
                break;
            case DIREITA:
                inRetorno = verDireita();
                break;
            case ESQUERDA:
                inRetorno = verEsquerda();
                break;
            case COSTA:
                inRetorno = verCosta();
                break;
            case Ambiente.DIAGONAL_COSTA_DIREITA:
                inRetorno = verDiagonalCostaDireita();
                break;
            case Ambiente.DIAGONAL_COSTA_ESQUERDA:
                inRetorno = verDiagonalCostaEsquerda();
                break;
             case Ambiente.DIAGONAL_FRENTE_DIREITA:
                inRetorno = verDiagonalFrenteDireita();
                break;
             case Ambiente.DIAGONAL_FRENTE_ESQUERDA:
                inRetorno = verDiagonoFrenteEsquerda();
                break;    
        }
        return inRetorno;
    }
        
    private byte verFrente() {        
        return MapaCopia[inLinhaRobo-1][inColunaRobo].getValor();
    }
    
    private byte verCosta() {                
        return MapaCopia[inLinhaRobo+1][inColunaRobo].getValor();
    }
    
    private byte verDireita() {                
        return MapaCopia[inLinhaRobo][inColunaRobo+1].getValor();
    }
    
    private byte verEsquerda() {                
        return MapaCopia[inLinhaRobo][inColunaRobo-1].getValor();
    }

    
    public boolean recarregarBateria() {
        inNivelBateria--;
        inOperacoes++;
        if (inValorAnterior == Ambiente.CARREGADOR){                                    
            inNivelBateria = inNivelMaximoBateria;
            return true;
        }
        return false;
    }
    
    public int verNivelBateria() {        
        inOperacoes++;
        return inNivelBateria;
    }
    
    public int verNivelMaximoBateria() {        
        inOperacoes++;
        return  inNivelMaximoBateria;
    }
    
    public boolean pegarLixo() {
        inOperacoes++;
        if (inNivelBateria==0){                                    
            objRobo.finalizar();     
            return false;
        }
        
        inNivelBateria--;         
        boolean boRetorno =true;
        
        //a cacamba ja esta cheia
        if((inPapelPego+inPlasticoPego+inMetalPego+inOrganicoPego+inVidroPego)== inNivelMaximoCacamba){            
            boRetorno =false;
        }        
        switch (inValorAnterior){
            case Ambiente.METAL:                
                inMetalPego++;                
                break;
            case Ambiente.PLASTICO:                
                inPlasticoPego++;                
                break;
             case Ambiente.PAPEL:                
                inPapelPego++;                
                break;
             case Ambiente.VIDRO:                
                inVidroPego++;                
                break;
             case Ambiente.ORGANICO:                
                inOrganicoPego++;                
                break;
             default:
                 //não tem lixo para ser recolhido
                 boRetorno =false;                     
        }
        inValorAnterior = Ambiente.CHAO;                
        return boRetorno;
    }

    
    @Override
    public boolean descarregarLixo() {
        inOperacoes++;
        if (inNivelBateria==0){                                   
            objRobo.finalizar();     
            return false;
        }
      inNivelBateria--;      
      boolean boRetorno =true;
      switch (inValorAnterior){
            case Ambiente.COLETOR_METAL_AMARELA:           
                inMetalDescarregado +=inMetalPego;
                inMetalPego=0;                
                break;
            case Ambiente.COLETOR_PLASTICO_VERMELHA:                
                inPlasticoDescarregado+=inPlasticoPego;
                inPlasticoPego=0;
                break;
             case Ambiente.COLETOR_PAPEL_AZUL:             
                inPapelDescarregado+=inPapelPego; 
                inPapelPego=0;
                break;
             case Ambiente.COLETOR_VIDRO_VERDE:           
                inVidroDescarregado+=inVidroPego;
                inVidroPego=0;
                break;
             case Ambiente.COLETOR_ORGANICO_MAROM:                 
                inOrganicoDescarregado+=inOrganicoPego; 
                inOrganicoPego=0;
                break;
             default:
                 //não tem lixo para ser recolhido
                boRetorno =false;
        }             
        return boRetorno;
    }

    
    public int verNivelCacamba() {        
        inOperacoes++;
        return (inPlasticoPego+inVidroPego+inMetalPego+inPapelPego+inOrganicoPego);       
    }
    
    public int verNivelMaximoCacamba() {        
        inOperacoes++;
        return inNivelMaximoCacamba;
    }

    
    public void finalizar() {        
        inOperacoes++;
        boRoboFinalizou = true;
    }
    
    public void finalizarAmbiente(){
        boFinalizar =true;
    }
    
    public void run() {
        //viva robo, torne-se ativo
        new Thread(objRobo).start();                        
    }

    void setRobo(Robo robo) {
        objRobo= robo;
        objRobo.setAmbiente(this);
        
    }
    
    private byte verDiagonalCostaDireita() {        
        return MapaCopia[inLinhaRobo+1][inColunaRobo+1].getValor();
    }

    private byte verDiagonalCostaEsquerda() {        
        return MapaCopia[inLinhaRobo+1][inColunaRobo-1].getValor();
    }

    private byte verDiagonalFrenteDireita() {                        
        return MapaCopia[inLinhaRobo-1][inColunaRobo+1].getValor();
    }

    private byte verDiagonoFrenteEsquerda() {        
        return MapaCopia[inLinhaRobo-1][inColunaRobo-1].getValor();
    }

    private boolean andarDiagonalCostaDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
        if(MapaCopia[inLinhaRobo+1][inColunaRobo+1].isPisavel()){            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo+1].setValor( Ambiente.ROBO);
            inLinhaRobo++;
            inColunaRobo++;            
            return true;
        }
        return false;
    }

    private boolean andarDiagonalCostaEsquerda(byte [][] mapeamento, byte[][] mapeamentoLimpeza) {
        if(MapaCopia[inLinhaRobo+1][inColunaRobo-1].isPisavel()){            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo-1].setValor( Ambiente.ROBO);
            inLinhaRobo++;
            inColunaRobo--;            
            return true;
        }
        return false;
    }

    private boolean andarDiagonalFrenteDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
       if(MapaCopia[inLinhaRobo-1][inColunaRobo+1].isPisavel()){                        
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo+1].setValor( Ambiente.ROBO);           
            inLinhaRobo--;  
            inColunaRobo++;            
            return true;
        }
        return false;
    }

    private boolean andarDiagonoFrenteEsquerda(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
        if(MapaCopia[inLinhaRobo-1][inColunaRobo-1].isPisavel()){                        
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo-1].setValor( Ambiente.ROBO);           
            inLinhaRobo--;    
            inColunaRobo--;            
            return true;
        }
        return false;
    }
    
    private boolean andarFrente(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {        
        if(MapaCopia[inLinhaRobo-1][inColunaRobo].isPisavel()){                        
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo].setValor( Ambiente.ROBO);           
            inLinhaRobo--;                        
            return true;
        }
        return false;
    }

    
    private boolean andarCosta(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if(MapaCopia[inLinhaRobo+1][inColunaRobo].isPisavel()){            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo].setValor( Ambiente.ROBO);
            inLinhaRobo++;            
            return true;
        }
        return false;
    }

    
    private boolean andarDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if(MapaCopia[inLinhaRobo][inColunaRobo+1].isPisavel()){            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo][inColunaRobo+1].setValor( Ambiente.ROBO);
            inColunaRobo++;            
            return true;
        }
        return false;
    }
    
    private boolean andarEsquerda(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if( MapaCopia[inLinhaRobo][inColunaRobo-1].isPisavel()){            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo][inColunaRobo-1].setValor( Ambiente.ROBO);
            inColunaRobo--;            
            return true;
        }
        return false;
    }   
   
}
