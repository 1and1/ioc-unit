package com.oneandone.iocunit.jtajpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * @author aschoerk
 * works like EjbName but is qualifier. So it allows to Inject Resources not only depending on their type as it is necessary
 * when injecting Strings.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface PersQualifier {

    @Nonbinding
    String name() default "";

    @Nonbinding
    String lookup() default "";

    @Nonbinding
    String mappedName() default "";

    public class PersQualifierLiteral extends AnnotationLiteral<PersQualifier> implements PersQualifier {

        private static final long serialVersionUID = 7107494117787642445L;
        private final String name;
        private final String mappedName;
        private final String lookup;

        public PersQualifierLiteral(String name, String lookup, String mappedName) {
            this.name = name;
            this.mappedName = mappedName;
            this.lookup = lookup;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String lookup() {
            return lookup;
        }

        @Override
        public String mappedName() {
            return mappedName;
        }

        @Override
        public String toString() {
            return "PersQualifierLiteral{" +
                   "name='" + name + '\'' +
                   ", mappedName='" + mappedName + '\'' +
                   ", lookup='" + lookup + '\'' +
                   '}';
        }

    }
}
