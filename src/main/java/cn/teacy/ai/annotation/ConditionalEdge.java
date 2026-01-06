package cn.teacy.ai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Mark a field as a conditional edge in the graph.
 * Support field types are:
 * <ul>
 *     <li>{@link com.alibaba.cloud.ai.graph.action.EdgeAction}</li>
 *     <li>{@link com.alibaba.cloud.ai.graph.action.AsyncEdgeAction}</li>
 *     <li>{@link com.alibaba.cloud.ai.graph.action.CommandAction}</li>
 *     <li>{@link com.alibaba.cloud.ai.graph.action.AsyncCommandAction}</li>
 * </ul>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalEdge {

    /**
     * The source node id.
     */
    String source();

    /**
     * The mappings of the conditions to the next node ids.
     */
    String[] mappings();

}
