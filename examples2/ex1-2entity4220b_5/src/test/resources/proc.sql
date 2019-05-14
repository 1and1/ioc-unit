SET QUOTED_IDENTIFIER ON

SET ANSI_NULLS ON

GO



create   procedure cl_dom_mobile_access.load_ref_simserial_subscriber @arg_job nvarchar(255)

with execute as owner

as

begin

    set nocount on;



    declare @log_message nvarchar(max);

    declare @proc nvarchar(255);

    declare @resultcode int;



    if @arg_job is null

        set @arg_job = user_name() + '#' + cast(newid() as nvarchar(255));



    set @resultcode = 0;

    set @proc = etl.get_proc_name(@@procid);



    begin try

        exec etl.log_revision

            @proc

          , @arg_job

          , '$URL: https://svn.1and1.org/svn/ebi_mssql_access/access/trunk/access/Layers/cl-layer/cl_dom_mobile_access/Programmability/load_ref_simserial_subscriber.sql $'

          , '$Revision: 4987 $'

          , '$Author: butiryaki $'

          , '$Date: 2019-04-08 12:02:32 +0200 (Mon, 08 Apr 2019) $';



        set @log_message = N'STARTED JOB ' + @arg_job;



        exec etl.log_event @proc, @arg_job, @log_message;



        drop table if exists #subscribersv;

        drop table if exists #addsimcardsv;



        declare @max_modification_date bigint = (

                    select isnull(max(modification_date), 0)

                    from cl_dom_mobile_access.ref_simserial_subscriber

                ); -- ermittlung der max(modification_date), die in die cl Tabelle beladen wurde



        set @log_message = N'Determine all simcard changes or their deactivation of subscribers that were changed...';



        exec etl.log_event @proc, @arg_job, @log_message;



        select distinct

               r2.revtstmp

             , sv1._revision

             , sv1.sim_serial

             , sv1.external_subscriber_id

             , sv1.state

        into #subscribersv

        from ods_mobileprocess_mss.subscribers_versions sv1 --sv1 sind alle S�tze die zu Subscribern geh�ren in dem betreffenden Interval ge�ndert wurden und eine �nderung an der simserial aufweisen oder den state gecancelled haben

        join ods_mobileprocess_mss.subscribers_versions sv2 -- sv2 sind alle S�tze, die im interessierten Interval ge�ndert wurden hinsichtlich sim karte oder k�ndigung

            on sv1.id = sv2.id

        join ods_mobileprocess_mss.revinfo              r

            on r.rev  = sv2._revision

        join ods_mobileprocess_mss.revinfo              r2 --r2.revtstmp ist der zugreh�rige epoch timestamp, der die revision ersetzt

            on r2.rev = sv1._revision

        where (

            sv1.sim_serial_mod   = 1

         or sv1.state            = 'CANCELLED'

        )

          and (

              sv2.sim_serial_mod = 1

           or sv2.state          = 'CANCELLED'

          );



        set @log_message = N'  --- > inserted ' + convert(varchar(11), @@rowcount) + N' records';



        exec etl.log_event @proc, @arg_job, @log_message;



        select distinct

               rs2.revtstmp

             , avs1._rev_type

             , avs1._revision

             , avs1.sim_serial

             , svs.external_subscriber_id

        into #addsimcardsv

        from ods_mobileprocess_mss.additional_sim_cards_versions avs1

        join ods_mobileprocess_mss.additional_sim_cards_versions avs2

            on avs1.subscriber_id = avs2.subscriber_id

        join ods_mobileprocess_mss.revinfo                       rs

            on rs.rev             = avs2._revision

        join ods_mobileprocess_mss.revinfo                       rs2

            on rs2.rev            = avs1._revision

        join ods_mobileprocess_mss.subscribers                   svs

            on svs.id             = avs1.subscriber_id;



        set @log_message = N'  --- > inserted ' + convert(varchar(11), @@rowcount) + N' records';



        exec etl.log_event @proc, @arg_job, @log_message;



        drop table if exists #tmp_result;



        set @log_message = N'Inserting the result into a temp table...';



        exec etl.log_event @proc, @arg_job, @log_message;



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

        )                                         as tmp

        join ods_mobileservices_simnumber.simcard si

            on si.serial = tmp.Id

        join ods_mobileprocess_mss.subscribers    mss

            on tmp.RefId = mss.external_subscriber_id

        where mss.state <> 'DISMANTLED'

        order by RefId

               , ValidStartDate;



        set @log_message = N'  --- > inserted ' + convert(varchar(11), @@rowcount) + N' records';



        exec etl.log_event @proc, @arg_job, @log_message;



        set @log_message = N'Update Target Table...';



        exec etl.log_event @proc, @arg_job, @log_message;



        insert into cl_dom_mobile_access.ref_simserial_subscriber (

            simserial

          , source

          , external_subscriber_id

          , ref_table

          , valid_start_date

          , valid_end_date

          , modification_date

          , imsi

          , cl_load_job_id

        )

        select

            simserial

          , Source

          , external_subscriber_id

          , RefTable

          , ValidStartDate

          , ValidEndDate

          , modificationDate

          , IMSI

          , @arg_job

        from #tmp_result

        where modificationDate > @max_modification_date;



        set @log_message = N'  --- > inserted ' + convert(varchar(11), @@rowcount) + N' records';



        exec etl.log_event @proc, @arg_job, @log_message;



        set @log_message = N'Finished Job ' + @arg_job;



        exec etl.log_event @proc, @arg_job, @log_message;



        return @resultcode;

    end try

    begin catch

        if @@trancount > 0

            rollback transaction;



        declare @error_number int;

        declare @error_severity int;

        declare @error_state int;

        declare @error_line int;

        declare @error_message nvarchar(max);



        set @error_number = error_number();

        set @error_severity = error_severity();

        set @error_state = error_state();

        set @error_line = error_line();

        set @error_message = error_message();



        exec etl.log_exception @proc, @arg_job, @error_number, @error_severity, @error_state, @error_line, @error_message;



        throw;

    end catch;

end;



GO