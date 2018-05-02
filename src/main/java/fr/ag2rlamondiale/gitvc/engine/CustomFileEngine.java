package fr.ag2rlamondiale.gitvc.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import fr.ag2rlamondiale.gitvc.referential.VCConstants;

public class CustomFileEngine {

	private static final Logger LOGGER = Logger.getLogger(CustomFileEngine.class);

	private CustomFileEngine() {}


	/**
	 * Renvoies une list de ligne du fichier 
	 * @param filePath
	 * @return repotGit;rel_1;dev
	 */
	public static List<String> getLinesFromFile(String filePath) {
		List<String> result = new ArrayList<>();
		try {
			result = Files.readAllLines(Paths.get(filePath), StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			LOGGER.error("Erreur de lecture du fichier " + filePath, e);
		}
		return result;
	}

	public static List<String> readCSVLine(String csvLine) {
		return new ArrayList<>(Arrays.asList(csvLine.split(";")));
	}

	public static void writeReportToFile(String reportCSV, String formattedDate) {
		BufferedWriter out = null;
		File f = new File(VCConstants.REPORT_FOLDER_NAME + "/Report_" + formattedDate + ".csv");
		try {
			out = new BufferedWriter(new FileWriter(f));
			out.write(reportCSV);
		} catch (IOException e) {
			LOGGER.error("Exception IO lors de l'écriture du rapport : ", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				LOGGER.error("Exception IO lors de la fermeture tu buffer : ", e);
			}
		}
	}

	public static void writeStringToFile(String string, String file) {
		BufferedWriter out = null;
		File f = new File(file);
		try {
			out = new BufferedWriter(new FileWriter(f));
			out.write(string);
		} catch (IOException e) {
			LOGGER.error("Exception IO lors de l'écriture du fichier : ", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				LOGGER.error("Exception IO lors de la fermeture tu buffer : ", e);
			}
		}
	}

	public static void removeDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (File aFile : files) {
					removeDirectory(aFile);
				}
			}
			dir.delete();
		} else {
			dir.delete();
		}
	}

	public static void cleanReportFolder() {
		removeDirectory(new File(VCConstants.REPORT_FOLDER_NAME));
	}

	//cest mieux fait et testé sur la session de Jeremy
	public static void copyFileFromPath(String path_from,String path_to) throws IOException{
		File toCopy = new File(path_from);
		File dest = new File(path_to);
		if (toCopy.exists()) {
			FileUtils.copyFile(toCopy, dest);
		}
	}


	public static String getFileEncodingFromPath(String path) throws IOException{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		String fileEncoding = "";
		try{
			fis = new FileInputStream(path);
			isr = new InputStreamReader(fis);

			fileEncoding = isr.getEncoding();

		}catch (IOException e){
			LOGGER.error("Erreur IO" + e.getMessage());
		}finally{
			try{
				if(fis != null){
					fis.close();
				}
				if(isr != null){
					isr.close();
				}
			}catch(IOException e){
				LOGGER.error("Erreu lors de la fermeture des buffers : " + e.getMessage());
			}
		}
		return fileEncoding;
	}

	/**
	 * Ne renvoies que les fichiers présent dans la liste et dans la branche
	 * Il faudra avoir checkout sur la branche en question avant d'appeler cette méthode
	 * @param files : liste de tous les fichiers
	 * @return la liste des fichier filtré
	 */
	public static Set<String> checkIfFileInBranches(Set<String> files){
		Set<String> res = new HashSet<String>();
		for(String file : files){
			File tmp = new File(file);
			if(tmp.exists()){
				res.add(file);
			}
		}
		return res;
	}


	/**
	 * Associe le chemin de chaque fichier avec son encodage de fichier
	 * @return HashMap<String chemin, String encodage>
	 * @throws IOException 
	 */
	public static Map<String,String> assoFileEncodWithPath(Set<String> files) throws IOException{
		Map<String,String> encodingMap = new HashMap<String,String>();
		for(String file : files){
			if(!Utils.isExceptionFile(file)){
				encodingMap.put(file, getFileEncodingFromPath(file));
			}
		}
		return encodingMap;
	}

}
