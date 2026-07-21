# weld-starter

The modules here start the Weld version used to run ioc-unit tests in a standalone (non-container)
Weld SE engine. `javaee-`/`jakarta-` API artifacts are normally `provided` (a real container
supplies them), but nothing plays that role for a standalone test JVM — so the starter module
includes everything actually needed to boot Weld SE as a real (`compile`-scope) dependency, so it
flows transitively into any consumer that depends on it.

## Modules

* **weld-starter-base**: version-independent glue code shared by all weld starters.
* **weld4-starter**: the only starter currently maintained. Boots Weld SE 5.1.0.Final (Jakarta EE
  10 namespace) for tests. Declares `org.jboss.weld:weld-se-core`, `weld-core-impl`, `weld-web`,
  `weld-jta` at `compile` scope, plus explicit `weld-spi:5.0.SP3` and `weld-api:5.0.SP3` at
  `compile` scope (both are `provided` in `ioc-unit`/`ioc-unit-contexts`/`ioc-unit-resteasy`/
  `ioc-unit-ejb` for real-container use, but nothing else supplies them for a standalone Weld SE
  bootstrap — see the root [README's dependency-management section](../README.md#dependency-management-bom-first)).

> Older `weld1-starter`/`weld2-starter`/`weld3-starter` modules (targeting `javaee-api` 7.0/8.0)
> have been removed from this reactor; only `weld4-starter` (Jakarta EE namespace) remains.

## Usage

Every ioc-unit test setup needs exactly one weld starter as a `test`-scoped dependency, without
an explicit version once you've imported `ioc-unit-bom`:

```XML
<dependency>
    <groupId>net.oneandone.ioc-unit</groupId>
    <artifactId>weld4-starter</artifactId>
    <scope>test</scope>
</dependency>
```

