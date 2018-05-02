package fr.ag2rlamondiale.gitvc.main;

import java.io.File;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import fr.ag2rlamondiale.gitvc.engine.CustomFileEngine;
import fr.ag2rlamondiale.gitvc.referential.VCConstants;

public class MainTest {

  @Test
  public void testFullProcess() {
    GitVC gitVC =
        new GitVC(getClass().getClassLoader().getResource("test_3.csv").getPath().substring(1));
    gitVC.execute();
    // Comme il m'est impossible de connaitre le timestamp du rapport, je vais parcourir le dossier
    // rapport à la recherche d'un CSV
    File reportFolder = new File(VCConstants.REPORT_FOLDER_NAME);
    String[] subfiles = reportFolder.list();
    int cpt = 0;
    boolean finded = false;
    while (!finded && cpt < subfiles.length) {
      if (subfiles[cpt].contains(".csv")) {
        finded = true;
      } else {
        cpt++;
      }
    }
    Assert.assertEquals(true, finded);
    if (finded) {
      List<String> lines = CustomFileEngine
          .getLinesFromFile(VCConstants.REPORT_FOLDER_NAME + File.separator + subfiles[cpt]);
      Assert.assertEquals(3, lines.size());
    }
  }
}
