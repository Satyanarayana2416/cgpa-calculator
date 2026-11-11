package com.example.cgpa.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class GradeScaleTest {

    @Test
    void mapsMarkBoundariesToPointsAndLetters() {
        assertThat(GradeScale.pointsFor(new BigDecimal("90"))).isEqualTo(10);
        assertThat(GradeScale.letterFor(new BigDecimal("90"))).isEqualTo("O");
        assertThat(GradeScale.pointsFor(new BigDecimal("80"))).isEqualTo(9);
        assertThat(GradeScale.letterFor(new BigDecimal("79.99"))).isEqualTo("A");
        assertThat(GradeScale.pointsFor(new BigDecimal("40"))).isEqualTo(4);
        assertThat(GradeScale.letterFor(new BigDecimal("39.99"))).isEqualTo("F");
    }
}