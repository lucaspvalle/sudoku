package org.sudoku;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;

import java.util.List;
import java.util.stream.Collectors;

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

        for (int indice = 0; indice < gradeDoSudoku.tamanhoGrade; indice++) {
            varNumeroNaPosicao[indice] = model.newIntVar(1, 9, String.format("posicao_{%d}", indice));
        }

        return varNumeroNaPosicao;
    }

    public void criarRestricaoParaNumeroNaoRepetirEmSuasRetas(CpModel model, IntVar[] varNumeroNaPosicao) {
        /*
            Essa restrição visa garantir que um mesmo número
            não se repita ao longo das suas retas (seja linha, seja coluna).

            Neste sentido, se o número 6 for alocado na linha 1 e coluna 1,
            nenhum outro número 6 pode estar nas linhas 2, 3, 4, ..., 9,
            assim como nenhum outro número 6 pode estar nas colunas 2, 3, 4, ..., 9.
         */

        for (int i = 0; i < gradeDoSudoku.tamanhoMatriz; i++) {
            List<LinearExpr> numerosNaLinha =
                    gradeDoSudoku.getLinhaFiltrada(i).mapToObj(
                            idx -> LinearExpr.newBuilder().add(varNumeroNaPosicao[idx]).build()
                    ).collect(Collectors.toList());

            List<LinearExpr> numerosNaColuna =
                    gradeDoSudoku.getColunaFiltrada(i).mapToObj(
                            idx -> LinearExpr.newBuilder().add(varNumeroNaPosicao[idx]).build()
                    ).collect(Collectors.toList());

            model.addAllDifferent(numerosNaLinha);
            model.addAllDifferent(numerosNaColuna);
        }
    }

    public void criarRestricaoParaNumeroNaoRepetirDentroDoBloco(CpModel model, IntVar[] varNumeroNaPosicao) {
        /*
            O jogo é dividido em nove blocos de 3x3.
            Essa restrição garante a regra de que, dentro de cada bloco, o número não pode se repetir.
         */

        for (int bloco = 0; bloco < gradeDoSudoku.tamanhoMatriz; bloco++) {
            List<LinearExpr> numerosNoBloco =
                    gradeDoSudoku.getBlocoFiltrado(bloco).mapToObj(
                            idx -> LinearExpr.newBuilder().add(varNumeroNaPosicao[idx]).build()
                    ).collect(Collectors.toList());

            model.addAllDifferent(numerosNoBloco);
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
        criarRestricaoParaNumeroNaoRepetirEmSuasRetas(model, varNumeroNaPosicao);
        criarRestricaoParaNumeroNaoRepetirDentroDoBloco(model, varNumeroNaPosicao);

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
