package fr.ag2rlamondiale.gitvc.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import fr.ag2rlamondiale.gitvc.dto.FileInfos;
import fr.ag2rlamondiale.gitvc.dto.ReportLine;
import fr.ag2rlamondiale.gitvc.dto.VCParameters;
import fr.ag2rlamondiale.gitvc.engine.CustomFileEngine;
import fr.ag2rlamondiale.gitvc.engine.GitEngine;
import fr.ag2rlamondiale.gitvc.engine.ReportEngine;
import fr.ag2rlamondiale.gitvc.engine.Utils;
import fr.ag2rlamondiale.gitvc.exceptions.GitVCException;
import fr.ag2rlamondiale.gitvc.referential.VCConstants;

public class GitVC {

	private static final Logger LOGGER = Logger.getLogger(GitVC.class);

	private final String propertyPath;
	private final ReportEngine reportEngine;

	public GitVC(String propPath) {
		propertyPath = propPath;
		reportEngine = new ReportEngine();
	}

	public void execute() {
		CustomFileEngine.cleanReportFolder();
		String reportCSV = ReportLine.getTitleLineCsv() + generateFullReportCSV();
		CustomFileEngine.writeReportToFile(reportCSV, Utils.getFormattedDate());
	}

	private String generateFullReportCSV() {
		String reportCSV = "";
		List<String> csvLines = CustomFileEngine.getLinesFromFile(propertyPath);
		for (String csvLine : csvLines) {
			VCParameters parameters = VCParameters.initFromCSVLine(csvLine);
			try {
				LOGGER.info("Lancement de la comparaison pour : " + parameters.toString());
				Set<String> diffFiles = GitEngine.getInstance().diffFilesFromParameters(parameters);
				
				reportCSV += generateReportForOneLine(diffFiles,parameters);
				reportCSV += generateReportEncodingFromDiffFiles(diffFiles,parameters);
				
				//suppression du repo
				LOGGER.info("Suppression du repository temporaire " + Utils.getFolderFromRepository(parameters.getRepositoryUri()));
				GitEngine.getInstance().eraseRepository();
				LOGGER.info("Fin de la comparaison pour : " + parameters.toString());
			} catch (GitAPIException | IOException e) {
				LOGGER.error(
						"Impossible d'éxécuter la comparaison pour ces paramètres : " + parameters.toString());
				LOGGER.error("Cause de l'erreur : ", e);
			}
		}
		return reportCSV;
	}
	
	
	private String generateReportEncodingFromDiffFiles(Set<String> diffFiles,VCParameters parameters) throws GitAPIException, IOException {
		String csvResult = "";
		Set<String> diffFileInBothBranches = CustomFileEngine.checkIfFileInBranches(diffFiles);
		
		GitEngine.getInstance().checkoutBranch(parameters.getVersionTo());
		Map<String,String> encodingMapTo = CustomFileEngine.assoFileEncodWithPath(diffFileInBothBranches);
		GitEngine.getInstance().checkoutBranch(parameters.getVersionFrom());
		Map<String,String> encodingMapFrom = CustomFileEngine.assoFileEncodWithPath(diffFileInBothBranches);
		csvResult += reportEngine.generateRepportEncodingCSV(encodingMapTo,encodingMapFrom);
		return csvResult;
	}
	
	//est testé et est placé au bon endroit sur la session de jeremy
	private String buildPath(VCParameters parameters,String repoFolder, String file){
		String pathDest = VCConstants.REPORT_FOLDER_NAME + File.separator 
				+ parameters.getReportDate() + File.separator + repoFolder + File.separator
				+ file.replaceAll("/", "-") + File.separator;
		pathDest = new File(pathDest).getAbsolutePath() + File.separator;
		return pathDest;
	}
	
	private String generateReportForOneLine(Set<String> diffFiles,VCParameters parameters)
			throws GitAPIException, IOException {
		String csvResult = "";
		String repoFolder = Utils.getFolderFromRepository(parameters.getRepositoryUri());
		LOGGER.info("Initialisation du repository temporaire " + repoFolder);
		
		for (String file : diffFiles) {
			if (!Utils.isExceptionFile(file)) {
				LOGGER.info("Recherche de non report de code sur le fichier : " + file);
				String pathDest = buildPath(parameters,repoFolder,file);
				
				new File(pathDest).mkdirs();
				FileInfos fileInfos = GitEngine.getInstance().getFileInfos(file,
						parameters.getVersionFrom(), parameters.getVersionTo(), pathDest);
				csvResult += reportEngine.generateReportCSV(repoFolder, fileInfos, pathDest);

				LOGGER.info("Copie de la version mergée " + parameters.getVersionFrom() + " du fichier");		
				CustomFileEngine.copyFileFromPath(repoFolder + File.separator + file,pathDest + "merged_" + parameters.getVersionFrom() + ".java");
			}
		}
		GitEngine.getInstance().resetHard();
		GitEngine.getInstance().checkoutBranch(parameters.getVersionTo());
		
		for (String file : diffFiles) {
			if (!Utils.isExceptionFile(file)) {
				String pathDest = buildPath(parameters,repoFolder,file);
				LOGGER.info("Copie de la version " + parameters.getVersionTo() + " du fichier");
				CustomFileEngine.copyFileFromPath(repoFolder + File.separator + file,pathDest + parameters.getVersionTo() + ".java");
			}
		}
		return csvResult;
	}

	/**
	 * Class main de GitVC permettant de lancer le traitement. Un paramètre est attendu, obligatoire,
	 * un path vers un fichier de propriété
	 * 
	 * Structure attendue du fichier : CSV Contenu attendu est une liste de :
	 * Repository;VersionFrom;VersionTo
	 * 
	 * @param args [0] path vers un fichier de propriété
	 * @throws Exception
	 */
	public static void main(String[] args) throws GitVCException {
		LOGGER.info("Appel à GitVC reçu. Lancement du script.");
		if (args.length == 0) {
			throw new GitVCException("Un paramètre attentu : path vers un fichier de propriétés");
		}
		new GitVC(args[0]).execute();
	}
}
