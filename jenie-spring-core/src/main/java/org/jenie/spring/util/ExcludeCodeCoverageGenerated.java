package org.jenie.spring.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For more information on annotation-based filtering, refer to
 * <a href="https://github.com/jacoco/jacoco/wiki/FilteringOptions">Annotation-Based
 * Filtering</a>. The annotation name must contain 'Generated'.
 *
 * @author thinkhardj park
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface ExcludeCodeCoverageGenerated {

}
