/*
 * Copyright 2018 Hochschule Luzern - Informatik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitstorm.gameoflife;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * Tests for {@link org.bitstorm.gameoflife.Cell}.
 */
final class CellTest {

    /**
     * In order to have access to a logger instance while testing separate classes.
     */
    @BeforeAll
    static void init() {
        StandaloneGameOfLife.initLoggerSetup();
    }

    /**
     * Test method for
     * {@link org.bitstorm.gameoflife.Cell#equals(java.lang.Object)}.
     */
    @Test
    void testEquals() {
        EqualsVerifier.forClass(Cell.class).withIgnoredFields("neighbour", "log").verify();
    }

    /**
     * Test method for {@link org.bitstorm.gameoflife.Cell#toString()}.
     */
    @Test
    void testToString() {
        assertThat(new Cell(1, 1).toString()).isNotBlank();
    }
}
