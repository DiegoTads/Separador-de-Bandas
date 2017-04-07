/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package separador;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.TiledImage;

/**
 *
 * @author Diego
 */

class No
{
    int RED;
    int GREEN;
    int BLUE;
    int grupo;
    float VALORRGB;
    No prox;

    public No(int red, int green, int blue, int g, float valorRGB)
    {
        RED = red;
        GREEN = green;
        BLUE = blue;
        grupo=g;
        VALORRGB = valorRGB;
        prox = null;
    }
}

class Lista
{
    No primeiro,ultimo;
    int totalNos;

    public Lista()
    {
        primeiro = ultimo = null;
        totalNos = 0;
    }

    public int getTotalNos()
    {
        return totalNos;
    }

    public boolean checkIfListaVazia()
    {
        if (getTotalNos() == 0)
        {
            return true;
        }
        
       return false;
    }

    public void inserirNoFim(No n)
    {
        if ( checkIfListaVazia() )
        {
            primeiro = ultimo = n;
        }
        
        else
        {
            ultimo.prox = n;
            ultimo = n;
        }
       totalNos++;
    }
}

public class Separador {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException 
    {   
        Random r = new Random();    
        BufferedImage IRED = ImageIO.read(new File("folha.jpg"));
        BufferedImage IGREEN = ImageIO.read(new File("folha.jpg"));
        BufferedImage IBLUE = ImageIO.read(new File("folha.jpg"));
        BufferedImage imagem = ImageIO.read(new File("folha.jpg"));
        PlanarImage RED = JAI.create("bandselect",IRED,new int[]{0});
        PlanarImage GREEN = JAI.create("bandselect",IGREEN,new int[]{1});
        PlanarImage BLUE = JAI.create("bandselect",IBLUE,new int[]{2});
        int largura = RED.getWidth(), x=0, kmedia=2;
        int altura = RED.getHeight();
        Raster rasterRED = RED.getData();
        Raster rasterGREEN = GREEN.getData();
        Raster rasterBLUE = BLUE.getData();
        WritableRaster rasterEscritaRED = rasterRED.createCompatibleWritableRaster();
        WritableRaster rasterEscritaGREEN = rasterGREEN.createCompatibleWritableRaster();
        WritableRaster rasterEscritaBLUE = rasterBLUE.createCompatibleWritableRaster();
        int[] pixelsRGB = imagem.getRGB(0, 0, largura, altura, null, 0, largura);
        int[] pixelsRED = new int[largura*altura];
        int[] pixelsGREEN = new int[largura*altura];
        int[] pixelsBLUE = new int[largura*altura];
        rasterRED.getPixels(0, 0, largura, altura, pixelsRED);
        rasterGREEN.getPixels(0, 0, largura, altura, pixelsGREEN);
        rasterBLUE.getPixels(0, 0, largura, altura, pixelsBLUE);
        
        rasterEscritaRED.setPixels(0, 0, largura, altura, pixelsRED);
        rasterEscritaGREEN.setPixels(0, 0, largura, altura, pixelsGREEN);
        rasterEscritaBLUE.setPixels(0, 0, largura, altura, pixelsBLUE);
       
        TiledImage ti = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti.setData(rasterEscritaRED);
        JAI.create("filestore",ti,"BANDA RED.png","PNG");
        
        TiledImage ti1 = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti1.setData(rasterEscritaGREEN);
        JAI.create("filestore",ti1,"BANDA GREEN.png","PNG");
        
        TiledImage ti2 = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti2.setData(rasterEscritaBLUE);
        JAI.create("filestore",ti2,"BANDA BLUE.png","PNG");

        Lista lista = new Lista();
        System.out.println("\nValores dos pixels ---------->\n ");
        
        for(int i=0;i<pixelsRED.length;i++)
        {
            lista.inserirNoFim(new No(pixelsRED[i],pixelsGREEN[i],pixelsBLUE[i],0,pixelsRGB[i]));
            System.out.println("Pixel "+(i+1)+": ("+pixelsRED[i]+" , "+pixelsGREEN[i]+" , "+pixelsBLUE[i]+")");
        }
        
