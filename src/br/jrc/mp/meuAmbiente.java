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
public class meuAmbiente implements Ambiente, Runnable{
    
    private MyTela[][] Mapa=null;    
    private JPanel Tela=null;
    private int inLinhaRobo, inColunaRobo;
    private int inQtdLinhas, inQtdColunas, inNivelBateria, inNivelMaximoBateria, inNivelMaximoCacamba;    
    private long loTempoRefresh=40;    
    private Robo objRobo= null;
        
    private int inOperacoesApresentacao = 0;
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
    
    private Estudo objEstudo;
    
    private boolean boRoboFinalizou=false;
        
    private int inIndiceApresentacao;
    
    public boolean isRoboFinalizou(){
        return boRoboFinalizou;
    }
    
    public meuAmbiente(MyTela[][] mapa,  int linhaRobo, int colunaRobo,  int nivelMaximoBateria, int nivelMaximoCacamba, Robo robo) {
        Mapa= mapa;        
        inLinhaRobo = linhaRobo;
        inColunaRobo = colunaRobo;
        inQtdLinhas = mapa.length;
        inQtdColunas = mapa[0].length;
        
        inNivelBateria = nivelMaximoBateria;
        inNivelMaximoBateria = nivelMaximoBateria;
        inNivelMaximoCacamba = nivelMaximoCacamba;
        inValorAnterior = Ambiente.CARREGADOR;        
        
        objRobo= robo;
        objRobo.setAmbiente(this);                        
        
        MapaCopia = new MyTela[inQtdLinhas][inQtdColunas];
        byte[][] valoresIniciais = new byte[inQtdLinhas][inQtdColunas];
        
        inOperacoes=0;
        inOperacoesApresentacao = 0;
        
        for (int i=0; i<inQtdLinhas; i++)
            for (int j=0; j<inQtdColunas; j++){
                MapaCopia[i][j] = new MyTela(null);
                MapaCopia[i][j].setSize(mapa[i][j].getSize());
                MapaCopia[i][j].setValor( mapa[i][j].getValor());
                valoresIniciais[i][j] = mapa[i][j].getValor();
            }
        objEstudo = new Estudo();
        
        objEstudo.setValoresInciais(valoresIniciais);

        
    }
    
    private byte[][] copyArray(MyTela[][] valores, int qtdLinhas, int qtdColunas){
        byte[][] aryMapa = new byte[qtdLinhas][qtdColunas];
         for (int i=0; i<qtdLinhas; i++)
            for (int j=0; j<qtdColunas; j++){
                aryMapa[i][j] = valores[i][j].getValor();
            }
         return aryMapa;
    }
    
    @Override
    public synchronized boolean andar(byte direcao, byte[][] mapeamento, byte[][] mapeamentoLimpeza){
        boolean boRetorno = false;
        
        if (inNivelBateria==0){                       
            objEstudo.addLog(inOperacoes, inOperacoes + " Bateria Acabou...\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
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
    
    @Override
    public synchronized byte ver(byte direcao){
        byte inRetorno = Ambiente.DESCONHECIDO;
        
        if (inNivelBateria==0){                       
            objEstudo.addLog(inOperacoes, inOperacoes + " Bateria Acabou...\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
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
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Frente - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo-1][inColunaRobo].getValor();
    }
    
    private byte verCosta() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Costa - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo+1][inColunaRobo].getValor();
    }
    
    private byte verDireita() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo][inColunaRobo+1].getValor();
    }
    
