/**
Tentative de lecture automatique d'un code 39 dans une image.
Voir aussi : Code39Decoder
Le but est de décoder un code 39 dans une image obtenue à partir d'une 
caméra type webcam ( un des problèmes est le choix de cette caméra et la facon
de récupérer l'image dans un contexte industriel ).
Il faut récupérer l'image dans un fichier puis la traiter dans les deux
sens vertical et horizontal.

Plusieurs étapes: 
1/ récupération de l'image : 
2/ décodage par étude des pixels de l'image.
3/ Interprétation.

Décodage d'un code 39:
Le code39 comprend 9 barres dont 3 larges.
Une barre large mesure entre 2.2 et 3 fois une barre fine.
Les barres noires réalisent une prélection de 4 lettres, 
les barres blanches permettent le choix parmi ces 4 lettres.
ex :
barres noires: 1 pour large 0 pour etroite :
5 barres / 9  dont 2 larges noires.
KA1U	10001
LB2V	01001
MC3W	11000
ND4X	00101
OE5Y	10100
PF6Z	01100
QJ7-	00011
RH8.	10010
SI9 	01010
TJ0*	00110

Les barres blanches permettent de déterminer le rang dans la liste:
4 barres /9  dont une large blanche.
0001 : 1, 0010 : 2, 0100 : 3, 1000 : 4

Exemple codage de  *  :
Noires 00110   Blanches : 1000 (4ème lettre )
	N0-B1-N0-B0-N1-B0-N1-B0-N0

Entre deux lettres une  barre blanche est insérée 

Remarque :
Le code complet est précédé par une 
"quiet zone "  zone blanche suffisemment longue
une etoile *  il finit de la meme facon et peut etre lu dans les
deux sens. Ainsi *CODE39* estun code valide.
Il peut etre lu dans les deux sens .
Décodage :
	On met en oeuvre un timer, qui est en fait le nbre de pixels lus.
	Time-out est à régler suivant le duree de lecture. permet de déterminer 
	si on est dans une lecture ou non. La détection d'une quiet zone pemet
	de déclencher la lecture et de l'arrêter .
--
*/
package com.derhy.barcode;
import java.awt.Image ;
import java.awt.* ;
import java.awt.image.* ;
import javax.swing.ImageIcon ;

public class ImageDecoder extends java.awt.Panel  {	
	Image image ;
	int w , h ;
	int[] pixels ;	
	static final int SEUIL  = 45 ;// 45 seuil de passage blanc/noir	

	ImageDecoder(String chemin){
		this.image = new ImageIcon(chemin). getImage() ;		
    	w = image.getWidth(null); // si <0 l'image n'est pas encore chargée.
    	h = image.getHeight(null);
		System.out.println( "Image "+ chemin + " w h " + w +", "+ h);
		this.pixels = getPixels(image,0,0,w,h);
	}
	
	/** methode générique pour récuperer les pixels de l'image **/
	public int[] getPixels(Image img, int x, int y, int w, int h) {
		int[] p = new int[w * h];
		PixelGrabber pg = new PixelGrabber(img, x, y, w, h, p, 0, w);
	//Create a PixelGrabber object to grab the (x, y, w, h)  rectangular section of pixels from the specified image.
		try {pg.grabPixels(); } catch (InterruptedException e) { return null; }
 		return  p ;
 	}
 	
	public int getWidth() { return w ; }
	public int getHeigth() { return h ; }  
	public int[] getPixels(){ return pixels ; }

/** 
* recupère seulement une ligne de pixels de l'image 
* renvoi un tableau filtré correspondant à une ligne de 0 ou 1
**/
	public int[] getLine (int lineNumber){
		int colonne =  	w * lineNumber ;
		int[] ligne = new int[w] ; 
		for(int i= 0 ; i < w ;i++){
			ligne[i] = (pixels[i+colonne]>>16 ) & 0xff; //recupération du rouge
			if (ligne[i] > SEUIL ) ligne[i] = 1 ;
			else ligne[i]= 0 ;
		}
		return ligne ;
	}
}
