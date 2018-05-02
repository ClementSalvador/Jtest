package fr.ag2rlamondiale.gitvc.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import fr.ag2rlamondiale.gitvc.dto.Commit;
import fr.ag2rlamondiale.gitvc.dto.FileInfos;
import fr.ag2rlamondiale.gitvc.dto.ReportLine;

public class ReportEngine {

  private static final int MIN_DIFF_LINE_NB = 5;
  private static final int DELTA_FROM_DIFF_GIT_TO_CHANGES = 4;
  private static final int COMMIT_ID_LINE = -1;
  private static final int DATE_LINE = 1;
  private static final int COMMIT_LINE = 3;
  private static final int HEADER_LINE_NB = 5;



  public String generateReportCSV(String projectName, FileInfos fileInfos, String fileReportPath) {
    Set<ReportLine> reportLines =
        generateReportLinesForOnFile(projectName, fileInfos, fileReportPath);
    String csv = "";
    for (ReportLine reportLine : reportLines) {
      csv += "\n";
      csv += reportLine.getLineCsv();
    }
    return csv;
  }
  
  //a testé
  public String generateRepportEncodingCSV(Map<String,String> mapTo,Map<String,String> mapFrom){
	  String csv = "";
	  for(Map.Entry<String,String> entry : mapTo.entrySet()){
		  if(!entry.getValue().equals(mapFrom.get(entry.getKey()))){
			  csv += entry.getKey() + ";" + mapFrom.get(entry.getKey()) + ";" + mapTo.get(entry.getKey());
			  csv += "\n";
		  }
	  }
	  return csv;
  }


  private Set<ReportLine> generateReportLinesForOnFile(String projectName, FileInfos fileInfos,
      String fileReportPath) {
    Set<ReportLine> reportLines = new HashSet<>();
    List<Commit> commitsFromLog = readCommitsFromLogFile(fileInfos);
    List<String> changes = readFileDiff(fileInfos);
    List<ContentReport> lastNegativeReportLines = new ArrayList<>();
    List<ContentReport> contentReportToLog = new ArrayList<>();
    Status status = Status.NONE;
    for (String change : changes) {
      if (!isExceptionChange(change)) {
        ReportLine reportLine = new ReportLine();
        Commit c = findChangeInCommits(change, commitsFromLog);
        if (c != null) {
          initReportLine(reportLine, projectName, fileInfos, c);
        }
        Status currentStatus = findStatus(status, change);

        switch (currentStatus) {
          case START_NEGATIVE:
            contentReportToLog.addAll(lastNegativeReportLines);
            lastNegativeReportLines = new ArrayList<>();
            lastNegativeReportLines.add(new ReportEngine.ContentReport(change, reportLine));
            status = Status.SEARCH_NEGATIVE;
            break;
          case SEARCH_NEGATIVE:
            lastNegativeReportLines.add(new ReportEngine.ContentReport(change, reportLine));
            status = Status.SEARCH_NEGATIVE;
            break;
          case SEARCH_POSITIVE:
            handlePositiveLine(reportLines, lastNegativeReportLines, contentReportToLog, change,
                reportLine);
            status = Status.SEARCH_POSITIVE;
            break;
          case NONE:
          default:
            status = Status.NONE;
        }
      }
    }
    if (status.equals(Status.SEARCH_NEGATIVE)) {
      contentReportToLog.addAll(lastNegativeReportLines);
      reportLines.addAll(getReportLines(lastNegativeReportLines));
    }

    logContentReport(contentReportToLog, fileReportPath);

    return reportLines;
  }


  private void initReportLine(ReportLine reportLine, String projectName, FileInfos fileInfos,
      Commit c) {
    reportLine.setProject(projectName);
    reportLine.setAuthor(c.getAuthor());
    reportLine.setCommit(c.getCommitId());
    reportLine.setComment(c.getCommitComment());
    reportLine.setDate(c.getDate());
    reportLine.setFichier(fileInfos.getName());
    reportLine.setType("NON REPORT DE CODE");
  }


  private void handlePositiveLine(Set<ReportLine> reportLines,
      List<ContentReport> lastNegativeReportLines, List<ContentReport> contentReportToLog,
      String change, ReportLine reportLine) {
    String newChangeToTest = "-" + change.substring(1);
    ContentReport findedContentReport =
        checkIfContainsContent(lastNegativeReportLines, newChangeToTest);
    if (findedContentReport != null) {
      lastNegativeReportLines.remove(findedContentReport);
    } else {
      contentReportToLog.add(new ReportEngine.ContentReport(change, reportLine));
      reportLines.add(reportLine);
    }
  }

  private Status findStatus(Status status, String change) {
    if (change.startsWith("-")) {
      if (!status.equals(Status.SEARCH_NEGATIVE)) {
        return Status.START_NEGATIVE;
      }
      return Status.SEARCH_NEGATIVE;
    } else if (change.startsWith("+")) {
      return Status.SEARCH_POSITIVE;
    }
    return Status.NONE;
  }


  private void logContentReport(List<ContentReport> contentReportToLog, String fileReportPath) {
    String content = "";
    for (ContentReport contentReport : contentReportToLog) {
      content += contentReport.reportLine.getLineCsv() + "\n";
      content += contentReport.content + "\n\n";
    }
    CustomFileEngine.writeStringToFile(content, fileReportPath + "changes.log");
  }

  enum Status {
    SEARCH_POSITIVE, SEARCH_NEGATIVE, START_NEGATIVE, NONE
  }

  class ContentReport {
    private String content;
    private ReportLine reportLine;

