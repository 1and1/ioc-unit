package com.oneandone.iocunit.analyzer;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Stereotype;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.annotations.AnalyzerFlags;

/**
 * @author aschoerk
 */
public class Configuration {


    enum Phase {
        UNKNOWN,
        ANALYZING,
        MATCHING,
        FIXING,
        WARNING,
        INITIALIZING;
    }
    public boolean allowGuessing = true;
    public boolean produceInstanceInjectsByAvailables = false;
    public boolean addAllStartableBeans = false;
    public boolean addAvailableInterceptorsAndDecorators = false;
    public boolean didGuess = false;
    public List<Class<? extends Extension>> excludedExtensions;
    public List<Class<?>> initialAvailables = new ArrayList<>();

    Set<Class<? extends Annotation>> injectAnnotations = new HashSet<>();

    public Configuration(final TesterExtensionsConfigsFinder a) {
        this.testerExtensionsConfigsFinder = a;
        injectAnnotations.add(Inject.class);
        injectAnnotations.addAll(a.injectAnnotations);
        initialAvailables.addAll(a.initialAvailableClasses);
    }

    private Class<?> theTestClass;

    public Class<?> getTheTestClass() {
        return theTestClass;
    }

    private void handleAnalyzerFlags(Class<?> aClass) {
        if (aClass.equals(Object.class))
            return;
        AnalyzerFlags analyzerFlags = aClass.getAnnotation(AnalyzerFlags.class);
        handleAnalyzerFlags(aClass.getSuperclass());
        if (analyzerFlags != null) {
            this.allowGuessing = analyzerFlags.allowGuessing();
            this.produceInstanceInjectsByAvailables = analyzerFlags.produceInstanceInjectsByAvailables();
            this.addAllStartableBeans = analyzerFlags.addAllStartableBeans();
            this.addAvailableInterceptorsAndDecorators = analyzerFlags.addAvailableInterceptorsAndDecorators();
            this.excludedExtensions = Arrays.asList(analyzerFlags.excludedExtensions());
        }
    }

    public void setTheTestClass(final Class<?> testClass) {
        this.theTestClass = testClass;
        handleAnalyzerFlags(testClass);
    }

    private Phase phase = Phase.UNKNOWN;

    public void setPhase(final Phase phase) {
        this.phase = phase;
    }

    public TesterExtensionsConfigsFinder testerExtensionsConfigsFinder = null;

    Logger logger = LoggerFactory.getLogger(Configuration.class);

    // used to separate classes which might have configuring annotations and might have priority over
    // each other in case of injections.
    private Set<Class<?>> testClasses = new HashSet<>();

    private Set<Class<?>> enabledAlternatives = new HashSet<>();

    // candidates to be started.
    // The testclass, all classes found in @TestClasses
    // classes usable to fill injects in beansToBeStarted.
    private List<Class<?>> beansToBeStarted = new ArrayList<>(); // these beans must be given to CDI to be started

    // the testclass itself and beans defined by @TestClasses or @SutClasses, or by services
    private List<Class<?>> obligatory = new ArrayList<>();

    // classes defined to be excluded from configuration. Done by servcices or @ExcludedClasses
    private Set<Class<?>> excluded = new HashSet<>();

    private Set<URL> testClassPaths = new HashSet<>();

    public Set<URL> getTestClassPaths() {
        return testClassPaths;
    }

    public void addCandidate(Class<?> c) {
        if(!candidates.contains(c)) {
            candidates.add(c);
        }
        else {
            if(phase != Phase.ANALYZING && phase != Phase.INITIALIZING) {
                logger.debug("candidates already contains {}", c);
            }
        }
    }

    public boolean isCandidate(Class<?> c) {
        return candidates.contains(c);
    }

    public void moveCandidates(Collection<Class<?>> dest) {
        dest.addAll(candidates);
        candidates.clear();
    }

    public boolean emptyCandidates() {
        return candidates.isEmpty();
    }


    // previously available classes to be added to the startconfiguration
    private List<Class<?>> candidates = new ArrayList<>();

    // classes available to be added to configuration
    private Set<Class<?>> available = new HashSet<>();

    // abstract sut-classes or sut-interfaces making availables to candidates
    private Set<Class<?>> candidateSigns = new HashSet<>();

    private boolean availablesChanged = false;

    // producers available from obligatory and candidates
    private ProducerMap producerMap = new ProducerMap(this, "Obligatory");

    // producers found in availables
    private ProducerMap availableProducerMap = new ProducerMap(this, "Available");

    // producers created by enabled Alternatives
    private ProducerMap alternativesProducerMap = new ProducerMap(this, "Alternatives");

    // injects found in obligatory and candidates
    private Set<QualifiedType> injects = new HashSet<QualifiedType>();

    // Instance-injects found in obligatory and candidates
    private Set<QualifiedType> instanceInjects = new HashSet<QualifiedType>();

    // injects found in obligatory and candidates which are handled
    private Set<QualifiedType> handledInjects = new HashSet<QualifiedType>();

    private HashMultiMap<Class<?>, QualifiedType> classes2Injects = new HashMultiMap<>();

    private ElseClasses elseClasses = new ElseClasses();

    /**
     * signify clazz as sutClass
     *
     * @param clazz
     * @return
     */
    Configuration sutClass(Class<?> clazz) {
        testClasses.remove(clazz);
        return this;
    }


