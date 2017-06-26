/*
 *    Copyright 2014 Bryn Cooke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.ejbcdiunit.cdiunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface EjbQualifier {
    String name() default "";

    class EjbQualifierLiteral extends AnnotationLiteral<EjbQualifier> implements EjbQualifier {


        private static final long serialVersionUID = 4519600253306145960L;
        private final String name;

        static public final EjbQualifierLiteral INSTANCE = new EjbQualifierLiteral("");

        EjbQualifierLiteral(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }
}
