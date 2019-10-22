package com.oneandone.iocunitejb.bnetza4220b_5;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.ejb.XmlLessPersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunitejb.bnetza4220b_5.SVersionsEntity.SVersionsEntityBuilder;


/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@SutClasses({XmlLessPersistenceFactory.class})
public class ACC4220bTest {

    public static final String ACTIVE = "ACTIVE";
    public static final String DISMANTLED = "DISMANTLED";
    public static final String CANCELLED = "CANCELLED";
    public static final String PRECANCELLED = "PRECANCELLED";
    public static final String CREATED = "CREATED";
    @Inject
    EntityManager entityManager;

    @Inject
    ACC4220bService s;

    @Inject
    UserTransaction userTransaction;

    @Inject
    PersistenceFactory persistenceFactory;


    @BeforeEach
    void beforeEach() throws SystemException, NotSupportedException {
        userTransaction.begin();
        userTransaction.rollback();
    }

    @AfterEach
    void afterEach() throws SystemException {
        final int status = userTransaction.getStatus();
        if(status != Status.STATUS_NO_TRANSACTION && status != Status.STATUS_ROLLEDBACK
           && status != Status.STATUS_COMMITTED) {
            userTransaction.rollback();
        }
    }

    @Nested
    class SP004Example {
        SVersionsEntityBuilder recordBuilder = new SVersionsEntityBuilder()
                .withId(1)
                .withExternalSubscriberId(101)
                .withSimserial("")
                .withRevisionTimeTen(192353140)
                .withRevtype(0)
                .withState(CREATED)
                .withStateMod(true);

        @BeforeEach
        void initSimpleCard() {
            s.saveRecord(recordBuilder);
            s.saveRecord(recordBuilder
                    .withRevisionTimeTen(192354360)
            );
            s.saveRecord(recordBuilder
                    .withRevisionTimeTen(192354841)
                    .withSimserial("1")
                    .withSimserialMod(true)
                    .withState(ACTIVE)
                    .withStateMod(true)
            );

            s.saveSubscriber(101, CREATED, null);
        }

        @Test
        public void thenQueryDeliversOneRecord() {

            final List list = s.intermediateQuery();
            Assertions.assertEquals(1, list.size());
            final List result = s.resultQuery();
            Assertions.assertEquals(1, result.size());
        }
    }

    @Nested
    class WhenAccountIsCreated {
        SVersionsEntityBuilder recordBuilder = new SVersionsEntityBuilder()
                .withId(1)
                .withExternalSubscriberId(101)
                .withSimserial("")
                .withRevisionTimeTen(100)
                .withRevtype(0)
                .withState(CREATED)
                .withStateMod(true);

        @BeforeEach
        void initSimpleCard() {
            s.saveRecord(recordBuilder);
            s.saveSubscriber(101, CREATED, null);
        }

        @Test
        public void thenQueryDeliversNoRecord() {

            Assertions.assertEquals(0, s.intermediateQuery().size());
            Assertions.assertEquals(0, s.resultQuery().size());
        }


        @Nested
        class WhenNormalCardIsCreated {
            @BeforeEach
            void initSimpleCard() {

                s.saveRecord(recordBuilder
                        .withRevisionTimeTen(101)
                        .withSimserial("1")
                        .withSimserialMod(true)
                        .withState(ACTIVE)
                        .withStateMod(true)
                );

                s.saveRecord(recordBuilder.but()
                        .withRevisionTimeTen(102)
                        .withSimserialMod(false)
                        .withStateMod(false)
                );
                s.saveSubscriber(101, ACTIVE, "1");
            }

