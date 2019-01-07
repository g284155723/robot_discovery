/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.mp;

import br.jrc.mp.lib.Ambiente;
import br.jrc.mp.lib.Robo;
import br.jrc.utilis.Funcoes;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author jrcorrea
 */
public class fraAvaliar extends javax.swing.JFrame {
    private Robo meuRobo;    
    private meuAmbienteAvaliacao obAmbiente = null;
    private String ArquivoRobo=null;
    private int inBateriaMaximo =0;
    private int inCacambaMaximo =0;
    protected int inQtdExecucoes =0;
    private MyTela[][] tempTela = null;
    private ConteudoTabelaAvaliacao conteudo1 = new ConteudoTabelaAvaliacao();
    private ConteudoTabelaAvaliacao conteudo2 = new ConteudoTabelaAvaliacao();
    private long loTempo;
    private long loTempoTotal;
    private int inQtdAvalicoes=100;
    private String[] stAnimacao ={"|","/","--","\\"};
    private int inIndiceAnimacao = 0;
    private int inQtdMateriais=0;
    private int inTotalSemLimites =0;
    private int inTotalComLimites =0;
    
    private Timer relogio;
    
    private meuAmbienteAvaliacao novoAmbienteAvaliacao( int bateriaMaximo, int cacambaMaximo)        {
                
        Robo novoRobo = Funcoes.roboLoad(ArquivoRobo, txtClasse.getText());

        int linha, coluna;
        do{
            linha = (int) (Math.random() * tempTela.length);
            coluna = (int) (Math.random() * tempTela[0].length);
        }while(tempTela[linha][coluna].getValor()!=Ambiente.CHAO);

        meuAmbienteAvaliacao tempAmbiente  = new meuAmbienteAvaliacao(tempTela, linha, coluna, bateriaMaximo, cacambaMaximo);
        tempAmbiente.setRobo(novoRobo);   
        
        return tempAmbiente;
    }
    
    class TaskSemLimites extends TimerTask {   
        
        public void run() {            
            if (obAmbiente==null){
                inQtdExecucoes=0;
                obAmbiente= novoAmbienteAvaliacao( Integer.MAX_VALUE, Integer.MAX_VALUE);
                loTempo = System.currentTimeMillis();
                loTempoTotal = System.currentTimeMillis();
                new Thread(obAmbiente).start();                 
            }else{
                if (obAmbiente.isRoboFinalizou()){
                    int inTotalMateriais = (obAmbiente.getQtdMetal() + obAmbiente.getQtdOrganico() +  obAmbiente.getQtdPapel() + obAmbiente.getQtdPlastico() + obAmbiente.getQtdVidro());
                    int inFalhas = inQtdMateriais - inTotalMateriais;
                    //o robô deve voltar para o carregador, se para fora, a distancia e considerada falta.
                    int inDistanciaBase=obAmbiente.getDistanciaRoboCarregador();
                    inTotalSemLimites+=(obAmbiente.getQtdOperacoes() + ((inDistanciaBase+inFalhas)*(inQtdMateriais*10)));
                    int inResultado = (obAmbiente.getQtdOperacoes() + ((inDistanciaBase+inFalhas)*(inQtdMateriais*10)));
                    //incluir campos na tabela
                    conteudo2.add((inQtdExecucoes+1),0, 0, obAmbiente.getQtdMetal() , obAmbiente.getQtdOrganico(), obAmbiente.getQtdPapel(), obAmbiente.getQtdPlastico() , obAmbiente.getQtdVidro(), obAmbiente.getQtdOperacoes(), (int) (System.currentTimeMillis() - loTempo), inFalhas, inResultado,inDistanciaBase);                                        
                    conteudo2.fireTableDataChanged();                 
                    
                    inQtdExecucoes++;
                    if (inQtdExecucoes<inQtdAvalicoes){                        
                        obAmbiente= novoAmbienteAvaliacao(  Integer.MAX_VALUE, Integer.MAX_VALUE);
                        loTempo = System.currentTimeMillis();                                                                          
                        new Thread(obAmbiente).start();                        
                    }else{                         
                        conteudo2.add(0,0, 0, 0 , 0, 0, 0 , 0, inTotalSemLimites/inQtdAvalicoes, (int) (System.currentTimeMillis() - loTempoTotal), 0,inTotalSemLimites/inQtdAvalicoes,0);                                        
                        labQtdOperacoesSemLimites.setText(String.valueOf((int)inTotalSemLimites/inQtdAvalicoes));
                        labResultado.setText(String.valueOf((int)((inTotalComLimites/inQtdAvalicoes)+(inTotalSemLimites/inQtdAvalicoes))/2));
                        conteudo2.fireTableDataChanged();   
                        butExecutar1.setEnabled(true);
                        txtClasse.setEnabled(true);
                        txtQtdAvaliacao.setEnabled(true);
                        labMensagem.setText("Concluído");
                        return;
                    }                         
                }
            }           
            
            long loTempoParcial = (System.currentTimeMillis() - loTempoTotal);
            long loTempoPrevisao = (loTempoParcial/(inQtdExecucoes+1)) * (inQtdAvalicoes - inQtdExecucoes);
            labMensagem.setText(" Rodando " + (inQtdExecucoes+1) + " de " + inQtdAvalicoes  + " tempo " + Funcoes.getTempoFormatado(loTempoParcial)  + " previsão " + Funcoes.getTempoFormatado(loTempoPrevisao)  + " " +  stAnimacao[inIndiceAnimacao++]);
            
            if (inIndiceAnimacao==4) inIndiceAnimacao=0;
            
            relogio.schedule(new TaskSemLimites(), 1000);
        }        
    }    
    
