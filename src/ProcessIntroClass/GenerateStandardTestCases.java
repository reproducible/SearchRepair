package ProcessIntroClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import test.Configuration;
import Library.Utility;

public class GenerateStandardTestCases {

	private String introPath;
	private String outputFolderPath;

	protected static Logger logger = Logger
			.getLogger(GenerateStandardTestCases.class);

	public GenerateStandardTestCases() {
		this.introPath = Configuration.introclassPath;

		this.outputFolderPath = Configuration.outputPath;
		new File(outputFolderPath).mkdir();
		this.list = new ArrayList<String>();
	}

	private List<String> list;

	public void printFailed() {
		logger.error(list);
	}

	public void generate() {
		for (String program : Configuration.programs) {
			Path progPath = Paths.get(introPath + "/" + program);
			if (Files.exists(progPath)) {
				generate(program);
			} else {
				logger.warn("Configured to process " + program + " but directory not found in " + introPath + ", skipping.");
			}
		}
	}

	private void generate(String program) {
		logger.info("initializing output and analysis folders for " + program);
		String thisOutputFolder = outputFolderPath + "/" + program;
		Path outputPath = Paths.get(thisOutputFolder);
		if(!Files.exists(outputPath)) {
			if(!outputPath.toFile().mkdir()) {
				logger.error("Unable to make output folder " + outputPath + ", skipping!");
				return;
			}
		}

		int depth = 0;
		Path programPath = Paths.get(introPath + "/" + program);
		File file = programPath.toFile();
		List<File> queue = new ArrayList<File>();
		queue.add(file);
		while (!queue.isEmpty() && depth != 2) {
			List<File> list = new ArrayList<File>();
			for (int i = 0; i < queue.size(); i++) {
				File temp = queue.get(i);
				if (temp.getName().equals("tests"))
					continue;
				if (temp.isDirectory()) {
					for (File f : temp.listFiles()) {
						if (f.isDirectory())
							list.add(f);
					}
				}
			}
			queue = list;
			depth++;
		}
		if (depth != 2)
			return;

		int count = 0;
		for (File variant : queue) {
			Path caseFolderPath = Paths.get(thisOutputFolder + "/" + count++ + "/");
			File caseFolder = caseFolderPath.toFile();
			if(!Files.exists(caseFolderPath)) {
				if(!caseFolder.mkdir()) {
					logger.error("Unable to make case output folder " + caseFolderPath + ", skipping");
					return;
				}
			}
			if(!init(program, variant.toPath(), caseFolderPath)) {
				logger.error("Error in initialization for " + caseFolderPath);
				return;
			}
		}


	}

