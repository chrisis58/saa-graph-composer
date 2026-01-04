package cn.teacy.ai.config;

import cn.teacy.ai.core.IGraphBuilder;
import cn.teacy.ai.core.ReflectiveGraphBuilder;
import cn.teacy.ai.properties.SaaGraphComposerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static cn.teacy.ai.constants.ComposerConfigConstants.GRAPH_BUILDER_BEAN_NAME;

@AutoConfiguration
@EnableConfigurationProperties(SaaGraphComposerProperties.class)
@ConditionalOnProperty(prefix = "spring.ai.graph-composer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SaaGraphComposerAutoConfiguration {

    @Bean(GRAPH_BUILDER_BEAN_NAME)
    @ConditionalOnMissingBean(name = GRAPH_BUILDER_BEAN_NAME)
    public IGraphBuilder graphBuilder() {
        return new ReflectiveGraphBuilder();
    }

}