            @Test
            public void thenQueryDelivers1RecordWithoutValidEndDate() {
                try {
                    Assertions.assertEquals(1, s.intermediateQuery().size());
                    List rl = s.resultQuery();
                    assertEquals(1, rl.size());
                    new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(null)
                            .withModificationDate(1010)
                            .build().equalsArray((Object[]) rl.get(0));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Nested
            class WhenMeanwhileSwapped {
                @BeforeEach
                void initSimpleCard() {

                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(103)
                            .withSimserial("2")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(false)
                    );
                }

                @Test
                public void thenQueryDelivers2Records() {
                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(2, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(1029)
                            .withModificationDate(1029)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(101)
                            .withValidStartDate(1030)
                            .withValidEndDate(null)
                            .withModificationDate(1030)
                            .build().foundInList(rl));
                }

            }

            @Nested
            class WhenMeanwhileDismantled {
                @BeforeEach
                void initSimpleCard() {

                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(106)
                            .withSimserial("1")
                            .withSimserialMod(false)
                            .withState(DISMANTLED)
                            .withStateMod(true)
                    );
                    s.saveSubscriber(101, DISMANTLED, "1");

                    s.saveRecord(recordBuilder
                            .withId(2)
                            .withExternalSubscriberId(102)
                            .withRevisionTimeTen(103)
                            .withSimserial("")
                            .withSimserialMod(false)
                            .withState(CREATED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(104)
                            .withSimserial("2")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(105)
                            .withSimserial("2")
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(106)
                            .withSimserial("1")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveSubscriber(102, ACTIVE, "1");

                }

                @Test
                public void thenQueryDelivers2RecordsForNonDismantled() {

                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(2, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(102)
                            .withValidStartDate(1060)
                            .withValidEndDate(null)
                            .withModificationDate(1060)
                            .build().foundInList(rl));

                }

                @Test
                public void canCancelAfterGettingSimcardFromDismantled() {
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(107)
                            .withSimserial("1")
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveSubscriber(102, CANCELLED, "1");
                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(2, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(102)
                            .withValidStartDate(1060)
                            .withValidEndDate(1069)
                            .withModificationDate(1069)
                            .build().foundInList(rl));

                }

            }

            @Nested
            class WhenManyChangesWithMnpIntern {
                @BeforeEach
                void initSimpleCard() {

                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(106)
                            .withSimserial("3")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(false)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(107)
                            .withState(CANCELLED)
                            .withStateMod(true)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(108)
                            .withState(ACTIVE)
                            .withStateMod(true)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(109)
                            .withState(CANCELLED)
                            .withStateMod(true)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(110)
                            .withState(ACTIVE)
                            .withStateMod(true)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(111)
                            .withSimserial("4")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(false)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(112)
                            .withSimserial("4")
                            .withSimserialMod(true)
                            .withState(DISMANTLED)
                            .withStateMod(false)
                    );
                    s.saveSubscriber(101, DISMANTLED, "4");

                    s.saveRecord(recordBuilder
                            .withId(2)
                            .withExternalSubscriberId(102)
                            .withRevisionTimeTen(103)
                            .withSimserial("")
                            .withSimserialMod(false)
                            .withState(CREATED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(104)
                            .withSimserial("2")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(105)
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(106)
                            .withState(CANCELLED)
                            .withStateMod(false));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(112)
                            .withSimserial("4")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveSubscriber(102, ACTIVE, "4");

                }

                @Test
                public void thenQueryDelivers6RecordsBothSubscribersIncludingDismantled() {

                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(6, rl.size());
                    checkBase(rl);
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("4")
                            .withRefId(102)
                            .withValidStartDate(1120)
                            .withValidEndDate(null)
                            .withModificationDate(1120)
                            .build().foundInList(rl));

                }

                private void checkBase(final List rl) {
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(1059)
                            .withModificationDate(1059)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(101)
                            .withValidStartDate(1060)
                            .withValidEndDate(1069)
                            .withModificationDate(1069)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(101)
                            .withValidStartDate(1080)
                            .withValidEndDate(1089)
                            .withModificationDate(1089)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(101)
                            .withValidStartDate(1100)
                            .withValidEndDate(1109)
                            .withModificationDate(1109)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                }

                @Test
                public void canCancelAfterGettingSimcardFromDismantled() {
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(113)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveSubscriber(102, CANCELLED, "4");
                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(6, rl.size());
                    checkBase(rl);
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("4")
                            .withRefId(102)
                            .withValidStartDate(1120)
                            .withValidEndDate(1129)
                            .withModificationDate(1129)
                            .build().foundInList(rl));

                }

                @Test
                public void canSwapAfterGettingSimcardFromDismantled() {
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(113)
                            .withSimserial("5")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveSubscriber(102, ACTIVE, "5");
                    final List list = s.intermediateQuery();
                    List rl = s.resultQuery();
                    assertEquals(7, rl.size());
                    checkBase(rl);
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("4")
                            .withRefId(102)
                            .withValidStartDate(1120)
                            .withValidEndDate(1129)
                            .withModificationDate(1129)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("5")
                            .withRefId(102)
                            .withValidStartDate(1130)
                            .withValidEndDate(null)
                            .withModificationDate(1130)
                            .build().foundInList(rl));
                }

            }


