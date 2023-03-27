package org.example;

import java.util.Arrays;

public class Sudoku {

    public final int tamanhoMatriz = 9;
    public final int tamanhoBloco = (int) Math.sqrt(tamanhoMatriz);

    // A matriz é composta pela chave <linha, coluna>.
    public int[][] matriz = new int[tamanhoMatriz][tamanhoMatriz];

    public void imprimirMatriz() {
        for (int[] linha : matriz) {
            System.out.println(Arrays.toString(linha));
        }
    }

}
