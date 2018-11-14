package com.oneandone.cdi.weld;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.ParameterizedType;

import javax.inject.Inject;

import org.junit.Test;

import com.oneandone.cdi.weldstarter.WeldStarterTestBase;
import com.oneandone.cdi.weldstarter.WrappedDeploymentException;

/**
 * @author aschoerk
 */
public class GenericsTestsSimple extends WeldStarterTestBase {

    static class GenericT<T> {

        public T getValue() {
            return value;
        }

        public void setValue(final T valueP) {
            this.value = valueP;
        }

        T value;

    }

    static class GenericTSub<T, U> extends GenericT<T> {

        public U getUValue() {
            return uvalue;
        }

        public void setUValue(final U valueP) {
            this.uvalue = valueP;
        }

        U uvalue;

    }


    static class Bean {
        @Inject
        GenericT<Integer> genericT;

        @Inject
        GenericTSub<Integer, Float> genericTSub;
    }


    @Test
    public void testBean() throws NoSuchFieldException {
        setBeanClasses(Bean.class, GenericTSub.class);
        start();
        final Bean bean = selectGet(Bean.class);
        assertNull(bean.genericT.getValue());
        assertNull(((GenericTSub) (bean.genericT)).getUValue());
        assertEquals(Object.class, ((GenericTSub) (bean.genericT)).getClass().getDeclaredField("uvalue").getType());
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testBeanAmbiguus() throws NoSuchFieldException {
        setBeanClasses(Bean.class, GenericTSub.class, GenericT.class);
        start();
        final Bean bean = selectGet(Bean.class);
    }

    static class GenericBean<T> {
        @Inject
        GenericT<T> genericT;

        @Inject
        GenericTSub<T, Float> genericTSub;
    }

    @Test
    public void testGenericBean() {
        setBeanClasses(GenericBean.class, GenericTSub.class);
        start();
        final GenericBean bean = selectGet(GenericBean.class);
        assertEquals(null, bean.genericT.value);
        assertEquals(null, bean.genericTSub.value);
        assertEquals(null, bean.genericTSub.uvalue);
        bean.genericT.setValue(1000);
        bean.genericTSub.setValue(1001);
        bean.genericTSub.setUValue(1002);
    }

    @Test
    public void testGenericBean2() {
        setBeanClasses(GenericBean.class, GenericTSub.class);
        start();
        final GenericBean bean = selectGet(GenericBean.class);
        assertEquals(null, bean.genericT.value);
        assertEquals(null, bean.genericTSub.value);
        assertEquals(null, bean.genericTSub.uvalue);
        bean.genericT.setValue(1000);
        bean.genericTSub.setValue(1001);
        bean.genericTSub.setUValue(1002);
    }

    @Test(expected = WrappedDeploymentException.class)
    public void testGenericBeanAmbiguus() {
        setBeanClasses(GenericBean.class, GenericTSub.class, GenericT.class);
        start();
        final GenericBean bean = selectGet(GenericBean.class);
    }

    @Test
    public void checkClassStructure() {
        assertEquals(2, GenericTSub.class.getTypeParameters().length);
        assertEquals("T", GenericTSub.class.getTypeParameters()[0].getName());
        assertEquals("U", GenericTSub.class.getTypeParameters()[1].getName());
        assertEquals(1, ((ParameterizedType) GenericTSub.class.getGenericSuperclass()).getActualTypeArguments().length);
        assertEquals("T", ((ParameterizedType) GenericTSub.class.getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }
}
