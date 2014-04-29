/*************************************************************************
*
*  Immutable data type BoggleSolver that finds all valid words in a 
*  given boggle board using a client specified dictionary. 
*
*************************************************************************/
import java.util.HashSet;

public class BoggleSolver {
	private final Trie<Integer> gameDictionary = new Trie<Integer>();
	private HashSet<String> foundWords;
	private boolean[][] visited; 
	private int width;
	private int height;
	private BoggleBoard myBoard;
    
	/**
     * Initializes the data structure using the given array of strings as the dictionary.
     * The dictionary contains only the uppercase letters A through Z.
     * @param dictionary the dictionary
     */ 
    public BoggleSolver(String[] dictionary) {
    	int val = 0;
    	for (String s : dictionary) {
    		gameDictionary.put(s, val);
    		++val;
    	}
    }
    
    /**
     * Returns the set of all valid words in the given Boggle board, as an iterable.
     * @param board the boggle board
     * @return all valid words found in the board, as an iterable.
     */ 
    public Iterable<String> getAllValidWords(BoggleBoard board) {
    	width = board.cols(); 	
    	height = board.rows();  	
    	foundWords = new HashSet<String>();
    	myBoard = board;
    	solve();
    	return foundWords;
    }
    
    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * Throws IllegalArgumentException if the parameter is null.
     * @param word the word to score
     * @return the score of the word.
     */ 
    public int scoreOf(String word) {
		if (word == null) throw new IllegalArgumentException();
		if (word.length() <= 2 || !gameDictionary.keysThatMatch(word).iterator().hasNext()) {
			return 0;
		}
		switch (word.length()) {
			case 3:
			case 4:
				return 1;
			case 5:
				return 2;
			case 6:
				return 3;
			case 7:
				return 5;
			case 8:
			default:
				return 11;
		}
    }
    
    private void solve() {
    	visited = new boolean[height][width];
    	for (int row = 0; row < height; row++) {
    		for (int col = 0; col < width; col++) {
    			dfs("", row, col);
    		}
    	}
    }

    private void dfs(String prefix, int row, int col) {
    	if (row < 0 || col < 0 || row >= height || col >= width) return;
    	
        // can't visit a cell more than once
        if (visited[row][col]) return;
        
        if (myBoard.getLetter(row, col) == 'Q') prefix += "QU";
        else prefix += myBoard.getLetter(row, col);
        
        // if no word in the dictionary has this prefix then return
        if (!gameDictionary.keysWithPrefix(prefix)) return;
        
        // to avoid letter reuse
        visited[row][col] = true;
        
        Queue<String> matches = (Queue<String>) gameDictionary.keysThatMatch(prefix);
        
        // add matches to the Set of found words
        for (String s : matches) {
        	foundWords.add(s);
        }
        
        // explore all the neighbors
        for (int i = -1; i <= 1; i++) {
        	for (int j = -1; j <= 1; j++) {
        		dfs(prefix, row + i, col + j);
        	}
        }
        
        visited[row][col] = false;
    }
    
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        Stopwatch timer = new Stopwatch();
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        double time = timer.elapsedTime();
        StdOut.println("Score = " + score);
        StdOut.println("Time = " + time);
    }
}
