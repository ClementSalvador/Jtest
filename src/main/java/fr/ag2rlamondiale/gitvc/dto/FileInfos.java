package fr.ag2rlamondiale.gitvc.dto;

import java.util.List;

public class FileInfos {
  private String name;
  private List<String> diffs;
  private List<String> logs;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getDiffs() {
    return diffs;
  }

  public void setDiffs(List<String> diffs) {
    this.diffs = diffs;
  }

  public List<String> getLogs() {
    return logs;
  }

  public void setLogs(List<String> logs) {
    this.logs = logs;
  }


}
