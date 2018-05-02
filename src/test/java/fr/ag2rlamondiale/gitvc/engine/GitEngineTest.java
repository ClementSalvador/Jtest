package fr.ag2rlamondiale.gitvc.engine;

import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import fr.ag2rlamondiale.gitvc.dto.VCParameters;

public class GitEngineTest {

  private String repository = "git@github.com:ClementSalvador/projetTest.git";
  private String targetFolderRepository;
  private String versionFrom = "Rel_01";
  private String versionTo = "Dev";

  private VCParameters parameters;

  @Before
  public void before() throws GitAPIException, IOException {
    parameters = VCParameters.initFromCSVLine(repository + ";" + versionFrom + ";" + versionTo);
    targetFolderRepository = Utils.getFolderFromRepository(repository);
    GitEngine.getInstance().init(parameters);
  }

  @After
  public void after() throws IOException {
    GitEngine.getInstance().eraseRepository();
  }
  
  @Test 
  public void init_KO_test(){
    VCParameters p = VCParameters.initFromCSVLine("alzkejklzje;fmrkemlfkre;fmreklfmer");
    try{
      GitEngine.getInstance().init(p);
      fail("Devrait avoir planté ici ");
    }catch(GitAPIException e){}
  }
  
  @Test 
  public void init_checkout_KO_test(){
    GitEngine.getInstance().eraseRepository();
    VCParameters p = VCParameters.initFromCSVLine(repository+";fmrkemlfkre;fmreklfmer");
    try{
      GitEngine.getInstance().init(p);
      fail("Devrait avoir planté ici ");
    }catch(GitAPIException e){}
  }
  
  @Test 
  public void init_merge_KO_test(){
    GitEngine.getInstance().eraseRepository();
    VCParameters p = VCParameters.initFromCSVLine(repository + ";" + versionFrom + ";fmreklfmer");
    try{
      GitEngine.getInstance().init(p);
      fail("Devrait avoir planté ici ");
    }catch(GitAPIException e){}
  }

  @Test
  public void cloneRepository_testFolder() throws GitAPIException {
    File folder = new File(targetFolderRepository);
    Assert.assertEquals(true, folder.exists());
  }

  @Test
  public void cloneRepository_testFile() throws GitAPIException {
    File fileToTest = new File(targetFolderRepository + File.separator + "readme.md");
    Assert.assertEquals(true, fileToTest.exists());
  }

  @Test
  public void cloneRepository_testErase() throws IOException {
    GitEngine.getInstance().eraseRepository();
    File folder = new File(targetFolderRepository);
    Assert.assertEquals(false, folder.exists());
  }

  @Test
  public void checkoutBranch_test() throws GitAPIException {
    File f = new File(
        targetFolderRepository + "/src/main/java/test/package1/FileCreatedInRelease1.java");
    Assert.assertEquals(true, f.exists());
  }

  @Test
  public void mergeTwoVersion_test() throws GitAPIException {
    File f = new File(
        targetFolderRepository + "/src/main/java/test/package3/ClassCreatedInDevelopment.java");
    Assert.assertEquals(true, f.exists());
  }

  @Test
  public void diffTwoVersion_test() throws GitAPIException {
    Set<String> diffs = GitEngine.getInstance().diffWith(versionTo);
    Assert.assertEquals(2, diffs.size());
  }

  @Test
  public void logFile_test() throws GitAPIException {
    List<String> test =
        GitEngine.getInstance().logFile("src/main/java/test/package4/IInterface.java",
            new File("target/").getAbsolutePath() + File.separator);
    Assert.assertEquals(35, test.size());
  }
  
  @Test
  public void logFile_KO_test() throws GitAPIException {
    List<String> test =
        GitEngine.getInstance().logFile("truuuuuuuuuuuuuuuuuuuuuuuuuuc",
            new File("target/").getAbsolutePath() + File.separator);
    Assert.assertEquals(0, test.size());
  }

  @Test
  public void diffFile_test() throws GitAPIException {
    List<String> test = GitEngine.getInstance().diffFile(versionFrom, versionTo,
        "src/main/java/test/package4/IInterface.java",
        new File("target/").getAbsolutePath() + File.separator);
    Assert.assertEquals(12, test.size());
  }
  
  @Test
  public void diffFile_KO_test() throws GitAPIException {
    List<String> test = GitEngine.getInstance().diffFile(versionFrom, versionTo,
        "bidulemachintruc.da",
        new File("target/").getAbsolutePath() + File.separator);
    Assert.assertEquals(0, test.size());
  }
}
