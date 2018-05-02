package fr.ag2rlamondiale.gitvc.dto;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CommitTest {

	private Commit commitTest;
	
	private String author;
	private String commitId;
	private String date;
	private String commitComment;
	private String content;
	
	@Before
	public void before_test(){
		author = "auteur";
		date ="04/10/1998";
		commitId ="001";
		commitComment = "commentaire";
		content = "contenu";
		
		commitTest = new Commit();
		commitTest.setAuthor(author);
		commitTest.setDate(date);
		commitTest.setCommitId(commitId);
		commitTest.setCommitComment(commitComment);
		commitTest.setContent(content);
	}
	
	@Test
	public void getters_test(){
		assertEquals(author,commitTest.getAuthor());
		assertEquals(date,commitTest.getDate());
		assertEquals(commitId,commitTest.getCommitId());
		assertEquals(commitComment,commitTest.getCommitComment());
		assertEquals(content,commitTest.getContent());
	}
	
	@Test
	public void addContent_contentNull_test(){
		commitTest.setContent(null);
		String string = "duNouveauContenu";
		commitTest.addContent(string);
		
		assertEquals(string,commitTest.getContent());
	}
	
	@Test
	public void addContent_contentNotNull_test(){
		String string = "duNouveauContenu";
		String contenuFinal = commitTest.getContent() + "\n" + string;
		
		commitTest.addContent(string);
		assertEquals(contenuFinal,commitTest.getContent());
	}

}
