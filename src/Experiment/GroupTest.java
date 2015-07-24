package Experiment;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import search.ResultObject.ResultState;
import test.Configuration;

public class GroupTest {

	// FIXME: consider adding back in for testing when done refactoring
	//	public static void main(String[] args) {
	//		medianTest(false, 2);
	//		// smallestTest();
	//		// gradeTest();
	//		// checkSumTest();
	//		// syllablesTest();
	//	}

	public static void rerun(boolean wb, int repositoryType) {
		for(String program : Configuration.programs) {
			ESearchCase searcher = null;
			List<Path> list = new ArrayList<Path>();
			File file = new File(Configuration.outputPath + program);
			int size = file.listFiles().length;
			for (File root : file.listFiles()) {
				try {
					Path folder = Paths.get(Configuration.outputPath + program + "/" + root.getName());
					Path fileName = Paths.get(folder.toString() + "/" + program + ".c"); 
					if (repositoryType == 2) { // FIXME: make this not an integer because it's impossible to grok this way
						// OK beginning to figure it out: root is the variant number.
						// and I think he's appropriately leaving the future of this variant out, come to think of it
						int value = Integer.parseInt(root.getName());
						repositoryType = (value < size / 2) ? 3 : 4; 				
					}
					
					String transformArgs = "";
					boolean doTransform = false;
					switch(program) {
					case "checksum":
						searcher = new CheckSumSearchCase(program, folder,
								fileName, repositoryType);
						break;
					case "median":
					case "smallest":
						searcher =  new MedianSearchCase(program, folder,
								fileName, repositoryType);
						doTransform = true;

						break;
					case "grade":
						searcher = new GradeSearchCase(program, folder, fileName,
								repositoryType);
						doTransform = true;
						transformArgs = "--type grade";
						break;
					case "syllables":
						new SyllableSearchCase(program, folder,
								fileName, repositoryType);
					}

					searcher.transformAndInitRunDir(doTransform, transformArgs);
					searcher.initWbOrBB(wb);
					searcher.search(wb);
					searcher.recordResult(wb);
					if (searcher.getInfo().getResult().getState() == ResultState.SUCCESS) {
						list.add(folder);
					}
					for (Path s : list) {
						System.out.println(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


}
