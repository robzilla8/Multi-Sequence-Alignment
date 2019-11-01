package centerStar;

import java.util.ArrayList;
import java.util.HashMap;

public class CenterStarDriver {

	public static void main(String[] args) {
		String file = "";
		int mismatchPenalty = 0;
		int inDelPenalty = 0;
		try {
			file = args[0];
			mismatchPenalty = Integer.parseInt(args[1]);
			mismatchPenalty = -Math.abs(mismatchPenalty); // force this to be negative for global alignment
			inDelPenalty = Integer.parseInt(args[2]);
			inDelPenalty = -Math.abs(inDelPenalty);	   // force this to be negative for global alignment
		} catch (Exception e) {
			e.printStackTrace();
			System.err.printf("%n%nError! Specify Command line arguments (file name, mismatch penalty, insertion/Deletion penalty");
			System.exit(1);
		}
		ArrayList<String> compareStrings = new FASTAReader(file, "in").reader();
		int numberOfStrings = compareStrings.size();
		HashMap<String, Integer> scoreMap = new HashMap<String, Integer>();
		HashMap<String, String[]> resultMap = new HashMap<String, String[]>();
		ArrayList<String> hashes = new ArrayList<String>();
		// Get alignments and scores for each pair of strings
		for (int i = 0; i < numberOfStrings; i++) {
			for (int j = i + 1; j < numberOfStrings; j++) {
				NeedlemanMatrix n = new NeedlemanMatrix(compareStrings.get(i), compareStrings.get(j), 0, mismatchPenalty, inDelPenalty);
				String[] results = n.execute();
				int distance = -n.getScore();
				String hash = StringHashMagic(compareStrings.get(i), compareStrings.get(j));
				scoreMap.put(hash, distance);
				resultMap.put(hash, results);
				hashes.add(hash);
			}
		}
		// Test print
		int counter = 1;
		for (String s : hashes) {
			System.out.printf("Iteration: %d%n", counter);
			System.out.printf("	Hash: %s%n", s);
			System.out.printf("	Distance: %d%n", scoreMap.get(s));
			System.out.printf("	Result String 1 (s%d): %s%n", compareStrings.indexOf(resultMap.get(s)[0].replace("-","")) + 1, resultMap.get(s)[0]);
			System.out.printf("	Result String 2 (s%d): %s%n", compareStrings.indexOf(resultMap.get(s)[1].replace("-","")) + 1, resultMap.get(s)[1]);
			counter++;
		}
		
		// get the sum of the scores
		HashMap<String, Integer> sumOfScores = new HashMap<String, Integer>();
		for (String s1 : compareStrings) {
			int score = 0;
			for (String s2 : compareStrings) {
				if (!s1.equals(s2)) {
					score += scoreMap.get(StringHashMagic(s1, s2));
				}
			}
			sumOfScores.put(s1, score);
		}
		
		// Test print sum of scores
		for (String s : compareStrings) {
			System.out.printf("String: %s%n", s);
			System.out.printf("Sum of scores: %d%n", sumOfScores.get(s));
		}
	}

	private static String StringHashMagic(String s1, String s2) {
		if (s2.compareTo(s1) < 0) {
			return s1+s2;
		} else {
			return s2+s1;
		}
	}

}