    class TaskComLimites extends TimerTask {        
        public void run() {            
            if (obAmbiente==null){
                inQtdExecucoes=0;
                obAmbiente= novoAmbienteAvaliacao( inBateriaMaximo, inCacambaMaximo);
                loTempo = System.currentTimeMillis();
                loTempoTotal = System.currentTimeMillis();
                new Thread(obAmbiente).start();
                
            }else{
                if (obAmbiente.isRoboFinalizou()){
                    //incluir campos na tabela
                    int inTotalMateriais = (obAmbiente.getQtdMetal() + obAmbiente.getQtdOrganico() +  obAmbiente.getQtdPapel() + obAmbiente.getQtdPlastico() + obAmbiente.getQtdVidro());
                    int inFalhas = inQtdMateriais - inTotalMateriais;
                    //o robô deve voltar para o carregador, se para fora, a distancia e considerada falta.
                    int inDistanciaBase=obAmbiente.getDistanciaRoboCarregador();
                    
                    inTotalComLimites+=(obAmbiente.getQtdOperacoes() + ((inDistanciaBase+inFalhas)*(inQtdMateriais*10)));
                    int inResultado = (obAmbiente.getQtdOperacoes() + ((inDistanciaBase+inFalhas)*(inQtdMateriais*10)));
                    conteudo1.add((inQtdExecucoes+1),inBateriaMaximo, inCacambaMaximo, obAmbiente.getQtdMetal() , obAmbiente.getQtdOrganico(), obAmbiente.getQtdPapel(), obAmbiente.getQtdPlastico() , obAmbiente.getQtdVidro(), obAmbiente.getQtdOperacoes(), (int) (System.currentTimeMillis() - loTempo), inFalhas, inResultado, inDistanciaBase);                                        
                    conteudo1.fireTableDataChanged();  
                    
                    inQtdExecucoes++;
                    if (inQtdExecucoes<inQtdAvalicoes){                                                  
                            obAmbiente= novoAmbienteAvaliacao( inBateriaMaximo, inCacambaMaximo);
                            loTempo = System.currentTimeMillis();                                                    
                            new Thread(obAmbiente).start();                        
                    }else{                                                
                        //passo para as proximas execuções
                        conteudo1.add(0,inBateriaMaximo, inCacambaMaximo, 0 , 0, 0, 0 , 0, inTotalComLimites/inQtdAvalicoes, (int) (System.currentTimeMillis() - loTempoTotal), 0, inTotalComLimites/inQtdAvalicoes, inDistanciaBase);                                        
                        labQtdOperacoesComLimites.setText(String.valueOf((int)inTotalComLimites/inQtdAvalicoes));
                        conteudo1.fireTableDataChanged(); 
                        obAmbiente= null;                        
                        relogio.schedule(new TaskSemLimites(), 100);
                        return;
                    }                         
                }
            }       
            long loTempoParcial = (System.currentTimeMillis() - loTempoTotal);
            long loTempoPrevisao = (loTempoParcial/(inQtdExecucoes+1)) * (inQtdAvalicoes - inQtdExecucoes);
            labMensagem.setText(" Rodando " + (inQtdExecucoes+1) + " de " + inQtdAvalicoes  + " tempo " + Funcoes.getTempoFormatado(loTempoParcial)  + " previsão " + Funcoes.getTempoFormatado(loTempoPrevisao)  + " " +  stAnimacao[inIndiceAnimacao++]);
            if (inIndiceAnimacao==4) inIndiceAnimacao=0;
            relogio.schedule(new TaskComLimites(), 1000);
        }
    }
    
