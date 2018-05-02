package fr.ag2rlamondiale.gitvc.engine;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.Logger;

import fr.ag2rlamondiale.gitvc.referential.VCConstants;

public class CustomFileEngineTest {

  @Before
  public void before_tests() {
    File reportFold = new File("rapport");
    if (!reportFold.exists()) {
      reportFold.mkdirs();
    }
  }

  @Test
  public void getLines_oneline_test() {
    List<String> fileContent = CustomFileEngine.getLinesFromFile(
        getClass().getClassLoader().getResource("test_1.csv").getPath().substring(1));
    assertEquals("repositoryTest;V1;V2;Dossier/", fileContent.get(0));
  }

  @Test
  public void getLines_multiplelines_test() {
    List<String> fileContent = CustomFileEngine.getLinesFromFile(
        getClass().getClassLoader().getResource("test_2.csv").getPath().substring(1));
    assertEquals(2, fileContent.size());
    assertEquals("repositoryTest;V1;V2;Dossier/", fileContent.get(0));
    assertEquals("repositoryTest2;V3;V4;Dossier/", fileContent.get(1));

  }

  @Test
  public void getLines_KO_test() {
    List<String> fileContent = CustomFileEngine.getLinesFromFile(
        "cepathnepeutpasexistersaufsiundevestungrostrollmaisilferaplantercetestdoncilestcon.txt");
    assertEquals(0, fileContent.size());
  }

  @Test
  public void readCSVLine_test() {
    List<String> fileContent = CustomFileEngine.getLinesFromFile(
        getClass().getClassLoader().getResource("test_2.csv").getPath().substring(1));
    assertEquals(4, CustomFileEngine.readCSVLine(fileContent.get(0)).size());
  }

  @Test
  public void writeReportToFile_test() {
    String csvReport = "test;test2;test3\ntest4;test5;test6";
    String formattedDate = Utils.getFormattedDate();
    CustomFileEngine.writeReportToFile(csvReport, formattedDate);
    List<String> fileContent =
        CustomFileEngine.getLinesFromFile("rapport/Report_" + formattedDate + ".csv");
    assertEquals(2, fileContent.size());

    // Nettoyage du fichier
    File f = new File("rapport/Report_" + formattedDate + ".csv");
    if (f.exists())
      f.delete();
  }

  @Test
  public void writeStringToFile_test() {
    String csvReport = "ducontenu \n unautrecontenu";
    CustomFileEngine.writeStringToFile(csvReport, "testwriteFile.csv");
    List<String> fileContent = CustomFileEngine.getLinesFromFile("testwriteFile.csv");
    assertEquals(2, fileContent.size());
    // Nettoyage du fichier
    File f = new File("testwriteFile.csv");
    if (f.exists())
      f.delete();
  }
  
  @Test
  public void writeStringToFile_wrongPath_test(){
	  String directory = "src/test/resources/";
	  CustomFileEngine.writeStringToFile("ola \n ole",directory);
	  File f = new File(directory);
	  List<String> fileContent = CustomFileEngine.getLinesFromFile(directory);
	  assertEquals(0,fileContent.size());
	  if (f.exists())
	      f.delete();
  }
  
  @Test
  public void removeDirectory_test() throws FileNotFoundException, IOException {
    File fold = new File("fold");
    fold.mkdirs();
    new FileOutputStream(new File("fold/ahah")).close();
    assertEquals(true, fold.exists());
    CustomFileEngine.removeDirectory(fold);
    assertEquals(false, fold.exists());
  }

  @Test
  public void cleanReportFolder_test() {
    File f = new File(VCConstants.REPORT_FOLDER_NAME);
    if (!f.exists()) {
      f.mkdir();
    }
    CustomFileEngine.cleanReportFolder();
    f = new File(VCConstants.REPORT_FOLDER_NAME);
    assertEquals(false, f.exists());
  }
  
  @Test
  public void getFileEncodingFromPath_test() throws IOException{
	  String path = "src/test/resources/ClassTestUTF8.java";
	  String encoding = CustomFileEngine.getFileEncodingFromPath(path);
	  assertEquals("UTF8",encoding);
  }
  
  @Test
  public void getFileEncodingFromPath_wrongPath_test(){
	  String path = "un/chemin/inconnu";
	  String encoding = "";
	  try{
		  encoding = CustomFileEngine.getFileEncodingFromPath(path);
	  }catch(IOException e){
		  assertTrue(e instanceof IOException);
	  }
	  assertEquals("",encoding);
  }
  
  @Test
  public void checkIfFileInBranches_test(){
	  Set<String> temoin = new HashSet<String>();
	  temoin.add("src/test/resources/test_1.csv");
	  temoin.add("src/test/resources/test_2.csv");
	  temoin.add("src/test/resources/test_3.csv");
	  
	  Set<String> test = new HashSet<String>();
	  test.add("src/test/resources/test_1.csv");
	  test.add("src/test/resources/test_2.csv");
	  test.add("src/test/resources/test_3.csv");
	  test.add("un/fichier/qui/n/existe/pas.csv");
	  test.add("un/autre/inexistant.csv");
	  
	  test = CustomFileEngine.checkIfFileInBranches(test);
	  String[] temoin_tab = temoin.toArray(new String[0]);
	  String[] test_tab = test.toArray(new String[0]);
	  
	  assertEquals(temoin.size(),test.size());
	  assertEquals(temoin_tab[0],test_tab[0]);
	  assertEquals(temoin_tab[1],test_tab[1]);
	  assertEquals(temoin_tab[2],test_tab[2]);
  }
  
  @Test
  public void assoFileEncodWithPath_test() throws IOException{
	  Set<String> test = new HashSet<String>();
	  String p1 = "src/test/resources/ClassTestUTF8.java";
	  String p2 = "src/test/resources/FichierUT8";
	  test.add(p1);
	  test.add(p2);
	  
	  Map<String,String> mapTest = CustomFileEngine.assoFileEncodWithPath(test);
	  
	  assertEquals(CustomFileEngine.getFileEncodingFromPath(p1),mapTest.get(p1));
	  assertEquals(CustomFileEngine.getFileEncodingFromPath(p2),mapTest.get(p2));
	  
  }
  
  @Test
  public void assoFileEncodWithPath_execptionFile_test() throws IOException{
	  Set<String> files = new HashSet<String>();
	  files.add("test.yml");
	  files.add("test.project");
	  files.add("pom.xml");
	  files.add("test.gitignore");
	  
	  Map<String,String> mapTest = CustomFileEngine.assoFileEncodWithPath(files);
	  
	  assertEquals(0,mapTest.size());
  }
  
  @Test
  public void copyFileFromPath_goodPath_test() throws IOException{
	  String from = "src/test/resources/test_1.csv";
	  String to = "src/test/resources/newFile.csv";
	  
	  CustomFileEngine.copyFileFromPath(from, to);
	  File test = new File(to);
	  assertTrue(test.exists());
	  
	  if(test.exists()){
		  test.delete();
	  }
  }
  
  @Test
  public void copyFileFromPath_wrongPath_test() throws IOException{
	  
	  String from = "un/fichier/inexistant.csv";
	  String to = "src/test/resources/newFile.csv";
	  
	  CustomFileEngine.copyFileFromPath(from, to);
	  File test = new File(to);
	  assertFalse(test.exists());
	  
	  if(test.exists()){
		  test.delete();
	  }
  }
}
