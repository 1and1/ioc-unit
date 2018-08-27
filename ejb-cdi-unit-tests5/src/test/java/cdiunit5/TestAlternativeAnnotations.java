/*
 * Copyright 2011 Bryn Cooke Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package cdiunit5;

import javax.inject.Inject;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;


@ExtendWith(JUnit5Extension.class)
@ActivatedAlternatives(AImplementation2.class)
public class TestAlternativeAnnotations {

    @Inject
    private AImplementation1 impl1;

    @Inject
    private AImplementation2 impl2;

    @Inject
    private AInterface impl;

    @Test
    public void testAlternativeSelected() {

        Assertions.assertTrue(impl instanceof AImplementation2, "Should have been impl2");
    }

}
