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
public interface Ambiente {
 
        public final byte FRENTE = 1;
        public final byte DIREITA = 2;
        public final byte ESQUERDA = 3;
        public final byte COSTA = 4;
        public final byte DIAGONAL_FRENTE_DIREITA = 5;
        public final byte DIAGONAL_FRENTE_ESQUERDA = 6;
        public final byte DIAGONAL_COSTA_DIREITA = 7;
        public final byte DIAGONAL_COSTA_ESQUERDA = 8;
                    
        public final static byte BORDA = 1;
        public final static byte CHAO = 2;
        public final static byte METAL = 3;
        public final static byte PLASTICO = 4;
        public final static byte PAPEL = 5;
        public final static byte ORGANICO = 6;
        public final static byte VIDRO = 7;
        public final static byte OBSTACULO = 8;
        public final static byte CARREGADOR= 9;
        public final static byte COLETOR_METAL_AMARELA=10;
        public final static byte COLETOR_ORGANICO_MAROM=11;
        public final static byte COLETOR_PLASTICO_VERMELHA=12;
        public final static byte COLETOR_VIDRO_VERDE=13;
        public final static byte COLETOR_PAPEL_AZUL=14;    
        public final static byte ROBO=15; 
        public final static byte DESCONHECIDO=0;  
        
        public boolean andar(byte direcao, byte[][] mapeamento, byte[][] mapeamentoHistorico);
        
        public byte ver(byte direcao);
        
        public boolean recarregarBateria();
        public int verNivelBateria();
        public int verNivelMaximoBateria();
        
        public boolean pegarLixo();
        public boolean descarregarLixo();
        
        public int verNivelCacamba();
        public int verNivelMaximoCacamba();
        
        public void finalizar();
}
