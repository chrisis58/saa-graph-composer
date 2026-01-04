package cn.teacy.ai;

import cn.teacy.ai.annotation.ConditionalEdge;
import cn.teacy.ai.annotation.GraphComposer;
import cn.teacy.ai.annotation.GraphKey;
import cn.teacy.ai.annotation.GraphNode;
import cn.teacy.ai.core.ReflectiveGraphBuilder;
import cn.teacy.ai.exception.GraphDefinitionException;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReflectiveGraphBuilderErrorTest {

    private ReflectiveGraphBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new ReflectiveGraphBuilder();
    }

    @Test
    @DisplayName("Should throw exception when @GraphComposer is missing")
    void throwExceptionMissingAnnotation() {
        assertThatThrownBy(() -> builder.build(new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("@GraphComposer");
    }

    @Test
    @DisplayName("Should throw exception for invalid Node Action type")
    void throwExceptionInvalidNodeType() {
        InvalidNodeTypeComposer composer = new InvalidNodeTypeComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .satisfies(e -> {
                    assertThat(e.getCause()).hasMessageContaining("must be instance of");
                });
    }

    @GraphComposer
    static class InvalidNodeTypeComposer {
        @GraphNode(id = "node1")
        final String invalidNode = "I am not an action";
    }

    @Test
    @DisplayName("Should throw exception for duplicate Node IDs")
    void throwExceptionDuplicateNodeIds() {
        DuplicateNodeIdComposer composer = new DuplicateNodeIdComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("already exist");
    }

    @GraphComposer
    static class DuplicateNodeIdComposer {
        @GraphNode(id = "node1")
        final NodeAction nodeA = (state) -> Map.of();

        @GraphNode(id = "node1")
        final NodeAction nodeB = (state) -> Map.of();
    }

    @Test
    @DisplayName("Should throw exception for null Node Action")
    void throwExceptionNullNodeAction() {
        NullNodeComposer composer = new NullNodeComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasCauseInstanceOf(IllegalStateException.class)
                .satisfies(e -> {
                    assertThat(e.getCause()).hasMessageContaining("is null");
                });
    }

    @GraphComposer
    static class NullNodeComposer {
        @GraphNode(id = "node1")
        final NodeAction nodeA = null;
    }

    @Test
    @DisplayName("Should throw exception for non-String Graph Key")
    void throwExceptionNonStringGraphKey() {
        NonStringKeyComposer composer = new NonStringKeyComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("must be String");
    }

    @GraphComposer
    static class NonStringKeyComposer {
        @GraphKey
        static final Object KEY_INVALID = new Object();
    }

    @Test
    @DisplayName("Should throw exception for non-static Graph Key")
    void throwExceptionNonStaticGraphKey() {
        NonStaticKeyComposer composer = new NonStaticKeyComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("must be 'static'");
    }

    @GraphComposer
    static class NonStaticKeyComposer {
        @GraphKey
        final String KEY_INSTANCE = "instanceKey";
    }

    @Test
    @DisplayName("Should throw exception for non-final Graph Key")
    void throwExceptionNonFinalGraphKey() {
        NonFinalKeyComposer composer = new NonFinalKeyComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("must be 'final'");
    }

    @GraphComposer
    static class NonFinalKeyComposer {
        @GraphKey
        static String KEY_NON_FINAL = "nonFinalKey";
    }

    @Test
    @DisplayName("Should throw exception for null Edge Action")
    void throwExceptionNullEdgeAction() {
        NullRoutingComposer composer = new NullRoutingComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("is null");
    }

    @GraphComposer
    static class NullRoutingComposer {
        @ConditionalEdge(source = StateGraph.START, mappings = {"toEnd", StateGraph.END})
        final EdgeAction nullRouting = null;
    }

    @Test
    @DisplayName("Should throw exception for unsupported Edge Action type")
    void throwExceptionUnsupportedEdgeType() {
        UnsupportedEdgeTypeComposer composer = new UnsupportedEdgeTypeComposer();
        assertThatThrownBy(() -> builder.build(composer))
                .isInstanceOf(GraphDefinitionException.class)
                .hasMessageContaining("is not supported. Must be one of");
    }

    @GraphComposer
    static class UnsupportedEdgeTypeComposer {
        @ConditionalEdge(source = StateGraph.START, mappings = {"toEnd", StateGraph.END})
        final String invalidRouting = "I am not an EdgeAction";
    }

}