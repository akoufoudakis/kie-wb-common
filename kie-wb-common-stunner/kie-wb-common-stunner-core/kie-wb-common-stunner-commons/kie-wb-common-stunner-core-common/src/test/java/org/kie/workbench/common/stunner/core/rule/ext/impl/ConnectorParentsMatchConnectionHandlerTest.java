/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.rule.ext.impl;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.AbstractGraphDefinitionTypesTest;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.kie.workbench.common.stunner.core.validation.Violation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectorParentsMatchConnectionHandlerTest extends AbstractGraphDefinitionTypesTest {

    public static final String DEF_EDGE_ID = "EdgeDef1";
    public static final String EDGE_UUID = "edge1";

    @Mock
    private RuleExtension ruleExtension;

    @Mock
    private GraphConnectionContext connectionContext;

    @Mock
    private Object connectorDef;

    private ConnectorParentsMatchConnectionHandler tested;
    private Edge connector;
    private Graph graph;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        this.graph = graphHandler.graph;
        this.connector = graphHandler.newEdge(EDGE_UUID,
                                              Optional.of(connectorDef));
        when(graphHandler.definitionAdapter.getId(eq(connectorDef))).thenReturn(DEF_EDGE_ID);
        when(connectionContext.getGraph()).thenReturn(graph);
        when(connectionContext.getConnector()).thenReturn(connector);
        when(ruleExtension.getId()).thenReturn(DEF_EDGE_ID);
        when(ruleExtension.getArguments()).thenReturn(new String[]{"violation1"});
        tested = new ConnectorParentsMatchConnectionHandler(graphHandler.definitionManager);
    }

    @Test
    public void testTypes() {
        assertEquals(ConnectorParentsMatchConnectionHandler.class,
                     tested.getExtensionType());
        assertEquals(GraphConnectionContext.class,
                     tested.getContextType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccepts() {
        when(connectionContext.getSource()).thenReturn(Optional.of(nodeA));
        when(connectionContext.getTarget()).thenReturn(Optional.of(nodeB));
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        assertTrue(tested.accepts(ruleExtension,
                                  connectionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotAccepts() {
        when(connectionContext.getSource()).thenReturn(Optional.of(nodeA));
        when(connectionContext.getTarget()).thenReturn(Optional.of(nodeB));
        when(ruleExtension.getId()).thenReturn("anyOtherId");
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        assertFalse(tested.accepts(ruleExtension,
                                  connectionContext));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFailEvaluateParentDefinition() {
        when(connectionContext.getSource()).thenReturn(Optional.of(nodeA));
        when(connectionContext.getTarget()).thenReturn(Optional.of(nodeC));
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          connectionContext);
        assertNotNull(violations);
        assertTrue(violations.violations(Violation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateParentDefinition() {
        assertEvalSuccess(Optional.of(nodeA),
                          Optional.of(nodeB));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateMissingSource() {
        assertEvalSuccess(Optional.empty(),
                          Optional.of(nodeB));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateMissingTarget() {
        assertEvalSuccess(Optional.of(nodeA),
                          Optional.empty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateMissingNodes() {
        assertEvalSuccess(Optional.empty(),
                          Optional.empty());
    }

    private void assertEvalSuccess(final Optional<Node<? extends View<?>, ? extends Edge>> source,
                                   final Optional<Node<? extends View<?>, ? extends Edge>> target) {
        when(connectionContext.getSource()).thenReturn(source);
        when(connectionContext.getTarget()).thenReturn(target);
        when(ruleExtension.getTypeArguments()).thenReturn(new Class[]{ParentDefinition.class});
        final RuleViolations violations = tested.evaluate(ruleExtension,
                                                          connectionContext);
        assertNotNull(violations);
        assertFalse(violations.violations(Violation.Type.ERROR).iterator().hasNext());
    }
}