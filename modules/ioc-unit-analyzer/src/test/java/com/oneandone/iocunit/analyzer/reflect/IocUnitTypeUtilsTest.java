/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.iocunit.analyzer.reflect;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.oneandone.iocunit.analyzer.reflect.testbed.Foo;
import com.oneandone.iocunit.analyzer.reflect.testbed.GenericParent;
import com.oneandone.iocunit.analyzer.reflect.testbed.GenericTypeHolder;
import com.oneandone.iocunit.analyzer.reflect.testbed.StringParameterizedChild;
import org.junit.jupiter.api.Test;

/**
 * Test IocUnitTypeUtils
 */
@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
//raw types, where used, are used purposely
public class IocUnitTypeUtilsTest<B> {

    public interface This<K, V> {
    }

    public class That<K, V> implements This<K, V> {
    }

    public interface And<K, V> extends This<Number, Number> {
    }

    public class The<K, V> extends That<Number, Number> implements And<String, String> {
    }

    public class Other<T> implements This<String, T> {
    }

    public class Thing<Q> extends Other<B> {
    }

    public class Tester implements This<String, B> {
    }

    public This<String, String> dis;

    public That<String, String> dat;

    public The<String, String> da;

    public Other<String> uhder;

    public Thing ding;

    public IocUnitTypeUtilsTest<String>.Tester tester;

    public Tester tester2;

    public IocUnitTypeUtilsTest<String>.That<String, String> dat2;

    public IocUnitTypeUtilsTest<Number>.That<String, String> dat3;

    public Comparable<? extends Integer>[] intWildcardComparable;

    public static Comparable<String> stringComparable;

    public static Comparable<URI> uriComparable;

    public static Comparable<Integer> intComparable;

    public static Comparable<Long> longComparable;

    public static Comparable<?> wildcardComparable;

    public static URI uri;

    public static List<String>[] stringListArray;

    public void dummyMethod(final List list0, final List<Object> list1, final List<?> list2,
            final List<? super Object> list3, final List<String> list4, final List<? extends String> list5,
            final List<? super String> list6, final List[] list7, final List<Object>[] list8, final List<?>[] list9,
            final List<? super Object>[] list10, final List<String>[] list11, final List<? extends String>[] list12,
            final List<? super String>[] list13) {
    }

