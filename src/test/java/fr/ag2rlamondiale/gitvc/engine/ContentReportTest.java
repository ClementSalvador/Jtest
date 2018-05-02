package fr.ag2rlamondiale.gitvc.engine;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.ag2rlamondiale.gitvc.dto.ReportLine;
import fr.ag2rlamondiale.gitvc.engine.ReportEngine.ContentReport;

public class ContentReportTest {
	private ContentReport cr;
	private ReportLine rl;
	private String content;
	private ReportEngine rp;
	
	@Before
	public void before_test(){
		rp = new ReportEngine();
		content = "contenu";
		rl = new ReportLine();
		rl.setAuthor("clement");
		cr = rp.new ContentReport(content,rl);
	}
	
	@Test
	public void hashcode_test(){
		int hashcode_temoin = content.hashCode() + rl.hashCode();
		assertEquals(hashcode_temoin,cr.hashCode());
	}
	
	@Test
	public void getterAndSetters_test(){
		String content2 = "nouveauContenu";
		ReportLine rl2 = new ReportLine();
		
		cr.setContent(content2);
		cr.setReportLine(rl2);
		
		assertEquals(content2,cr.getContent());
		assertEquals(rl2,cr.getReportLine());
	}
	
	@Test
	public void equals_true_test(){
		ContentReport cr2 = rp.new ContentReport(content,rl);
		assertTrue(cr.equals(cr2));
	}
	
	@Test
	public void equals_notContentReportObj_test(){
		assertFalse(cr.equals(1234));
	}
	
	 @Test
	public void equals_differentContent_test(){
		 ContentReport cr2 = rp.new ContentReport("unContenuDifferent",rl);
		 assertFalse(cr.equals(cr2));
	}
	
	 @Test
	public void equals_differentReportLine_test(){
		ReportLine rl2 = new ReportLine();
		rl2.setAuthor("Thomas");
		ContentReport cr2 = rp.new ContentReport(content,rl2);
		assertFalse(cr.equals(cr2));
	 }
}
