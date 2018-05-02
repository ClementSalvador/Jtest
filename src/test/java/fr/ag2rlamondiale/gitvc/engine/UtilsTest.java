package fr.ag2rlamondiale.gitvc.engine;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ag2rlamondiale.gitvc.dto.VCParameters;

public class UtilsTest {

	private VCParameters parameters;

	@Before
	public void before_tests() {
		parameters = VCParameters.initFromCSVLine("git@git-prd.server.lan:A1002/GPSWkf.git;V1;V2");
	}

	@Test
	public void getFolderFromRepository_test_repo_OK() {
		Assert.assertEquals("GPSWkf", Utils.getFolderFromRepository("git@git-prd.server.lan:A1002/GPSWkf.git"));
	}

	@Test
	public void getFolderFromRepository_test_repo_KO() {
		Assert.assertEquals("ERROR", Utils.getFolderFromRepository("zemkldn,zklendzjmhffzefrgeg:ezmlfkmzlef"));
	}

	@Test
	public void getGeneralDiffFilePath_test() {
		String path = Paths.get("").toAbsolutePath().toString() + File.separator + "rapport" + File.separator + "GPSWkf"
				+ File.separator + parameters.getReportDate() + File.separator + "diff.log";
		Assert.assertEquals(path, Utils.getGeneralDiffFilePath(parameters));
	}

	@Test
	public void getDedicatedFileDiffFilePath_test() {
		String path = Paths.get("").toAbsolutePath().toString() + File.separator + "rapport" + File.separator + "GPSWkf"
				+ File.separator + parameters.getReportDate() + File.separator + "stats" + File.separator + "test.java"+ File.separator +"gitdiff.log";
		Assert.assertEquals(path, Utils.getDedicatedFileDiffFilePath(parameters, "blablabla/blabla/dsjkdl/test.java"));
	}

	@Test
	public void getDedicatedFileLogFilePath_test() {
		String path = Paths.get("").toAbsolutePath().toString() + File.separator + "rapport" + File.separator + "GPSWkf"
				+ File.separator + parameters.getReportDate() + File.separator + "stats" + File.separator + "test.java"+ File.separator + "gitlog.log";
		Assert.assertEquals(path, Utils.getDedicatedFileLogFilePath(parameters, "blablabla/blabla/dsjkdl/test.java"));
	}	
	
	@Test
	public void isExceptionFile_test(){
	  Assert.assertEquals(false, Utils.isExceptionFile("monTest.java"));
	  Assert.assertEquals(false, Utils.isExceptionFile("monTest.xml"));
	  Assert.assertEquals(false, Utils.isExceptionFile("monTest.js"));
	  Assert.assertEquals(true, Utils.isExceptionFile("monTest.yml"));
	  Assert.assertEquals(true, Utils.isExceptionFile(".gitignore"));
	  Assert.assertEquals(true, Utils.isExceptionFile("monTest.project"));
	  Assert.assertEquals(true, Utils.isExceptionFile("pom.xml"));
	}
	
	
}