	private boolean init(String program, Path variantPath, Path outputPath) {
		Path variantProgramSourcePath = Paths.get(variantPath + "/" + program + ".c");
		// copy program.C
		try {
			Files.copy(variantProgramSourcePath, outputPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (DirectoryNotEmptyException e) { // this is fine
			
		} catch (IOException e) {
			logger.error("Failed to copy variant source code in " + variantPath.toString() + " to output folder " + outputPath.toString() + "; skipping!");
			return false; 
		}
		
		Utility.writeTOFile(outputPath + "/original", variantProgramSourcePath.toString());
		if(!initializeTesting(program, variantPath, outputPath)) {
			logger.error("Failed to initialize testing for " + variantPath.toString() + "; skipping!");
			return false;		
			}
		getOtherTechInfo(variantPath, outputPath);
		return true;
	}
	
	private boolean initializeTesting(String program, Path variantPath, Path outputPath) {
		for(String whichTests : new String[]{"whitebox","blackbox"} ) {
			// create output folders
			for(String type : new String[]{"positive", "negative"} ) {
				Path testOutputPath = Paths.get(outputPath.toString() + "/" + whichTests + "/" + type);
				if(!Files.exists(testOutputPath)) {
					if(!testOutputPath.toFile().mkdirs()) {
						logger.error("Unable to make test output folder" + testOutputPath + ", skipping the rest of test generation for this variant.");
						return false;
					}
				}
			}

			// compile test executable for this variant
			String testingExe = "./" + program;
			Path testExePath = Paths.get(outputPath.toString() + "/./" + program);
			if(Files.exists(testExePath)) {
				testExePath.toFile().delete();
			}

			String gccCmd = "gcc -o " + testExePath.toString() + " " + outputPath + "/" + program + ".c";
			String s = Utility.runCProgram(gccCmd);
			if (s.equals("failed")) {
				return false;
			}

			// run each test in each suite; if it passes, it's a positive test, and if it fails, it's a negative test.
			Path testSuitePath = Paths.get(variantPath.toString() + "/../../tests/" + whichTests).normalize();
			File testSuiteFile = testSuitePath.toFile();
			// FIXME: refactor this a little bit, but maintain functionality that controls test/fail status.
			for (File file : testSuiteFile.listFiles()) {
				String testCandidate = file.getAbsolutePath();
				if (testCandidate.endsWith(".in")) {
					String input = Utility.getStringFromFile1(testCandidate);
					String outPath = testCandidate.substring(0, testCandidate.length() - 3) + ".out";
					String runOutput = Utility.runCProgramWithInput(testExePath.toString(),
							input);
					String tempOuputFile = "./tempFolder/test.out";
					Utility.writeTOFile(tempOuputFile, runOutput);
					String testStatus = Utility.runCProgramWithPythonCommand(testExePath.toString(),
							tempOuputFile, testCandidate, outPath, program).trim();
					if (testStatus.equals("Test passed.")) {
						String index = testCandidate.substring(testCandidate.lastIndexOf('/') + 1,
								testCandidate.lastIndexOf('.'));
						Utility.copy(testCandidate, outputPath.toString() + "/" + whichTests + "/positive/"
								+ index + ".in");
						Utility.copy(outPath, outputPath.toString() + "/" + whichTests + "/positive/"
								+ index + ".out");
					} else {
						String index = testCandidate.substring(testCandidate.lastIndexOf('/') + 1,
								testCandidate.lastIndexOf('.'));
						Utility.copy(testCandidate, outputPath.toString() + "/" + whichTests + "/negative/"
								+ index + ".in");
						Utility.copy(outPath, outputPath.toString() + "/" + whichTests + "/negative/"
								+ index + ".out");
					}
				}
			}
		}
		return true;

	}

	// FIXME: this is probably very inefficient, consider fixing when other core functionality is addressed
	private void getOtherTechInfo(Path inputFolder, Path outputFolder) {
		new File(outputFolder + "/repair").mkdir();
    		for (File file : inputFolder.toFile().listFiles()) {
			String name = file.getName();
			if (name.endsWith("log") && name.startsWith("gp")) {
				String fileString = Utility.getStringFromFile(file
						.getAbsolutePath());
				if (fileString.contains("Repair Found")
						|| fileString.contains("repair found")) {
					if (name.contains("wb")) {
						Utility.writeTOFile(outputFolder + "/repair/gp-wb",
								"success");
					} else if (name.contains("bb")) {
						Utility.writeTOFile(outputFolder + "/repair/gp-bb",
								"success");
					}
				}
			} else if (name.endsWith("log") && name.startsWith("ae")) {
				String fileString = Utility.getStringFromFile(file
						.getAbsolutePath());
				if (fileString.contains("Repair Found")
						|| fileString.contains("repair found")) {
					if (name.contains("wb")) {
						Utility.writeTOFile(outputFolder + "/repair/ae-wb",
								"success");
					} else if (name.contains("bb")) {
						Utility.writeTOFile(outputFolder + "/repair/ae-bb",
								"success");
					}
				}
			} else if (name.endsWith("log") && name.startsWith("tsp")) {
				String fileString = Utility.getStringFromFile(file
						.getAbsolutePath());
				if (fileString.contains("Repair Found")
						|| fileString.contains("repair found")) {
					if (name.contains("-wb-")) {
						Utility.writeTOFile(outputFolder + "/repair/tsp-wb",
								"success");
					} else if (name.contains("-bb-")) {
						Utility.writeTOFile(outputFolder + "/repair/tsp-bb",
								"success");
					}
				}
			}
		}

	}

// FIXME: consider adding this back in for testing later
//	public static void main(String[] args) {
//		if (args.length > 1)
//			Configuration.configure(args[1]);
//		GenerateStandardTestCases test = new GenerateStandardTestCases();
//		test.generate();
//		test.printFailed();
//	}

}
