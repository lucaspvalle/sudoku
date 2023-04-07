package org.sudoku;

public class Main {

    public static void main(String[] args) {
        GradeDoSudoku gradeDoSudoku = new GradeDoSudoku();

        try {
            new GeradorDoSudoku(gradeDoSudoku);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        gradeDoSudoku.imprimirMatriz();
    }

}