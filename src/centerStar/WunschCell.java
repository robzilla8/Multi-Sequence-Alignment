package centerStar;

//TODO: Auto-generated Javadoc
/**
* The Class WunschCell. These cells build the matrix of the needleman wunsch algorithm and allow traversal
*/
public class WunschCell {
	
	/** The score. */
	private int score;
	// Is the optimal path towards the parent a match or mismatch?
	// represents going diagonal (up and left) on the matrix
	private boolean optimalMatch = false;
	// Is the optimal path towards the parent an insertion in S?
	// represents going up on the matrix
	private boolean optimalInsertion = false;
	// Is the optimal path towards the parent a deletion in T?
	// represents going left on the matrix
	private boolean optimalDeletion = false;
	
	private String cellInfo;
	
	// Pointer to the wunsch cell representing an insertion in S that leads to an optimal score for this cell
	private WunschCell insertionParent;
	// Pointer to the wunsch cell representing a deltion in T that leads to an optimal score for this cell
	private WunschCell deletionParent;
	// Pointer to the wunsch cell representing a match of mismatch in S & T that leads to an optimal score for this cell
	private WunschCell matchParent;
	
	public WunschCell() {
		
	}
	
	/**
	 * Gets the score.
	 *
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Update score.
	 *
	 * @param score the value to update the score to
	 */
	public void updateScore(int score) {
		this.score = score;
	}
	
	/**
	 * Adds the to score.
	 *
	 * @param addVector the value to add to score
	 */
	public void addToScore(int addVector) {
		score += addVector;
	}
	
	// Setter/getter methods for potential parents to each Wunsch cell
	public void addInsertionParent(WunschCell parent) {
		optimalInsertion = true;
		insertionParent = parent;
	}
	
	public WunschCell getInsertionParent() {
		return insertionParent;
	}
	
	public void addDeletionParent(WunschCell parent) {
		optimalDeletion = true;
		deletionParent = parent;
	}
	
	public WunschCell getDeletionParent() {
		return deletionParent;
	}
	
	public void addMatchParent(WunschCell parent) {
		optimalMatch = true;
		matchParent = parent;
	}
	
	public WunschCell getMatchParent() {
		return matchParent;
	}
	
	public boolean getInsertion() {
		return optimalInsertion;
	}
	
	public boolean getDeletion() {
		return optimalDeletion;
	}
	
	public boolean getMatch() {
		return optimalMatch;
	}
	
	public void setCellInfo(String info) {
		cellInfo = info;
	}
	
	public String getCellInfo() {
		return cellInfo;
	}

}