package com.infine.data;

import org.apache.solr.client.solrj.beans.Field;

public class Stock extends UserData{
	
	private static final String TECHNICAL_TYPE = "valeur";
	private static final String PREFIX_ID_VALEUR = TECHNICAL_TYPE + "-";
	
//  Exemple de donnee	
//	"@FF8  ";1646;"FTE FP";"FRANCE TELECOM";"5176177";"France";"EUR";"53";"FTE";"FR0000133308";"FTE.PA";40462;"FTE";"FIXED LINE TELECOMMUNICATIONS";"118101"
//      0	;  1 ;    2   ;       3        ;    4    ;    5   ;  6  ;  7  ; 8  ;        9     ;   10   ;  11 ;  12  ;             13               ;    14
	
	@Field
	private String id; // type-codeIsin"valeur-FR0000133308"
	
	@Field
	private String isin; // code isin "FR0000133308"
	
	@Field
	private String name; //"FRANCE TELECOM"
	
	@Field
	private String code; //FTE
	
	@Field
	private String pays; //France
	
	@Field
	private String devise; //EUR
	
	@Field
	private String type = TECHNICAL_TYPE; // ici donnee custom --> c'est un type technique
	
	@Field
	private String codeYahoo; // FTE.PA
	
	@Field
	private String secteur; //FIXED LINE TELECOMMUNICATIONS
	
	
	public void setComposedId(String id){
		this.id = PREFIX_ID_VALEUR + id;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getIsin() {
		return isin;
	}


	public void setIsin(String isin) {
		this.isin = isin;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getPays() {
		return pays;
	}


	public void setPays(String pays) {
		this.pays = pays;
	}


	public String getDevise() {
		return devise;
	}


	public void setDevise(String devise) {
		this.devise = devise;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCodeYahoo() {
		return codeYahoo;
	}


	public void setCodeYahoo(String codeYahoo) {
		this.codeYahoo = codeYahoo;
	}


	public String getSecteur() {
		return secteur;
	}


	public void setSecteur(String secteur) {
		this.secteur = secteur;
	}

}