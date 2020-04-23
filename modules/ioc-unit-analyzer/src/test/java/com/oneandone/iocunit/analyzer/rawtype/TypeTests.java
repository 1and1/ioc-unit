package com.oneandone.iocunit.analyzer.rawtype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.oneandone.iocunit.analyzer.QualifiedType;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class TypeTests {
    List<String>      listString;
    List              rawlist;
    RawListSub        rawListSub;
    StringList        stringList;
    ArrayList<String> arrayListString;

    static QualifiedType qtype(String name) {
        try {
            return new QualifiedType(TypeTests.class.getDeclaredField(name));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static QualifiedType qtype(Class<?> c, String methodName) {
        try {
            return new QualifiedType(c.getDeclaredMethod(methodName));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    static QualifiedType qtype(Class<?> c) {
            return new QualifiedType(c,false);
    }

    QualifiedType listStringf = qtype("listString");

    @Test
    public void test() {
        assertFalse(qtype(RawListSub.class).isAssignableTo(qtype("listString")));
        assertTrue(qtype(RawListSub.class).isAssignableTo(qtype("rawlist")));
        assertTrue(qtype(RawListSub.class).isAssignableTo(qtype("rawListSub")));
        assertFalse(qtype(RawListSub.class).isAssignableTo(qtype("stringList")));
        assertFalse(qtype(RawListSub.class).isAssignableTo(qtype("arrayListString")));
    }
}
