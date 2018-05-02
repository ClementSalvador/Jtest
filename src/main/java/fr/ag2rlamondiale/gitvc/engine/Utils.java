package fr.ag2rlamondiale.gitvc.engine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import fr.ag2rlamondiale.gitvc.dto.VCParameters;

public class Utils {
  private static final int LENGTH_REPO_AFTER_SPLIT = 2;
  
  private Utils() {}

  public static String getFolderFromRepository(String repository) {
    String[] repoSplit1 = repository.split("/");
    if (repoSplit1 != null && repoSplit1.length == LENGTH_REPO_AFTER_SPLIT) {
      String[] repoSplit2 = repoSplit1[1].split("\\.");
      return repoSplit2[0];
    }
    return "ERROR";
  }

  public static String getGeneralDiffFilePath(VCParameters parameters) {
    StringBuilder txt = new StringBuilder();
    txt.append(parameters.getReportFolder());
    txt.append(File.separator);
    txt.append(getFolderFromRepository(parameters.getRepositoryUri()));
    txt.append(File.separator);
    txt.append(parameters.getReportDate());
    txt.append(File.separator);
    txt.append("diff.log");
    return txt.toString();
  }

  public static String getDedicatedFileDiffFilePath(VCParameters parameters, String modifiedFile) {
    StringBuilder txt = new StringBuilder();
    txt.append(getStatsFilePath(parameters, modifiedFile));
    txt.append("gitdiff.log");
    return txt.toString();
  }

  public static String getDedicatedFileLogFilePath(VCParameters parameters, String modifiedFile) {
    StringBuilder txt = new StringBuilder();
    txt.append(getStatsFilePath(parameters, modifiedFile));
    txt.append("gitlog.log");
    return txt.toString();
  }

  public static String getFormattedDate() {
    Calendar c = GregorianCalendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmssS");
    return format.format(c.getTime());
  }

  private static Object getStatsFilePath(VCParameters parameters, String modifiedFile) {
    StringBuilder txt = new StringBuilder();
    txt.append(parameters.getReportFolder());
    txt.append(File.separator);
    txt.append(getFolderFromRepository(parameters.getRepositoryUri()));
    txt.append(File.separator);
    txt.append(parameters.getReportDate());
    txt.append(File.separator);
    txt.append("stats");
    txt.append(File.separator);
    String[] splittedName = modifiedFile.split("/");
    if (splittedName.length > 1) {
      txt.append(splittedName[splittedName.length - 1]);
    } else {
      txt.append(splittedName[0]);
    }
    txt.append(File.separator);
    return txt.toString();
  }

  public static boolean isExceptionFile(String file) {
    return file.endsWith(".yml") || file.endsWith(".project") || file.endsWith("pom.xml")
        || file.endsWith(".gitignore");
  }
}
