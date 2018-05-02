package fr.ag2rlamondiale.gitvc.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import fr.ag2rlamondiale.gitvc.dto.FileInfos;
import fr.ag2rlamondiale.gitvc.dto.VCParameters;
import fr.ag2rlamondiale.gitvc.exceptions.GitVCException;

public class GitEngine {

  private static final Logger LOGGER = Logger.getLogger(GitEngine.class);
  private Git currentGitInstance;
  private String currentRepository = "";

  /*
   * SINGLETON
   * 
   */

  private GitEngine() {}

  private static class SingletonHolder {
    private static final GitEngine instance = new GitEngine();

    // Ne doit JAMAIS etre appel� (M�me en test coverage)
    private SingletonHolder() {} 
  }

  public static GitEngine getInstance() {
    return SingletonHolder.instance;
  }

  private boolean launchCMD(String cmd) {
    Runtime rn = Runtime.getRuntime();
    try {
      Process pr = rn.exec("cmd /C " + cmd);
      pr.waitFor();
      if (pr.exitValue() != 0) {
        return false;
      }
    } catch (IOException | InterruptedException e) {
      LOGGER.error("Erreur lors de l'�x�cution d'une commande : " + cmd, e);
      return false;
    }
    return true;
  }

  /**
   * Cette methode va successivement : <br/>
   * Cloner le repository � <b>parameters.repositoryURI</b>, <br/>
   * Checkout la branche � <b>parameters.versionFrom</b>, <br/>
   * Merger la branche <b>parameters.versionTo</b> dans le working tree
   * 
   * @param parameters doit contenir repositoryURI, versionFrom et versionTo
   * @throws GitAPIException
   */
  public void init(VCParameters parameters) throws GitAPIException {
    try {
      cloneRepository(parameters.getRepositoryUri(),
          Utils.getFolderFromRepository(parameters.getRepositoryUri()));
      checkoutBranch(parameters.getVersionFrom());
      mergeWith(parameters.getVersionTo());
    } catch (GitAPIException e) {
      LOGGER.error("Erreur lors de l'initialisation du repository", e);
      throw new GitVCException(e.getMessage());
    }
  }

  public Set<String> diffWith(String toMerge) throws GitAPIException {
    if (currentGitInstance != null) {
      try {
        DiffCommand command = currentGitInstance.diff();
        AbstractTreeIterator newTreeParser =
            prepareTreeParser(currentGitInstance.getRepository(), "refs/remotes/origin/" + toMerge);
        List<DiffEntry> diff = command.setNewTree(newTreeParser).call();
        Set<String> diffFiles = new HashSet<>();
        for (DiffEntry entry : diff) {
          checkAndAddInDiff(diffFiles, entry);
        }
        return diffFiles;
      } catch (IOException e) {
        LOGGER.error("Erreur IO lors du diff ", e);
        throw new GitVCException(e.getMessage());
      }
    }
    return new HashSet<>();
  }

  private void checkAndAddInDiff(Set<String> diffFiles, DiffEntry entry) {
    if (!"/dev/null".equals(entry.getOldPath())) {
      diffFiles.add(entry.getOldPath());
    }
  }

  public FileInfos getFileInfos(String file, String versionFrom, String versionTo, String pathDest)
      throws GitAPIException {

    FileInfos infos = new FileInfos();
    infos.setName(file);
    infos.setDiffs(diffFile(versionFrom, versionTo, file, pathDest));
    infos.setLogs(logFile(file, pathDest));

    return infos;
  }

  public void eraseRepository() {
    if (currentGitInstance != null) {
      currentGitInstance.close();
      currentGitInstance = null;
      File repository = new File(Utils.getFolderFromRepository(currentRepository));
      if (repository.exists()) {
        CustomFileEngine.removeDirectory(repository);
      }
      currentRepository = "";
    }

  }

  public void resetHard() throws GitAPIException {
    if (currentGitInstance != null) {
      currentGitInstance.reset().setMode(ResetType.HARD).call();
    }
  }

  void cloneRepository(String repository, String targetFolder) throws GitAPIException {
    if (currentGitInstance != null) {
      eraseRepository();
    }
    currentGitInstance =
        Git.cloneRepository().setURI(repository).setDirectory(new File(targetFolder)).call();
    currentRepository = repository;
  }