    /**
     * signify clazz as testclass
     *
     * @param clazz
     * @return
     */
    Configuration testClass(Class<?> clazz) {
        testClasses.add(clazz);
        final CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource != null)
            testClassPaths.add(codeSource.getLocation());

        return this;
    }

    public Configuration testClassCandidates(final Collection<Class<?>> classes) {
        if(classes != null) {
            for (Class<?> c : classes)
                testClass(c)
                        .candidate(c);
        }
        return this;
    }

    /**
     * signify clazz as to be included in starting configuration
     *
     * @param clazz
     * @return
     */
    Configuration obligatory(Class<?> clazz) {
        obligatory.add(clazz);
        available.add(clazz);
        return this;
    }

    /**
     * signify class as available for starting configuration
     *
     * @param clazz
     * @return
     */
    Configuration available(Class<?> clazz) {
        if (!available.contains(clazz)) {
            available.add(clazz);
            availablesChanged = true;
        }
        return this;
    }


    public Configuration candidate(final Class<?> c) {
        addCandidate(c);
        return this;
    }

    public Configuration enabledAlternative(final Class<?> c) {
        if(c.getAnnotation(Alternative.class) == null || c.isAnnotation() && c.getAnnotation(Stereotype.class) == null) {
            logger.error("Invalid enabled Alternative {}", c.getName());
        }
        if(c.isAnnotation() && c.getAnnotation(Stereotype.class) != null) {
            elseClasses.foundAlternativeStereotypes.add(c);
        }
        else {
            enabledAlternatives.add(c);
        }
        return this;
    }

    public Configuration excluded(final Class<?> c) {
        excluded.add(c);
        return this;
    }

    public boolean isExcluded(final Class<?> c) {
        return excluded.contains(c);
    }


    public Configuration tobeStarted(final Class<?> c) {
        addToBeStarted(c);
        return this;
    }

    public boolean isToBeStarted(Class<?> c) {
        return beansToBeStarted.contains(c);
    }

    public void setTesterExtensionsConfigsFinder(final TesterExtensionsConfigsFinder testerExtensionsConfigsFinderP) {
        this.testerExtensionsConfigsFinder = testerExtensionsConfigsFinderP;
    }

    public boolean isTestClass(final Class c) {
        return testClasses.contains(c);
    }

    public Configuration inject(final QualifiedType i) {
        logger.trace("Adding Inject {}", i);
        injects.add(i);
        if (i.isInstance() && produceInstanceInjectsByAvailables) {
            logger.info("Found Instance-Inject {} will be filled by all found availables.",i);
            instanceInjects.add(i);
        } else {
            injects.add(i);
        }
        return this;
    }

    Configuration elseClass(Class<?> c) {
        elseClasses.elseClass(c);
        return this;
    }

    public boolean isAvailable(final Class<? extends Annotation> annotationType) {
        return available.contains(annotationType);
    }

    public ProducerMap getProducerMap() {
        return producerMap;
    }

    public ProducerMap getAvailableProducerMap() {
        return availableProducerMap;
    }

    public ProducerMap getAlternativesProducerMap() {
        return alternativesProducerMap;
    }

    public Set<QualifiedType> getInjects() {
        return injects;
    }

    public Set<Class<?>> getExcludedClasses() {
        return excluded;
    }

    public boolean isActiveAlternativeStereoType(final Annotation c) {
        logger.trace("Searching for alternative Stereotype {}", c);
        for (Class stereoType : elseClasses.foundAlternativeStereotypes) {
            if(stereoType.getName().equals(c.annotationType().getName())) {
                logger.trace("Search found alternative Stereotype {}", c);
                return true;
            }
        }
        return false;
    }

    public boolean isEnabledAlternative(final Class declaringClass) {
        return enabledAlternatives.contains(declaringClass);
    }


    public void addToBeStarted(Class<?> c) {
        if(beansToBeStarted.contains(c)) {
            logger.warn("Trying to add {} testerExtensionsConfigsFinder second time", c);
        }
        else {
            beansToBeStarted.add(c);
            obligatory.add(c);
        }
    }

    public boolean isSuTClass(final Class declaringClass) {
        return !testClasses.contains(declaringClass);
    }

    void injectHandled(QualifiedType inject, final QualifiedType producingType) {
        injects.remove(inject);
        handledInjects.add(inject);
        if(producingType != null) {
            classes2Injects.put(producingType.getDeclaringClass(), inject);
        }
    }

    Set<QualifiedType> getInjectsForClass(Class<?> key) {
        return classes2Injects.get(key);
    }


    public ElseClasses getElseClasses() {
        return elseClasses;
    }

    public Set<Class<?>> getEnabledAlternatives() {
        return enabledAlternatives;
    }

    public List<Class<?>> getObligatory() {
        return obligatory;
    }

    public Set<QualifiedType> getInstanceInjects() {
        return instanceInjects;
    }

    public boolean isAvailablesChanged() {
        return availablesChanged;
    }

    public void setAvailablesChanged(final boolean availablesChanged) {
        this.availablesChanged = availablesChanged;
    }

    public Set<Class<?>> getCandidateSigns() {
        return candidateSigns;
    }

    public Set<Class<?>> getAvailable() {
        return available;
    }

}
