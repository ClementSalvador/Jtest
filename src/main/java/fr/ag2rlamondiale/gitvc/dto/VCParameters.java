package fr.ag2rlamondiale.gitvc.dto;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import fr.ag2rlamondiale.gitvc.engine.CustomFileEngine;
import fr.ag2rlamondiale.gitvc.engine.Utils;
import fr.ag2rlamondiale.gitvc.referential.VCConstants;

public class VCParameters {
  private static final int MIN_PARAM = 3;
  private static final int REPO_IND = 0;
  private static final int VERSION_FROM_IND = 1;
  private static final int VERSION_TO_IND = 2;

  private String repositoryUri;
  private String versionFrom;
  private String versionTo;
  private String reportFolder;
  private String reportDate;

  private VCParameters() {

  }
  
  /**
   * Initialise les paramètre à partir d'une ligne CSV
   * @param ligne de fichier csv
   * @return this
   */
  public static VCParameters initFromCSVLine(String line) {
    VCParameters vcparams = new VCParameters();
    List<String> values = CustomFileEngine.readCSVLine(line);
    if (values.size() >= MIN_PARAM) {
      vcparams.repositoryUri = values.get(REPO_IND);
      vcparams.versionFrom = values.get(VERSION_FROM_IND);
      vcparams.versionTo = values.get(VERSION_TO_IND);
    }
    vcparams.reportFolder =
        Paths.get("").toAbsolutePath().toString() + File.separator + VCConstants.REPORT_FOLDER_NAME;
    vcparams.reportDate = Utils.getFormattedDate();
    return vcparams;
  }

  public boolean hasNullOrEmptyParameter() {
    return nullOrEmpty(repositoryUri) || nullOrEmpty(versionFrom) || nullOrEmpty(versionTo)
        || nullOrEmpty(reportFolder);
  }

  private boolean nullOrEmpty(String s) {
    return s == null || s.isEmpty() || "".equals(s);
  }

  @Override
  public String toString() {
    return "repositoryUri:" + repositoryUri + ", versionFrom:" + versionFrom + ", versionTo:"
        + versionTo + ", reportFolder" + reportFolder;
  }

  // GETTERS

  public String getRepositoryUri() {
    return repositoryUri;
  }

  public String getVersionFrom() {
    return versionFrom;
  }

  public String getVersionTo() {
    return versionTo;
  }

  public String getReportFolder() {
    return reportFolder;
  }

  public String getReportDate() {
    return reportDate;
  }

  public void setReportDate(String reportDate) {
    this.reportDate = reportDate;
  }

}
