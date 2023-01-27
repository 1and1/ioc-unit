package iocunit.ejbresource;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;

import com.oneandone.iocunit.ejb.PersistenceContextQualifier;
import com.oneandone.iocunit.jpa.XmlLessInitializingPersistenceFactory;

import iocunit.ejbresource.em.PUQual1;
import iocunit.ejbresource.em.PUQual2;

/**
 * @author aschoerk
 */
public class EmTestResources {
    public static class Datasource1PersistenceFactory extends XmlLessInitializingPersistenceFactory {

        @Override
        protected String getFilenamePrefix() {
            return "ds1";
        }

        @Override
        public String getEntityBeanRegex() {
            return ".*\\.pu1\\..*1";
        }

        @Produces
        @PUQual1
        @Override
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }

        @Produces
        @PersistenceContextQualifier(name = "pu1")
        public EntityManager produceEntityManagerQualified() {
            return super.produceEntityManager();
        }
    }

    public static class Datasource2PersistenceFactory extends XmlLessInitializingPersistenceFactory {

        @Override
        protected String getFilenamePrefix() {
            return "ds2";
        }

        @Override
        public String getEntityBeanRegex() {
            return ".*\\.pu2\\..*2";
        }

        @Produces
        @PUQual2
        @Override
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }

        @Produces
        @PersistenceContextQualifier(name = "pu2")
        public EntityManager produceEntityManagerQualified() {
            return super.produceEntityManager();
        }
    }

    public static class Datasource3PersistenceFactory extends XmlLessInitializingPersistenceFactory {

        @Override
        protected String getFilenamePrefix() {
            return "ds3";
        }

        @Override
        public String getEntityBeanRegex() {
            return ".*\\.Entity3";
        }

        @Produces
        @Override
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }

        @Produces
        @PersistenceContextQualifier(name = "pu3")
        public EntityManager produceEntityManagerQualified() {
            return super.produceEntityManager();
        }
    }

}
