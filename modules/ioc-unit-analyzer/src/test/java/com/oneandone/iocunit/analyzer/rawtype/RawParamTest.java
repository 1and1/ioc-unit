package com.oneandone.iocunit.analyzer.rawtype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.BaseTest;
import com.oneandone.iocunit.analyzer.QualifiedType;
import com.oneandone.iocunit.analyzer.rawtype.producers.ParameterizedProducer;
import com.oneandone.iocunit.analyzer.rawtype.producers.RawListSubProducer;
import com.oneandone.iocunit.analyzer.rawtype.producers.RawProducer;
import com.oneandone.iocunit.analyzer.rawtype.producers.StringListProducer;
import com.oneandone.iocunit.analyzer.rawtype.types.RawListSub;
import com.oneandone.iocunit.analyzer.rawtype.types.StringList;

/**
 * @author aschoerk
 */
public class RawParamTest extends BaseTest {

    void testAndCheckProducer(Class<?> testClass, Class<?> producerClass, Class<?>... elseClasses) {
        createTest(testClass, producerClass);
        assertEquals(2 + elseClasses.length, toBeStarted.size());
        Set<Class<? extends Object>> compareToBeStarted = new HashSet<>();
        compareToBeStarted.add(testClass);
        compareToBeStarted.add(producerClass);
        for (Class<?> c : elseClasses) {
            compareToBeStarted.add(c);
        }
        assertEquals(toBeStarted.stream().collect(Collectors.toSet()), compareToBeStarted);
    }

    @Test
    public void analyzeRawListContainerRawListSubExcluded() {
        testAndCheckProducer(RawListContainerRawStringListIncluded.class, RawProducer.class, StringList.class);
    }

    @Test
    public void analyzeRawListContainerRawProducerExcluded() {
        testAndCheckProducer(RawListContainerRawProducerExcluded.class, RawListSubProducer.class, StringList.class);
    }

    @Test
    public void directTest() throws NoSuchFieldException {
        QualifiedType q = new QualifiedType(RawListSub.class, true);
        QualifiedType i = new QualifiedType(ParameterizedListContainer.class.getDeclaredField("list"));
        assertFalse(q.isAssignableTo(i));
    }

    @Test
    public void analyzeParameterizedListContainerStringListExcluded() {
        testAndCheckProducer(ParameterizedListContainer.class, ParameterizedProducer.class);
    }

    @Test
    public void analyzeStringListContainerNotItself() {
        testAndCheckProducer(StringListContainerNotItself.class, StringListProducer.class);
    }

    @Test
    public void analyzeStringListContainerNoProducer() {
        testAndCheckProducer(StringListContainerNoProducer.class, StringList.class);
    }

    @Test
    public void analyzeRawListSubContainerNotItself() {
        testAndCheckProducer(RawListSubContainerNotItself.class, RawListSubProducer.class);
    }

    @Test
    public void analyzeRawListSubContainerNoProducer() {
        testAndCheckProducer(RawListSubContainerNoProducer.class, RawListSub.class);
    }
}

