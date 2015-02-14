package com.derhy.barcode;
import java.util.HashMap ;
public class Code39Decoder {

	static HashMap map;
	int[] code ;

	static{
		map = new HashMap() ;							
		map.put("10001"	,"KA1U");
		map.put("01001"	,"LB2V");
		map.put("11000"	,"MC3W");
		map.put("00101"	,"ND4X");
		map.put("10100"	,"OE5Y");
		map.put("01100"	,"PF6Z");
		map.put("00011"	,"QJ7-");
		map.put("10010"	,"RH8.");
		map.put("01010"	,"SI9 ");
		map.put("00110"	,"TJ0*");
	}

	/**
	 * Cherche la taille minimum de la barre étroite.
	 * On ne prend pas en compte la barre 9 qui est la dixième barre qui correspond à l'inter-caractère
	 * On classe les barres de la plus grande à la moins grande et on renvoi la taille de la 3 ème barre la plus grande.
	 */
	protected int  taille_barre_fine  (int[] code){
		int[] max = new int[code.length] ;
		max = (int[])code.clone() ;
		//tri par selection :
		int imax =0 ;
		for( int i=0 ; i< 3 ; i++) {
			imax = max[i] ;
			for(int j= i+1 ; j <  9 ; j++){
				if(max[j]> imax){
					max[i] = max[j] ;
					max[j]= imax ;
					imax = max[i] ;
				}
			}

		}
		return imax ;
	}
	/**
	 * Traduit les données lues en pixels en barres, on retourne un tableau du type 000101100 
	 * si on ne trouve pas ca on envoie une exception.
	 11111111111111111*11111*111000001000001101100000101111110110110000011011000001111101101100000100000100000111110111011011011011111100000101100000110110000011110000011101101011011111101000001100000110111110111000001000001101111111111111111111111111111
	 1    5   1  3   5        1      5      2  1   2  /   5     1
	 */

	protected int[] traduire_en_barres ( int[] code) throws BarcodeException {
		int[] resultat = new int[9] ;
		int tbase =  taille_barre_fine(code);
		int max_larges = 0 ;
		for(int i=0 ; i<9 ;i++)	{
			if( code[i] >= tbase  ){ 
				resultat[i] = 1 ; 
				max_larges++ ;
			}else{
				resultat[i] = 0 ;
			}
		}
		if (max_larges != 3) throw new BarcodeException( "Erreurs, barres larges : " + max_larges+ " " + tab(code) ) ;
		return resultat ;
	}
	protected String tab(int[] tab){
		String res = "{" ;
		for(int i=0; i<tab.length;i++) res += tab[i]+ ","  ;
		return res+"}" ;
	}

	public char conversion(int[] code) throws BarcodeException {
		String noirs = "" ; // noirs
		int nbNoirs= 0 ; // barres larges noires normale=2
		int rang = 0 ; // blancs
		int nbBlancs= 0 ;// barres larges blanches normale=1 
		int[] barres = traduire_en_barres(code) ;
		for (int i=0 ; i< 9 ; i++) {	// Les barres paires sont noires let barres impaires blanches.
			if(i%2 == 0){// noirs
				noirs += barres[i] ;
				if (barres[i] == 1 ) nbNoirs ++ ;
			}
			if(i%2==1) {
				if(barres[i] == 1 ){
					rang = 4 - (i/2) ;
					nbBlancs++ ;
				}
			}

		}
		if( nbBlancs != 1) throw new BarcodeException ("Pr de barres blanches" + nbBlancs+ " " + tab(barres) ) ;
		if( nbNoirs != 2) throw new BarcodeException ("Pr de barre noires "+ nbNoirs+ " " + tab(barres) ) ;
		// tout va bien on recherche maintenant le carcactère reconnu :
		if( !map.containsKey(noirs) )throw new BarcodeException("Motif non reconnu") ;

		String s  = (String)map.get(noirs) ;
		char c = s.toCharArray() [rang-1] ;
		return c ;
	}
}
