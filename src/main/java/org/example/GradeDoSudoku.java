package org.example;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GradeDoSudoku {

    public final int tamanhoMatriz = 9;
    public final int tamanhoGrade = (int) Math.pow(tamanhoMatriz, 2);

    // Índices
    public int[] linhas;
    public int[] colunas;

    // Informações referentes à tupla de (linha, coluna)
    public int[] blocos;
    public int[] valor;

    public GradeDoSudoku() {

        linhas = new int[tamanhoGrade];
        colunas = new int[tamanhoGrade];
        blocos = new int[tamanhoGrade];
        valor = new int[tamanhoGrade];

        int indiceDaGrade = 0;

        for (int linha = 0; linha < tamanhoMatriz; linha++) {
            for (int coluna = 0; coluna < tamanhoMatriz; coluna++) {
                linhas[indiceDaGrade] = linha;
                colunas[indiceDaGrade] = coluna;
                blocos[indiceDaGrade] = getBlocoDaCelula(linha, coluna);

                indiceDaGrade++;
            }
        }
    }

    private int getBlocoDaCelula(int linha, int coluna) {
        // Quantidade de blocos por retas. Por exemplo, para um jogo de 81 células,
        // temos 3 blocos horizontais e 3 blocos verticais, totalizando 9.
        int quantidadeDeBlocos = (int) Math.sqrt(this.tamanhoMatriz);

        int indiceHorizontal = Math.floorDiv(linha, quantidadeDeBlocos);
        int indiceVertical = Math.floorDiv(coluna, quantidadeDeBlocos);

        return indiceVertical + (indiceHorizontal * quantidadeDeBlocos);
    }

    private IntStream filtrarAtributo(int[] atributo, int filtroDoAtributo) {
        return IntStream.range(0, atributo.length).filter(idx -> atributo[idx] == filtroDoAtributo);
    }

    public IntStream getLinhaFiltrada(int filtroDeLinha) {
        return filtrarAtributo(this.linhas, filtroDeLinha);
    }

    public IntStream getColunaFiltrada(int filtroDeColuna) {
        return filtrarAtributo(this.colunas, filtroDeColuna);
    }

    public IntStream getBlocoFiltrado(int filtroDeBloco) {
        return filtrarAtributo(this.blocos, filtroDeBloco);
    }

    public int[] getValoresDaLinha(int filtroDeLinha) {
        return getLinhaFiltrada(filtroDeLinha).map(idx -> valor[idx]).toArray();
    }

    public int[] getValoresDaColuna(int filtroDeColuna) {
        return getColunaFiltrada(filtroDeColuna).map(idx -> valor[idx]).toArray();
    }

    public int[] getValoresDoBloco(int filtroDeBloco) {
        return getBlocoFiltrado(filtroDeBloco).map(idx -> valor[idx]).toArray();
    }

    public void imprimirMatriz() {
        for (int linha = 0; linha < this.tamanhoMatriz; linha++) {
            System.out.println(Arrays.toString(getValoresDaLinha(linha)));
        }
    }
}
