package com.oneandone.iocunit.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

/**
 * @author aschoerk
 * works like EjbName but is qualifier. So it allows to Inject Resources not only depending on their type as it is necessary
 * when injecting Strings.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface ResourceQualifier {

    String name() default "";

    String lookup() default "";

    String mappedName() default "";

    public class ResourceQualifierLiteral extends AnnotationLiteral<ResourceQualifier> implements ResourceQualifier {

        private static final long serialVersionUID = 7107494117787642445L;
        private final String name;
        private final String mappedName;
        private final String lookup;

        public ResourceQualifierLiteral(String name, String lookup, String mappedName) {
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
            return "ResourceQualifierLiteral{" +
                   "name='" + name + '\'' +
                   ", mappedName='" + mappedName + '\'' +
                   ", lookup='" + lookup + '\'' +
                   '}';
        }

//        @Override
//        public boolean equals(final Object o) {
//            if(this == o) return true;
//            if(o == null || getClass() != o.getClass()) return false;
//            if(!super.equals(o)) return false;
//            ResourceQualifierLiteral that = (ResourceQualifierLiteral) o;
//            return Objects.equals(name, that.name) &&
//                   Objects.equals(mappedName, that.mappedName) &&
//                   Objects.equals(lookup, that.lookup);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(super.hashCode(), name, mappedName, lookup);
//        }
    }
}
