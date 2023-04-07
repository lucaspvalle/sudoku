package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class GradeDoSudokuTest {

    GradeDoSudoku gradeDoSudoku = new GradeDoSudoku();

    @Test
    public void testFiltroDeLinhas() {
        int[] linhasFiltradas = gradeDoSudoku.getLinhaFiltrada(1).toArray();
        int[] linhasEsperadas = {9, 10, 11, 12, 13, 14, 15, 16, 17};

        assert Arrays.equals(linhasFiltradas, linhasEsperadas);
    }

    @Test
    public void testFiltroDeColunas() {
        int[] colunasFiltradas = gradeDoSudoku.getColunaFiltrada(3).toArray();
        int[] colunasEsperadas = {3, 12, 21, 30, 39, 48, 57, 66, 75};

        assert Arrays.equals(colunasFiltradas, colunasEsperadas);
    }

    @Test
    public void testFiltroDeBlocos() {
        int[] blocosFiltrados = gradeDoSudoku.getBlocoFiltrado(5).toArray();
        int[] blocosEsperados = {33, 34, 35, 42, 43, 44, 51, 52, 53};

        assert Arrays.equals(blocosFiltrados, blocosEsperados);
    }
}