package com.oneandone.iocunitejb.bnetza4220b_5;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by aschoerk on 28.06.17.
 */
@Entity
public class SVersionsEntity {
    @Id
    @GeneratedValue
    private Long notUsedId;

    private Integer id;
    private Integer revision;
    private Integer revtype;
    private Integer externalSubscriberId;
    private String simserial;
    private boolean simserialMod;
    private String state;
    private boolean stateMod;
    private String providerShortName;


    public static final class SVersionsEntityBuilder {
        private Integer id;
        private Integer revision;
        private Integer revtype;
        private Integer externalSubscriberId;
        private String simserial = "";
        private boolean simserialMod = false;
        private String state;
        private boolean stateMod = false;
        private String providerShortName;

        public SVersionsEntityBuilder() {}

        public static SVersionsEntityBuilder aSVersionsEntity() { return new SVersionsEntityBuilder(); }

        public SVersionsEntityBuilder withId(Integer id) {
            this.id = id;
            return this;
        }

        public SVersionsEntityBuilder withRevisionTimeTen(Integer revision) {
            this.revision = revision * 10;
            return this;
        }

        public SVersionsEntityBuilder withRevtype(Integer revtype) {
            this.revtype = revtype;
            return this;
        }

        public SVersionsEntityBuilder withExternalSubscriberId(Integer externalSubscriberId) {
            this.externalSubscriberId = externalSubscriberId;
            return this;
        }

        public SVersionsEntityBuilder withSimserial(String simserial) {
            this.simserial = simserial;
            return this;
        }

        public SVersionsEntityBuilder withSimserialMod(boolean simserialMod) {
            this.simserialMod = simserialMod;
            return this;
        }

        public SVersionsEntityBuilder withState(String state) {
            this.state = state;
            return this;
        }

        public SVersionsEntityBuilder withStateMod(boolean stateMod) {
            this.stateMod = stateMod;
            return this;
        }

        public SVersionsEntityBuilder withProviderShortName(String providerShortName) {
            this.providerShortName = providerShortName;
            return this;
        }

        public SVersionsEntityBuilder but() { return aSVersionsEntity().withId(id).withRevisionTimeTen(revision).withRevtype(revtype).withExternalSubscriberId(externalSubscriberId).withSimserial(simserial).withSimserialMod(simserialMod).withState(state).withStateMod(stateMod).withProviderShortName(providerShortName); }

        public SVersionsEntity build() {
            SVersionsEntity sVersionsEntity = new SVersionsEntity();
            sVersionsEntity.externalSubscriberId = this.externalSubscriberId;
            sVersionsEntity.stateMod = this.stateMod;
            sVersionsEntity.state = this.state;
            sVersionsEntity.revision = this.revision;
            sVersionsEntity.revtype = this.revtype;
            sVersionsEntity.id = this.id;
            sVersionsEntity.simserial = this.simserial;
            sVersionsEntity.providerShortName = this.providerShortName;
            sVersionsEntity.simserialMod = this.simserialMod;
            return sVersionsEntity;
        }
    }
}
