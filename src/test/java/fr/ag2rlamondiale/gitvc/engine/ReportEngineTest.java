package fr.ag2rlamondiale.gitvc.engine;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.ag2rlamondiale.gitvc.dto.FileInfos;

public class ReportEngineTest {
	
	ReportEngine rp;
	
	@Before
	public void before_test(){
		rp = new ReportEngine();
	}
	
	
	@Test
	public void generateRapportCSV_test() throws ParseException{
		FileInfos fileInfos = new FileInfos();
		String projectName = "GitVC";
		String filereportPath = "src/test/resources/report";
		fileInfos.setName("/src/paquet1/Class02.java");
		fileInfos.setDiffs(CustomFileEngine.getLinesFromFile("src/test/resources/gitdiff.log"));
		fileInfos.setLogs(CustomFileEngine.getLinesFromFile("src/test/resources/gitlog.log"));
		
		String report = rp.generateReportCSV(projectName, fileInfos, filereportPath);
		String[] lignes = report.split("\n");
		
		for(int i = 1; i < lignes.length; ++i){
			System.out.println(lignes[i]);
			String[] champ = lignes[i].split(";");
			assertEquals(projectName,champ[0]);
			assertEquals("SALVADOR<clement.salvador@cgi.com>",champ[1]);
			assertEquals(fileInfos.getName(),champ[2]);
			
			//le quatrieme champ doit correspondre à l'id du commit
			assertTrue(champ[3].matches("[a-zA-Z0-9]{40}"));
			
			//le cinquieme champ doit correspondre au message du commit
			assertTrue(champ[4].matches("(Rel_01 : )(.*)"));
			
			
			//le sixieme champ doit correspondre à une date (expression reguliere un peu grande)
			assertTrue(champ[5].matches("([a-zA-Z]{3})( )([a-zA-Z]{3})( )([1-9]|1[0-9]|2[0-9]|3[0-1])( )(0[0-9]|1[0-9]|2[0-3])(:)(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])"
					+ "(:)(0[0-9]|1[0-9]|2[0-9]|3[0-9]|4[0-9]|5[0-9])( )(\\d{4})( )"));
			
			//Attention, si on ajoute la vérification d'encodage il faudra changer ce test
			assertEquals("NON REPORT DE CODE",champ[6]);
			
		}
	}
	
	@Test
	public void generateRepportEncodingCSV_test(){
		Map<String,String> mapTo = new HashMap<String,String>();
		Map<String,String> mapFrom = new HashMap<String,String>();
		
		mapFrom.put("fichier1.java", "UTF-8");
		mapFrom.put("fichier2.java", "UTF-8");
		mapFrom.put("fichier3.java","UTF-8");
		
		mapTo.put("fichier1.java", "Cp1525");
		mapTo.put("fichier2.java", "UTF-8");
		mapTo.put("fichier3.java","Cp1525");
		
		String rapportTemoin = "fichier1.java;UTF-8;Cp1525\nfichier3.java;UTF-8;Cp1525\n";
		
		assertEquals(rapportTemoin,rp.generateRepportEncodingCSV(mapTo, mapFrom));
	}
}
