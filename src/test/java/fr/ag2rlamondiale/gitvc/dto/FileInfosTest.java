package fr.ag2rlamondiale.gitvc.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileInfosTest {

	private FileInfos fiTest;
	private String name;
	private List<String> diff;
	private List<String> logs;
	
	@Before
	public void before_test(){
		fiTest = new FileInfos();
		diff = new ArrayList<String>();
		logs =  new ArrayList<String>();
		diff.add("diff1");
		diff.add("diff2");
		logs.add("log1");
		logs.add("log2");
		
		fiTest.setName(name);
		fiTest.setDiffs(diff);
		fiTest.setLogs(logs);
	}
	
	@Test
	public void getters_test(){
		assertEquals(name,fiTest.getName());
		assertEquals(diff,fiTest.getDiffs());
		assertEquals(logs,fiTest.getLogs());
	}

}
