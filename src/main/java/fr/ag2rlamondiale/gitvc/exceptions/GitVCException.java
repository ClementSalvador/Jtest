package fr.ag2rlamondiale.gitvc.exceptions;

import org.eclipse.jgit.api.errors.GitAPIException;

public class GitVCException extends GitAPIException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public GitVCException(String message) {
    super(message);
  }

}
