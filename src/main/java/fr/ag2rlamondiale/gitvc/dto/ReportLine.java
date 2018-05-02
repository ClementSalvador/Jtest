package fr.ag2rlamondiale.gitvc.dto;

public class ReportLine {
  private String project = "";
  private String author = "";
  private String fichier = "";
  private String commit = "";
  private String comment = "";
  private String date = "";
  private String type = "";
  
  private static final String TITLE = "Projet;Auteur;Fichier concern�;commit;commentaire;date;type";
  private static final String ENCODING_TITLE = "Projet;fichier;ancien encodage;nouvel encodage";
  
  public String getLineCsv() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(project);
	  sb.append(";");
	  sb.append(author);
	  sb.append(";");
	  sb.append(fichier);
	  sb.append(";");
	  sb.append(commit);
	  sb.append(";");
	  sb.append(comment);
	  sb.append(";");
	  sb.append(date);
	  sb.append(";");
	  sb.append(type);
	  
	  return sb.toString();
  }

  public static String getTitleLineCsv() {
	  return TITLE;
  }
  
  public static String getTiltleEncodingCsv(){
	  return ENCODING_TITLE;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getFichier() {
    return fichier;
  }

  public void setFichier(String fichier) {
    this.fichier = fichier;
  }

  public String getCommit() {
    return commit;
  }

  public void setCommit(String commit) {
    this.commit = commit;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public int hashCode() {
	  return project.hashCode() + author.hashCode() + fichier.hashCode() + commit.hashCode()
        + comment.hashCode() + date.hashCode() + type.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof ReportLine)) {
      return false;
    }
    ReportLine r = (ReportLine) obj;

    boolean parametersOk =
        compare(r.project, project) && compare(r.author, author) && compare(r.fichier, fichier);
    parametersOk = parametersOk && compare(r.commit, commit) && compare(r.comment, comment);
    parametersOk = parametersOk && compare(r.date, date) && compare(r.type, type);

    return parametersOk;
  }

  private boolean compare(String s1, String s2) {
    if (null == s1) {
      return null == s2;
    } else {
      return s1.equals(s2);
    }
  }
}
