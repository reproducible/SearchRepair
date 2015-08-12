package Experiment;

import java.util.Arrays;

import search.ResultObject.ResultState;

public class GradeSearchCase extends ESearchCase {

	public GradeSearchCase(String folder, String fileName, int repo) {
		super(folder, fileName, repo);
	}

	@Override
	public void search(boolean wb) {
		
		this.initWbOrBB(wb);
		
		
		
		
		if(this.getPositives().size() == 0) {
			this.getInfo().getResult().setState(ResultState.NOPOSITIVE);
			return;
		}
		if(this.getNegatives().size() == 0){
			this.getInfo().getResult().setState(ResultState.CORRECT);
			return;
		}
		
		int[] range = getBugLines();
		System.out.println(Arrays.toString(range));
		String prefix = this.getRunDir() + "/" + this.getFileName().substring(0, this.getFileName().lastIndexOf('.'));
		SearchCase instan = new SearchCase(prefix, this.getRepo());
		instan.setBuggy(range);
		instan.setNegatives(this.getNegatives());
		instan.setPositives(this.getPositives());
		instan.setVerifications(this.getValidationTests());
		instan.search();	
		this.setInfo(instan.getInfo());
	}

	
	
	

	public static void main(String[] args){
		GradeSearchCase instan = new GradeSearchCase("./bughunt/grade/120", "grade.c", 4);
		instan.transformAndInitRunDir(true, "--type grade");
		instan.initInputAndOutput();
//		instan.search(true);
//		instan.recordResult(true);
		instan.search(false);
		instan.recordResult(false);
	}

}