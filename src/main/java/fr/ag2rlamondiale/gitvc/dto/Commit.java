package fr.ag2rlamondiale.gitvc.dto;

public class Commit {
  private String author;
  private String date;
  private String commitId;
  private String commitComment;
  private String content;

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCommitId() {
    return commitId;
  }

  public void setCommitId(String commitId) {
    this.commitId = commitId;
  }

  public String getCommitComment() {
    return commitComment;
  }

  public void setCommitComment(String commitComment) {
    this.commitComment = commitComment;
  }

  public void addContent(String string) {
    if (content == null) {
      content = string;
    } else {
      content += "\n" + string;
    }

  }

}