    @SuppressWarnings("boxing") // deliberately used here
    @Test
    public void testIsAssignable() throws SecurityException, NoSuchMethodException,
            NoSuchFieldException {
        List list0 = null;
        List<Object> list1 = null;
        List<?> list2 = null;
        List<? super Object> list3 = null;
        List<String> list4 = null;
        List<? extends String> list5 = null;
        List<? super String> list6 = null;
        List[] list7 = null;
        List<Object>[] list8 = null;
        List<?>[] list9 = null;
        List<? super Object>[] list10 = null;
        List<String>[] list11 = null;
        List<? extends String>[] list12 = null;
        List<? super String>[] list13;
        final Class<?> clazz = getClass();
        final Method method = clazz.getMethod("dummyMethod", List.class, List.class, List.class,
                List.class, List.class, List.class, List.class, List[].class, List[].class,
                List[].class, List[].class, List[].class, List[].class, List[].class);
        final Type[] types = method.getGenericParameterTypes();
//        list0 = list0;
        delegateBooleanAssertion(types, 0, 0, true);
        list1 = list0;
        delegateBooleanAssertion(types, 0, 1, true);
        list0 = list1;
        delegateBooleanAssertion(types, 1, 0, true);
        list2 = list0;
        delegateBooleanAssertion(types, 0, 2, true);
        list0 = list2;
        delegateBooleanAssertion(types, 2, 0, true);
        list3 = list0;
        delegateBooleanAssertion(types, 0, 3, true);
        list0 = list3;
        delegateBooleanAssertion(types, 3, 0, true);
        list4 = list0;
        delegateBooleanAssertion(types, 0, 4, true);
        list0 = list4;
        delegateBooleanAssertion(types, 4, 0, true);
        list5 = list0;
        delegateBooleanAssertion(types, 0, 5, true);
        list0 = list5;
        delegateBooleanAssertion(types, 5, 0, true);
        list6 = list0;
        delegateBooleanAssertion(types, 0, 6, true);
        list0 = list6;
        delegateBooleanAssertion(types, 6, 0, true);
//        list1 = list1;
        delegateBooleanAssertion(types, 1, 1, true);
        list2 = list1;
        delegateBooleanAssertion(types, 1, 2, true);
        list1 = (List<Object>) list2;
        delegateBooleanAssertion(types, 2, 1, false);
        list3 = list1;
        delegateBooleanAssertion(types, 1, 3, true);
        list1 = (List<Object>) list3;
        delegateBooleanAssertion(types, 3, 1, false);
        // list4 = list1;
        delegateBooleanAssertion(types, 1, 4, false);
        // list1 = list4;
        delegateBooleanAssertion(types, 4, 1, false);
        // list5 = list1;
        delegateBooleanAssertion(types, 1, 5, false);
        // list1 = list5;
        delegateBooleanAssertion(types, 5, 1, false);
        list6 = list1;
        delegateBooleanAssertion(types, 1, 6, true);
        list1 = (List<Object>) list6;
        delegateBooleanAssertion(types, 6, 1, false);
//        list2 = list2;
        delegateBooleanAssertion(types, 2, 2, true);
        list2 = list3;
        delegateBooleanAssertion(types, 2, 3, false);
        list2 = list4;
        delegateBooleanAssertion(types, 3, 2, true);
        list3 = (List<? super Object>) list2;
        delegateBooleanAssertion(types, 2, 4, false);
        list2 = list5;
        delegateBooleanAssertion(types, 4, 2, true);
        list4 = (List<String>) list2;
        delegateBooleanAssertion(types, 2, 5, false);
        list2 = list6;
        delegateBooleanAssertion(types, 5, 2, true);
        list5 = (List<? extends String>) list2;
        delegateBooleanAssertion(types, 2, 6, false);
//        list3 = list3;
        delegateBooleanAssertion(types, 6, 2, true);
        list6 = (List<? super String>) list2;
        delegateBooleanAssertion(types, 3, 3, true);
        // list4 = list3;
        delegateBooleanAssertion(types, 3, 4, false);
        // list3 = list4;
        delegateBooleanAssertion(types, 4, 3, false);
        // list5 = list3;
        delegateBooleanAssertion(types, 3, 5, false);
        // list3 = list5;
        delegateBooleanAssertion(types, 5, 3, false);
        list6 = list3;
        delegateBooleanAssertion(types, 3, 6, true);
        list3 = (List<? super Object>) list6;
        delegateBooleanAssertion(types, 6, 3, false);
//        list4 = list4;
        delegateBooleanAssertion(types, 4, 4, true);
        list5 = list4;
        delegateBooleanAssertion(types, 4, 5, true);
        list4 = (List<String>) list5;
        delegateBooleanAssertion(types, 5, 4, false);
        list6 = list4;
        delegateBooleanAssertion(types, 4, 6, true);
        list4 = (List<String>) list6;
        delegateBooleanAssertion(types, 6, 4, false);
//        list5 = list5;
        delegateBooleanAssertion(types, 5, 5, true);
        list6 = (List<? super String>) list5;
        delegateBooleanAssertion(types, 5, 6, false);
        list5 = (List<? extends String>) list6;
        delegateBooleanAssertion(types, 6, 5, false);
//        list6 = list6;
        delegateBooleanAssertion(types, 6, 6, true);

//        list7 = list7;
        delegateBooleanAssertion(types, 7, 7, true);
        list8 = list7;
        delegateBooleanAssertion(types, 7, 8, true);
        list7 = list8;
        delegateBooleanAssertion(types, 8, 7, true);
        list9 = list7;
        delegateBooleanAssertion(types, 7, 9, true);
        list7 = list9;
        delegateBooleanAssertion(types, 9, 7, true);
        list10 = list7;
        delegateBooleanAssertion(types, 7, 10, true);
        list7 = list10;
        delegateBooleanAssertion(types, 10, 7, true);
        list11 = list7;
        delegateBooleanAssertion(types, 7, 11, true);
        list7 = list11;
        delegateBooleanAssertion(types, 11, 7, true);
        list12 = list7;
        delegateBooleanAssertion(types, 7, 12, true);
        list7 = list12;
        delegateBooleanAssertion(types, 12, 7, true);
        list13 = list7;
        delegateBooleanAssertion(types, 7, 13, true);
        list7 = list13;
        delegateBooleanAssertion(types, 13, 7, true);
//        list8 = list8;
        delegateBooleanAssertion(types, 8, 8, true);
        list9 = list8;
        delegateBooleanAssertion(types, 8, 9, true);
        list8 = (List<Object>[]) list9;
        delegateBooleanAssertion(types, 9, 8, false);
        list10 = list8;
        delegateBooleanAssertion(types, 8, 10, true);
        list8 = (List<Object>[]) list10; // NOTE cast is required by Sun Java, but not by Eclipse
        delegateBooleanAssertion(types, 10, 8, false);
        // list11 = list8;
        delegateBooleanAssertion(types, 8, 11, false);
        // list8 = list11;
        delegateBooleanAssertion(types, 11, 8, false);
        // list12 = list8;
        delegateBooleanAssertion(types, 8, 12, false);
        // list8 = list12;
        delegateBooleanAssertion(types, 12, 8, false);
        list13 = list8;
        delegateBooleanAssertion(types, 8, 13, true);
        list8 = (List<Object>[]) list13;
        delegateBooleanAssertion(types, 13, 8, false);
//        list9 = list9;
        delegateBooleanAssertion(types, 9, 9, true);
        list10 = (List<? super Object>[]) list9;
        delegateBooleanAssertion(types, 9, 10, false);
        list9 = list10;
        delegateBooleanAssertion(types, 10, 9, true);
        list11 = (List<String>[]) list9;
        delegateBooleanAssertion(types, 9, 11, false);
        list9 = list11;
        delegateBooleanAssertion(types, 11, 9, true);
        list12 = (List<? extends String>[]) list9;
        delegateBooleanAssertion(types, 9, 12, false);
        list9 = list12;
        delegateBooleanAssertion(types, 12, 9, true);
        list13 = (List<? super String>[]) list9;
        delegateBooleanAssertion(types, 9, 13, false);
        list9 = list13;
        delegateBooleanAssertion(types, 13, 9, true);
//        list10 = list10;
        delegateBooleanAssertion(types, 10, 10, true);
        // list11 = list10;
        delegateBooleanAssertion(types, 10, 11, false);
        // list10 = list11;
        delegateBooleanAssertion(types, 11, 10, false);
        // list12 = list10;
        delegateBooleanAssertion(types, 10, 12, false);
        // list10 = list12;
        delegateBooleanAssertion(types, 12, 10, false);
        list13 = list10;
        delegateBooleanAssertion(types, 10, 13, true);
        list10 = (List<? super Object>[]) list13;
        delegateBooleanAssertion(types, 13, 10, false);
//        list11 = list11;
        delegateBooleanAssertion(types, 11, 11, true);
        list12 = list11;
        delegateBooleanAssertion(types, 11, 12, true);
        list11 = (List<String>[]) list12;
        delegateBooleanAssertion(types, 12, 11, false);
        list13 = list11;
        delegateBooleanAssertion(types, 11, 13, true);
        list11 = (List<String>[]) list13;
        delegateBooleanAssertion(types, 13, 11, false);
//        list12 = list12;
        delegateBooleanAssertion(types, 12, 12, true);
        list13 = (List<? super String>[]) list12;
        delegateBooleanAssertion(types, 12, 13, false);
        list12 = (List<? extends String>[]) list13;
        delegateBooleanAssertion(types, 13, 12, false);
//        list13 = list13;
        delegateBooleanAssertion(types, 13, 13, true);
        final Type disType = getClass().getField("dis").getGenericType();
        // Reporter.log( ( ( ParameterizedType ) disType
        // ).getOwnerType().getClass().toString() );
        final Type datType = getClass().getField("dat").getGenericType();
        final Type daType = getClass().getField("da").getGenericType();
        final Type uhderType = getClass().getField("uhder").getGenericType();
        final Type dingType = getClass().getField("ding").getGenericType();
        final Type testerType = getClass().getField("tester").getGenericType();
        final Type tester2Type = getClass().getField("tester2").getGenericType();
        final Type dat2Type = getClass().getField("dat2").getGenericType();
        final Type dat3Type = getClass().getField("dat3").getGenericType();
        dis = dat;
        assertTrue(IocUnitTypeUtils.isAssignable(datType, disType));
        // dis = da;
        assertFalse(IocUnitTypeUtils.isAssignable(daType, disType));
        dis = uhder;
        assertTrue(IocUnitTypeUtils.isAssignable(uhderType, disType));
        dis = ding;
        assertFalse(IocUnitTypeUtils.isAssignable(dingType, disType),
                String.format("type %s not assignable to %s!", dingType, disType));
        dis = tester;
        assertTrue(IocUnitTypeUtils.isAssignable(testerType, disType));
        // dis = tester2;
        assertFalse(IocUnitTypeUtils.isAssignable(tester2Type, disType));
        // dat = dat2;
        assertFalse(IocUnitTypeUtils.isAssignable(dat2Type, datType));
        // dat2 = dat;
        assertFalse(IocUnitTypeUtils.isAssignable(datType, dat2Type));
        // dat = dat3;
        assertFalse(IocUnitTypeUtils.isAssignable(dat3Type, datType));
        final char ch = 0;
        final boolean bo = false;
        final byte by = 0;
        final short sh = 0;
        int in = 0;
        long lo = 0;
        final float fl = 0;
        double du = 0;
        du = ch;
        assertTrue(IocUnitTypeUtils.isAssignable(char.class, double.class));
        du = by;
        assertTrue(IocUnitTypeUtils.isAssignable(byte.class, double.class));
        du = sh;
        assertTrue(IocUnitTypeUtils.isAssignable(short.class, double.class));
        du = in;
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, double.class));
        du = lo;
        assertTrue(IocUnitTypeUtils.isAssignable(long.class, double.class));
        du = fl;
        assertTrue(IocUnitTypeUtils.isAssignable(float.class, double.class));
        lo = in;
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, long.class));
        lo = Integer.valueOf(0);
        assertTrue(IocUnitTypeUtils.isAssignable(Integer.class, long.class));
        // Long lngW = 1;
        assertFalse(IocUnitTypeUtils.isAssignable(int.class, Long.class));
        // lngW = Integer.valueOf( 0 );
        assertFalse(IocUnitTypeUtils.isAssignable(Integer.class, Long.class));
        in = Integer.valueOf(0);
        assertTrue(IocUnitTypeUtils.isAssignable(Integer.class, int.class));
        final Integer inte = in;
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, Integer.class));
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, Number.class));
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, Object.class));
        final Type intComparableType = getClass().getField("intComparable").getGenericType();
        intComparable = 1;
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, intComparableType));
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, Comparable.class));
        final Serializable ser = 1;
        assertTrue(IocUnitTypeUtils.isAssignable(int.class, Serializable.class));
        final Type longComparableType = getClass().getField("longComparable").getGenericType();
        // longComparable = 1;
        assertFalse(IocUnitTypeUtils.isAssignable(int.class, longComparableType));
        // longComparable = Integer.valueOf( 0 );
        assertFalse(IocUnitTypeUtils.isAssignable(Integer.class, longComparableType));
        // int[] ia;
        // long[] la = ia;
        assertFalse(IocUnitTypeUtils.isAssignable(int[].class, long[].class));
        final Integer[] ia = null;
        final Type caType = getClass().getField("intWildcardComparable").getGenericType();
        intWildcardComparable = ia;
        assertTrue(IocUnitTypeUtils.isAssignable(Integer[].class, caType));
        // int[] ina = ia;
        assertFalse(IocUnitTypeUtils.isAssignable(Integer[].class, int[].class));
        final int[] ina = null;
        Object[] oa;
        // oa = ina;
        assertFalse(IocUnitTypeUtils.isAssignable(int[].class, Object[].class));
        oa = new Integer[0];
        assertTrue(IocUnitTypeUtils.isAssignable(Integer[].class, Object[].class));
        final Type bClassType = AClass.class.getField("bClass").getGenericType();
        final Type cClassType = AClass.class.getField("cClass").getGenericType();
        final Type dClassType = AClass.class.getField("dClass").getGenericType();
        final Type eClassType = AClass.class.getField("eClass").getGenericType();
        final Type fClassType = AClass.class.getField("fClass").getGenericType();
        final AClass aClass = new AClass(new AAClass<>());
        aClass.bClass = aClass.cClass;
        assertTrue(IocUnitTypeUtils.isAssignable(cClassType, bClassType));
        aClass.bClass = aClass.dClass;
        assertTrue(IocUnitTypeUtils.isAssignable(dClassType, bClassType));
        aClass.bClass = aClass.eClass;
        assertTrue(IocUnitTypeUtils.isAssignable(eClassType, bClassType));
        aClass.bClass = aClass.fClass;
        assertTrue(IocUnitTypeUtils.isAssignable(fClassType, bClassType));
        aClass.cClass = aClass.dClass;
        assertTrue(IocUnitTypeUtils.isAssignable(dClassType, cClassType));
        aClass.cClass = aClass.eClass;
        assertTrue(IocUnitTypeUtils.isAssignable(eClassType, cClassType));
        aClass.cClass = aClass.fClass;
        assertTrue(IocUnitTypeUtils.isAssignable(fClassType, cClassType));
        aClass.dClass = aClass.eClass;
        assertTrue(IocUnitTypeUtils.isAssignable(eClassType, dClassType));
        aClass.dClass = aClass.fClass;
        assertTrue(IocUnitTypeUtils.isAssignable(fClassType, dClassType));
        aClass.eClass = aClass.fClass;
        assertTrue(IocUnitTypeUtils.isAssignable(fClassType, eClassType));
    }

    public void delegateBooleanAssertion(final Type[] types, final int i2, final int i1, final boolean expected) {
        final Type type1 = types[i1];
        final Type type2 = types[i2];
        final boolean isAssignable = IocUnitTypeUtils.isAssignable(type2, type1);

        if (expected) {
            assertTrue(isAssignable,
                    "[" + i1 + ", " + i2 + "]: From "
                                + String.valueOf(type2) + " to "
                                + String.valueOf(type1));
        } else {
            assertFalse(isAssignable,
                    "[" + i1 + ", " + i2 + "]: From "
                                + String.valueOf(type2) + " to "
                                + String.valueOf(type1));
        }
    }

    @SuppressWarnings("boxing") // boxing is deliberate here
    @Test
    public void testIsInstance() throws SecurityException, NoSuchFieldException {
        final Type intComparableType = getClass().getField("intComparable").getGenericType();
        final Type uriComparableType = getClass().getField("uriComparable").getGenericType();
        intComparable = 1;
        assertTrue(IocUnitTypeUtils.isInstance(1, intComparableType));
        // uriComparable = 1;
        assertFalse(IocUnitTypeUtils.isInstance(1, uriComparableType));
    }

    @Test
    public void testGetTypeArguments() {
        Map<TypeVariable<?>, Type> typeVarAssigns;
        TypeVariable<?> treeSetTypeVar;
        Type typeArg;

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(Integer.class, Comparable.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        assertTrue(typeVarAssigns.containsKey(treeSetTypeVar),
                "Type var assigns for Comparable from Integer: " + typeVarAssigns);
        typeArg = typeVarAssigns.get(treeSetTypeVar);
        assertEquals(Integer.class, typeVarAssigns.get(treeSetTypeVar),
                "Type argument of Comparable from Integer: " + typeArg);

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(int.class, Comparable.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        assertTrue(typeVarAssigns.containsKey(treeSetTypeVar),
                "Type var assigns for Comparable from int: " + typeVarAssigns);
        typeArg = typeVarAssigns.get(treeSetTypeVar);
        assertEquals(Integer.class, typeVarAssigns.get(treeSetTypeVar),
                "Type argument of Comparable from int: " + typeArg);

        final Collection<Integer> col = Arrays.asList(new Integer[0]);
        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(List.class, Collection.class);
        treeSetTypeVar = Comparable.class.getTypeParameters()[0];
        assertFalse(typeVarAssigns.containsKey(treeSetTypeVar),
                "Type var assigns for Collection from List: " + typeVarAssigns);

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(AAAClass.BBBClass.class, AAClass.BBClass.class);
        assertEquals(2, typeVarAssigns.size());
        assertEquals(String.class, typeVarAssigns.get(AAClass.class.getTypeParameters()[0]));
        assertEquals(String.class, typeVarAssigns.get(AAClass.BBClass.class.getTypeParameters()[0]));

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(Other.class, This.class);
        assertEquals(2, typeVarAssigns.size());
        assertEquals(String.class, typeVarAssigns.get(This.class.getTypeParameters()[0]));
        assertEquals(Other.class.getTypeParameters()[0], typeVarAssigns.get(This.class.getTypeParameters()[1]));

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(And.class, This.class);
        assertEquals(2, typeVarAssigns.size());
        assertEquals(Number.class, typeVarAssigns.get(This.class.getTypeParameters()[0]));
        assertEquals(Number.class, typeVarAssigns.get(This.class.getTypeParameters()[1]));

        typeVarAssigns = IocUnitTypeUtils.getTypeArguments(Thing.class, Other.class);
        assertEquals(2, typeVarAssigns.size());
        assertEquals(getClass().getTypeParameters()[0], typeVarAssigns.get(getClass().getTypeParameters()[0]));
        assertEquals(getClass().getTypeParameters()[0], typeVarAssigns.get(Other.class.getTypeParameters()[0]));
    }

    @Test
    public void testTypesSatisfyVariables() throws SecurityException,
            NoSuchMethodException {
        final Map<TypeVariable<?>, Type> typeVarAssigns = new HashMap<>();
        final Integer max = IocUnitTypeUtilsTest.<Integer>stub();
        typeVarAssigns.put(getClass().getMethod("stub").getTypeParameters()[0], Integer.class);
        assertTrue(IocUnitTypeUtils.typesSatisfyVariables(typeVarAssigns));
        typeVarAssigns.clear();
        typeVarAssigns.put(getClass().getMethod("stub2").getTypeParameters()[0], Integer.class);
        assertTrue(IocUnitTypeUtils.typesSatisfyVariables(typeVarAssigns));
        typeVarAssigns.clear();
        typeVarAssigns.put(getClass().getMethod("stub3").getTypeParameters()[0], Integer.class);
        assertTrue(IocUnitTypeUtils.typesSatisfyVariables(typeVarAssigns));
    }

    @Test
    public void testDetermineTypeVariableAssignments() throws SecurityException,
            NoSuchFieldException {
        final ParameterizedType iterableType = (ParameterizedType) getClass().getField("iterable")
                .getGenericType();
        final Map<TypeVariable<?>, Type> typeVarAssigns = IocUnitTypeUtils.determineTypeArguments(TreeSet.class,
                iterableType);
        final TypeVariable<?> treeSetTypeVar = TreeSet.class.getTypeParameters()[0];
        assertTrue(typeVarAssigns.containsKey(treeSetTypeVar));
        assertEquals(iterableType.getActualTypeArguments()[0], typeVarAssigns
                .get(treeSetTypeVar));
    }

    @Test
    public void testGetRawType() throws SecurityException, NoSuchFieldException {
        final Type stringParentFieldType = GenericTypeHolder.class.getDeclaredField("stringParent")
                .getGenericType();
        final Type integerParentFieldType = GenericTypeHolder.class.getDeclaredField("integerParent")
                .getGenericType();
        final Type foosFieldType = GenericTypeHolder.class.getDeclaredField("foos").getGenericType();
        final Type genericParentT = GenericParent.class.getTypeParameters()[0];
        assertEquals(GenericParent.class, IocUnitTypeUtils.getRawType(stringParentFieldType, null));
        assertEquals(GenericParent.class, IocUnitTypeUtils.getRawType(integerParentFieldType,
                        null));
        assertEquals(List.class, IocUnitTypeUtils.getRawType(foosFieldType, null));
        assertEquals(String.class, IocUnitTypeUtils.getRawType(genericParentT,
                StringParameterizedChild.class));
        assertEquals(String.class, IocUnitTypeUtils.getRawType(genericParentT,
                stringParentFieldType));
        assertEquals(Foo.class, IocUnitTypeUtils.getRawType(Iterable.class.getTypeParameters()[0],
                foosFieldType));
        assertEquals(Foo.class, IocUnitTypeUtils.getRawType(List.class.getTypeParameters()[0],
                foosFieldType));
        assertNull(IocUnitTypeUtils.getRawType(genericParentT, GenericParent.class));
        assertEquals(GenericParent[].class, IocUnitTypeUtils.getRawType(GenericTypeHolder.class
                .getDeclaredField("barParents").getGenericType(), null));
    }

    @Test
    public void testIsArrayTypeClasses() {
        assertTrue(IocUnitTypeUtils.isArrayType(boolean[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(byte[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(short[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(int[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(char[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(long[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(float[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(double[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(Object[].class));
        assertTrue(IocUnitTypeUtils.isArrayType(String[].class));

        assertFalse(IocUnitTypeUtils.isArrayType(boolean.class));
        assertFalse(IocUnitTypeUtils.isArrayType(byte.class));
        assertFalse(IocUnitTypeUtils.isArrayType(short.class));
        assertFalse(IocUnitTypeUtils.isArrayType(int.class));
        assertFalse(IocUnitTypeUtils.isArrayType(char.class));
        assertFalse(IocUnitTypeUtils.isArrayType(long.class));
        assertFalse(IocUnitTypeUtils.isArrayType(float.class));
        assertFalse(IocUnitTypeUtils.isArrayType(double.class));
        assertFalse(IocUnitTypeUtils.isArrayType(Object.class));
        assertFalse(IocUnitTypeUtils.isArrayType(String.class));
    }

    @Test
    public void testIsArrayGenericTypes() throws Exception {
        final Method method = getClass().getMethod("dummyMethod", List.class, List.class, List.class,
                List.class, List.class, List.class, List.class, List[].class, List[].class,
                List[].class, List[].class, List[].class, List[].class, List[].class);

        final Type[] types = method.getGenericParameterTypes();

        assertFalse(IocUnitTypeUtils.isArrayType(types[0]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[1]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[2]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[3]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[4]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[5]));
        assertFalse(IocUnitTypeUtils.isArrayType(types[6]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[7]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[8]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[9]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[10]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[11]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[12]));
        assertTrue(IocUnitTypeUtils.isArrayType(types[13]));
    }

    @Test
    public void testGetPrimitiveArrayComponentType() {
        assertEquals(boolean.class, IocUnitTypeUtils.getArrayComponentType(boolean[].class));
        assertEquals(byte.class, IocUnitTypeUtils.getArrayComponentType(byte[].class));
        assertEquals(short.class, IocUnitTypeUtils.getArrayComponentType(short[].class));
        assertEquals(int.class, IocUnitTypeUtils.getArrayComponentType(int[].class));
        assertEquals(char.class, IocUnitTypeUtils.getArrayComponentType(char[].class));
        assertEquals(long.class, IocUnitTypeUtils.getArrayComponentType(long[].class));
        assertEquals(float.class, IocUnitTypeUtils.getArrayComponentType(float[].class));
        assertEquals(double.class, IocUnitTypeUtils.getArrayComponentType(double[].class));

        assertNull(IocUnitTypeUtils.getArrayComponentType(boolean.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(byte.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(short.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(int.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(char.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(long.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(float.class));
        assertNull(IocUnitTypeUtils.getArrayComponentType(double.class));
    }

    @Test
    public void testGetArrayComponentType() throws Exception {
        final Method method = getClass().getMethod("dummyMethod", List.class, List.class, List.class,
                List.class, List.class, List.class, List.class, List[].class, List[].class,
                List[].class, List[].class, List[].class, List[].class, List[].class);

        final Type[] types = method.getGenericParameterTypes();

        assertNull(IocUnitTypeUtils.getArrayComponentType(types[0]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[1]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[2]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[3]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[4]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[5]));
        assertNull(IocUnitTypeUtils.getArrayComponentType(types[6]));
        assertEquals(types[0], IocUnitTypeUtils.getArrayComponentType(types[7]));
        assertEquals(types[1], IocUnitTypeUtils.getArrayComponentType(types[8]));
        assertEquals(types[2], IocUnitTypeUtils.getArrayComponentType(types[9]));
        assertEquals(types[3], IocUnitTypeUtils.getArrayComponentType(types[10]));
        assertEquals(types[4], IocUnitTypeUtils.getArrayComponentType(types[11]));
        assertEquals(types[5], IocUnitTypeUtils.getArrayComponentType(types[12]));
        assertEquals(types[6], IocUnitTypeUtils.getArrayComponentType(types[13]));
    }

    @Test
    public void testLang820() {
        final Type[] typeArray = {String.class, String.class};
        final Type[] expectedArray = {String.class};
        assertArrayEquals(expectedArray, IocUnitTypeUtils.normalizeUpperBounds(typeArray));
    }

    @Test
    public void testParameterize() throws Exception {
        final ParameterizedType stringComparableType = IocUnitTypeUtils.parameterize(Comparable.class, String.class);
        assertTrue(IocUnitTypeUtils.equals(getClass().getField("stringComparable").getGenericType(),
            stringComparableType));
        assertEquals("java.lang.Comparable<java.lang.String>", stringComparableType.toString());
    }

    @Test
    public void testParameterizeNarrowerTypeArray() {
        final TypeVariable<?>[] variables = ArrayList.class.getTypeParameters();
        final ParameterizedType parameterizedType = IocUnitTypeUtils.parameterize(ArrayList.class, variables);
        final Map<TypeVariable<?>, Type> mapping = Collections.<TypeVariable<?>, Type>singletonMap(variables[0], String.class);
        final Type unrolled = IocUnitTypeUtils.unrollVariables(mapping, parameterizedType);
        assertEquals(IocUnitTypeUtils.parameterize(ArrayList.class, String.class), unrolled);
    }

    @Test
    public void testParameterizeWithOwner() throws Exception {
        final Type owner = IocUnitTypeUtils.parameterize(IocUnitTypeUtilsTest.class, String.class);
        final ParameterizedType dat2Type = IocUnitTypeUtils.parameterizeWithOwner(owner, That.class, String.class, String.class);
        assertTrue(IocUnitTypeUtils.equals(getClass().getField("dat2").getGenericType(), dat2Type));
    }

    @Test
    public void testWildcardType() throws Exception {
        final WildcardType simpleWildcard = IocUnitTypeUtils.wildcardType().withUpperBounds(String.class).build();
        final Field cClass = AClass.class.getField("cClass");
        assertTrue(IocUnitTypeUtils.equals(((ParameterizedType) cClass.getGenericType()).getActualTypeArguments()[0],
            simpleWildcard));
        assertEquals(String.format("? extends %s", String.class.getName()), IocUnitTypeUtils.toString(simpleWildcard));
        assertEquals(String.format("? extends %s", String.class.getName()), simpleWildcard.toString());
    }

    @Test
    public void testUnboundedWildcardType() {
        final WildcardType unbounded = IocUnitTypeUtils.wildcardType().withLowerBounds((Type) null).withUpperBounds().build();
        assertTrue(IocUnitTypeUtils.equals(IocUnitTypeUtils.WILDCARD_ALL, unbounded));
        assertArrayEquals(new Type[] { Object.class }, IocUnitTypeUtils.getImplicitUpperBounds(unbounded));
        assertArrayEquals(new Type[] { null }, IocUnitTypeUtils.getImplicitLowerBounds(unbounded));
        assertEquals("?", IocUnitTypeUtils.toString(unbounded));
        assertEquals("?", unbounded.toString());
    }

    @Test
    public void testLowerBoundedWildcardType() {
       final WildcardType lowerBounded = IocUnitTypeUtils.wildcardType().withLowerBounds(java.sql.Date.class).build();
       assertEquals(String.format("? super %s", java.sql.Date.class.getName()), IocUnitTypeUtils.toString(lowerBounded));
       assertEquals(String.format("? super %s", java.sql.Date.class.getName()), lowerBounded.toString());

       final TypeVariable<Class<Iterable>> iterableT0 = Iterable.class.getTypeParameters()[0];
       final WildcardType lowerTypeVariable = IocUnitTypeUtils.wildcardType().withLowerBounds(iterableT0).build();
       assertEquals(String.format("? super %s", iterableT0.getName()), IocUnitTypeUtils.toString(lowerTypeVariable));
       assertEquals(String.format("? super %s", iterableT0.getName()), lowerTypeVariable.toString());
    }

    @Test
    public void testLang1114() throws Exception {
        final Type nonWildcardType = getClass().getDeclaredField("wildcardComparable").getGenericType();
        final Type wildcardType = ((ParameterizedType) nonWildcardType).getActualTypeArguments()[0];

        assertFalse(IocUnitTypeUtils.equals(wildcardType, nonWildcardType));
        assertFalse(IocUnitTypeUtils.equals(nonWildcardType, wildcardType));
    }

    @Test
    public void testGenericArrayType() throws Exception {
        final Type expected = getClass().getField("intWildcardComparable").getGenericType();
        final GenericArrayType actual =
            IocUnitTypeUtils.genericArrayType(IocUnitTypeUtils.parameterize(Comparable.class, IocUnitTypeUtils.wildcardType()
                .withUpperBounds(Integer.class).build()));
        assertTrue(IocUnitTypeUtils.equals(expected, actual));
        assertEquals("java.lang.Comparable<? extends java.lang.Integer>[]", actual.toString());
    }

    @Test
    public void testToStringLang1311() {
        assertEquals("int[]", IocUnitTypeUtils.toString(int[].class));
        assertEquals("java.lang.Integer[]", IocUnitTypeUtils.toString(Integer[].class));
        final Field stringListField = FieldUtils.getDeclaredField(getClass(), "stringListArray");
        assertEquals("java.util.List<java.lang.String>[]", IocUnitTypeUtils.toString(stringListField.getGenericType()));
    }

    @Test
    public void testToLongString() {
        assertEquals(getClass().getName() + ":B", IocUnitTypeUtils.toLongString(getClass().getTypeParameters()[0]));
    }

    @Test
    public void testWrap() {
        final Type t = getClass().getTypeParameters()[0];
        assertTrue(IocUnitTypeUtils.equals(t, IocUnitTypeUtils.wrap(t).getType()));

        assertEquals(String.class, IocUnitTypeUtils.wrap(String.class).getType());
    }

    public static class ClassWithSuperClassWithGenericType extends ArrayList<Object> {
        private static final long serialVersionUID = 1L;

        public static <U> Iterable<U> methodWithGenericReturnType() {
            return null;
        }
    }

    @Test
    public void testLANG1190() throws Exception {
        final Type fromType = ClassWithSuperClassWithGenericType.class.getDeclaredMethod("methodWithGenericReturnType").getGenericReturnType();
        final Type failingToType = IocUnitTypeUtils.wildcardType().withLowerBounds(ClassWithSuperClassWithGenericType.class).build();

        assertTrue(IocUnitTypeUtils.isAssignable(fromType, failingToType));
    }

    @Test
    public void testLANG1348() throws Exception {
        final Method method = Enum.class.getMethod("valueOf", Class.class, String.class);
        assertEquals("T extends java.lang.Enum<T>", IocUnitTypeUtils.toString(method.getGenericReturnType()));
    }

    public Iterable<? extends Map<Integer, ? extends Collection<?>>> iterable;

    public static <G extends Comparable<G>> G stub() {
        return null;
    }

    public static <G extends Comparable<? super G>> G stub2() {
        return null;
    }

    public static <T extends Comparable<? extends T>> T stub3() {
        return null;
    }
}

class AAClass<T> {

    public class BBClass<S> {
    }
}

class AAAClass extends AAClass<String> {
    public class BBBClass extends BBClass<String> {
    }
}

@SuppressWarnings("rawtypes")
//raw types, where used, are used purposely
class AClass extends AAClass<String>.BBClass<Number> {

    AClass(final AAClass<String> enclosingInstance) {
        enclosingInstance.super();
    }

    public class BClass<T> {
    }

    public class CClass<T> extends BClass {
    }

    public class DClass<T> extends CClass<T> {
    }

    public class EClass<T> extends DClass {
    }

    public class FClass extends EClass<String> {
    }

    public class GClass<T extends BClass<? extends T> & AInterface<AInterface<? super T>>> {
    }

    public BClass<Number> bClass;

    public CClass<? extends String> cClass;

    public DClass<String> dClass;

    public EClass<String> eClass;

    public FClass fClass;

    public GClass gClass;

    public interface AInterface<T> {
    }
}
