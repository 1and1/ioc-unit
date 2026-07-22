# weld-starter

The modules here start the Weld version used to run ioc-unit tests in a standalone (non-container)
Weld SE engine. `javaee-`/`jakarta-` API artifacts are normally `provided` (a real container
supplies them), but nothing plays that role for a standalone test JVM — so the starter module
includes everything actually needed to boot Weld SE as a real (`compile`-scope) dependency, so it
flows transitively into any consumer that depends on it.

## Modules

* **weld-starter-base**: version-independent glue code shared by all weld starters. Defines the
  `com.oneandone.cdi.weldstarter.spi.WeldStarter` SPI interface that `ioc-unit`'s core depends on
  — see "Advanced: the WeldStarter SPI" below.
* **weld-starter**: the only starter currently maintained. Boots Weld SE (Jakarta EE 10
  namespace) for tests. Declares `org.jboss.weld:weld-se-core`, `weld-core-impl`, `weld-web`,
  `weld-jta` at `compile` scope (version driven by the `weld-engine.version` property in its own
  pom), plus explicit `weld-spi`/`weld-api` at `compile` scope (version driven by the
  `weld-spi-api.version` property — both are `provided` in `ioc-unit`/`ioc-unit-contexts`/
  `ioc-unit-resteasy`/`ioc-unit-ejb` for real-container use, but nothing else supplies them for a
  standalone Weld SE bootstrap — see the root
  [README's dependency-management section](../README.md#dependency-management-bom-first)).
  It also declares `jakarta.el:jakarta.el-api:5.0.1` and `org.glassfish:jakarta.el:4.0.2` at
  `compile` scope: `weld-web`'s `WeldWebModule` eagerly loads `jakarta.el.ExpressionFactory`
  during Weld container startup, regardless of whether the test itself uses EL/JSP, so this must
  live here — not in `ioc-unit-validate` — to reach *every* IocUnit test that depends on
  `weld-starter`, whether or not `ioc-unit-validate` is also used.

> Older `weld1-starter`/`weld2-starter`/`weld3-starter` modules (targeting `javaee-api` 7.0/8.0)
> have been removed from this reactor; only `weld-starter` (Jakarta EE namespace) remains.

## Upgrading the Weld version (e.g. for a new target WildFly release)

Weld is not part of the WildFly BOM (confirmed: none of the imported WildFly BOMs mention
`org.jboss.weld` at all) — it's a container-internal implementation detail, so this project pins
its own Weld version independently. `weld-starter/pom.xml` drives this via exactly two
properties:

* `weld-spi-api.version` — for `weld-spi`/`weld-api` (these two are always bundled by WildFly at
  the same version as each other).
* `weld-engine.version` — for `weld-se-core`/`weld-core-impl`/`weld-web`/`weld-jta` (a separate
  release train from the SPI/API pair — confirmed for WildFly 39.0.1.Final, which bundles
  `weld-spi`/`weld-api` at `5.0.SP3` but `weld-core-impl`/`weld-web`/`weld-jta` at `5.1.6.Final`).

To determine which Weld release to pin when targeting a new WildFly version, a maintainer needs
to reason through this chain (not extract WildFly's binary distribution — that's a one-off
verification technique, not the ongoing process):

1. **Which Jakarta EE version does the target WildFly release implement?** Check the target
   WildFly release's own documentation/release notes, or inspect which `jakarta.enterprise.cdi-api`
   version its BOM pins (e.g. WildFly 39 → `jakarta.enterprise.cdi-api:4.0.1` → CDI 4.0 → Jakarta
   EE 10).
2. **Which CDI spec version does that Jakarta EE version require?** Follow the Jakarta EE
   platform specification for that release (e.g. Jakarta EE 10 requires CDI 4.0).
3. **Which Weld release implements that CDI spec version?** Check Weld's own release notes /
   compatibility documentation (Weld is the CDI reference implementation) for a release compatible
   with the CDI spec version from step 2.
4. Update `weld-spi-api.version` and `weld-engine.version` accordingly, run the full reactor
   build, and re-verify against a real consumer project (see the root README's testing notes).

> **Known caveat (as of this writing):** bumping `weld-engine.version` from `5.1.0.Final` to
> `5.1.6.Final` — the exact version WildFly 39.0.1.Final bundles — breaks 5 tests in
> `ioc-unit-analyzer`'s `excludedclasses/*` suite with `WELD-001408` (a producer field declared on
> an abstract superclass, inherited by a concrete subclass, no longer resolves once the "real"
> producer's owning class is excluded via `@ExcludedClasses`). This appears to be a genuine Weld
> behavior change between these two patch releases, not just a version-string bump. `weld-engine.version`
> is deliberately kept at `5.1.0.Final` until this is root-caused and fixed; see
> `.idea/codeReview` for the investigation notes.

## Advanced: the `WeldStarter` SPI

`ioc-unit`'s core never compiles against a specific Weld implementation directly — it only depends
on `weld-starter-base`, which defines a plain `WeldStarter` interface (start/stop the container,
resolve beans, etc.). At runtime, `WeldSetupClass.getWeldStarter()` uses Java's standard
`ServiceLoader` mechanism to discover whichever `WeldStarter` implementation is present on the
classpath (registered via a `META-INF/services/com.oneandone.cdi.weldstarter.spi.WeldStarter`
file) — the same pattern used by JDBC drivers (`java.sql.Driver`) or SLF4J bindings.

This is why every consumer must explicitly add exactly one starter module (currently
`weld-starter`) as a `test`-scoped dependency themselves — `ioc-unit` deliberately does not
bundle or transitively pull in any concrete implementation, so that different consumers could in
principle run against different Weld implementations without `ioc-unit`'s own core code changing.

This mirrors how Arquillian separates `arquillian-core` (SPI: `DeployableContainer`) from
independently-versioned container adapters, and how the JUnit Platform separates
`junit-platform-engine` (SPI: `TestEngine`) from `junit-jupiter-engine`/`junit-vintage-engine`
implementations — both discovered via the same `ServiceLoader` mechanism.

Two independent things determine what a test actually exercises:

* **Which Jakarta EE spec APIs your code compiles against** — driven by whichever WildFly BOM the
  *consumer* project imports (e.g. `jakarta.enterprise.cdi-api` version).
* **Which Weld release actually boots the container** — driven by which starter module
  (`weld-starter` today) the consumer adds.

These don't have to match generations for basic `@Inject`/`@Produces`/`@ApplicationScoped` usage
to work, but mismatching them means CDI SPI/extension-level code (custom `Extension`s,
`BeanManager` internals — exactly what `ioc-unit-validate`'s `TestExtensionServices` uses) risks
behaving differently than it would on the actual target container. Providing a genuinely new Weld
major version's starter (e.g. a hypothetical `weld6-starter`) requires writing new bootstrap code
against that Weld release's own (non-spec) internal bootstrap classes (`WeldBootstrap`,
`CDI11Bootstrap`, etc.) — it is not a drop-in version bump, since these classes are Weld's own
internal SPI and can change between Weld major versions.

## Usage

Every ioc-unit test setup needs exactly one weld starter as a `test`-scoped dependency, without
an explicit version once you've imported `ioc-unit-bom`:

```XML
<dependency>
    <groupId>net.oneandone.ioc-unit</groupId>
    <artifactId>weld-starter</artifactId>
    <scope>test</scope>
</dependency>
```


