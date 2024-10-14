package org.jenie.spring.helloworld.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <a href="https://github.com/jacoco/jacoco/wiki/FilteringOptions">Annotation-Based
 * Filtering</a> 을 참고한다. 어노테이션 이름에 Generated 가 포함되어야 한다.
 *
 * @author thinkhardj park
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface ExcludeCodeCoverageGenerated {

}