    /**
     * Creates new form fraAvaliar
     */
    public fraAvaliar(String arquivoRobo) {
        
        initComponents();                                               
        relogio = new Timer();
        
        tabInformacoes1.setModel(conteudo1);
        tabInformacoes2.setModel(conteudo2);
        
        ArquivoRobo=arquivoRobo;      
        int inLargura = fraPrincipal.getLargura();
        int inAltura = fraPrincipal.getAltura();
        if (inAltura>inLargura){
            inBateriaMaximo = inAltura*8;
        }else{
            inBateriaMaximo = inLargura*8;
        }
        tempTela =fraPrincipal.getMapa();
        
        int[] inMateriais = new int[]{0,0,0,0,0,0,0,0};
        for (int i=0;i<tempTela.length;i++)
            for (int j=0;j<tempTela[0].length;j++)
                if (tempTela[i][j].getValor()==Ambiente.METAL || tempTela[i][j].getValor()==Ambiente.ORGANICO || tempTela[i][j].getValor()==Ambiente.PAPEL || tempTela[i][j].getValor()==Ambiente.PLASTICO || tempTela[i][j].getValor()==Ambiente.VIDRO){
                    inQtdMateriais++;
                    inMateriais[tempTela[i][j].getValor()]++;
                }
        if (inQtdMateriais>4){
            inCacambaMaximo = (int)(inQtdMateriais/4);
        }else{
            inCacambaMaximo = inQtdMateriais;
        }
        labMetal.setText(String.valueOf( inMateriais[Ambiente.METAL]));
        labOrganico.setText(String.valueOf( inMateriais[Ambiente.ORGANICO]));
        labPapel.setText(String.valueOf( inMateriais[Ambiente.PAPEL]));
        labPlastico.setText(String.valueOf( inMateriais[Ambiente.PLASTICO]));
        labVidro.setText(String.valueOf( inMateriais[Ambiente.VIDRO]));
        labTotal.setText(String.valueOf(inQtdMateriais));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtClasse = new javax.swing.JTextField();
        labMensagem = new javax.swing.JLabel();
        butExecutar1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        labMetal = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labOrganico = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labPapel = new javax.swing.JLabel();
        labPlastico = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        labVidro = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        labTotal = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtQtdAvaliacao = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        labQtdOperacoesComLimites = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labQtdOperacoesSemLimites = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labResultado = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabInformacoes1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabInformacoes2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Configurações"));

        jLabel1.setText("Classe");

        txtClasse.setText("br.jrc.robo.jrcRoboA2");

        labMensagem.setBackground(new java.awt.Color(255, 255, 204));
        labMensagem.setForeground(new java.awt.Color(102, 102, 102));
        labMensagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labMensagem.setOpaque(true);

        butExecutar1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        butExecutar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/executar.png"))); // NOI18N
        butExecutar1.setToolTipText("Executar");
        butExecutar1.setAlignmentY(0.0F);
        butExecutar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butExecutar1.setMargin(null);
        butExecutar1.setMaximumSize(new java.awt.Dimension(24, 24));
        butExecutar1.setMinimumSize(new java.awt.Dimension(24, 24));
        butExecutar1.setName(""); // NOI18N
        butExecutar1.setPreferredSize(new java.awt.Dimension(24, 24));
        butExecutar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butExecutar1ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Materiais"));

        labMetal.setBackground(new java.awt.Color(255, 255, 204));
        labMetal.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labMetal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labMetal.setText("Metal");
        labMetal.setOpaque(true);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Metal");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Orgânico");

        labOrganico.setBackground(new java.awt.Color(255, 255, 204));
        labOrganico.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labOrganico.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labOrganico.setText("Metal");
        labOrganico.setOpaque(true);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Papel");

        labPapel.setBackground(new java.awt.Color(255, 255, 204));
        labPapel.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labPapel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labPapel.setText("Metal");
        labPapel.setOpaque(true);

        labPlastico.setBackground(new java.awt.Color(255, 255, 204));
        labPlastico.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labPlastico.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labPlastico.setText("Metal");
        labPlastico.setOpaque(true);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Plástico");

