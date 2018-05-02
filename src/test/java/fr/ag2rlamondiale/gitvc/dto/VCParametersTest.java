package fr.ag2rlamondiale.gitvc.dto;

import org.junit.Assert;
import org.junit.Test;


public class VCParametersTest {
	private final String csvLine_valid = "repositoryTest;V1;V2";
	private final String csvLine_notvalid = "repositoryTest;V1;";
	
	@Test
	public void initFromCSVLine_valid_test(){
		VCParameters parameters = VCParameters.initFromCSVLine(csvLine_valid);
		Assert.assertNotNull(parameters);
		Assert.assertEquals("repositoryTest", parameters.getRepositoryUri());
		Assert.assertEquals("V1", parameters.getVersionFrom());
		Assert.assertEquals("V2", parameters.getVersionTo());
	}
	
	@Test
	public void initFromCSVLine_notvalid_test(){
		VCParameters parameters = VCParameters.initFromCSVLine(csvLine_notvalid);
		Assert.assertNotNull(parameters);
		Assert.assertEquals(true, parameters.hasNullOrEmptyParameter());
	}
	
	@Test
	public void setter_test(){
		VCParameters test = VCParameters.initFromCSVLine(csvLine_valid);
		test.setReportDate("17/04/2018");
		
		Assert.assertEquals("17/04/2018",test.getReportDate());
	}
	
	@Test
	public void hasNullOrEmptyParameters_test(){
		VCParameters test = VCParameters.initFromCSVLine(";v1;v2");
		VCParameters test2 = VCParameters.initFromCSVLine("repositoryTest;;v2");
		VCParameters test3 = VCParameters.initFromCSVLine("repositoryTest;v1;");
	
		Assert.assertTrue(test.hasNullOrEmptyParameter());
		Assert.assertTrue(test2.hasNullOrEmptyParameter());
		Assert.assertTrue(test3.hasNullOrEmptyParameter());
		
	}
}
