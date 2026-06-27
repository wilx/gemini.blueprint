# Spring 6 / Java 17 Port Notes

This branch ports Gemini Blueprint to Java 17 bytecode (`maven.compiler.release=17`) and the Spring Framework 6.2 line.

## Dependency choices

* Spring Framework: Apache ServiceMix OSGi-wrapped Spring bundles at `6.2.8_1`, keeping Spring Framework dependencies consumable as OSGi bundles.
* OSGi API: `org.osgi:osgi.core:8.0.0`, with compendium APIs kept at `osgi.cmpn:7.0.0` where the project still uses legacy compendium packages.
* Equinox: Eclipse platform runtime `3.31.0` for the test platform.
* Felix: `org.apache.felix.main:6.0.0`.

The available ServiceMix Spring 6.2 wrapped bundles currently top out at `6.2.8_1`, so the port keeps those OSGi bundle artifacts instead of using plain `org.springframework` jars. Gemini Blueprint's own bundles keep explicit Spring import ranges of `[6.2.0,7)`.

## Java SecurityManager

Spring 6 and modern JDKs no longer support the old SecurityManager-oriented assumptions. The port removes Gemini Blueprint's internal privileged-access wrappers and test policy usage that only existed to support that model. Functionality unrelated to Java's deprecated SecurityManager is left intact.

## Knopflerfish

The Knopflerfish test platform is intentionally removed from the Maven build and CI profiles. The available Knopflerfish framework line used by this project is not a meaningful Java 17 target and its Maven repository is no longer reliably consumable from automated builds. Runtime detection helpers remain where they are part of Gemini Blueprint's public behavior, but the in-process test platform is no longer compiled or advertised.

## Remaining integration-test notes

Equinox and Felix remain the Java 17-supported integration-test targets. Felix listener timing and synchronized OSGi tests should be investigated as focused failures if they reappear; they should not be hidden by blanket skips.


## Java agent setup

Surefire and Failsafe run tests with explicit `-javaagent` entries for `byte-buddy-agent` and `mockito-core`. This avoids runtime self-attachment for Mockito inline mocks on newer JDKs while keeping Java 17 as the supported project baseline. The test stack is Mockito 5.23.0, Byte Buddy Agent 1.17.7, and EasyMock 5.6.0.
