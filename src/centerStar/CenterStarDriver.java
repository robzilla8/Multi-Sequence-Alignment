package centerStar;

import java.util.ArrayList;
import java.util.Arrays;
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

		// get the sum of the scores and min index
		String minScoringString = "";
		int minScore = Integer.MAX_VALUE;
		HashMap<String, Integer> sumOfScores = new HashMap<String, Integer>();
		for (String s1 : compareStrings) {
			int score = 0;
			for (String s2 : compareStrings) {
				if (!s1.equals(s2)) {
					score += scoreMap.get(StringHashMagic(s1, s2));
				}
			}
			if (score < minScore) {
				minScore = score;
				minScoringString = s1;
			}
			sumOfScores.put(s1, score);
		}

		// Test print sum of scores
		for (String s : compareStrings) {
			System.out.printf("String: %s%n", s);
			System.out.printf("Sum of scores: %d%n", sumOfScores.get(s));
		}
		System.out.printf("%n%nMin scoring string: \"%s\"%nScore: %d%n", minScoringString, sumOfScores.get(minScoringString));
		System.out.printf("----Alignments of min scoring string----%n");
		for (String s : compareStrings) {
			if (!s.equals(minScoringString)) {
				System.out.printf("	Distance: %d%n", scoreMap.get(StringHashMagic(minScoringString, s)));
				System.out.printf("	Result String 1 (s%d): %s%n", compareStrings.indexOf(resultMap.get(StringHashMagic(minScoringString, s))[0].replace("-","")) + 1, resultMap.get(StringHashMagic(minScoringString, s))[0]);
				System.out.printf("	Result String 2 (s%d): %s%n", compareStrings.indexOf(resultMap.get(StringHashMagic(minScoringString, s))[1].replace("-","")) + 1, resultMap.get(StringHashMagic(minScoringString, s))[1]);
			}
		}
		System.out.println();
		System.out.println();
		System.out.println("---Multi Sequence Alignment----");
		// String Buffer containing the multi sequence alignments
		ArrayList<StringBuffer> msa = new ArrayList<StringBuffer>();
		// String buffer containing the min scoring string
		StringBuffer minSequenceAlignment = new StringBuffer(minScoringString);
		// Array list containing the gaps that will be added to min scoring string
		ArrayList<Integer> minStringGapIndeces = new ArrayList<Integer>();
		for (String s : compareStrings) {
			if (!s.equals(minScoringString)) {
				String modifiedMin = "";
				StringBuffer modifiedCompare = new StringBuffer();
				// Figure out which string is the gapified and which is the min from our map
				if (isGapifiedString(minScoringString, resultMap.get(StringHashMagic(minScoringString, s))[0])) {
					modifiedMin = resultMap.get(StringHashMagic(minScoringString, s))[0];
					modifiedCompare.append(resultMap.get(StringHashMagic(minScoringString, s))[1]);
				} else {
					modifiedMin = resultMap.get(StringHashMagic(minScoringString, s))[1];
					modifiedCompare.append(resultMap.get(StringHashMagic(minScoringString, s))[0]);
				}

				// Check to see if the modified min has any gaps and record what index they are at
				ArrayList<Integer> gapIndeces = new ArrayList<Integer>();
				for (int i = 0; i < modifiedMin.length(); i++) {
					if (modifiedMin.charAt(i) == '-') {
						gapIndeces.add(i);
						if (!minStringGapIndeces.contains(i)) {
							minStringGapIndeces.add(i);
							// add gaps to the other sequences too
							for (StringBuffer sb : msa) {
								sb.insert(i, '-');
							}
						}
					}
				}

				// Add gaps to modifiedCompare based on gaps that were added to modified min in previous trials
				int offset = 0;
				for (int gapsToAdd : minStringGapIndeces) {
					// don't want to add gaps based on the current result of comparing center string to s
					if (!gapIndeces.contains(gapsToAdd)) {
						if (gapsToAdd + offset >= modifiedCompare.length() - 1) {
							modifiedCompare.append('-');
						} else {
							modifiedCompare.insert(gapsToAdd + offset, '-');
						}
						offset++;
					}
				}
				msa.add(modifiedCompare);
			}
		}

		// Handle the min string
		int[] sortedMinStringGapIndeces = new int[minStringGapIndeces.size()];
		for (int i = 0; i  < minStringGapIndeces.size(); i++) {
			sortedMinStringGapIndeces[i] = minStringGapIndeces.get(i);
		}
		Arrays.sort(sortedMinStringGapIndeces);
		int offset = 0;
		for (int gap : sortedMinStringGapIndeces) {
			if (gap + offset >= minSequenceAlignment.length() - 1) {
				minSequenceAlignment.append('-');
			} else {
				minSequenceAlignment.insert(gap + offset, '-');
			}
			offset++;
		}
		msa.add(minSequenceAlignment);

		// Can we print? Will it work? Find out next time on Dragon Ball Z!
		for (StringBuffer sb : msa) {
			StringBuffer indexString = new StringBuffer(String.format("s(%d):", compareStrings.indexOf(sb.toString().replace("-", "")) + 1));
			while (indexString.length() < 8) {
				indexString.append(' ');
			}
			System.out.printf("%s%s | Length: %d%n",indexString.toString(), sb.toString(), sb.toString().length());
		}
		// Next time on Dragonball Z...It works!
	}

	/**
	 * Check to see if a sequence is the same as another sequence with gaps in it
	 * @param original the original sequence to check
	 * @param compare a sequence containing gaps represented with "-"
	 * @return true if the strings are equal after removing the gaps in compare, false otherwise
	 * 
	 * EG
	 * "ABC"=original vs "A-B--C"=compare would return true
	 */
	private static boolean isGapifiedString(String original, String compare) {
		return original.equals(compare.replace("-", ""));
	}

	private static String StringHashMagic(String s1, String s2) {
		if (s2.compareTo(s1) < 0) {
			return s1+s2;
		} else {
			return s2+s1;
		}
	}

}
