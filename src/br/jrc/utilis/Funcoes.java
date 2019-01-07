/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jrc.utilis;

import br.jrc.mp.fraPrincipal;
import br.jrc.mp.lib.Ambiente;
import br.jrc.mp.lib.Robo;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author jrcorrea
 */
public class Funcoes {
    
    
    
    public static String getTempoFormatado(long tempo) {
    	long loDias = 0;
        long loHoras = 0;
        long loMinutos = 0;        
        long loSegundos =0;
        long loMileSegundos =tempo;
        
        String stTemp ="";

        if (loMileSegundos > 86400000) {
                loDias = (loMileSegundos / 86400000);
                loMileSegundos = (loMileSegundos - (loDias * 86400000));
                stTemp+= loDias + "d "; 
        }
        if (loMileSegundos > 3600000) {
                loHoras = (loMileSegundos / 3600000);
                loMileSegundos = (loMileSegundos - (loHoras * 3600000));
                stTemp+= loHoras + "h ";
        }
        if (loMileSegundos > 60000) {
                loMinutos = loMileSegundos / 60000;
                loMileSegundos = (loMileSegundos - (loMinutos * 60000));
                stTemp+= loMinutos + "m ";
        }
        if (loMileSegundos > 1000) {
                loSegundos = loMileSegundos / 1000;
                loMileSegundos = (loMileSegundos - (loSegundos * 1000));
                stTemp+= loSegundos + "s ";
        }
        if (loMileSegundos > 0) {
                stTemp+= "." + loMileSegundos ;
        }
        return stTemp;
    }
         
    public static int getRGB(int cor){
        switch(cor){
            case Ambiente.BORDA:
                return  new Color(106,90,205).getRGB() ;
            case Ambiente.CARREGADOR:
                return Color.CYAN.getRGB();
            case Ambiente.CHAO:
                return Color.WHITE.getRGB();
            case Ambiente.DESCONHECIDO:
                return Color.BLACK.getRGB();    
            case Ambiente.COLETOR_METAL_AMARELA:
                return new Color(255,215,0).getRGB();
            case Ambiente.COLETOR_ORGANICO_MAROM:
                return new Color(165,42,42).getRGB();
            case Ambiente.COLETOR_PAPEL_AZUL:
                return new Color(0,0,205).getRGB();
            case Ambiente.COLETOR_PLASTICO_VERMELHA:
                return new Color(255,69,0).getRGB();
            case Ambiente.COLETOR_VIDRO_VERDE:
                return new Color(0,100,0).getRGB();
            case Ambiente.METAL:
                return Color.YELLOW.getRGB();
            case Ambiente.OBSTACULO:
                return new Color(148,155,147).getRGB();
            case Ambiente.ORGANICO:
                return new Color(150,7,7).getRGB();
            case Ambiente.PAPEL:
                return  Color.BLUE.getRGB();
            case Ambiente.PLASTICO:
                return  Color.RED.getRGB();
            case Ambiente.ROBO:
                return new Color(115,86,192).getRGB();
            case Ambiente.VIDRO:
                return  Color.GREEN.getRGB();
            default:
                return Color.BLACK.getRGB();    
        }
    }
    
    public static Icon gerarImagem(byte[][] matriz){
        GraphicsEnvironment ge =   GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage( matriz[0].length, matriz.length);
        
        for (int i=0;i<matriz.length;i++)
            for(int j=0;j<matriz[0].length;j++)
                image.setRGB(j, i, getRGB(matriz[i][j]));
        
                
        return new ImageIcon(image);
    }
    
    public static Icon getScaledImage(Icon srcImg, int novaLargura, int novaAltura){
        //primeiro converto o icone em imagem
        int w = srcImg.getIconWidth();
        int h = srcImg.getIconHeight();
        GraphicsEnvironment ge =   GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        
        Graphics2D g = image.createGraphics();
        srcImg.paintIcon(null, g, 0, 0);
        g.dispose();
      
        BufferedImage resizedImg;
        resizedImg = new BufferedImage( novaLargura, novaAltura, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(image, 0, 0, novaLargura, novaAltura, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }
    
    
    public static Robo roboLoad(String path, String objeto){
        File file = new File(path);
        Class cls = null;
        Robo myRobo = null;
        try {
            // Convert File to a URL
            URL url = file.toURL();          // file:/c:/myclasses/
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            ClassLoader cl = new URLClassLoader(urls);

            // Load in the class; MyClass.class should be located in
            // the directory file:/c:/myclasses/com/mycompany
            cls = cl.loadClass(objeto);
            myRobo = (Robo) cls.newInstance();
        } catch (MalformedURLException e) {
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException ex) {
            Logger.getLogger(fraPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(fraPrincipal.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return myRobo;
    }
        
}
