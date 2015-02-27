import java.util.Random;
import java.util.Arrays;
import java.awt.Color;
public class CellSim{


	public static void main(String[] args){
		// Create a n x n 2-D character array representing the tissue sample
		// You can pick n
		// Write your code to test your methods here

		System.out.println("Enter an integer n to determine the dimensions of the grid: ");
		int n = IO.readInt();
		while(n <= 1){
			System.out.println("Enter an integer n to determine the dimensions of the grid: ");
			n = IO.readInt();
		}

		CellSimGUI cellSimGUI = new CellSimGUI(n, 75);

		System.out.println("Enter the threshold: ");
		int threshold = IO.readInt();
		while(threshold < 0 || threshold > 100){
			System.out.println("Enter the threshold: ");
			threshold = IO.readInt();
		}
		System.out.println("Enter the maximum rounds: ");
		int maxRounds = IO.readInt();
		while(maxRounds <= 0){
			System.out.println("Enter the maximum rounds: ");
			maxRounds = IO.readInt();
		}
		System.out.println("Enter the frequency: ");
		int frequency = IO.readInt();
		while(frequency <=0 || frequency > maxRounds){
			System.out.println("Enter the frequency: ");
			frequency = IO.readInt();
		}
		System.out.println("Enter percent of X agents as a percentage without the \"%\" sign: ");
		int percentX = IO.readInt();
		while(percentX < 0 || percentX > 100){
			System.out.println("Enter percent of X agents as a percentage without the \"%\" sign: ");
			percentX = IO.readInt();
		}
		System.out.println("Enter percent blank cells as a percentage without the \"%\" sign: ");
		int percentBlank = IO.readInt();
		while(percentBlank < 0 || percentBlank > 100){
			System.out.println("Enter percent blank cells as a percentage without the \"%\" sign: ");
			percentBlank = IO.readInt();
		}

		char[][] tissue = new char[n][n];
		assignCellTypes(tissue, percentBlank, percentX);
		char[][] initialTissue = new char[tissue.length][tissue[0].length];
		for(int i = 0; i < initialTissue.length; i++){		//stores initial tissue
    		for(int j = 0; j < initialTissue[i].length; j++){
    			initialTissue[i][j] = tissue[i][j];
    		}
    	}

		int i;
		int numMoved = 0;
		for(i = 1; i <= maxRounds; i++){
			for(int j = 0; j < n; j++){
				for(int k = 0; k < n; k++){
					if(tissue[j][k] == 'X')
						cellSimGUI.setCell(j, k, Color.blue);
					if(tissue[j][k] == 'O')
						cellSimGUI.setCell(j, k, Color.green);
					if(tissue[j][k] == ' ')
						cellSimGUI.setCell(j, k, Color.red);
				}
			}
			if(boardSatisfied(tissue, threshold) == false){
				numMoved += moveAllUnsatisfied(tissue, threshold);
				if(i%frequency == 0){
					System.out.println("Round "+ i + ": ");
					printTissue(tissue);
				}
			}else if(boardSatisfied(tissue, threshold) == true){
				break;
			}
		}
		System.out.println("The initial tissue sample is: ");
		printTissue(initialTissue);
		System.out.println("The final tissue sample is: ");
		printTissue(tissue);
		
		if(boardSatisfied(tissue, threshold) == true)
			System.out.println("This board was satisfied in " + (i-1) + " round(s).");
		else if(boardSatisfied(tissue, threshold) == false){
			double agentSatisfied = 0;
			double totalAgents = 0;
			double percentSatisfied = 0;
			for(int k = 0; k < tissue.length; k++){
				for(int l = 0; l < tissue[k].length; l++){
					if(tissue[k][l] != ' '){
						totalAgents++;
					}
					if(isSatisfied(tissue, k, l, threshold) == true && tissue[k][l] != ' '){
						agentSatisfied++;
					}
				}
			}
			percentSatisfied = (agentSatisfied*100/totalAgents);
			System.out.println("This board was not satisfied in " + maxRounds + " round(s), but " + percentSatisfied + "% " + "of the agents in the tissue sample were satisfied.");
		}
		System.out.println("There was/were " + numMoved + " movement(s) that occurred in this simulation.");
	}
	
	/**
	* Given a tissue sample, prints the cell make up in grid form
	*
	* @param tissue a 2-D character array representing a tissue sample
	* 
	***/

	public static void printTissue(char[][] tissue){	//Prints any 2D array.
	    for(int i = 0; i < tissue.length; i++){
	        for(int j = 0; j < tissue[i].length; j++){
	            System.out.print(tissue[i][j] + "\t");
	        }
	        System.out.println();	//Prints new line
	    }
	}

	/**
	* Given a blank tissue sample, populate it with the correct cell makeup given the parameters. 
	* Cell type 'X' will be represented by the character 'X'
	* Cell type 'O' will be represented by the character 'O'
	* Vacant spaces will be represented by the character ' '
	*
	* Phase I: alternate X and O cells throughout, vacant cells at the "end" (50% credit)
	*		e.g.:	'X' 'O' 'X' 'O' 'X'
	*				'O' 'X' 'O' 'X' 'O'
	*				'X' 'O' 'X' 'O' 'X'
	*				' ' ' ' ' ' ' ' ' '
	*				' ' ' ' ' ' ' ' ' '
	*
	* Phase II: Random assignment of all cells (100% credit)
	*
	* @param tissue a 2-D character array that has been initialized
	* @param percentBlank the percentage of blank cells that should appear in the tissue
	* @param percentX Of the remaining cells, not blank, the percentage of X cells that should appear in the tissue. Round up if not a whole number
	*
	**/

