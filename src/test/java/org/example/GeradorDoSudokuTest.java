package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class GeradorDoSudokuTest {

    GradeDoSudoku gradeDoSudoku;
    GeradorDoSudoku model;

    public GeradorDoSudokuTest() {
        this.gradeDoSudoku = new GradeDoSudoku();

        try {
            model = new GeradorDoSudoku(gradeDoSudoku);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void verificaFactibilidade() {
        assert model.getStatusIsFeasible();
    }

    private boolean verificaUnicidade(int[] sequencia) {
        int contagem = (int) Arrays.stream(sequencia).distinct().count();
        return contagem == sequencia.length;
    }

    @Test
    public void verificaUnicidadeDeNumerosEmLinhas() {
        for (int linha = 0; linha < gradeDoSudoku.tamanhoMatriz; linha++) {
            int[] valoresDaLinha = gradeDoSudoku.getValoresDaLinha(linha);
            assert verificaUnicidade(valoresDaLinha);
        }
    }

    @Test
    public void verificaUnicidadeDeNumerosEmColunas() {
        for (int coluna = 0; coluna < gradeDoSudoku.tamanhoMatriz; coluna++) {
            int[] valoresDaColuna = gradeDoSudoku.getValoresDaColuna(coluna);
            assert verificaUnicidade(valoresDaColuna);
        }
    }

    @Test
    public void verificaUnicidadeDeNumerosEmBlocos() {
        for (int bloco = 0; bloco < gradeDoSudoku.tamanhoMatriz; bloco++) {
            int[] valoresDoBloco = gradeDoSudoku.getValoresDoBloco(bloco);
            assert verificaUnicidade(valoresDoBloco);
        }
    }

}