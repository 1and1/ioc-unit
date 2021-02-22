package com.oneandone.iocunit.jtajpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * @author aschoerk
 * works like EjbName but is qualifier. So it allows to Inject PersistenceUnits not only depending on their type
 * as it is necessary.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface PersistenceContextQualifier {

    String name() default "";

    String unitName() default "";

    public class PersistenceContextQualifierLiteral extends AnnotationLiteral<PersistenceContextQualifier> implements PersistenceContextQualifier {

        private static final long serialVersionUID = 1892735261517454937L;
        private final String name;
        private final String unitName;


        public PersistenceContextQualifierLiteral(String name, String unitName) {
            this.name = name;
            this.unitName = unitName;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String unitName() {
            return unitName;
        }


        @Override
        public String toString() {
            return "PersistenceContextQualifierLiteral{" +
                   "name='" + name + '\'' +
                   ", unitName='" + unitName + '\'' +
                   '}';
        }
    }
}
