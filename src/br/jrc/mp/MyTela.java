package br.jrc.mp;


import br.jrc.mp.lib.Ambiente;
import br.jrc.mp.lib.Robo;
import br.jrc.utilis.Funcoes;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JLabel;


class MyTela extends JLabel {
                              
    private byte Valor;
    private boolean fixo=false;
    private final Icon[] Imagens;
    
    public void setFixo(){
        fixo=false;
    }
    public boolean isFixo(){
        return fixo;
    }
    
    public MyTela(Icon[] imagens){
        Imagens = imagens;
        setBackground(Color.WHITE);
        setOpaque(true);
    }
    public void setValor(byte valor){
        Valor = valor;
    }
    
    public void setImagem(byte valor){
        Valor = valor;
        //System.out.print(" | " + valor);
        this.setIcon(Funcoes.getScaledImage(Imagens[valor],this.getWidth(),this.getHeight()));
        
        switch(valor){
            case Ambiente.BORDA:
                this.setBackground(Color.GRAY);
                break;
            case Ambiente.CHAO:
                this.setBackground(Color.WHITE);
                break;
            case Ambiente.OBSTACULO:    
                this.setBackground(Color.BLACK);
                break;                
            case Ambiente.VIDRO:
                this.setBackground(Color.GREEN);
                break;
            case Ambiente.METAL:
                this.setBackground(Color.YELLOW);
                break; 
            case Ambiente.PLASTICO:
                this.setBackground(Color.RED);
                break;     
            case Ambiente.PAPEL:
                this.setBackground(Color.BLUE);
                break; 
            case Ambiente.ORGANICO:
                this.setBackground(new Color(92,51,23));
                break;     
            case Ambiente.CARREGADOR:
                this.setBackground(Color.LIGHT_GRAY);
                break;                
            case Ambiente.COLETOR_METAL_AMARELA:
                this.setBackground(Color.LIGHT_GRAY);
                break;        
            case Ambiente.COLETOR_VIDRO_VERDE:
                this.setBackground(Color.LIGHT_GRAY);
                break;        
            case Ambiente.COLETOR_PLASTICO_VERMELHA:
                this.setBackground(Color.LIGHT_GRAY);
                break;        
            case Ambiente.COLETOR_PAPEL_AZUL:
                this.setBackground(Color.LIGHT_GRAY);
                break;        
            case Ambiente.COLETOR_ORGANICO_MAROM:
                this.setBackground(Color.LIGHT_GRAY);                
                break;        
        }
        
    }
    
    public byte getValor() {
        return Valor;
    }
    
     
//    private void moveSquare(int x, int y) {
//        int OFFSET = 1;
//        if ((squareX!=x) || (squareY!=y)) {
//            repaint(squareX,squareY,Tamanho+OFFSET,Tamanho+OFFSET);
//            squareX=x;
//            squareY=y;
//            //repaint(squareX,squareY,Tamanho+OFFSET,squareH+OFFSET);
//        } 
//    }
    

 
    
//    protected void paintComponent(Graphics g) {
//             
//        //super.paintComponent(g);
//        
//        //g.drawString("This is my custom Panel!",10,20);
//        g.setColor(Cor);
//        g.fillOval(squareX,squareY,Tamanho,Tamanho);
//        //g.fillRect(squareX,squareY,squareW,squareH);
//       // g.setColor(Color.BLACK);
//        g.drawOval(squareX,squareY,Tamanho,Tamanho);                  
//        
//    }  

    boolean isPisavel() {
        if (Valor==Ambiente.BORDA || Valor==Ambiente.OBSTACULO){
            return  false;
        }
        return true;
    }

    boolean isCarregador() {
        if (Valor==Ambiente.CARREGADOR){
            return  true;
        }
        return false;
    }

}