	public static void assignCellTypes(char[][] tissue, int percentBlank, int percentX){	//Randomizes Xs, Os, and blanks
		//Your code goes here																//given parameters
		Random r = new Random();
	    int row = 0;
	    int col = 0;
	    int rRow = r.nextInt(tissue.length);
	    int rCol = r.nextInt(tissue.length);

	    int numCells = (tissue.length * tissue.length);
	    double numBlank = numCells * (percentBlank/100.0);		//Determins # that need to be blank based off percentBlank parameter
	    numBlank = Math.ceil(numBlank);
	    double numX = (numCells-numBlank) * (percentX/100.0);	//Determinds # that need to be Xs based off percentX parameter
	    numX = Math.ceil(numX);
	    double numO = numCells - (numBlank + numX);

	    int countX = 0;							
	    while (countX < numX){					//randomizes and counts Xs
	        rRow = r.nextInt(tissue.length);
	        rCol = r.nextInt(tissue.length);
	        if (tissue[rRow][rCol] == '\0'){
	            tissue[rRow][rCol] = 'X';
	            countX++;
	        } 
	    }

	    int countO = 0;
	    while (countO < numO){					//randomizes and counts Os
	        rRow = r.nextInt(tissue.length);
	        rCol = r.nextInt(tissue.length);
	        if (tissue[rRow][rCol] == '\0') {
	            tissue[rRow][rCol] = 'O';
	            countO++;
	        }
	    }

	    int countBlank = 0;						//randomizes and counts blanks
	    while (countBlank < numBlank){
	        rRow = r.nextInt(tissue.length);
	        rCol = r.nextInt(tissue.length);
	        if (tissue[rRow][rCol] == '\0'){
	            tissue[rRow][rCol] = ' ';
	            countBlank++;
	        }   
	    } 
	}
	/**
    * Given a tissue sample, and a (row,col) index into the array, determines if the agent at that location is satisfied.
    * Note: Blank cells are always satisfied (as there is no agent)
    *
    * @param tissue a 2-D character array that has been initialized
    * @param row the row index of the agent
    * @param col the col index of the agent
    * @param threshold the percentage of like agents that must surround the agent to be satisfied
    * @return boolean indicating if given agent is satisfied
    *
    **/

    public static boolean isSatisfied(char[][] tissue, int row, int col, int threshold){
    	int happy = 0;
    	int n = 0;

    	char element = tissue[row][col]; //element == X, O, blank

	    if(element == ' ')	//Blanks elements are always satisfied.
			return true;
    	for(int i = -1; i <= 1; i++){	//Goes through rows 
    		for(int j = -1; j <= 1; j++){	//Goes through columns
    			if(row+i == -1 || col+j == -1 || row+i > tissue.length-1 || col+j > tissue[0].length-1)	//Skips boarders
    				continue;
    			if(row == row+i && col == col+j)	//Skips the element
    				continue;
    			if(element == tissue[row+i][col+j])	
    				happy++;
    			if(tissue[row+i][col+j] != ' ')
    				n++;
    		}
    	}
		if((happy*100) >= (threshold*n))
			return true;
		return false;
	}

	// /**
 //    * Given a tissue sample, determines if all agents are satisfied.
 //    * Note: Blank cells are always satisfied (as there is no agent)
 //    *
 //    * @param tissue a 2-D character array that has been initialized
 //    * @return boolean indicating whether entire board has been satisfied (all agents)
 //    **/
    public static boolean boardSatisfied(char[][] tissue, int threshold){	
    	for(int i = 0; i < tissue.length; i++){		//Goes through rows
    		for(int j = 0; j < tissue[i].length; j++){		//Goes through columns
    			if(isSatisfied(tissue, i, j, threshold) == false){	//checks for element's satisfaction
    				return false;
    			}
    		}
    	}
    	return true;
    }


     /**
    * Given a tissue sample, move all unsatisfied agents to a vacant cell
    *
    * @param tissue a 2-D character array that has been initialized
    * @param threshold the percentage of like agents that must surround the agent to be satisfied
    * @return an integer representing how many cells were moved in this round
    **/
    public static int moveAllUnsatisfied(char[][] tissue, int threshold){
    	int count = 0;
    	boolean[][] tempArray = new boolean[tissue.length][tissue[0].length];

    	if(boardSatisfied(tissue, threshold) == true)
    		return count;

    	for(int i = 0; i < tissue.length; i++){
    		for(int j = 0; j < tissue[i].length; j++){
    			tempArray[i][j] = isSatisfied(tissue, i, j, threshold);
    		}
    	}
    	for(int k = 0; k < tempArray.length; k++){
    		for(int l = 0; l < tempArray[k].length; l++){
    			if(tempArray[k][l] == false){
    				count++;
    				moveTo(tissue, k, l);
    			}
    		}
    	}
    	return count;
    }

    public static void moveTo(char[][] tissue, int i, int j){    
	    for(int k = 0; k < tissue.length; k++){
	        for(int l = 0; l < tissue[k].length; l++){
	            if(tissue[k][l] == ' '){
	                tissue[k][l] = tissue[i][j];
	                tissue[i][j] = ' ';
	                return;
	            }
	        }
	    }
	}
	

}