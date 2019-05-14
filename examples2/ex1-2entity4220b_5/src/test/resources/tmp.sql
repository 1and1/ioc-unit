select distinct

       tmp.Id                                       as simserial

     , 'EBI IS-910'                                 as Source

     , tmp.RefId                                    as external_subscriber_id

     , 'Mapping'                                    as RefTable

     , tmp.ValidStartDate

     , tmp.ValidEndDate

     , isnull(tmp.ValidEndDate, tmp.ValidStartDate) as modificationDate

     , si.imsi                                      as IMSI

into #tmp_result

from (

    select

        av1.sim_serial             as Id

      , av1.external_subscriber_id as RefId

      , av1.revtstmp               as ValidStartDate

      , (

            select min(revtstmp) - 1

            from #addsimcardsv av2

            where av2.revtstmp               > av1.revtstmp

              and av1.external_subscriber_id = av2.external_subscriber_id

              and (

                  av1.sim_serial             <> av2.sim_serial

               or av2._rev_type              = 2

              )

        )                          as ValidEndDate

    from #addsimcardsv av1

    where av1._rev_type <> 2

      and not exists (

        select sim_serial

        from #addsimcardsv av3

        where av3.sim_serial = av1.sim_serial

          and av3.revtstmp   < av1.revtstmp

    )

    union

    select

        sv1.sim_serial as Id

      , sv1.external_subscriber_id

      , sv1.revtstmp   as ValidStartDate

      , (

            select min(revtstmp) - 1

            from #subscribersv sv2

            where sv2.revtstmp               > sv1.revtstmp

              and sv1.external_subscriber_id = sv2.external_subscriber_id

              and (

                  sv1.sim_serial             <> sv2.sim_serial

               or sv2.state                  = 'CANCELLED'

              )

        )              as ValidEndDate

    from #subscribersv sv1

    where sv1.state <> 'CANCELLED'

      and sv1.sim_serial in ('7010905529836', '7010905671265', '7010805027360', '7011105357473')

)                                         as tmp

join ods_mobileservices_simnumber.simcard si

    on si.serial = tmp.Id

join ods_mobileprocess_mss.subscribers    mss

    on tmp.RefId = mss.external_subscriber_id

where mss.state <> 'DISMANTLED'

  and si.imsi is not null

order by RefId

       , ValidStartDate;



select *

from cl_dom_mobile_access.ref_simserial_subscriber

where simserial in ('7010905529836', '7010905671265', '7010805027360', '7011105357473');



select *

from #tmp_result

where simserial in ('7010905529836', '7010905671265', '7010805027360', '7011105357473');



select *

from #addsimcardsv

where sim_serial in ('7010905529836', '7010905671265', '7010805027360', '7011105357473');



select *

from #subscribersv

where sim_serial in ('7010905529836', '7010905671265', '7010805027360', '7011105357473');

