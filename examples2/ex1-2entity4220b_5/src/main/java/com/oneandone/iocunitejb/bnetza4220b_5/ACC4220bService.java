package com.oneandone.iocunitejb.bnetza4220b_5;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author aschoerk
 */
@Stateless
public class ACC4220bService {
    @Inject
    EntityManager entityManager;

    public List intermediateQuery() {
        entityManager.createNativeQuery("drop table if exists sv1 ").executeUpdate();
        Query q = entityManager.createNativeQuery("create table sv1 "
                                                  + "as "
                                                  + "select distinct "
                                                  + "   s1.revision,"   // !!! changed
                                                  + "   s1.simserial,"
                                                  + "   s1.externalSubscriberId,"
                                                  + "   s1.state "
                                                  + "from sversionsentity s1 "
                                                  + "where (s1.simserialmod or s1.state in ('CANCELLED')) "

        );
        q.executeUpdate();
        q = entityManager.createNativeQuery("select * from sv1 order by revision");
        Object res = q.getResultList().stream().collect(Collectors.toList());
        return (List) res;
    }

    public List resultQuery() {
        entityManager.createNativeQuery("drop table if exists tmp_result ").executeUpdate();
        Query q;
        q = entityManager.createNativeQuery("create table tmp_result "
                                            + "as select distinct "
                                            + "          tmp.id as simserial,"
                                            + "          tmp.refid as externalSubscriberId,"
                                            + "          tmp.validStartDate,"
                                            + "          tmp.validEndDate," // ","
                                            + "          case when tmp.validEndDate is null "
                                            + "               then tmp.ValidStartDate "
                                            + "               else tmp.ValidEndDate"
                                            + "          end as modificationDate"
                                            + "   from ("
                                            + "       select sv1.simserial as id, "
                                            + "              sv1.externalSubscriberId as refid, "
                                            + "              sv1.revision as validStartDate,"
                                            + "              ("
                                            + "                 select min(revision) - 1"
                                            + "                 from sv1 sv2"
                                            + "                 where sv2.revision > sv1.revision"
                                            + "                     and sv1.externalSubscriberId = sv2.externalSubscriberId"
                                            + "                 and ("
                                            + "                    sv1.simserial             <> sv2.simserial"
                                            + "                    or sv2.state                  = 'CANCELLED'"
                                            + "                 )"
                                            + "              ) as ValidEndDate"
                                            + "       from sv1 "
                                            + "       where sv1.state NOT IN ('CANCELLED')"
                                            + "    ) as tmp"
                                            + "    join subscriber mss "
                                            + "       on tmp.refid = mss.externalSubscriberId"
                                            + "    where mss.state <> 'DISMANTLED' OR mss.sim_serial <> tmp.id"
                                            + "");
        q.executeUpdate();
        q = entityManager.createNativeQuery("select * from tmp_result order by modificationDate");
        Object res = q.getResultList().stream().collect(Collectors.toList());
        return (List) res;
    }

    public void saveRecord(SVersionsEntity.SVersionsEntityBuilder sVersionsEntityBuilder) {
        entityManager.persist(sVersionsEntityBuilder.build());
    }

    public void saveSubscriber(int id, String state, String sim_serial) {

        List<Subscriber> results = entityManager.createQuery(
                "select s from Subscriber s where externalSubscriberId = :id", Subscriber.class)
                .setParameter("id", id)
                .getResultList();
        if(results.size() == 1) {
            final Subscriber subscriber1 = results.get(0);
            subscriber1.state = state;
            subscriber1.sim_serial = sim_serial;
            entityManager.merge(subscriber1);
        }
        else {
            Subscriber subscriber = new Subscriber(id, state, sim_serial);
            entityManager.persist(subscriber);
        }

    }


}
