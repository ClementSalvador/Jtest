package fr.ag2rlamondiale.gitvc.dto;

import java.util.ArrayList;
import java.util.List;

public enum Test {
	PROJET1("nomCompletProjet1"),
	PROJET2("nomCompletPojet2");
	
	private String nom;
	private List<String> files;
	
	Test(String nom){
		this.nom = nom;
	}
	
	public String getNom(){
		return nom;
	}
	
	public void initFiles(){
		files = new ArrayList<>();
		switch (this){
			case PROJET1:
				
				
			
		}
			
	}
}
