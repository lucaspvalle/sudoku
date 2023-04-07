package org.sudoku;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeradorDoSudoku {
    class SolutionPrinter extends CpSolverSolutionCallback {
        public SolutionPrinter(IntVar[] varNumeroNaPosicao_In) {
            varNumeroNaPosicao = varNumeroNaPosicao_In;
        }

        @Override
        public void onSolutionCallback() {
            for (int idx = 0; idx < varNumeroNaPosicao.length; idx++) {
                gradeDoSudoku.valor[idx] = (int) value(varNumeroNaPosicao[idx]);
            }
        }
    }

    public GradeDoSudoku gradeDoSudoku;
    private final boolean estadoFactivelDaOtimizacao;

    private IntVar[] varNumeroNaPosicao;

    public IntVar[] criarVariaveisDePosicao(CpModel model) {
        // A variável de posição guarda o número alocado (de 1 a 9) na célula.
        IntVar[] varNumeroNaPosicao = new IntVar[gradeDoSudoku.tamanhoGrade];

        for (int idx = 0; idx < gradeDoSudoku.tamanhoGrade; idx++) {
            varNumeroNaPosicao[idx] = model.newIntVar(1, 9, String.format("posicao_{%d}", idx));
        }

        return varNumeroNaPosicao;
    }

    public void adicionarVariaveisEmRestricao(CpModel model, IntStream streamDeCelulas) {
        model.addAllDifferent(
                streamDeCelulas.mapToObj(
                        idx -> LinearExpr.newBuilder().add(varNumeroNaPosicao[idx]).build()
                ).collect(Collectors.toList())
        );
    }

    public void criarRestricoesDeUnicidadeDoNumero(CpModel model) {
        /*
            Essa restrição visa garantir que um mesmo número
            não se repita ao longo das suas retas (seja linha, seja coluna).

            Neste sentido, se o número 6 for alocado na linha 1 e coluna 1,
            nenhum outro número 6 pode estar nas linhas 2, 3, 4, ..., 9,
            assim como nenhum outro número 6 pode estar nas colunas 2, 3, 4, ..., 9.

            Além disso, o jogo é dividido em nove blocos de 3x3.
            Essa restrição também garante a regra de que, dentro de cada bloco, o número não pode se repetir.
         */

        for (int idx = 0; idx < gradeDoSudoku.tamanhoMatriz; idx++) {
            adicionarVariaveisEmRestricao(model, gradeDoSudoku.getLinhaFiltrada(idx));
            adicionarVariaveisEmRestricao(model, gradeDoSudoku.getColunaFiltrada(idx));
            adicionarVariaveisEmRestricao(model, gradeDoSudoku.getBlocoFiltrado(idx));
        }
    }

    public boolean getStatusIsFeasible() {
        return estadoFactivelDaOtimizacao;
    }

    public GeradorDoSudoku(GradeDoSudoku gradeDoSudoku) throws Exception {
        /*
            O Sudoku será gerado por um modelo matemático que buscará uma solução factível,
            isto é: a disposição dos números na matriz conforme as regras do jogo.

            Portanto, a variável de decisão contém a posição <i, j> do número na matriz,
            enquanto as restrições forçam o atendimento de apenas um número no seu bloco,
            na sua coluna e na sua linha...
         */

        this.gradeDoSudoku = gradeDoSudoku;

        CpSolverStatus status;
        CpModel model = new CpModel();

        Loader.loadNativeLibraries();

        // Variáveis
        this.varNumeroNaPosicao = criarVariaveisDePosicao(model);

        // Restrições
        criarRestricoesDeUnicidadeDoNumero(model);

        // Execução!
        CpSolver solver = new CpSolver();
        SolutionPrinter cb = new SolutionPrinter(varNumeroNaPosicao);

        solver.solve(model);

        status = solver.solve(model, cb);
        estadoFactivelDaOtimizacao = (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE);

        if (!estadoFactivelDaOtimizacao) {
            throw new Exception("Otimização não obteve resultado!");
        }
    }
}
