package io.hiyenwong.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hi Yen Wong
 * @date 2022/5/2 14:49
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name="io.hiyenwong.spring.boot.autoconfigure.MiniAutoConfiguration")
@ConditionalOnProperty(prefix="")
public class MinioContextAutoConfiguration {
}