    public ContentReport(String c, ReportLine rl) {
      content = c;
      reportLine = rl;
    }

    @Override
    public int hashCode() {
      return content.hashCode() + reportLine.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof ContentReport) {
        ContentReport r = (ContentReport) obj;
        return r.reportLine.equals(reportLine) && r.content.equals(content);
      }
      return false;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public ReportLine getReportLine() {
      return reportLine;
    }

    public void setReportLine(ReportLine reportLine) {
      this.reportLine = reportLine;
    }

  }

  private List<ReportLine> getReportLines(List<ContentReport> lastPositiveReportLines) {
    List<ReportLine> lines = new ArrayList<>();
    for (ContentReport contentReport : lastPositiveReportLines) {
      lines.add(contentReport.reportLine);
    }
    return lines;
  }

  private ContentReport checkIfContainsContent(List<ContentReport> lastPositiveReportLines,
      String newChangeToTest) {
    ContentReport cr = null;
    boolean finded = false;
    int cpt = 0;
    while (cpt < lastPositiveReportLines.size() && !finded) {
      if (lastPositiveReportLines.get(cpt).content.equals(newChangeToTest)) {
        cr = lastPositiveReportLines.get(cpt);
        finded = true;
      }
      cpt++;
    }
    return cr;
  }



  private static boolean isExceptionChange(String change) {
    return (change.contains("<version>") && change.contains("</version>"))
        || change.contains("\\ No newline at end of file");
  }

  private static Commit findChangeInCommits(String change, List<Commit> commitsFromLog) {
    Commit c = null;
    boolean findedCommit = false;
    int cpt = 0;
    while (cpt < commitsFromLog.size() && !findedCommit) {
      if (commitsFromLog.get(cpt).getContent().contains(change)) {
        findedCommit = true;
        c = commitsFromLog.get(cpt);
      }
      cpt++;
    }
    return c;
  }

  private static List<Commit> readCommitsFromLogFile(FileInfos fileInfos) {
    List<Commit> commits = new ArrayList<>();
    boolean currentDiffIsOurFile = false;
    boolean firstcommitFound = false;
    Commit tmpCommit = new Commit();
    int cpt = 0;

    while (cpt < fileInfos.getLogs().size()) {
      
      if (fileInfos.getLogs().get(cpt).contains("Author:")) {
        if(!firstcommitFound){
          firstcommitFound = true;
        }else if(tmpCommit.getContent() != null){
          commits.add(tmpCommit);
        }
        
        tmpCommit = new Commit();
        tmpCommit.setAuthor(getAuthorFromLogLine(fileInfos.getLogs().get(cpt)));
        tmpCommit.setCommitId(getCommitIdFromLogLine(fileInfos.getLogs().get(cpt + COMMIT_ID_LINE)));
        tmpCommit.setDate(eraseStartBlanks(getDateFromLogLine(fileInfos.getLogs().get(cpt + DATE_LINE))));
        tmpCommit.setCommitComment(
            eraseStartBlanks(getCommitCommentFromLogLine(fileInfos.getLogs().get(cpt + COMMIT_LINE))));
        cpt += HEADER_LINE_NB;
      }

      if (fileInfos.getLogs().get(cpt).startsWith("diff --git")) {
        if (fileInfos.getLogs().get(cpt).contains(fileInfos.getName())) {
          currentDiffIsOurFile = true;
        } else {
          currentDiffIsOurFile = false;
        }
        cpt += DELTA_FROM_DIFF_GIT_TO_CHANGES;
      }

      if (currentDiffIsOurFile) {
        tmpCommit.addContent(fileInfos.getLogs().get(cpt));
      }
      cpt++;
    }
    if (tmpCommit.getContent() != null) {
      commits.add(tmpCommit);
    }
    return commits;
  }

  private static String eraseStartBlanks(String dateFromLogLine) {
    String tmp = dateFromLogLine;
    while (tmp.startsWith(" ")) {
      tmp = tmp.substring(1);
    }
    return tmp;
  }

  private static String getCommitCommentFromLogLine(String string) {
    return string;
  }

  private static String getDateFromLogLine(String changeLine) {
    String[] split = changeLine.split("Date:");
    if (split.length > 1) {
      String[] split2 = split[1].split("\\+");
      return split2[0];
    } else {
      return "No id found";
    }
  }

  private static String getCommitIdFromLogLine(String changeLine) {
    String[] split = changeLine.split(" ");
    if (split.length > 1) {
      return split[1];
    } else {
      return "No id found";
    }
  }

  private static String getAuthorFromLogLine(String changeLine) {
    String res = changeLine.replaceAll(" ", "");
    String[] split = res.split(":");
    if (split.length > 1) {
      return split[1];
    } else {
      return "No author found";
    }
  }



  private static List<String> readFileDiff(FileInfos fileInfos) {
    List<String> changes = new ArrayList<String>();
    if (fileInfos.getDiffs().size() > MIN_DIFF_LINE_NB) {
      for (int i = 0; i < MIN_DIFF_LINE_NB; i++) {
        fileInfos.getDiffs().remove(0);
      }
    }

    for (String line : fileInfos.getDiffs()) {
      if (line.startsWith("+") || line.startsWith("-")) {
        String withoutSpaces = line.replaceAll(" ", "");
        withoutSpaces = withoutSpaces.replaceAll("\t", "");
        if (!"+".equals(withoutSpaces) && !"-".equals(withoutSpaces)) {
          changes.add(line);
        }
      }
    }

    return changes;
  }

}
