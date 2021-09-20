/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ballerina.semantic.api.test;

import io.ballerina.compiler.api.SemanticModel;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.semantic.api.test.util.SemanticAPITestUtils;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticProperty;
import io.ballerina.tools.diagnostics.DiagnosticPropertyKind;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.LineRange;
import org.testng.annotations.Test;

import java.util.List;

import static io.ballerina.compiler.api.symbols.TypeDescKind.NIL;
import static io.ballerina.compiler.api.symbols.TypeDescKind.UNION;
import static org.testng.Assert.assertEquals;

/**
 * Test cases for the APIs for getting the diagnostics of the semantic model.
 *
 * @since 2.0.0
 */
public class DiagnosticsTest {

    @Test
    public void testAllDiagnostics() {
        SemanticModel model = SemanticAPITestUtils.getDefaultModulesSemanticModel(
                "test-src/testerrorproject/");

        List<Diagnostic> diagnostics = model.diagnostics();
        Object[][] expErrs = getExpectedErrors();

        assertEquals(diagnostics.size(), expErrs.length);
        for (int i = 0; i < expErrs.length; i++) {
            assertDiagnostic(diagnostics.get(i), expErrs[i]);
        }
    }

    @Test
    public void testDiagnosticsInARange() {
        SemanticModel model = SemanticAPITestUtils.getDefaultModulesSemanticModel(
                "test-src/testerrorproject/");

        LineRange range = LineRange.from("type_checking_errors.bal", LinePosition.from(1, 0),
                                         LinePosition.from(18, 19));
        List<Diagnostic> diagnostics = model.diagnostics(range);

        assertEquals(diagnostics.size(), 1);
        assertDiagnostic(diagnostics.get(0), getExpectedErrors()[3]);
    }

    @Test
    public void testDiagnosticProperties() {
        SemanticModel model = SemanticAPITestUtils.getDefaultModulesSemanticModel(
                "test-src/testerrorproject/");

        LineRange range = LineRange.from("diagnostic_properties_check.bal", LinePosition.from(1, 0),
                LinePosition.from(17, 24));
        List<Diagnostic> diagnostics = model.diagnostics(range);

        assertEquals(diagnostics.size(), 3);
        assertDiagnosticProperties(diagnostics);
    }

    // Utils

    private Object[][] getExpectedErrors() {
        return new Object[][]{
                {"missing semicolon token", 18, 0},
                {"invalid token 'string'", 21, 8},
                {"missing identifier", 21, 15},
                {"incompatible types: expected 'int', found 'string'", 17, 12},
                {"incompatible types: 'int' cannot be cast to 'string'", 19, 15}
        };
    }

    private void assertDiagnostic(Diagnostic diagnostic, Object[] expected) {
        assertEquals(diagnostic.message(), expected[0]);
        assertEquals(diagnostic.location().lineRange().startLine().line(), expected[1]);
        assertEquals(diagnostic.location().lineRange().startLine().offset(), expected[2]);
    }

    private Object[][] getExpectedDiagnosticProperties() {
        return new Object[][] {
                {"[string]|record {| string arg2; |}", UNION},
                {"()", NIL},
        };
    }

    private void assertDiagnosticProperties(List<Diagnostic> diagnostics) {
        int index = 0;

        for (Diagnostic diagnostic : diagnostics) {

            List<DiagnosticProperty<?>> diagnosticProperties = diagnostic.properties();

            if (diagnosticProperties != null) {

                for (DiagnosticProperty<?> property : diagnosticProperties) {

                    // The diagnostic property value is an instance of TypeSymbol only if the property kind is SYMBOLIC.
                    if (property.kind() == DiagnosticPropertyKind.SYMBOLIC) {
                        TypeSymbol typeSymbol = (TypeSymbol) property.value();
                        assertEquals(typeSymbol.signature(), getExpectedDiagnosticProperties()[index][0]);
                        assertEquals(typeSymbol.typeKind(), getExpectedDiagnosticProperties()[index++][1]);
                    }

                }

            }

        }

    }
}