            @Nested
            class WhenAnotherWithSwapGetsDismantledLikeMnpIntern {
                @BeforeEach
                void initSimpleCard() {

                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(106)
                            .withSimserial("3")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(false)
                    );
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(107)
                            .withSimserial("3")
                            .withSimserialMod(false)
                            .withState(DISMANTLED)
                            .withStateMod(true)
                    );
                    s.saveSubscriber(101, DISMANTLED, "3");

                    s.saveRecord(recordBuilder
                            .withId(2)
                            .withExternalSubscriberId(102)
                            .withRevisionTimeTen(103)
                            .withSimserial("")
                            .withSimserialMod(false)
                            .withState(CREATED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(104)
                            .withSimserial("2")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(105)
                            .withSimserial("2")
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(107)
                            .withSimserial("3")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(true));
                    s.saveSubscriber(102, ACTIVE, "3");

                }

                @Test
                public void thenQueryDelivers3RecordsBothSubscribersIncludingDismantled() {

                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(3, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(1059)
                            .withModificationDate(1059)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(102)
                            .withValidStartDate(1070)
                            .withValidEndDate(null)
                            .withModificationDate(1070)
                            .build().foundInList(rl));

                }

                @Test
                public void canCancelAfterGettingSimcardFromDismantled() {
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(108)
                            .withSimserial("3")
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true));
                    s.saveSubscriber(102, CANCELLED, "3");
                    final List list = s.intermediateQuery();
                    // Assertions.assertEquals(2, list.size());
                    List rl = s.resultQuery();
                    assertEquals(3, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(1059)
                            .withModificationDate(1059)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(102)
                            .withValidStartDate(1070)
                            .withValidEndDate(1079)
                            .withModificationDate(1079)
                            .build().foundInList(rl));

                }

                @Test
                public void canSwapAfterGettingSimcardFromDismantled() {
                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(108)
                            .withSimserial("4")
                            .withSimserialMod(true)
                            .withState(ACTIVE)
                            .withStateMod(false));
                    s.saveSubscriber(102, ACTIVE, "4");
                    final List list = s.intermediateQuery();
                    List rl = s.resultQuery();
                    assertEquals(4, rl.size());
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("1")
                            .withRefId(101)
                            .withValidStartDate(1010)
                            .withValidEndDate(1059)
                            .withModificationDate(1059)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("2")
                            .withRefId(102)
                            .withValidStartDate(1040)
                            .withValidEndDate(1049)
                            .withModificationDate(1049)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("3")
                            .withRefId(102)
                            .withValidStartDate(1070)
                            .withValidEndDate(1079)
                            .withModificationDate(1079)
                            .build().foundInList(rl));
                    assertTrue(new DialogikaResult.DialogikaResultBuilder()
                            .withId("4")
                            .withRefId(102)
                            .withValidStartDate(1080)
                            .withValidEndDate(null)
                            .withModificationDate(1080)
                            .build().foundInList(rl));

                }

            }


            @Nested
            class WhenDirectlyCanceled {
                @BeforeEach
                void initSimpleCard() {

                    s.saveRecord(recordBuilder
                            .withRevisionTimeTen(103)
                            .withSimserial("1")
                            .withSimserialMod(false)
                            .withState(CANCELLED)
                            .withStateMod(true)
                    );
                    s.saveSubscriber(101, CANCELLED, "1");
                }

                @Test
                public void thenQueryDelivers1RecordWithValidEndDate() {
                    try {
                        final List list = s.intermediateQuery();
                        // Assertions.assertEquals(2, list.size());
                        List rl = s.resultQuery();
                        assertEquals(1, rl.size());
                        new DialogikaResult.DialogikaResultBuilder()
                                .withId("1")
                                .withRefId(101)
                                .withValidStartDate(1010)
                                .withValidEndDate(1029)
                                .withModificationDate(1029)
                                .build().equalsArray((Object[]) rl.get(0));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


}
