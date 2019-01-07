/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.robo;

import br.jrc.mp.lib.Ambiente;
import br.jrc.mp.lib.Robo;

/**
 *
 * @author jrcorrea
 */
public class jrcRobo implements Robo{

    private Ambiente meuAmbiente = null;  
    
    private int[][] Mapeamento = new int[51][51];
    private int[][] MapeamentoLimpeza = new int[51][51];
    private boolean boFinalizar=false;
    private int inLinhaRobo = 26;
    private int inColunaRobo = 26;
    
    public void setAmbiente(Ambiente amb) {
        meuAmbiente = amb;
    }
    
    public int[][] getMapeamento() {
        return Mapeamento;
    }
    
    public int[][] getMapeamentoLimpeza() {
        return MapeamentoLimpeza;
    }

    private boolean andar(int direcao ){
            boolean boRetorno = false;
            switch(direcao){
                case Ambiente.FRENTE:
                    if (meuAmbiente.andar(Ambiente.FRENTE, Mapeamento, MapeamentoLimpeza)){
                        inLinhaRobo--;
                        boRetorno=true;
                    }
                    break;
                case Ambiente.COSTA:
                    if(meuAmbiente.andar(Ambiente.COSTA, Mapeamento, MapeamentoLimpeza)){
                        inLinhaRobo++;
                        boRetorno=true;
                    }
                    break;
                case Ambiente.DIREITA:
                    if(meuAmbiente.andar(Ambiente.DIREITA, Mapeamento, MapeamentoLimpeza)){
                        inColunaRobo++;
                        boRetorno=true;
                    }
                    break;
                case Ambiente.ESQUERDA:
                    if(meuAmbiente.andar(Ambiente.ESQUERDA, Mapeamento, MapeamentoLimpeza)){
                        inColunaRobo--;
                        boRetorno=true;
                    }
                    break;                              
            } 
            return boRetorno;
    }
    
    private void alterarTamanhoMatriz(int tamanho){
        int [][] novaMatriz = new int[Mapeamento.length + tamanho][Mapeamento.length+tamanho];
        int [][] novaMatriz1 = new int[Mapeamento.length + tamanho][Mapeamento.length+tamanho];
        for (int i=0;i<Mapeamento.length; i++)
            for (int j=0;j<Mapeamento.length; j++){
                novaMatriz[i + (tamanho/2)][j + (tamanho/2)] = Mapeamento[i][j];
                novaMatriz1[i + (tamanho/2)][j + (tamanho/2)] = MapeamentoLimpeza[i][j];
            }
        
        inLinhaRobo+=(tamanho/2);
        inColunaRobo+=(tamanho/2);
        Mapeamento = novaMatriz;
        MapeamentoLimpeza = novaMatriz1;
    }
    
    public void run() {
        int inConte=0;
        
        int inDirecao=Ambiente.FRENTE;
        Mapeamento[inLinhaRobo][inColunaRobo] = Ambiente.CARREGADOR;
        MapeamentoLimpeza[inLinhaRobo][inColunaRobo] = Ambiente.CARREGADOR;
        while(!boFinalizar){
            int inVisao=Ambiente.DESCONHECIDO;
            //vejo primeiro o ambiente, para onde vou andar
            switch(inDirecao){
                case Ambiente.FRENTE:
                    if(inLinhaRobo==0) alterarTamanhoMatriz(6);                    
                    if (Mapeamento[inLinhaRobo-1][inColunaRobo]==Ambiente.DESCONHECIDO)   {
                       inVisao =meuAmbiente.ver(Ambiente.FRENTE);
                       Mapeamento[inLinhaRobo-1][inColunaRobo] = inVisao;
                       MapeamentoLimpeza[inLinhaRobo-1][inColunaRobo] = inVisao;
                    }else{
                       inVisao++; 
                    }
                 break;
                case Ambiente.COSTA:                    
                    if(inLinhaRobo+1==Mapeamento.length) alterarTamanhoMatriz(6);                    
                    if (Mapeamento[inLinhaRobo+1][inColunaRobo]==Ambiente.DESCONHECIDO)   {
                       inVisao =meuAmbiente.ver(Ambiente.COSTA);
                       Mapeamento[inLinhaRobo+1][inColunaRobo] = inVisao;
                       MapeamentoLimpeza[inLinhaRobo+1][inColunaRobo] = inVisao;
                    }else{
                       inVisao++; 
                    }
                 break;                                                                                
                case Ambiente.DIREITA:
                   if(inColunaRobo+1==Mapeamento.length) alterarTamanhoMatriz(6);
                   if (Mapeamento[inLinhaRobo][inColunaRobo+1]==Ambiente.DESCONHECIDO)   {
                      inVisao =meuAmbiente.ver(Ambiente.DIREITA);
                      Mapeamento[inLinhaRobo][inColunaRobo+1] = inVisao;
                      MapeamentoLimpeza[inLinhaRobo][inColunaRobo+1] = inVisao;
                   }else{
                      inVisao++; 
                   }
                 break;
                case Ambiente.ESQUERDA:
                    if(inColunaRobo==0) alterarTamanhoMatriz(6);
                   if (Mapeamento[inLinhaRobo][inColunaRobo-1]==Ambiente.DESCONHECIDO)   {
                      inVisao =meuAmbiente.ver(Ambiente.ESQUERDA);
                      Mapeamento[inLinhaRobo][inColunaRobo-1] = inVisao;
                      MapeamentoLimpeza[inLinhaRobo][inColunaRobo-1] = inVisao;
                   }else{
                      inVisao++; 
                   }
                 break;
                 
            }
            
            //depois ver o ambiente dou um passo
            switch(inVisao){
                case Ambiente.CHAO:
                     andar(inDirecao);
                    break;                
                case Ambiente.CARREGADOR:
                    andar(inDirecao);
                    meuAmbiente.recarregarBateria();                    
                    break; 
                case Ambiente.COLETOR_METAL_AMARELA:
                case Ambiente.COLETOR_PAPEL_AZUL:
                case Ambiente.COLETOR_VIDRO_VERDE:    
                case Ambiente.COLETOR_PLASTICO_VERMELHA:    
                case Ambiente.COLETOR_ORGANICO_MAROM:    
                    andar(inDirecao);
                    meuAmbiente.descarregarLixo();                    
                    break;                                               
                case Ambiente.METAL:
                case Ambiente.ORGANICO:
                case Ambiente.PLASTICO:    
                case Ambiente.VIDRO:
                case Ambiente.PAPEL:    
                    andar(inDirecao);
                    if (meuAmbiente.pegarLixo()){
                       Mapeamento[inLinhaRobo][inColunaRobo] = Ambiente.CHAO; 
                    }                    
                    break; 
                default:
                    inDirecao++;
                    if(inDirecao>=5){                        
                        inDirecao=(int)(Math.random() * 4)+1;
                    }
            }
                        
            inConte++;
            if (inConte==5000){
                break;
            }
        }  
        meuAmbiente.finalizar();
        
    }

    
    public String getNome() {
        return "Robo do Jesus";
    }

    
    public void finalizar() {
        boFinalizar = true;
    }
    
}
