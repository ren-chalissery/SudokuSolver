/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class implementing the grid for standard Sudoku. Extends SudokuGrid (hence
 * implements all abstract methods in that abstract class). You will need to
 * complete the implementation for this for task A and subsequently use it to
 * complete the other classes. See the comments in SudokuGrid to understand what
 * each overriden method is aiming to do (and hence what you should aim for in
 * your implementation).
 */
public class StdSudokuGrid extends SudokuGrid {

//	public int size = 0;
	public int[][] grid;
	public int[] symbols;

	public StdSudokuGrid() {
		super();

		// TODO: any necessary initialisation at the constructor
	} // end of StdSudokuGrid()

	/* ********************************************************* */

	@Override
	public void initGrid(String filename) throws FileNotFoundException, IOException {

		Scanner sc = new Scanner(new File(filename));

		// Get the size of the puzzle
		size = Integer.parseInt(sc.nextLine());
		grid = new int[size][size];

		// Get the list of symbols used
		String symbolsStrArr[] = sc.nextLine().split(" ");
		symbols = new int[symbolsStrArr.length];
		for (int i = 0; i < symbolsStrArr.length; i++) {
			symbols[i] = Integer.parseInt(symbolsStrArr[i]);
		}

		// Set the initial Values
		while (sc.hasNextLine()) {
			String lineContent = sc.nextLine();
			String[] arr = lineContent.split(" ");
			int row = Integer.parseInt(arr[0].split(",")[0]);
			int col = Integer.parseInt(arr[0].split(",")[1]);
			grid[row][col] = Integer.parseInt(arr[1]);
		}

		sc.close();
	} // end of initBoard()

	@Override
	public void outputGrid(String filename) throws FileNotFoundException, IOException {
		try {

			// Create file to output data
			File file = new File(filename);
			if (file.createNewFile()) {
				System.out.println("File created successfully!");
			}

			// Write to file
			FileWriter fileWriter = new FileWriter(filename);
			fileWriter.write(toString());
			fileWriter.close();

		} catch (IOException e) {
			System.out.println("IOException: Error in outputting to file!");
			e.printStackTrace();
		}
	} // end of outputBoard()

	@Override
	public String toString() {

		int gridLength = grid.length;

		// Create a string builder to display the grid
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < gridLength; i++) {
			String prefix = "";
			for (int j = 0; j < gridLength; j++) {
				sb.append(prefix);
				sb.append(grid[i][j]);
				prefix = ",";
			}
			sb.append('\n');
		}
		return sb.toString();
	} // end of toString()

	@Override
	public boolean validate() {
		for (int row = 0; row < size; row++) {
			for (int column = 0; column < size; column++) {
				int gridValue = grid[row][column];
				if (gridValue == 0 || !isValidSymbol(gridValue) || countInRow(row, gridValue) > 1
						|| countInColumn(column, gridValue) > 1 || countInBox(row, column, gridValue) > 1)
					return false;
			}
		}
		return true;
	} // end of validate()

	// Check if the symbol is invalid
	private boolean isValidSymbol(int symbol) {
		for (int n : symbols) {
			if (n == symbol)
				return true;
		}
		return false;
	}

	// Check if the symbol is duplicate in row
	private int countInRow(int row, int number) {
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (grid[row][i] == number)
				count++;
		}
		return count;
	}

	// Check if the symbol is duplicate in column.
	private int countInColumn(int col, int number) {
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (grid[i][col] == number)
				count++;
		}
		return count;
	}

	// Check if the number is invalid in the box
	private int countInBox(int row, int col, int number) {
		int boxSize = (int) Math.sqrt(size);
		int rowBox = row - row % boxSize;
		int columnBox = col - col % boxSize;
		int count = 0;
		for (int i = rowBox; i < rowBox + boxSize; i++) {
			for (int j = columnBox; j < columnBox + boxSize; j++) {
				if (grid[i][j] == number)
					count++;
			}

		}
		return count;
	}

	public boolean validateToInsert(int row, int col, int gridValue) {
		if (isValidSymbol(gridValue) && countInRow(row, gridValue) == 0 && countInColumn(col, gridValue) == 0
				&& countInBox(row, col, gridValue) == 0) {
			return true;
		}
		return false;
	}
} // end of class StdSudokuGrid
