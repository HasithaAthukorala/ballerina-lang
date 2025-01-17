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

package org.wso2.ballerinalang.compiler.tree.expressions;

import org.ballerinalang.model.tree.NodeKind;
import org.ballerinalang.model.tree.types.TypeNode;
import org.wso2.ballerinalang.compiler.tree.BLangClassDefinition;
import org.wso2.ballerinalang.compiler.tree.BLangNodeAnalyzer;
import org.wso2.ballerinalang.compiler.tree.BLangNodeTransformer;
import org.wso2.ballerinalang.compiler.tree.BLangNodeVisitor;
import org.wso2.ballerinalang.compiler.tree.types.BLangType;


/**
 * Represents the object-constructor-expr.
 *
 * @since 2.0.0
 */
public class BLangObjectConstructorExpression extends BLangExpression {

    // BLangNodes
    public BLangClassDefinition classNode;
    public BLangTypeInit typeInit;
    public BLangType referenceType;

    // Parser Flags and Data
    public boolean isClient;
    public boolean isService;

    public BLangObjectConstructorExpression() {
        super();
        this.isClient = false;
    }

    @Override
    public void accept(BLangNodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <T> void accept(BLangNodeAnalyzer<T> analyzer, T props) {
        analyzer.visit(this, props);
    }

    @Override
    public <T, R> R apply(BLangNodeTransformer<T, R> modifier, T props) {
        return modifier.transform(this, props);
    }

    /**
     * Returns the kind of this node.
     *
     * @return the kind of this node.
     */
    @Override
    public NodeKind getKind() {
        return NodeKind.OBJECT_CTOR_EXPRESSION;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (isClient) {
            sb.append("client ");
        }

        if (isService) {
            sb.append("service ");
        }

        sb.append("object ");
        if (referenceType != null && referenceType.getBType().name != null) {
            sb.append(referenceType.getBType().name.getValue());
        }
        sb.append(" {");
        sb.append(this.classNode.toString());
        sb.append("};\n");
        return sb.toString();
    }

    /**
     * Add a type reference.
     *
     * @param type Type that is referenced by this type.
     */
    public void addTypeReference(TypeNode type) {
        if (this.referenceType == null) {
            this.referenceType = (BLangType) type;
            this.classNode.addTypeReference(type);
            return;
        }
        throw new RuntimeException("object-constructor-expr can only have one type-reference");
    }
}