        System.out.println("");
        double centroidesRGB[] = new double[kmedia];
        No temporario = lista.primeiro;
        double diferenca[] = new double[kmedia];
        double aux2[] = new double[kmedia];
        double aux3[] = new double[kmedia];
        int gruposaux[] = new int [kmedia];
        int gruposaux1[] = new int [kmedia];
        double menor_distancia, troca, grupoaux=0; 
        int grupo = 0;

        for(int i=0;i<kmedia;i++)
        {
            centroidesRGB[i]=0;
            int num=r.nextInt(lista.getTotalNos());
            temporario=lista.primeiro;

            for(int j=0;j<=num;j++)
            {
                if(j==num && temporario.VALORRGB!=0)
                {
                    centroidesRGB[i]=temporario.VALORRGB;
                }

                temporario=temporario.prox;
            }

            for(int g=0;g<i;g++)
            {
                if(centroidesRGB[i]==centroidesRGB[g] || centroidesRGB[i]==0)
                {
                    i--;
                }
            } 
        }

        // Imprime o valor dos centroides
        for(int i=0;i<centroidesRGB.length;i++)
        {
            System.out.println("Primeiros Centroides: "+centroidesRGB[i]);
        }

        temporario=lista.primeiro;
        System.out.println("\nRelação: ");   
        int fator_de_troca=0;
        // Inicio do K-means agrupamento de dados
        while(fator_de_troca>=0)
        {
            troca=0;
            temporario=lista.primeiro;

            for(int v=0;v<gruposaux.length;v++)
            {
                gruposaux[v]=0;
            }

            for(int e=0;e<aux2.length;e++)
            {
                aux2[e]=0;
                aux3[e]=0;
            }

            // Media a distancia dos pixels ao centroides, marca em qual grupo o centroides pertence, e soma os valores de cada grupo para calcular os novos centroides
            for(int i=0;i<lista.getTotalNos();i++)
            {     
                menor_distancia=0;

                if(temporario.VALORRGB!=0)
                {
                    for(int k=0;k<centroidesRGB.length;k++)
                    {
                        diferenca[k]=centroidesRGB[k]-temporario.VALORRGB;

                        if(diferenca[k]<0)
                        {
                            diferenca[k]=(diferenca[k]*-1);
                        }

                        if(k==0)
                        {
                            menor_distancia=diferenca[k];
                            grupo=0;
                            gruposaux[k]++;
                        }

                        if(diferenca[k]<menor_distancia)
                        {  
                            grupo=k;
                            menor_distancia=diferenca[k];
                            gruposaux[k]++;
                        }

                    } 

                    temporario.grupo=grupo;
                    aux2[temporario.grupo]=(aux2[temporario.grupo]+temporario.VALORRGB);
                    aux3[temporario.grupo]++;
                }

                temporario=temporario.prox;
            }

System.out.println("");
            //preenche o grupo auxiliar e defini novos centroides
            if(fator_de_troca==0)
            {
                teste_de_troca(gruposaux, gruposaux1, centroidesRGB, aux2, aux3);                    
            }

            // indica quantas trocas houve em relação ao grupo auxiliar
            if(fator_de_troca>0)
            {
                for(int q=0;q<gruposaux.length;q++)
                {
                    if(gruposaux1[q]>=gruposaux[q])
                    {
                        troca=troca+((gruposaux1[q]-gruposaux[q]));          
                    }

                    else
                    {
                        troca=troca+((gruposaux[q]-gruposaux1[q]));
                    }
                }

        System.out.print("troca:"+troca);
        System.out.println("\n");

                // Caso desejar atribuir uma porcentagem, a condição muda para if(troca>=(vetI.length*0.02))
                if(troca>0)
                {
                    teste_de_troca(gruposaux, gruposaux1, centroidesRGB, aux2, aux3);  
                }
                // condição para finalizar o algoritmo

                else
                {
                    fator_de_troca=-2;
                }
            } 

            fator_de_troca++;
        }
        
        temporario=lista.primeiro;
        
        int red0=0,green0=0,blue0=0, red1=0,green1=0,blue1=0, quantidade0=0, quantidade1=0, mred0=0,mgreen0=0,mblue0=0, mred1=0,mgreen1=0,mblue1=0;
        
