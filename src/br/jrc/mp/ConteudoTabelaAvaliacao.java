package br.jrc.mp;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import br.jrc.utilis.Funcoes;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author jrcorrea
 */
public class ConteudoTabelaAvaliacao  extends AbstractTableModel {  
   private HashMap mapDados = new HashMap();    
      
    private String[] colunas = {"Nº","Bateria","Caçamba","Metal","Orgânico","Papel","Plástico","Vidro","Qtd Materiais","Operações","Falhas","Dist.Base","Resultado","Tempo" };
    private int inLinha = 0;
                     
    public int getColumnWith(int col){
        return 100;
    }
            
    public int getColumnCount() {  
      return 14;  
    }  
      
    public int getRowCount() {  
      return mapDados.size();  
    }  
      
    public String getColumnName(int col) {  
      return colunas[col];  
    }  
      
    public Object getValueAt(int row, int col) { 
        int[] valores = (int[])mapDados.get(row);
        if (col==13){
            return Funcoes.getTempoFormatado(valores[col]);
        }        
        return valores[col];  
    }  
    public void add(int numero, int bateria, int cacamba, int metal, int organico, int papel, int plastico, int vidro, int operacoes, int tempo, int inFalhas, int inResultado, int distanciaRobo )  {
        int inTotalMateriais = (metal + organico +  papel + plastico + vidro);        
        mapDados.put(inLinha, new int[]{numero, bateria, cacamba,metal, organico, papel, plastico, vidro, inTotalMateriais , operacoes, inFalhas, distanciaRobo, inResultado, tempo});        
        inLinha++;
    }
//    public Class getColumnClass(int c) {  
//      return !liDadosQuina.isEmpty() ? ((Quina)liDadosQuina.get(0)).getCelula(c).getClass() : null;  
//    }  
      
    public boolean isCellEditable(int row, int col) {  
      return col == 0;  
    }  
      
    public void setValueAt(Object value, int row, int col) {  
        int[] valores = (int[])mapDados.get(row);
        valores[col]=(int)value;
        mapDados.put(row, valores);        
    }     
}  