        labVidro.setBackground(new java.awt.Color(255, 255, 204));
        labVidro.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labVidro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labVidro.setText("Metal");
        labVidro.setOpaque(true);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Vidro");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Total");

        labTotal.setBackground(new java.awt.Color(255, 255, 204));
        labTotal.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTotal.setText("Metal");
        labTotal.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labMetal, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(labOrganico, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labPapel, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labPlastico, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labVidro, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labTotal))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labVidro))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labPlastico))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labMetal)
                            .addComponent(labOrganico)
                            .addComponent(labPapel))))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        jLabel2.setText("Qtd.");

        txtQtdAvaliacao.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtQtdAvaliacao.setText("10");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultado"));

        labQtdOperacoesComLimites.setBackground(new java.awt.Color(255, 255, 204));
        labQtdOperacoesComLimites.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labQtdOperacoesComLimites.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labQtdOperacoesComLimites.setText("0");
        labQtdOperacoesComLimites.setToolTipText("Resultado do Robô com limite na bateria e na caçamba");
        labQtdOperacoesComLimites.setOpaque(true);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Limitado");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Ilimitado");

        labQtdOperacoesSemLimites.setBackground(new java.awt.Color(255, 255, 204));
        labQtdOperacoesSemLimites.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labQtdOperacoesSemLimites.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labQtdOperacoesSemLimites.setText("0");
        labQtdOperacoesSemLimites.setToolTipText("Resultado do Robô sem limite na bateria e na caçamba");
        labQtdOperacoesSemLimites.setOpaque(true);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Total");

        labResultado.setBackground(new java.awt.Color(255, 255, 204));
        labResultado.setFont(new java.awt.Font("Verdana", 1, 10)); // NOI18N
        labResultado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labResultado.setText("0");
        labResultado.setOpaque(true);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Operações");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labQtdOperacoesComLimites, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labQtdOperacoesSemLimites, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labResultado, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labQtdOperacoesComLimites)
                    .addComponent(labQtdOperacoesSemLimites)
                    .addComponent(labResultado)
                    .addComponent(jLabel15))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClasse)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtQtdAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butExecutar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
            .addComponent(labMensagem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtClasse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtQtdAvaliacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(butExecutar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações"));

        jScrollPane2.setAutoscrolls(true);

        tabInformacoes1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        tabInformacoes1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabInformacoes1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabInformacoes1MandarParaClipBoard(evt);
            }
        });
        jScrollPane2.setViewportView(tabInformacoes1);

        jScrollPane3.setAutoscrolls(true);

        tabInformacoes2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        tabInformacoes2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tabInformacoes2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabInformacoes2MandarParaClipBoard(evt);
            }
        });
        jScrollPane3.setViewportView(tabInformacoes2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addComponent(jScrollPane3)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butExecutar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butExecutar1ActionPerformed
        butExecutar1.setEnabled(false);
        txtClasse.setEnabled(false);
        txtQtdAvaliacao.setEnabled(false);
        meuRobo = Funcoes.roboLoad(ArquivoRobo, txtClasse.getText());
        if (meuRobo!=null){   
            obAmbiente = null;
            inTotalSemLimites =0;
            inTotalComLimites =0;
            conteudo1 = new ConteudoTabelaAvaliacao();
            conteudo2 = new ConteudoTabelaAvaliacao();
            tabInformacoes1.setModel(conteudo1);
            tabInformacoes2.setModel(conteudo2);
            inQtdAvalicoes = Integer.valueOf(txtQtdAvaliacao.getText());
            labMensagem.setText(" Robô " + meuRobo.getNome());
            relogio.schedule(new TaskComLimites(), 100); 
        }else{
            butExecutar1.setEnabled(true);
            txtClasse.setEnabled(true);
            txtQtdAvaliacao.setEnabled(true);
        }
    }//GEN-LAST:event_butExecutar1ActionPerformed

    private void tabInformacoes1MandarParaClipBoard(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabInformacoes1MandarParaClipBoard
    }//GEN-LAST:event_tabInformacoes1MandarParaClipBoard

    private void tabInformacoes2MandarParaClipBoard(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabInformacoes2MandarParaClipBoard
        // TODO add your handling code here:
    }//GEN-LAST:event_tabInformacoes2MandarParaClipBoard


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butExecutar1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labMensagem;
    private javax.swing.JLabel labMetal;
    private javax.swing.JLabel labOrganico;
    private javax.swing.JLabel labPapel;
    private javax.swing.JLabel labPlastico;
    private javax.swing.JLabel labQtdOperacoesComLimites;
    private javax.swing.JLabel labQtdOperacoesSemLimites;
    private javax.swing.JLabel labResultado;
    private javax.swing.JLabel labTotal;
    private javax.swing.JLabel labVidro;
    private javax.swing.JTable tabInformacoes1;
    private javax.swing.JTable tabInformacoes2;
    private javax.swing.JTextField txtClasse;
    private javax.swing.JTextField txtQtdAvaliacao;
    // End of variables declaration//GEN-END:variables
}
