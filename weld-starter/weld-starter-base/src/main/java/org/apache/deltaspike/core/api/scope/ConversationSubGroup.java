/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.core.api.scope;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Allows to close a part of a group e.g.:
 *
 * public class MyGroup{}
 * &#x0040;ConversationScoped
 * &#x0040;ConversationGroup(MyGroup.class)
 * public class BeanA {}
 * <p></p>
 * &#x0040;ConversationScoped
 * &#x0040;ConversationGroup(MyGroup.class)
 * public class BeanB {}
 * <p></p>
 * &#x0040;ConversationScoped
 * &#x0040;ConversationGroup(MyGroup.class)
 * public class BeanC {}
 * <p></p>
 * &#x0040;ConversationSubGroup(of = MyGroup.class, subGroup = {BeanA.class, BeanB.class})
 * public class MySubGroup {}
 * <p>or</p>
 * &#x0040;ConversationSubGroup(subGroup = {BeanA.class, BeanB.class})
 * public class MySubGroup extends MyGroup {}
 * <p></p>
 * //...
 * this.groupedConversationManager.closeConversation(MySubGroup.class)
 *
 * OR it's possible to use implicit sub-groups (point to the interface(s) instead of the bean-class itself):
 * public interface MyUseCase {}
 *
 * &#x0040;ConversationSubGroup(of = MyGroup.class, subGroup = MyUseCase.class)
 * public class ImplicitSubGroup {}
 *
 * &#x0040;Named("myController")
 * &#x0040;ConversationScoped
 * &#x0040;ConversationGroup(MyGroup.class)
 * public class MyController implements Serializable, MyUseCase
 * {
 *    //...
 * }
 * //...
 * this.groupedConversationManager.closeConversation(ImplicitSubGroup.class)
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface ConversationSubGroup
{
    /**
     * Optionally defines the base conversation group
     * @return base conversation group or ConversationSubGroup if the subgroup inherits from the base conversation group
     */
    Class<?> of() default ConversationSubGroup.class;

    /**
     * Beans of the group which should be closed
     * @return beans of the group which should be closed
     */
    Class<?>[] subGroup();
}
