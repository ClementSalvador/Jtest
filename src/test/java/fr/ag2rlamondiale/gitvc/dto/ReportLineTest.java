package fr.ag2rlamondiale.gitvc.dto;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ReportLineTest {

	ReportLine test;

	String project;
	String author;
	String fichier;
	String commit;
	String comment;
	String date;
	String type;



	@Before
	public void before_test(){
		
		project = "projet";
		author = "auteur";
		fichier = "fichier";
		commit = "commit";
		comment = "commentaire";
		date = "date";
		type = "type";
		test = new ReportLine();
		test.setProject(project);
		test.setAuthor(author);
		test.setFichier(fichier);
		test.setCommit(commit);
		test.setComment(comment);
		test.setDate(date);
		test.setType(type);
	}

	@Test
	public void hashCode_test(){

		int hashcodeTemoin = project.hashCode() + author.hashCode() + fichier.hashCode() + commit.hashCode() + comment.hashCode() + date.hashCode() + type.hashCode();
		int hascodeTest = test.hashCode();

		assertEquals(hashcodeTemoin,hascodeTest);
	}

	@Test
	public void equals_true_test(){
		ReportLine test2 = test;
		ReportLine testPareil = new ReportLine();
		testPareil.setProject(project);
		testPareil.setAuthor(author);
		testPareil.setFichier(fichier);
		testPareil.setCommit(commit);
		testPareil.setComment(comment);
		testPareil.setDate(date);
		testPareil.setType(type);
		
		assertTrue(test.equals(test2));
		assertTrue(test.equals(testPareil));
		
	}
	
	@Test
	public void equals_null_test(){
		ReportLine test3 = null;
		assertFalse(test.equals(test3));
	}
	
	@Test
	public void equals_differentField_test(){
		ReportLine test4 = new ReportLine();
		test4.setAuthor("auteur_different");
		
		assertFalse(test.equals(test4));
		assertFalse(test4.equals(test));
	}
	
	@Test
	public void equals_wrongObject_test(){
		assertFalse(test.equals("pasUnReportLine"));
	}
	
	@Test
	public void equals_oneFieldNull_test(){
		ReportLine test5 = new ReportLine();
		test5.setAuthor(null);
		test5.setProject("UnProjet");
		test.setProject(null);
		assertFalse(test.equals(test5));
		assertFalse(test5.equals(test));
	}
	
	
	
	@Test
	public void equals_bothFieldNull_test(){
		ReportLine test6 = new ReportLine();
		ReportLine test7 = new ReportLine();
		
		test7.setProject(null);
		test7.setAuthor(null);
		test7.setFichier(null);
		test7.setCommit(null);
		test7.setComment(null);
		test7.setDate(null);
		test7.setType(null);
		
		test6.setProject(null);
		test6.setAuthor(null);
		test6.setFichier(null);
		test6.setCommit(null);
		test6.setComment(null);
		test6.setDate(null);
		test6.setType(null);
		
		assertTrue(test7.equals(test6));
	}
		
	@Test
	public void getLineCsv_test(){
		String ligneCsvTemoin = "projet;auteur;fichier;commit;commentaire;date;type";
		assertEquals(ligneCsvTemoin,test.getLineCsv());
	}
	
	
	@Test
	public void getter_test(){
		assertEquals(project,test.getProject());
		assertEquals(author,test.getAuthor());
		assertEquals(fichier,test.getFichier());
		assertEquals(commit,test.getCommit());
		assertEquals(comment,test.getComment());
		assertEquals(date,test.getDate());
		assertEquals(type,test.getType());
	}

}