    private byte verEsquerda() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Esquerda - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo][inColunaRobo-1].getValor();
    }

    
    public boolean recarregarBateria() {
        inNivelBateria--;
        if (inValorAnterior == Ambiente.CARREGADOR){                        
            objEstudo.addLog(inOperacoes, inOperacoes + " - Recarregar Bateria - " + inNivelBateria+ "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            inNivelBateria = inNivelMaximoBateria;
            return true;
        }
        return false;
    }
    
    public int verNivelBateria() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Nível Bateria - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{--inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return inNivelBateria;
    }
    
    public int verNivelMaximoBateria() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Nível Máximo Bateria - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{--inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return  inNivelMaximoBateria;
    }
    
    public boolean pegarLixo() {
         if (inNivelBateria==0){            
            objEstudo.addLog(inOperacoes, inOperacoes + " Bateria Acabou...\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            objRobo.finalizar();     
            return false;
        }
        inNivelBateria--; 
        String stTemp=" Sem Material";
        boolean boRetorno =true;
        
        //a cacamba ja esta cheia
        if((inPapelPego+inPlasticoPego+inMetalPego+inOrganicoPego+inVidroPego)== inNivelMaximoCacamba){
            stTemp=" Caçamba cheia";
            boRetorno =false;
        }        
        switch (inValorAnterior){
            case Ambiente.METAL:                
                inMetalPego++;
                stTemp=" 1 Metal Pego";
                break;
            case Ambiente.PLASTICO:                
                inPlasticoPego++;
                stTemp=" 1 Plástico Pego";
                break;
             case Ambiente.PAPEL:                
                inPapelPego++;
                stTemp=" 1 Papel Pego";
                break;
             case Ambiente.VIDRO:                
                inVidroPego++;
                stTemp=" 1 Vidro Pego";
                break;
             case Ambiente.ORGANICO:                
                inOrganicoPego++;
                stTemp=" 1 Material Orgânico Pego";
                break;
             default:
                 //não tem lixo para ser recolhido
                 boRetorno =false;                     
        }
        inValorAnterior = Ambiente.CHAO;        
        objEstudo.addLog(inOperacoes, inOperacoes + " - " + stTemp + " - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return boRetorno;
    }

    
    @Override
    public boolean descarregarLixo() {
        if (inNivelBateria==0){                       
            objEstudo.addLog(inOperacoes, inOperacoes + " Bateria Acabou...\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            objRobo.finalizar();     
            return false;
        }
       inNivelBateria--;
      String stTemp=" Sem Material";
      boolean boRetorno =true;
       switch (inValorAnterior){
            case Ambiente.COLETOR_METAL_AMARELA:
                stTemp =  inMetalPego + " Unidade(s) de Metal ";
                inMetalDescarregado +=inMetalPego;
                inMetalPego=0;
                
                break;
            case Ambiente.COLETOR_PLASTICO_VERMELHA:
                stTemp =  inPlasticoPego + " Unidade(s) de Plástico ";
                inPlasticoDescarregado+=inPlasticoPego;
                inPlasticoPego=0;
                break;
             case Ambiente.COLETOR_PAPEL_AZUL:
                stTemp =  inPapelPego + " Unidade(s) de Papel "; 
                inPapelDescarregado+=inPapelPego; 
                inPapelPego=0;
                break;
             case Ambiente.COLETOR_VIDRO_VERDE:
                 stTemp =  inVidroPego + " Unidade(s) de Vidro "; 
                inVidroDescarregado+=inVidroPego;
                inVidroPego=0;
                break;
             case Ambiente.COLETOR_ORGANICO_MAROM:
                 stTemp =  inOrganicoPego + " Unidade(s) de Material Orgânico "; 
                inOrganicoDescarregado+=inOrganicoPego; 
                inOrganicoPego=0;
                break;
             default:
                 //não tem lixo para ser recolhido
                boRetorno =false;
        }
       
       objEstudo.addLog(inOperacoes, inOperacoes + " - " + stTemp  + " descarregado(s) - " + inNivelBateria +  "\n");
       objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return boRetorno;
    }

    
    public int verNivelCacamba() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Nível Caçamba - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{--inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return (inPlasticoPego+inVidroPego+inMetalPego+inPapelPego+inOrganicoPego);
        
    }
    
    public int verNivelMaximoCacamba() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Nível Máximo Caçamba - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{--inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return inNivelMaximoCacamba;
    }

    
    public void finalizar() {        
        objEstudo.addLog(inOperacoes, inOperacoes + " - Robo Finalizou - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        boRoboFinalizou = true;
    }
    
    public void finalizarAmbiente(){
        boFinalizar =true;
    }
    
    public void run() {
        //viva robo, torne-se ativo
        new Thread(objRobo).start();
                        
//        do{
//            try {
//                Thread.sleep(loTempoRefresh);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(meuAmbiente.class.getName()).log(Level.SEVERE, null, ex);
//            }
//                        
//            mostrar(inOperacoesApresentacao);
//            
//            inOperacoesApresentacao++;
//            
//        }while( boRoboFinalizou==false || inOperacoesApresentacao<objEstudo.qtdLogs());
//        
//        for (int i=inOperacoesApresentacao; 1<objEstudo.qtdLogs(); i++){
//            mostrar(i);
//        }
//        
//        Log.insert("Fim Ambiente\n",0);
//        
//        inIndiceApresentacao = inOperacoesApresentacao;
//        
//        if (objRobo!=null){
//            objRobo.finalizar();
//        }
    }


    
               
    

    private byte[][] copiarMatriz(byte[][] matriz) {
        byte[][] novaMatriz = new byte[matriz.length][matriz[0].length];
        for(int i=0;i<matriz.length;i++)
            for(int j=0;j<matriz[0].length;j++)
                novaMatriz[i][j]=matriz[i][j];
          
        return novaMatriz;
    }

    private byte verDiagonalCostaDireita() {
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Diagono Costa Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo+1][inColunaRobo+1].getValor();
    }

    private byte verDiagonalCostaEsquerda() {
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Diagono Costa Esquerda - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo+1][inColunaRobo-1].getValor();
    }

    private byte verDiagonalFrenteDireita() {
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Diagonal Frontal Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo-1][inColunaRobo+1].getValor();
    }

    private byte verDiagonoFrenteEsquerda() {
        objEstudo.addLog(inOperacoes, inOperacoes + " - Ver Diagonal Frontal Esquerda - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return MapaCopia[inLinhaRobo-1][inColunaRobo-1].getValor();
    }

    private boolean andarDiagonalCostaDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
        if(MapaCopia[inLinhaRobo+1][inColunaRobo+1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo+1,inColunaRobo+1, Ambiente.ROBO};
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo+1].setValor( Ambiente.ROBO);
            inLinhaRobo++;
            inColunaRobo++;
            objEstudo.addMovimento(inOperacoes,movimento);                        
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Diagonal Costa Direita - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Diagonal Costa Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }

    private boolean andarDiagonalCostaEsquerda(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
        if(MapaCopia[inLinhaRobo+1][inColunaRobo-1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo+1,inColunaRobo-1, Ambiente.ROBO};
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo-1].setValor( Ambiente.ROBO);
            inLinhaRobo++;
            inColunaRobo--;
            objEstudo.addMovimento(inOperacoes,movimento);                        
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Diagonal Costa Direita - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Diagonal Costa Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }

    private boolean andarDiagonalFrenteDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
       if(MapaCopia[inLinhaRobo-1][inColunaRobo+1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo-1,inColunaRobo+1, Ambiente.ROBO};
            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo+1].setValor( Ambiente.ROBO);           
            inLinhaRobo--;  
            inColunaRobo++;
            objEstudo.addMovimento(inOperacoes,movimento);                            
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Diagonal Frontal Direita - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Diagonal Frontal Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }

    private boolean andarDiagonoFrenteEsquerda(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {
       if(MapaCopia[inLinhaRobo-1][inColunaRobo-1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo-1,inColunaRobo-1, Ambiente.ROBO};
            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo-1].setValor( Ambiente.ROBO);           
            inLinhaRobo--;    
            inColunaRobo--;
            objEstudo.addMovimento(inOperacoes,movimento);                            
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Diagonal Frontal Esquerda - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Diagonal Frontal Esquerda - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});            
        return false;
    }
    
    private boolean andarFrente(byte [][] mapeamento, byte[][] mapeamentoLimpeza) {
        
        if(MapaCopia[inLinhaRobo-1][inColunaRobo].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo-1,inColunaRobo, Ambiente.ROBO};
            
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);            
            inValorAnterior=MapaCopia[inLinhaRobo-1][inColunaRobo].getValor();
            MapaCopia[inLinhaRobo-1][inColunaRobo].setValor( Ambiente.ROBO);           
            inLinhaRobo--;            
            objEstudo.addMovimento(inOperacoes,movimento);                            
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Frente - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Frente - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            
        return false;
    }

    
    private boolean andarCosta(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if(MapaCopia[inLinhaRobo+1][inColunaRobo].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo+1,inColunaRobo, Ambiente.ROBO};
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo+1][inColunaRobo].getValor();
            MapaCopia[inLinhaRobo+1][inColunaRobo].setValor( Ambiente.ROBO);
            inLinhaRobo++;
            objEstudo.addMovimento(inOperacoes,movimento);                        
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Costa - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Costa - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }

    
    private boolean andarDireita(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if(MapaCopia[inLinhaRobo][inColunaRobo+1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo,inColunaRobo+1, Ambiente.ROBO};
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo][inColunaRobo+1].getValor();
            MapaCopia[inLinhaRobo][inColunaRobo+1].setValor( Ambiente.ROBO);
            inColunaRobo++;
            objEstudo.addMovimento(inOperacoes,movimento);            
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Direita - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Direita - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }
    
    private boolean andarEsquerda(byte[][] mapeamento, byte[][] mapeamentoLimpeza) {

        if( MapaCopia[inLinhaRobo][inColunaRobo-1].isPisavel()){
            int[] movimento = {inLinhaRobo, inColunaRobo, inValorAnterior,inLinhaRobo,inColunaRobo-1, Ambiente.ROBO};
            MapaCopia[inLinhaRobo][inColunaRobo].setValor(inValorAnterior);
            inValorAnterior=MapaCopia[inLinhaRobo][inColunaRobo-1].getValor();
            MapaCopia[inLinhaRobo][inColunaRobo-1].setValor( Ambiente.ROBO);
            inColunaRobo--;
            objEstudo.addMovimento(inOperacoes,movimento);  
            objEstudo.addLimpeza(inOperacoes, copiarMatriz(mapeamentoLimpeza));   
            objEstudo.addMapeamento(inOperacoes,copiarMatriz(mapeamento));
            objEstudo.addLog(inOperacoes, inOperacoes + " - Andar Esquerda - " + inNivelBateria +  "\n");
            objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
            return true;
        }
        objEstudo.addLog(inOperacoes, inOperacoes + " - FALHA Andar Esquerda - " + inNivelBateria +  "\n");
        objEstudo.addDados(inOperacoes++, new int[]{inNivelBateria, inMetalPego,inOrganicoPego, inPapelPego, inPlasticoPego, inVidroPego, inMetalDescarregado, inOrganicoDescarregado,inPapelDescarregado, inPlasticoDescarregado,inVidroDescarregado});
        return false;
    }
    
    public void primeiraOperacao() {
        inIndiceApresentacao=0;
        
        MapaCopia = new MyTela[inQtdLinhas][inQtdColunas];
                        
        for (int i=0; i<inQtdLinhas; i++)
            for (int j=0; j<inQtdColunas; j++){
                MapaCopia[i][j] = new MyTela(null);
                MapaCopia[i][j].setSize(Mapa[i][j].getSize());
                MapaCopia[i][j].setImagem(objEstudo.getValorInicial(i,j));
            }
        //mostrar(inIndiceApresentacao);
    }
    
    public boolean adiantarOperacao() {
        if (inIndiceApresentacao<inOperacoesApresentacao){
            //mostrar(++inIndiceApresentacao);
            return true;
        }
        return false;
    }
    
    public boolean voltarOperacao() {
        if (inIndiceApresentacao>1){
            //mostrar(--inIndiceApresentacao);
            
            //for (int i=1; i<=inIndiceApresentacao;i++){                
            //    mostrar(i);
            //}            
            return true;
        }
        return false;
    }
    
    void ultimaOperacao() {        
        for (int i=1; i<=inOperacoesApresentacao;i++){
            inIndiceApresentacao = i;
            //mostrar(inIndiceApresentacao);
        }
    }
    
    public int[] getDados(int indice){
        return  (int[]) objEstudo.getDados(indice);
    }
    public String getLog(int indice){
        return (String) objEstudo.getLog(indice);
    }
    public int[] getMovimento(int indice){
        return (int[]) objEstudo.getMovimento(indice);            
    }
    public byte[][] getLimpeza(int indice){
        return (byte[][]) objEstudo.getLimpeza(indice);
    }
    public byte[][] getMapeamento(int indice){
        return (byte[][]) objEstudo.getMapeamento(indice);
    }
