package net.oneandone.ejbcdiunit.relbuilder.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class CdiRelBuilder {
    Logger logger = LoggerFactory.getLogger(CdiRelBuilder.class);


    public static class NotSupportedTypeYet extends AnalyzerException {
        public static final long serialVersionUID = -7645090605110467195L;
    }

    public static class AnalyzerException extends Exception {
        public static final long serialVersionUID = -4731804312861785688L;
    }


    private final Rels.RootRel rootRel;

    public Rels.RootRel getRootRel() {
        return rootRel;
    }

    public CdiRelBuilder(Collection<Class<?>> initialClassesP) throws AnalyzerException {
        List<ClassWrapper> initialClasses = new ArrayList<>();
        for (Class<?> c : initialClassesP) {
            initialClasses.add(new ClassWrapper(c));
        }
        this.rootRel = new Rels.RootRel(initialClasses);
        RelationFactory relFactory = new RelationFactory();
        for (ClassWrapper clazz : initialClasses) {
            relFactory.createBeanFromClass(this.rootRel, clazz);
        }
        rootRel.setBeanClasses(relFactory.beanClasses);
    }

}