  public void checkoutBranch(String branch) throws GitAPIException {
    if (currentGitInstance != null) {
      List<Ref> refs = currentGitInstance.branchList().call();
      boolean findedInLocal = false;
      int cpt = 0;
      while (!findedInLocal && cpt < refs.size()) {
        if (refs.get(cpt).getName().contains(branch)) {
          findedInLocal = true;
        } else {
          cpt++;
        }
      }
      if (findedInLocal) {
        currentGitInstance.checkout().setName(branch).call();
      } else {
        CheckoutCommand command = currentGitInstance.checkout().setCreateBranch(true)
            .setName(branch).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            .setStartPoint("origin/" + branch);
        try {
          command.call();
        } catch (NullPointerException e) {
          LOGGER.error("Null pointer � l'appel de l'API Git ", e);
          throw new GitVCException("R�f�rence " + branch + " nulle : " + e.getMessage());
        }
      }
    }
  }

  void mergeWith(String toMerge) throws GitAPIException {
    if (currentGitInstance != null) {
      MergeCommand command = currentGitInstance.merge();
      String refName = "refs/remotes/origin/" + toMerge;
      try {
        command.include(currentGitInstance.getRepository().exactRef(refName));
      } catch (IOException e) {
        LOGGER.error("Erreur IO lors du merge ", e);
        throw new GitVCException(e.getMessage());
      }
      command.setCommit(false);
      try {
        command.call();
      } catch (NullPointerException e) {
        LOGGER.error("Null pointer � l'appel de l'API Git ", e);
        throw new GitVCException("R�f�rence " + toMerge + " nulle : " + e.getMessage());
      }
    }
  }
  
  void mergeCMD(String toMerge){
	  if(currentGitInstance != null){
		  String directory = currentGitInstance.getRepository().getDirectory().getAbsolutePath()
		          .replaceAll("\\.git", "");
		  String cmd = "cd " + directory + "& git merge " + toMerge + "--no-edit --strategy-option=theirs";
		  if(launchCMD(cmd)){
			  
		  }
	  }
  }

  List<String> logFile(String file, String pathDest) throws GitAPIException {

    if (currentGitInstance != null) {
      String directory = currentGitInstance.getRepository().getDirectory().getAbsolutePath()
          .replaceAll("\\.git", "");
      String cmd = "cd " + directory + "& git log -p " + file + " > " + pathDest + "gitlog.log";
      if (launchCMD(cmd)) {
        return CustomFileEngine.getLinesFromFile(pathDest + "gitlog.log");
      } else {
        LOGGER.error("Impossible de g�n�rer le git log --full-diff du fichier " + file);
      }
    }
    return new ArrayList<>();
  }

  List<String> diffFile(String versionFrom, String versionTo, String file, String pathDest) {
    if (currentGitInstance != null) {
      String directory = currentGitInstance.getRepository().getDirectory().getAbsolutePath()
          .replaceAll("\\.git", "");
      String cmd = "cd " + directory + "& git diff " + "remotes/origin/" + versionTo + ".."
          + versionFrom + " " + file + " > " + pathDest + "gitdiff.log";
      if (launchCMD(cmd)) {
        return CustomFileEngine.getLinesFromFile(pathDest + "gitdiff.log");
      } else {
        LOGGER.error("Impossible de g�n�rer le git diff du fichier " + file);
      }
    }

    return new ArrayList<>();
  }
  
  //� Tester, On peut mieux g�rer l'exception
  public Set<String> diffFilesFromParameters(VCParameters parameters) throws GitAPIException{
	  
	  GitEngine.getInstance().init(parameters);
	  Set<String> diffFiles = GitEngine.getInstance().diffWith(
				parameters.getVersionTo());
	  LOGGER.info(diffFiles.size() + " fichiers trouv�s lors du diff");
	  return diffFiles;
  }
  

  /*
   * UTILS
   */

  private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref)
      throws IOException {
    Ref head = repository.exactRef(ref);
    try (RevWalk walk = new RevWalk(repository)) {
      RevCommit commit = walk.parseCommit(head.getObjectId());
      RevTree tree = walk.parseTree(commit.getTree().getId());
      CanonicalTreeParser treeParser = new CanonicalTreeParser();
      try (ObjectReader reader = repository.newObjectReader()) {
        treeParser.reset(reader, tree.getId());
      }
      walk.dispose();
      return treeParser;
    }
  }

}
