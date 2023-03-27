package org.example;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;

public class SudokuModel {
    class SolutionPrinter extends CpSolverSolutionCallback {
        public SolutionPrinter(IntVar[][] varNumeroNaPosicao_In) {
            varNumeroNaPosicao = varNumeroNaPosicao_In;
        }

        @Override
        public void onSolutionCallback() {
            for (int i = 0; i < sudoku.tamanhoMatriz; i++) {
                for (int j = 0; j < sudoku.tamanhoMatriz; j++) {
                    sudoku.matriz[i][j] = (int) value(varNumeroNaPosicao[i][j]);
                }
            }
        }
    }

    public Sudoku sudoku = new Sudoku();

    private final CpModel model = new CpModel();
    private final CpSolverStatus status;

    private IntVar[][] varNumeroNaPosicao = new IntVar[sudoku.tamanhoMatriz][sudoku.tamanhoMatriz];

    public void criarVariaveisDePosicao() {
        /*
            A variável de decisão é composta da seguinte chave: <linha, coluna>.
            Dessa forma, se o número 9 estiver na linha 1 e coluna 3, a variável <1, 3> é igual a 9.
        */
        for (int i = 0; i < sudoku.tamanhoMatriz; i++) {
            for (int j = 0; j < sudoku.tamanhoMatriz; j++) {
                String nomeDaVariavel = String.format("posicao_{%d}{%d}", i, j);
                varNumeroNaPosicao[i][j] = model.newIntVar(1, 9, nomeDaVariavel);
            }
        }
    }

    public void criarRestricaoParaNumeroNaoRepetirEmSuasRetas() {
        /*
            Essa restrição visa garantir que um mesmo número
            não se repita ao longo das suas retas (seja linha, seja coluna).

            Neste sentido, se o número 6 for alocado na linha 1 e coluna 1,
            nenhum outro número 6 pode estar nas linhas 2, 3, 4, ..., 9,
            assim como nenhum outro número 6 pode estar nas colunas 2, 3, 4, ..., 9.
         */

        // Como as linhas e colunas têm o mesmo tamanho, vamos reaproveitar a iteração para ambos.
        // Sendo assim, i e j podem ser tanto linha quanto coluna, alternando-se
        for (int i = 0; i < sudoku.tamanhoMatriz; i++) {
            LinearExpr[] numerosNaLinha = new LinearExpr[sudoku.tamanhoMatriz];
            LinearExpr[] numerosNaColuna = new LinearExpr[sudoku.tamanhoMatriz];

            for (int j = 0; j < sudoku.tamanhoMatriz; j++) {
                numerosNaLinha[j] = LinearExpr.newBuilder().add(varNumeroNaPosicao[j][i]).build();
                numerosNaColuna[j] = LinearExpr.newBuilder().add(varNumeroNaPosicao[i][j]).build();
            }

            model.addAllDifferent(numerosNaLinha);
            model.addAllDifferent(numerosNaColuna);
        }
    }

    public void criarRestricaoParaNumeroNaoRepetirDentroDoBloco() {
        /*
            O jogo é dividido em nove blocos de 3x3.
            Essa restrição garante a regra de que, dentro de cada bloco, o número não pode se repetir.
         */

        int bloco_i = 0, bloco_j = 0;
        while (bloco_i + bloco_j < 2 * (sudoku.tamanhoBloco - 1)) {
            LinearExpr[] blocos = new LinearExpr[sudoku.tamanhoMatriz];

            int var = 0;
            for (int i = sudoku.tamanhoBloco * bloco_i; i < (1 + bloco_i) * sudoku.tamanhoBloco; i++) {
                for (int j = sudoku.tamanhoBloco * bloco_j; j < (1 + bloco_j) * sudoku.tamanhoBloco; j++) {
                    blocos[var] = LinearExpr.newBuilder().add(varNumeroNaPosicao[i][j]).build();
                    var++;
                }
            }

            model.addAllDifferent(blocos);

            if (bloco_j < sudoku.tamanhoBloco - 1) {
                bloco_j++;
            } else {
                bloco_i++;
            }
        }
    }

    public SudokuModel() {
        /*
            O Sudoku será gerado por um modelo matemático que buscará uma solução factível,
            isto é: a disposição dos números na matriz conforme as regras do jogo.

            Portanto, a variável de decisão contém a posição <i, j> do número na matriz,
            enquanto as restrições forçam o atendimento de apenas um número no seu bloco,
            na sua coluna e na sua linha...
         */
        Loader.loadNativeLibraries();

        // Variáveis
        criarVariaveisDePosicao();

        // Restrições
        criarRestricaoParaNumeroNaoRepetirEmSuasRetas();
        criarRestricaoParaNumeroNaoRepetirDentroDoBloco();

        // Execução!
        model.exportToFile("teste.txt");

        CpSolver solver = new CpSolver();
        SolutionPrinter cb = new SolutionPrinter(varNumeroNaPosicao);

        solver.solve(model);

        status = solver.solve(model, cb);

        if (getStatusIsFeasible()) {
            System.out.println("Solução obtida!");
            sudoku.imprimirMatriz();
        } else {
            System.out.println("Infactível!");
        }
    }

    public boolean getStatusIsFeasible() {
        return (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE);
    }
}
