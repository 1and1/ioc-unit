package net.oneandone.ejbcdiunit.purecdi;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;

/**
 * @author aschoerk
 */
public class GenericsTestLists extends WeldStarterTestBase {
    static class GBase {

    }

    static class GBaseDown extends GBase {

    }

    static class GBase2 {

    }

    static class Bean {
        @Inject
        List<GBase> listGBase;

        @Inject
        List<GBase2> listGBase2;

        @Inject
        ArrayList<GBase2> arraylistGBase2;

        @Inject
        List<? extends GBase> listAllGBase;

    }

    static class Producer {
        @Produces
        List<GBase> createList() {
            return Arrays.asList(new GBase(), new GBase());
        }

        @Produces
        ArrayList<GBase2> createList2() {
            return new ArrayList(Arrays.asList(new GBase2(), new GBase2(), new GBase2(), new GBase2()));
        }
    }

    @Test
    public void testList() {
        setBeanClasses(Producer.class, Bean.class);
        start();
        final Bean bean = selectGet(Bean.class);
        assertEquals(2, bean.listGBase.size());
        assertEquals(4, bean.listGBase2.size());
        assertEquals(4, bean.arraylistGBase2.size());
    }

    @Test
    public void testLang() throws NoSuchFieldException, NoSuchMethodException {
        final Type listGBaseType = Bean.class.getDeclaredField("listGBase").getGenericType();
        final Type listGBase2Type = Bean.class.getDeclaredField("listGBase2").getGenericType();
        final Type arraylist2Type = Bean.class.getDeclaredField("arraylistGBase2").getGenericType();
        final Type listAllGBaseType = Bean.class.getDeclaredField("listAllGBase").getGenericType();
        final Type createListType = Producer.class.getDeclaredMethod("createList").getGenericReturnType();
        final Type createList2Type = Producer.class.getDeclaredMethod("createList2").getGenericReturnType();

        assertFalse(TypeUtils.isAssignable(listGBase2Type, arraylist2Type));
        assertTrue(TypeUtils.isAssignable(arraylist2Type, listGBase2Type));
        assertFalse(TypeUtils.isAssignable(listGBase2Type, createList2Type));
        assertTrue(TypeUtils.isAssignable(createList2Type, listGBase2Type));
        assertTrue(TypeUtils.isAssignable(listGBaseType, createListType));
        assertTrue(TypeUtils.isAssignable(createListType, listGBaseType));
    }


}