        for(int i=0;i<lista.getTotalNos();i++)
        {
            if(temporario.grupo==0)
            {
                red0=red0+temporario.RED;
                green0=green0+temporario.GREEN;
                blue0=blue0+temporario.BLUE;
                quantidade0++;
            }
            
            else
            {
                red1=red1+temporario.RED;
                green1=green1+temporario.GREEN;
                blue1=blue1+temporario.BLUE;
                quantidade1++;
            }
            
            temporario=temporario.prox;
        }
        
        mred0=red0/quantidade0;
        mgreen0=green0/quantidade0;
        mblue0=blue0/quantidade0;
        mred1=red1/quantidade1;
        mgreen1=green1/quantidade1;
        mblue1=blue1/quantidade1;

        System.out.println("Valores medios dos pixels ----------> \n");
        System.out.println("Grupo 1 : ("+mred0+" , "+mgreen0+" , "+mblue0+")");
        System.out.println("Grupo 2 : ("+mred1+" , "+mgreen1+" , "+mblue1+")");

        temporario=lista.primeiro;

        for(int i=0;i<pixelsRED.length;i++)
        {
            if(temporario.grupo==0)
            {
                pixelsRED[i]=mred0;
                pixelsGREEN[i]=mgreen0;
                pixelsBLUE[i]=mblue0;
            }
            
            else
            {
                pixelsRED[i]=mred1;
                pixelsGREEN[i]=mgreen1;
                pixelsBLUE[i]=mblue1;
            }
            
            temporario=temporario.prox;
        }
        
        rasterEscritaRED.setPixels(0, 0, largura, altura, pixelsRED);
        rasterEscritaGREEN.setPixels(0, 0, largura, altura, pixelsGREEN);
        rasterEscritaBLUE.setPixels(0, 0, largura, altura, pixelsBLUE);
       
        TiledImage ti3 = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti3.setData(rasterEscritaRED);
        JAI.create("filestore",ti3,"UNICA COR BANDA RED.png","PNG");
        
        TiledImage ti4 = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti4.setData(rasterEscritaGREEN);
        JAI.create("filestore",ti4,"UNICA COR BANDA GREEN.png","PNG");
        
        TiledImage ti5 = new TiledImage(RED,RED.getTileWidth(),RED.getTileHeight());
        ti5.setData(rasterEscritaBLUE);
        JAI.create("filestore",ti5,"UNICA COR BANDA BLUE.png","PNG");
        
        PlanarImage imagemR = JAI.create("fileload", "UNICA COR BANDA RED.png");
        PlanarImage imagemG = JAI.create("fileload", "UNICA COR BANDA GREEN.png");
        PlanarImage imagemB = JAI.create("fileload", "UNICA COR BANDA BLUE.png");
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(imagemR);
        pb.addSource(imagemG);
        pb.addSource(imagemB);

        PlanarImage imagemResultado = JAI.create("bandmerge",pb);
        int largura1 = imagemResultado.getWidth();
        int altura1 = imagemResultado.getHeight();
        SampleModel sm = imagemResultado.getSampleModel();
        int nbandas = sm.getNumBands();
        Raster rasterIR = imagemResultado.getData();
        WritableRaster rasterEscrita1 = rasterIR.createCompatibleWritableRaster();
        float[] pixelsIR = new float[largura*altura*nbandas];
        rasterIR.getPixels(0, 0, largura, altura, pixelsIR);

        // Grava a imagem RGB
        rasterEscrita1.setPixels(0, 0, largura1, altura1, pixelsIR);
        TiledImage ti6 = new TiledImage(imagemResultado,imagemResultado.getTileWidth(),imagemResultado.getTileHeight());
        ti6.setData(rasterEscrita1);
        JAI.create("filestore",ti6,"IMAGEM RESULTADO.png","PNG");
    }
    
    public static void teste_de_troca(int[] gruposaux, int[] gruposaux1, double[] centroides, double[] aux2, double[] aux3)
    {
        // indica quantas trocas houve em relação ao grupo auxiliar
        for(int t=0;t<gruposaux.length;t++)
        {
            gruposaux1[t]=gruposaux[t];
        }
        
        //recalcula o novo centroide
        for(int p=0;p<aux2.length;p++)
        {
            centroides[p]=(aux2[p]/aux3[p]);
            System.out.print("Proximos Centroides:"+centroides[p]);
            System.out.println("");
        }
    }
}
