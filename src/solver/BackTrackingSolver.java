/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.StdSudokuGrid;
import grid.SudokuGrid;

/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver {
	// TODO: Add attributes as needed.

	public BackTrackingSolver() {
		// TODO: any initialisation you want to implement.
	} // end of BackTrackingSolver()

	@Override
	public boolean solve(SudokuGrid grid) {
		StdSudokuGrid stdSudokuGrid = (StdSudokuGrid) grid;

		int size = stdSudokuGrid.size;
		int[] validSymbols = stdSudokuGrid.symbols;
		int[][] tempGrid = stdSudokuGrid.grid;

		for (int row = 0; row < size; row++) {
			for (int col = 0; col < size; col++) {
				
				// Get symbol for empty cell
				if (tempGrid[row][col] == 0) {

					// Get the validSymbols
					for (int symbol : validSymbols) {
						if (stdSudokuGrid.validateToInsert(row, col, symbol)) {
							tempGrid[row][col] = symbol;
							if (solve(grid)) {
								return true;
							} else {
								tempGrid[row][col] = 0;
							}
						}
					}
					return false;
				}
			}
		}
		return true;
	} // end of solve()

} // end of class BackTrackingSolver()
