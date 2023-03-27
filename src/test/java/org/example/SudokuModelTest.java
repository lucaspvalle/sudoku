package org.example;

import org.junit.jupiter.api.Test;

class SudokuModelTest {

    SudokuModel model = new SudokuModel();

    @Test
    public void verificaFactibilidade() {
        assert model.getStatusIsFeasible();
    }
}