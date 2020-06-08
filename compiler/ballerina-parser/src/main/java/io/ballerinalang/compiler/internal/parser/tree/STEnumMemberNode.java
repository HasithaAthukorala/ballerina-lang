/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerinalang.compiler.internal.parser.tree;

import io.ballerinalang.compiler.syntax.tree.EnumMemberNode;
import io.ballerinalang.compiler.syntax.tree.Node;
import io.ballerinalang.compiler.syntax.tree.NonTerminalNode;
import io.ballerinalang.compiler.syntax.tree.SyntaxKind;

import java.util.Collection;
import java.util.Collections;

/**
 * This is a generated internal syntax tree node.
 *
 * @since 2.0.0
 */
public class STEnumMemberNode extends STNode {
    public final STNode metadata;
    public final STNode identifier;
    public final STNode equalToken;
    public final STNode constExprNode;

    STEnumMemberNode(
            STNode metadata,
            STNode identifier,
            STNode equalToken,
            STNode constExprNode) {
        this(
                metadata,
                identifier,
                equalToken,
                constExprNode,
                Collections.emptyList());
    }

    STEnumMemberNode(
            STNode metadata,
            STNode identifier,
            STNode equalToken,
            STNode constExprNode,
            Collection<STNodeDiagnostic> diagnostics) {
        super(SyntaxKind.ENUM_MEMBER, diagnostics);
        this.metadata = metadata;
        this.identifier = identifier;
        this.equalToken = equalToken;
        this.constExprNode = constExprNode;

        addChildren(
                metadata,
                identifier,
                equalToken,
                constExprNode);
    }

    public STNode modifyWith(Collection<STNodeDiagnostic> diagnostics) {
        return new STEnumMemberNode(
                this.metadata,
                this.identifier,
                this.equalToken,
                this.constExprNode,
                diagnostics);
    }

    public STEnumMemberNode modify(
            STNode metadata,
            STNode identifier,
            STNode equalToken,
            STNode constExprNode) {
        if (checkForReferenceEquality(
                metadata,
                identifier,
                equalToken,
                constExprNode)) {
            return this;
        }

        return new STEnumMemberNode(
                metadata,
                identifier,
                equalToken,
                constExprNode);
    }

    public Node createFacade(int position, NonTerminalNode parent) {
        return new EnumMemberNode(this, position, parent);
    }

    @Override
    public void accept(STNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> T apply(STNodeTransformer<T> transformer) {
        return transformer.transform(this);
    }
}