//    private void mostrar(int indice) {
//            
//            int[] dados = 
//            String log = 
//            int[] movimento= 
//            byte[][] limpeza= (byte[][]) objEstudo.getLimpeza(indice);
//            byte[][] mapeamento= (byte[][]) objEstudo.getMapeamento(indice);
//            //se tiver novos movimentos mostro na tela
//            if (movimento!=null){                                                                                          
//                 Mapa[movimento[0]][movimento[1]].setImagem((byte) movimento[2]); 
//                 Mapa[movimento[3]][movimento[4]].setImagem((byte) movimento[5]); 
//            }
//            
//            if (dados!=null){
//                NivelCacamba.setText(String.valueOf((dados[1]+dados[2]+dados[3]+dados[4]+dados[5])));
//                NivelBateria.setText(String.valueOf(dados[0]));
//                Log.insert(log,0);
//                NivelCacambaTotal.setText(dados[1] + "me " +  dados[2] + "or " + dados[3] + "pa " + dados[4] + "pl " + dados[5] +  "vi  descarregados " + dados[6] + "me " +  dados[7] + "or " + dados[8] + "pa " + dados[9] + "pl " + dados[10] +  "vi "  );
//                QtdOperacoes.setText(String.valueOf(indice));
//            }
//                                    
//            if (Mapeamento!=null && mapeamento!=null){
//                Mapeamento.setIcon(Funcoes.getScaledImage( Funcoes.gerarImagem(mapeamento), Mapeamento.getWidth(), Mapeamento.getHeight()));
//            }
//            if (Limpeza!=null && limpeza!=null){
//                Limpeza.setIcon(Funcoes.getScaledImage( Funcoes.gerarImagem(limpeza), Limpeza.getWidth(), Limpeza.getHeight()));
//            }
//                        
//            //Tela.repaint();
//    }

    public int getQtdOperacoes() {
        return inOperacoes;
    }
   
}
