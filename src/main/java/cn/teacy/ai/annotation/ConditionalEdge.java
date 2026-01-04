package cn.teacy.ai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Determine the next edge in the graph.
 * Support method type:
 * <ul>
 *     <li>com.alibaba.cloud.ai.graph.action.EdgeAction</li>
 *     <li>com.alibaba.cloud.ai.graph.action.AsyncEdgeAction</li>
 *     <li>com.alibaba.cloud.ai.graph.action.CommandAction</li>
 * </ul>
 * @see com.alibaba.cloud.ai.graph.action.EdgeAction
 * @see com.alibaba.cloud.ai.graph.action.AsyncEdgeAction
 * @see com.alibaba.cloud.ai.graph.action.CommandAction
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
