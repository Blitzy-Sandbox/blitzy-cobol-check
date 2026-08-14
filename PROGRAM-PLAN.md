# DELIVERABLE A — PROGRAM PLAN

**Notation key.** **`FACT`** carries a `[path:locator]` or a retrieved URL · **`INFERENCE`** carries its reasoning inline · **`UNKNOWN`** means unmeasured by anyone, and is never quietly upgraded. Sequencing is expressed exclusively through `BLOCKED-BY`. Every citation in this document was re-verified mechanically against the working tree at commit `c79624bd286d5f08f156ccce755bc5d4fffd1909`: 166 of 166 citation-*content* assertions passed, and all 211 `[path:Lnn]` references — spanning 32 files and 287 line numbers — resolved and range-checked.

---

## A1. Executive summary

A maintained fork of an archived COBOL unit-test precompiler, extended until it can generate and run regression tests for enterprise COBOL at scale. Ten sequenced runs: repair the approval harness (1a); build a characterization safety net (1b); modernize build, dependencies and identity, shipping the first automated release (2); correct defects and adopt seven of the eight upstream pull requests, the eighth superseded by 1a (3); generalize the stub-emission seam into a mock dispatch point (4); add file-I/O and VSAM mocking (5), SQL/DB2 mocking (6) and CICS mocking (7); add a test generator behind a CI-enforced mutation floor (8); add a pluggable z/OS launcher, structurally complete and labelled unverified (9). The precompiler/source-injection model is retained throughout.

The single biggest technical risk is **false confidence**. Every `EXEC SQL`, `EXEC CICS` and batch file I/O verb is currently replaced with a literal `CONTINUE` `FACT [src/main/java/org/openmainframeproject/cobolcheck/features/interpreter/InterpreterController.java:L149-L150, L165-L166]`, so the tool can report a passing test for code it silently deleted. The build is green today only because the approval harness compares a 0-byte file `FACT` (reproduced: 0-byte output, zero programs compiled, exit 0). Every run therefore states its executed program count, and "green" is defined mechanically rather than trusted.

### A1.1 Governing constraints, and the absence of user-specified rules

`FACT` — `review_rules` returns exactly `No user rules provided.`, confirmed by a full-document read (`view_range=[1,-1]`). The project's on-disk rules document is **empty**. There is therefore **no rule to cite by name, none to summarize, and none to invent**. Readers should note that `review_rules` is where the full text of every rule *would* live, and that in this project it contains nothing.

`INFERENCE` — the absence is not permission to lower the bar; reasoning: a missing constraint document removes external enforcement, not the engineering obligation. In its place this programme holds itself to enterprise-standard best practice, and each standard below is tied to a decision actually taken in this document so the claim is auditable:

| Standard | Where this plan honours it |
| --- | --- |
| Evidence over assertion, with `[path:locator]` citations | Every claim in A2 carries a citation or an explicit `UNKNOWN`. Verification was mechanical rather than asserted: 166 citation-*content* assertions were checked against the files, and every `[path:Lnn]` reference in this document — 211 citations spanning 32 files and 287 line numbers — was resolved and range-checked |
| No fabricated versions | `org.cyclonedx.bom` is pinned exactly because it was verified; the Mockito, JUnit and JaCoCo targets are expressed as **constraints** deferred to execution-time resolution recorded in a handoff |
| Reproducibility as a first-class requirement | The verbatim compiler anchor, the single `-D_FORTIFY_SOURCE` value, `core.autocrlf=false`, and committed npm lockfiles |
| The smallest verifiable diff | Reusing the existing pseudo-text tokenizer instead of writing a parser; relocating one build helper instead of inventing a harness; rejecting the history rewrite |
| Decide, then record the rejected alternative | Every ADR in A4 carries its alternatives and the reason each was rejected |
| Documentation is never proof | Wiki-documented syntax verified absent from source; a third-party design document's unsourced SQLCODE values refused as authority |
| Supply-chain and licence hygiene as shipping requirements | A real SBOM at a fixed path, removal of the redundant copyleft-adjacent artifact that is *declared and resolved* on the production classpath — though **not** currently packaged into any committed archive (A2.6) — and the governance stubs filled rather than deleted |
| Automation over convention | The idempotent rename script with a mechanically checkable diff, the normalization filter with its own self-test, the coverage gate wired in, the mutation floor CI-enforced |

**A provenance distinction that matters operationally.** The dense body of binding constraints this programme runs under — the backward-compatibility contract, the "do not touch" set, the two-axis change discipline, the mechanical definition of green, the evidence standard, the environment-authority rule, the four declared non-goals and the scope boundary — are **prompt-level constraints, not user-specified rules**. They are *not* retrievable through the rules mechanism, so a reader who goes looking for a rules document will find nothing. That is precisely why they are carried verbatim inside this document and inside every run prompt in Deliverable B, and why the duplication in Deliverable B must not be "optimized" away.

---

## A2. Verified baseline

Every row below was produced by executing something in this session, or is labelled `UNKNOWN`. Nothing here is recalled or inferred into fact.

### A2.1 Environment activation — and a contradiction resolved in the ground truth's favour

`FACT` — the literal first command of this run was `. /etc/profile.d/blitzy-cobolcheck-env.sh`; it exists (2,290 bytes) and sourced with exit 0. It exports `JDK8_HOME`, `JDK11_HOME`, `JDK21_HOME`, `JAVA_HOME=$JDK11_HOME`, `GRADLE_USER_HOME=/root/.gradle`, `PATH`, and a de-duplicated `COB_CFLAGS`.

**Flagged loudly, as required.** The brief for this document asserted that a direct probe of this container found the opposite — no such script, no `cobc`, no `java`, no `/usr/lib/jvm`, no `GITHUB_TOKEN`. `FACT` — that is contradicted. All of it is present *after* sourcing. `INFERENCE` — the contrary probe was a pre-sourcing probe error rather than a finding; reasoning: nothing is on `PATH` in a non-login shell, which is exactly the failure mode the activation script exists to remove, and the same probe run after sourcing succeeds. Consequently **no golden-file finding in this plan inherits `UNKNOWN` on toolchain grounds**, no amendment request is raised, and the build matrix below is measured rather than estimated.

**The toolchain reproducibility anchor, verbatim** `FACT` (`cobc --version`):

```text
cobc (GnuCOBOL) 3.2.0
Copyright (C) 2023 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
Written by Keisuke Nishida, Roger While, Ron Norman, Simon Sobisch, Edward Hart
Built     Sep 23 2025 17:57:37
Packaged  Jul 28 2023 17:02:56 UTC
C version "15.2.0"
```

`FACT` — `COB_CFLAGS` is exported as a single `-D_FORTIFY_SOURCE` value: `-std=c17 -finline-functions -pipe -Wdate-time -D_FORTIFY_SOURCE=3 -Wno-unused -fsigned-char`. This matters because `ProcessOutputWriter.writeOutPutToConsole()` prints the child's **standard error** to `System.out` `FACT [src/main/java/org/openmainframeproject/cobolcheck/features/launcher/ProcessOutputWriter.java:L111-L114]` — `System.out.println(processInput)` at `L112` and `System.out.println(processError)` at `L113`. `INFERENCE` — a duplicated fortify definition would therefore write a compiler warning into `actual-output.txt` and into every golden file captured; reasoning: the warning is emitted on stderr by the C compiler, the launcher pipes stderr into stdout, and stdout is what the harness redirects.

`FACT` — `core.autocrlf` is `false`, set repository-locally. **Session-local, not durable:** it lives in `.git/config` and is therefore absent from a fresh clone, so every run prompt must set and verify it rather than inherit it.

### A2.2 Repository identity

| Datum | Value | Provenance |
| --- | --- | --- |
| Fork default branch | `Developer` | `FACT` — `repository_metadata.default_branch` in the upstream API capture `[upstream-harvest/pulls.json]`, which is the **durable** source. `INFERENCE` — `git symbolic-ref refs/remotes/origin/HEAD` is **not** a reliable second source and must not be used as one downstream; reasoning: it records the branch a clone was created on, so in an agent clone it resolves to the working branch rather than to `Developer`, and a prompt that reads it would misidentify the default branch without any error |
| HEAD SHA (pristine) | `c79624bd286d5f08f156ccce755bc5d4fffd1909`, subject "Revise README for project status and capitalization" | `FACT` — `git rev-parse` / `git log` |
| Tracked files | **384** at `c79624bd` | `FACT` — `git ls-tree -r --name-only c79624bd \| wc -l` |
| Divergence from `upstream/Developer` | **0 behind / 0 ahead** | `FACT` — `git rev-list --left-right --count refs/remotes/upstream/Developer...c79624bd` |
| Local branches | `Developer`, plus the working branch for this session | `FACT` — `git branch`. **Contradicts the ground truth**, which names `Developer` and `blitzy-modernization`; no `blitzy-modernization` branch exists in this checkout |
| Remotes | `origin`, `upstream`, `gm`, `livingmf` | `FACT` — `git remote`, **as observed in the planning session's clone**. The `origin` URL embeds an access credential and is deliberately not reproduced anywhere in this document. **Session-local, not durable:** remotes live in `.git/config`, so a fresh clone carries `origin` alone and every run prompt must add and verify the other three idempotently rather than assume them |
| Upstream refs fetched | 21 heads + **211** PR head refs under `refs/remotes/upstream/pr/*` | `FACT` — `git for-each-ref`, **as observed in the planning session's clone**. **Session-local, not durable:** refs are per-clone, so a fresh clone has none and every run prompt must fetch `+refs/heads/*` and `+refs/pull/*/head` and then prove all eight open pull-request heads resolve |
| Java sources | 110 under `src/main/java` totalling **12,131** lines, across **19** package directories | `FACT` — `git ls-files` + `wc -l` + `dirname \| sort -u`. **Contradicts the ground truth's "25 packages"**; its own package table lists 19 and measurement confirms 19 |
| Test sources | 35 files; **34** match `*Test.java`/`*IT.java`; only **33** contain `@Test`/`@ParameterizedTest`; 5 are `*IT` | `FACT` — `git ls-files` + `grep`. The 35th is `Utilities.java`, a helper |
| Repository size | GitHub reports 20,331 KB; local pack is 23.59 MiB | `FACT` — `repository_metadata.size` `[upstream-harvest/pulls.json]`; `git count-objects -vH` |

### A2.3 The JDK 8 / 11 / 21 build matrix — verbatim outcomes

All three legs were executed in this session after sourcing the activation script, on a warm Gradle cache, with the working tree restored to pristine afterwards. **What follows is the transcript itself rather than a summary of it, and no line is elided.** Each leg reproduces, verbatim: the whole of Gradle's own output up to and including `> Task :test`; then a complete, exhaustive accounting of the test-event stream that `> Task :test` forwards; then the whole of Gradle's own output from the end of that stream to the process exit status. Every line of every capture falls into exactly one of those three parts, and for the middle part every line falls into exactly one counted class whose form is given — the classification is asserted mechanically and its counts sum to the span length.

`INFERENCE` — the middle part is presented as an exhaustive classification rather than as 7,174 further quoted lines because its information content is a per-test result list, and the same results are available in a stricter and machine-readable form from the JUnit XML quoted for each leg; reasoning: the build script sets `showStandardStreams = true` at `[build.gradle:L86]` inside the `testLogging` block opened at `[build.gradle:L85]`, and registers the result events at `[build.gradle:L87]` (`events "passed", "skipped", "failed"`), so those lines are the test framework's own forwarded logging rather than any part of Gradle's build outcome, and quoting them would multiply this document's length several-fold while adding nothing the aggregate does not already establish. Nothing is hidden by it: the class list is exhaustive, each class's exact line form is stated, and the counts are asserted to sum.

**Reproduction command for all three legs**, so any reader can regenerate these transcripts byte-for-byte on the same image:

```bash
. /etc/profile.d/blitzy-cobolcheck-env.sh
rm -rf bin temp testruns actual-output.txt          # cold tree; `clean` alone does NOT remove these
JAVA_HOME=$JDK11_HOME ./gradlew clean test approvalTest ; echo "EXIT=$?"
JAVA_HOME=$JDK8_HOME  ./gradlew clean test           ; echo "EXIT=$?"
JAVA_HOME=$JDK21_HOME ./gradlew clean test           ; echo "EXIT=$?"
```

#### Leg 1 — JDK 11, the documented gate: `JAVA_HOME=$JDK11_HOME ./gradlew clean test approvalTest`

`FACT` — total captured output: **2,353 lines**. Lines 1–39, verbatim and complete:

```text
Starting a Gradle Daemon, 3 incompatible and 1 stopped Daemons could not be reused, use --status for details

> Configure project :
Copied with jar version 0.2.19 to approvalTest directory
Project: root project 'cobol-check'
Name: defaultProperties
Path: :defaultProperties
Project directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667
Build directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/build
Version: unspecified
Group: org.openmainframeproject
Description: Unit testing framework for Cobol
Linux detected
./approvaltest: 1: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 2: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 3: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 4: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 5: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 6: ./temp/approvalTest/cobolcheck: not found
exit from compare: 0
./expected-output.txt matches ./actual-output.txt - PASS

> Task :clean

> Task :compileJava
Note: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

> Task :processResources
> Task :classes

> Task :compileTestJava
Note: Some input files use unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

> Task :processTestResources NO-SOURCE
> Task :testClasses

> Task :test
```

`FACT` — lines 40–2333 are the forwarded test-event stream, **2,294 lines**, classified exhaustively (748 + 550 + 56 + 13 + 337 + 1 + 523 + 66 = 2,294):

| Class | Exact line form | Count |
| --- | --- | --- |
| Blank | the empty string | 748 |
| Per-test result | `<TestClass> > <method>() PASSED` \| `STANDARD_ERROR` \| `STANDARD_OUT` | **550** — of which `PASSED` 369, `STANDARD_ERROR` 156, `STANDARD_OUT` 25. **Zero** `FAILED` |
| Parameterized result | `<TestClass> > [<n>] <display name> PASSED` | 56, all `PASSED` |
| Class-level stream header | `<TestClass> STANDARD_ERROR` | 13 |
| Tool log | any line containing `CobolCheck:` — the tool's own `INFO`/`WARN` records written to the forwarded stream | 337 |
| JVM warning | `OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended` | 1 |
| Indented detail | lines beginning with whitespace: forwarded COBOL source echoes and continuation text | 523 |
| Unindented continuation | wrapped remainders of parameterized display names, which embed multi-line COBOL source | 66 |

`FACT` — lines 2334–2353, verbatim and complete, ending with the process exit status:

```text
> Task :clearLibJar UP-TO-DATE

> Task :unitTest
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

> Task :integrationTest
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended

> Task :fatJar
> Task :copyJarToBin
> Task :copyRunScripts
> Task :approvalTest

Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.9.4/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 18s
12 actionable tasks: 11 executed, 1 up-to-date
EXIT=0
```

`FACT` — JUnit XML aggregate over `build/test-results/test/*.xml`, the authoritative test-count evidence: **33 classes, tests=457, failures=0, errors=0, skipped=0, passed=457**.

`FACT` — **the JDK 11 stop-and-report gate is therefore passed, and the programme proceeds.**

#### Leg 2 — JDK 8: `JAVA_HOME=$JDK8_HOME ./gradlew clean test`

`FACT` — total captured output: **2,330 lines**. Lines 1–30, verbatim and complete:

```text
Starting a Gradle Daemon, 3 incompatible and 2 stopped Daemons could not be reused, use --status for details

> Configure project :
Copied with jar version 0.2.19 to approvalTest directory
Project: root project 'cobol-check'
Name: defaultProperties
Path: :defaultProperties
Project directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667
Build directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/build
Version: unspecified
Group: org.openmainframeproject
Description: Unit testing framework for Cobol

> Task :clean

> Task :compileJava
Note: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

> Task :processResources
> Task :classes

> Task :compileTestJava
Note: Some input files use unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

> Task :processTestResources NO-SOURCE
> Task :testClasses

> Task :test
```

`FACT` — lines 31–2322 are the forwarded test-event stream, **2,292 lines**, classified exhaustively (747 + 550 + 56 + 13 + 337 + 523 + 66 = 2,292): identical class set and identical counts to leg 1 except one fewer blank line and **no** JVM sharing warning — blank 747, per-test result 550 (`PASSED` 369, `STANDARD_ERROR` 156, `STANDARD_OUT` 25, **zero** `FAILED`), parameterized 56 all `PASSED`, class-level stream header 13, tool log 337, indented detail 523, unindented continuation 66.

`FACT` — lines 2323–2330, verbatim and complete:

```text

Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.9.4/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 10s
5 actionable tasks: 5 executed
EXIT=0
```

`FACT` — JUnit XML aggregate: **33 classes, tests=457, failures=0, errors=0, skipped=0, passed=457**.

`FACT` — a second result this leg establishes independently, visible in its lines 1–30 above: there is **no** `Linux detected`, **no** `./temp/approvalTest/cobolcheck: not found`, and **no** comparison verdict. `INFERENCE` — `clean test` therefore does not realize `approvalTest` and does not run the harness at all; reasoning: the harness output that appears under `> Configure project :` in leg 1 is absent here, and the only difference between the two invocations is the requested task set.

#### Leg 3 — JDK 21: `JAVA_HOME=$JDK21_HOME ./gradlew clean test`

`FACT` — total captured output: **2,660 lines**. Lines 1–50, verbatim and complete — note the three JDK-21-only compiler-warning groups, absent from both other legs:

```text
Starting a Gradle Daemon, 3 incompatible and 3 stopped Daemons could not be reused, use --status for details

> Configure project :
Copied with jar version 0.2.19 to approvalTest directory
Project: root project 'cobol-check'
Name: defaultProperties
Path: :defaultProperties
Project directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667
Build directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/build
Version: unspecified
Group: org.openmainframeproject
Description: Unit testing framework for Cobol

> Task :clean

> Task :compileJava
warning: [options] source value 8 is obsolete and will be removed in a future release
warning: [options] target value 8 is obsolete and will be removed in a future release
warning: [options] To suppress warnings about obsolete options, use -Xlint:-options.
/tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java:389: warning: [deprecation] Locale(String) in Locale has been deprecated
            locale = new Locale(settings.getProperty(LOCALE_LANGUAGE_CONFIG_KEY));
                     ^
/tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java:391: warning: [deprecation] Locale(String,String) in Locale has been deprecated
            locale = new Locale(
                     ^
/tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java:395: warning: [deprecation] Locale(String,String,String) in Locale has been deprecated
            locale = new Locale(settings.getProperty(LOCALE_LANGUAGE_CONFIG_KEY),
                     ^
Note: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/main/java/org/openmainframeproject/cobolcheck/services/Config.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
6 warnings

> Task :processResources
> Task :classes

> Task :compileTestJava
warning: [options] source value 8 is obsolete and will be removed in a future release
warning: [options] target value 8 is obsolete and will be removed in a future release
warning: [options] To suppress warnings about obsolete options, use -Xlint:-options.
/tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/src/test/java/org/openmainframeproject/cobolcheck/StringTokenizerExtractorTest.java:24: warning: [deprecation] Locale(String,String) in Locale has been deprecated
        Locale.setDefault(new Locale("en", "US"));
                          ^
Note: Some input files use unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
4 warnings

> Task :processTestResources NO-SOURCE
> Task :testClasses

> Task :test
```

`FACT` — lines 51–2638 are the forwarded test-event stream, **2,588 lines**, classified exhaustively (564 + 462 + 56 + 13 + 150 + 1 + 1306 + 36 = 2,588):

| Class | Exact line form | Count |
| --- | --- | --- |
| Blank | the empty string | 564 |
| Per-test result | `<TestClass> > <method>() PASSED` \| `FAILED` \| `STANDARD_ERROR` | **462** — of which `FAILED` **193**, `PASSED` 176, `STANDARD_ERROR` 93 |
| Parameterized result | `<TestClass> > [<n>] <display name> PASSED` \| `FAILED` | 56 — `PASSED` 54, `FAILED` **2** |
| Class-level stream header | `<TestClass> STANDARD_ERROR` | 13 |
| Tool log | any line containing `CobolCheck:` | 150 |
| JVM warning | `OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended` | 1 |
| Indented detail | lines beginning with whitespace: stack frames and forwarded COBOL source echoes | 1,306 |
| Unindented continuation | wrapped parameterized display names (33) **plus the three agent-instrumentation lines quoted immediately below** | 36 |

`FACT` — those three unindented lines are the earliest appearance of the class-file-version failure, at capture lines 51, 88 and 93, verbatim:

```text
java.lang.instrument.IllegalClassFormatException: Error while instrumenting sun/util/resources/cldr/provider/CLDRLocaleDataMetaInfo.
Caused by: java.io.IOException: Error while instrumenting sun/util/resources/cldr/provider/CLDRLocaleDataMetaInfo.
Caused by: java.lang.IllegalArgumentException: Unsupported class file major version 65
```

`FACT` — lines 2639–2660, verbatim and complete:

```text
457 tests completed, 227 failed

> Task :test FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':test'.
> There were failing tests. See the report at: file:///tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/build/reports/tests/test/index.html

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.9.4/userguide/command_line_interface.html#sec:command_line_warnings

BUILD FAILED in 16s
5 actionable tasks: 5 executed
EXIT=1
```

`FACT` — JUnit XML aggregate: **33 classes, tests=457, failures=227, errors=0, skipped=0, passed=230**.

**The JDK 21 failure signature, measured exhaustively — and this corrects the frame chain carried into this session.** `FACT` — the 227 failing test cases decompose into exactly three `Caused by:` chains, counted over every `<failure>` element in the JUnit XML:

| Cases | Chain, verbatim from the report |
| --- | --- |
| **183** | `Caused by: org.mockito.exceptions.base.MockitoException: Could not modify all classes` followed by the mocked type's own hierarchy, which differs per test case — for `ExpanderTest.it_inserts_code_correctly_when_call_has_different_exception_handling()` it reads verbatim `[class java.io.Reader, interface java.lang.AutoCloseable, interface java.lang.Readable, interface java.io.Closeable, class java.io.BufferedReader]` → `at net.bytebuddy.TypeCache.findOrInsert(TypeCache.java:153)` → `Caused by: java.lang.IllegalStateException: ` (message empty on that line) → `Byte Buddy could not instrument all classes within the mock's type hierarchy` → `Caused by: java.lang.IllegalArgumentException: Unsupported class file major version 65` → `at net.bytebuddy.jar.asm.ClassReader.<init>(ClassReader.java:189)` |
| **36** | `Caused by: java.lang.IllegalStateException: Could not initialize plugin: interface org.mockito.plugins.MemberAccessor (alternate: null)` → `at org.mockito.internal.configuration.plugins.PluginLoader$1.invoke(PluginLoader.java:84)` → `Caused by: java.lang.IllegalStateException: Failed to load interface org.mockito.plugins.MemberAccessor implementation declared in java.lang.CompoundEnumeration@` followed by that object's identity hash, which differs per JVM run → `at org.mockito.internal.configuration.plugins.PluginInitializer.loadImpl(PluginInitializer.java:57)` → `Caused by: java.lang.reflect.InvocationTargetException` → `Caused by: java.lang.IllegalArgumentException: Unknown Java version: 21` → `at net.bytebuddy.ClassFileVersion.ofJavaVersion(ClassFileVersion.java:257)` |
| **8** | no `Caused by:` chain — a bare `org.mockito.exceptions.base.MockitoException` reported at `MockitoExtension.java:153`, whose message names the field it could not read and the instance it could not read it from, e.g. verbatim for `CopybookExpanderIT.it_expands_a_simple_copybook()`: `Cannot read state from field: private static final java.lang.String org.openmainframeproject.cobolcheck.CopybookExpanderIT.applicationSourceFilenameSuffix, on instance: org.openmainframeproject.cobolcheck.CopybookExpanderIT@` followed by that instance's identity hash |

**Flagged loudly — two frames carried into this session do not exist in the report.** `FACT` — a search across every `<failure>` element for `InlineByteBuddyMockMaker.<clinit>(InlineByteBuddyMockMaker.java:117)` and for `ByteBuddyAgent.install` returns **zero** occurrences. `FACT` — what does occur is `InlineByteBuddyMockMaker` at `createMock(:318)`, `createMockType(:379)`, `createStaticMock(:543)` and `doCreateMock(:339)`, and `PluginInitializer.loadImpl` at both `:52` and `:57`. `INFERENCE` — the substantive conclusion is unchanged and now rests on measured frames: the inline mock maker is the failing component, so the remedy is to **drop** `org.mockito:mockito-inline` rather than bump it; reasoning: 183 of 227 failures reach `Unsupported class file major version 65` through Byte Buddy's inline instrumentation of an existing class hierarchy, which is the inline mock maker's mechanism, and the remaining 36 fail while loading a Mockito plugin against the same Byte Buddy. `FACT` — the resolved Byte Buddy is `net.bytebuddy:byte-buddy:1.10.18` and `byte-buddy-agent:1.10.18`, arriving transitively through `org.mockito:mockito-junit-jupiter:3.6.28` `[build.gradle:L105]`.

`INFERENCE` — the `BUILD SUCCESSFUL in 18s`, `BUILD SUCCESSFUL in 10s` and `BUILD FAILED in 16s` lines quoted verbatim above are wall-clock readings of these particular executions on a warm cache, not planning figures and not a performance claim; reasoning: they are part of the transcript being reproduced, and a leg re-run in another session will print a different number while the pass/fail verdict and the test counts hold.
### A2.4 The vacuous green — reproduced, and root-caused more precisely than the ground truth

`FACT` — on a cold tree (`bin/`, `temp/`, `testruns/`, `actual-output.txt` all absent), the JDK 11 gate produced this, in this order, with the harness block appearing under `> Configure project :` and **before** `> Task :clean`. This is capture lines 3–23 of leg 1 in A2.3, reproduced **complete and with nothing elided** — every line between the two markers is present:

```text
> Configure project :
Copied with jar version 0.2.19 to approvalTest directory
Project: root project 'cobol-check'
Name: defaultProperties
Path: :defaultProperties
Project directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667
Build directory: /tmp/blitzy/blitzy-cobol-check/blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36-w-001_300667/build
Version: unspecified
Group: org.openmainframeproject
Description: Unit testing framework for Cobol
Linux detected
./approvaltest: 1: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 2: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 3: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 4: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 5: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 6: ./temp/approvalTest/cobolcheck: not found
exit from compare: 0
./expected-output.txt matches ./actual-output.txt - PASS

> Task :clean
```

`FACT` — the eight lines between `Copied with jar version 0.2.19 to approvalTest directory` and `Linux detected` are the build script's own project-property dump, not omitted content: `Project:`, `Name:`, `Path:`, `Project directory:`, `Build directory:`, `Version:`, `Group:` and `Description:`. They are reproduced above because a transcript with an elision in it is not evidence.

`FACT` — `actual-output.txt` was **0 bytes**, **zero** COBOL programs were compiled, and the build exited **0**.

`FACT` — this proves that **both** the harness execution *and* the comparison run during the configuration phase. The `.execute()` calls are at `[build.gradle:L213]` and `[build.gradle:L216]`, but `new BuildHelper().compareFiles(...)` is at `[build.gradle:L230]` and the failure `throw` at `[build.gradle:L233-L235]`, all inside the same task-configuration body spanning `[build.gradle:L205-L239]`. `INFERENCE` — relocating only the two `.execute()` calls would leave the comparison at configuration time, judging output produced later and thereby inverting the very order the repair exists to establish; reasoning: the printed sequence above shows the comparison verdict emitted before `:clean`, i.e. before any task has run at all.

**Flagged loudly — the ground truth's rationale for `[build.gradle:L203]` is wrong.** `FACT` — pull request #338, still open upstream and titled "Fixed Gradle build for approval tests", consists precisely of changing `dependsOn fatJar` to `dependsOn copyJarToBin, copyRunScripts`, and `[build.gradle:L203]` **already reads** `dependsOn copyJarToBin, copyRunScripts` (`git diff 08d3c988 refs/remotes/upstream/pr/338 -- build.gradle`). `FACT` — #338 does **not** move the body out of the configuration block; its diff leaves `def output = -1` in the task-configuration body. `INFERENCE` — therefore the `dependsOn` declaration is necessary but wholly insufficient, and the ground truth's claim that it "is what makes the harness execute after the jar and scripts it needs are in place" does not hold; reasoning: `dependsOn` orders **task execution**, and a statement in a configuration body has already run before any task executes — demonstrated by the log above, in which the harness fails and the comparison passes before `:clean`. The obvious fix was already tried upstream and the harness is still vacuous.

**Which invocations actually fire the harness — measured, and narrower than described.**

| Invocation | Harness fires? | Evidence |
| --- | --- | --- |
| `./gradlew clean test approvalTest` | **Yes**, at configuration time | `FACT` — the log above |
| `./gradlew clean approvalTest` | **Yes**, at configuration time | `FACT` — harness and comparison at log lines 12–20, `BUILD SUCCESSFUL` at line 54 |
| `./gradlew tasks` | **Yes** — and it writes a 0-byte `actual-output.txt` into the working tree | `FACT` — six `not found` lines, `Linux detected`, `exit from compare: 0`, `matches ... - PASS`, and `actual-output.txt` created at 0 bytes |
| `./gradlew clean test` | **No** | `FACT` — no `Linux detected`, no `not found` lines, no comparison verdict; no `temp/` and no `actual-output.txt` created |

`INFERENCE` — the discriminator is task realization, not the phase: `tasks.register("approvalTest", Test) { ... }` `[build.gradle:L201]` is a lazy registration, so its configuration closure runs only on invocations that realize the task; `./gradlew tasks` realizes every task in order to list them, while `./gradlew clean test` never realizes `approvalTest`. The precise formulation is therefore: **the harness executes during the configuration phase of any invocation that realizes `approvalTest`**. A corollary the ground truth does not state: merely *listing* tasks pollutes the working tree with a 0-byte `actual-output.txt`, which can silently overwrite a real capture — an independent argument for the relocation.

### A2.5 What the harness does when it actually runs

`FACT` — after a full build plus the executable-bit grants the harness needs, `./approvaltest` was run directly. Six CLI invocations were issued (`grep -c 'INF000: Starting Cobol Check'` = 6) but only **five** reached a COBOL compile-and-run (`grep -c 'INF008: About to launch process'` = 5).

`FACT` — the sixth fails outright:

```text
Exception in thread "main" java.lang.RuntimeException: java.io.FileNotFoundException: src/main/cobol/FILECOPY (No such file or directory)
```

`FACT` — the harness passes `-p FILECOPY` `[approvaltest:L4]` while the program file is `src/main/cobol/FileCopy.cbl` and the suite directory is `src/test/cobol/FileCopy`. `INFERENCE` — on a case-sensitive filesystem the name does not resolve and an unhandled `RuntimeException` escapes `main`, while on a case-insensitive filesystem it would resolve; reasoning: the exception names the exact uppercase path, and the only difference between the two platforms is filesystem case folding. `INFERENCE` — the Linux and Windows harnesses therefore **cannot** produce identical output, yet CI compares both against the same `expected-output.txt` across three operating systems `FACT [.github/workflows/VerifyAction.yml:L13, L30]`.

**The false green, proven by execution.** `FACT` — the five GnuCOBOL child processes reported exit codes **4, 0, 0, 4, 0**, and all five runs nonetheless logged `INF004: Cobol-Check terminating: 0`. `FACT` — the decision is `if (exitCode > 4)` `[src/main/java/org/openmainframeproject/cobolcheck/Main.java:L25]` followed by `initializer.setExitStatusHalt()` at `L26`, and the only two status values that exist are `STATUS_NORMAL = 0` and `STATUS_HALT = 8` `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L16-L17]`, whose own comment at `L15` reads `// File read status values`. `INFERENCE` — the boundary is off by one for precisely the value GnuCOBOL returns, so a failing COBOL run is indistinguishable from a passing one at the process level; reasoning: two observed children returned exactly 4, `4 > 4` is false, and no other code path sets halt.

`FACT` — the inverse paradox is confirmed at exact lines: `--help` `[.../workers/Initializer.java:L35-L37]` and `--version` `[.../workers/Initializer.java:L40-L42]` both call `statusController.setExitStatusHalt()`, which assigns `Constants.STATUS_HALT` `[.../features/Status/StatusController.java:L21]`, and `exitProgram()` calls `System.exit(status.exitStatus)` `[.../features/Status/StatusController.java:L61]`. So **help and version exit 8 while failing COBOL tests exit 0**.

**Baseline staleness, measured exactly.** `FACT` — the real run produced `actual-output.txt` at **332 lines / 27,269 bytes** against `expected-output.txt` at **234 lines / 12,336 bytes**: a delta of **+98 lines / +14,933 bytes**. This corroborates the ground truth's "roughly 100 lines and 15 KB" and replaces it with exact figures. `FACT` — the first divergence is at line 5, and it is a result-format change: the baseline carries `EXPECTED +00000000025.7500000, WAS +00000000025.7400000` on one line where the current code emits `EXPECTED ...` and `WAS ...` on two. `INFERENCE` — the +98 lines are predominantly this one-line-to-two-line split rather than new test content; reasoning: the divergence pattern repeats at each `EXPECTED`/`WAS` pair throughout the diff.

`FACT` — simulating `BuildHelper.compareFiles()` over the two files returns `1` at line 5, so once the harness genuinely runs the comparison **does** fail against the stale baseline. `FACT` — conversely, with an empty `actual-output.txt` the loop guard at `[build.gradle:L273]` — `while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null)` — short-circuits on the first iteration, the body never executes, and control reaches `return 0` at `[build.gradle:L286]`. Both defects are therefore demonstrated rather than argued.

`FACT` — running the documented gate **mutates tracked files**: `approvaltest` changes mode `100644 → 100755` (from `"chmod +x ./approvaltest".execute()` `[build.gradle:L213]`), `build/libs/cobol-check-0.2.19.jar` is rebuilt, `build/distributions/cobol-check-0.2.19.zip` is deleted by `clean` and not regenerated by that task set, and a harness run additionally changes the mode of `scripts/linux_gnucobol_run_tests`.

### A2.6 Fat-jar contents, and the archive reproducibility finding

`FACT` — `configurations.compile` resolves to nothing. `./gradlew dependencies --configuration compile` reports `compile - Dependencies for source set 'main' (deprecated, use 'implementation' instead). (n)` followed by `No dependencies`. `INFERENCE` — the `from { configurations.compile.collect { ... } }` block at `[build.gradle:L138]` therefore bundles zero third-party bytecode; reasoning: the collection it iterates is empty, and all eight declarations are `implementation`/`testImplementation` with none `compile` `[build.gradle:L97-L106]`.

`FACT` — three archives are tracked, not two. **Contradicts the ground truth**, which says "both contain exactly 200 entries":

| Archive | Entries | Top-level | Third-party bytecode |
| --- | --- | --- | --- |
| `build/libs/cobol-check-0.2.19.jar` | **200** | `META-INF`, `org` | **0** |
| `vs-code-extension/Cobol-check/bin/cobol-check-0.2.19.jar` | **200** | `META-INF`, `org` | **0** |
| `build/distributions/cobol-check-0.2.19.zip` | **17** | `bin`, `config.properties`, `scripts`, `src` | **0** |

`FACT` — measured with Python's `zipfile`; zero entries under `javax/`, `jakarta/`, `com/sun/`, `org/glassfish/`, `org/jetbrains/` or `org/intellij/` in any of the three. **Flagged:** `unzip` **is present** in this image (`unzip` resolves on `PATH`), contradicting the ground truth's statement that it is absent; the Python route was simply retained for exact entry-level comparison.

`FACT` — the committed jar's bytes are **not reproducible**, and a re-measurement in this session **corrects an earlier revision of this section, which reported the difference as 13 entries differing by timestamp alone. That is wrong in both halves.** Committed blob 273,628 bytes versus rebuilt 273,558 bytes; the 200-entry name set is identical; and the 200 entries partition as follows:

| Difference | Count | What differs |
| --- | --- | --- |
| **Content** | **13** | The six packaged copybooks and the seven message bundles — every non-`.class` resource in the jar |
| **Timestamp only, content identical** | **187** | Everything else: 160 `.class` entries, the manifest and the directory entries |

**The content difference is line endings, and it is a finding in its own right.** `FACT` — the committed jar's `CCHECKWS.CPY` entry is 7,844 bytes containing **129 CRLF** sequences and zero bare LFs; the rebuilt entry is 7,715 bytes containing **zero CRLF** and 129 LFs; the two are **equal after CRLF-to-LF normalization**, and the rebuilt entry is byte-identical to the on-disk source. The same pattern holds for all thirteen — `messages.properties`, for instance, carries 64 CRLFs in the committed jar and none in the rebuild.

`INFERENCE` — the committed artifact was therefore built on a machine where line-ending translation was active, while a build here with `core.autocrlf=false` reproduces the sources as committed; reasoning: the only difference is the line terminator, the rebuilt entries match the working-tree bytes exactly, and this clone has translation disabled. **This is the strongest available evidence for the `core.autocrlf=false` mandate: it is not a theoretical hazard but an observed corruption in a shipped artifact**, and it lands specifically on `CCHECKWS.CPY` and the other copybooks, which are injected into the program under test as column-significant fixed-format COBOL. `INFERENCE` — a run that captured golden files from a translated checkout would bake the same corruption into its baseline, and the resulting diffs would be indistinguishable from real behaviour changes.

`INFERENCE` — the timestamp half remains true of the other 187 entries: Gradle 6.9.4 embeds build-time timestamps in archive entries, so a byte-identical **archive** oracle is unattainable on the current build; reasoning: those entries' content matches exactly and only the recorded timestamps differ. This makes Gradle 9's reproducible-archive default a genuine asset rather than an incidental one. `INFERENCE` — but the two causes must not be conflated, because they have different fixes: the timestamps are fixed by the build-tool upgrade, whereas the line endings are fixed only by controlling the checkout, which no build-tool version can do for you.

### A2.7 Dependency closure and the vulnerability scan

`FACT` — the full resolved closure was measured with `./gradlew dependencies --configuration runtimeClasspath` and `--configuration testRuntimeClasspath`. The runtime closure is **9** coordinates: `org.jetbrains:annotations:15.0`; `javax.xml.bind:jaxb-api:2.3.0`; `jakarta.xml.bind:jakarta.xml.bind-api:2.3.2`; `jakarta.activation:jakarta.activation-api:1.2.1`; `org.glassfish.jaxb:jaxb-runtime:2.3.2`; `org.glassfish.jaxb:txw2:2.3.2`; `com.sun.istack:istack-commons-runtime:3.0.8`; `org.jvnet.staxex:stax-ex:1.8.1`; `com.sun.xml.fastinfoset:FastInfoset:1.2.16`. The combined runtime plus test closure is **24** coordinates.

**Flagged loudly.** `FACT` — the ground truth describes a fifteen-coordinate set. The measured closure is **24** (25 with the Sonar plugin coordinate), it does **not** contain `com.sun.activation:javax.activation:1.2.0` — the resolved activation artifact is `jakarta.activation:jakarta.activation-api:1.2.1` — and it **adds** `org.jvnet.staxex:stax-ex:1.8.1`, `com.sun.xml.fastinfoset:FastInfoset:1.2.16`, `org.apiguardian:apiguardian-api:1.1.0`, `org.opentest4j:opentest4j:1.2.0`, `org.junit.platform:junit-platform-commons:1.7.0`, `org.junit.platform:junit-platform-engine:1.7.0` and `org.junit:junit-bom:5.7.0`. `INFERENCE` — the ground truth's list was inferred rather than resolved; reasoning: it names a plausible activation artifact that the resolver does not select and omits four transitives the resolver does select.

`FACT` — the declared JUnit skew resolves upward at runtime: `org.junit.jupiter:junit-jupiter:5.6.1` `[build.gradle:L102]` resolves to `5.7.0`, because `mockito-junit-jupiter` brings `org.junit:junit-bom:5.7.0` transitively.

**Vulnerability scan.** `FACT` — no vulnerability scanner is present in this image (`trivy`, `grype`, `osv-scanner`, `syft`, `snyk` all absent), and nothing was installed. The scan was performed by querying the database directly with tools already present.

| Scan attribute | Value |
| --- | --- |
| Scanner | The **OSV** database, queried directly at `https://api.osv.dev/v1/query` with `python3` from the standard library |
| Ecosystem | `Maven` |
| Database snapshot | OSV is a live query service, so the snapshot is the **query instant: `2026-08-14T06:57:09Z`** — stated as an instant precisely because no offline snapshot date exists to quote |
| Coordinates queried | **25** — the 24 measured closure coordinates plus `org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.0` |
| Result | **0 advisories. 0 failed queries.** |

`INFERENCE` — the CVE premise does not hold, so the modernization run's dependency work is justified by **JDK 21 and Gradle 9 compatibility and by licence hygiene**, not by vulnerability remediation; reasoning: every coordinate in the resolved closure returned an empty advisory list from the database that would report one. This corroborates the ground truth's own correction. `INFERENCE` — old is not the same as vulnerable, and a plan that claimed otherwise would be spending effort against a finding set that does not exist.

### A2.8 Per-PR apply-cleanly status — all eight

`FACT` — measured non-mutatingly with `git merge-tree --write-tree --no-messages c79624bd <pr-head>`; diffstats are against each PR's own merge base. No commit, branch, tag or push was created.

| PR | Origin | Apply status | Files | Diffstat | Merge base | Conflicting paths |
| --- | --- | --- | --- | --- | --- | --- |
| **#330** | `openmainframeproject/cobol-check` | **CONFLICT** | 6 | +139 / −21 | `b0b39da8` | `InterpreterController.java`, `LineRepository.java`, `Generator.java`, `ExpanderTest.java` |
| **#336** | `openmainframeproject/cobol-check` | **CONFLICT** | 10 | +157 / −8 | `2235764e` | `package-lock.json`, `vs-code-extension/Cobol-check/scripts/windows_gnucobol_run_tests.cmd`, `vs-code-extension/package.json` |
| **#337** | `openmainframeproject/cobol-check` | **CONFLICT** | 6 | +28 / −24 | `08d3c988` | `InterpreterController.java`, `InterpreterControllerTest.java`, `vs-code-extension/Cobol-check/bin/cobol-check-0.2.8.jar` (**delete/modify**) |
| **#338** | `openmainframeproject/cobol-check` | **CONFLICT** | 3 | +9 / −9 | `08d3c988` | `approvaltest`, `approvaltestWin.cmd`, `build.gradle` |
| **#408** | `generalmotors/openmainframeproject-cobol-check` | **CLEAN** | 8 | +261 | `e372dd7f` | — |
| **#409** | `generalmotors/openmainframeproject-cobol-check` | **CLEAN** | 3 | +138 / −1 | `e372dd7f` | — |
| **#410** | `generalmotors/openmainframeproject-cobol-check` | **CLEAN** | 3 | +10 / −1 | `e372dd7f` | — |
| **#411** | `Living-Mainframe/cobol-check` | **CLEAN** | 9 | +231 / −14 | `e372dd7f` | — |

`FACT` — all eight target `Developer` and all eight carry `locked = true`; the upstream repository carries `archived = true` and the credential used held `pull` as its only permission `[upstream-harvest/pulls.json]`. `FACT` — each PR's head SHA resolves identically from two independent sources: the API capture and the locally fetched `refs/remotes/upstream/pr/<n>` (`9010b81`, `69603d7`, `8c07d40`, `11faeef`, `7d8a086`, `2d1b367`, `ab4875f`, `e88bc1a`).

`INFERENCE` — a perfect inversion of the intuitive expectation: **every upstream-origin PR conflicts and every third-party PR applies cleanly**; reasoning: the four upstream branches predate five years of `Developer` movement and two of them edit committed binaries or lockfiles, whereas the four third-party branches all share the recent merge base `e372dd7f`.

**Apply-cleanly status is not a quality signal, and this table must not be read as one.** `FACT` — the apply column reports only whether a three-way merge produces conflicts. `FACT` — the cleanest-applying contribution in the set, #411, carries **no upstream review at all**: `/pulls/411/reviews`, `/pulls/411/comments` and `/issues/411/comments` each returned HTTP 200 with **zero records**, and `requested_reviewers` and `requested_teams` are both empty `[upstream-harvest/pr-411-reviews.json]`. `FACT` — the one contribution that *was* examined by a domain reviewer, #330, is the one that conflicts and was rejected. `INFERENCE` — mergeability and scrutiny are therefore anti-correlated in this set, so adoption is gated on review rather than on the apply column; reasoning: a recent branch conflicts less because it is recent, not because it is right.

Two consequences that change routing:

- `FACT` — #337 conflicts on a **committed jar**, and its own diff edits `vs-code-extension/Cobol-check/bin/cobol-check-0.2.8.jar`, a path that no longer exists at HEAD. The evidence chain, each step checkable: `FACT` — that jar existed in reachable history, last touched at `c431d60` ("minor detail") and introduced at `b40b8f0` ("New builds for 0.2.8"); `FACT` — it is absent at `c79624bd286d5f08f156ccce755bc5d4fffd1909`, where `git ls-files vs-code-extension/Cobol-check/bin/` returns only `cobol-check-0.2.19.jar`. `INFERENCE` — the fork therefore **deleted** the path while #337 **modifies** it, which is a delete/modify conflict rather than a content conflict; reasoning: a merge cannot reconcile an edit to a file one side removed. `INFERENCE` — committing build outputs is what converts an ordinary text merge into this class of conflict, and a delete/modify conflict on a binary cannot be resolved by choosing a side: keeping the deletion discards the contributor's edit, and restoring the path reintroduces a stale build output for a version the project no longer ships. `INFERENCE` — that is precisely why the disposition harvests #337's fixture and abandons its jar edit rather than attempting a merge.
- `FACT` — #338 conflicts on exactly `build.gradle`, `approvaltest` and `approvaltestWin.cmd`. `INFERENCE` — that is the same file set the approval-harness repair must touch, so the repair supersedes #338 rather than coexisting with it.

### A2.9 Does `Living-Mainframe` hold commits beyond PR #411? — Yes, definitively

`FACT` — the `livingmf` remote carries 23 branches. Ten hold commits not reachable from #411's head `e88bc1a`: `All-sam-fixes` (27), `Area-after-col-72` (6), `fix-db2` (6), `DDH-12842-Stop-when-compile-fails` (3), `add-cics-mock` (2), `fix-loop-in-extension` (2), `IMS` (1), `add-sql-mock` (1), `Fix-approval-test` (1), `main` (1).

`FACT` — `livingmf/Fix_exit_code_jar` (`79d9430`) is already an **ancestor** of `c79624bd` (`git merge-base --is-ancestor` succeeds), so it is already merged — yet the false green documented above persists. `INFERENCE` — whatever that branch corrected, it was not the exit-status decision; reasoning: `Main.java:L25` still reads `exitCode > 4` and two observed children returning 4 still produced `terminating: 0`.

`FACT` — `livingmf/add-sql-mock` adds exactly one file, `cobol-check-sql-mock.zip` (40,396 bytes, 31 entries), containing `features/sql/{SQLMockParser, SQLMockCodeGenerator, SQLMockRepository, SQLMockDefinition, SQLStatementType, SQLMockDataRow}`, two test classes, `docs/SQL_MOCK_DESIGN.md` (16,152 bytes), `docs/INTEGRATION_GUIDE.md` and `examples/CUSTMGMT.{CBL,cut}`. `FACT` — `livingmf/add-cics-mock` adds `cobol-check-cics-mock.zip` (whose entry paths contain a literal unexpanded `{src/...,src/...}` brace glob) and `cobol-check-cics-mock_1.zip` (21,679 bytes, 29 entries) containing `features/cics/{CICSMockParser, CICSCommandType, CICSResponseCode, CICSMockDataRow, CICSMockDefinition, CICSMockCodeGenerator, CICSMockRepository}`, `examples/CUSTINQ.{CBL,cut}` and `docs/CICS_MOCK_DESIGN.md`.

**This is prior art, and it must be handled as design input rather than as code.** `FACT` — `SQL_MOCK_DESIGN.md` uses only the SQLCODE literals `0`, `100` and `-001`, and cites **no source at all**: a search for `IBM`, `DB2 for z/OS`, `SQL Reference`, `GnuCOBOL` and `-std=ibm` returns zero matches. `FACT` — `CICSResponseCode.java` in that archive defines **44** enum constants and does not reference `EIBResponseTable` or `EIBResponseCodes` anywhere, while the in-repo table performs **76** insertions resolving to **75 unique condition names** `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java:L43-L118]` (see A2.11 row 5 for the duplicate). `INFERENCE` — these archives are unproposed, unreviewed, opaque binary drops whose semantics are unsourced and whose CICS condition set is both narrower than and inconsistent with the in-repo authority; reasoning: no pull request exists for either, the design document supplies values without provenance, and 44 ≠ 75 with no reference to the existing table. Disposition: **design input only, explicitly labelled unverified provenance, never adoptable code.**

`FACT` — `livingmf/IMS` (`721c2f8`) is not IMS support; it is "Fix for stubbing linkage section if config is set", 4 files, +14 / −2, touching `InterpreterController.java`, `Config.java`, `Interpreter.java` and `InterpreterControllerTest.java`.

### A2.10 Per-file copybook verdicts, cited `file:line`

| File | Verdict | Evidence |
| --- | --- | --- |
| `src/main/resources/org/openmainframeproject/cobolcheck/copybooks/DFHEIBLK.CPY` | **Packaged but dead** | `FACT` — present in the shipped jar (200-entry listing), but the sole classpath loader `getBoilerplateCodeFromCopybooks()` `[.../features/testSuiteParser/TestSuiteParserController.java:L298]`, which builds its path at `L300` and reads it at `L301`, is invoked only at `L123`, `L181` and `L195`, all with `CCHECK*` names |
| `copybooks/DFHEIBLK.CPY` | **Orphaned duplicate** | `FACT` — neither packaged nor loaded, and byte-identical to the packaged copy (md5 `ed3c04206f31d99682487b0df43c2fe4` for both) |
| `src/main/cobol/copy/SQLCA.cpy` | **Loaded at runtime, packaged in no artifact** | `FACT` — `[src/main/cobol/DB2PROG.cbl:L12]` `EXEC SQL INCLUDE SQLCA  END-EXEC.` → `[.../features/interpreter/InterpreterController.java:L463-L484]` → `[.../features/interpreter/LineRepository.java:L135-L145]` → `CopybookExpander`, resolved from the filesystem under `[config.properties:L125]` with `cpy` in the suffix list at `[config.properties:L162]`. Absent from all three archive listings |
| `testfiles/CICDEMO-AFTER.CBL` | **Sample/test material only** | `FACT` — a fixture; no production code path reads it |

`FACT` — extending the finding: **only three of the six packaged copybooks are ever loaded.** The six are `CCHECKPARAGRAPHSPD.CPY`, `CCHECKRESULTPD.CPY`, `CCHECKWS.CPY`, `DATETIME`, `DFHEIBLK.CPY`, `OUTPUT`; the three `CCHECK*` files are the only ones named at the three call sites, so `DATETIME` and `OUTPUT` also ship and are never read. `INFERENCE` — that is plain packaging hygiene rather than a legal question; reasoning: neither file is IBM-derived.

**Flagged:** `FACT` — `copybooks/CCHECKWS.CPY` is **not** byte-identical to the packaged copy (md5 `1a4f5941a2ce738326e3404da516ed2a` versus `09dd62ab27c658e67ec5a7510e7222dc`; 6,933 versus 7,715 bytes). `INFERENCE` — the legacy `copybooks/` tree is a drifted partial duplicate rather than a clean mirror, so a deletion instruction that treats it as a mirror would discard content that has diverged; reasoning: only `DFHEIBLK.CPY` matches byte-for-byte, and `CCHECKPD.CPY` in that tree has no packaged counterpart by that name at all.

### A2.11 The remaining ground-truth contradictions

Established by measurement in this session and flagged rather than quietly corrected. The seven that change a plan decision appear above in A2.4, A2.7, A2.8, A2.9 and A2.10, plus these eight — of which rows 7 and 8 concern this planning run's own inputs and outputs rather than the codebase:

| # | Ground-truth claim | Measured | Consequence |
| --- | --- | --- | --- |
| 1 | `Keywords.java` is at `services/cobolLogic/Keywords.java` | `FACT` — it is at `src/main/java/org/openmainframeproject/cobolcheck/features/testSuiteParser/Keywords.java`; nothing exists at the cited path | Any enumerated surface naming the cited path points at a non-existent file. Corrected in the enumerations |
| 2 | The z/OS copybook fall-through expands a non-SQLCA include **twice** | `FACT` — `[.../features/interpreter/InterpreterController.java:L467-L481]`: `case ZOS:` at `L468`, the SQLCA/SQLDA early `return;` at `L469-L470`, `default:` at `L471`, expansion at `L472-L480`, `break;` at `L481`. Mechanically asserted: **no `break` token between `L468` and `L471`** | **Refuted.** SQLCA/SQLDA on z/OS expands **zero** times; a non-SQLCA include falls through and expands **exactly once**, identically to every other platform. The missing `break` is real but **load-bearing** — inserting one would stop non-SQLCA z/OS expansion altogether, a regression |
| 3 | The regex `replaceAll` is in `LineRepository` | `FACT` — `LineRepository` does hardcoded token extraction at `[.../features/interpreter/LineRepository.java:L110-L113]`; the regex substitution is at `[.../features/interpreter/CopybookExpander.java:L72]` and `[:L79]` | Both halves of the defect are real but live in different files; a repair scoped to one file would be incomplete |
| 4 | The macOS launcher branch is **undocumented** | `FACT` — `[.../features/launcher/Launcher.java:L43]` reads `NOTE: Currently not supporting OSX or ZOS.`, naming both | It **is** documented. The real hazard is that `macos-latest` sits in the CI matrix `[.github/workflows/VerifyAction.yml:L13]` while the OSX arm returns `null` (`launcher` initialised `null` at `L46`, construction commented out at `L58`), and `[config.properties:L203]` populates `osx.process` unreachably |
| 5 | The CICS condition table holds roughly 78 entries | `FACT` — exactly **76** `put(` insertions resolving to **75 unique condition names** `[.../services/cobolLogic/EIBResponseTable.java:L43-L118]` | Corrected **in both directions: neither 78 nor 76 is the number of conditions the runtime map holds.** `FACT` — `LENGERR` is inserted twice, at `[:L51]` with EIBFN `0x04` and at `[:L84]` with EIBFN `0x06`, the other three components identical (`0xE1`, `0`, `22`); `HashMap.put` replaces rather than rejects, so the `0x06` tuple survives and the map exposes 75 names. `FACT` — the class javadoc at `[:L22]` already states `File Control is assumed (EIBFN - 0x06..)`, so the surviving value matches documented intent. Additionally `lookup()` at `L130-L133` returns a **zero-filled** `EIBResponseCodes` on a miss — a silent default that is itself a false-confidence hazard |
| 6 | Documented mock syntax exists in the keyword tables | `FACT` — `INPUT-FILE`, `ON OPEN`, `ON READ`, `DATASET` and `CONDITION` return **zero** occurrences across both `Keywords.java` and `Constants.java` | Corroborated. Documentation is not evidence |
| 7 | Pull request #330 carries **five review comments** | `FACT` — the endpoint that serves review comments on a pull request, `GET /repos/openmainframeproject/cobol-check/pulls/330/comments`, returned **3** `[upstream-harvest/pr-330-reviews.json → _capture.record_counts.review_comments]`, corroborated by GitHub's own counter `review_comments = 3` in the captured `pull_request` payload. Two other captured families coincidentally total 5 — conversation comments `issue_comments = 5`, and `reviews` (2) + `review_comments` (3) — and neither is accepted as a substitute | **Contradicted: expected inline review comments 5, actual 3.** The adjudication in A5.1 is unaffected because it rests on the *content* of the objections rather than on their count, and both decisive objections are present in the capture. `UNKNOWN` — the fate of the two implied inline records: no deletion, resolution or hidden-record signal appears in any captured payload, and an archived repository cannot be re-read to establish one |
| 8 | The planning run's outputs are never committed to the fork | `FACT` — thirteen planning-artifact paths are tracked at this branch's head, `git diff c79624bd286d5f08f156ccce755bc5d4fffd1909 --name-status` reporting all thirteen at status `A`. `FACT` — `origin/Developer` is still at that same baseline, so none has reached the fork's default branch | **Contradicted as delivered.** The requirement, the evidence, the mechanical cause and the run the removal is itemized to are recorded in full in A9.1, with the human confirmation it is gated on carried in A11 |

`FACT` — further matters of fact, each measured: the largest classes are `TestSuiteParser.java` 60,602 B / 1,240 L, `Keywords.java` 38,427 B / 724 L, `InterpreterController.java` 26,737 B / 658 L, then `Interpreter.java` 24,484 B / 623 L and `Config.java` 18,725 B / 414 L — so the "do not make them worse" constraint attaches to the first three; two releases share the version number `0.2.19` `[CHANGELOG.md:L23, L26]`, and a **second** stale planned heading `## \[1.0.0\]` exists at `[CHANGELOG.md:L8]` alongside `## \[0.3.0\]` at `[CHANGELOG.md:L15]`; `[config.properties:L188]` holds the literal string `null` for `gnucobol.compile.options`; the README advertises two output formats that throw `[README.md:L10]`; a second Sonar surface exists in `sonar-project.properties` carrying `sonar.sources=src` and a six-entry exclusion list, while `projectKey`/`organization` live only at `[build.gradle:L19-L20]`; the rename blast radius is exactly **161** tracked text files (163 including binaries) with the highest concentrations in `Generator.java` (16), `TestSuiteConcatenator.java` (14), and `ProcessOutputWriter.java`, `InterpreterController.java` and `CopybookExpander.java` (12 each); there are **16** suite directories against 15 programs, making `src/test/cobol/FDTEST/FDTEST01.CUT` a confirmed orphan; no `doc`, `docs`, `wiki` or `adr` directory exists anywhere and no JAR release workflow exists at all — `.github/` holds exactly three files; and the npm surface is three mutually inconsistent manifests with no committed lockfile.

`FACT` — two further findings the ground truth does not contain: `src/test/java/org/openmainframeproject/cobolcheck/features/interpreter/LineRepositoryTest.java` is an 18-line **empty shell** containing zero `@Test` methods, so `LineRepository` — a core-surface class for two later runs — has a test class with no tests; and `insert_notice.sh` prepends `notice.txt` to each `*.java` file while skipping any file whose **second** line already matches `Copyright `, yet `notice.txt` is 1 byte (a bare newline). `INFERENCE` — the script has therefore been prepending an empty line and can never satisfy its own guard, which is the concrete mechanism behind zero Java files carrying a copyright or licence header and zero SPDX identifiers existing anywhere; reasoning: the guard tests for text the script never inserts.

### A2.12 The test-suite performance envelope, and how it is measured

`FACT` — observed on this container with a warm Gradle cache, single operating system, as Gradle's own reported wall clock. These are measurements of existing work, not estimates and not a schedule:

| Command | Reported, verbatim | Where the transcript is |
| --- | --- | --- |
| `JAVA_HOME=$JDK11_HOME ./gradlew clean test approvalTest` | `BUILD SUCCESSFUL in 18s` | A2.3 leg 1, capture line 2351 |
| `JAVA_HOME=$JDK8_HOME ./gradlew clean test` | `BUILD SUCCESSFUL in 10s` | A2.3 leg 2, capture line 2328 |
| `JAVA_HOME=$JDK21_HOME ./gradlew clean test` | `BUILD FAILED in 16s` | A2.3 leg 3, capture line 2658 |
| `./gradlew clean test` with `cobc` absent from `PATH` | `BUILD SUCCESSFUL in 7s` | A11.4 Q5, a separate experiment |

`FACT` — these readings are per-execution and they move between executions of the identical command on the identical image: an earlier execution of the JDK 8 leg in this session's environment reported `BUILD SUCCESSFUL in 12s` and an earlier JDK 21 leg reported `BUILD FAILED in 13s`, against the 10s and 16s reproduced verbatim in A2.3. `INFERENCE` — that variance is precisely why the envelope below is defined as a ratio re-measured back-to-back rather than as an absolute figure; reasoning: a criterion stated in seconds would be satisfied or violated by daemon warmth alone.

`FACT` — the envelope's denominator, measured: the `.cut` corpus is 23 files carrying **25** `TestSuite` declarations and **171** `TestCase` declarations; the approval harness exercises 6 programs of which 5 execute, producing 11 `TESTSUITE:` blocks and 332 output lines.

**Both declaration counts contradict an earlier revision of this plan, which recorded 22 and 159; the earlier figures were wrong and the two distinct counting errors that produced them are stated here so neither is repeated.** `FACT` — a raw case-insensitive token count over the 23 files returns 25 and **176**, not 171: the extra five are the word `Testcase` appearing inside a quoted test-case *name*, at `[src/test/cobol/MOCKTEST/BeforeAfterTest.cut:L19,L23,L27,L99,L102]`. Stripping quoted string literals before counting yields **25 / 171**, which is the authoritative measurement. `FACT` — an independent method agrees exactly: counting lines whose first non-blank token is `TESTSUITE` or `TESTCASE` also returns **25 / 171**. `INFERENCE` — the 22 / 159 figures came from a count restricted to declarations followed by a **double**-quoted name on the same line; reasoning: 8 suite declarations and 12 case declarations use single quotes or place the name on the following line, and 25 − 8 = 17 while the double-quote-only rule also drops declarations it cannot pair, which is why both figures were low. `INFERENCE` — the denominator matters beyond bookkeeping; reasoning: the performance envelope is defined as a ratio against it, and Run 8's mutation floor is measured over the same corpus, so an understated denominator would flatter both.

**The envelope, stated with its measurement method.** `INFERENCE` — an envelope quoted as an absolute figure is unfalsifiable across machines, so it is defined as a **ratio against a baseline re-measured on the same machine in the same invocation**; reasoning: the measurements above vary with cache warmth and host, while a ratio measured back-to-back does not. The criterion: on the machine under test, `./gradlew clean test` is measured immediately before and after the change under assessment, and generated-suite execution is measured by a named task over a stated suite count; the reported figure is *elapsed per generated suite* and *total elapsed as a multiple of the pre-change `clean test` baseline*. A run may not report an envelope without stating the suite count, the command, and both measurements.

### A2.13 Baseline evidence appendix — every claim in A2 re-openable from a command

`INFERENCE` — the rest of section A2 states results; this subsection states **how each was obtained**, so a later reader can re-open any of them rather than taking them on trust. Reasoning: a baseline whose commands are not recorded is indistinguishable from a baseline that was asserted, and every oracle in A3 rests on this section. Each block below gives the command, its exit status, and either the complete output or the decisive lines of it. Every command was executed in this session, in this container, at the pristine HEAD `c79624bd286d5f08f156ccce755bc5d4fffd1909`, after sourcing the activation script named in A2.1.

**A2.13.1 Toolchain anchor.** Exit status `0` on all four.

```text
$ cobc --version
cobc (GnuCOBOL) 3.2.0
Copyright (C) 2023 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
Written by Keisuke Nishida, Roger While, Ron Norman, Simon Sobisch, Edward Hart
Built     Sep 23 2025 17:57:37
Packaged  Jul 28 2023 17:02:56 UTC
C version "15.2.0"

$ "$JDK8_HOME/bin/java"  -version   # JDK8_HOME=/usr/lib/jvm/java-8-openjdk-amd64
openjdk version "1.8.0_492"
$ "$JDK11_HOME/bin/java" -version   # JDK11_HOME=/usr/lib/jvm/java-11-openjdk-amd64
openjdk version "11.0.31" 2026-04-21
$ "$JDK21_HOME/bin/java" -version   # JDK21_HOME=/usr/lib/jvm/java-21-openjdk-amd64
openjdk version "21.0.11" 2026-04-21

$ echo "$COB_CFLAGS"
-std=c17 -finline-functions -pipe -Wdate-time -D_FORTIFY_SOURCE=3 -Wno-unused -fsigned-char
$ echo "$COB_CFLAGS" | grep -o _FORTIFY_SOURCE | wc -l
1
```

`FACT` — the `cobc` block above is the **complete** version output, quoted in full because it is the golden-file reproducibility anchor and a later run must be able to compare it character for character. `FACT` — `_FORTIFY_SOURCE` appears exactly once, which is the condition under which the compiler writes nothing to stderr and therefore nothing into a golden file.

**A2.13.2 The build matrix.** Three commands, three exit statuses, totals read from the generated JUnit XML rather than from the console summary.

| Command | Exit | JUnit XML totals |
| --- | --- | --- |
| `JAVA_HOME=$JDK8_HOME ./gradlew clean test` | `0` — `BUILD SUCCESSFUL in 4s` | `classes=33 tests=457 failures=0 errors=0 skipped=0 passed=457` |
| `JAVA_HOME=$JDK11_HOME ./gradlew clean test approvalTest` | `0` — `BUILD SUCCESSFUL in 11s` | `classes=33 tests=457 failures=0 errors=0 skipped=0 passed=457` |
| `JAVA_HOME=$JDK21_HOME ./gradlew clean test` | `1` — `BUILD FAILED in 8s` | `classes=33 tests=457 failures=227 errors=0 skipped=0 passed=230` |

`FACT` — the JDK 21 failure signature, quoted from the stack trace: `Caused by: java.lang.IllegalArgumentException: Unsupported class file major version 65`, reached through `org.mockito.internal.creation.bytebuddy.InlineByteBuddyMockMaker` and `net.bytebuddy.agent.ByteBuddyAgent.install`. `INFERENCE` — this is a test-execution failure rather than a build-tool refusal, which is why the unblocker is the Mockito and Byte Buddy bump and not a Gradle version change; reasoning: the build reached test execution and failed inside the mock maker, and 227 of 457 tests — the ones that mock — are affected while the other 230 pass.

`FACT` — the JDK 11 leg was run on a **cold** tree, established by `rm -rf bin temp testruns actual-output.txt` immediately beforehand. That matters: `clean` alone does not remove `bin/` or `temp/`, so a second invocation behaves differently from a first.

**A2.13.3 The vacuous green, verbatim.** `FACT` — from the cold JDK 11 run above, printed under `> Configure project :`, i.e. during Gradle's **configuration** phase, before the tasks its own `dependsOn` declares have executed:

```text
Linux detected
./approvaltest: 1: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 2: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 3: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 4: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 5: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 6: ./temp/approvalTest/cobolcheck: not found
exit from compare: 0
./expected-output.txt matches ./actual-output.txt - PASS
```

`FACT` — measured on the same run: `actual-output.txt` size **0 bytes**; occurrences of `About to launch process` **0**; occurrences of `not found` **6**. `INFERENCE` — the build therefore reports the approval comparison as passing having compiled and executed **zero** COBOL programs, which is precisely the condition the definition of green in the change discipline exists to exclude; reasoning: a zero-byte actual output compared against a non-empty baseline returned success, so the comparison itself cannot be distinguishing content.

**A2.13.4 Dependency closure.** `FACT` — `./gradlew dependencies --configuration compile` exits `0` and reports:

```text
compile - Dependencies for source set 'main' (deprecated, use 'implementation' instead). (n)
No dependencies
```

`INFERENCE` — that single line is the whole fat-jar defect; reasoning: the `fatJar` assembly collects `configurations.compile` at `[build.gradle:L138]`, and a configuration with no dependencies contributes no files, so the archive can only ever contain the project's own classes.

`FACT` — `./gradlew dependencies --configuration runtimeClasspath` exits `0` and resolves nine coordinates:

```text
+--- org.jetbrains:annotations:15.0
+--- javax.xml.bind:jaxb-api:2.3.0
+--- jakarta.xml.bind:jakarta.xml.bind-api:2.3.2
|    \--- jakarta.activation:jakarta.activation-api:1.2.1
\--- org.glassfish.jaxb:jaxb-runtime:2.3.2
     +--- jakarta.xml.bind:jakarta.xml.bind-api:2.3.2 (*)
     +--- org.glassfish.jaxb:txw2:2.3.2
     +--- com.sun.istack:istack-commons-runtime:3.0.8
     +--- org.jvnet.staxex:stax-ex:1.8.1
     +--- com.sun.xml.fastinfoset:FastInfoset:1.2.16
     \--- jakarta.activation:jakarta.activation-api:1.2.1
```

**This is where the supplied transitive list is contradicted, and the correction is now `FACT` rather than `INFERENCE`.** `FACT` — `com.sun.activation:javax.activation:1.2.0`, which the ground truth names as part of the closure, is **absent**; the activation artifact actually resolved is `jakarta.activation:jakarta.activation-api:1.2.1`. `FACT` — two coordinates the supplied list omits are present: `org.jvnet.staxex:stax-ex:1.8.1` and `com.sun.xml.fastinfoset:FastInfoset:1.2.16`. `INFERENCE` — the supplied list was partly inferred rather than resolved; reasoning: it names a plausible artifact that the build does not use and misses two that it does, which is the signature of a closure written from recall rather than read from a resolution report.

`FACT` — `./gradlew dependencies --configuration testRuntimeClasspath` exits `0` and shows the two facts the modernization plan depends on: `org.junit.jupiter:junit-jupiter:5.6.1` resolves **upward** to `5.7.0` because `org.junit:junit-bom:5.7.0` arrives transitively, and `org.mockito:mockito-inline:3.6.0` pulls `mockito-core:3.6.0 -> 3.6.28`, which pulls `net.bytebuddy:byte-buddy:1.10.18`, `net.bytebuddy:byte-buddy-agent:1.10.18` and `org.objenesis:objenesis:3.1`.

**A2.13.5 The vulnerability scan.** `FACT` — executed by `POST https://api.osv.dev/v1/query` with `python3` from the standard library, one request per coordinate. Query instant **`2026-08-14T09:38:28+00:00`**, which is the snapshot identifier because OSV is a live service and publishes no offline snapshot date. `FACT` — **25** Maven coordinates queried, **every response HTTP 200**, and the recorded total is `TOTAL ADVISORIES ACROSS ALL 25 COORDINATES: 0`. `FACT` — no scanner was installed; `trivy`, `grype`, `osv-scanner`, `syft` and `snyk` are all absent from this image, which is why the database was queried directly.

**A2.13.6 The three committed archives.** `FACT` — inspected with Python's `zipfile`; `unzip` is present in this image (`UnZip 6.00 of 20 April 2009`) and nothing was installed for the purpose.

| Archive | sha256 | Bytes | Entries | Top-level | Third-party bytecode entries |
| --- | --- | --- | --- | --- | --- |
| `build/libs/cobol-check-0.2.19.jar` | `46343a5ba4c1dbb1bb6584fa51c9b78b8ad34c4a74f091295e9da11b5e86fa54` | 273,628 | 200 | `META-INF`, `org` | **0** |
| `vs-code-extension/Cobol-check/bin/cobol-check-0.2.19.jar` | `46343a5ba4c1dbb1bb6584fa51c9b78b8ad34c4a74f091295e9da11b5e86fa54` | 273,628 | 200 | `META-INF`, `org` | **0** |
| `build/distributions/cobol-check-0.2.19.zip` | `4e8b703e36eca4c32d3f1fc360a9ef7e991b21c31142d6b4548297483fe64b79` | 245,014 | 17 | `bin`, `config.properties`, `scripts`, `src` | **0** |

`FACT` — the two jars are **byte-identical** to each other, which their equal digests establish directly. `FACT` — a rebuild is **not** byte-identical to either: `JAVA_HOME=$JDK11_HOME ./gradlew fatJar` exits `0` and yields sha256 `3d6fdfa89e405e0ddaba96327c166912e99838e334147ef5137dfc938a322217` at 273,558 bytes, differing from the committed jar in 13 entries by content and 187 by timestamp — see A2.6 for the partition and its two distinct causes. `FACT` — the third-party scan covered the prefixes `javax/`, `jakarta/`, `com/sun/`, `org/glassfish/`, `org/jetbrains/`, `org/intellij/`, `net/bytebuddy/`, `org/junit/`, `org/mockito/` and `org/jvnet/`, and matched nothing in any archive; the jars contain 160 `.class` entries and no nested archive. `FACT` — a refinement worth recording because a naive filter gets it wrong: the jar carries **six** packaged copybook entries — `CCHECKPARAGRAPHSPD.CPY`, `CCHECKRESULTPD.CPY`, `CCHECKWS.CPY`, `DATETIME`, `DFHEIBLK.CPY`, `OUTPUT` — and two of them carry **no file extension**, so an enumeration filtered on `*.CPY` reports four and undercounts. `FACT` — seven message bundles ship alongside them.

**A2.13.7 Re-derivation script for the corpus, table and citation claims.** `FACT` — the following is self-contained, requires only `git` and `python3`, and reproduces the counts in A2.12, the CICS figures in A2.11 and ADR-04, and the copybook disambiguation in Deliverable C. It is quoted so the claims can be re-opened without this session's files.

```bash
#!/usr/bin/env bash
# Run from the repository root. Prints the measured value beside each expectation.
set -u
echo "copy fixtures (expect 41)        : $(git ls-files 'src/main/cobol/copy/' | wc -l)"
echo "  of which non-recursive (39)    : $(git ls-files 'src/main/cobol/copy/' | grep -c '^src/main/cobol/copy/[^/]*$')"
# NB: a `src/main/cobol/*.CBL` pathspec would report 53 — git's pathspec `*` matches `/`
# too, so it also captures the .CBL fixtures under copy/ and copy/Outrec/. Anchor instead.
echo "programs (expect 15)             : $(git ls-files 'src/main/cobol/' | grep -icE '^src/main/cobol/[^/]+\.cbl$')"
echo ".cut files (expect 23)           : $(git ls-files 'src/test/cobol/' | grep -icE '\.cut$')"
echo "suite dirs (expect 16)           : $(git ls-files 'src/test/cobol/' | cut -d/ -f4 | sort -u | wc -l)"
echo "testfiles/ (expect 14)           : $(git ls-files 'testfiles/' | wc -l)"
echo "copybooks/ (expect 5)            : $(git ls-files 'copybooks/' | wc -l)"
python3 - <<'EOF'
import re, subprocess, hashlib
ls = lambda p: subprocess.run(["git","ls-files",p],capture_output=True,text=True).stdout.split()
cut=[f for f in ls('src/test/cobol/') if f.lower().endswith('.cut')]
s=c=0
for f in cut:
    t=open(f,encoding='utf-8',errors='replace').read()
    t=re.sub(r'"[^"\n]*"','""',t); t=re.sub(r"'[^'\n]*'","''",t)  # strip literals FIRST
    s+=len(re.findall(r'testsuite',t,re.I)); c+=len(re.findall(r'testcase',t,re.I))
print(f"TestSuite (expect 25)            : {s}")
print(f"TestCase  (expect 171)           : {c}")
eib="src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java"
txt=open(eib).read()
keys=re.findall(r'put\(\s*"([A-Z0-9]+)"',txt)
dup=sorted({k for k in keys if keys.count(k)>1})
print(f"EIB put( sites (expect 76)       : {len(re.findall(chr(92)+'bput'+chr(92)+'(',txt))}")
print(f"EIB unique names (expect 75)     : {len(set(keys))}")
print(f"EIB duplicated key (expect LENGERR): {dup}")
print(f"  its two lines (expect 51, 84)  : "
      f"{[i for i,l in enumerate(txt.splitlines(),1) if 'put(\"LENGERR\"' in l.replace(' ','')]}")
for p in ["src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY",
          "copybooks/CCHECKWS.CPY"]:
    b=open(p,'rb').read()
    print(f"{p}\n    md5={hashlib.md5(b).hexdigest()}  lines={len(b.decode('utf-8','replace').splitlines())}")
EOF
```

Expected digests, so a drifted checkout is detectable: the packaged copybook is `09dd62ab27c658e67ec5a7510e7222dc` at 129 lines and the legacy one is `1a4f5941a2ce738326e3404da516ed2a` at 116 lines. `INFERENCE` — quoting both is what makes the two files distinguishable in a citation; reasoning: they share a filename, differ in content, and an abbreviated path cannot select between them.

**A2.13.8 Primary sources for the four web-research findings.** `FACT` — each finding in this plan that rests on external documentation rather than on this repository is anchored to its official source:

| Finding | Primary source |
| --- | --- |
| Gradle 9 requires JVM 17+ for the daemon while still compiling and testing on Java 8 via toolchains, and makes archive tasks reproducible by default | `https://docs.gradle.org/current/userguide/upgrading_major_version_9.html` and `https://docs.gradle.org/current/userguide/compatibility.html` |
| Mockito 5 requires Java 11+, drops the separate inline artifact because the inline mock maker became the default, and Mockito 4's Byte Buddy does not support Java 21 | `https://github.com/mockito/mockito/releases` (5.0.0 release notes) and `https://javadoc.io/doc/org.mockito/mockito-core/latest/org.mockito/org/mockito/Mockito.html` |
| The CycloneDX Gradle plugin's 3.x line targets Gradle 9 and documents output to `build/reports/sbom/` | `https://github.com/CycloneDX/cyclonedx-gradle-plugin` |
| GnuCOBOL claims conformance to no standard level while passing a large portion of the NIST COBOL 85 suite, which is distributed as a separate archive | `https://gnucobol.sourceforge.io/` and the `cobc --help` conformance options in this image |

`INFERENCE` — these are cited as URLs rather than as `file:line` because they are the one class of claim this repository cannot establish; reasoning: the evidence rule requires a locator, and for an external tool's documented behaviour the retrieved document *is* the locator. `UNKNOWN` — the exact patch versions to pin for Mockito, the aligned JUnit release and the coverage tool are deliberately not fixed here; they are resolved against the manifest at execution time and recorded in the run's handoff, as A2.13.9 states.

**A2.13.9 What this appendix does not establish, stated rather than implied.**

| Claim | Status | Why |
| --- | --- | --- |
| The three archives are byte-reproducible across two builds | **They are not** — established in this session, not assumed | `JAVA_HOME=$JDK11_HOME ./gradlew fatJar` exits `0` and produces sha256 `3d6fdfa89e405e0ddaba96327c166912e99838e334147ef5137dfc938a322217` at 273,558 bytes against the committed `46343a5ba4c1dbb1bb6584fa51c9b78b8ad34c4a74f091295e9da11b5e86fa54` at 273,628. An entry-by-entry comparison partitions the 200 entries into **13 differing in content** (line endings) and **187 differing in timestamp only**, which is the corrected finding in A2.6. The digests in A2.13.6 are of the **committed** archives; this row is the only rebuild measurement |
| Exact dependency versions to adopt in the modernization run | `UNKNOWN` by choice | The constraint each must satisfy is stated in the plan; the version is resolved at execution time and recorded in the handoff, so no number here is invented |
| Behaviour on Windows or macOS | `UNKNOWN` | Only the Linux container was executed. The CI matrix covers three platforms, but no run of it is part of this baseline |
| That `Living-Mainframe` holds commits beyond PR #411 | `FACT`, established in A2.9 from the fetched refs | Included here only to note that it rests on a fetch performed in this session, not on the API capture |
| The upstream wiki and CI history | Not captured | Out of scope of the harvest by design; recorded in the harvest manifest's own limits section rather than here |

---

## A3. Run sequence

> **This item is a self-contained, copy-pasteable block.** It carries every definition it relies on, because every later run prompt embeds it into a session that has none of this document's context. It contains no cross-references to other items.

### A3.0 Definitions this block relies on

**The two orthogonal labels.** Every run carries a permitted-surface label and a behaviour-discipline label, written `<surface>·<discipline>`. A run whose two labels are not both stated has no oracle and must not start. The axes must not be collapsed: *which code a run may touch* and *whether observable behaviour may change* are independent, and collapsing them produces runs whose scope contradicts their own discipline.

**Both rule sets below are reproduced verbatim from the governing constraint text — character for character, including its American spelling of "behavior", its `·` separators and its parenthesised definitions. They are inside a fenced block so that nothing can reflow or reword them, and so that "verbatim" is mechanically checkable rather than a claim. Do not paraphrase them, do not re-order them, and do not carry a reworded copy into any document you produce.** **This block is the canonical copy; the two run prompts in Deliverable B reproduce it byte-for-byte, and any copy that differs from it is wrong.**

```text
Permitted surface: S1 non-production (tests, golden files, corpus, harness, CI) · S2 peripheral production (build scripts, wrapper, packaging, dependency declarations, identifiers, file layout, headers — no parsing, interpretation or generation logic, and no passing-through improvements) · S3 enumerated core (named classes and methods on the parse/interpret/generate path, enumerated before the run starts; anything outside the enumeration is S2-disciplined within that run) · S4 new code (new classes and packages isolated from the existing large classes, plus enumerated touches to shared code where unavoidable)

Behavior discipline: PRES preserving (any observable change is a defect) · CORR corrective (specific changes are the objective, each itemized before the run starts; an unitemized golden-file change is a defect even if it looks like an improvement; may add a config key whose default reproduces current behavior, but no new DSL syntax, CLI flag or output format) · ADD additive (new behavior required, landing behind new syntax and new keys defaulting to current behavior, shipping with tests and documentation in the same change)
```

- **Second disciplines must be declared in advance** with their own oracle. Two *undeclared* disciplines are the defect. Where `S3`/`S4` work must touch code outside its enumeration, that touch drops to `S2` and its discipline is declared explicitly — preserving by default, corrective only if a defect genuinely must be fixed to land the run.

**The definition of green.** Green means **all** of: `clean test` reports **457 or more** tests with zero failures · the approval harness **compiled and executed a non-zero number of COBOL programs** · `actual-output.txt` is **non-empty** · the comparison against the approved baseline **genuinely matched**. The build is currently green for the wrong reason — vacuously, on a zero-byte `actual-output.txt` with zero COBOL programs compiled. An agent that satisfies the invariant by leaving the harness broken has failed, not succeeded. **Any run reporting green must state the executed COBOL program count.**

**The safety net.** 457 tests across 34 named test classes with zero failures, of which 33 classes actually execute tests. Existing assertions must not be weakened, deleted, disabled, renamed or narrowed to make a change pass. The single mechanism by which a pre-existing assertion may change is an itemized corrective task naming the class and method **before** the run starts, recording old and new expected values in the handoff with the motivating issue number. The rename is the one carve-out: it necessarily edits every existing test source, so "unmodified" cannot be taken literally, and the enforcement is instead that the commit's diff over `src/test/` is mechanically verifiable as matching only the rename script's token set.

**Standing environment precondition for every run.** The first command of every session is `. /etc/profile.d/blitzy-cobolcheck-env.sh`. Nothing is on `PATH` in a non-login shell, and a probe that fails *before* sourcing it is a probe error, not a finding.

**Ordering.** Sequencing is expressed exclusively as `BLOCKED-BY`. No durations, dates or estimates appear anywhere.

### A3.1 Two structural properties of this sequence

**Runs 1a and 1b are deliberately kept apart.** A single run doing both would have a **circular oracle** — the harness judged fixed because its output looks right, and the output judged right because the harness works — leaving any failure unattributable between "still broken" and "captured the wrong thing".

**Modernization precedes bug fixing.** Modernization must be provably behaviour-preserving. If behaviour fixes landed in the same run, a changed golden file would be ambiguous between a migration defect and an intended fix.

### A3.2 Run 1a — Approval-harness repair

- **Labels:** `S2·CORR`
- **Scope:** two itemized changes to the build script, plus one mechanically-required relocation. (i) Move the entire `approvalTest` task-configuration body at `[build.gradle:L205-L239]` into `doLast`, leaving the `dependsOn copyJarToBin, copyRunScripts` declaration at `[build.gradle:L203]` outside it. The relocation covers the whole body, not merely the two `.execute()` calls at `[build.gradle:L213]` and `[build.gradle:L216]`, because the body also contains `new BuildHelper().compareFiles(...)` at `[build.gradle:L230]` and the `throw new StopExecutionException(...)` at `[build.gradle:L233-L235]`; relocating only the executions would leave the comparison at configuration time, judging output produced later. This is a mechanical precision of change (i), not a third change. (ii) Change the comparison loop guard at `[build.gradle:L273]` so that **unequal stream lengths fail** instead of reaching `return 0` at `[build.gradle:L286]`.
- **Required relocation:** `BuildHelper` is an inline **Groovy** class declared in the build script at `[build.gradle:L263-L296]`, not a Java file, and is structurally unreachable from anything under `src/test/java`. It moves to `buildSrc/`, which is the mechanical precondition of change (ii) rather than a third change, because the mandated test proving `compareFiles()` fails on unequal stream lengths and on an empty stream cannot otherwise be written.
- **Entry precondition:** `clean test` green at 457/457 on JDK 11 with `JAVA_HOME` set to the JDK 11 home exported by the activation script.
- **Success oracle:** `clean test` remains green at 457 or more tests with zero failures; the repaired harness **compiles and executes a non-zero number of COBOL programs**, and the run states that count; `actual-output.txt` is non-empty; the new `buildSrc` test asserts a failing verdict for unequal stream lengths **and** for an empty stream, and passes; and `approvalTest` **fails** against the un-refreshed baseline.
- **Exit criteria:** `approvalTest` ends **RED by design**. The run repairs the harness without refreshing the stale approved file, so the comparison necessarily fails. That red is the evidence the repair worked and is the required exit state. **The run must not revert on account of it** — reverting would undo the fix and restore the vacuous pass. `clean test` must still be green at 457/457. No other run in this programme may end red.
- **Tag:** `run-1a-approval-harness-repair`
- **BLOCKED-BY:** nothing. This is the first run.
- **Explicit deferrals:** the CI GnuCOBOL install step is **not** in this run's surface; this run reports its executed-program count from the **local** run only. Baseline regeneration is deferred. Any change to `Interpreter`, `InterpreterController`, `LineRepository`, `Generator` or `TestSuiteParser` is out of scope. Adopting upstream pull request #338 is superseded by this run rather than deferred, because that pull request conflicts on exactly `build.gradle`, `approvaltest` and `approvaltestWin.cmd`, and its central change — `dependsOn copyJarToBin, copyRunScripts` — is already present at `[build.gradle:L203]` while the harness remains vacuous.
- **Adding tests is always in scope** and never counts against the itemized enumeration. "Exactly two itemized changes, nothing else" governs production and build code only. A run whose repair is not provable without a new test is *required* to add one. Only the *modification* of pre-existing tests is restricted.

### A3.3 Run 1b — Characterization safety net

- **Labels:** `S1·PRES`, plus a **declared secondary surface `S2·PRES`** bounded to `build.gradle` test-registration and golden-comparison wiring only — see the dedicated entry below. A run whose declared surfaces do not cover the files it edits has no oracle for those edits and must not start.
- **Scope:** a characterization corpus and golden-file set, a golden-file normalization filter with its own self-test, the regenerated approval baseline, and the CI GnuCOBOL install step. The corpus is drawn from the repository's own material: 15 COBOL programs under `src/main/cobol/`, **41** copybook fixtures under `src/main/cobol/copy/` (39 directly in the directory plus 2 under `src/main/cobol/copy/Outrec/`, so a non-recursive listing sees 39 and a recursive one 41 — the supplied ground truth's 40 matches neither), 16 suite directories holding 23 `.cut`/`.CUT` files under `src/test/cobol/`, 14 fixtures under `testfiles/`, plus `examples/SAMPLEJC` and `testsuites/`. The harness currently invokes exactly six programs — `NUMBERS`, `ALPHA`, `GREETING`, `FILECOPY`, `MOCKTEST`, `DPICNUMBERS` — so nine of the fifteen are exercised end-to-end by nothing: `BIPM012`, `DB2PROG`, `LONGLINESANDNUMBERS`, `MOCK`, `MOCKPARA`, `REPLAC`, `RETURNCODE`, `TESTNESTED`, `WS88LEVEL`. Widening the harness to cover them is the single highest-leverage risk reduction available and costs nothing in production risk, because this run touches no production code.
- **Artifacts:** `src/test/corpus/`, `src/test/golden/` (the regenerated approval baseline is one of the golden files), and the normalization filter under `src/test/java/io/blitzy/cbltest/golden/`.
- **The normalization filter must canonicalize both the old and the new identifier strings**, because it is built here and the rename happens later. The four token pairs are: Java package namespace `org.openmainframeproject.cobolcheck` → `io.blitzy.cbltest`; Gradle group `org.openmainframeproject` → `io.blitzy`; artifact ID and `productName` `cobol-check` → `cbltest`; product display name `Cobol Check` / `COBOL Check` → `Blitzy COBOL Test`. The filter carries its own test proving it normalizes **only** those four token pairs and nothing else.
- **Declared secondary surface: `S2·PRES`, bounded to the build script and nothing else.** `INFERENCE` — the primary surface cannot cover this run's own work, so declaring it is mandatory rather than optional; reasoning: registering a new test source set and wiring a golden-file comparison task requires editing `build.gradle`, which the surface vocabulary classifies as `S2` peripheral production, and a run that edits `S2` under an `S1`-only label has no oracle for the edit it actually made. **Exactly what the secondary surface permits:** adding declarations to `build.gradle` that (i) register the new test classes and the golden-file comparison so Gradle executes them, and (ii) declare the corpus and golden directories as task inputs and outputs. **Nothing else.** No dependency change, no wrapper change, no plugin change, no JaCoCo or Sonar change, no `fatJar` change, no modification to the behaviour of any pre-existing task. **Its own oracle, checkable independently of the `S1` oracle:** `git diff <start-commit> HEAD -- build.gradle` consists solely of added lines registering test execution or golden comparison — no line is deleted and no pre-existing line is modified — and `./gradlew tasks` reports every task that existed at the start commit with the same name and the same `dependsOn` set. The discipline stays `PRES` on both surfaces: the build script gains the ability to run new tests and gains nothing else.
- **Entry precondition:** the tag `run-1a-approval-harness-repair` exists; `RUN-1A-HANDOFF.md` is present and reconciled; `clean test` green at 457/457; `approvalTest` red against the stale baseline, which is the expected inherited state.
- **Success oracle:** full green restored under the mechanical definition — `clean test` at 457 or more with zero failures, the harness compiling and executing a non-zero and **stated** number of COBOL programs, `actual-output.txt` non-empty, and the comparison genuinely matching the regenerated baseline. Additionally: every corpus program has a golden file; the normalization filter's self-test passes; and CI is green on a runner that installs GnuCOBOL.
- **Exit criteria:** green, tagged, with the verbatim `cobc --version` recorded in the handoff as the toolchain anchor, and with the record of which corpus programs exercise which copybook.
- **Tag:** `run-1b-characterization`
- **BLOCKED-BY:** `run-1a-approval-harness-repair`
- **Explicit deferrals:** no production code changes; no dependency changes; no rename. The two known harness defects are characterized, **not** fixed: that `-p FILECOPY` does not resolve against `src/main/cobol/FileCopy.cbl` on a case-sensitive filesystem and raises an unhandled exception, and that the Linux harness issues six separate processes `[approvaltest:L1-L6]` while the Windows harness issues one `[approvaltestWin.cmd:L1]`. Both are corrective work and belong to a later run; this run's job is to capture current behaviour, including the failure.
- **Three environment facts must be established before any capture**, because golden files are otherwise not reproducible: `cobc --version` recorded verbatim as the toolchain anchor; `COB_CFLAGS` exported with a **single** `-D_FORTIFY_SOURCE` value, because Debian-family GnuCOBOL packaging defines it twice and the resulting compiler warning is written to stderr, which `ProcessOutputWriter.writeOutPutToConsole()` prints to `System.out` at `[src/main/java/org/openmainframeproject/cobolcheck/features/launcher/ProcessOutputWriter.java:L112-L113]` and therefore into every golden file captured; and `core.autocrlf=false`, because the project has line-ending sensitivity in column-significant fixed-format COBOL. The anchor becomes an entry precondition for the next run, and a mismatch between this run's re-read and the planning run's recorded anchor is itself a finding, meaning the image is not stable across sessions and every golden file is suspect.
- **Corpus programs that exercise either legally-sensitive copybook must supply it from an explicit corpus-local path**, so the corpus survives a later unvendoring and so any later copybook-traceable golden-file diff is unambiguous evidence that the file is loaded at runtime.

### A3.4 Run 2 — Modernization

- **Labels:** `S2·PRES` for the run as a whole, **plus one declared second discipline `S2·CORR`** covering exactly one itemized observable change: the repair of the `xml` result format. Both label pairs are stated here, before the run starts, which is what makes the second discipline *declared* rather than a defect. A run may carry a second discipline only by itemizing it in advance with its own oracle; two **undeclared** disciplines are the defect.
- **The declared second discipline, itemized in full.** `S2·CORR`, one change and one only: replacing `configurations.compile` with `runtimeClasspath` in the fat-jar `from{}` block at `[build.gradle:L138]`. `FACT` — `configurations.compile` resolves to nothing, since every one of the eight dependency declarations at `[build.gradle:L97-L106]` is `implementation` or `testImplementation` and `./gradlew dependencies --configuration compile` reports `No dependencies`; all three committed archives contain zero entries under `javax/`, `jakarta/`, `com/sun/`, `org/glassfish/` or `org/jetbrains/`. `INFERENCE` — the observable consequence is that the `xml` result format stops throwing, because the class reported missing at `[src/main/java/org/openmainframeproject/cobolcheck/features/launcher/Formatter/Formats/XMLFormat.java:L19]` was never packaged rather than being absent from the dependency set; reasoning: one configuration change both fills the jar and removes the linkage failure, so the packaging fix and the format repair are the same edit and cannot be separated into different runs. **Nothing else observable may change under this label**: not the `html` format, not exit codes, not the copybook resolution path, not any parsing behaviour. **The formatter itself is not edited**: nothing changes in `[src/main/java/org/openmainframeproject/cobolcheck/features/launcher/Formatter/Formats/XMLFormat.java]` at all, because the class it needs was never packaged rather than wrongly used.
- **The second discipline's own oracle, independent of the preserving oracle.** Three conditions, all required: (i) the fat jar contains the runtime dependencies, asserted as an **archive entry-name set** comparison before and after — entry names, not timestamps, which will legitimately change; (ii) a new test invokes the `xml` result format end to end and asserts it produces a well-formed JUnit XML document **and does not throw**, where the same test is required to fail before the change and pass after it; (iii) the JUnit XML **schema**, the format name `xml` and the style enumeration values are unchanged, so the repair is additive from every consumer's standpoint. `INFERENCE` — this oracle has to be separate from the byte-identical golden-file oracle because the two would otherwise contradict each other; reasoning: the preserving oracle demands no observable change while this one demands exactly one, and only naming them separately lets both be checked. **Its own regression bound:** the `txt`/`directOutput` default path is byte-identical and every golden file is byte-identical after normalization, so the newly packaged runtime closure is an observed addition rather than an unnoticed one. **What it does not license:** the `html` format is *not* repaired here — it fails at a deliberate `throw` on a data-transfer-object type mismatch and belongs to Run 3 — and no other behaviour correction rides along on this declaration.
- **Scope:** Gradle wrapper 6.9.4 → 9.x with the daemon on JDK 17 or higher and a toolchain pinning *shipped* bytecode to Java 8; the dependency changes; the identity rename; the SBOM; removal of the Sonar integration; deletion of committed binaries and junk by ordinary commit, **retaining `expected-output.txt` and `gradle/wrapper/gradle-wrapper.jar`**; deletion of the planning run's own artifacts from the working tree by the same ordinary commit — `PROGRAM-PLAN.md` and all twelve paths under `upstream-harvest/` — which is itemized here because they are tracked in this repository contrary to the planning run's own boundary and nothing in the product reads them; filling the governance stubs; the architecture-decision-record tree at `docs/adr/NNNN-<slug>.md`, created from nothing because no `doc`, `docs`, `wiki` or `adr` directory exists anywhere; and the first automated release workflow, which must be written from nothing because `.github/` holds exactly three files and none of them publishes a JAR. **No upstream pull request is adopted in this run**: of the eight open upstream pull requests, this run owns **none** — #338 is superseded by Run 1a and the remaining seven (#330, #336, #337, #408, #409, #410, #411) all belong to Run 3. The npm work in this run is manifest hygiene only — version unification, dependency reclassification, the `engines.vscode` floor, committed lockfiles and `npm ci` — none of which changes what the extension does; pull request #336's infinite-loop timeout is an observable change and belongs to Run 3.
- **Entry precondition:** the tag `run-1b-characterization` exists; full green under the mechanical definition; the golden-file set and the normalization filter are in place; and the `cobc --version` string re-read at the start of this run **matches** the anchor recorded in the previous run's handoff. **For the planning-artifact deletion half only**, one further precondition: written human confirmation that `PROGRAM-PLAN.md` and `upstream-harvest/` are preserved outside this repository. Absent that confirmation, that half alone is deferred and the rest of the run proceeds — the upstream capture is one-time and non-repeatable, so deleting it on the assumption that a copy exists is not an acceptable risk. One further entry precondition applies to the extension-publish half: the Marketplace publisher credential described in the next entry is either provisioned or explicitly recorded as absent.
- **The one credential this programme adds, and the only human provisioning step in it.** `FACT` — the extension is published today by a third-party action reading a **misspelled** repository secret: `pat: ${{ secrets.VS_CODE_EXTENTION_SECRET }}` at `[.github/workflows/DeployExtension.yml:L27]` ("EXTENTION", not "EXTENSION"). `FACT` — that secret authenticates the **old** publisher, `openmainframeproject`, and ADR-07 changes the publisher to `blitzy` and the extension identifier to `blitzy.cbltest-extension`. `INFERENCE` — a marketplace publisher token is scoped to its publisher, so the existing secret cannot authenticate the new identity and the rename therefore *requires* a new credential rather than merely benefiting from one; reasoning: the publish call names the publisher, and a token issued for a different publisher is rejected. **Entry precondition:** a Marketplace publisher personal access token issued under the `blitzy` publisher, exposed as the repository secret **`VSCE_PAT`** — correctly spelled, superseding `VS_CODE_EXTENTION_SECRET`, which is deleted in the same change so the misspelling cannot be reintroduced. **Consumption constraint, stated because this build makes it necessary:** the secret is read **only** at publish time, inside the publishing workflow's own step. It is never referenced in `build.gradle`, never read at Gradle **configuration** time, and never passed as a Gradle property — a build that already executes shell commands during configuration is exactly the build in which a configuration-time secret read would leak into unrelated invocations. **Publish oracle:** the workflow authenticates as `blitzy`, publishes the extension under `blitzy.cbltest-extension`, and the published version is `0.3.0`; the workflow log shows no credential value and no credential fragment. **If the token is not provisioned when this run executes, the extension-publish job is skipped with an explicit message naming `VSCE_PAT` as the missing precondition, and the run's handoff records the extension as unpublished.** `INFERENCE` — the JAR release is deliberately *not* gated on it; reasoning: the JAR release workflow needs only the automatically-provided repository token, so a missing marketplace credential must not be allowed to block the first automated release this programme exists to produce. **Users are not silently updated either way:** a marketplace identity change is a new item rather than an update, so existing installations of `openmainframeproject.cobol-check-extension` keep working and receive nothing — which A6.2 records as an announced change.
- **Success oracle (the `S2·PRES` oracle; the `S2·CORR` oracle is stated separately above and both must hold):** every golden file is **byte-identical** after normalization by the filter built in the previous run; `clean test` green at 457 or more with zero failures on the JDK 11 and JDK 21 legs; the approval harness compiles and executes a non-zero and **stated** number of COBOL programs; the fat jar contains the runtime dependencies; the SBOM exists at `build/reports/sbom/bom.json` and `bom.xml`; a release workflow produces a downloadable JAR; and the diff over `src/test/` is mechanically verifiable as matching only the rename script's token set.
- **Exit criteria:** green, tagged, with the golden-file status itemized file by file in the handoff.
- **Tag:** `run-2-modernization`
- **BLOCKED-BY:** `run-1b-characterization`
- **Explicit deferrals:** no behaviour correction beyond the single declared `S2·CORR` item above. The `html` format repair, the exit-status repair, the copybook resolution change and every parsing fix are deferred to Run 3, because this run's preserving discipline makes any *other* observable change a defect. In particular the `html` failure is **not** fixed here: `FACT` — it is a deliberate `throw` on a data-transfer-object type mismatch, `if (dataTransferObject instanceof String)` at `[.../Formatter/Formats/HTMLFormat.java:L19]` raising `IncompatibleClassChangeError` at `[:L26-L27]`, so `INFERENCE` — adding JAXB to the jar cannot change an `instanceof String` test and treating it as a linkage problem would leave it unfixed after the dependency work lands; reasoning: the two formats fail for unrelated reasons that happen to surface together in the README.
- **The JDK 8 test leg does not survive this run.** The shipped artifact remains Java 8 bytecode and its runtime dependencies are Java 8 compatible, but the Mockito bump required to unblock JDK 21 requires Java 11 or later at test runtime. "No longer *tested* on Java 8" is a real change of posture and is spent deliberately here rather than absorbed silently into a dependency bump.

### A3.5 Run 3 — Defect correction and upstream pull-request adoption

- **Labels:** `S3·CORR`, plus two declared out-of-enumeration touches — **`S2·CORR`** for the extension subtree (itemized correction (h)) and **`S1·CORR`** for the single permitted pre-existing-assertion change (itemized correction (i)). Both are specified with their own oracles below. This run therefore carries **nine** itemized corrections, not seven.
- **Enumerated core surface, fully package-qualified.** Anything outside this list is `S2`-disciplined within this run:
  - `org.openmainframeproject.cobolcheck.Main.main(String[])`
  - `org.openmainframeproject.cobolcheck.services.Constants` — the exit-status constants
  - `org.openmainframeproject.cobolcheck.features.Status.StatusController.setExitStatusHalt()`, `.isExitStatusHalt()`, `.exitProgram()`
  - `org.openmainframeproject.cobolcheck.features.Status.CurrentStatus`
  - `org.openmainframeproject.cobolcheck.workers.Initializer.run()`
  - `org.openmainframeproject.cobolcheck.features.interpreter.LineRepository.addExpandedCopyStatementsToFileSectionStatements(...)` and `.getExpandedCopyDB2Statements(String)`
  - `org.openmainframeproject.cobolcheck.features.interpreter.CopybookExpander.expand(List,String,StringTuple)`, `.expandDB2(List,String)`, `.getIncludedLines(String)`
  - `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.updateLineRepository(...)` — specifically the working-storage `EXEC SQL INCLUDE` branch
  - `org.openmainframeproject.cobolcheck.features.interpreter.CobolReader` — for the line-filtering adoptions
  - `org.openmainframeproject.cobolcheck.services.StringHelper`
  - `org.openmainframeproject.cobolcheck.features.launcher.Formatter.Formats.HTMLFormat.writeInFormat(String)` and the five data-transfer objects under `org.openmainframeproject.cobolcheck.features.launcher.Formatter.DataTransferObjects`
  - `org.openmainframeproject.cobolcheck.services.log.Log` and `org.openmainframeproject.cobolcheck.services.log.LogLevel`
  - Note the package correction: `Keywords` is `org.openmainframeproject.cobolcheck.features.testSuiteParser.Keywords`, **not** `services.cobolLogic.Keywords`; nothing exists at the latter path.
- **Scope:** the itemized behaviour corrections plus the **seven** upstream pull requests routed to this run — #330 (intent only), #336 (intent only, on the extension manifests as the previous run leaves them), #337 (fixture harvest only), #408, #409, #410 (fixture harvest only) and #411 — preserving author attribution in the commits that re-apply them, since all are contributions by named individuals under the project licence and all are locked by the archive so they must be re-applied rather than merged. **The eighth is not this run's:** #338 is superseded by Run 1a, whose repair is its entire substance. That accounts for all eight — seven here, one at Run 1a, none anywhere else — so this list is complete as written and no other section may assign them differently.
- **Itemized corrections, declared in advance:** (a) the exit-status repair, which requires a **new distinct nonzero exit code** because only `STATUS_NORMAL = 0` and `STATUS_HALT = 8` exist today and a completed GnuCOBOL program returns 0 or 4 regardless of assertion outcomes; its oracle already exists uncovered in the repository at `src/test/cobol/RETURNCODE/ReturnCode-4.cut`, a nine-line suite deliberately failing at return code 4. (b) The `html` output format, which fails at a **deliberate throw** on a data-transfer-object type mismatch — `if (dataTransferObject instanceof String)` at `[.../Formats/HTMLFormat.java:L19]` raising `IncompatibleClassChangeError` at `[L26-L27]` — so the repair is data-transfer-object wiring, not a dependency change; treating it as a linkage problem would leave it unfixed after the dependency work lands. (c) `COPY ... REPLACING`, repaired by routing through the pseudo-text tokenizer that already exists rather than writing a new parser. (d) Informational output leaking past a disabled log level. (e) The unhandled crash when a test glob matches no suite. (f) The `-p FILECOPY` case-resolution failure and the structural non-equivalence of the two approval harnesses. (g) The working-storage `EXEC SQL INCLUDE` platform branch, whose intent must be made explicit **without** inserting a `break`. (h) **The extension's unbounded run loop**, adopting the intent of pull request #336 to add a configurable timeout, closing issue #287; its files are the extension manifests and scripts, which lie outside this run's enumerated core, so this item is peripheral-surface within this run and its corrective discipline is declared here explicitly. (i) **One pre-existing assertion change**, the only one this run makes: class `org.openmainframeproject.cobolcheck.MessagesTest`, method `it_retrieves_a_message_with_substitution_values`, whose expected value is currently the transcribed English string `"ERR001: testSuite is null on entry to Generator.runSuite() method."` at `[src/test/java/org/openmainframeproject/cobolcheck/MessagesTest.java:L12-L13]` and becomes the same message resolved from its bundle key, motivating issue #127.
- **On (c):** `org.openmainframeproject.cobolcheck.services.cobolLogic.replace` holds six classes — `Replace`, `ReplaceSet`, `ReplaceStatementLocator`, `ReplaceToken`, `ReplaceTokenType`, `ReplaceTokenizer` — with `LEADING`/`TRAILING` and multi-pair support, backed by five dedicated test classes, and it is live on the generation path. It implements the COBOL `REPLACE` **statement** rather than `COPY ... REPLACING`, established from its own javadoc rather than assumed, but the two directives share identical pseudo-text lexis. The defective copy path handles a single pair through hardcoded token positions at `[.../features/interpreter/LineRepository.java:L110-L113]` — guarded by a suspect `||` at `L111-L112`, where either `REPLACING` or `BY` alone satisfies the condition — and performs substitution with a regex `replaceAll` at `[.../features/interpreter/CopybookExpander.java:L72]` and `[:L79]`, which treats pseudo-text as a regular expression. Both halves live in different files, so a repair scoped to one file would be incomplete.
- **On (g):** at `[.../features/interpreter/InterpreterController.java:L467-L481]`, `case ZOS:` at `L468` performs an early `return;` at `L469-L470` for `SQLCA`/`SQLDA` and then falls through to `default:` at `L471` with **no** `break`. The consequence is that a non-SQLCA include on z/OS is expanded **exactly once**, identically to every other platform — it is **not** expanded twice. The missing `break` is therefore **load-bearing**: inserting one would stop non-SQLCA z/OS includes being expanded at all, which is a regression. The corrective action is to hoist the early return out of the switch so the intent is expressed rather than implied, under a golden file proving behaviour is unchanged.
- **Two touches fall outside the enumerated core surface and their disciplines are declared here rather than discovered mid-run.** `INFERENCE` — the surface rule requires it: work outside an `S3` enumeration drops to `S2` and is preserving **by default**, corrective only where a defect genuinely must be fixed to land the run; reasoning: an undeclared corrective touch is indistinguishable from scope creep after the fact. **(h) The extension subtree** — `vs-code-extension/**`, TypeScript, not on the parse/interpret/generate path — is an `S2·CORR` touch. Its oracle: a run that would previously not terminate now terminates at the configured timeout with a diagnostic naming the timeout, the timeout is a new configuration key whose default reproduces current behaviour for any run that completes, and no other extension behaviour changes. **(i) One test source under `src/test/java`** is an `S1·CORR` touch, and it is the run's single permitted pre-existing-assertion change. Its oracle: exactly one assertion changes, in the class and method named in item (i) above; the handoff's §10 table carries its old value, its new value and issue #127; and `git diff <start-commit> HEAD -- src/test/java` shows no other modification, rename, deletion or narrowing.
- **Entry precondition:** the tag `run-2-modernization` exists; full green; the golden-file set is byte-stable under the normalization filter; the toolchain anchor matches.
- **Success oracle:** every itemized correction has a test that fails before and passes after; **every** golden-file change is itemized in advance, and an unitemized golden-file change is a defect even if it looks like an improvement; `clean test` green with zero failures; the harness compiles and executes a non-zero and stated number of COBOL programs; the `RETURNCODE` suite now produces a distinct nonzero process exit code; and the per-pull-request disposition is recorded with its evidence.
- **Exit criteria:** green, tagged, with the new exit code recorded in the handoff's public-surface section and announced in the changelog.
- **Tag:** `run-3-bugfix-pr-adoption`
- **BLOCKED-BY:** `run-2-modernization`
- **Explicit deferrals:** no new mock syntax, no new CLI flag, no new output format — all three are outside the corrective discipline. The stub-to-mock generalization is deferred. Pull request #330 is **not** adopted as authored.

### A3.6 Run 4 — Stub-to-mock architecture

- **Labels:** `S3·PRES`
- **Enumerated core surface, fully package-qualified. This surface spans two packages, so an unqualified enumeration would be ambiguous:**
  - `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.shouldLineBeStubbed(CobolLine, State)` — the predicate, at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/Interpreter.java:L272-L294]`
  - `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.checkForBatchFileIOStatement(CobolLine)`
  - `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.shouldCurrentLineBeStubbed()` — emission site 1, at `[.../features/interpreter/InterpreterController.java:L145-L158]`, emitting the pair at `L149-L150`
  - `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.shouldCurrentStatementBeStubbed()` — emission site 2, at `[.../features/interpreter/InterpreterController.java:L160-L172]`, emitting the identical pair at `L165-L166`
  - `org.openmainframeproject.cobolcheck.features.interpreter.State`
  - `org.openmainframeproject.cobolcheck.features.interpreter.LineRepository`
  - `org.openmainframeproject.cobolcheck.features.interpreter.CobolReader.getCurrentStatement()`, `.getCurrentLine()`, `.putNextLine(String)`
  - `org.openmainframeproject.cobolcheck.services.Constants` — the `EXEC SQL`, `EXEC CICS`, `END-EXEC` and `CALL` tokens
- **The seam is three methods across two packages, not two.** The predicate lives in `Interpreter`; the two emissions live in `InterpreterController`. Emission site 2 is the one that handles multi-line statements, which is exactly what `EXEC SQL`, `EXEC CICS` and batch file I/O statements are. An enumeration that omits it would leave the generalized hook still emitting a bare `CONTINUE` for the statements the later feature runs care about most.
- **A structural asymmetry the strategy must preserve.** `shouldCurrentStatementBeStubbed()` iterates `reader.getCurrentStatement()` at `L161` but performs its `endsInPeriod` check against `reader.getCurrentLine()` at `L164`, and its `return true` at `L168` sits **outside** the `PROCEDURE_DIVISION` guard at `L163` — so it reports "stubbed" without emitting anything when the flag is unset. Breaking that asymmetry moves characterization golden files.
- **Scope:** extract the stub-emission decision behind a `StubStrategy` interface whose only initial implementation emits the existing `.`/`CONTINUE` pair, so that a stubbed statement can later dispatch to a mock instead. Absorb the parser refactor needed to make that possible.
- **Entry precondition:** the tag `run-3-bugfix-pr-adoption` exists; full green; the golden-file set byte-stable; the toolchain anchor matches.
- **Success oracle:** **every golden file byte-identical** after normalization — this run changes structure only; `clean test` green with zero failures; the harness compiles and executes a non-zero and stated number of COBOL programs; and a new test demonstrates that an alternative `StubStrategy` implementation is reachable at all three enumerated seam points, including the multi-line one.
- **Exit criteria:** green, tagged, with the extension-point contract recorded in the handoff as a decision later runs must respect.
- **Tag:** `run-4-stub-mock-architecture`
- **BLOCKED-BY:** `run-3-bugfix-pr-adoption`
- **Explicit deferrals:** no new mock syntax and no new mock behaviour. This run builds the seam and ships exactly one strategy that reproduces today's output byte for byte. It must not enlarge `TestSuiteParser.java`, `Keywords.java` or `InterpreterController.java`, which are the three largest classes at 60,602, 38,427 and 26,737 bytes respectively.

### A3.7 Runs 5, 6 and 7 — resource mocking

These three runs share a shape: `S4·ADD`, each blocked by its predecessor, each landing new syntax additively behind new configuration keys whose defaults reproduce current behaviour, each shipping tests and documentation in the same change, and each labelling any semantics it cannot establish from a reachable source as provisional and user-visible.

**A shared asset that removes the need to invent a data layout.** The shipped working-storage copybook already reserves typed slots for the mock types these runs add: `01 ==UT==MOCKS.` at `[src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY:L72]`, a four-character `==UT==MOCK-TYPE PIC X(04)` at `L95` carrying 88-level conditions `MOCK-FILE` at `L96`, `MOCK-CALL` at `L97`, **`MOCK-CICS` at `L98`**, **`MOCK-SQL` at `L99`** and `MOCK-PARA` at `L100`, with `REDEFINES` overlays including **`==UT==MOCK-CICS-DATA` at `L110`** (carrying `==UT==MOCK-CICS-KEYWORDS-KEY PIC X(806)` at `L111`) and **`==UT==MOCK-SQL-DATA` at `L115`**. The new mock syntax therefore extends an existing data-layout contract rather than inventing one.

| | **Run 5 — file I/O and VSAM mocking** | **Run 6 — SQL and DB2 mocking** | **Run 7 — CICS mocking** |
| --- | --- | --- | --- |
| **Labels** | `S4·ADD` | `S4·ADD` | `S4·ADD` |
| **Enumerated shared-code touches, fully package-qualified** | `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.checkForBatchFileIOStatement(CobolLine)`; the `StubStrategy` seam; `org.openmainframeproject.cobolcheck.features.testSuiteParser.Keywords`; `org.openmainframeproject.cobolcheck.services.Constants`; `org.openmainframeproject.cobolcheck.features.testSuiteParser.Mock`, `.MockRepository`, `.MockGenerator`, `.MockScope` | The `StubStrategy` seam for `EXEC SQL`; `org.openmainframeproject.cobolcheck.features.testSuiteParser.Keywords`; `org.openmainframeproject.cobolcheck.services.Constants`; `org.openmainframeproject.cobolcheck.features.testSuiteParser.Mock`, `.MockRepository`, `.MockGenerator` | The `StubStrategy` seam for `EXEC CICS`; `org.openmainframeproject.cobolcheck.services.cobolLogic.EIBResponseTable.lookup(String)`; `org.openmainframeproject.cobolcheck.services.cobolLogic.EIBResponseCodes`; `org.openmainframeproject.cobolcheck.features.testSuiteParser.Keywords`; `org.openmainframeproject.cobolcheck.services.Constants` |
| **New package** | `io.blitzy.cbltest.features.fileio` | `io.blitzy.cbltest.features.sql` | `io.blitzy.cbltest.features.cics` |
| **Entry precondition** | Tag `run-4-stub-mock-architecture` exists; full green; anchor matches | Tag `run-5-file-io-vsam-mock` exists; full green; anchor matches | Tag `run-6-sql-db2-mock` exists; full green; anchor matches |
| **Success oracle** | A corpus program whose read loop **terminates** under a mocked file, proven by a suite that hangs or fails without the mock and passes with it; all pre-existing golden files byte-identical; `clean test` green; executed program count stated | A corpus program on the live `SQLCA.cpy` path asserting a mocked `SQLCODE` and a cursor fetch sequence; all pre-existing golden files byte-identical; `clean test` green; executed program count stated | A corpus program asserting a mocked CICS condition resolved **through** `EIBResponseTable`, which is dead code today; all pre-existing golden files byte-identical; `clean test` green; executed program count stated |
| **Exit criteria** | Green, tagged, provisional semantics register updated | Green, tagged, provisional semantics register updated | Green, tagged, provisional semantics register updated |
| **Tag** | `run-5-file-io-vsam-mock` | `run-6-sql-db2-mock` | `run-7-cics-mock` |
| **BLOCKED-BY** | `run-4-stub-mock-architecture` | `run-5-file-io-vsam-mock` | `run-6-sql-db2-mock` |
| **Deferrals** | No SQL or CICS work. No IMS work in any run | No CICS work. No `SQLCODE` value table populated from recall — anything not covered by the in-repo authority or by documented GnuCOBOL `-std=ibm` semantics ships `UNKNOWN` and provisional | No SQL work. The in-repo table is the authority — 76 insertions, 75 unique names; no third-party condition enum is adopted |

`FACT` — `DB2PROG` is the only corpus program on the live `SQLCA.cpy` resolution path `[src/main/cobol/DB2PROG.cbl:L12]`, and it is exercised end-to-end by nothing today. `FACT` — `src/main/cobol/copy/SQLCA.cpy` carries the SQLCA **field layout only** (`SQLCAID`, `SQLCABC`, `SQLCODE PIC S9(9) USAGE BINARY VALUE 0`, `SQLERRM`/`SQLERRML`/`SQLERRMC`, `SQLERRP`) across 24 lines; it carries **no** `SQLCODE` value semantics. `INFERENCE` — Run 6 therefore inherits an explicit `BLOCKED-BY` on `SQLCODE` value semantics; reasoning: the in-repo authority establishes layout but not values, so any value beyond those the corpus itself exercises is `UNKNOWN` until a reachable source supplies it.

### A3.8 Run 8 — Test generation at scale

- **Labels:** `S4·ADD`
- **Enumerated shared-code touches, fully package-qualified:** `org.openmainframeproject.cobolcheck.features.testSuiteParser.TestSuiteParser` — read-only consumption of its parse contract, no enlargement; `org.openmainframeproject.cobolcheck.services.cobolLogic.NumericFields`; `org.openmainframeproject.cobolcheck.services.Config`. **For the invocation surface, the touch is enumerated exactly rather than named by class** (ADR-06 decides the contract; this is its file-level consequence): one new long option and one new short option in `org.openmainframeproject.cobolcheck.services.Constants.COMMAND_lINE_OPTIONS` at `[.../services/Constants.java:L27-L28]`; one branch in `org.openmainframeproject.cobolcheck.workers.Initializer.run()` alongside the existing `help`/`version` branches at `[.../workers/Initializer.java:L35-L44]`, returning before `environmentController.runSetup(...)` at `[:L45]`; one dispatch branch in `org.openmainframeproject.cobolcheck.Main.main(String[])` between the halt check at `[.../Main.java:L12-L14]` and the merge-loop construction at `[:L16-L17]`, constructing the generator and returning instead of entering the loop at `[:L19-L28]`; and one help-text line. `ArgumentHandler` and `ArgumentHandlerController` are **not** modified — they parse whatever the option string declares.
- **New package:** `io.blitzy.cbltest.generator`, whose source directory is the fixed path `src/main/java/io/blitzy/cbltest/generator/`, deliberately bounded away from the three largest existing classes. The mutation harness that judges it lives at the fixed path `src/test/mutation/`.
- **Scope:** a component that consumes COBOL source and emits `.cut` test suites, plus the mutation harness that judges whether the generated suites actually detect faults.
- **The mutation criterion, named in full.** Operators: **statement deletion, comparison-operator inversion, condition negation, literal substitution.** Harness: built **in-repo** at `src/test/mutation/`, because no off-the-shelf COBOL mutation-testing tool exists, so building it is the only option rather than a preference. Floor: **70%** mutation score against the characterization corpus, **CI-enforced**. The floor is non-relaxable; only the number is arguable, and any change to it must be stated, justified and still enforced. All four operators are implementable as line-level transformations on column-addressable fixed-format source.
- **Entry precondition:** the tag `run-7-cics-mock` exists; full green; anchor matches.
- **Success oracle:** generated suites for a stated set of corpus programs achieve a mutation score of 70% or more, enforced by a CI check that fails below the floor; all pre-existing golden files byte-identical; `clean test` green with zero failures; the harness compiles and executes a non-zero and stated number of COBOL programs; and the performance envelope is reported with its suite count, its command, and both the pre-change and post-change baseline measurements.
- **Exit criteria:** green, tagged, with the mutation score and the envelope measurements recorded.
- **Tag:** `run-8-test-generation`
- **BLOCKED-BY:** `run-7-cics-mock`
- **Explicit deferrals:** no enlargement of `TestSuiteParser.java`, `Keywords.java` or `InterpreterController.java`. No new mock types.

### A3.9 Run 9 — z/OS and macOS launcher

- **Labels:** `S4·ADD`, plus **one declared** `S2·CORR` for the macOS repair, itemized in advance with its own oracle
- **Enumerated shared-code touches, fully package-qualified:** `org.openmainframeproject.cobolcheck.features.launcher.Launcher.getPlatformSpecificLauncher(Platform)` at `[src/main/java/org/openmainframeproject/cobolcheck/features/launcher/Launcher.java:L45-L70]`; `org.openmainframeproject.cobolcheck.features.launcher.ProcessLauncher`; `org.openmainframeproject.cobolcheck.features.launcher.LauncherController`; `org.openmainframeproject.cobolcheck.services.platform.Platform` and `.PlatformLookup`; `org.openmainframeproject.cobolcheck.services.Config` — for `zos.process` and `osx.process`.
- **New package:** `io.blitzy.cbltest.features.launcher.zos`
- **Scope:** a pluggable launcher interface with a z/OS implementation that is structurally complete and **labelled unverified**, and the macOS repair.
- **Two dead branches exist, not one.** `Launcher.getPlatformSpecificLauncher` initialises `ProcessLauncher launcher = null` at `L46`; the `OSX` arm has its construction commented out at `L58` and the `ZOS` arm at `L62`, so both return `null`. Both are documented together at `[.../features/launcher/Launcher.java:L43]`, which reads `NOTE: Currently not supporting OSX or ZOS.` The asymmetry that matters is operational rather than documentary: `macos-latest` sits in the CI matrix at `[.github/workflows/VerifyAction.yml:L13]` against a `null` launcher, and `[config.properties:L203]` populates `osx.process` with a value that is unreachable because the launcher is `null` before the key is ever consulted, while `[config.properties:L206]` leaves `zos.process` empty.
- **Entry precondition:** the tag `run-8-test-generation` exists; full green; anchor matches.
- **Success oracle:** the macOS arm returns a working launcher and the CI macOS leg genuinely executes COBOL programs, stating the count — this is the declared corrective half, and it is falsifiable on a platform CI actually exercises. The z/OS arm is structurally complete behind the interface, covered by tests that exercise the interface with a test double, and **labelled unverified** in the handoff, the changelog and the user-facing documentation. All pre-existing golden files byte-identical on Linux; `clean test` green with zero failures.
- **Exit criteria:** green, tagged, with the unverified surface enumerated explicitly.
- **Tag:** `run-9-zos-launcher`
- **BLOCKED-BY:** `run-8-test-generation`
- **Explicit deferrals:** no claim of z/OS verification. No IMS work.

### A3.10 The per-file copybook routing branch, and why

Routing follows the empirical resolution verdict per file rather than an assumption. Branch **(i)** means unvendoring reduces to deletion plus packaging, with no resolution logic touched, no new configuration key and no golden-file delta. Branch **(ii)** means the resolution path itself must change, which requires an additive configuration key whose default reproduces current behaviour.

| File | Branch | Why, from evidence |
| --- | --- | --- |
| `src/main/resources/org/openmainframeproject/cobolcheck/copybooks/DFHEIBLK.CPY` | **(i)**, in Run 2 | Packaged but **dead**. It ships in the jar, but the sole classpath loader `getBoilerplateCodeFromCopybooks()` at `[src/main/java/org/openmainframeproject/cobolcheck/features/testSuiteParser/TestSuiteParserController.java:L298]` — building its path at `L300` and reading it at `L301` — is invoked only at `L123`, `L181` and `L195`, always with a `CCHECK*` name. Nothing resolves it at runtime, so removing it is behaviour-neutral and produces no golden-file delta |
| `copybooks/DFHEIBLK.CPY` | **(i)**, same run | Orphaned duplicate: neither packaged nor loaded, and byte-identical to the packaged copy. Deleted alongside it |
| `src/main/cobol/copy/SQLCA.cpy` | **(ii)** for resolution, in Run 3; the packaging half is a genuine **no-op** | It **is** loaded at runtime: `[src/main/cobol/DB2PROG.cbl:L12]` → `[.../features/interpreter/InterpreterController.java:L463-L484]` → `[.../features/interpreter/LineRepository.java:L135-L145]` → `CopybookExpander`, resolved from the filesystem under `[config.properties:L125]` with `cpy` in the suffix list at `[config.properties:L162]`. But it is packaged in **no** artifact — absent from all three archive listings — so there is nothing to stop shipping. Deleting the in-repo sample would break the DB2 corpus program and an existing expander test unless the characterization corpus supplies the copybook from a corpus-local path, which is exactly what Run 1b is required to do |
| `testfiles/CICDEMO-AFTER.CBL` | Sample material only | A fixture; no production code path reads it. No action beyond fixture hygiene |

`INFERENCE` — a hygiene finding that is not a legal question: `DATETIME` and `OUTPUT` also ship in the jar and are never read, since only the three `CCHECK*` names reach the loader; reasoning: the loader has exactly three call sites and each passes a `CCHECK*` filename.

### A3.11 All thirteen coverage areas, each assigned to a run

Nothing is silently dropped. Where an area is judged unnecessary it is stated as unnecessary with reasoning.

| # | Coverage area | Assigned to | Note |
| --- | --- | --- | --- |
| 1 | Build and toolchain modernization | Run 2 | Gradle 9 daemon on JDK 17+, toolchain shipping Java 8 bytecode |
| 2 | Dependency and supply-chain hygiene | Run 2 | SBOM at a fixed path, committed npm lockfiles, `npm ci` |
| 3 | Identity rename and mark removal | Run 2 | Identifier strings fixed by this plan, because the normalization filter in Run 1b needs them and Run 1b executes first |
| 4 | Release automation | Run 2 | Written from nothing; no release workflow exists in any form |
| 5 | CI hardening and the green gate | Run 1b, Run 2, Run 8 | GnuCOBOL install step in Run 1b; coverage gate wired into `check` in Run 2; mutation floor in Run 8 |
| 6 | Characterization and golden-file safety net | Run 1a then Run 1b | Kept apart to avoid a circular oracle |
| 7 | Defect correction and upstream pull-request adoption | Run 3 | Includes the single deliberate breaking change |
| 8 | Stub-to-mock extension point | Run 4 | Three methods across two packages |
| 9 | Resource mocking | Runs 5, 6, 7 | File I/O and VSAM, SQL and DB2, CICS — one per run so each has its own oracle |
| 10 | Test generation and the mutation floor | Run 8 | Harness built in-repo; no off-the-shelf COBOL mutation tool exists |
| 11 | Platform reach | Run 9 | z/OS structurally complete and unverified; macOS genuinely repaired |
| 12 | **Correctness assurance — cross-cutting** | Runs 1b, 4, 5, 6, 7, 8 | **Seam management:** a single in-repo *verified/provisional register* is the shared artifact. Run 1b creates it and records what the corpus actually demonstrates; Run 4 must leave it unchanged, since a preserving run that alters a correctness claim has changed observable behaviour; Runs 5, 6 and 7 each append only the entries their own corpus program exercises; Run 8 makes the register machine-checked by failing CI when a claim marked verified has no corpus program covering it. The register is one file with one owner per run and an append-only discipline, so no two runs can contradict each other silently |
| 13 | **Licence, governance and documentation compliance — cross-cutting** | Runs 2 and 3 | **Seam management:** ownership is split by artifact class, not by topic. Run 2 owns everything that is packaging or metadata — the SBOM, the licence headers, `THIRD_PARTY.md`, `SECURITY.md`, `notice.txt`, the root licence file that two vendored typing files reference and which does not exist, the Apache/MIT subtree boundary, and the copybook deletions routed to branch (i). Run 3 owns only the copybook **resolution** change routed to branch (ii), because that is a behaviour change and Run 2's preserving discipline forbids it. The boundary is mechanical: if a change alters what the tool *reads at runtime* it is Run 3's, otherwise it is Run 2's |

**Judged unnecessary, with reasoning rather than omission.** IMS support: no IMS runtime is reachable, so it would ship structurally unverified with no path to validation. GraalVM native image: it adds a second build and packaging path before the primary one is releasable. Localization work: seven locale bundles already ship, and extending them serves no identified consumer. A second extension marketplace: publishing to one before the first automated release exists inverts the dependency order. A rewrite in another language or replacement of the precompiler/source-injection model: excluded architecturally, not merely deprioritized.

---

## A4. Program-level architecture decision records

> **This item is a self-contained, copy-pasteable block.** It carries every definition and every code example it relies on, because every later run prompt embeds it into a session that has none of this document's context. It contains no cross-references to other items.

Twelve records. Each states its decision, the alternatives considered, why each alternative was rejected, and the run that implements it. No unresolved fork is left anywhere: where two candidate designs conflicted, one was chosen and the other recorded.

### The three reference code blocks these records depend on

**Reference block 1 — working `.cut` mock syntax, reproduced exactly.** This is simultaneously the backward-compatibility contract's concrete form and the design baseline that new mock types extend **additively**. Every construct in it must continue to parse and behave identically.

```plaintext
           MOCK CALL 'PROG1'
                MOVE "Global PROG1" TO VALUE-1
           END-MOCK
           TestCase "C-1 Simple call mock works"
           MOCK CALL 'PROG1'
                MOVE "From mocked PROG1" TO VALUE-1
           END-MOCK
           PERFORM 600-MAKE-CALL
           EXPECT VALUE-1 TO BE "From mocked PROG1"
           VERIFY CALL 'PROG1' HAPPENED ONCE
           MOCK CALL 'PROG3' USING
             BY CONTENT VALUE-1, BY VALUE VALUE-2, VALUE-3
                MOVE "content" TO VALUE-1
           END-MOCK
           VERIFY CALL 'PROG3' USING BY CONTENT VALUE-1, BY VALUE VALUE-2, VALUE-3
                HAPPENED 2 TIMES
           MOCK PARAGRAPH 300-CHANGE-1
              EVALUATE VALUE-1
              WHEN "Hello" MOVE "MOCKED" TO VALUE-1
              END-EVALUATE
           END-MOCK
           VERIFY PARAGRAPH 500-SWITCH NEVER HAPPENED
```

**Reference block 2 — the critical stubbing predicate, reproduced exactly.** This is the `PROCEDURE_DIVISION` branch of `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.shouldLineBeStubbed(CobolLine, State)` at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/Interpreter.java:L273-L278]`.

```java
if (state.isFlagSetFor(Constants.PROCEDURE_DIVISION)) {
    if (checkForBatchFileIOStatement(line) || line.containsToken(Constants.CALL_TOKEN) ||
        line.containsToken(Constants.EXEC_SQL_TOKEN) || line.containsToken(Constants.EXEC_CICS_TOKEN)
        || line.containsToken(Constants.END_EXEC_TOKEN)) {
        return true;
    }
}
```

**Reference block 3 — what replaces a stubbed line, reproduced exactly.** This pair is emitted at `[src/main/java/org/openmainframeproject/cobolcheck/features/interpreter/InterpreterController.java:L149-L150]` and again, identically, at `[:L165-L166]`.

```java
reader.putNextLine("           .");
reader.putNextLine("            CONTINUE");
```

Reference blocks 2 and 3 together are the single most important fact about this codebase: every `EXEC SQL`, `EXEC CICS` and batch file I/O verb is replaced with a literal `CONTINUE`, and generalizing that replacement is the extension point on which all three resource-mocking runs depend.

---

### ADR-01 — Retain the precompiler / source-injection model

**Decision.** The tool remains a precompiler: it merges the program under test with test suites and test-case management logic to produce a copy with test code embedded, then compiles and executes the copy. Every extension in this programme generalizes that pipeline rather than replacing it.

**Alternatives considered.** (a) A runtime interpreter for the `.cut` language. (b) A language-server-protocol service driving execution. (c) A GnuCOBOL compiler plugin. (d) A rewrite in another language.

**Rejected because.** (a) and (b) discard the property the tool exists for — paragraph-level isolation achieved by injecting into `WORKING-STORAGE` and the `PROCEDURE DIVISION` of a *copy* — and would require re-implementing COBOL semantics rather than delegating them to a real compiler. (c) binds the tool to one compiler's internals, when the mocked-resource work explicitly needs to run against a compiler that has neither DB2 nor CICS. (d) spends the only genuine asset this project has: a suite of 457 tests passing with zero failures, which is a working refactoring safety net. `INFERENCE` — spending a green build on a rewrite is the primary way this project fails; reasoning: the safety net is the only thing that makes the behaviour-preserving oracles in later runs enforceable, and a rewrite invalidates all of it at once.

**Implementing run.** All runs; enforced as a standing constraint rather than a task.

---

### ADR-02 — The stub-to-mock extension point is a `StubStrategy` interface at a three-method seam

**Decision.** Extract the stub-emission decision behind a `StubStrategy` interface. The seam is **three methods across two packages**, all of which must be in the enumerated surface:

1. `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.shouldLineBeStubbed(CobolLine, State)` — the predicate, `[.../services/cobolLogic/Interpreter.java:L272-L294]`
2. `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.shouldCurrentLineBeStubbed()` — emission site 1, `[.../features/interpreter/InterpreterController.java:L145-L158]`, emitting reference block 3 at `L149-L150`
3. `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.shouldCurrentStatementBeStubbed()` — emission site 2, `[.../features/interpreter/InterpreterController.java:L160-L172]`, emitting reference block 3 identically at `L165-L166`

The initial and only implementation shipped by the extracting run emits exactly the existing `.`/`CONTINUE` pair, so the extraction is provably behaviour-preserving.

**The keying is decided here, not left to the consuming runs.** Resource mock types are registered as additional strategies **keyed by the statement kind the predicate matched** — batch file I/O, `CALL`, `EXEC SQL`, `EXEC CICS` — and by nothing else. `INFERENCE` — keying on the statement kind is the only option that keeps the extraction behaviour-preserving; reasoning: the predicate at `[.../services/cobolLogic/Interpreter.java:L272-L294]` already discriminates exactly those kinds and nothing else, so a strategy keyed on them is a pure refactor of an existing branch, whereas a strategy keyed on a mock *registration* would require the interpreter to consult the mock repository at a point where it does not today — a new dependency, a new lookup and a new failure mode, none of which can be proven behaviour-preserving against golden files in the same run. **Rejected alternative:** key the strategy on a per-statement mock-registration lookup, so that a statement dispatches only when a matching mock exists. Rejected because it inverts the dependency direction between the interpreter and the mock repository inside a run whose entire oracle is that no golden file moves, and because the same effect is available *inside* a strategy without changing the seam.

**What this decision does not settle, and deliberately leaves to the first consuming run.** How a strategy, once selected by statement kind, chooses among several registered mocks of that same kind at run time — by resource name, by signature, by declaration order, or by the existing scope rules that already let a local mock override a global one. `INFERENCE` — that is a question *inside* one strategy rather than about the seam, so it does not affect this ADR and must not reopen it; reasoning: the seam passes the matched statement to the strategy, and everything the strategy then does with it is private to that strategy. The first resource-mocking run settles it and records it; the other two inherit it.

**Two properties the extraction must preserve.** `FACT` — emission site 2 is the site that handles **multi-line** statements; it iterates `reader.getCurrentStatement()` at `L161`, whereas emission site 1 handles the single current line. `FACT` — emission site 2 carries a structural asymmetry: its `endsInPeriod` check at `L164` tests `reader.getCurrentLine()` rather than the iterated line, and its `return true` at `L168` sits **outside** the `PROCEDURE_DIVISION` guard at `L163`, so it reports "stubbed" without emitting anything when the flag is unset. `INFERENCE` — breaking either property moves characterization golden files; reasoning: both determine exactly which lines reach the emission calls, and the golden files record the emitted text.

**Alternatives considered.** (a) Generalize only emission site 1, as the reference-block attribution implies. (b) Put the strategy behind the predicate in `Interpreter` alone and leave both emissions hardcoded. (c) Introduce a visitor over the whole interpretation pass. (d) Replace the two emission sites with one unified method as part of the same run.

**Rejected because.** (a) leaves multi-line `EXEC SQL`, `EXEC CICS` and batch I/O statements — precisely the statements the three resource-mocking runs are built to handle — still emitting a bare `CONTINUE`, so all three later runs would build on a partially generalized hook. (b) makes the strategy unable to influence what is *emitted*, which is the entire point. (c) is a large redesign of the class the constraint says not to make worse, and it cannot be proven behaviour-preserving against golden files in one step. (d) conflates a structural extraction with a behavioural unification: the two methods have genuinely different reader semantics and the asymmetry above is observable, so unifying them is a separate, itemized, corrective decision that must not ride along inside a preserving run.

**Implementing run.** Run 4 (`S3·PRES`). Consumed by Runs 5, 6 and 7.

---

### ADR-03 — New mock syntax extends the existing `MOCK <TYPE> ... END-MOCK` contract additively, keyed to the already-shipped typed slots

**Decision.** New resource mock types are introduced as additional `<TYPE>` values in the existing `MOCK <TYPE> ... END-MOCK` / `VERIFY <TYPE> ... HAPPENED` grammar shown in reference block 1, with the existing verification family (`HAPPENED ONCE`, `HAPPENED N TIMES`, `NEVER HAPPENED`) reused unchanged. No existing construct changes meaning, and no existing construct is deprecated.

The type vocabulary is not invented, because the shipped runtime working-storage layout already reserves typed slots for exactly these types. `FACT [src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY]` — `01 ==UT==MOCKS.` at `L72`; `==UT==MOCK-TYPE PIC X(04)` at `L95`; 88-level conditions `==UT==MOCK-FILE VALUE 'FILE'` at `L96`, `==UT==MOCK-CALL VALUE 'CALL'` at `L97`, `==UT==MOCK-CICS VALUE 'CICS'` at `L98`, `==UT==MOCK-SQL VALUE 'SQL'` at `L99`, `==UT==MOCK-PARA VALUE 'PARA'` at `L100`; and `REDEFINES` overlays `==UT==MOCK-FILE-DATA` at `L104`, `==UT==MOCK-CALL-DATA` at `L108`, `==UT==MOCK-CICS-DATA` at `L110` with `==UT==MOCK-CICS-KEYWORDS-KEY PIC X(806)` at `L111`, `==UT==MOCK-PARA-DATA` at `L112` and `==UT==MOCK-SQL-DATA` at `L115`. `FACT` — the parser side already carries `MOCK_KEYWORD`, `MOCK_TYPE` and `ENDMOCK_KEYWORD` at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L70-L73]` and the verification vocabulary at `[:L75-L81]`.

**A hard constraint on the design.** `FACT` — the syntax documented in the upstream wiki does **not** exist: `INPUT-FILE`, `ON OPEN`, `ON READ`, `DATASET` and `CONDITION` all return **zero** occurrences across `org.openmainframeproject.cobolcheck.features.testSuiteParser.Keywords` and `org.openmainframeproject.cobolcheck.services.Constants`. `INFERENCE` — the wiki therefore cannot be used as a specification; reasoning: it documents keywords the parser has never recognized, so implementing "what the wiki says" would ship syntax no existing user can be using. Documented-but-absent forms may be *adopted* as the new syntax on their merits, but never on the authority of the document.

**Alternatives considered.** (a) A separate top-level statement family for resource mocks, disjoint from `MOCK`. (b) An external mock-definition file in a data format outside the `.cut` DSL. (c) Adopt the wiki-documented spellings verbatim as authoritative. (d) Adopt the syntax defined in the third-party design archives found on a fork.

**Rejected because.** (a) doubles the grammar surface and the verification vocabulary for no user benefit, and it strands the typed slots already reserved in the shipped copybook. (b) breaks the property that a `.cut` file is the single self-contained description of a test, which is what the four identified consumer classes depend on. (c) treats documentation as evidence, which is the failure mode this programme ranks above all others; the keywords are provably absent from the source. (d) those archives are unproposed, unreviewed, opaque binary drops; their SQL design document supplies `SQLCODE` values with no cited source at all, and their CICS response enum defines 44 constants while ignoring the in-repo table's 75 unique conditions entirely — adopting their syntax would import unsourced semantics along with it.

**This becomes permanent public API the moment it ships**, so it is settled once here rather than three times in three runs.

**Implementing runs.** Run 5 (`FILE`), Run 6 (`SQL`), Run 7 (`CICS`) — each `S4·ADD`.

---

### ADR-04 — Correctness assurance rests on two reachable sources in a fixed order, with a machine-checked verified/provisional register

**Decision.** Mocked resource semantics are established from two sources in a fixed order, and from nothing else:

1. **The in-repo authority, first.** `FACT` — `org.openmainframeproject.cobolcheck.services.cobolLogic.EIBResponseTable` performs exactly **76** `put(` insertions at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java:L43-L118]`, each mapping a condition name to an `EIBResponseCodes` triple — but the resulting map holds **75 unique condition names**, because `LENGERR` is inserted twice, at `[:L51]` and `[:L84]`, and the second insertion silently replaces the first. **The authority is therefore 75 runtime conditions, not 76, and a run that assumes 76 will look for a condition that is not reachable.** `FACT` — `src/main/cobol/copy/SQLCA.cpy` carries the SQLCA **field layout** across 24 lines (`SQLCAID`, `SQLCABC`, `SQLCODE PIC S9(9) USAGE BINARY VALUE 0`, `SQLERRM` with `SQLERRML`/`SQLERRMC`, `SQLERRP`).
2. **Documented GnuCOBOL `-std=ibm` semantics, second**, always labelled `INFERENCE` with any divergence stated.

Anything covered by neither is emitted **`UNKNOWN`**, inherited by the consuming run as an explicit `BLOCKED-BY`, and the affected mock type ships **labelled provisional**.

**The duplicate insertion is itself a register problem, and Run 7 must close it rather than annotate it.** `FACT` — the two `LENGERR` insertions differ in exactly one component: EIBFN `0x04` at `[.../services/cobolLogic/EIBResponseTable.java:L51]` versus `0x06` at `[:L84]`, with `0xE1`, `0` and `22` identical in both. `FACT` — the class javadoc at `[:L22]` reads `INVREQ, DISABLED, IOERR, ISCINVREQ, NOTAUTH, LENGERR, NOTOPEN, NOSPACE, NOTFND can be produced from multiple CICS modules. File Control is assumed (EIBFN - 0x06..)`, and `HashMap.put` keeps the later insertion, so the surviving `0x06` tuple is the one the javadoc says is intended. `INFERENCE` — the outcome is therefore correct today; reasoning: the documented assumption and the map's actual content agree. `INFERENCE` — but "the outcome happens to be right" is not "the table is right", and the difference is user-visible: a reader counting `put(` calls concludes 76 conditions are mockable, while a run enumerating the constructed map finds 75, and nothing in the source signals which is intended. **Run 7 must therefore remove the redundant insertion — or replace the map with one that rejects duplicate keys — before reporting the table wired and complete, and must state in its handoff which condition names changed meaning as a result.** Until it does, `LENGERR` cannot be marked `verified` in the register.

**The distinction is CI-enforced and user-visible.** A single in-repo *verified/provisional register* is the shared artifact. Every mock type and every semantic claim has exactly one entry marked `verified` or `provisional`. A CI check fails the build when an entry marked `verified` has no corpus program covering it, which makes the label falsifiable rather than declarative. The same register is rendered into user-facing documentation and into the tool's own output for provisional mock types, so a user who relies on a provisional semantic is told so at the point of use rather than in a footnote.

**What this explicitly forbids.** No `SQLCODE`, `FILE STATUS` or `RESP` table is populated from recall, and no IBM documentation host is requested. `FACT` — `SQLCA.cpy` establishes layout but carries **no** `SQLCODE` value semantics, so `SQLCODE` values beyond those a corpus program actually exercises are `UNKNOWN`. `FACT` — the third-party SQL design archive on a fork uses only the literals `0`, `100` and `-001` and cites no source whatsoever, and its CICS counterpart defines 44 response constants without referencing the in-repo 76-entry table. `INFERENCE` — neither is admissible as authority; reasoning: an unsourced value set is indistinguishable from recall, and a condition set narrower than and inconsistent with the in-repo table cannot be reconciled with it.

**Alternatives considered.** (a) Populate the tables from model knowledge of DB2 and CICS behaviour. (b) Request access to IBM documentation or an IBM compiler. (c) Ship the mocks without any verified/provisional distinction. (d) Adopt the third-party archives' tables. (e) Adopt an external COBOL conformance suite as the differential oracle.

**Rejected because.** (a) is precisely the false-confidence failure this project ranks above every other quality attribute: a plausible-looking wrong `SQLCODE` is worse than an absent one, because it is silently believed. (b) is unavailable and cannot be solved by effort. (c) makes the tool's own confidence unreadable, so a user cannot tell a verified semantic from a guess. (d) imports unsourced values, as above. (e) is genuinely attractive as a differential-testing asset and needs no proprietary host, but it requires fetching an external archive, so it is carried as an open question rather than assumed available.

**Implementing runs.** Run 1b creates the register; Run 4 must leave it unchanged; Runs 5, 6 and 7 each append only what their own corpus exercises; Run 8 makes it machine-checked.

---

### ADR-05 — The z/OS interface boundary is a pluggable launcher, shipped structurally complete and labelled unverified

**Decision.** z/OS support is added as an implementation behind the existing `ProcessLauncher` abstraction, in its own package, with the honest label attached in the handoff, the changelog and the user documentation. It is covered by tests that exercise the interface through a test double; it is never claimed to be verified.

**Why the boundary is where it is.** `FACT` — `org.openmainframeproject.cobolcheck.features.launcher.Launcher.getPlatformSpecificLauncher(Platform)` at `[.../features/launcher/Launcher.java:L45-L70]` initialises `ProcessLauncher launcher = null` at `L46` and returns it; the `OSX` arm has its construction commented out at `L58` and the `ZOS` arm at `L62`, so **both** return `null`. `FACT` — both are documented together at `[.../features/launcher/Launcher.java:L43]`: `NOTE: Currently not supporting OSX or ZOS.` `FACT` — `macos-latest` is in the CI matrix at `[.github/workflows/VerifyAction.yml:L13]`, `[config.properties:L203]` sets `osx.process` to a value that is unreachable because the launcher is `null` before the key is consulted, and `[config.properties:L206]` leaves `zos.process` empty.

**Alternatives considered.** (a) Skip z/OS entirely. (b) Ship z/OS and claim it works. (c) Build z/OS support first, since several upstream issues ask for it. (d) Repair macOS in an earlier run and leave z/OS unaddressed.

**Rejected because.** (a) discards the pluggable-launcher boundary that also fixes macOS, and leaves a documented platform returning `null`. (b) is the false-confidence failure applied to a platform nobody can test here. (c) inverts the dependency order: the launcher is the least verifiable component and would consume the safety net before the safety net exists. (d) is nearly right, and it is why the macOS half is carried as a **declared corrective discipline inside the same run** with its own falsifiable oracle on a platform CI actually exercises — but splitting it into an earlier run would put a behaviour change inside a preserving run.

**Implementing run.** Run 9 (`S4·ADD` plus one declared `S2·CORR`).

---

### ADR-06 — The test-generation component is bounded away from the precompiler

**Decision.** The generator lives in its own package, `io.blitzy.cbltest.generator`. It consumes COBOL source and emits `.cut` suites as text. It does **not** participate in merging, interpretation or launching, and it reaches the existing code only through a read-only consumption of the parse contract plus the numeric-field metadata. The precompiler remains able to run without the generator present, and the generator remains testable without compiling COBOL.

**The invocation contract is decided here, because an unreachable component is not a delivered one.** The generator is invoked by a **new additive command-line flag that dispatches directly into the generator package, bypassing the existing worker chain entirely.**

`FACT` — the current chain is `[src/main/java/org/openmainframeproject/cobolcheck/Main.java:L8-L31]`: `new Initializer(args)` at `L9`, `initializer.run()` at `L10`, the halt check at `L12-L14`, `new Generator()` at `L16` and `new CobolTestRunner()` at `L17`, then the loop at `L19-L28` calling `generator.prepareAndRunMerge(...)` at `L21` and conditionally `testRunner.run(...)` at `L24`. `FACT` — the flag set is a single string, `Constants.COMMAND_lINE_OPTIONS` at `[.../services/Constants.java:L27-L28]`, carrying `c:l:p:t:g:a:e:s:r:vh` and the long forms `config-file, log-level, programs, tests, generated-tests, all-tests, error-log, source-context, run-directory, version, help`; it is consumed through `ArgumentHandlerController` at `[.../workers/Initializer.java:L25]`. `FACT` — `Initializer.run()` at `[.../workers/Initializer.java:L34]` already contains the exact dispatch shape this decision reuses: `isKeySet("help")` at `L35-L39` and `isKeySet("version")` at `L40-L44` each emit, call `setExitStatusHalt()` and `return` — **before** `environmentController.runSetup(...)` at `L45-L46` and before the merge loop is ever reached.

**The exact shared touch, enumerated so no executing agent has to infer it.** One new long option and one new short option added to the option string at `[.../services/Constants.java:L27-L28]`; one branch in `Initializer.run()` alongside the existing `help`/`version` branches at `[.../workers/Initializer.java:L35-L44]`, which sets a generate-mode flag and returns before `environmentController.runSetup(...)` at `[:L45]`; one dispatch branch in `Main.main` between the halt check at `[.../Main.java:L12-L14]` and the merge-loop construction at `[:L16-L17]`, which constructs the generator and returns instead of entering the loop at `[:L19-L28]`; and one help-text line. **Nothing else in `ArgumentHandler`, `ArgumentHandlerController`, `Initializer` or `Main` is modified**, and no class on the parse, interpret or merge path is touched at all.

**Compatibility effect.** `INFERENCE` — zero for every existing user; reasoning: the flag is new, so no existing invocation supplies it, and with the flag absent `Main.main` takes byte-for-byte the path it takes today. But the flag itself **is** new observable public surface — a permanent CLI addition — so it is declared in advance in Run 8's public-surface section, recorded in its handoff §7, and announced in the changelog. Generation and precompilation are **mutually exclusive modes**: supplying the generate flag together with `--tests` or `--all-tests` is a usage error that exits with the existing usage status rather than doing both.

**Its oracle, in three parts.** (i) **With the flag absent:** every pre-existing golden file byte-identical, the argument-parsing tests unchanged, and `Main.main`'s path unaltered. (ii) **With the flag present:** a test asserts that the merge, interpret and launch path is **not entered** — the generator is reached without `Generator.prepareAndRunMerge` at `[.../Main.java:L21]` or `CobolTestRunner.run` at `[:L24]` being invoked — which is what makes ADR-06's boundary a measurement rather than an intention. (iii) `--help` output gains exactly one documented line, checked against the golden help text.

**Alternatives considered.** (a) A separate entry point or a second published artifact. (b) A programmatic API only, with no user-facing invocation. (c) A mode threaded *through* the existing worker chain, so generation runs as a phase of `Initializer` → `Generator` → `CobolTestRunner`. (d) A subcommand verb (`cbltest generate ...`) rather than a flag.

**Rejected because.** (a) splits the release across two artifacts and breaks the invocation-shape contract, which is a plain `java -jar` call from a shell script or container. (b) leaves the component unreachable from the only invocation shape users have, so the feature would ship undeliverable. (c) is ADR-06's rejected alternative (b) restated at the CLI level: it couples generation to interpretation, so a generation defect becomes indistinguishable from an interpretation defect — and it is exactly what the direct dispatch above avoids. (d) requires a new argument-parsing layer, because the existing handler parses options rather than verbs, which is a far larger change to shared code than one option-string entry and one branch.

**Alternatives considered.** (a) Generate inside `TestSuiteParser`, which already understands the DSL. (b) Generate as a mode of the existing CLI worker chain. (c) Emit merged source directly, skipping the `.cut` intermediate. (d) Ship the generator as a separate repository or artifact.

**Rejected because.** (a) enlarges the largest class in the codebase — `TestSuiteParser.java` at 60,602 bytes and 1,240 lines — which the standing constraint forbids. (b) couples generation to the interpretation pipeline, so a generation defect becomes indistinguishable from an interpretation defect. (c) forfeits the property that makes generation reviewable and mutation-testable at all: a generated `.cut` file is human-readable, diffable, and executable by exactly the same path a hand-written one takes, which is what lets the mutation harness judge generated suites with the same oracle as authored ones. (d) splits the release and the test corpus across two repositories for no benefit to a solo-maintained project.

**Implementing run.** Run 8 (`S4·ADD`).

---

### ADR-07 — The rename identifier strings are fixed here, not deferred to the run that performs the rename

**Decision.**

| Surface | Current | New |
| --- | --- | --- |
| Java package namespace | `org.openmainframeproject.cobolcheck` | `io.blitzy.cbltest` |
| Gradle group | `org.openmainframeproject` | `io.blitzy` |
| Artifact ID / `productName` | `cobol-check` | `cbltest` |
| User-facing product name | `Cobol Check` / `COBOL Check` | `Blitzy COBOL Test` |
| Marketplace publisher | `openmainframeproject` | `blitzy` |
| Extension ID | `openmainframeproject.cobol-check-extension` | `blitzy.cbltest-extension` |

The first release under the new identity is **0.3.0**, and the stale planned heading at `[CHANGELOG.md:L15]` is retitled. Three reasons: the artifact ID and publisher both change, which is a coordinate change warranting a minor bump even under a preserving discipline; `FACT` — two releases already share the version number `0.2.19` at `[CHANGELOG.md:L23]` and `[CHANGELOG.md:L26]`, so the numbering must be reconciled regardless; and reserving `0.3.0` for features several runs away would leave the modernization and correction runs unnumbered. `FACT` — a **second** stale planned heading exists at `[CHANGELOG.md:L8]` (`## \[1.0.0\]`), so the reconciliation must address both.

**Why this cannot be deferred.** The golden-file normalization filter is built in Run 1b and must canonicalize both the old **and** the new namespace, group, artifact ID and product display name — and Run 1b executes **before** the rename. `INFERENCE` — without these strings the filter is unwritable and the byte-identical oracle for the modernization run collapses silently rather than loudly; reasoning: an un-normalized identifier token appears in golden output, so every golden file diffs for a reason the oracle cannot distinguish from a migration defect.

**Why three segments map to three.** `FACT` — resources are resolved by classpath path: `Constants.COBOLCHECK_PACKAGE_PATH = "/org/openmainframeproject/cobolcheck"` at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L21]`, `COBOLCHECK_COPYBOOK_DIRECTORY = COBOLCHECK_PACKAGE_PATH + "/copybooks/"` at `[:L24]`, consumed by `getBoilerplateCodeFromCopybooks()` which builds its path at `[.../features/testSuiteParser/TestSuiteParserController.java:L300]` and reads it with `getResourceAsStream` at `[:L301]`. `INFERENCE` — a path-depth change would break resource lookup and produce golden-file diffs indistinguishable from migration defects, whereas an equal-depth mapping reduces the source move to a two-level directory rename plus a token substitution; reasoning: the resource path is assembled from the package path literal, so its segment count is load-bearing.

**Blast radius, measured.** `FACT` — exactly **161** tracked text files contain `openmainframeproject` or `neopragma` tokens (163 including binaries), with the highest concentrations in `Generator.java` (16), `TestSuiteConcatenator.java` (14), and `ProcessOutputWriter.java`, `InterpreterController.java` and `CopybookExpander.java` (12 each). `FACT` — one launch script still references a namespace that no longer exists: `run:L5` and `run:L7` invoke `--tests com.neopragma.cobolcheck.*Test` and `*IT`. `INFERENCE` — that script therefore runs zero tests today; reasoning: no class matches that package.

**One consequence of the publisher row is a credential, not a code change, and it is the programme's only human provisioning step.** `FACT` — the extension is published by a third-party action reading `pat: ${{ secrets.VS_CODE_EXTENTION_SECRET }}` at `[.github/workflows/DeployExtension.yml:L27]`, a secret whose name is misspelled and whose token authenticates the **old** publisher. `INFERENCE` — changing the publisher to `blitzy` therefore requires a new token rather than a renamed reference; reasoning: a marketplace token is scoped to its publisher and the publish call names the publisher, so the old token is rejected outright. The replacement is the correctly-spelled secret **`VSCE_PAT`**, read only at publish time and never at Gradle configuration time, with the misspelled secret deleted in the same change. Run 2 carries it as an entry precondition and skips the extension-publish job with an explicit message if it is absent — without gating the JAR release on it.

**The rename must be executed by an idempotent script**, and the resulting commit's diff over `src/test/` must be mechanically verifiable as matching only the script's token set. That verification is the enforcement mechanism the preserving discipline depends on, because the rename necessarily edits every existing test source and so "unmodified" cannot be taken literally.

**Alternatives considered.** (a) An `io.github.<owner>` four-segment namespace. (b) Defer the strings to the run that performs the rename. (c) Keep the existing namespace and change only the display name. (d) A two-segment namespace.

**Rejected because.** (a) is the conventional Maven Central pattern for individual publishers, but it is four segments deep, introduces exactly the path-depth churn this record's decision avoids by keeping three segments mapping to three, and embeds an organization name that would itself need changing later. (b) makes Run 1b unexecutable, as shown. (c) does not satisfy the mark-removal requirement, since the namespace itself carries the mark. (d) is unconventional for a published artifact and still changes path depth.

**Implementing run.** Run 2 applies it; Run 1b consumes the strings.

---

### ADR-08 — The history purge is rejected; committed binaries are removed by ordinary commit

**Decision.** No history rewrite. No `git filter-repo`, no `git rebase --root`, no force-push of a tagged ref. The committed binaries and junk are removed by an ordinary commit in Run 2 and **remain in history**, accepted explicitly. This is a decision the plan makes, not a fork it leaves open.

**Reasons, decisive one first.** (1) A rewrite invalidates every commit hash, destroying the ability to cite the upstream SHAs this programme itself depends on — including every pull-request head that the adoption run re-applies, whose identities are recorded as `9010b81`, `69603d7`, `8c07d40`, `11faeef`, `7d8a086`, `2d1b367`, `ab4875f` and `e88bc1a`, and the merge bases `b0b39da8`, `2235764e`, `08d3c988` and `e372dd7f` against which each was measured. (2) `FACT` — the storage benefit is negligible: the fourteen deletion candidates total 909,650 bytes, of which the three archives are 792,270 bytes, against a local pack of 23.59 MiB — under 4%. The largest tracked binary at the repository root is `LAUNCHTESTT` at 33,360 bytes, an ELF 64-bit executable. (3) The binaries are not secrets, so there is no security driver. (4) The constraint is all-or-nothing before the first run tags anything, so adopting it would insert an irreversible, unvalidatable rewrite ahead of the first known-good checkpoint.

**Tool availability is explicitly not part of this reasoning.** `FACT` — `git-filter-repo` is absent from this image and nothing was installed. The decision would be identical if it were present.

**Deletion lists must be built from version control, not from a supplied enumeration.** `FACT` — all fourteen candidates are tracked: `Generator-java-save.txt` (45,583 B), `LAUNCHTESTT` (33,360 B), `TESTPRG.CBL` (37,989 B), `foobar` (0 B), `pad` (181 B), `test-input` (71 B), `test-output` (80 B), `notice.txt` (1 B), `package-lock.json` (90 B), `_config.yml` (25 B), `build/distributions/cobol-check-0.2.19.zip` (245,014 B), `build/libs/cobol-check-0.2.19.jar` (273,628 B), `vs-code-extension/Cobol-check/bin/cobol-check-0.2.19.jar` (273,628 B), `vs-code-extension/Cobol-check/ParserErrorLog.txt` (0 B). `FACT` — four names supplied as junk are **untracked and absent**: `CC##99`, `CC##99.CBL`, `CC##TEST`, `ALLTESTS`; they are runtime output, observed being generated into `testruns/` during an actual harness run. **Two carve-outs are mandatory.** `FACT` — `expected-output.txt` (12,336 B) is the load-bearing approval baseline and must be **retained**, not deleted; `gradle/wrapper/gradle-wrapper.jar` (58,694 B) is a tracked binary that must be **retained**, because committing it is required for `./gradlew` to function, and under a Gradle 9 migration it is replaced rather than deleted. `FACT` — two candidates are ignored yet tracked and therefore need `git rm --cached` in addition to their existing entries: `LAUNCHTESTT` at `[.gitignore:L12]` and `ParserErrorLog.txt` at `[.gitignore:L125]`.

**Alternatives considered.** (a) A preliminary purge run before any tag exists. (b) Purge only the three archives. (c) Leave the binaries tracked.

**Rejected because.** (a) and (b) both invalidate hashes for a sub-4% storage gain and destroy the citation base. (c) leaves a live defect: `FACT` — pull request #337 conflicts on `vs-code-extension/Cobol-check/bin/cobol-check-0.2.8.jar` as a **delete/modify** conflict, and `FACT` — running the documented gate rewrites `build/libs/cobol-check-0.2.19.jar` and deletes `build/distributions/cobol-check-0.2.19.zip`, so merely building the project dirties the working tree.

**Implementing run.** Run 2.

---

### ADR-09 — `BuildHelper` moves to `buildSrc/`

**Decision.** The comparison helper is relocated from an inline declaration in the build script into `buildSrc/`, with the mandated test asserting that `compareFiles()` fails on unequal stream lengths **and** on an empty stream.

**Why it is required rather than optional.** `FACT` — `BuildHelper` is not a Java file: it is a **Groovy** class declared inline in the build script at `[build.gradle:L263-L296]`, structurally unreachable from anything under `src/test/java`. `INFERENCE` — the mandated test therefore cannot be written at all without relocation, which makes the move the mechanical **precondition** of the comparison-loop repair rather than a third change; reasoning: a test source set cannot reference a class declared in the build script's own class space.

**Alternatives considered.** (a) A functional test through Gradle's test kit. (b) Extract into `src/main/java`. (c) Leave it inline and prove the repair only through the task's end-to-end behaviour.

**Rejected because.** (a) exercises the *task* rather than the comparison *function* the requirement is written about, so it does not satisfy the requirement. (b) would ship build logic inside the production artifact, compounding an existing leak — `FACT` — `[build.gradle:L75-L81]` already declares `srcDirs "src/main/resources", "src/test/resources"` for the **main** source set, so test resources already ship in the production jar. (c) is exactly the circular oracle this programme's run split exists to avoid: the function judged correct because the task's output looks right.

A secondary benefit: `INFERENCE` — it removes an inline Groovy class from the build script ahead of a Gradle 9 migration that embeds Groovy 4; reasoning: inline script classes are the constructs most exposed to a Groovy major-version change.

**Implementing run.** Run 1a, as the declared precondition of its second itemized change.

---

### ADR-10 — The Sonar integration is removed entirely rather than retargeted

**Decision.** Remove all three Sonar surfaces: the plugin declaration and configuration block in the build script, the standalone configuration file, and the scan script. Static analysis is replaced with self-hosted CI checks.

**Reasons.** (1) `FACT` — the plugin is `id 'org.sonarqube' version '3.0'` at `[build.gradle:L5]`, which blocks a Gradle 9 migration. (2) `INFERENCE` — it resolves from the plugin portal at *configuration* time, so every Gradle invocation depends on that host being reachable; reasoning: plugin resolution happens during configuration, before any task runs. (3) `FACT` — it points at a third party's analysis organization: `sonar.projectKey "neopragma_cobol-check"` at `[build.gradle:L19]` and `sonar.organization "neopragma-github"` at `[build.gradle:L20]`, neither of which the new maintainer controls. (4) `FACT` — a **second** Sonar surface exists that no supplied document mentions: `sonar-project.properties`, carrying `sonar.sources=src` and a six-entry exclusion list. Removal must cover both surfaces plus the `sonar-scan` script, or the configuration file survives as a dangling artifact.

**Alternatives considered.** (a) Bump the plugin and retarget it at a new organization. (b) Keep it at 3.0 and stay on Gradle 6. (c) Remove the build-script block and keep the standalone file.

**Rejected because.** (a) requires provisioning an external analysis organization, which is exactly the kind of external dependency this programme is optimized to avoid, and it is outside the declared secret inventory. (b) forfeits the Gradle 9 migration, and with it the reproducible-archive default that strengthens the byte-identical oracle — `FACT` — the committed jar is currently **not** reproducible: 13 of 200 entries differ from a rebuild by timestamp alone. (c) leaves a configuration file describing an integration that no longer exists, which is a documentation-versus-reality divergence of exactly the kind this programme is correcting.

**Consequence recorded honestly.** The upstream request is triaged Won't-do on this reasoning rather than silently dropped. `FACT` — that request is issue **#150**, whose captured title is `Produce Sonarqube formatted output for test results.`; naming it by number and captured title matters here, because the specific dialect being declined is SonarQube's own and the plan is removing the very integration that would consume it.

**Implementing run.** Run 2.

---

### ADR-11 — The exit-status repair is the single deliberate breaking change, and it lands in Run 3

**Decision.** The command-line contract's exit codes are **corrected rather than preserved**. A new distinct nonzero exit code is introduced to mean "COBOL tests failed". This is the only intentional break in the programme; every other surface in the compatibility contract is preserved.

**Why a new code is unavoidable.** `FACT` — only two status values exist: `STATUS_NORMAL = 0` and `STATUS_HALT = 8` at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L16-L17]`, whose own comment at `L15` reads `// File read status values`. `FACT` — the decision is `if (exitCode > 4)` at `[src/main/java/org/openmainframeproject/cobolcheck/Main.java:L25]`. `FACT` — measured in this session, five GnuCOBOL child processes returned **4, 0, 0, 4, 0** and all five runs logged `INF004: Cobol-Check terminating: 0`. `INFERENCE` — the boundary is off by one for precisely the value the compiler returns, so no pipeline can detect a failure; reasoning: `4 > 4` is false and no other path sets halt. `FACT` — the inverse paradox: `--help` at `[.../workers/Initializer.java:L35-L37]` and `--version` at `[:L40-L42]` both set halt, which `[.../features/Status/StatusController.java:L21]` maps to `8` and `[:L61]` passes to `System.exit`. So help and version exit non-zero while failing tests exit zero. `INFERENCE` — reusing `8` for test failure would make a failed test indistinguishable from a usage message, so a third value is required.

**Why Run 3 and not Run 2.** Run 2's preserving discipline makes any observable behaviour change a defect. Run 3's entire purpose is itemized behaviour correction, and its byte-identical golden-file oracle survives intact because process exit status is not golden-file content.

**Because it is new observable public surface** — outside the corrective discipline's normal config-key-only allowance — it must be declared in advance in Run 3's prompt, itemized, recorded in that run's handoff public-surface section, announced in the changelog, and carried in open questions with a migration note.

**Its oracle already exists, uncovered.** `FACT` — `src/test/cobol/RETURNCODE/ReturnCode-4.cut` is a nine-line suite deliberately failing at return code 4, and `RETURNCODE` is one of the nine programs the approval harness does not exercise.

**Two companion corrections join the same itemization**, because both are the same defect class — a failure the tool declines to report: informational output leaking past a disabled log level, and the unhandled crash when a test glob matches no suite. `FACT` — the message catalogue already anticipates the latter with `ERR007` and `ERR010`, both reading `empty input stream`, at `[src/main/resources/org/openmainframeproject/cobolcheck/messages/messages.properties:L9]` and `[:L12]`.

**Alternatives considered.** (a) Preserve the current exit codes. (b) Reuse `8`. (c) Change the comparison to `>= 4`. (d) Put the repair in Run 2 alongside the other build-facing work.

**Rejected because.** (a) perpetuates a defect that defeats the tool's stated purpose in a pipeline. (b) conflates usage errors with test failures. (c) is closer, but it silently reclassifies whatever else returns exactly 4 and still offers callers only one nonzero value, so a pipeline still cannot distinguish a usage error from a test failure. (d) puts a behaviour change inside a preserving run.

**Implementing run.** Run 3 (`S3·CORR`).

---

### ADR-12 — The mutation harness is built in-repo, with a CI-enforced 70% floor

**Decision.** Generated test suites are judged by a mutation harness built **in-repo** at `src/test/mutation/`, with four operators — **statement deletion, comparison-operator inversion, condition negation, literal substitution** — and a **70%** mutation-score floor enforced in CI against the characterization corpus.

**Why built rather than adopted.** No off-the-shelf COBOL mutation-testing tool exists, so building it is the only option rather than a preference. `INFERENCE` — all four operators are tractable as line-level transformations; reasoning: the corpus is fixed-format, column-addressable COBOL, so each operator is a deterministic edit at a known column range rather than a parse-tree rewrite.

**Why a floor at all, and why this one.** `INFERENCE` — coverage cannot judge generated tests, because a generated suite that executes every line while asserting nothing scores full coverage and detects no fault; reasoning: coverage measures execution, mutation measures detection, and the failure mode being guarded against is precisely a test that runs code without checking it. The floor is **non-relaxable**; only the number is arguable, and any change to it must be stated, justified and still enforced.

**Alternatives considered.** (a) A line- or branch-coverage threshold on generated suites. (b) Adopt a mutation tool from another language ecosystem. (c) Human review of generated suites. (d) No numeric gate, just an advisory score.

**Rejected because.** (a) is defeated by assertion-free tests, as above — and the repository already demonstrates the pathology at the project level: `FACT` — a coverage gate is defined at `[build.gradle:L30-L60]` but is **not** wired in, since `check.dependsOn` appears nowhere and `jacocoTestCoverageVerification` occurs exactly once, at its own definition. (b) cannot mutate COBOL. (c) does not scale to thousands of generated suites and depends on human memory, which this programme treats as a latent defect. (d) an advisory number is not a gate; the whole point is that the build fails.

**A related answer this record settles.** The existing coverage gate should also be **wired into `check`** and its exclusions should **shrink**. `FACT` — the 0.8 rule excludes exactly **13** entries, among them `*Generator` and `*TestSuiteConcatenator`, and a second rule drops `*LinuxProcessLauncher` and `*TestSuiteConcatenator` to a 0.2 floor. `INFERENCE` — excluding generation-path classes from a coverage gate removes the gate's value precisely where the risk is highest; reasoning: those classes sit on the path that rewrites the user's source.

**Implementing runs.** Run 8 builds the harness and the floor; Run 2 wires the existing coverage gate into `check`.

---

## A5. Backlog triage — all 45 open issues and all 8 open pull requests

> **This item is a self-contained, copy-pasteable block.** It carries every definition it relies on, because every later run prompt embeds it into a session that has none of this document's context. It contains no cross-references to other items.

### A5.0 Provenance of this triage, and why the capture cannot be repeated

`FACT` — this triage is authored from a persisted one-time capture of the upstream repository, taken through the GitHub REST API with a read-only credential. The capture recorded `authenticated = true` with an observed rate-limit ceiling of **5,000**, alongside a deliberate unauthenticated control request against the same endpoint that observed **60**. `INFERENCE` — the control is what makes the capture trustworthy; reasoning: an exhausted unauthenticated quota returns empty response bodies rather than errors, so a truncated capture would be indistinguishable from a complete one, and the two ceilings observed on the same URL in the same session prove the credential was honoured on the requests that carried data.

`FACT` — the capture returned **53** raw entries from the issues endpoint, of which **8** carry a `pull_request` key, leaving **45** issues. `FACT` — the upstream repository metadata independently reports `open_issues_count = 53`, `archived = true`, `default_branch = "Developer"`, and a credential permission set of `{admin: false, maintain: false, push: false, triage: false, pull: true}`. `INFERENCE` — 53 equals 53 because GitHub counts open pull requests in that field, which independently corroborates that no page of the capture was lost; and write access was not merely prohibited by instruction but unavailable, since `pull` was the only permission granted.

The upstream repository is archived and read-only. Nothing was written to it: no push, no comment, no reopen, no label, no issue edit. Implementation runs inherit this triage and must not re-scrape it.

**Classification scale.** `Critical` — the programme cannot deliver its objective without it. `High` — a real defect or a directly-requested capability with an identified consumer. `Medium` — worth doing, no blocking dependency. `Low` — genuine but low-leverage. `Won't-do` — declined, with the reason stated.

**Title fidelity, and why it has its own column.** `FACT` — the **Raw upstream title** column reproduces the captured `title` field byte-for-byte, including upstream typos (`Extent the functionality of the copybook expander` on #206, `FreeBSD version in wiki does not make sens` on #343), upstream casing (`gradle`, `Sonarqube`, `z/OS Process launcher`), upstream prefixes (`[ENHANCEMENT]` on #294, `FR:` on #321, `Question:` on #391, `Quick Question:` on #384) and upstream punctuation (the trailing period on #150). No markdown formatting is added inside that column, so any cell can be compared mechanically against `upstream-harvest/issues.json` and `upstream-harvest/pulls.json` — which is exactly how these 53 cells were verified. Every rewording this plan uses for its own readability lives in the separate **Normalized subject** column, marked `—` where none is needed, and the classification lives in the section heading a row sits under. `FACT` — one exception is unavoidable and is stated rather than hidden: #210's captured title ends in a single space, which no markdown table cell can carry, so that one title is written as a code span (`` `Enable multiple paths for certain configurations ` ``) inside which the trailing space survives. `INFERENCE` — keeping the two columns apart matters beyond tidiness; reasoning: every later run prompt embeds this block, so a paraphrase in the title position becomes the identifier a downstream session searches upstream for, and a search for a title this plan invented finds nothing.

### A5.1 The eight open pull requests

`FACT` — all eight target `Developer`; all eight carry `locked = true`; the repository carries `archived = true`. `INFERENCE` — they therefore cannot be merged and must be retrieved as refs or patches and re-applied in the fork; reasoning: an archived repository accepts no merge operation and a locked pull request accepts no update. **Author attribution must be preserved in the commits that re-apply them**, because all eight are contributions by named individuals under the project licence.

`FACT` — origins split three ways: **#330, #336, #337, #338** are branches on the upstream repository itself; **#408, #409, #410** originate from `generalmotors/openmainframeproject-cobol-check`; **#411** originates from `Living-Mainframe/cobol-check`. `INFERENCE` — the latter two groups require adding third-party remotes, not merely fetching upstream; reasoning: their head commits are not reachable from any upstream branch, only from the upstream pull-request head refs or from the originating forks directly.

`FACT` — apply-cleanly status measured non-mutatingly with `git merge-tree --write-tree` against the pristine fork head. Head SHAs resolve identically from two independent sources — the API capture and the locally fetched pull-request head refs.

**THE CANONICAL PULL-REQUEST ROUTING MATRIX. This block is the single source of truth for which run owns which pull request. Every other statement about pull-request routing in this document — the executive summary, the run sequence, and the provisional sketches — reproduces exactly these assignments, and any statement that disagrees with this matrix is wrong.** All eight appear; none is elided; the run column is exhaustive.

| PR | Origin | Apply | Owning run | Disposition in one line |
| --- | --- | --- | --- | --- |
| **#338** | upstream | CONFLICT | **Run 1a** | Superseded. Its central change is already present in the fork at `[build.gradle:L203]` and is insufficient on its own, so Run 1a supersedes it rather than adopting it |
| **#336** | upstream | CONFLICT | **Run 3** | Intent re-applied on the extension manifests **as Run 2 leaves them**, as itemized behaviour correction (h). Closes issue #287 |
| **#330** | upstream | CONFLICT | **Run 3** | Not adopted as authored. Its intent is re-implemented, expanding **in place** and leaving `ExpanderTest` intact, honouring both recorded review objections |
| **#337** | upstream | CONFLICT | **Run 3** | Superseded by #408. Its `src/main/cobol/MOCK.CBL` fixture and any uncovered `InterpreterControllerTest` case are harvested; neither its committed-jar edit — a delete/modify conflict on the removed `cobol-check-0.2.8.jar` — nor its `InterpreterController` churn is taken |
| **#408** | generalmotors | CLEAN | **Run 3** | Adopted, minus its committed `testruns/testResults.txt`. Closes issue #335 |
| **#409** | generalmotors | CLEAN | **Run 3** | Adopted. Closes issue #339, advances #306 and #151 |
| **#410** | generalmotors | CLEAN | **Run 3** | Superseded by #411 for the code; its `FileCopy.cbl` and `FSTEST.cpy` fixtures are harvested so the file-section case stays covered |
| **#411** | Living-Mainframe | CLEAN | **Run 3** | Adopted under a golden file. Advances issue #206 |

`FACT` — the matrix totals: **Run 1a 1** (#338), **Run 3 7** (#330, #336, #337, #408, #409, #410, #411), **Run 2 0**, summing to **8** with each pull request appearing exactly once and none deferred out of the programme. `INFERENCE` — any formulation that has Run 3 adopting all eight is therefore incorrect, and none appears anywhere in this document; the correct statement is that **Run 3 owns seven of the eight** and Run 1a supersedes the eighth; reasoning: #338's entire substance is the approval-harness ordering that Run 1a repairs, so adopting it separately would re-derive a fix that is already present and already insufficient.

**Why #336 is routed to Run 3 rather than to Run 2, decided here with the rejected alternative recorded.** `FACT` — #336 is titled "Fix loop in extension" and its subject is issue #287, "VS Code extension should try to catch infinite loops"; the change it makes is a timeout that terminates a run which would otherwise hang. `INFERENCE` — that is an **observable behaviour change**, so it cannot land in a run whose discipline is `PRES`, under which any observable change is a defect; reasoning: a user whose run previously hung and now terminates observes a different outcome, which is precisely what preservation forbids. `INFERENCE` — Run 3's discipline is `CORR`, whose entire purpose is itemized behaviour correction, and #336's file surface — the extension manifests and scripts — sits outside Run 3's enumerated core, so it drops to the peripheral surface within that run with its discipline declared explicitly, which is the mechanism the two-axis discipline provides for exactly this case. **Rejected alternative:** routing it to Run 2 alongside the npm manifest unification, on the convenience argument that the manifests are being rewritten there anyway so re-applying the intent in the same run avoids touching them twice. Rejected for two reasons: it would require Run 2 to carry a *second* declared corrective discipline beyond the one `xml` item, weakening the one property that makes Run 2's oracle checkable; and applying #336's intent **after** the manifests are unified is in any case the safer order, since the intent then lands on the manifests that will ship rather than on ones about to be replaced. `FACT` — what remains with Run 2 is only the behaviour-neutral manifest hygiene: unifying the three version numbers, moving development dependencies out of the server manifest's `dependencies` block, raising the root `engines.vscode` floor to the client's actual `^1.52.0`, committing lockfiles, and replacing `npm install` with `npm ci`. None of those changes what the extension does.

The per-pull-request evidence behind those dispositions, with measured diff shapes:

| PR | Raw upstream title (verbatim from capture) | Origin | Apply | Class | Disposition |
| --- | --- | --- | --- | --- | --- |
| **#330** | Fix db2 | upstream | **CONFLICT** — 6 files, +139/−21, conflicting in `InterpreterController.java`, `LineRepository.java`, `Generator.java`, `ExpanderTest.java` | High | **Do not adopt as authored.** Re-implement its intent in Run 3 |
| **#336** | Fix loop in extension | upstream | **CONFLICT** — 10 files, +157/−8, conflicting in `package-lock.json`, `vs-code-extension/Cobol-check/scripts/windows_gnucobol_run_tests.cmd`, `vs-code-extension/package.json` | Medium | Adopt the intent in **Run 3**, re-applied on the extension manifests as Run 2 leaves them, as itemized behaviour correction (h). Closes issue #287. **Not Run 2**, whose preserving discipline forbids the observable change a timeout introduces |
| **#337** | Area after col 72 | upstream | **CONFLICT** — 6 files, +28/−24, conflicting in `InterpreterController.java`, `InterpreterControllerTest.java`, and `vs-code-extension/Cobol-check/bin/cobol-check-0.2.8.jar` as a **delete/modify** conflict | Medium | **Superseded by #408.** Harvest its `src/main/cobol/MOCK.CBL` fixture and any `InterpreterControllerTest` case #408 does not cover; adopt neither its jar edit nor its `InterpreterController` churn. Run 3 |
| **#338** | Fixed Gradle build for approval tests | upstream | **CONFLICT** — 3 files, +9/−9, conflicting in `approvaltest`, `approvaltestWin.cmd`, `build.gradle` | Critical | **Superseded by Run 1a.** Its central change is already present in the fork and is insufficient on its own |
| **#408** | COBOL Line Filtering: Add GnuCOBOL compatibility for EJECT/SKIP statements and remove right side sequence numbers | generalmotors | **CLEAN** — 8 files, +261 | High | **Adopt** in Run 3, minus its committed `testruns/testResults.txt`. Closes #335 |
| **#409** | Handle LINKAGE SECTION and PROCEDURE DIVISION USING; skip ENTRY statements | generalmotors | **CLEAN** — 3 files, +138/−1 | High | **Adopt** in Run 3. Closes #339, advances #306 and #151 |
| **#410** | Clear copyTokens between COPY statements to prevent copybook reuse bug | generalmotors | **CLEAN** — 3 files, +10/−1 | High | **Superseded by #411 for the code**; harvest its fixture. Run 3 |
| **#411** | Added Support to expand copybooks defined in Working Storage Section and Copy Token Cleanup | Living-Mainframe | **CLEAN** — 9 files, +231/−14 | High | **Adopt in Run 3 only behind a fresh technical review.** `FACT` — **never reviewed upstream**: all three review families returned HTTP 200 with zero records, and `requested_reviewers` and `requested_teams` are both empty. Applying cleanly is not review. Gate: characterized under a golden file in Run 1b, then reviewed on merit in Run 3 against the #330 objection that expansion must stay in place. Advances #206 |

**Adjudication of #330, on technical merit.** `FACT` — **the supplied count of five review comments is contradicted**: the inline review-comments endpoint `pulls/330/comments` returned **3**, corroborated by the captured payload's own `review_comments = 3`, while the figure of 5 belongs to the conversation family `issues/330/comments`; the two families are not interchangeable and neither substitutes for the other. `INFERENCE` — the adjudication below is unaffected, because it rests on the content of the two decisive objections rather than on how many comments exist, and both objections are present in the capture; reasoning: a count establishes volume, not merit. `FACT` — the review thread carries exactly **two review submissions, three inline review comments and five conversation comments**, and the two submissions carry **different states**, quoted from the capture: `id 1694706276`, `user.login "Rune-Christensen"`, `author_association "COLLABORATOR"`, `state "CHANGES_REQUESTED"`, `submitted_at "2023-10-24T12:30:27Z"`, `body ""`; and `id 1696117856`, `user.login "samdion1994"`, `author_association "COLLABORATOR"`, `state "COMMENTED"`, `submitted_at "2023-10-25T00:14:50Z"`, `body ""`. `FACT` — one submission is `CHANGES_REQUESTED`; it is not "the" state of the thread, and the second submission neither seconds nor withdraws it. `UNKNOWN` — whether that `CHANGES_REQUESTED` review was later dismissed is **not established by this capture**: GitHub records review dismissals as timeline events, and no timeline or dismissal endpoint was queried — the captured families are `/pulls/330/reviews`, `/pulls/330/comments`, `/issues/330/comments` and `/pulls/330`. `INFERENCE` — nothing in what *was* captured supersedes it, so it is treated as standing; reasoning: the reviews array holds no later submission by the same reviewer changing state, the pull request is still `state "open"` with `mergeable false` and `mergeable_state "dirty"`, and the last conversation comment (`2023-11-16T02:44:26Z`) is the author asking for a merge rather than a reviewer clearing the objection. `FACT` — the substantive objection is recorded verbatim in the thread: the reviewer states that the pull request cannot be approved *because the expansion of the copybook is no longer happening "in place"*, meaning working-storage elements are moved to other locations in working storage. `FACT` — a second objection concerns a **removed test**: a review comment on `src/test/java/org/openmainframeproject/cobolcheck/ExpanderTest.java` asks why the test needed removing and states that a no-longer-passing test should be fixed rather than removed. `FACT` — the author's final comment asks for it to be merged with a later follow-up fix, and acknowledges the same problem may exist in the file-section expander. `FACT` — the pull request reports `mergeable = false` and `mergeable_state = "dirty"`.

`INFERENCE` — the reviewer's objection is technically correct and decisive; reasoning: the tool's contract is source injection into a *copy* at the position the original directive occupied, so relocating expanded working-storage items changes the generated program's data layout, which is observable and can change compilation behaviour for `REDEFINES` and level-01 grouping. `INFERENCE` — the removed-test objection is independently decisive under this programme's own discipline; reasoning: existing assertions may not be deleted to make a change pass, and the only permitted mechanism is an itemized corrective task declared before a run starts. **Disposition:** the *intent* of #330 — expanding DB2 copybooks correctly — is adopted in Run 3, but the implementation is not. The two conflicting objections are honoured by expanding **in place** and by keeping `ExpanderTest` intact. #411 is the better-shaped **starting point** for the same concern, since it applies cleanly and adds tests rather than removing one — but "better-shaped" is a statement about its diff, not about its scrutiny. `FACT` — #411 carries **no upstream review at all**: `/pulls/411/reviews`, `/pulls/411/comments` and `/issues/411/comments` each returned HTTP 200 with **zero records**, the pull-request payload's own `review_comments` and `comments` counters are both `0`, and `requested_reviewers` and `requested_teams` are both empty arrays. `INFERENCE` — that asymmetry is the trap to avoid: #330 was rejected by a domain reviewer while #411 was never examined by one, so ranking #411 above #330 on "applies cleanly and adds tests" would be substituting mergeability for scrutiny; reasoning: an unreviewed patch touching the same expansion path can carry the very defect #330 was rejected for, and nothing in the capture rules that out. **Adoption gate, mandatory:** #411 is characterized under a golden file in Run 1b before it is applied, and reviewed on technical merit in Run 3 against the in-place-expansion objection — specifically, whether it relocates working-storage items — with the verdict and its evidence recorded in that run's handoff. If it relocates them, it is treated exactly as #330 was: intent adopted, implementation not.

**Adjudication of #410 versus #411.** `FACT` — #410 touches `src/main/cobol/FileCopy.cbl`, `src/main/cobol/copy/FSTEST.cpy` and `LineRepository.java`, and its stated problem is that multiple `COPY` statements in the **FILE SECTION** reused the first copybook because `copyTokens` was not cleared. `FACT` — #411 touches `LineRepository.java`, `InterpreterController.java`, `Generator.java`, three new COBOL fixtures, a new `.cut` suite, `ExpanderTest.java` and `InterpreterControllerTest.java`, and its own description states that it both expands **WORKING-STORAGE** copybooks *and* clears `copyTokens` between `COPY` statements. `INFERENCE` — #411 subsumes #410's code fix and adds capability, so adopting both would apply the same correction twice; reasoning: the token-clearing concern appears in #411's own stated solution. `INFERENCE` — but #411's fixtures are working-storage while #410's are file-section, so adopting only #411 would lose a file-section regression fixture; reasoning: the two touch different COBOL sections and the reuse bug was originally reported in the file section. **Disposition:** adopt #411's code; harvest #410's `FileCopy.cbl` and `FSTEST.cpy` fixtures into the characterization corpus so the file-section case stays covered.

**Adjudication of #337 versus #408.** `FACT` — both modify `org.openmainframeproject.cobolcheck.features.interpreter.CobolReader`. #337 is titled "Area after col 72" and trims content past column 72; #408 filters `EJECT`/`SKIP` pagination directives **and** removes right-hand sequence numbers, touching `CobolReader`, `Constants`, `StringHelper` and two test classes. `FACT` — #337 also edits `vs-code-extension/Cobol-check/bin/cobol-check-0.2.8.jar`, a path that does not exist at the fork's head, which is why it conflicts on a binary. `INFERENCE` — #408 covers #337's concern and adds a second one, applies cleanly, and avoids both the committed-jar edit and the `InterpreterController` churn; reasoning: right-hand sequence numbers are exactly the content in the area after column 72, and #408's surface is strictly narrower on the interpretation path. **Disposition:** adopt #408; treat #337 as superseded and harvest only its fixture and any uncovered test case.

**A finding about #338 that changes Run 1a's framing.** `FACT` — #338's entire build-script diff is `dependsOn fatJar` → `dependsOn copyJarToBin, copyRunScripts`, and the fork **already contains** `dependsOn copyJarToBin, copyRunScripts` at `[build.gradle:L203]`. `FACT` — #338 does not move the task body out of the configuration block. `FACT` — the harness remains vacuous in the fork today: a cold-tree run of the documented gate produced six `./temp/approvalTest/cobolcheck: not found` lines, `exit from compare: 0`, a `PASS` verdict, a **0-byte** `actual-output.txt` and zero compiled COBOL programs, all printed under `> Configure project :` before `> Task :clean`. `INFERENCE` — the obvious fix was already attempted upstream and does not work, because `dependsOn` orders task **execution** while the body runs during **configuration**, before any task executes; reasoning: the comparison verdict is printed before `:clean`. **Disposition:** Run 1a supersedes #338 and its prompt must say so, so no executing agent re-derives a fix that is already present and already insufficient.

### A5.2 The 45 open issues

`FACT` — numbers, titles, labels and dates are taken from the persisted capture, and each **Raw upstream title** cell is the captured `title` string byte-for-byte. Grouped by disposition; every issue appears exactly once.

**Critical — the programme's objective depends on these.**

| # | Raw upstream title (verbatim from capture) | Normalized subject (this plan) | Assigned to | Reason |
| --- | --- | --- | --- | --- |
| 329 | Fix approval tests | — | **Run 1a**, completed by **Run 1b** | The false-green root cause. Measured: 0-byte `actual-output.txt`, zero programs compiled, build exit 0 |
| 15 | Mock a batch file READ and provide a fake record | — | **Run 5** | Literally part of the unshipped release: `[CHANGELOG.md:L21]` lists "Mock batch file I/O" under `## \[0.3.0\]` `### Planned` at `[CHANGELOG.md:L15-L17]` |
| 20 | Mock or stub a SQL resource and provide fake results | — | **Run 6** | `[CHANGELOG.md:L20]` lists "Mock SQL tables" under the same planned release |
| 17 | Mock or stub a CICS resource and provide fake results | — | **Run 7** | `[CHANGELOG.md:L19]` lists "Mock CICS resources" under the same planned release |

**High — a real defect, or a requested capability with an identified consumer.**

| # | Raw upstream title (verbatim from capture) | Normalized subject (this plan) | Assigned to | Reason |
| --- | --- | --- | --- | --- |
| 113 | Globs should be supported for --programs and --tests options | Glob support for `--programs` and `--tests` (bug) | **Run 3** | Labelled `bug` upstream. Same defect class as the unhandled empty-stream crash; the catalogue already carries `ERR007` and `ERR010` reading `empty input stream` at `[src/main/resources/org/openmainframeproject/cobolcheck/messages/messages.properties:L9, L12]` |
| 335 | Issue with program with value after position 72 | — | **Run 3** | Closed by adopting pull request #408, which applies cleanly |
| 339 | Testing a callable program - error: executable program requested but PROCEDURE/ENTRY has USING clause | Callable program rejected because `PROCEDURE`/`ENTRY` has a `USING` clause | **Run 3** | Closed by adopting pull request #409, which applies cleanly |
| 206 | Extent the functionality of the copybook expander | Extend the functionality of the copybook expander — the captured title carries the upstream typo *Extent* | **Run 3** | Addressed by adopting #411 plus the `COPY ... REPLACING` repair; the defective path uses hardcoded token positions at `[.../features/interpreter/LineRepository.java:L110-L113]` and a regex `replaceAll` at `[.../features/interpreter/CopybookExpander.java:L72, L79]` |
| 346 | Cobol check can't understand and suppress "USAGE SQL" | Cobol Check cannot understand or suppress `USAGE SQL` | **Run 3** | An interpretation defect on the stub path; the token set is `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L145-L147]` |
| 394 | Variable with format PIC S9(n)V9(n) SIGN LEADING is not recognized as numeric | `PIC S9(n)V9(n) SIGN LEADING` not recognized as numeric | **Run 3** | A `NumericFields` classification defect; misclassification silently switches an `EXPECT` from numeric to alphanumeric comparison |
| 147 | config.properties file should be optional in COBOL folder | `config.properties` should be optional in the COBOL folder | **Run 3** | Additive configuration behaviour with a default reproducing current behaviour |
| 221 | Add configuration for compiler options | — | **Run 3** | The key exists but holds the **literal string** `null`: `[config.properties:L188]` `gnucobol.compile.options = null` |
| 306 | Enable usage of linkage section items during tests | Enable usage of linkage-section items during tests | **Run 3** | Advanced by #409; a third-party fork also carries a linkage-section stubbing fix behind a configuration flag |
| 74 | Create ProcessLauncher for z/OS using JZOS | Create `ProcessLauncher` for z/OS using JZOS | **Run 9** | The `ZOS` arm returns `null`: construction commented out at `[.../features/launcher/Launcher.java:L62]`, `launcher` initialised `null` at `[:L46]` |
| 75 | Create ProcessLauncher for z/OS using Zowe | Create `ProcessLauncher` for z/OS using Zowe | **Run 9** | Same arm; one of the two implementations is chosen in that run, not both |
| 390 | z/OS Process launcher fails to load | z/OS process launcher fails to load | **Run 9** | Direct consequence of the `null` return above, compounded by an empty `[config.properties:L206]` `zos.process` |
| 93 | Add autoincremented build numbers to the gradle build | Add autoincremented build numbers to the Gradle build | **Run 2** | Must be resolved regardless: two releases share one version at `[CHANGELOG.md:L23]` and `[CHANGELOG.md:L26]` |

**Medium — worth doing, no blocking dependency.**

| # | Raw upstream title (verbatim from capture) | Normalized subject (this plan) | Assigned to | Reason |
| --- | --- | --- | --- | --- |
| 189 | Refactor the TestSuiteParser.java class | Refactor the `TestSuiteParser.java` class | **Run 4** | The largest class in the codebase at 60,602 bytes / 1,240 lines. Absorbed as the parser refactor the extension-point extraction needs, under a byte-identical golden-file oracle |
| 289 | Refactoring of the TestsuiteParser class | Refactoring of the `TestsuiteParser` class | **Run 4** | A duplicate of #189; recorded as such rather than triaged twice |
| 228 | Make it more obvious what's boilerplate and what's code being tested | — | **Run 4** | The stub-emission seam is exactly where the boundary becomes explicit |
| 391 | Question: Filtering/Blocking STOP RUN/GOBACK | Filtering/blocking `STOP RUN`/`GOBACK` | **Run 4** | A question about what the stub predicate should match; answerable only once the seam is a strategy |
| 97 | Test case reporting improvements | — | **Run 3** | Rides with the output-format repairs |
| 210 | `Enable multiple paths for certain configurations ` | Enable multiple paths for certain configurations — the captured title carries a trailing space | **Run 3** | Additive configuration key with a backward-compatible default |
| 334 | Output file group declaration copied twice in the generated program | — | **Run 1b**, then **Run 3** if still reproducible | `[CHANGELOG.md:L27]` claims a related repair shipped in one of the two `0.2.19` entries. Characterize first: if the corpus shows it fixed, close it with the golden file as evidence; if not, itemize it in Run 3. Documentation is not evidence |
| 185 | Add encoding for the generated test-results file | — | **Run 1b**, then close or itemize | `[CHANGELOG.md:L33]` claims encoding was addressed in `0.2.17`. Same treatment: characterize, then close with evidence or itemize |
| 151 | Management of the Linkage Section and GETMAIN CICS | Management of the linkage section and `GETMAIN` CICS | **Run 3** for the linkage half via #409; **Run 7** for the CICS half | Splits cleanly along the two disciplines: parsing correction versus new CICS mock behaviour |
| 127 | Change test case for internationalization to refer to resource name instead of absolute text values | Internationalization test should refer to a resource name rather than absolute text values (bug) | **Run 3** | **Not Run 1b.** It requires *modifying* a pre-existing assertion, and the only permitted mechanism for that is an itemized corrective task naming the class and method before a run starts — which Run 1b, whose handoff §10 must read NONE, does not and may not have. It lands in Run 3 as itemized correction (i): class `org.openmainframeproject.cobolcheck.MessagesTest`, method `it_retrieves_a_message_with_substitution_values`, old expected value the literal `"ERR001: testSuite is null on entry to Generator.runSuite() method."` at `[src/test/java/org/openmainframeproject/cobolcheck/MessagesTest.java:L12-L13]`, new expected value the same message resolved from the bundle key rather than transcribed, motivating issue #127 |
| 287 | Visual Studio Code extension should try to catch infinite loops | VS Code extension should try to catch infinite loops | **Run 3** | Closed by adopting the intent of pull request #336, which adds a configurable timeout. **Not Run 2:** a timeout is an observable behaviour change in the extension, and Run 2's `S2·PRES` discipline makes any observable change a defect. Run 3 is the corrective run that already owns seven of the eight pull requests, so it lands there as itemized correction (h), with the extension subtree declared as an out-of-enumeration `S2` touch under a corrective discipline |
| 384 | Quick Question: Installation Guide for z/OS | Installation guide for z/OS | **Run 9** | Documentation that cannot honestly be written until the launcher exists and its verification status is settled |

**Low — genuine but low-leverage.**

| # | Raw upstream title (verbatim from capture) | Normalized subject (this plan) | Assigned to | Reason |
| --- | --- | --- | --- | --- |
| 129 | Working storage in the unit test | Working storage in the unit test — labelled *in discussion* upstream | Deferred beyond Run 9 | Labelled `enhancement` and `in discussion` upstream. An unresolved design discussion, not an actionable defect. It touches the same data-layout question that decided the #330 adjudication, so it should be revisited only once expansion is provably in-place |
| 212 | Double-clicking tree-entry should browse code in VS Code Extension | Double-clicking a tree entry should browse code in the VS Code extension | Deferred beyond Run 9 | Extension user-experience work with no dependency on any run here |
| 343 | FreeBSD version in wiki does not make sens | FreeBSD version in the wiki does not make sense — the captured title carries the upstream typo *sens* | **Run 2**, documentation only | The wiki is unreachable and is not the documentation surface this programme maintains. What is actionable is that in-repo documentation must not repeat it |
| 102 | Add support for EXPECT x TO BE NUMERIC | Add support for `EXPECT x TO BE NUMERIC` | Deferred beyond Run 9 | Additive DSL surface, deferred for the reason stated immediately after this table |
| 18 | Perform one-time setup at the start of a test suite | — | Deferred beyond Run 9 | Additive DSL surface, and `[CHANGELOG.md:L12]` lists "Before/After All" under the stale `## \[1.0.0\]` `### Planned` heading at `[CHANGELOG.md:L8-L10]` |
| 21 | Support parameterized test cases | — | Deferred beyond Run 9 | Additive DSL surface; `[CHANGELOG.md:L13]` lists it under the same stale heading |
| 168 | Introduce RETURNS when mocking CALL-statements | Introduce `RETURNS` when mocking `CALL` statements | Deferred beyond Run 9 | Additive DSL surface on the mock grammar |
| 237 | Support for ending execution of a unit test in a mock | — | Deferred beyond Run 9 | Additive DSL surface on the mock grammar; note that `src/test/cobol/RETURNCODE/ReturnCode-4.cut` already exercises `GOBACK` inside a mock |
| 294 | [ENHANCEMENT] Array initializer | Array initializer — the captured title carries an `[ENHANCEMENT]` prefix | Deferred beyond Run 9 | Additive DSL surface |
| 399 | All-pair with automatic combinations | — | Deferred beyond Run 9 | Additive DSL surface, and partially subsumed by the generator in Run 8 |
| 213 | Make a gradle wrapper for Cobol Check | Make a Gradle wrapper for Cobol Check | **Close as already satisfied** | `FACT` — the wrapper exists: `gradle/wrapper/gradle-wrapper.jar` (58,694 bytes) and `gradle/wrapper/gradle-wrapper.properties` with `distributionUrl` pointing at `gradle-6.9.4-all.zip`. Run 2 replaces the distribution; the issue itself is done |

**The deferral reasoning for the additive-DSL group (#102, #18, #21, #168, #237, #294, #399), stated rather than implied.** `INFERENCE` — each of these adds permanent public `.cut` syntax, and the mock-syntax contract is not settled until the three resource-mocking runs have shipped their type vocabulary; designing new DSL surface before that risks two incompatible grammars in the same language; reasoning: the mock grammar and the assertion grammar share the same parser and the same keyword table, so a syntax choice made now constrains the mock syntax later. They are deferred **with** this reason rather than dropped, and the sequence deliberately ends at the point where generation exists, because a generator changes which DSL features are worth adding.

**Won't-do — declined, with the reason.**

| # | Raw upstream title (verbatim from capture) | Normalized subject (this plan) | Reason |
| --- | --- | --- | --- |
| 323 | IMS | IMS support | No IMS runtime is reachable, so any implementation would ship structurally unverified with no path to validation. `FACT` — a third-party fork carries a branch named `IMS`, but its single commit is "Fix for stubbing linkage section if config is set" touching four files, not IMS support, so no prior art exists even there |
| 220 | Build cobol-check as GraalVM native-image | Build cobol-check as a GraalVM native image | Adds a second build and packaging path before the primary one is releasable at all. `FACT` — no release workflow of any kind exists: `.github/` holds exactly three files and none publishes a JAR. `FACT` — the issue's own thread records the native image failing to load packaged copybook resources, which is the same classpath-resource fragility that makes the rename's equal-depth namespace mapping load-bearing |
| 53 | Review and correct localized messages | — | `FACT` — seven locale bundles already ship (`messages.properties` plus `_de`, `_es`, `_fi`, `_fr`, `_it`, `_ja`). Extending or reviewing them serves no consumer identified for this revival, and the work needs native speakers rather than engineering |
| 321 | FR: please co-publish vscode extension on Open VSX Registry | Co-publish the VS Code extension on the Open VSX Registry — a second marketplace | `FACT` — this is the only issue with recent activity in the capture. Declined **for now** nonetheless: a second marketplace before the first automated release exists inverts the dependency order. It becomes reconsiderable the moment Run 2's release workflow ships, and it is recorded here rather than dropped precisely because it is the most likely candidate for early reconsideration |
| 150 | Produce Sonarqube formatted output for test results. | Produce SonarQube-formatted output for test results | Declined as a consequence of removing the analysis integration entirely rather than retargeting it: the plugin at `[build.gradle:L5]` blocks a Gradle 9 migration, resolves from a plugin host at configuration time, and points at a third party's organization at `[build.gradle:L19-L20]`. `FACT` — the thread records that XML/JUnit result formatting was already delivered by an earlier merged pull request, so the remaining request is specifically the SonarQube dialect the captured title names. Note that the `xml` format currently throws, and its repair rides with the fat-jar fix in Run 2 |

### A5.3 Triage totals

`FACT` — 45 issues, each appearing exactly once: **4** Critical (#329, #15, #20, #17), **13** High (#113, #335, #339, #206, #346, #394, #147, #221, #306, #74, #75, #390, #93), **12** Medium (#189, #289, #228, #391, #97, #210, #334, #185, #151, #127, #287, #384), **11** Low (#129, #212, #343, #102, #18, #21, #168, #237, #294, #399, #213), **5** Won't-do (#323, #220, #53, #321, #150). 4 + 13 + 12 + 11 + 5 = 45. `FACT` — 8 pull requests: **3** adopted (#408, #409, #411), **2** superseded with fixture harvest (#337, #410), **1** superseded by a run (#338), **1** adopted in intent only on the current manifests (#336), **1** not adopted as authored with its intent re-implemented (#330).

`INFERENCE` — the shape of this triage is itself a finding: the three Critical feature issues are exactly the three items listed as planned in the unshipped release at `[CHANGELOG.md:L19-L21]`, and the fourth Critical issue is the harness that would have caught their absence; reasoning: the release notes name them and the harness compares a zero-byte file, so the project stalled with its next release specified and its safety net inoperative.

---

## A6. Backward-compatibility impact analysis

### A6.1 The four consumers this analysis is assessed against

| Consumer | Standing of the claim | What they depend on |
| --- | --- | --- |
| A language-track test runner for a programming-exercise platform, roughly 46 exercises | `FACT` — corroborated independently by the persisted capture: issue #220's body states its author was testing the tool for a GnuCOBOL language track on a learning platform, and its thread records a packaging concern about installing a Java program | The command-line invocation shape, the `txt` result format, and a low-friction install |
| A training course built on the tool | Carried ground truth, **not independently verified in this session**; labelled as such rather than restated as fact | The `.cut` DSL exactly as taught, and stable output text |
| Roughly 2,980 extension installs | Carried ground truth as to magnitude, **not independently verified in this session**. `FACT` — the extension exists and is published under a marketplace item identifier recorded at `[README.md:L26]` | The extension identifier, the publisher identity, and the jar the extension ships |
| A bank IT provider running the tool in a regulated release pipeline | Carried ground truth, **not independently verified in this session** | Reproducible dependency resolution, the invocation shape, and — critically — an exit code that reflects test outcome |

`INFERENCE` — all four are pinned to an archived upstream, so a maintained fork that stays compatible is immediately useful and one that breaks their tests is worthless; reasoning: the upstream repository carries `archived = true`, so no consumer can receive a fix from it.

### A6.2 Surface-by-surface impact

| Surface | Treatment | What existing users observe | Run |
| --- | --- | --- | --- |
| **`.cut` test DSL** | **Preserved** | Nothing. Every `0.2.19` construct continues to parse and behave identically: suite and case declarations, the expectation family and its comparators, the four existing mock types, mock termination, the verification count family, per-case hooks, 88-level truth assertions, reference modification and qualified names. New mock types are **additive only**, added as new `<TYPE>` values in the existing `MOCK <TYPE> ... END-MOCK` grammar | 5, 6, 7 add; no run removes |
| **`config.properties`** | **Preserved, extended additively** | Nothing. Existing keys keep their names, semantics and defaults without exception, and a `0.2.19` configuration file continues to work unchanged. `FACT` — 36 non-comment keys exist today. Exactly one new key is anticipated before the resource-mocking runs: the copybook-resolution key, whose default reproduces the current bundled behaviour. The resource-mocking runs add their mock keys and one output key, `test.results.annotate.provisional`, defaulting to `false` so output stays byte-identical unless a user opts in | 3 adds one key; 5, 6, 7 add mock keys plus `test.results.annotate.provisional` (default `false`) |
| **CLI flag set and semantics** | **Preserved, extended additively** | Nothing, unless a user opts in by passing the new flag. `FACT` — the flag set is one string at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L27-L28]` and every existing short and long option keeps its name and semantics. Run 8 adds **one** new flag, which selects generation mode and dispatches directly into the generator package rather than through the existing worker chain — see ADR-06. `INFERENCE` — no existing invocation is affected; reasoning: the flag is new, so no current command line supplies it, and with it absent `Main.main` takes exactly the path it takes today. It **is** new observable public surface and is declared in advance, recorded in Run 8's handoff §7, and announced in the changelog. Generation and precompilation are mutually exclusive: the flag together with `--tests` or `--all-tests` is a usage error | **8** |
| **CLI exit codes** | **CORRECTED — the single deliberate break** | A pipeline that today sees `0` for a failing COBOL test will see a **new distinct nonzero code**. `FACT` — today five GnuCOBOL children returned `4, 0, 0, 4, 0` and all five runs logged `terminating: 0`, because the decision is `if (exitCode > 4)` at `[src/main/java/org/openmainframeproject/cobolcheck/Main.java:L25]` and only `STATUS_NORMAL = 0` / `STATUS_HALT = 8` exist at `[.../services/Constants.java:L16-L17]`. `FACT` — conversely `--help` and `--version` already exit `8` via `[.../workers/Initializer.java:L35-L37, L40-L42]` → `[.../features/Status/StatusController.java:L21, L61]`. **A pipeline that treated `0` as success was previously being told success unconditionally**, so the change surfaces failures that were always there rather than creating new ones. This is announced in the changelog and carried in open questions with a migration note | **3** |
| **`txt` / `directOutput` result format** | **Preserved — the protected surface** | Nothing. `FACT` — it is the default: `[config.properties:L141]` `test.results.format = txt` and `[config.properties:L151]` `test.results.format.style = directOutput`, and it works | — |
| **`xml` result format** | **CORRECTED** | A format that throws today starts working. `FACT` — `[.../Formatter/Formats/XMLFormat.java:L19]` calls `JAXBContext.newInstance(...)` against `javax.xml.bind` imports at `L5-L7`, and the class was never packaged because `configurations.compile` resolves to nothing — verified: `./gradlew dependencies --configuration compile` reports `No dependencies`, and all three committed archives contain **zero** entries under `javax/`, `jakarta/`, `com/sun/`, `org/glassfish/` or `org/jetbrains/`. Repair is a side effect of replacing `configurations.compile` with `runtimeClasspath` at `[build.gradle:L138]`. Format names, style enumeration values and the result XML schema are preserved; only the exception is removed. `INFERENCE` — additive from every user's standpoint, because no consumer can depend on behaviour that throws | **2**, under its declared secondary discipline **`S2·CORR`**, whose scope is this one build-script line and this one format and whose oracle is: `test.results.format = xml` completes with exit 0 and emits well-formed JUnit XML where it previously raised `NoClassDefFoundError`, with the `txt` default path and every golden file byte-identical |
| **`html` result format** | **CORRECTED** | A format that throws today starts working. `FACT` — this is **not** a linkage failure: `[.../Formatter/Formats/HTMLFormat.java:L19]` tests `if (dataTransferObject instanceof String)` and raises `IncompatibleClassChangeError` at `[:L26-L27]` when it is false. It is a deliberate throw on a data-transfer-object type mismatch, so the repair is DTO wiring. `INFERENCE` — treating it as a linkage problem would leave it unfixed after the dependency work lands; reasoning: adding JAXB to the jar cannot change an `instanceof String` test | **3** |
| **Provisional-mock annotation in result output** | **ADDED, opt-in, off by default** | Nothing unless the user sets the new key. `FACT` — the protected default is `[config.properties:L141]` `test.results.format = txt` with `[config.properties:L151]` `test.results.format.style = directOutput`, and with the new key `test.results.annotate.provisional` at its `false` default the output of every format and style is **byte-identical** to today. With it `true`, an affected `txt` result line is followed by one `           *> PROVISIONAL <mock-type> <register-entry-id>` line — comment-shaped, so inert to any consumer scanning for verdicts — and the `xml` form gains a `provisional="<register-entry-id>"` attribute. `INFERENCE` — this is the only way the annotation and the byte-identical golden oracle can both hold; reasoning: Runs 5, 6 and 7 each require every pre-existing golden file unchanged, which an unconditional annotation would violate. Schema declared before the first annotation ships and permanent thereafter; golden files exist for **both** settings | **5**, **6**, **7** — whichever adds the first provisional claim introduces the key |
| **Invocation shape** | **Preserved** | Nothing. Runnable as a jar from a shell script or a container. `FACT` — the generated launch script is exactly `#!/bin/sh` followed by `java -jar bin/cobol-check-0.2.19.jar $@`, so the shape is a plain jar invocation | — |
| **Artifact coordinates, product name, publisher, extension ID** | **CHANGED, deliberately and announced** | A new Gradle group, artifact ID, Java package namespace, product display name, marketplace publisher and extension identifier. `INFERENCE` — extension users must install under the new identifier rather than receiving an in-place update; reasoning: a marketplace item identity change is a new item, not an update to the old one, so existing installations of `openmainframeproject.cobol-check-extension` keep working and receive nothing further. The first release under the new identity is `0.3.0`. `FACT` — publishing under the new identity requires a new Marketplace publisher token, exposed as the correctly-spelled secret `VSCE_PAT` and superseding the misspelled `VS_CODE_EXTENTION_SECRET` at `[.github/workflows/DeployExtension.yml:L27]`; if it is absent the extension-publish job is skipped with an explicit message and the JAR release proceeds regardless | **2** |
| **Tested-on-Java-8 posture** | **CHANGED, spent deliberately** | See below | **2** |

### A6.3 The derived consequence: the JDK 8 test leg does not survive

`FACT` — measured in this session and reproduced verbatim in A2.3 leg 2: `JAVA_HOME=$JDK8_HOME ./gradlew clean test` reports `BUILD SUCCESSFUL in 10s`, exit 0, with the JUnit XML recording **457 tests, 0 failures, 0 errors, 0 skipped**. `FACT` — `JAVA_HOME=$JDK21_HOME ./gradlew clean test` reports `457 tests completed, 227 failed` then `BUILD FAILED in 16s`, exit 1, and the 227 failures carry `java.lang.IllegalArgumentException: Unsupported class file major version 65` in **183** cases and `java.lang.IllegalArgumentException: Unknown Java version: 21` in **36**, against a resolved `net.bytebuddy:byte-buddy:1.10.18`. The full per-chain decomposition, and the correction of two frames that do not appear in the report, are in A2.3 leg 3.

`INFERENCE` — unblocking JDK 21 requires a Mockito line whose bundled Byte Buddy understands class-file version 65, and that line requires **Java 11 or later at test runtime**, so the JDK 8 test leg cannot survive the bump; reasoning: the failure is a class-file-version rejection inside the mocking framework's agent, which is fixed only by a newer framework, and newer frameworks raise their own runtime floor.

**What this does and does not change for users.** `FACT` — the shipped artifact remains Java 8 bytecode: `[build.gradle:L67-L68]` sets `sourceCompatibility` and `targetCompatibility` to `VERSION_1_8`, and the plan retains Java 8 as the *shipped* bytecode level by using a toolchain while the build daemon runs on JDK 17 or later. `FACT` — the runtime dependencies are Java 8 compatible: the resolved runtime closure is nine coordinates, headed by `jakarta.xml.bind:jakarta.xml.bind-api:2.3.2` and `org.glassfish.jaxb:jaxb-runtime:2.3.2`. `INFERENCE` — no consumer running the jar on a Java 8 JVM is broken by this; reasoning: the bytecode level and the runtime dependency set are unchanged.

**But "the project is no longer *tested* on Java 8" is a real change of posture**, and it is stated here rather than absorbed silently into a dependency bump. `FACT` — the 457-test suite passes on JDK 8 today, so the guarantee being given up is a measured one, not a theoretical one. It is spent deliberately, in exchange for a JDK 21 leg that currently fails 227 tests, and it is recorded in the changelog alongside the exit-code change.

### A6.4 What must never happen silently

Any break not listed above must be raised as an open question with a migration path before it is introduced. `INFERENCE` — the enforcement mechanism is the golden-file oracle plus the itemization rule; reasoning: in a preserving run any observable change is a defect, and in a corrective run an unitemized golden-file change is a defect even if it looks like an improvement, so an unannounced break cannot pass a run boundary without someone deliberately suppressing evidence.

---

## A7. Correctness strategy — how mocked DB2, VSAM and CICS semantics are known to be right without IBM compilers

### A7.1 The constraint, stated plainly

`FACT` — the only executable COBOL target available is GnuCOBOL: `cobc (GnuCOBOL) 3.2.0`, built `Sep 23 2025 17:57:37`, packaged `Jul 28 2023 17:02:56 UTC`, C version `15.2.0`. No IBM Enterprise COBOL, DB2 or CICS is available, and this cannot be solved by effort. `INFERENCE` — therefore the GnuCOBOL path is *verified* and the z/OS path is *structured but explicitly unverified*; reasoning: verification requires execution, and only one of the two can be executed here.

### A7.2 The two reachable sources, in a fixed order

**Source 1 — the in-repo authority, consulted first.**

- `FACT` — `org.openmainframeproject.cobolcheck.services.cobolLogic.EIBResponseTable` holds exactly **76** condition entries at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java:L43-L118]`, each mapping a CICS condition name to an `EIBResponseCodes` value constructed from four components at `[.../services/cobolLogic/EIBResponseCodes.java:L21]`. This is the primary in-repo semantic authority for CICS conditions.
- `FACT` — `src/main/cobol/copy/SQLCA.cpy` carries the SQLCA **field layout** across 24 lines: `SQLCAID`, `SQLCABC`, `SQLCODE PIC S9(9) USAGE BINARY VALUE 0`, `SQLERRM` containing `SQLERRML` and `SQLERRMC`, and `SQLERRP`. This is the authority for SQLCA **structure**.

**Source 2 — documented GnuCOBOL `-std=ibm` semantics, consulted second**, always labelled `INFERENCE` with any divergence from the in-repo authority stated explicitly.

**Anything covered by neither is emitted `UNKNOWN`**, inherited by the consuming run as an explicit `BLOCKED-BY`, and the affected mock type ships **labelled provisional**.

### A7.3 What is verified, what is provisional, and what is unknown

| Claim class | Status | Basis |
| --- | --- | --- |
| CICS condition **name → code triple** mapping for the **74** condition names inserted exactly once | **Verified against the in-repo authority** | `FACT` — the table is explicit at `[.../services/cobolLogic/EIBResponseTable.java:L43-L118]` and each of these names is inserted once, so the source text and the constructed map agree. A mock that sets these fields is verified *as consistent with the table*, and the register records exactly that scope |
| **`LENGERR` specifically** | **`provisional` until Run 7 resolves the duplicate** | `FACT` — inserted twice, at `[:L51]` with EIBFN `0x04` and `[:L84]` with EIBFN `0x06`; `HashMap.put` keeps the `0x06` tuple. `INFERENCE` — a register entry may not be marked `verified` against a source that states the value twice and disagrees with itself, even though the surviving value matches the javadoc assumption at `[:L22]`; reasoning: the label asserts that the source establishes the value, and a self-contradictory source does not |
| The **count** of mockable CICS conditions | **75, not 76** | `FACT` — 76 `put(` insertions resolve to 75 unique keys. `INFERENCE` — the register's own coverage arithmetic must use the map size; reasoning: an entry for a 76th condition could never be covered by a corpus program, so it would fail the A7.4 check permanently |
| CICS conditions **outside** the 75 | **`UNKNOWN`** | `FACT` — not in the table. `FACT` — a third-party archive on a fork defines only **44** response constants and does not reference the in-repo table at all, so it cannot extend it |
| **SQLCA field layout** | **Verified against the in-repo authority** | `FACT` — the 24-line copybook, live on the resolution path from `[src/main/cobol/DB2PROG.cbl:L12]` |
| **`SQLCODE` values** beyond those a corpus program exercises | **`UNKNOWN`** | `FACT` — `SQLCA.cpy` carries layout only and declares `SQLCODE` with `VALUE 0`; it carries no value semantics. `FACT` — a third-party design document on a fork uses only `0`, `100` and `-001` and cites **no source at all**: a search for `IBM`, `DB2 for z/OS`, `SQL Reference`, `GnuCOBOL` and `-std=ibm` returns zero matches |
| **Batch file I/O and VSAM `FILE STATUS` values** | **Provisional except where the corpus demonstrates them** | `FACT` — the tool recognizes batch file I/O statements through `Interpreter.checkForBatchFileIOStatement(CobolLine)`, but no in-repo table of status values exists |
| Mocked **control flow** — that a stubbed statement dispatches to a mock, that a read loop terminates, that a mock is invoked the asserted number of times | **Verified by execution** | `INFERENCE` — this class is fully verifiable on GnuCOBOL, because it is a property of the generated program's control flow rather than of a proprietary subsystem's semantics; reasoning: the generated program is compiled and run, so the assertion either fires or does not |

`INFERENCE` — the strategy's core insight is that these two classes are separable: **control flow is verifiable, values are not**; reasoning: the precompiler's contribution is the injected control flow, and the resource semantics are data the user supplies to the mock. So the tool can be honest and useful simultaneously — it guarantees that the mock fires and that the program behaves as if the resource responded, while labelling the *response values* as the user's responsibility wherever no reachable source establishes them.

### A7.4 How the distinction is CI-enforced

A single in-repo **verified/provisional register** is the shared artifact across every run that touches semantics. Enforcement has three mechanical parts:

1. **Every mock type and every semantic claim has exactly one register entry**, marked `verified` or `provisional`, with the entry for `verified` naming the source — either the in-repo authority with its `file:line`, or documented GnuCOBOL `-std=ibm` behaviour.
2. **CI fails when an entry marked `verified` has no corpus program covering it.** `INFERENCE` — this is what makes the label falsifiable rather than declarative; reasoning: without it, "verified" is an assertion an author can make for free, which is exactly the false-confidence failure mode.
3. **CI fails when a mock type exists in the parser but has no register entry at all**, so a new type cannot ship unlabelled.
4. **CI proves the annotation key still defaults to off.** A test runs the tool with `test.results.annotate.provisional` unset and asserts the output is byte-identical to the golden file captured for the `false` setting. `INFERENCE` — without it the key can silently flip and break the protected format; reasoning: a default is only a default while something checks it, and this key is the one place the correctness strategy touches a surface the compatibility contract protects.
5. **Condition, status and code coverage is enumerated from the *constructed map*, never from the number of `put(` calls in the source.** `FACT` — those two numbers already differ: `EIBResponseTable` performs 76 insertions at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java:L43-L118]` and the resulting map holds 75 keys, because `LENGERR` is inserted at both `[:L51]` and `[:L84]`. `INFERENCE` — a check that counted source text would certify a condition the runtime does not expose; reasoning: the 76th insertion is overwritten before any lookup can reach it, so a register generated from `put(` sites would contain an entry no corpus program can possibly cover. The check must additionally **fail on a duplicate key**, which is what turns the `LENGERR` entry from a permanent caveat into a closable task for Run 7.

`INFERENCE` — the register must be append-only with one owning run per entry; reasoning: runs execute in separate sessions with no shared memory, so a mutable shared file is the one place two runs could silently contradict each other.

### A7.5 How the distinction is user-visible

**In the result output — opt-in, additive, and off by default.** `INFERENCE` — this has to be opt-in rather than automatic, and the reason is a hard constraint rather than a preference; reasoning: A6.2 protects the `txt`/`directOutput` format as the surface existing users depend on and Runs 5, 6 and 7 all carry the oracle "every pre-existing golden file byte-identical", so an annotation emitted unconditionally would violate both at once — it would change the protected default format's bytes and move every golden file, and neither is distinguishable from a defect. The design that satisfies both:

- **A new configuration key, `test.results.annotate.provisional`, default `false`.** With the default, output is **byte-identical** to today for every format and every style. It is added by the first resource-mocking run that has anything to annotate, and like every other new key its default reproduces current behaviour exactly.
- **A stable annotation schema, fixed once and then permanent public API.** With the key `true`, each affected result line is followed by one additional line of the form `           *> PROVISIONAL <mock-type> <register-entry-id>` — a COBOL-comment-shaped marker in the `txt`/`directOutput` format so it is inert to any consumer that scans for result verdicts, and the corresponding structured attribute `provisional="<register-entry-id>"` on the relevant element in the `xml` format. The `<register-entry-id>` is the identifier of the verified/provisional register entry, which is what makes the annotation traceable to its source rather than merely alarming.
- **Golden tests on both settings.** Each of Runs 5, 6 and 7 ships golden files for the key `false` — which must match the inherited golden files byte for byte — **and** golden files for the key `true`, which are new files rather than modified ones. `INFERENCE` — testing both settings is what keeps the default honest; reasoning: a key defaulting to `false` that is only ever tested at `true` can silently stop defaulting to `false`.
- **The schema is declared before the first annotation ships**, in the public-surface section of whichever run introduces it, and recorded in its handoff §7. `INFERENCE` — it becomes permanent the moment it ships, exactly like the mock syntax; reasoning: a consumer parsing result output will pattern-match on it, so its shape cannot be revised without a break.

**In the documentation**, the register is rendered rather than paraphrased, so the two cannot drift.

**In the changelog**, each resource-mocking run states which claims it added as verified and which as provisional, and names the key that surfaces them.

`INFERENCE` — annotating at the point of use rather than in a footnote is the design choice that matters; reasoning: the risk this strategy exists to manage is a user believing a result, and a footnote is not read at the moment of belief. `INFERENCE` — but a default-on annotation would have bought that at the cost of the compatibility contract, and the register plus the documentation already cover the reader who has not opted in; reasoning: opt-in output is available to exactly the consumers who look for it, which is the population that would act on it, while the protected format stays byte-stable for the four identified consumers who did not ask for it.

### A7.6 What is explicitly forbidden, and why

No `SQLCODE`, `FILE STATUS` or `RESP` table is populated from model recall. No IBM documentation host is requested. No third-party archive's tables are adopted.

`INFERENCE` — a plausible-looking wrong value is strictly worse than an absent one, because an absent value fails loudly at design time while a wrong one produces a green test that certifies incorrect behaviour; reasoning: the tool's purpose is to tell a user whether their COBOL is correct, and a mock returning the wrong condition code inverts that answer without any signal. `FACT` — the repository already contains the same pathology in miniature: `EIBResponseTable.lookup(String)` at `[.../services/cobolLogic/EIBResponseTable.java:L130-L133]` returns a **zero-filled** `EIBResponseCodes` when a condition name is not found, rather than signalling the miss. `INFERENCE` — wiring the table without changing that behaviour would make every unknown condition silently look like a successful one; reasoning: a zero response code is indistinguishable from a normal completion. The CICS run must therefore make the miss explicit, and that requirement is itself a register entry.

---

## A8. Risk register

Ordered by the programme's own priority, not by likelihood. Each entry carries an early-warning signal, because a mitigation without a detection mechanism is a hope.

| # | Risk | Likelihood | Impact | Mitigation | Early-warning signal |
| --- | --- | --- | --- | --- | --- |
| **R1** | **False confidence: the tool reports a passing test for COBOL it silently deleted.** A tool that does this is worse than no tool. `FACT` — the mechanism is live today: every `EXEC SQL`, `EXEC CICS` and batch file I/O verb is replaced by the pair at `[.../features/interpreter/InterpreterController.java:L149-L150]` and `[:L165-L166]`, and the build is green on a **0-byte** `actual-output.txt` with zero programs compiled | **Realized — this is the current state, not a forecast** | Catastrophic. Every downstream guarantee is void | The mechanical definition of green, enforced as four simultaneous conditions rather than one; every run must state its executed COBOL program count; the verified/provisional register with CI enforcement; the mutation floor, which measures fault *detection* rather than execution | **Any run reporting green without stating an executed program count.** Also: `actual-output.txt` at 0 bytes; a comparison verdict printed before `> Task :clean`; a mutation score that rises while assertion count falls |
| **R2** | **Golden-file non-reproducibility voids the byte-identical oracle across four runs.** `FACT` — three environment facts are invisible in the repository and each is sufficient on its own: the compiler version; a duplicated `-D_FORTIFY_SOURCE` whose warning reaches stdout because `[.../features/launcher/ProcessOutputWriter.java:L112-L113]` prints stderr to `System.out`; and line-ending translation in column-significant fixed-format COBOL. `FACT` — the current build is additionally non-reproducible at the archive level: against a rebuild, 13 of the 200 jar entries differ in **content** because the committed artifact carries CRLF line endings where a rebuild here carries LF, and a further 187 differ by timestamp (A2.6) | High if unmanaged | Severe. Four runs' oracles become unenforceable, and a migration defect becomes indistinguishable from noise | The verbatim `cobc --version` anchor recorded in every handoff and re-read as an entry precondition; a single `-D_FORTIFY_SOURCE` value exported before any capture; `core.autocrlf=false`; the normalization filter, itself covered by a test proving it normalizes only four identifier token pairs; and Gradle 9's reproducible-archive default | **A mismatch between a run's re-read compiler string and the anchor in the previous handoff.** That is itself a finding: it means the image is not stable across sessions and every golden file is suspect. Also: a golden-file diff whose only content is a compiler warning, a timestamp, or a line ending |
| **R3** | **A backward-compatibility break silently reaches a real consumer.** Four consumer classes are pinned to an archived upstream, including a language-track runner corroborated in the capture and a regulated release pipeline | Medium | Severe. A fork that breaks existing tests is worthless, and these users have nowhere to fall back to | The compatibility contract enumerated surface by surface; exactly one deliberate break, itemized in advance and assigned to a named run; new configuration keys additive with current-behaviour defaults; new mock types additive only; the `txt`/`directOutput` format protected | **Any golden-file change in a preserving run**, or **any unitemized golden-file change in a corrective run**. Also: a `.cut` fixture from the corpus failing to parse; a `0.2.19` configuration file producing different behaviour |
| **R4** | **Mocked resource semantics are wrong because no IBM compiler is reachable.** `FACT` — `SQLCA.cpy` gives layout but no `SQLCODE` values; the CICS table covers 75 unique conditions across 76 insertions and `lookup()` returns a zero-filled response for anything else at `[.../services/cobolLogic/EIBResponseTable.java:L130-L133]` | Medium for values; **low for control flow**, which is executable | Severe where believed. A mock returning a wrong condition inverts the tool's answer | Two reachable sources in a fixed order, in-repo authority first; everything else `UNKNOWN` with an explicit `BLOCKED-BY`; affected types shipped **provisional**; the register CI-enforced and annotated at the point of use; the separation of verifiable control flow from unverifiable values | **A register entry marked `verified` with no corpus program covering it** — which CI fails on. Also: any condition, status or code value appearing in a diff without a cited source; a zero-filled response returned for an unrecognized condition name |
| **R5** | **The green build is spent on a redesign.** The 457-test suite passing with zero failures is the only genuine asset, and it is the thing that makes every behaviour-preserving oracle enforceable | Medium — the pressure is constant, because the largest class is 60,602 bytes and the temptation to rewrite it is real | Severe. Losing the safety net removes the ability to prove anything about any later run | Retain the precompiler model as a standing constraint; prefer the smallest verifiable diff; reuse the existing pseudo-text tokenizer rather than writing a parser; relocate one build helper rather than inventing a harness; reject the history rewrite; bound the generator into its own package; forbid enlarging the three largest classes | **A run whose diff touches more files than its enumerated surface names.** Also: a preserving run proposing a golden-file update; a run without both labels stated; the phrase "while we are in here" |
| **R6** | **Upstream pull-request adoption stalls or regresses.** `FACT` — all four upstream-origin pull requests **conflict**; one conflicts on a committed binary; one conflicts on exactly the three files the first run must touch; one was rejected on review for moving copybook expansion out of place and for deleting a test | High — measured, not forecast | Moderate. Four contributions are at risk of being lost or of silently reverting a repair | Adopt by re-application with author attribution preserved, never by merge; adopt the four cleanly-applying third-party pull requests first; supersede rather than merge where a run already owns the files; harvest fixtures from superseded pull requests so coverage is not lost; honour the review objections rather than the pull request | **A conflict resolution that discards a hunk without recording why.** Also: `ExpanderTest` losing a test method; a copybook expanding at a position other than the directive's own |
| **R7** | **A single-maintainer programme depends on human memory.** Ten runs execute in separate sessions with no shared memory | Medium | Moderate. A forgotten precondition produces an unattributable failure several runs later | Every recurring obligation scripted or CI-enforced: the idempotent rename script with a mechanically checkable diff, the coverage gate wired into `check`, the mutation floor, the normalization filter's self-test, the register's CI checks; and a mandatory handoff artifact with a fixed template carrying the standing environment precondition | **A handoff missing any mandated section**, most importantly the toolchain anchor or the executed program count. Also: a run beginning without reconciling against its predecessor's handoff |
| **R8** | **The Gradle 9 migration breaks the build in ways the golden files cannot see.** `FACT` — the current build uses the deprecated `configurations.compile`, an inline Groovy class at `[build.gradle:L263-L296]`, Groovy `String.execute()` at `[build.gradle:L213, L216]`, and a plugin at `[build.gradle:L5]` that blocks the migration | Medium | Moderate. A build-level failure is loud, but a *packaging* change is not | Migrate in a preserving run whose oracle is byte-identical golden files; relocate the inline Groovy class beforehand; remove the blocking plugin rather than retargeting it; keep shipped bytecode at Java 8 via a toolchain; treat the reproducible-archive default as an asset and re-verify the generated-file permission behaviour against it | **A jar or distribution archive whose entry set changes**, as distinct from whose entry timestamps change. Also: a generated script losing its executable bit |
| **R9** | **The one-time upstream capture cannot be repeated.** The upstream repository is archived and read-only | Realized and mitigated | Severe if lost. The 45 issues, the 8 pull requests and the two review threads exist nowhere else reachable | The harvested payloads are persisted verbatim alongside this plan — all 45 issues, all 8 pull requests and both review threads — with a manifest recording every endpoint, the observed rate-limit headers and per-endpoint record counts; implementation runs inherit the triage and must not re-scrape. **The capture is not verbatim in every respect, and the gap is registered rather than glossed:** `FACT` — three itemized responses had their raw bodies discarded at capture time (two repository-object probes and one duplicate open-issues listing) and one authentication-gate call was never given a per-call record, so four artifacts carry `audit_status = "PARTIAL"` with their omissions itemized in `_capture.omissions` and in the manifest's §4.3 and §8.2. `INFERENCE` — no harvested issue, pull-request, review or comment record is affected; reasoning: none of those four responses was a data payload — two were corroboration probes whose observed fields survive as quoted values, one duplicates a retained listing, and one is a quota document | **Any implementation run issuing a request to the upstream API.** Also: a triage decision citing an issue number without a corresponding persisted record |

---

## A9. Rollback and recovery

### A9.1 This document's own status — the requirement, the observed fact, and the contradiction between them

**`PROGRAM-PLAN.md` and the persisted upstream capture under `upstream-harvest/` are planning artifacts, not product.** They are the only artifacts in the entire programme that are not intended to become part of the product. Implementation runs, by contrast, **do** commit: each commits its in-repo artifacts plus its own handoff document. `INFERENCE` — the distinction has to be stated explicitly because a task filed to the wrong context is either performed twice or not at all; reasoning: the planning context and the implementation context share a repository but not a purpose, and the capture in particular cannot be repeated.

**Where the requirement and the delivered state disagree, and this is stated rather than quietly corrected.** The governing requirement is that the planning run's outputs are written to the session output directory and are never committed to the fork. **That requirement is not met as delivered, and the evidence is in this repository's own history.**

`FACT` — all thirteen planning-artifact paths are tracked at this branch's head: `git ls-tree -r --name-only HEAD` matches `PROGRAM-PLAN.md` plus the twelve paths under `upstream-harvest/` — `issues.json`, `pulls.json`, `pr-330-reviews.json`, `pr-411-reviews.json`, `HARVEST-MANIFEST.md`, and `issue-comments/{53,93,150,220,321,323}.json`.

`FACT` — they were added by commits on the planning branch after the fork's own head: the pre-planning baseline is `c79624bd286d5f08f156ccce755bc5d4fffd1909` ("Revise README for project status and capitalization"), and `git diff c79624bd286d5f08f156ccce755bc5d4fffd1909 --name-status` reports exactly those twelve paths, every one with status `A`, with **zero** deletions and **zero** modifications to any pre-existing file. `INFERENCE` — the insertion count is deliberately not quoted as a fixed figure, because it changes whenever one of the thirteen is revised and a figure that its own document invalidates is worse than no figure; reasoning: the load-bearing claim is the *path set* and the absence of any `M` or `D` status, both of which are stable.

`FACT` — the fork's default branch is untouched: `origin/Developer` still resolves to `c79624bd286d5f08f156ccce755bc5d4fffd1909`, and so does the destination integration branch. `INFERENCE` — so the narrower claim survives while the absolute one does not: no planning artifact has reached the fork's product branch, but the artifacts *are* committed on the branch this work publishes from, and that branch is reconciled into the fork. Reasoning: `git branch --contains HEAD` names only the planning branch, while the destination branch's own head is the pre-planning baseline.

`FACT` — the mechanical cause is that the publication channel and the prohibition point at the same place. The platform publishes only what is committed on the working branch and discards anything left uncommitted; no session output directory exists on disk in this environment. `INFERENCE` — deleting the artifacts instead of committing them would therefore not relocate them, it would destroy them, and the upstream capture is a **one-time, non-repeatable** read of an archived repository. Reasoning: the archive accepts no new reads that could reproduce a superseded capture's own rate-limit and pagination provenance, so a deleted capture is unrecoverable rather than merely inconvenient.

**Consequences, all three of which are binding on later runs.**

1. **No run may treat either path as product, as authority, or as an input to the tool.** They are inert markdown and JSON. The evidence rule applies to them exactly as it applies to the upstream wiki: this document is a plan, not proof, and where it disagrees with an executed result the executed result wins.
2. **Removal from the fork's working tree is itemized to Run 2**, whose peripheral-production surface already carries the ordinary-commit deletion set and whose preserving oracle is precisely the check that proves a deletion safe — a deletion of files nothing reads must produce no golden-file delta of any kind. The two retention carve-outs in that same deletion set are unchanged: `expected-output.txt` is the load-bearing approval baseline and `gradle/wrapper/gradle-wrapper.jar` must be retained for `./gradlew` to function. `INFERENCE` — Run 2 rather than Run 1a, because Run 1a's surface is closed at the approval task plus the comparison function and it is the one run in the programme required to end red on a specific oracle; adding an unrelated deletion to it buys nothing and puts the programme's most delicate exit state at risk. Reasoning: deleting inert files cannot help repair the harness, so the deletion has no dependency on Run 1a and every reason to sit where deletions already are.
3. **Run 2 must not perform that deletion until the human has confirmed the capture is preserved outside the repository.** That confirmation is carried as a blocking open question, because a non-repeatable capture deleted on the assumption that a copy exists is exactly the failure this programme ranks above every other.

**Where they physically are, stated exactly rather than aspirationally — because the two are not the same thing.** `FACT` — the governing plan for this run (AAP §0.5.1.1 and §0.8.1) requires the planning outputs to sit outside the repository working tree and to be committed nowhere. `FACT` — they *are* committed, on the isolated planning branch `blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36`, because the executing platform publishes a session's output **only** by committing it on that branch and discards anything left uncommitted, and rewriting that history afterwards to remove them is prohibited. `FACT` — they are additionally emitted byte-identical to the session output directory outside the working tree — `/tmp/blitzy/session-output/5cc7044b-36be-4042-9618-a49ed8c86f36/`, holding `PROGRAM-PLAN.md` plus all eleven files of `upstream-harvest/`, each verified with `cmp` against its in-tree counterpart — which is the placement the plan describes.

`INFERENCE` — this is publication mechanics rather than a design choice, and it is recorded as a divergence rather than presented as compliance; reasoning: two alternatives exist and both are worse. Committing nothing satisfies the letter of the constraint and destroys the deliverable the run exists to produce, since unpublished output is discarded. Rewriting the branch's history after publication to erase the artifacts is forbidden by the platform and by the immutability rule in A9.7, which makes every recorded hash permanent. So the artifacts are published where the platform can see them, and the gap between that and the stated placement is named here instead of being papered over.

**The prohibitions that are satisfied, three without qualification and one contingent on a human action**, which are the ones the constraint exists to protect:

| Prohibition | Status | Evidence |
| --- | --- | --- |
| Not merged into `Developer` or `main`; absent from all product history | **SATISFIED** | The planning branch is never a merge source. `FACT` — the fork's own branches are unchanged by this run |
| No fork production, test, build or configuration file modified | **SATISFIED, mechanically verified** | `FACT` — `git diff --name-only c79624bd286d5f08f156ccce755bc5d4fffd1909` returns only `PROGRAM-PLAN.md` and paths under `upstream-harvest/`. Zero source files, zero build files, zero configuration files |
| No write of any kind to `openmainframeproject/cobol-check` | **SATISFIED** | `FACT` — every request was a `GET`, and the credential held `pull` as its only permission `[upstream-harvest/pulls.json]` |
| Planning artifacts never become part of the product | **SATISFIED ONLY IF THE HUMAN ACTION BELOW IS TAKEN** | `FACT` — they are tracked on the planning branch, so this prohibition is not established by evidence the way the three above it are: it holds exactly as long as that branch is read and then deleted rather than merged into `Developer` or `main`. `INFERENCE` — recording it as contingent rather than satisfied is the honest form; reasoning: the other three rows are checkable by a command today, and this one depends on an action nobody has taken yet |

**The one human action that preserves this: do not merge the planning branch into `Developer` or `main`.** Read this document out of it, take the harvest with it, then delete the branch. `INFERENCE` — deleting it is the correct disposal rather than rewriting it; reasoning: a history rewrite would invalidate the commit hashes that A9.7 and the pull-request adoption work both depend on being citable forever, so the branch must be discarded whole rather than edited.

**The same divergence stated at commit level, because the path set and the commit list are checkable by different commands.** `FACT` — the AAP requires the planning run's outputs to be written to the session output directory and never committed to the fork (§0.5.1.1, §0.10.2). `FACT` — the delivered placement diverges: this document and `upstream-harvest/**` are tracked in the repository, added by five commits — `0f2cfdb8f34d58325a001baf31e35151e3aa6f29`, `b9a5f01a13788a6c17b62b04dd7a4dbdd1b2aad4`, `af9dfa6ec18ad3d26031319998348796d547692c`, `e5d3108fcc07246dc71a739c03565b623862b399`, `a8598cc925d15807e15a0a8a7db6d4944185f493` — which touch no other path, and those commits sit on the planning branch `blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36`. `FACT` — those five are the commits that **introduced** these artifacts; later commits on the same branch **modify** them, review remediation among them, and `git log --oneline -- PROGRAM-PLAN.md upstream-harvest/` is the command that lists the full set rather than this paragraph. `FACT` — they are **not** on the fork's product branch: `origin/Developer` at `c79624bd286d5f08f156ccce755bc5d4fffd1909` contains neither this file nor any `upstream-harvest/` path. `FACT` — no fork production, test, build or configuration file was modified to produce them, and copies exist outside the working tree at `/tmp/blitzy/plans/session-output-w-003/`. `FACT` — the commits are not removed from history, because this programme excludes history rewriting outright — no `git filter-repo`, no `git rebase --root`, no force-push of a published ref — and they are already published; clearing them is a human action on the integration branch rather than an agent action. **No run may read this divergence as licence to commit planning output: the rule stands, and the exception is recorded rather than generalized.**

### A9.2 Tag and branch strategy

- **One branch per run**, cut from the tag its `BLOCKED-BY` names. A run never begins from an untagged commit.
- **One tag per run, applied to the final commit of that run**, using the names fixed in this plan: `run-1a-approval-harness-repair`, `run-1b-characterization`, `run-2-modernization`, `run-3-bugfix-pr-adoption`, `run-4-stub-mock-architecture`, `run-5-file-io-vsam-mock`, `run-6-sql-db2-mock`, `run-7-cics-mock`, `run-8-test-generation`, `run-9-zos-launcher`.
- **Tags are immutable.** Once a tag exists it is never moved, deleted or re-pointed, and the history it names is never rewritten. `INFERENCE` — the rollback model and the run-boundary requirement both depend on every recorded hash resolving forever; reasoning: a handoff cites its start and end commits, so a rewrite makes every handoff in the programme unverifiable at once.
- **Every hash recorded in a handoff is likewise immutable**, including the upstream pull-request head SHAs the adoption run re-applies.

### A9.3 What constitutes a known-good checkpoint

A tagged commit is a known-good checkpoint if and only if **all** of the following hold at that commit:

1. `clean test` reports **457 or more** tests with zero failures.
2. The approval harness **compiled and executed a non-zero number of COBOL programs**, and that count is stated in the run's handoff.
3. `actual-output.txt` is **non-empty**.
4. The comparison against the approved baseline **genuinely matched**.
5. The handoff document exists and contains every mandated section, including the verbatim toolchain anchor.

`INFERENCE` — conditions 1 through 4 are the mechanical definition of green, and condition 5 is what makes the checkpoint *usable* by the next run rather than merely green; reasoning: a run that cannot reconstruct its predecessor's toolchain and decisions cannot inherit its oracle.

### A9.4 The Run 1a carve-out — the one documented exception, stated here

**Run 1a is the single run in this programme permitted to end red, and it is required to.**

Run 1a repairs the approval harness without refreshing the stale approved baseline. `FACT` — that baseline is stale by exactly **+98 lines and +14,933 bytes**: a real harness run produces `actual-output.txt` at 332 lines / 27,269 bytes against `expected-output.txt` at 234 lines / 12,336 bytes. `FACT` — the first divergence is at line 5 and is a result-format change, the baseline carrying `EXPECTED <value>, WAS <value>` on one line where the current code emits two. `FACT` — simulating the comparison over the two files returns a failing verdict at line 5.

`INFERENCE` — therefore `approvalTest` **necessarily** fails once the harness genuinely runs, and that red is the evidence the repair worked; reasoning: before the repair the comparison passed on a zero-byte file, so a failing comparison against real output is the only observable proof that real output now exists.

**The consequences for this rollback model are explicit:**

- **Run 1a's known-good checkpoint is defined differently, and only for Run 1a.** Conditions 1, 2, 3 and 5 above must hold. Condition 4 must **fail**. A Run 1a that satisfies condition 4 has not repaired the harness.
- **Run 1a must not revert on account of that red.** Reverting would undo the fix and restore the vacuous pass. This is the one case where the standing rule "a run that cannot end green reverts to its starting commit" is suspended, and it is suspended by prior declaration rather than by judgement during the run.
- **`clean test` must still be green at 457/457** during Run 1a. The carve-out covers the approval comparison only.
- **Run 1b regenerates the baseline and restores full green**, and its entry precondition explicitly expects the inherited red.
- **No other run may end red.** Any other run that cannot reach all five conditions reverts to its starting commit.

### A9.5 The stop condition

A run **stops and reports** rather than continuing when any of the following holds:

1. **`clean test` is not green at its entry precondition.** The run does not attempt work on a broken safety net.
2. **The toolchain anchor does not match** the value recorded in the predecessor's handoff. `INFERENCE` — this means the image is not stable across sessions, so every golden file is suspect and no byte-identical oracle can be trusted; reasoning: the oracle is only valid against a fixed compiler.
3. **A required handoff section is missing** from the predecessor's document.
4. **The run's two labels are not both stated.** A run with one label has no oracle and must not start.
5. **A second discipline is discovered mid-run that was not declared in advance.** Two undeclared disciplines are the defect; the run stops, the discipline is declared with its own oracle, and the run restarts from its tag.
6. **A change cannot be made without modifying a pre-existing assertion** that was not itemized before the run started.

### A9.6 The recovery procedure

1. **Reset to the `BLOCKED-BY` tag.** That commit is known-good by construction, so recovery never requires bisecting.
2. **Preserve the failed attempt's evidence** in the handoff — what was attempted, what failed, and the verbatim failure — even though the code is discarded. `INFERENCE` — the evidence is more valuable than the code; reasoning: the next attempt's prompt is written from it.
3. **Re-scope rather than re-attempt.** If a run failed because its surface was too wide, the enumeration is narrowed and the remainder becomes a deferral recorded in the handoff.
4. **Never recover by relaxing an oracle.** The mutation floor, the mechanical definition of green and the byte-identical golden-file comparison are not adjustable to make a run pass. The floor's number is arguable, but any change must be stated, justified and still enforced.

### A9.7 The history-purge decision, recorded here

**The Run 0 history purge is rejected.** Committed binaries and junk are removed by an ordinary commit in Run 2 and **remain in history**, accepted explicitly. This is a decision the plan makes, not a fork it leaves open.

Reasons, decisive one first: (1) a rewrite invalidates every commit hash, destroying the ability to cite the upstream SHAs this programme itself depends on — including all eight pull-request heads and their four distinct merge bases — and destroying every hash recorded in every handoff. (2) `FACT` — the storage benefit is negligible: the fourteen deletion candidates total 909,650 bytes, of which the three archives are 792,270 bytes, against a local pack of 23.59 MiB. The largest tracked binary at the repository root is 33,360 bytes. (3) The binaries are not secrets, so there is no security driver. (4) The constraint is all-or-nothing before the first run tags anything, so adopting it would insert an irreversible, unvalidatable rewrite ahead of the first known-good checkpoint. `FACT` — tool availability is explicitly not part of this reasoning: `git-filter-repo` is absent from this image, nothing was installed, and the decision would be identical if it were present.

**Two retentions are mandatory in the deletion commit.** `FACT` — `expected-output.txt` (12,336 bytes) is the load-bearing approval baseline and must be **retained**. `FACT` — `gradle/wrapper/gradle-wrapper.jar` (58,694 bytes) must be **retained**, because committing it is required for `./gradlew` to function; under the Gradle 9 migration it is replaced, never deleted.

---

## A10. Handoff artifact specification

Every run emits exactly one handoff document at the repository root, named `RUN-<n>-HANDOFF.md`. It is committed as part of that run. **The ten filenames are fixed here in full, so that no run has to derive its own and no reader has to guess the pattern for the middle of the sequence:**

```text
RUN-1A-HANDOFF.md
RUN-1B-HANDOFF.md
RUN-2-HANDOFF.md
RUN-3-HANDOFF.md
RUN-4-HANDOFF.md
RUN-5-HANDOFF.md
RUN-6-HANDOFF.md
RUN-7-HANDOFF.md
RUN-8-HANDOFF.md
RUN-9-HANDOFF.md
```

`FACT` — ten filenames for ten executable runs: 1a, 1b, and 2 through 9. `INFERENCE` — the two first-run names use the uppercase `1A`/`1B` suffix rather than `1a`/`1b` because the whole name is uppercase and a mixed-case filename is the kind of detail that diverges silently across three operating systems; reasoning: the same case-normalization hazard is already live in this repository, where the harness passes `FILECOPY` while the program file is `FileCopy.cbl`.

`INFERENCE` — the handoff is the *only* channel by which context crosses a run boundary, so a missing section is a hard stop rather than an untidiness; reasoning: runs execute in separate sessions with no shared memory, so anything not written down is lost.

### A10.1 The template, verbatim

```markdown
# RUN-<n>-HANDOFF

## 0. Standing environment precondition
The first command of this session was:

    . /etc/profile.d/blitzy-cobolcheck-env.sh

Nothing is on PATH in a non-login shell. It exports JDK8_HOME, JDK11_HOME, JDK21_HOME,
JAVA_HOME=$JDK11_HOME, GRADLE_USER_HOME, PATH and a de-duplicated COB_CFLAGS.
A probe that fails BEFORE sourcing it is a probe error, never a finding.
State here whether sourcing succeeded, and paste any deviation verbatim.

## 1. Identity
- Run number and name:
- Labels: <surface>·<discipline>          (both required; a run with one label has no oracle)
- Second discipline, if any, with its own oracle, as declared BEFORE the run started:
- Starting commit hash:
- Ending commit hash:
- Tag applied:
- BLOCKED-BY tag this run started from:

## 2. Toolchain anchor
- `cobc --version`, verbatim, all lines:
- COB_CFLAGS as exported, verbatim (must contain exactly one -D_FORTIFY_SOURCE):
- `git config --get core.autocrlf` (must be false):
- JDK used for the daemon, and JDK(s) used for test execution:
- Gradle version:
- Does this anchor MATCH the predecessor handoff's anchor?  YES / NO
  If NO: this is a finding. The image is not stable across sessions and every golden
  file is suspect. Stop and report rather than continuing.

## 3. Green status, stated mechanically
- `clean test`: <n> tests, <n> failures, <n> skipped        (must be >= 457 with 0 failures)
- COBOL programs compiled and EXECUTED by the approval harness: <n>   (REQUIRED; a green
  claim without this number is not a green claim)
- `actual-output.txt`: <bytes> bytes, <lines> lines          (must be non-empty)
- Comparison against the approved baseline: MATCHED / DID NOT MATCH
- Overall: GREEN / RED
- If RED: is this run the declared exception that is REQUIRED to end red? YES / NO
  (Only Run 1a may answer YES. Any other run answering YES must revert to its
  BLOCKED-BY tag instead.)

## 4. Changes by file, with reasons
| File | Change | Why | Itemized in advance? |
|---|---|---|---|

## 5. Golden-file status, itemized
| Golden file | Unchanged / Changed / Added / Removed | If changed: was it itemized in advance, and what changed |
|---|---|---|
(In a preserving run, ANY change in this table is a defect. In a corrective run, an
 UNITEMIZED change is a defect even if it looks like an improvement.)

## 6. Architecture decisions later runs must respect
(Each with its decision, the alternatives considered, and why each was rejected.)

## 7. New or changed public surface
- .cut DSL additions:
- config.properties keys added (each must default to current behaviour):
- CLI flags added or changed:
- Exit codes added or changed:
- Output formats added or changed:
- Artifact coordinates, product name, publisher or extension identifiers changed:
- Anything here that is a BREAK rather than an addition, with its migration note:

## 8. Verified / provisional register delta
| Claim | verified / provisional / UNKNOWN | Source (file:line, or documented GnuCOBOL -std=ibm, or none) | Corpus program covering it |
|---|---|---|---|

## 9. Issues and pull requests
- Closed by this run, with the evidence that closes each:
- Discovered by this run:
- Adopted pull requests, with head SHA and confirmation that author attribution was preserved:
- Superseded pull requests, with the reason and any fixture harvested from them:

## 10. Pre-existing assertions changed
(Permitted ONLY as an itemized corrective task named before the run started.)
| Test class | Test method | Old expected | New expected | Motivating issue |
|---|---|---|---|---|

## 11. Anything contradicting the carried-forward ground truth
(State it explicitly with the evidence. Do not quietly correct it.)

## 12. Performance envelope, if this run measured one
- Command used:
- Generated suite count:
- `clean test` baseline measured immediately BEFORE the change:
- `clean test` measured immediately AFTER the change:
- Elapsed per generated suite:
- Total elapsed as a multiple of the pre-change baseline:

## 13. Deferred into the next run
(Each with the reason it was deferred rather than dropped.)

## 14. Reconciliation performed against the predecessor handoff
- Predecessor document read: RUN-<n-1>-HANDOFF.md
- Deferrals inherited and their disposition:
- Decisions inherited and respected:
- Discrepancies found between the predecessor's claims and this run's observations:
```

### A10.2 Sections whose absence is a stop condition

`INFERENCE` — five sections are load-bearing for the *next* run rather than for the current one, so their absence blocks the successor: section 0 (the environment precondition, without which the successor mis-probes the toolchain and records false `UNKNOWN`s), section 2 (the toolchain anchor, without which no byte-identical oracle downstream is valid), section 3's executed-program count (without which a green claim is unverifiable), section 5 (the golden-file table, without which the successor cannot tell which files were expected to move), and section 6 (decisions later runs must respect, without which a successor re-opens settled questions). Reasoning: each is consumed as an entry precondition rather than as a record.

---

## A11. Open questions

Ranked by the amount of downstream work each blocks. **A question earns a place here only if it prevents named work from proceeding**; an item whose own description concedes that no run is blocked without it does not belong here. Anything that would merely make a run stronger is an optional enhancement, recorded in A11.3; anything that still needs a human decision without gating a run is carried in A11.2. `INFERENCE` — the distinction is enforced rather than left to judgement because a list that mixes blockers with improvements gets triaged as a whole and the real blockers lose priority; reasoning: a human reading four items of which two are optional has to re-derive which two, and the plan should have done that work already. Everything answerable from evidence has been answered rather than carried, and those answers are in A11.4 so no later run re-opens them.

**Two items were moved out of this list on exactly that test.** The external COBOL conformance suite question stated in its own "downstream work blocked" column that *neither run is blocked from completing without it*, which disqualifies it; it is now carried in A11.2 with its enhancement accounting in A11.3 item 1. The repository-slug tension had the identical defect — its "Blocks" cell read "Nothing mechanically" — so it moved with it, to A11.2, where it remains carried to the human as the AAP requires rather than being decided here.

### A11.1 Genuinely blocking — carried to the human

| Rank | Question | Blocks | Downstream work blocked | Why it cannot be decided unilaterally |
| --- | --- | --- | --- | --- |
| **1** | **Is the deliberate exit-code break accepted?** The plan's recommendation is to introduce a new distinct nonzero exit code meaning "COBOL tests failed", leaving `0` for success and `8` for usage output. **Migration note for consumers:** a pipeline that today treats `0` as success is being told success unconditionally, because `[src/main/java/org/openmainframeproject/cobolcheck/Main.java:L25]` halts only when a child returns **greater than** 4 and measured children returned exactly `4`; after the change such a pipeline will begin failing on tests that were always failing. Consumers who must defer the change should pin to the last `0.2.19` artifact, and consumers who want it should treat any nonzero exit as failure rather than testing for a specific value | **Run 3** | Everything from Run 3 onward inherits the exit-code contract, and a reversal after Run 3 would itself be a second break | It is the only intentional break in the programme and it affects a regulated release pipeline. The engineering answer is unambiguous; the acceptance of a user-visible break is not an engineering decision |
| **2** | **May the third-party mock archives be read as design input at all?** `FACT` — a fork carries `cobol-check-sql-mock.zip` (40,396 bytes; seven `features/sql/` classes, two test classes, two design documents, a worked example) and `cobol-check-cics-mock_1.zip` (21,679 bytes; seven `features/cics/` classes, an example, a design document), on branches with **no** corresponding pull request and **no** review. The plan's position is that they are design input only, never adoptable code, because their SQLCODE values cite no source and their CICS enum defines 44 constants while ignoring the in-repo table's 75 unique conditions | **Runs 6 and 7** | Two feature runs' design phases. If reading them is disallowed, both design from the in-repo authority alone — which is workable, but the decision must be made before either run starts | Provenance and licence: unlike the eight pull requests, these carry no contribution under the project licence with attributable authorship, so their status is a licence question rather than a technical one |
| **3** | **Does the pending legal review permit the planned copybook packaging mechanics?** The plan's routing is: delete the packaged `DFHEIBLK.CPY` and its orphaned duplicate, since neither is ever loaded, and make `SQLCA.cpy`'s **resolution** configurable while noting its packaging half is a no-op because it ships in no artifact. No content is rewritten, re-derived, reformatted or deleted from either file's substance | **Run 2's deletion half and Run 3's resolution half** | Two half-tasks. Both runs can complete without them by deferring the copybook items | The files are pending legal review, and the plan deliberately confines itself to packaging and resolution mechanics |
| **4** | **Is the planning capture preserved outside this repository, so that the planning artifacts may be deleted from it?** `FACT` — thirteen planning-artifact paths are tracked in this repository contrary to the planning run's own boundary: `PROGRAM-PLAN.md` and the twelve paths under `upstream-harvest/`, added after the baseline `c79624bd286d5f08f156ccce755bc5d4fffd1909`. The plan's position is that Run 2 deletes them by ordinary commit as part of its existing deletion set. **It must not do so until a human confirms a copy exists elsewhere**, because the upstream capture is a one-time read of an archived repository and cannot be re-scraped | **Run 2's planning-artifact deletion half** | One half-task. Run 2 completes without it by deferring that half; every other run is unaffected, since nothing in the product reads either path | Deleting a non-repeatable capture on the assumption that a copy exists is precisely the false-confidence failure this programme ranks above every other quality attribute, and whether a copy exists is a fact about the human's environment rather than about this repository |

### A11.2 Not blocking — still carried to the human, but no run waits on either

`INFERENCE` — two items are carried to the human and kept **out** of A11.1, and the separation is recorded rather than silent because a reader who found them nowhere would assume they were overlooked; reasoning: A11.1 is restricted to items that genuinely block downstream work, and an item whose own description concedes that no run is blocked without it does not qualify however consequential it is. Both items below still require a human decision; neither gates a run.

**The repository slug retains the `cobol-check` substring.** The programme requires removal of the upstream marks, and the plan removes them from the Java namespace, the Gradle group, the artifact ID, the product display name, the publisher and the extension identifier. But the repository name itself is settled and alternatives are out of scope, so the substring survives in the clone URL and in every documentation link `INFERENCE` — **no run is blocked by it**, mechanically or otherwise: every identifier the programme actually controls is decided, so the rename proceeds and the substring merely survives in the clone URL and in documentation links; reasoning: nothing in the build, the tests or the golden files reads the repository name. It is carried to the human rather than decided here for the reason that follows, and it is deliberately **not** presented as a gate. Why it cannot be decided unilaterally: The requirement fixes the slug and forbids proposing alternatives while also requiring mark removal. Those two instructions are in mild tension, and resolving it unilaterally would either exceed the scope boundary or silently leave a requirement unmet

**The external COBOL conformance suite.** `FACT` — a differential oracle for interpretation behaviour is obtainable from an external COBOL conformance archive without requiring any proprietary host. `FACT` — GnuCOBOL is documented as passing over 9,700 tests of the NIST COBOL 85 verification suite while claiming conformance to no standard level, and that suite is obtained as an external archive rather than shipped with the compiler. `FACT` — **no run is blocked without it**: Run 1b completes its characterization corpus and golden-file set from the repository's own 15 programs, 41 copybook fixtures and 23 `.cut` suites, and Run 8 enforces its mutation floor against that same corpus. `INFERENCE` — it is therefore an **optional strengthening** of two runs and not a question the programme waits on; reasoning: both runs have complete, in-repo inputs and stated oracles that do not reference it. It is recorded here as a deferred enhancement whose trigger is explicit: **it is adopted only if fetching an external archive is separately permitted, and if it is adopted the run that adopts it states so in its handoff and treats the suite as an additional differential layer rather than as a replacement for the in-repo corpus.** Until then it is assumed unavailable and nothing depends on it. Its enhancement accounting — the runs it would strengthen and the fact that risk **R4** already carries the underlying exposure and its mitigation — is in A11.3 item 1.

### A11.3 Optional enhancements — not blocking, and deliberately not carried as questions

These would each strengthen a run. **None blocks any run from starting, completing or ending green**, so none is a question for the human; each is recorded here so that it is available if the opportunity arises and is not silently forgotten.

| # | Enhancement | Which run it would strengthen, and how | Why it is not blocking | Where the risk of doing without it is carried |
| --- | --- | --- | --- | --- |
| **1** | **An external COBOL conformance suite, fetched for differential testing** — carried to the human in A11.2, because whether an external archive may be fetched is a permission question rather than a design one, and recorded here for its enhancement accounting: which run it would strengthen, why it is not blocking, and where the risk of doing without it is carried. `FACT` — GnuCOBOL is documented as passing over 9,700 tests of the NIST COBOL 85 verification suite while claiming conformance to no standard level, and that suite is obtained as an external archive rather than shipped with the compiler | **Run 1b** gains an optional differential layer over the characterization corpus; **Run 8** gains mutation-corpus breadth beyond the 23 in-repo `.cut` suites | Run 1b's oracle is the golden-file set drawn from the repository's own material, and Run 8's floor is measured against that same corpus. Both are fully defined and fully checkable without any external material. The suite would add breadth, not correctness | Risk **R4** (unverifiable mocked resource semantics) already carries the underlying exposure and its mitigation — two reachable sources in a fixed order, everything else `UNKNOWN` and provisional |
| **2** | **Adoption rather than construction of a COBOL mutation-testing harness.** `FACT` — research established that no off-the-shelf COBOL mutation-testing tool exists; the canonical inventory of COBOL software and tools lists build, CI, unit-testing and debugging tools and no mutation-testing entry | **Run 8** would spend less effort on the harness and more on the generator | ADR-12 already decides that the harness is built in-repo at `src/test/mutation/`, because building it is the only available option rather than a preference. Nothing is blocked; the enhancement is simply unavailable today | ADR-12 records the decision and the four operators; the floor is CI-enforced either way |

`INFERENCE` — both items are *availability* questions rather than *design* questions, which is why neither can block; reasoning: a design question leaves a run unable to decide what to build, whereas an availability question leaves it able to build the smaller thing that is fully specified. Neither run's success oracle references either item.

### A11.4 Answered from evidence, not carried

**Q1 — Why was `archiveFileName` reverted?**
`FACT` — `[build.gradle:L129]` holds the commented-out line `//    archiveFileName "${productName}-${productVersion}.jar"` immediately above the live `archiveName "${productName}-${productVersion}.jar"` at `[build.gradle:L130]`. `INFERENCE` — the space-call form is the reason: `archiveName` is the legacy string setter, whereas `archiveFileName` is a lazy property, and `archiveFileName "value"` resolves as a call to a method `archiveFileName(String)` that the archive task does not declare, so it fails rather than assigning. The correct forms are `archiveFileName = "value"` or `archiveFileName.set("value")`. Reasoning: the two lines differ only in the property name, so the failure cannot be attributable to the value, and the legacy setter is the one that accepts the space-call form. **This is labelled `INFERENCE`, not `FACT`** — no attempt was made to modify the build script in this planning run, so the mechanical confirmation is assigned to Run 2, which must record the verbatim failure of the space-call form before adopting the assignment form.

**Q2 — What replaces the configuration-time `.execute()` calls?**
The whole task body at `[build.gradle:L205-L239]` moves into `doLast`, which is what makes the `dependsOn copyJarToBin, copyRunScripts` declaration at `[build.gradle:L203]` effective at all. `FACT` — the calls themselves are `"chmod +x ./approvaltest".execute()` at `[build.gradle:L213]` and `"./approvaltest".execute()` at `[build.gradle:L216]`. `INFERENCE` — relocation alone is necessary but not sufficient as a long-term answer: a raw Groovy `String.execute()` inside `doLast` bypasses the build tool's own process plumbing and is hostile to a configuration cache, so the constraint the replacement must satisfy is *an injected execution service invoked inside the task action, with the working directory and the environment stated explicitly, and with the child's exit status checked rather than discarded*. Reasoning: the current form discards the exit status entirely, which is one of the two reasons the harness can fail without the build noticing. **The specific API is not pinned here**, because the target build-tool major version's exec surface must be confirmed at execution time and recorded in Run 2's handoff rather than guessed. `FACT` — a further reason to relocate: `[build.gradle:L213]` mutates a **tracked** file's mode, changing `approvaltest` from `100644` to `100755` on every run that realizes the task.

**Q3 — Is the coverage gate wired into `check`, and should its exclusions shrink?**
`FACT` — **it is not wired in.** A search for `check.dependsOn` returns nothing, and `jacocoTestCoverageVerification` occurs exactly once in the build script, at its own definition `[build.gradle:L30]`. `INFERENCE` — therefore `./gradlew build` is entirely unaffected by the gate's current violations, and the gate is decorative; reasoning: an unattached verification task never runs. **Answer: wire it into `check` in Run 2, and yes, the exclusions should shrink.** `FACT` — the 0.8 rule excludes exactly **13** entries at `[build.gradle:L37-L49]`, among them `*Generator` and `*TestSuiteConcatenator`, and a second rule at `[build.gradle:L51-L58]` drops `*LinuxProcessLauncher` and `*TestSuiteConcatenator` to a 0.2 floor. `INFERENCE` — excluding generation-path classes removes the gate's value precisely where the risk is highest, because those classes rewrite the user's source; reasoning: the tool's central hazard is emitting a wrong program, and the generator is what emits it. The shrink must be staged: wire the gate in first at the current exclusion set so the attachment itself is provably non-breaking, then remove exclusions one at a time as coverage is added.

**Q4 — Is publication to a central Maven repository warranted?**
**Answer: not for the first release.** `INFERENCE` — every identified consumption shape is jar-or-container invocation rather than dependency resolution; reasoning: `FACT` — the generated launch script is `#!/bin/sh` plus `java -jar bin/cobol-check-0.2.19.jar $@`, the packaging produces a jar plus a distribution zip, and no evidence anywhere in the repository shows the artifact being consumed as a build dependency. `INFERENCE` — publication would add four credentials plus a signing key before any automated release exists at all; reasoning: `FACT` — `.github/` contains exactly three files, none of which publishes a JAR, and there is no `maven-publish` plugin anywhere. **Decision: Run 2 ships a release workflow producing a downloadable JAR. Central-repository publication is deferred behind a named trigger — the first request from a consumer that genuinely resolves the artifact as a dependency.**

**Q5 — Does `test` require `cobc`, or only `approvalTest`?**
`FACT` — **only the approval path requires it, and this was settled by experiment.** With `cobc` removed from `PATH` (confirmed absent by `command -v cobc`), `JAVA_HOME=$JDK11_HOME ./gradlew clean test` reported `BUILD SUCCESSFUL in 7s` with **457 passed, 0 failed**. `FACT` — a second, sharper result from the same experiment: `./gradlew clean test` does **not** run the harness at all — no `Linux detected`, no `not found` lines, no comparison verdict, and no `temp/` or `actual-output.txt` created — whereas `./gradlew tasks` **does** run it and writes a 0-byte `actual-output.txt` into the working tree. `INFERENCE` — the discriminator is task realization: `tasks.register(...)` at `[build.gradle:L201]` is lazy, so the configuration closure runs only when something realizes the task, and listing tasks realizes everything. This **corroborates** the carried claim that listing tasks triggers the harness while **correcting** its implied generality: the correct statement is that the harness runs during the configuration phase of any invocation that realizes `approvalTest`. Practical consequence for CI: the `clean test` step needs no COBOL compiler, but the `clean approvalTest` step does, and `FACT` — a recursive search for `cobc` or `gnucobol` across all of `.github` returns **nothing**, while `[.travis.yml:L8]` — the only file that ever installed it — is dead.

**Q6 — Is the z/OS run worth doing at all?**
**Answer: yes, last, and structurally only.** Three reasons from evidence. (1) `FACT` — four open issues ask for it: #74, #75, #384 and #390. (2) `FACT` — the current state is worse than unimplemented, it is silently broken: `Launcher.getPlatformSpecificLauncher` initialises `ProcessLauncher launcher = null` at `[.../features/launcher/Launcher.java:L46]` and the `ZOS` arm's construction is commented out at `[:L62]`, so the method returns `null`, while `[config.properties:L206]` leaves `zos.process` empty. `INFERENCE` — a user following the documentation reaches a null launcher rather than a clear refusal; reasoning: nothing in the path reports the unsupported platform. (3) **The run pays for itself on a platform that can actually be tested.** `FACT` — the `OSX` arm is broken identically, with its construction commented out at `[:L58]`; `macos-latest` is in the CI matrix at `[.github/workflows/VerifyAction.yml:L13]`; and `[config.properties:L203]` populates `osx.process` with a value that is unreachable because the launcher is `null` before the key is consulted. `INFERENCE` — so the pluggable-launcher boundary delivers one **falsifiable** improvement (macOS, verifiable in CI) alongside one honestly-labelled unverified one (z/OS); reasoning: CI already runs a macOS leg, so the macOS half has a real oracle. Both branches are documented together at `[.../features/launcher/Launcher.java:L43]`, which reads `NOTE: Currently not supporting OSX or ZOS.` — so the carried claim that the macOS branch is undocumented is **contradicted**; what is undocumented is the user-visible consequence.

---

### A11.5 Disclosed divergences — not questions, but each needs one human action

`INFERENCE` — these are separated from A11.1 because they are not open questions: each is a decision already taken and disclosed, with a specific action a human must perform. Reasoning: filing a settled divergence as a question invites it to be re-litigated, while filing it as compliance hides it — so it is recorded as neither.

| # | What the governing plan requires | What was actually done | Why | Impact if the action is skipped | The human action |
| --- | --- | --- | --- | --- | --- |
| **1** | The planning outputs — this document and `upstream-harvest/` — sit outside the repository working tree and are **committed nowhere** | They are committed on the isolated planning branch `blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36`, **and** emitted byte-identical to `/tmp/blitzy/session-output/5cc7044b-36be-4042-9618-a49ed8c86f36/` outside the tree, all twelve files `cmp`-verified | The executing platform publishes a session's output only by committing it on that branch and discards anything uncommitted; erasing it afterwards would require a history rewrite, which is prohibited. Committing nothing would have destroyed the deliverable | Planning artifacts would reach a product branch, which is the outcome the constraint exists to prevent | **Do not merge the planning branch into `Developer` or `main`.** Read this document out of it, keep the harvest, then delete the branch. Full statement in A9.1 |
| **2** | The upstream marks are removed from the product's identity | Removed from the Java namespace, the Gradle group, the artifact ID, the publisher, the extension ID and the product display name — but the **repository slug retains the `cobol-check` substring**, which this plan is not permitted to propose changing | The slug is fixed by the run's own scope; only the identifiers inside the repository were in play | A residual mark survives in the clone URL and in every issue link, even though nothing the tool ships carries it | Decide whether the slug is acceptable as-is. Carried as blocking question 4 in A11.1 because it is genuinely a judgement call rather than a mechanical step |

`FACT` — divergence 1 is verifiable in one command: `git diff --name-only c79624bd286d5f08f156ccce755bc5d4fffd1909` returns only `PROGRAM-PLAN.md` and paths under `upstream-harvest/`, so the claim that no fork source, build or configuration file was touched is checkable rather than asserted.

---

## A12. Requested visibility

Reasoning surfaced on exactly four topics: *the stub→mock extension point design in `Interpreter`/`InterpreterController`, since all three feature runs depend on it; the new mock syntax, which becomes permanent public API the moment it ships; the correctness-assurance strategy, given no access to IBM compilers; the boundary between the test-generation component and the core precompiler.*

### A12.1 The stub→mock extension point design in `Interpreter` / `InterpreterController`

The thing being decided is small and its blast radius is large, which is why it is settled once at programme level rather than three times inside three feature runs.

**What the code actually does.** `FACT` — the decision is a predicate, `Interpreter.shouldLineBeStubbed(CobolLine, State)` at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/Interpreter.java:L272-L294]`, whose `PROCEDURE_DIVISION` branch at `L273-L278` matches batch file I/O, `CALL`, `EXEC SQL`, `EXEC CICS` and `END-EXEC`. `FACT` — the *action* is elsewhere, in two places: `InterpreterController.shouldCurrentLineBeStubbed()` at `[.../features/interpreter/InterpreterController.java:L145-L158]` emits `.` and `CONTINUE` at `L149-L150`, and `InterpreterController.shouldCurrentStatementBeStubbed()` at `[:L160-L172]` emits the identical pair at `L165-L166`.

**Why the seam is three methods and not one.** The supplied ground truth attributes the predicate to `InterpreterController`; that attribution is imprecise, and the imprecision matters. The predicate lives in `Interpreter`, in package `services.cobolLogic`; the emissions live in `InterpreterController`, in package `features.interpreter`. `INFERENCE` — so the enumerated surface must be **fully package-qualified**, or it is ambiguous about which of two classes is meant; reasoning: two different packages contain classes whose names differ by a suffix. And the second emission site is the one that iterates `reader.getCurrentStatement()` at `L161` — meaning it is the site that handles **multi-line** statements, which is exactly what `EXEC SQL`, `EXEC CICS` and multi-line batch I/O are. `INFERENCE` — generalizing only the site the reference example points at would leave the generalized hook emitting a bare `CONTINUE` for the statements the three feature runs exist to handle; reasoning: single-line `EXEC SQL` is the exception, not the rule, in real COBOL.

**Why a strategy interface rather than anything richer.** The alternative designs were a visitor over the interpretation pass, a predicate-only extension leaving emission hardcoded, and unifying the two emission methods. `INFERENCE` — a strategy is the only one of the four that can be introduced with a **byte-identical** oracle; reasoning: ship exactly one implementation that emits the existing pair, and every golden file must be unchanged, which converts "did the refactor preserve behaviour" from a judgement into a measurement. A visitor cannot be introduced that way, a predicate-only extension cannot influence emission at all, and unification changes observable behaviour.

**The asymmetry that must survive, and why it is uncomfortable.** `FACT` — `shouldCurrentStatementBeStubbed()` iterates the current *statement* at `L161` but performs its `endsInPeriod` check against the current *line* at `L164`, and its `return true` at `L168` sits **outside** the `PROCEDURE_DIVISION` guard at `L163`, so it can report "stubbed" while emitting nothing. `INFERENCE` — this looks like a defect and the temptation to fix it inside the extraction is strong, but doing so would be an undeclared second discipline inside a preserving run; reasoning: both properties determine which lines reach emission, so changing either moves characterization golden files, and a golden-file change in a preserving run is a defect by definition. The extraction therefore preserves the asymmetry exactly, and any correction becomes a separately itemized corrective task in a later run with its own oracle. This is the clearest example in the programme of the discipline being load-bearing rather than bureaucratic.

**What the three feature runs consume.** One registration point keyed by the statement kind the predicate matched, at all three seam points. `INFERENCE` — settling it once is what allows the three feature runs to be independently reviewable; reasoning: if each invented its own hook, the third would be refactoring the first two, and none of the three could claim a byte-identical oracle for the shared code.

### A12.2 The new mock syntax — permanent public API from the moment it ships

**The constraint that dominates every other consideration.** A `.cut` file is a user's source code. `INFERENCE` — once a syntax ships, every future version must parse it, so a syntax decision is less reversible than almost any other decision in this programme; reasoning: four consumer classes hold `.cut` files pinned to an archived upstream and cannot be asked to rewrite them.

**Why the syntax is an extension rather than a new grammar.** `FACT` — the shipped runtime working-storage layout already reserves typed slots for exactly the types the feature runs add: `01 ==UT==MOCKS.` at `[src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY:L72]`, a four-character `==UT==MOCK-TYPE PIC X(04)` at `[:L95]`, and 88-level conditions naming `FILE` at `L96`, `CALL` at `L97`, **`CICS` at `L98`**, **`SQL` at `L99`** and `PARA` at `L100`, with `REDEFINES` overlays including `==UT==MOCK-CICS-DATA` at `L110` — itself carrying `==UT==MOCK-CICS-KEYWORDS-KEY PIC X(806)` at `L111` — and `==UT==MOCK-SQL-DATA` at `L115`. `INFERENCE` — the data-layout contract for CICS and SQL mocks was designed years ago and shipped unused, so the new syntax has an existing contract to honour rather than a blank sheet; reasoning: the type discriminator and the per-type overlays are already in the copybook that every generated program includes. This is the single strongest argument for additive extension: the runtime is already shaped for it.

**Why the wiki cannot be the specification, even though it describes exactly this feature.** `FACT` — `INPUT-FILE`, `ON OPEN`, `ON READ`, `DATASET` and `CONDITION` return **zero** occurrences across `Keywords` and `Constants`. `INFERENCE` — the documented syntax has never existed, so "implement what the documentation says" would ship a grammar no user can currently be using while implying continuity with something that was never shipped; reasoning: a keyword absent from the keyword table cannot have been parsed. The documented spellings may still be *chosen* on their merits — they are plausible and they carry no compatibility risk precisely because nothing uses them — but the choice must be made on design grounds and recorded as such, never on the authority of the document.

**Why not adopt the third-party archives' syntax, which already exists and is more complete.** This is the most tempting shortcut available in the whole programme: two archives on a fork contain fourteen classes, design documents and worked examples covering exactly Runs 6 and 7. `INFERENCE` — adopting their syntax means adopting their semantics, and their semantics are unsourced; reasoning: `FACT` — the SQL design document uses only `0`, `100` and `-001` and cites no source at all, and the CICS response type defines 44 constants without referencing the in-repo table's 75 unique conditions. `INFERENCE` — a syntax that names conditions the in-repo authority does not contain would force one of two bad outcomes: either ship the 44 unsourced constants as if verified, or ship a syntax whose vocabulary exceeds what the register can mark verified; reasoning: the syntax fixes the vocabulary and the register must then account for every term in it. So the archives inform the design and supply nothing authoritative.

**The reversibility asymmetry that decides the shape.** New `<TYPE>` values inside the existing `MOCK ... END-MOCK` / `VERIFY ... HAPPENED` grammar are additive: no existing construct changes meaning, and a user's existing `.cut` files are untouched. A new top-level statement family would double the grammar and the verification vocabulary permanently. `INFERENCE` — given that the verification family (`HAPPENED ONCE`, `HAPPENED N TIMES`, `NEVER HAPPENED`) is already the right shape for asserting that a resource interaction occurred, inventing a second family would be adding permanent public API to solve a problem the existing API already solves; reasoning: "was this resource read twice" is the same question as "was this paragraph performed twice".

### A12.3 The correctness-assurance strategy, given no access to IBM compilers

**The honest starting position.** Only GnuCOBOL is executable here. `FACT` — `cobc (GnuCOBOL) 3.2.0`. No amount of effort changes that, so the strategy cannot be "test it harder"; it has to be a strategy about **what can be claimed**.

**The insight the strategy turns on.** Two classes of claim are being conflated by the phrase "are the mocks right", and they have completely different verifiability:

- **Control flow** — does a stubbed statement dispatch to the mock, does a mocked read loop terminate, is the mock invoked the asserted number of times, does the generated program still compile. `INFERENCE` — this class is **fully verifiable** on GnuCOBOL, because it is a property of the generated program's structure rather than of a proprietary subsystem; reasoning: the precompiler's own contribution *is* the injected control flow, and a real compiler executes it.
- **Values** — what `SQLCODE` a particular failure sets, what `RESP` a particular CICS condition raises, what `FILE STATUS` a particular I/O outcome produces. `INFERENCE` — this class is **not** verifiable here at all, because the authority is the proprietary subsystem's behaviour.

`INFERENCE` — separating them is what lets the tool be simultaneously honest and useful; reasoning: the overwhelming majority of the value of a mock is that the code under test takes the branch the developer wanted to exercise, and that is in the verifiable class. Conflating the two would force a choice between shipping nothing and shipping something dishonest.

**Why the source order is fixed rather than "best available".** In-repo authority first, then documented GnuCOBOL `-std=ibm` semantics, then `UNKNOWN`. `INFERENCE` — the ordering exists to prevent a specific failure: a plausible external claim overriding a citable in-repo fact; reasoning: `FACT` — the in-repo table has 75 unique conditions across 76 insertions and a third-party artifact has 44, so "most recent source wins" or "most detailed source wins" would both discard the citable authority in favour of the unsourced one.

**Why `UNKNOWN` propagates as `BLOCKED-BY` rather than as a footnote.** `INFERENCE` — an `UNKNOWN` that does not block anything gets silently resolved by whoever needs it, from recall; reasoning: an agent implementing a mock needs *some* value, and the path of least resistance is to supply a plausible one. Making it an explicit `BLOCKED-BY` on the consuming run forces the gap to be visible at the moment it matters.

**Why enforcement is CI rather than review, and why it is annotated at the point of use.** `INFERENCE` — "verified" is a label an author can apply for free, so it must be falsifiable by a machine: CI fails when a `verified` entry has no corpus program covering it, and fails when a parser-visible mock type has no register entry at all; reasoning: the only cheap, repeatable check available is whether an executable corpus program exercises the claim. And the annotation appears in the tool's own output rather than in documentation because `INFERENCE` — the risk is a user *believing a result*, and the moment of belief is when the result is read; reasoning: a caveat in a document is not present at that moment.

**The repository already demonstrates the failure mode this strategy exists to prevent.** `FACT` — `EIBResponseTable.lookup(String)` at `[.../services/cobolLogic/EIBResponseTable.java:L130-L133]` returns a zero-filled response when a condition name is not found. `INFERENCE` — wired as-is into a CICS mock, every unrecognized condition would present as a successful completion; reasoning: a zero response code is what normal completion looks like. That is a silent wrong answer produced by a lookup miss — the exact shape of the risk, already sitting in the code, and it is why the CICS run's register entry must include making the miss explicit.

### A12.4 The boundary between the test-generation component and the core precompiler

**What the boundary is.** The generator consumes COBOL source and emits `.cut` text. It does not merge, interpret or launch. It reads the parse contract and the numeric-field metadata, and nothing else. `INFERENCE` — stated as an invariant: the precompiler must run correctly with the generator absent, and the generator must be testable without compiling any COBOL; reasoning: two components that cannot be exercised independently cannot be diagnosed independently.

**Why the `.cut` intermediate is kept rather than emitting merged source directly.** This is the load-bearing decision and it is not obvious, because skipping the intermediate would be less code. `INFERENCE` — the intermediate is what makes generated tests *judgeable*: a generated `.cut` file is human-readable, diffable, and executed by exactly the same path a hand-written one takes, so the mutation harness can judge generated and authored suites with one oracle; reasoning: if the generator emitted merged source, it would need its own correctness oracle distinct from the one the rest of the programme uses, and the mutation floor could not be expressed against it. There is a second, quieter benefit: `INFERENCE` — a generated `.cut` file is reviewable by a human who does not know the generator's internals, which matters for a solo-maintained project; reasoning: review capacity is the scarce resource.

**Why the generator does not live in the parser that already understands the DSL.** `FACT` — `TestSuiteParser.java` is 60,602 bytes / 1,240 lines, the largest class in the codebase, and `Keywords.java` at 38,427 bytes and `InterpreterController.java` at 26,737 bytes follow it. `INFERENCE` — adding generation to the largest class on the critical path would make the one class nobody can safely refactor both larger and more central; reasoning: the standing constraint is not to make the largest classes worse, and generation is a genuinely separable concern with a different failure mode.

**Why generation is bounded away from interpretation specifically.** `INFERENCE` — the two have different oracles and coupling them destroys both: interpretation is judged by byte-identical golden files, generation is judged by mutation score, and a component that did both would fail one oracle for reasons belonging to the other; reasoning: a generated-suite change legitimately alters output, while an interpretation change legitimately must not, so a single component would have contradictory success criteria.

**What crosses the boundary, and the risk that creates.** The parse contract and the numeric-field metadata. `INFERENCE` — the numeric-field dependency is the boundary's weakest point and must be treated as a real coupling rather than a detail; reasoning: `FACT` — a misclassified numeric field silently switches an `EXPECT` from numeric to alphanumeric comparison, which is issue #394's subject, and pull request #411's own description states that working-storage fields not being available early enough caused numeric fields to be treated as non-numeric. `INFERENCE` — so a generator that reads that metadata inherits a live defect class, and the boundary must therefore be crossed through an explicit read-only contract that the generation run's tests exercise directly, rather than by reaching into shared mutable state; reasoning: a shared-state dependency would make a generation defect and an interpretation defect indistinguishable, which is precisely what this boundary exists to prevent.

---
---

# DELIVERABLE B — TWO COMPLETE RUN PROMPTS

Two prompts, for **Run 1a** and **Run 1b**. Each assumes the executing session has **none** of this plan's context and is written to stand alone. The ground truth and both discipline rule sets are repeated **verbatim in each prompt on purpose**. This duplication is mandatory and must not be de-duplicated: there is no rules document to fall back on — `review_rules` for this project returns exactly `No user rules provided.` — so anything not written inside a prompt is unavailable to the session executing it.

---
---

## PROMPT B1 — RUN 1a: APPROVAL-HARNESS REPAIR

### 1. Repository, remotes and environment preconditions

**Your first command is this, literally, before anything else:**

```bash
. /etc/profile.d/blitzy-cobolcheck-env.sh
```

**Nothing is on `PATH` in a non-login shell.** That script exports `JDK8_HOME`, `JDK11_HOME`, `JDK21_HOME`, `JAVA_HOME=$JDK11_HOME`, `GRADLE_USER_HOME`, `PATH` and a de-duplicated `COB_CFLAGS`. It is idempotent. **A probe that fails BEFORE you source it is a probe error, never a finding.** Do not report "the JDK is missing" or "cobc is missing" without having sourced it first, and do not attempt to install anything.

Then verify, and record each verbatim output in your handoff:

```bash
cobc --version
"$JDK8_HOME/bin/java"  -version
"$JDK11_HOME/bin/java" -version
"$JDK21_HOME/bin/java" -version
echo "$COB_CFLAGS"                  # must contain exactly ONE -D_FORTIFY_SOURCE
JAVA_HOME=$JDK11_HOME ./gradlew --version
```

`core.autocrlf` is deliberately **not** probed in this block: it is clone-local state that a fresh clone does not carry, so it is set and verified in the repository block below rather than asserted before anything has set it. Probing it here would report a spurious failure on a clean clone — the same class of probe error the paragraph above warns about.

**The working repository is `Blitzy-Sandbox/blitzy-cobol-check`.** All commits land here. `openmainframeproject/cobol-check` is the archived research-only upstream and is never a commit target.

**Do not assume any of the following is already true of your clone — verify each, and establish it if it is not.** The planning run observed four remotes, 21 upstream heads, 211 upstream pull-request head refs and `core.autocrlf=false`, but every one of those is **clone-local state that does not travel**: a fresh clone has one remote, no upstream refs, no local `core.autocrlf` setting, and an `origin/HEAD` that points at whatever branch it was cloned on rather than at `Developer`. Run this block, in order, before anything else:

```bash
# 1. Confirm you are in the right repository. The origin URL must resolve to the fork.
#    It may legitimately be an on-disk path rather than a github.com URL, and it may embed
#    a credential -- never echo it into a log, a handoff or a commit message.
git rev-parse --show-toplevel
git remote get-url origin | sed -E 's#(https://)[^@]*@#\1<redacted>@#'

# 2. Line-ending translation OFF, repository-locally. Column-significant fixed-format
#    COBOL is corrupted by translation, and every golden file depends on this.
git config --local core.autocrlf false
test "$(git config --get core.autocrlf)" = false || { echo 'FATAL: core.autocrlf is not false'; exit 1; }

# 3. Remotes, added idempotently. `git remote add` fails if the remote exists, so
#    set-url is used to make the block re-runnable.
add_remote() { git remote add "$1" "$2" 2>/dev/null || git remote set-url "$1" "$2"; }
add_remote upstream https://github.com/openmainframeproject/cobol-check.git
add_remote gm       https://github.com/generalmotors/openmainframeproject-cobol-check.git
add_remote livingmf https://github.com/Living-Mainframe/cobol-check.git
git remote -v

# 4. Upstream branches AND every pull-request head ref. The 8 open pull requests are
#    locked by the archive, so they are re-applied from refs, never merged.
git fetch upstream '+refs/heads/*:refs/remotes/upstream/*'
git fetch upstream '+refs/pull/*/head:refs/remotes/upstream/pr/*'
git fetch gm
git fetch livingmf

# 5. Prove the fetch worked rather than assuming it.
git for-each-ref --format='%(refname)' 'refs/remotes/upstream/pr/*' | wc -l   # expect >= 8
for n in 330 336 337 338 408 409 410 411; do
  git rev-parse --verify -q "refs/remotes/upstream/pr/$n" >/dev/null \
    || echo "MISSING pull-request head ref: $n"
done
```

**The fork's default branch is `Developer`.** Take that from the captured API metadata — `upstream-harvest/pulls.json` → `repository_metadata.default_branch` — and **not** from `git symbolic-ref refs/remotes/origin/HEAD`, which reports whatever branch your clone was created on and in an agent clone is normally the working branch rather than `Developer`. If your `origin/HEAD` disagrees with `Developer`, that is expected and is not a finding; if the captured metadata disagrees with `Developer`, that **is** a finding — report it.

**If any step above fails, stop and report rather than working around it.** A run that proceeds without the pull-request head refs cannot verify the apply-cleanly status it inherits, and a run that proceeds with line-ending translation on will produce golden files that no other machine reproduces.

**`openmainframeproject/cobol-check` is archived and read-only. Never write to it — no push, no comment, no reopen, no label, no issue edit, under any circumstances.**

Two operational hazards, both measured, that will waste your time if you do not know them:

1. **`./gradlew clean` deletes tracked files.** `build/distributions/cobol-check-0.2.19.zip` and `build/libs/cobol-check-0.2.19.jar` are both **tracked**, and `clean` removes them. `"chmod +x ./approvaltest"` inside the build script also changes a **tracked** file's mode from `100644` to `100755`. Expect a dirty working tree after any build, and restore deliberately rather than committing the churn accidentally.
2. **Do not pipe Gradle output into `tail`, `head` or `grep`.** The daemon holds the pipe open and your shell will hang. Redirect to a file and read the file.
3. **Two tracked paths in this repository are inert planning artifacts, not product, and not authority.** `PROGRAM-PLAN.md` and everything under `upstream-harvest/` were committed by the planning run that wrote your prompt. Nothing in the tool reads them, no test references them, and no build task touches them. **Do not modify them, do not delete them, and do not treat either as evidence** — the governing evidence rule is that only source code and observed execution establish behaviour, and where a document and an executed result disagree the executed result wins. That rule applies to `PROGRAM-PLAN.md` itself exactly as it applies to the upstream wiki. Their removal from the working tree is itemized to a later run and is not yours.

### 2. Carried-forward ground truth, verbatim

Everything in this section was established by execution or by reading source at the cited line. Where you find it contradicted, **say so explicitly and show your evidence** — do not quietly correct it.

**The toolchain anchor.** `cobc --version` reports:

```text
cobc (GnuCOBOL) 3.2.0
Copyright (C) 2023 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
Written by Keisuke Nishida, Roger While, Ron Norman, Simon Sobisch, Edward Hart
Built     Sep 23 2025 17:57:37
Packaged  Jul 28 2023 17:02:56 UTC
C version "15.2.0"
```

**The build state, measured.** `JAVA_HOME=$JDK11_HOME ./gradlew clean test approvalTest` reports `BUILD SUCCESSFUL`, exit 0, with **457 tests passed, 0 failed, 0 skipped**. `JAVA_HOME=$JDK8_HOME ./gradlew clean test` also reports 457 passed, 0 failed. `JAVA_HOME=$JDK21_HOME ./gradlew clean test` **fails**, with the JUnit XML recording 457 tests and **227 failures** whose signature is `java.lang.IllegalArgumentException: Unsupported class file major version 65`. **The JDK 21 failure is an expected baseline datum. Do not fix it in this run.**

**The defect you are repairing.** The build is green **for the wrong reason**. On a cold tree the following prints under `> Configure project :` and **before** `> Task :clean`:

```text
Linux detected
./approvaltest: 1: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 2: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 3: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 4: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 5: ./temp/approvalTest/cobolcheck: not found
./approvaltest: 6: ./temp/approvalTest/cobolcheck: not found
exit from compare: 0
./expected-output.txt matches ./actual-output.txt - PASS
```

`actual-output.txt` is **0 bytes**, **zero** COBOL programs are compiled, and the build exits **0**.

**Two independent causes, both of which you are fixing:**

- **Cause 1 — the whole task body runs during the configuration phase.** `tasks.register("approvalTest", Test) { ... }` at `[build.gradle:L201]` is a lazy registration whose configuration closure body spans `[build.gradle:L205-L239]`. That body contains the harness executions at `[build.gradle:L213]` (`"chmod +x ./approvaltest".execute()`) and `[build.gradle:L216]` (`"./approvaltest".execute()`), **and also** the comparison at `[build.gradle:L230]` (`output = new BuildHelper().compareFiles(approvalExpectedOutput, approvalActualOutput, true)`) and the failure branch at `[build.gradle:L233-L235]` ending in `throw new StopExecutionException(...)`. Because it is a configuration body, all of it runs before any task executes — which is why the verdict is printed before `:clean`.
- **Cause 2 — the comparison returns success on an empty stream.** `[build.gradle:L273]` reads `while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null)`. With an empty `actual-output.txt` the `&&` short-circuits on the first iteration, the loop body never runs, and control reaches `return 0` at `[build.gradle:L286]`, which the caller treats as a match.

**A finding that will save you from re-deriving a fix that already exists and does not work.** Upstream pull request #338, still open, is titled "Fixed Gradle build for approval tests". Its entire build-script change is `dependsOn fatJar` → `dependsOn copyJarToBin, copyRunScripts`, and `[build.gradle:L203]` **already reads** `dependsOn copyJarToBin, copyRunScripts`. #338 does **not** move the body out of the configuration block. So the `dependsOn` declaration is already present and the harness is still vacuous, because `dependsOn` orders **task execution** and a configuration body has already run before any task executes. **This run supersedes pull request #338.** Do not adopt it; record the supersession in your handoff.

**Which invocations fire the harness, measured.** `./gradlew clean test approvalTest` fires it. `./gradlew clean approvalTest` fires it. `./gradlew tasks` fires it **and writes a 0-byte `actual-output.txt` into the working tree**. `./gradlew clean test` does **not** fire it, because it never realizes the `approvalTest` task. Consequence: `clean test` does not require `cobc`; verified by running it with `cobc` removed from `PATH`, which reported `BUILD SUCCESSFUL` with 457 passed and 0 failed.

**What happens when the harness really runs.** After a full build and the executable-bit grants it needs, `./approvaltest` issues six CLI invocations but only **five** reach a COBOL compile-and-run. The sixth fails outright:

```text
Exception in thread "main" java.lang.RuntimeException: java.io.FileNotFoundException: src/main/cobol/FILECOPY (No such file or directory)
```

The harness passes `-p FILECOPY` at `[approvaltest:L4]` while the program file is `src/main/cobol/FileCopy.cbl` and the suite directory is `src/test/cobol/FileCopy`. On a case-sensitive filesystem the name does not resolve. **This is a real defect, and it is NOT yours to fix in this run.** Characterize it, report the count honestly as five, and record it as a deferral.

**The baseline is stale, by measured amounts.** A real harness run produces `actual-output.txt` at **332 lines / 27,269 bytes**, against `expected-output.txt` at **234 lines / 12,336 bytes** — a delta of **+98 lines / +14,933 bytes**. The first divergence is at line 5 and is a result-format change: the baseline carries `EXPECTED <value>, WAS <value>` on one line where the current code emits `EXPECTED <value>` and `WAS <value>` on two. **Do not refresh the baseline. Its staleness is load-bearing for your exit state.**

**`BuildHelper` is not a Java file.** It is a **Groovy** class declared inline in the build script at `[build.gradle:L263-L296]`, structurally unreachable from anything under `src/test/java`.

**Two `expected-output.txt` files exist and are byte-identical**: the one at the repository root and a second at `src/test/approvalTest/expected-output.txt` (both 234 lines / 12,336 bytes, same MD5). A dormant parallel harness layout of six files sits under `src/test/approvalTest/`. **It is not yours to triage in this run.**

**The repository, at a glance.** 384 tracked files; 110 Java sources under `src/main/java` totalling 12,131 lines across 19 package directories; 35 test sources of which 34 match `*Test.java`/`*IT.java` and 33 actually contain test annotations; `build.gradle` is 296 lines. `[approvaltest]` is mode `0644`, has no shebang, and issues **six separate** invocations for `NUMBERS ALPHA GREETING FILECOPY MOCKTEST DPICNUMBERS`. `[approvaltestWin.cmd]` is mode `0644` and issues **one** invocation passing all six names. The two are structurally non-equivalent — six processes against one — yet CI compares both against the same `expected-output.txt` across three operating systems.

### 3. Scope

**In scope — exactly two itemized changes, plus one mechanically-required relocation.**

**(i) Move the entire `approvalTest` task-configuration body into `doLast`.**

This is larger than the two `.execute()` calls, and the precision matters. The body to relocate is **`[build.gradle:L205-L239]`** in full — the `def output = -1` initialisation, the OS detection, both `.execute()` calls at `L213` and `L216`, the `weRanATest` handling, the comparison at `L230`, and the pass/fail branch at `L233-L238`. The `dependsOn copyJarToBin, copyRunScripts` declaration at **`[build.gradle:L203]` stays outside** the action, because it is a task-wiring declaration and it is what makes the action run *after* the jar and the launch scripts exist.

Relocating only the two `.execute()` calls would leave the comparison at configuration time, judging output that is produced later — inverting the order the repair exists to establish. **This is a mechanical precision of change (i). It is not a third change.**

**(ii) Change the comparison so unequal stream lengths fail.**

`[build.gradle:L273]` currently short-circuits on the shorter stream and falls through to `return 0` at `[build.gradle:L286]`. After your change, a length mismatch — including the empty-stream case — must produce a failing verdict. Preserve the existing behaviour for equal-length streams exactly: the same per-line trimming, the same difference reporting, the same failing verdict on a content mismatch, and the same `-1` on exception.

**The required relocation, which is the mechanical precondition of (ii).** `BuildHelper` moves out of the build script into `buildSrc/`. This is required, not optional: the test you must write to prove change (ii) cannot reference a class declared in the build script's own class space. **It is the precondition of change (ii), not a third change.** The resulting file-level diff is exactly:

| Path | Change |
| --- | --- |
| `build.gradle` | Task body `L205-L239` moved into `doLast`; comparison guard at `L273` repaired; the inline `class BuildHelper` at `L263-L296` **removed**; `buildSrc` wiring added if the build script needs an import |
| `buildSrc/build.gradle` | **New.** Applies the Groovy plugin and a test framework; declares the test dependency |
| `buildSrc/src/main/groovy/BuildHelper.groovy` | **New.** The relocated class, with `compareFiles(String, String, boolean)` intact and change (ii) applied |
| `buildSrc/src/test/groovy/BuildHelperTest.groovy` | **New.** The mandated test |

The mandated test must assert **both** cases: that `compareFiles()` returns a failing verdict when the two streams have **unequal lengths**, and that it returns a failing verdict when one stream is **empty**. Add whatever further cases you need — equal-and-matching returns success, equal-but-differing returns failure, a missing file returns the exception value.

**Adding tests is always in scope and never counts against the itemized enumeration.** "Exactly two itemized changes, nothing else" governs **production and build code only**. A run whose repair is not provable without a new test is *required* to add one. Only the *modification* of pre-existing tests is restricted.

**Explicitly OUT of scope. Do not touch any of these:**

- **Any Java source under `src/main/java`.** In particular `Interpreter`, `InterpreterController`, `LineRepository`, `CopybookExpander`, `Generator`, `TestSuiteParser`, `Main`, `Constants`, `StatusController` and every output formatter.
- **Any pre-existing test under `src/test/java`.** Do not modify, rename, disable, delete or narrow a single existing assertion.
- **`expected-output.txt`.** Do not refresh, regenerate or edit the approved baseline. Its staleness is required for your exit state.
- **The CI workflows.** `.github/workflows/VerifyAction.yml` installs no GnuCOBOL and hosted runners do not supply one — a recursive search for `cobc` or `gnucobol` across all of `.github` returns nothing. That is a real gap and it belongs to the next run. Report your executed-program count from your **local** run only.
- **Dependency versions, the Gradle wrapper version, the Sonar plugin, the JaCoCo configuration, the coverage-gate exclusions, the `fatJar` `configurations.compile` line at `[build.gradle:L138]`, and the commented `archiveFileName` at `[build.gradle:L129]`.** All of these are known issues owned by a later run.
- **The identity rename.** No package, group, artifact or product-name change.
- **Committed binaries and junk files.** Do not delete any of them, and do not rewrite history: no `git filter-repo`, no `git rebase --root`, no force-push.
- **The `FILECOPY` case-resolution defect and the Linux/Windows harness non-equivalence.** Both are real; both belong to a later run. Characterize and defer.
- **The dormant `src/test/approvalTest/` layout.** Leave it alone.
- **Upstream pull request #338**, which this run supersedes.

### 4. Your labels, and both discipline rule sets verbatim

#### Governing constraints and their provenance — read this before the rule sets below

**There are no user-specified rules on this project, and you must not go looking for any.** The rules mechanism was queried and returned exactly:

```text
No user rules provided.
```

The project's on-disk rules document is **empty**. There is therefore **no rule to cite by name, none to summarize, and none to invent**. That mechanism is where the full text of every rule *would* live, and on this project it contains nothing.

**The absence is not permission to lower the bar.** Enterprise-standard best practice applies in its place, and this run is held to it: evidence over assertion with a `path:locator` citation behind every claim about existing behaviour · no fabricated version pins — state the constraint a version must satisfy and resolve the pin at execution time, recording it in your handoff · reproducibility as a first-class requirement · the smallest verifiable diff · decide, then record the rejected alternative · documentation is never proof, and where a document and an executed result disagree the executed result wins · supply-chain and licence hygiene as shipping requirements · automation over convention, because anything depending on human memory is a latent defect.

**The provenance distinction that matters operationally, stated because it changes what you should do when you cannot find something.** The dense body of binding constraints this run operates under — the backward-compatibility contract, the "do not touch" set, the two-axis change discipline, the mechanical definition of green, the evidence standard, the environment-authority rule, the four declared non-goals and the scope boundary — are **prompt-level constraints, not user-specified rules**. They are **not retrievable through the rules mechanism**: a session that goes looking for a rules document will find nothing and must not conclude that the constraints do not exist. That is precisely why they are written out in full inside this prompt rather than referenced, and why the duplication in this prompt is deliberate and must not be "optimized" away. **This prompt is self-contained by design: everything binding on you is in it.**

#### Your labels, and both rule sets

**Your labels: `S2·CORR`.**

A run whose two labels are not both stated has no oracle and must not start. Yours are both stated. **Do not collapse the axes**: *which code you may touch* and *whether observable behaviour may change* are independent, and collapsing them produces a run whose scope contradicts its own discipline.

**Both rule sets below are reproduced verbatim from the governing constraint text — character for character, including its American spelling of "behavior", its `·` separators and its parenthesised definitions. They are inside a fenced block so that nothing can reflow or reword them, and so that "verbatim" is mechanically checkable rather than a claim. Do not paraphrase them, do not re-order them, and do not carry a reworded copy into any document you produce.** Both axes are given in full — all four surface values and all three discipline values — so you can see where `S2` and `CORR` sit among them:

```text
Permitted surface: S1 non-production (tests, golden files, corpus, harness, CI) · S2 peripheral production (build scripts, wrapper, packaging, dependency declarations, identifiers, file layout, headers — no parsing, interpretation or generation logic, and no passing-through improvements) · S3 enumerated core (named classes and methods on the parse/interpret/generate path, enumerated before the run starts; anything outside the enumeration is S2-disciplined within that run) · S4 new code (new classes and packages isolated from the existing large classes, plus enumerated touches to shared code where unavoidable)

Behavior discipline: PRES preserving (any observable change is a defect) · CORR corrective (specific changes are the objective, each itemized before the run starts; an unitemized golden-file change is a defect even if it looks like an improvement; may add a config key whose default reproduces current behavior, but no new DSL syntax, CLI flag or output format) · ADD additive (new behavior required, landing behind new syntax and new keys defaulting to current behavior, shipping with tests and documentation in the same change)
```

**Applied to you — this paragraph is application, not rule text; the verbatim rule text is the fenced block above and nothing outside it.** `S2` means the build script and `buildSrc/` are your surface and nothing on the parse/interpret/generate path is — and it explicitly forbids passing-through improvements, so a tidy-up you notice in `build.gradle` while you are in there is out of scope unless it is one of your two itemized changes. `CORR` means your two changes are the objective and anything else observable is a defect. You may **not** add a DSL construct, a CLI flag or an output format. You may add a config key only if its default reproduces current behaviour, and you do not need one.

**Second disciplines must be declared in advance** with their own oracle. Two *undeclared* disciplines are the defect. If you discover mid-run that you cannot land the repair without a second discipline, **stop and report** rather than proceeding.

**The safety net, and what you may not do to it.** 457 tests across 34 named test classes with zero failures. Existing assertions must **not** be weakened, deleted, disabled, renamed or narrowed to make a change pass. The single mechanism by which a pre-existing assertion may change is an itemized corrective task naming the class and method **before** the run starts, recording old and new expected values in the handoff with the motivating issue number. **You have no such itemized task. Therefore you may not alter any pre-existing assertion at all.**

### 5. System boundaries and the backward-compatibility contract, as they apply to you

**The architecture is a precompiler and must remain one.** The tool merges the program under test with test suites and test-case management logic to produce a copy with test code embedded, then compiles and executes the copy. Do not replace this with a runtime interpreter, a language-server service or a compiler plugin, and do not rewrite anything in another language.

**The compatibility contract — the surfaces that must not break.** The `.cut` test DSL; `config.properties` keys, their names, semantics and defaults; the CLI flag set and semantics; the `txt`/`directOutput` result format, which is the default and works; and the invocation shape, which is a plain `java -jar` call from a shell script or container. Real users depend on these today and all of them are pinned to an archived upstream, so a fork that breaks their tests is worthless.

**As applied to you, this is simple: you touch none of those surfaces.** Your changes are confined to the build script and `buildSrc/`. Nothing you do may change what the tool emits, how it is invoked, what it accepts or what it prints. If any of your changes alters `actual-output.txt`'s *content* — as opposed to causing it to be produced at all — that is a defect.

**Two things you must not do even though they would make your run look better.** Do not refresh the baseline to turn your red green. Do not weaken the comparison to accommodate the stale baseline. Both would restore exactly the false green you are removing.

### 6. Acceptance criteria — mechanically checkable

**The definition of green, verbatim.** Green means **all** of: `clean test` reports **457 or more** tests with zero failures · the approval harness **compiled and executed a non-zero number of COBOL programs** · `actual-output.txt` is **non-empty** · the comparison against the approved baseline **genuinely matched**. The build is currently green for the wrong reason — vacuously, on a zero-byte `actual-output.txt` with zero COBOL programs compiled. **An agent that satisfies the invariant by leaving the harness broken has failed, not succeeded. Any run reporting green must state the executed program count.**

**You are the one documented exception, and you are required to end red.**

| # | Criterion | How it is checked | Required result |
| --- | --- | --- | --- |
| 1 | Unit suite still green | `JAVA_HOME=$JDK11_HOME ./gradlew clean test` and read the JUnit XML under `build/test-results/test/` | **457 or more tests, 0 failures, 0 errors** |
| 2 | The harness actually runs COBOL | Count `About to launch process` occurrences in the harness output | **Non-zero, and you must state the number.** It will be **5**, because `-p FILECOPY` fails to resolve — report 5, not 6 |
| 3 | Real output is produced | `wc -c actual-output.txt` | **Non-zero.** It will be approximately 27,269 bytes / 332 lines |
| 4 | The harness runs at execution time, not configuration time | Capture the full log and locate the harness output relative to `> Task :` markers | The harness block must appear **after** a `> Task :` marker, **not** under `> Configure project :` |
| 5 | The comparison now fails against the stale baseline | `JAVA_HOME=$JDK11_HOME ./gradlew clean approvalTest`; read the exit status and the verdict line | **`approvalTest` FAILS.** This is the required exit state |
| 6 | The empty-stream defect is fixed and proven | Run the `buildSrc` test | The test asserting a failing verdict on **unequal lengths** and on an **empty stream** passes |
| 7 | `./gradlew tasks` no longer runs the harness | On a clean tree, `rm -rf bin temp testruns actual-output.txt`, then `JAVA_HOME=$JDK11_HOME ./gradlew tasks`, then check for `actual-output.txt` | **No `actual-output.txt` is created**, and no harness output appears |
| 8 | No out-of-scope file changed | `git diff --name-status <start-commit> HEAD` | Only `build.gradle` and the three `buildSrc/` paths. **No** file under `src/main/java`, **no** pre-existing file under `src/test/java`, and **not** `expected-output.txt` |
| 9 | No pre-existing assertion altered | `git diff <start-commit> HEAD -- src/test/` | **Empty** |

**Criterion 5 is the point of the run, so it bears repeating.** You repair the harness without refreshing the stale baseline, so the comparison **necessarily** fails against an out-of-date approved file. **That red is the evidence the repair worked and it is your required exit state.** Before your change the comparison passed on a zero-byte file; a failing comparison against real output is the only observable proof that real output now exists.

**You must NOT revert on account of that red.** Reverting would undo the fix and restore the vacuous pass. The standing rule that a run unable to end green reverts to its starting commit **is suspended for this run only**, by prior declaration. Criterion 1 must still hold: `clean test` green at 457/457. **No other run in this programme may end red.**

**Stop and report** rather than continuing if: `clean test` is not green at entry; `cobc --version` differs from the anchor above; you cannot land the repair without a second discipline; or you cannot land it without altering a pre-existing assertion.

### 7. The handoff artifact you must produce

Create **`RUN-1A-HANDOFF.md`** at the repository root and commit it as part of this run. The next run reads it and reconciles against it, so a missing section blocks that run. Use exactly this structure.

```markdown
# RUN-1A-HANDOFF

## 0. Standing environment precondition
The first command of this session was:
    . /etc/profile.d/blitzy-cobolcheck-env.sh
Nothing is on PATH in a non-login shell. A probe that fails BEFORE sourcing it is a
probe error, never a finding. State whether sourcing succeeded and paste any deviation.

## 1. Identity
Run: 1a — Approval-harness repair
Labels: S2·CORR
Second discipline declared in advance: none
Starting commit hash:                     <full 40-character SHA — mandatory>
Ending commit hash:                       <full 40-character SHA — mandatory>
Tag applied:                              run-1a-approval-harness-repair
BLOCKED-BY tag started from: none (first run)

All three identity fields above are mandatory and separate, matching the canonical
template in A10.1.  The tag is not a substitute for either hash: the rollback model
resolves a checkpoint by hash, and a tag that is later moved or a hash that was never
recorded both leave this run unrecoverable.

## 2. Toolchain anchor
`cobc --version` verbatim, all lines. COB_CFLAGS verbatim (exactly one -D_FORTIFY_SOURCE).
`git config --get core.autocrlf` (must be false). Daemon JDK, test JDK(s), Gradle version.
Does this anchor match the value carried in the prompt?  YES / NO
If NO: this is a finding. Stop and report; every golden file downstream would be suspect.

## 3. Green status, stated mechanically
`clean test`: <n> tests, <n> failures, <n> skipped        (must be >= 457 with 0 failures)
COBOL programs compiled and EXECUTED by the harness: <n>  (REQUIRED. Expected 5, not 6)
`actual-output.txt`: <bytes> bytes, <lines> lines          (must be non-empty)
Comparison against the approved baseline: MATCHED / DID NOT MATCH   (must be DID NOT MATCH)
Overall: RED on approvalTest, GREEN on clean test
Is this run the declared exception REQUIRED to end red?  YES
Evidence the harness now runs at execution time, not configuration time:

## 4. Changes by file, with reasons
| File | Change | Why | Itemized in advance? |

## 5. Golden-file status, itemized
`expected-output.txt`: UNCHANGED — deliberately not refreshed. State its size and line count,
and the measured delta against the actual output produced by the repaired harness.

## 6. Architecture decisions later runs must respect
State the buildSrc/ relocation and its consequence for the Gradle 9 / Groovy 4 migration.
State what replaced the configuration-time execution, and whether the child's exit status
is now checked rather than discarded.

## 7. New or changed public surface
Expected: NONE. If you changed anything user-observable, say so and explain why.

## 8. Verified / provisional register delta
Expected: none for this run.

## 9. Issues and pull requests
Issue #329 "Fix approval tests": advanced, not closed — closure needs the next run's baseline.
Pull request #338: SUPERSEDED by this run. Record that its central change was already
present at [build.gradle:L203] and was insufficient on its own.

## 10. Pre-existing assertions changed
Expected: NONE. `git diff <start> HEAD -- src/test/` must be empty; paste the result.

## 11. Anything contradicting the carried-forward ground truth
State it explicitly with evidence. Do not quietly correct it. Specifically confirm or
refute: the +98 line / +14,933 byte staleness; that 5 of 6 programs execute; that
`-p FILECOPY` raises an unhandled FileNotFoundException.

## 12. Performance envelope
Not measured in this run.

## 13. Deferred into the next run
- Regenerate `expected-output.txt` and restore full green.
- Add a GnuCOBOL install step to `.github/workflows/VerifyAction.yml`; without it no
  run's green claim is CI-verifiable. A recursive search for cobc|gnucobol across .github
  currently returns nothing.
- The `-p FILECOPY` case-resolution failure and its unhandled exception.
- The structural non-equivalence of `approvaltest` (six processes) and `approvaltestWin.cmd`
  (one process) against a single shared baseline across three operating systems.
- Triage of the dormant `src/test/approvalTest/` six-file layout and its byte-identical
  second copy of `expected-output.txt`.

## 14. Reconciliation performed against the predecessor handoff
Not applicable — this is the first run.
```

### 8. Your output contract

- **Commit to this repository.** Your in-repo changes plus `RUN-1A-HANDOFF.md`, and nothing else.
- **Tag the final commit `run-1a-approval-harness-repair`.** The tag is immutable once created: never move, delete or re-point it, and never rewrite the history it names.
- **Do not commit build output or working-tree churn.** Restore `build/distributions/cobol-check-0.2.19.zip`, `build/libs/cobol-check-0.2.19.jar`, `approvaltest`'s file mode and `scripts/linux_gnucobol_run_tests`' file mode to their committed states before committing. Do not commit `actual-output.txt`, `temp/`, `testruns/` or `bin/`.
- **Do not create any document other than `RUN-1A-HANDOFF.md`.** No status reports, no progress summaries, no setup guides, no validation guides.
- **No temporal planning anywhere** in your handoff — no estimates, no durations, no dates. Sequencing is expressed only as `BLOCKED-BY`.
- **No outreach of any kind.** Do not contact anyone, and do not propose doing so. This is a solo-maintainer project with agent support; optimize for automation and minimal human decision points.
- **Label every claim in your handoff** `FACT` with a `file:line` or a retrieved URL, `INFERENCE` with its reasoning stated, or `UNKNOWN`. Never silently upgrade an inference to a fact.
- **Verify and record; never install.** If a tool is present, use it and record its version. If it is absent, record the omission and label the dependent finding `UNKNOWN`. Self-provisioning is not available to you.
- **Read the source rather than the documentation.** Where a document and an executed result disagree, the executed result wins. The upstream wiki documents mock syntax that does not exist in the keyword tables; treat every document, including this prompt, as claims to be checked against the code.

---
---

## PROMPT B2 — RUN 1b: CHARACTERIZATION SAFETY NET

### 1. Repository, remotes and environment preconditions

**Your first command is this, literally, before anything else:**

```bash
. /etc/profile.d/blitzy-cobolcheck-env.sh
```

**Nothing is on `PATH` in a non-login shell.** That script exports `JDK8_HOME`, `JDK11_HOME`, `JDK21_HOME`, `JAVA_HOME=$JDK11_HOME`, `GRADLE_USER_HOME`, `PATH` and a de-duplicated `COB_CFLAGS`. It is idempotent. **A probe that fails BEFORE you source it is a probe error, never a finding.** Do not report a missing JDK or a missing `cobc` without having sourced it first, and do not attempt to install anything.

Then verify, and record each verbatim output in your handoff:

```bash
cobc --version
"$JDK11_HOME/bin/java" -version
echo "$COB_CFLAGS"                  # must contain exactly ONE -D_FORTIFY_SOURCE
JAVA_HOME=$JDK11_HOME ./gradlew --version
```

`core.autocrlf` is deliberately **not** probed in this block: it is clone-local state that a fresh clone does not carry, so it is set and verified in the repository block below rather than asserted before anything has set it. Probing it here would report a spurious failure on a clean clone — the same class of probe error the paragraph above warns about.

**The working repository is `Blitzy-Sandbox/blitzy-cobol-check`.** All commits land here. `openmainframeproject/cobol-check` is the archived research-only upstream and is never a commit target.

**Do not assume any of the following is already true of your clone — verify each, and establish it if it is not.** The planning run observed four remotes, 21 upstream heads, 211 upstream pull-request head refs and `core.autocrlf=false`, but every one of those is **clone-local state that does not travel**: a fresh clone has one remote, no upstream refs, no local `core.autocrlf` setting, and an `origin/HEAD` that points at whatever branch it was cloned on rather than at `Developer`. Run this block, in order, before anything else:

```bash
# 1. Confirm you are in the right repository. The origin URL must resolve to the fork.
#    It may legitimately be an on-disk path rather than a github.com URL, and it may embed
#    a credential -- never echo it into a log, a handoff or a commit message.
git rev-parse --show-toplevel
git remote get-url origin | sed -E 's#(https://)[^@]*@#\1<redacted>@#'

# 2. Line-ending translation OFF, repository-locally. Column-significant fixed-format
#    COBOL is corrupted by translation, and every golden file depends on this.
git config --local core.autocrlf false
test "$(git config --get core.autocrlf)" = false || { echo 'FATAL: core.autocrlf is not false'; exit 1; }

# 3. Remotes, added idempotently. `git remote add` fails if the remote exists, so
#    set-url is used to make the block re-runnable.
add_remote() { git remote add "$1" "$2" 2>/dev/null || git remote set-url "$1" "$2"; }
add_remote upstream https://github.com/openmainframeproject/cobol-check.git
add_remote gm       https://github.com/generalmotors/openmainframeproject-cobol-check.git
add_remote livingmf https://github.com/Living-Mainframe/cobol-check.git
git remote -v

# 4. Upstream branches AND every pull-request head ref. The 8 open pull requests are
#    locked by the archive, so they are re-applied from refs, never merged.
git fetch upstream '+refs/heads/*:refs/remotes/upstream/*'
git fetch upstream '+refs/pull/*/head:refs/remotes/upstream/pr/*'
git fetch gm
git fetch livingmf

# 5. Prove the fetch worked rather than assuming it.
git for-each-ref --format='%(refname)' 'refs/remotes/upstream/pr/*' | wc -l   # expect >= 8
for n in 330 336 337 338 408 409 410 411; do
  git rev-parse --verify -q "refs/remotes/upstream/pr/$n" >/dev/null \
    || echo "MISSING pull-request head ref: $n"
done
```

**The fork's default branch is `Developer`.** Take that from the captured API metadata — `upstream-harvest/pulls.json` → `repository_metadata.default_branch` — and **not** from `git symbolic-ref refs/remotes/origin/HEAD`, which reports whatever branch your clone was created on and in an agent clone is normally the working branch rather than `Developer`. If your `origin/HEAD` disagrees with `Developer`, that is expected and is not a finding; if the captured metadata disagrees with `Developer`, that **is** a finding — report it.

**If any step above fails, stop and report rather than working around it.** A run that proceeds without the pull-request head refs cannot verify the apply-cleanly status it inherits, and a run that proceeds with line-ending translation on will produce golden files that no other machine reproduces.

**`openmainframeproject/cobol-check` is archived and read-only. Never write to it — no push, no comment, no reopen, no label, no issue edit, under any circumstances.**

Three operational hazards, all measured:

1. **`./gradlew clean` deletes tracked files.** `build/distributions/cobol-check-0.2.19.zip` and `build/libs/cobol-check-0.2.19.jar` are both **tracked**, and `clean` removes them. The build also changes the file mode of the tracked `approvaltest`, and a harness run changes the mode of the tracked `scripts/linux_gnucobol_run_tests`. Expect a dirty tree and restore deliberately.
2. **Do not pipe Gradle output into `tail`, `head` or `grep`.** The daemon holds the pipe open and your shell will hang. Redirect to a file and read the file.
3. **Two tracked paths in this repository are inert planning artifacts, not product, and not authority.** `PROGRAM-PLAN.md` and everything under `upstream-harvest/` were committed by the planning run that wrote your prompt. Nothing in the tool reads them, no test references them, and no build task touches them. **Do not modify them, do not delete them, and do not treat either as evidence** — the governing evidence rule is that only source code and observed execution establish behaviour, and where a document and an executed result disagree the executed result wins. That rule applies to `PROGRAM-PLAN.md` itself exactly as it applies to the upstream wiki. Their removal from the working tree is itemized to a later run and is not yours.
3. **`clean` does not remove `bin/`, `temp/`, `testruns/` or `actual-output.txt`.** To reproduce a genuinely cold tree, `rm -rf bin temp testruns actual-output.txt` first.

### 2. MANDATORY FIRST STEP — reconcile against `RUN-1A-HANDOFF.md`

**Before you plan or change anything, read `RUN-1A-HANDOFF.md` at the repository root and reconcile against it. This is a required step, not a courtesy.** Record the reconciliation in section 14 of your own handoff. Specifically:

1. **Confirm the tag `run-1a-approval-harness-repair` exists** and that your starting commit is that tag. Do not begin from an untagged commit.
2. **Compare its toolchain anchor with your own `cobc --version`.** If they differ, **stop and report**. A mismatch means the image is not stable across sessions, every golden file you are about to capture would be suspect, and the entire purpose of this run would be void.
3. **Confirm its reported green status matches what you observe**: `clean test` green at 457 or more with zero failures, and `approvalTest` **red**. **The inherited red is expected and correct** — the previous run repaired the harness without refreshing the stale baseline, and that red is the evidence the repair worked. **Do not treat it as breakage.**
4. **Confirm its executed COBOL program count.** It should be **5**, not 6.
5. **Take up every item in its section 13 deferral list** and record the disposition of each: which you are performing in this run, which you are passing on, and why.
6. **Read its section 11**, and state whether anything it recorded as contradicting the carried ground truth changes your plan.
7. **Read its section 6** and respect every architecture decision it records — in particular the relocation of the comparison helper into `buildSrc/` and whatever replaced the configuration-time execution.
8. **If `RUN-1A-HANDOFF.md` is missing, or is missing its section 0, 2, 3, 5 or 6, stop and report.** Those five sections are consumed as entry preconditions rather than as records.

### 3. Carried-forward ground truth, verbatim

Everything here was established by execution or by reading source at the cited line. Where you find it contradicted, **say so explicitly and show your evidence** — do not quietly correct it.

**The toolchain anchor.** `cobc --version` reports:

```text
cobc (GnuCOBOL) 3.2.0
Copyright (C) 2023 Free Software Foundation, Inc.
License GPLv3+: GNU GPL version 3 or later <https://gnu.org/licenses/gpl.html>
This is free software; see the source for copying conditions.  There is NO
warranty; not even for MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
Written by Keisuke Nishida, Roger While, Ron Norman, Simon Sobisch, Edward Hart
Built     Sep 23 2025 17:57:37
Packaged  Jul 28 2023 17:02:56 UTC
C version "15.2.0"
```

**Two environment facts that make golden files non-reproducible if unrecorded. Both are invisible in the repository, and both must be established before any capture.**

1. **`COB_CFLAGS` must carry exactly one `-D_FORTIFY_SOURCE` value.** Debian-family GnuCOBOL packaging defines it twice, the C compiler then emits a redefinition warning on **stderr**, and `ProcessOutputWriter.writeOutPutToConsole()` prints the child's stderr to `System.out` — verified at `[src/main/java/org/openmainframeproject/cobolcheck/features/launcher/ProcessOutputWriter.java:L111-L114]`, with `System.out.println(processInput)` at `L112` and `System.out.println(processError)` at `L113`. The warning would therefore be written into `actual-output.txt` and into every golden file you capture. The activation script already exports a de-duplicated value; **verify it and record it verbatim before capturing anything.**
2. **`core.autocrlf` must be `false`.** The project has line-ending sensitivity in column-significant fixed-format COBOL. **Do not assume it is already set.** It is repository-local configuration living in `.git/config`, so it does not survive a fresh clone; the setup block in §1 sets it locally and then asserts the read-back value. If you skipped that block, run it now — a golden file captured with line-ending translation active is wrong in a way no later comparison can detect.

**The build state, measured.** `JAVA_HOME=$JDK11_HOME ./gradlew clean test` reports 457 passed, 0 failed, 0 skipped. `JAVA_HOME=$JDK8_HOME ./gradlew clean test` also reports 457 passed, 0 failed. `JAVA_HOME=$JDK21_HOME ./gradlew clean test` **fails**, with the JUnit XML recording 457 tests and **227 failures**: **183** carry `java.lang.IllegalArgumentException: Unsupported class file major version 65`, reached through `net.bytebuddy.jar.asm.ClassReader.<init>(ClassReader.java:189)` while Byte Buddy instruments an existing type hierarchy; **36** carry `java.lang.IllegalArgumentException: Unknown Java version: 21` at `net.bytebuddy.ClassFileVersion.ofJavaVersion(ClassFileVersion.java:257)` while a Mockito plugin loads; and **8** carry no `Caused by:` chain. **The JDK 21 failure is an expected baseline datum. Do not fix it in this run.**

**`clean test` does not require `cobc`; only the approval path does.** Verified by running `clean test` with `cobc` removed from `PATH`: `BUILD SUCCESSFUL`, 457 passed, 0 failed. Verified further: `./gradlew clean test` does not run the harness at all, while `./gradlew tasks` does, because `tasks.register(...)` is lazy and listing tasks realizes every task.

**The COBOL corpus, measured.** 15 programs under `src/main/cobol/`: `ALPHA.CBL`, `BIPM012.CBL`, `DB2PROG.cbl`, `DPICNUMBERS.CBL`, `FileCopy.cbl`, `GREETING.CBL`, `LONGLINESANDNUMBERS.CBL`, `MOCK.CBL`, `MOCKPARA.CBL`, `MOCKTEST.CBL`, `NUMBERS.CBL`, `REPLAC.CBL`, `RETURNCODE.CBL`, `TESTNESTED.CBL`, `WS88LEVEL.CBL` — note the mixed extension casing. **41** copybook fixtures under `src/main/cobol/copy/` — 39 directly in the directory plus 2 under `src/main/cobol/copy/Outrec/`, so a non-recursive listing sees 39 and a recursive one 41. 16 suite directories under `src/test/cobol/` holding 23 `.cut`/`.CUT` files, carrying **25** `TestSuite` declarations and **171** `TestCase` declarations in total. 14 fixtures under `testfiles/`. 5 files under `copybooks/`. Plus `examples/SAMPLEJC` and `testsuites/testsuite1`, `testsuites/testsuite2`.

**Three of those counts contradict the ground truth carried into earlier revisions of this plan, which recorded the copy-fixture count as 40, the `TestSuite` count as 22 and the `TestCase` count as 159. All three were wrong; do not restore them.** Re-measure them yourself before relying on them; these four commands produce 41, 23, 25 and 171 respectively:

```bash
git ls-files 'src/main/cobol/copy/' | wc -l
git ls-files 'src/test/cobol/' | grep -icE '\.cut$'
python3 - <<'EOF'
import re, subprocess
fs=[f for f in subprocess.run("git ls-files 'src/test/cobol/'",shell=True,
    capture_output=True,text=True).stdout.split() if f.lower().endswith('.cut')]
s=c=0
for f in fs:
    t=open(f,encoding='utf-8',errors='replace').read()
    t=re.sub(r'"[^"\n]*"','""',t); t=re.sub(r"'[^'\n]*'","''",t)   # NOT optional
    s+=len(re.findall(r'testsuite',t,re.I)); c+=len(re.findall(r'testcase',t,re.I))
print('TestSuite',s,'TestCase',c)
EOF
```

Stripping quoted string literals before counting is **not optional**: a raw token count returns 176 `TestCase`, because the word appears inside a quoted test-case name at `[src/test/cobol/MOCKTEST/BeforeAfterTest.cut:L19,L23,L27,L99,L102]`. If your own count disagrees with 41 / 23 / 25 / 171, **stop and report** rather than proceeding — the corpus has changed under you and every golden file derived from it is suspect.

**The coverage gap, quantified.** The approval harness invokes exactly six programs — `NUMBERS`, `ALPHA`, `GREETING`, `FILECOPY`, `MOCKTEST`, `DPICNUMBERS` — at `[approvaltest:L1-L6]`. **Nine of the fifteen programs are exercised end-to-end by nothing**: `BIPM012`, `DB2PROG`, `LONGLINESANDNUMBERS`, `MOCK`, `MOCKPARA`, `REPLAC`, `RETURNCODE`, `TESTNESTED`, `WS88LEVEL`. Widening the harness to cover them is the single highest-leverage risk reduction available in the whole programme, and it costs nothing in production risk because this run touches no production code.

**Two uncovered fixtures are disproportionately valuable.** `DB2PROG` is the **only** program on the live `SQLCA.cpy` resolution path: `[src/main/cobol/DB2PROG.cbl:L12]` is `EXEC SQL INCLUDE SQLCA  END-EXEC.` and `L13` is `EXEC SQL INCLUDE TEXEM  END-EXEC.`, resolved from the filesystem under `[config.properties:L125]` (`application.copybook.directory = src/main/cobol/copy`) with `cpy` in the suffix list at `[config.properties:L162]`. And `src/test/cobol/RETURNCODE/ReturnCode-4.cut` is a nine-line suite **deliberately failing at return code 4** — the ready-made oracle for a later exit-code repair, currently exercised by nothing.

**One confirmed orphan.** `src/test/cobol/FDTEST/FDTEST01.CUT` has no matching program in `src/main/cobol/`. There are 16 suite directories against 15 programs, and this is the difference. Record it; do not invent a program for it.

**A defect you will encounter and must characterize rather than fix.** The harness issues six CLI invocations but only **five** reach a COBOL compile-and-run. The sixth fails:

```text
Exception in thread "main" java.lang.RuntimeException: java.io.FileNotFoundException: src/main/cobol/FILECOPY (No such file or directory)
```

`-p FILECOPY` is passed at `[approvaltest:L4]` while the program file is `src/main/cobol/FileCopy.cbl` and the suite directory is `src/test/cobol/FileCopy`. On a case-sensitive filesystem it does not resolve. **Capture this behaviour in a golden file exactly as it is. It is a real defect and it belongs to a later run.**

**A structural non-equivalence you must record rather than repair.** `[approvaltest]` is mode `0644`, has no shebang, and issues **six separate** processes. `[approvaltestWin.cmd]` is mode `0644` and issues **one** process passing all six program names. Yet CI compares both against the same `expected-output.txt` across three operating systems, per `[.github/workflows/VerifyAction.yml:L13]` (`os: [ubuntu-22.04, windows-latest, macos-latest]`) and `[:L30]` (`./gradlew clean approvalTest`).

**The baseline you are regenerating.** `expected-output.txt` is 234 lines / 12,336 bytes. A real harness run produces **332 lines / 27,269 bytes** — a delta of **+98 lines / +14,933 bytes**. The first divergence is at line 5 and is a result-format change: the baseline carries `EXPECTED <value>, WAS <value>` on one line where the current code emits two lines. A byte-identical second copy exists at `src/test/approvalTest/expected-output.txt`; if you regenerate one, decide and record what happens to the other.

**The CI gap you are closing.** `[.github/workflows/VerifyAction.yml]` is exactly 30 lines. It runs `./gradlew clean test` at `L28` and `./gradlew clean approvalTest` at `L30`, across `os: [ubuntu-22.04, windows-latest, macos-latest]` at `L13` with `java-version: [11]` at `L14`. **A recursive search for `cobc` or `gnucobol` across all of `.github` returns nothing**, and hosted runners do not ship a COBOL compiler. `[.travis.yml:L8]` — `- sudo apt -y install gnucobol` — is the only file that ever installed it, and it is dead. **Without a GnuCOBOL install step, no run's green claim is CI-verifiable, so this cannot be deferred further.**

**The identifier strings your normalization filter must canonicalize.** These are decided and fixed; do not invent alternatives and do not defer them.

| Surface | Old token | New token |
| --- | --- | --- |
| Java package namespace | `org.openmainframeproject.cobolcheck` | `io.blitzy.cbltest` |
| Gradle group | `org.openmainframeproject` | `io.blitzy` |
| Artifact ID / `productName` | `cobol-check` | `cbltest` |
| Product display name | `Cobol Check` / `COBOL Check` | `Blitzy COBOL Test` |

The rename itself happens in a **later** run. You are building the filter **before** the rename precisely so that the later run's byte-identical oracle is enforceable: if the filter did not already canonicalize the new tokens, every golden file would diff after the rename for a reason indistinguishable from a migration defect.

**Why an equal-depth namespace mapping matters, for context.** Resources resolve by classpath path: `Constants.COBOLCHECK_PACKAGE_PATH = "/org/openmainframeproject/cobolcheck"` at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L21]` and `COBOLCHECK_COPYBOOK_DIRECTORY = COBOLCHECK_PACKAGE_PATH + "/copybooks/"` at `[:L24]`, consumed by `getBoilerplateCodeFromCopybooks()` which builds its path at `[src/main/java/org/openmainframeproject/cobolcheck/features/testSuiteParser/TestSuiteParserController.java:L300]` and reads it at `[:L301]`. Three segments map to three, so path depth does not change.

**The two legally-sensitive copybooks, and what that means for your corpus.** `src/main/resources/org/openmainframeproject/cobolcheck/copybooks/DFHEIBLK.CPY` is **packaged but never loaded** — the sole classpath loader named above is invoked only at `[.../features/testSuiteParser/TestSuiteParserController.java:L123]`, `[:L181]` and `[:L195]`, always with a `CCHECK*` name. `copybooks/DFHEIBLK.CPY` is an orphaned duplicate, byte-identical to the packaged copy. `src/main/cobol/copy/SQLCA.cpy` **is** loaded at runtime but is packaged in no artifact. A later run will unvendor them. **Therefore any corpus program that exercises either file must supply it from an explicit corpus-local path**, so your corpus survives that unvendoring, and so that any later copybook-traceable golden-file diff is unambiguous evidence the file is loaded at runtime. **Record which corpus programs exercise which copybook.**

### 4. Scope

**In scope.**

1. **The characterization corpus at `src/test/corpus/`.** Cover all 15 COBOL programs, the copybook fixtures they need, and the 23 `.cut` suites. Corpus programs exercising `DFHEIBLK.CPY` or `SQLCA.cpy` must take them from a corpus-local path.
2. **The golden-file set at `src/test/golden/`**, one golden file per corpus program, capturing current behaviour **including current defects**.
3. **The regenerated approval baseline**, which is one of the golden files. Regenerate `expected-output.txt` from a real harness run so that full green is restored.
4. **The golden-file normalization filter at `src/test/java/io/blitzy/cbltest/golden/`**, canonicalizing the four identifier token pairs in both directions, **plus a test of the filter itself** proving it normalizes only those four token pairs and nothing else — in particular that it does not alter COBOL source content, numeric values, file paths that merely resemble the tokens, or any other text.
5. **Widening the approval harness** from six programs toward the fifteen, so the nine currently-unexercised programs gain end-to-end coverage. Where widening changes the harness scripts, keep the Linux and Windows forms's structural difference recorded rather than silently unified.
6. **The GnuCOBOL install step in `.github/workflows/VerifyAction.yml`**, so that the approval leg can genuinely compile COBOL in CI. Add it for every matrix operating system on which it is achievable, and record explicitly any leg on which it is not.
7. **The verified/provisional register**, created here as the shared artifact later runs append to. Seed it only with what your corpus actually demonstrates. Every entry is `verified`, `provisional` or `UNKNOWN`; every `verified` entry names its source and the corpus program covering it.
8. **New tests, freely.** Adding tests is always in scope and never counts against any enumeration.

**Explicitly OUT of scope. Do not touch any of these:**

- **Any Java source under `src/main/java`.** Not one line. In particular `Interpreter`, `InterpreterController`, `LineRepository`, `CopybookExpander`, `Generator`, `TestSuiteParser`, `Keywords`, `Main`, `Constants`, `StatusController`, `Launcher` and every output formatter.
- **Any pre-existing assertion under `src/test/java`.** Do not modify, rename, disable, delete or narrow one. You may **add** test classes and methods.
- **Every defect you will encounter.** Characterize, never repair: the `-p FILECOPY` case-resolution failure and its unhandled exception; the Linux/Windows harness non-equivalence; the exit code of `0` on failing COBOL tests; the `xml` and `html` output formats that throw; the `COPY ... REPLACING` single-pair limitation; the working-storage `EXEC SQL INCLUDE` platform branch. **A golden file that records a defect is doing its job.**
- **Dependency versions, the Gradle wrapper version, the Sonar plugin and configuration file, the JaCoCo configuration and its coverage-gate exclusions, and the `fatJar` `configurations.compile` line.** All owned by a later run.
- **The identity rename itself.** You encode the token pairs in the filter; you do not rename anything.
- **Committed binaries and junk files.** Do not delete any, and do not rewrite history: no `git filter-repo`, no `git rebase --root`, no force-push.
- **The build script, beyond the narrow secondary surface declared in §5.** You may add declarations that register your new tests and wire the golden-file comparison, and declare the corpus and golden directories as task inputs and outputs. You may **not** migrate the wrapper, change the dependency block, touch the Sonar or JaCoCo configuration, touch the `fatJar` assembly, alter any pre-existing task's behaviour, delete any existing line, or "improve" anything you notice. Every one of those belongs to a later run.

### 5. Your labels, and both discipline rule sets verbatim

#### Governing constraints and their provenance — read this before the rule sets below

**There are no user-specified rules on this project, and you must not go looking for any.** The rules mechanism was queried and returned exactly:

```text
No user rules provided.
```

The project's on-disk rules document is **empty**. There is therefore **no rule to cite by name, none to summarize, and none to invent**. That mechanism is where the full text of every rule *would* live, and on this project it contains nothing.

**The absence is not permission to lower the bar.** Enterprise-standard best practice applies in its place, and this run is held to it: evidence over assertion with a `path:locator` citation behind every claim about existing behaviour · no fabricated version pins — state the constraint a version must satisfy and resolve the pin at execution time, recording it in your handoff · reproducibility as a first-class requirement · the smallest verifiable diff · decide, then record the rejected alternative · documentation is never proof, and where a document and an executed result disagree the executed result wins · supply-chain and licence hygiene as shipping requirements · automation over convention, because anything depending on human memory is a latent defect.

**The provenance distinction that matters operationally, stated because it changes what you should do when you cannot find something.** The dense body of binding constraints this run operates under — the backward-compatibility contract, the "do not touch" set, the two-axis change discipline, the mechanical definition of green, the evidence standard, the environment-authority rule, the four declared non-goals and the scope boundary — are **prompt-level constraints, not user-specified rules**. They are **not retrievable through the rules mechanism**: a session that goes looking for a rules document will find nothing and must not conclude that the constraints do not exist. That is precisely why they are written out in full inside this prompt rather than referenced, and why the duplication in this prompt is deliberate and must not be "optimized" away. **This prompt is self-contained by design: everything binding on you is in it.**

#### Your labels, and both rule sets

**Your labels: `S1·PRES`.**
**Your labels: `S1·PRES`, plus a declared secondary surface `S2·PRES`.**

A run whose two labels are not both stated has no oracle and must not start. Yours are both stated, on both surfaces. **Do not collapse the axes**: *which code you may touch* and *whether observable behaviour may change* are independent, and collapsing them produces a run whose scope contradicts its own discipline.

**Both rule sets below are reproduced verbatim from the governing constraint text — character for character, including its American spelling of "behavior", its `·` separators and its parenthesised definitions. They are inside a fenced block so that nothing can reflow or reword them, and so that "verbatim" is mechanically checkable rather than a claim. Do not paraphrase them, do not re-order them, and do not carry a reworded copy into any document you produce.** Both axes are given in full — all four surface values and all three discipline values — so you can see where `S1` and `PRES` sit among them:

```text
Permitted surface: S1 non-production (tests, golden files, corpus, harness, CI) · S2 peripheral production (build scripts, wrapper, packaging, dependency declarations, identifiers, file layout, headers — no parsing, interpretation or generation logic, and no passing-through improvements) · S3 enumerated core (named classes and methods on the parse/interpret/generate path, enumerated before the run starts; anything outside the enumeration is S2-disciplined within that run) · S4 new code (new classes and packages isolated from the existing large classes, plus enumerated touches to shared code where unavoidable)

Behavior discipline: PRES preserving (any observable change is a defect) · CORR corrective (specific changes are the objective, each itemized before the run starts; an unitemized golden-file change is a defect even if it looks like an improvement; may add a config key whose default reproduces current behavior, but no new DSL syntax, CLI flag or output format) · ADD additive (new behavior required, landing behind new syntax and new keys defaulting to current behavior, shipping with tests and documentation in the same change)
```

**Applied to you — this paragraph is application, not rule text; the verbatim rule text is the fenced block above and nothing outside it.** `S1` means tests, golden files, the corpus, the harness and CI configuration are your surface, and no production code is — and `S1` explicitly includes CI, which is why the GnuCOBOL install step is yours. `PRES` means **any observable change in the tool's behaviour is a defect**. You are capturing behaviour, not improving it. The one apparent exception is not one: regenerating `expected-output.txt` changes a *recorded expectation* to match the tool's *actual current* behaviour, which is the definition of characterization rather than a behaviour change — and the previous run's handoff records the measured delta so the regeneration is auditable.

**Declared secondary surface: `S2·PRES`, bounded to the build script and nothing else.** `INFERENCE` — the primary surface cannot cover this run's own work, so declaring it is mandatory rather than optional; reasoning: registering a new test source set and wiring a golden-file comparison task requires editing `build.gradle`, which the surface vocabulary classifies as `S2` peripheral production, and a run that edits `S2` under an `S1`-only label has no oracle for the edit it actually made. **Exactly what the secondary surface permits:** adding declarations to `build.gradle` that (i) register the new test classes and the golden-file comparison so Gradle executes them, and (ii) declare the corpus and golden directories as task inputs and outputs. **Nothing else.** No dependency change, no wrapper change, no plugin change, no JaCoCo or Sonar change, no `fatJar` change, no modification to the behaviour of any pre-existing task. **Its own oracle, checkable independently of the `S1` oracle:** `git diff <start-commit> HEAD -- build.gradle` consists solely of added lines registering test execution or golden comparison — no line is deleted and no pre-existing line is modified — and `./gradlew tasks` reports every task that existed at the start commit with the same name and the same `dependsOn` set. The discipline stays `PRES` on both surfaces: the build script gains the ability to run new tests and gains nothing else.

**Second disciplines must be declared in advance** with their own oracle. Two *undeclared* disciplines are the defect. Note that your secondary declaration is a second **surface**, not a second discipline: the discipline is `PRES` on both, so nothing about the tool's observable behaviour may change on either. If you find you cannot capture behaviour without changing it, **stop and report** rather than proceeding.

**The safety net, and what you may not do to it.** 457 tests across 34 named test classes with zero failures. Existing assertions must **not** be weakened, deleted, disabled, renamed or narrowed to make a change pass. The single mechanism by which a pre-existing assertion may change is an itemized corrective task naming the class and method **before** the run starts, recording old and new expected values in the handoff with the motivating issue number. **You have no such itemized task. Therefore you may not alter any pre-existing assertion at all.** You may add as many new ones as you need.

### 6. System boundaries and the backward-compatibility contract, as they apply to you

**The architecture is a precompiler and must remain one.** The tool merges the program under test with test suites and test-case management logic to produce a copy with test code embedded, then compiles and executes the copy. Do not replace this with a runtime interpreter, a language-server service or a compiler plugin, and do not rewrite anything in another language.

**The compatibility contract — the surfaces that must not break.** The `.cut` test DSL, in every construct any existing suite uses; `config.properties` keys, their names, semantics and defaults, such that a `0.2.19` configuration file still works; the CLI flag set and semantics; the `txt`/`directOutput` result format, which is the default and works — `[config.properties:L141]` `test.results.format = txt` and `[config.properties:L151]` `test.results.format.style = directOutput`; and the invocation shape, a plain `java -jar` call from a shell script or container. Real users depend on all of these and all of them are pinned to an archived upstream.

**As applied to you, your relationship to that contract is unusual and important: you are building the instrument that will detect violations of it in every later run.** You touch none of those surfaces yourself. Your corpus and golden files are what make "any observable change is a defect" a measurement rather than an opinion, for four subsequent runs. That is why capturing current behaviour *including its defects* is correct: a golden file that records a wrong-but-current output is what later proves a change was intentional.

**One consequence to design for deliberately.** Your corpus must cover the mock constructs, because those are the most intricate part of the DSL and three later runs extend them. The constructs to ensure are covered include global and local mock scope with local overriding global, signature matching including `USING` argument lists, and the whole verification family — `HAPPENED ONCE`, `HAPPENED <n> TIMES` and `NEVER HAPPENED`. The relevant vocabulary is at `[src/main/java/org/openmainframeproject/cobolcheck/services/Constants.java:L70-L81]`. Note that `MOCK`, `MOCKPARA` and `MOCKTEST` are three separate programs and only `MOCKTEST` is currently exercised end-to-end.

### 7. Acceptance criteria — mechanically checkable

**The definition of green, verbatim.** Green means **all** of: `clean test` reports **457 or more** tests with zero failures · the approval harness **compiled and executed a non-zero number of COBOL programs** · `actual-output.txt` is **non-empty** · the comparison against the approved baseline **genuinely matched**. The build is currently green for the wrong reason — vacuously, on a zero-byte `actual-output.txt` with zero COBOL programs compiled. **An agent that satisfies the invariant by leaving the harness broken has failed, not succeeded. Any run reporting green must state the executed program count.**

**You must end fully GREEN under all four conditions. You inherit a red approval comparison and your job includes clearing it honestly — by regenerating the baseline from real output, never by weakening the comparison.**

| # | Criterion | How it is checked | Required result |
| --- | --- | --- | --- |
| 1 | Unit suite green | `JAVA_HOME=$JDK11_HOME ./gradlew clean test`, then read the JUnit XML under `build/test-results/test/` | **457 or more tests, 0 failures, 0 errors** |
| 2 | The harness runs COBOL, and you say how much | Count `About to launch process` occurrences | **Non-zero, stated explicitly.** It must be **greater than the 5 inherited**, because you are widening the harness |
| 3 | Real output produced | `wc -c actual-output.txt` | **Non-zero** |
| 4 | The comparison genuinely matches | `JAVA_HOME=$JDK11_HOME ./gradlew clean approvalTest` | **PASSES**, against a baseline regenerated from real output |
| 5 | Golden coverage is complete | Compare the golden-file set against the 15 programs in `src/main/cobol/` | **Every** corpus program has a golden file; any program deliberately excluded is named with its reason |
| 6 | The normalization filter is correct **and** minimal | Run the filter's own test | It normalizes all four token pairs in both directions **and** provably alters nothing else |
| 7 | CI can compile COBOL | Inspect `.github/workflows/VerifyAction.yml`; then `grep -rni -e cobc -e gnucobol .github/`. **Use the two `-e` forms exactly as written.** A single alternation pattern is unsafe here: a `\|` copied out of this table is a *literal pipe* to `grep -E`, so the search silently returns nothing and the criterion appears to fail even after a correct workflow change | The search now **returns matches**, and the approval leg has a compiler available on every matrix leg where that is achievable; any leg where it is not is named with its reason |
| 8 | Determinism proven, not assumed | Run the full capture **twice** on a cold tree (`rm -rf bin temp testruns actual-output.txt` between runs) and diff the two outputs | **Byte-identical after normalization.** If not, you have an unrecorded environment dependency — find it before proceeding |
| 9 | No production code changed | `git diff --name-status <start-commit> HEAD -- src/main/java` | **Empty** |
| 10 | No pre-existing assertion altered | Review `git diff <start-commit> HEAD -- src/test/java` and confirm every change is an addition | No modification, rename, deletion or narrowing of any existing test |
| 11 | Copybook provenance recorded | Read your own handoff | Every corpus program exercising `DFHEIBLK.CPY` or `SQLCA.cpy` is named, with the corpus-local path it takes the file from |
| 12 | The declared `S2·PRES` secondary surface stayed inside its bounds | `git diff <start-commit> HEAD -- build.gradle` | **Added lines only** — every added line registers test execution, wires the golden comparison, or declares a corpus/golden directory as a task input or output. **No deleted line and no modified pre-existing line.** No dependency, wrapper, plugin, JaCoCo, Sonar or `fatJar` change appears in the diff |
| 13 | No pre-existing task's behaviour changed | `./gradlew tasks` at the start commit and at HEAD, compared | Every task present at the start commit is still present with the **same name** and the **same `dependsOn` set**. New tasks may be added; none may be renamed, removed or re-wired |

**Criterion 8 deserves emphasis.** A golden-file set that has never been shown to reproduce is not a safety net; it is a source of false alarms that later runs will learn to ignore. Running the capture twice is the cheapest available proof that the three environment facts — the compiler version, the single `-D_FORTIFY_SOURCE`, and `core.autocrlf=false` — are sufficient. If the two captures differ, the difference **is** the finding.

**Stop and report** rather than continuing if: `RUN-1A-HANDOFF.md` is missing or missing any of its sections 0, 2, 3, 5 or 6; the tag `run-1a-approval-harness-repair` does not exist; your `cobc --version` differs from the anchor above or from the one in that handoff; `clean test` is not green at entry; you cannot capture behaviour without changing it; or you cannot proceed without altering a pre-existing assertion.

### 8. The handoff artifact you must produce

Create **`RUN-1B-HANDOFF.md`** at the repository root and commit it as part of this run. The next run reads it and reconciles against it, so a missing section blocks that run. **Your toolchain anchor becomes that run's entry precondition** — it is the single most consequential thing you record.

```markdown
# RUN-1B-HANDOFF

## 0. Standing environment precondition
The first command of this session was:
    . /etc/profile.d/blitzy-cobolcheck-env.sh
Nothing is on PATH in a non-login shell. A probe that fails BEFORE sourcing it is a
probe error, never a finding. State whether sourcing succeeded and paste any deviation.

## 1. Identity
Run: 1b — Characterization safety net
Labels: S1·PRES
Declared secondary surface: S2·PRES, bounded to build.gradle test-registration and
  golden-comparison wiring only.  Paste `git diff <start> HEAD -- build.gradle` here and
  confirm it consists solely of ADDED registration lines with no deletion and no
  modification of a pre-existing line.  Confirm `./gradlew tasks` still reports every
  pre-existing task with the same name and the same dependsOn set.
Second discipline declared in advance: none (the discipline is PRES on both surfaces)
Starting commit hash:                     <full 40-character SHA — mandatory>
Ending commit hash:                       <full 40-character SHA — mandatory>
Tag applied:                              run-1b-characterization
BLOCKED-BY tag started from: run-1a-approval-harness-repair

All three identity fields above are mandatory and separate, matching the canonical
template in A10.1.  The tag is not a substitute for either hash: the next run's entry
precondition is that its starting commit IS this run's ending commit, and that check is
unperformable if the ending hash was never written down.

## 2. Toolchain anchor   <-- THIS IS AN ENTRY PRECONDITION FOR THE NEXT RUN
`cobc --version` verbatim, all lines.
COB_CFLAGS verbatim (must contain exactly one -D_FORTIFY_SOURCE).
`git config --get core.autocrlf` (must be false). Daemon JDK, test JDK(s), Gradle version.
Does this anchor match RUN-1A-HANDOFF.md's anchor?  YES / NO
If NO: that is a finding. Stop and report; every golden file captured here is suspect.

## 3. Green status, stated mechanically
`clean test`: <n> tests, <n> failures, <n> skipped        (must be >= 457 with 0 failures)
COBOL programs compiled and EXECUTED by the harness: <n>  (REQUIRED. Must exceed the
  5 inherited from Run 1a. State which programs, by name.)
`actual-output.txt`: <bytes> bytes, <lines> lines          (must be non-empty)
Comparison against the approved baseline: MATCHED        (must be MATCHED)
Overall: GREEN
Determinism: two cold-tree captures diffed byte-identical after normalization?  YES / NO

## 4. Changes by file, with reasons
| File | Change | Why | Itemized in advance? |

## 5. Golden-file status, itemized
One row per golden file: its program, its size, and whether it records a known defect.
State explicitly which golden files capture defective current behaviour and what the
defect is, so a later run cannot mistake an intentional repair for a regression.
Also state the disposition of the byte-identical second baseline at
src/test/approvalTest/expected-output.txt.

## 6. Architecture decisions later runs must respect
- The four identifier token pairs the normalization filter canonicalizes, verbatim.
- The filter's location and the guarantee its own test provides.
- The corpus layout, and the corpus-local copybook rule with the reason behind it.
- The verified/provisional register: its location, its schema, and the append-only
  discipline with one owning run per entry.

## 7. New or changed public surface
Expected: NONE. The regenerated baseline is a recorded expectation, not public surface.

## 8. Verified / provisional register delta
| Claim | verified / provisional / UNKNOWN | Source (file:line, or documented GnuCOBOL -std=ibm, or none) | Corpus program covering it |
Seed only what the corpus actually demonstrates. Do not mark anything verified that no
corpus program covers. Do not populate any SQLCODE, FILE STATUS or RESP table from recall.

## 9. Issues and pull requests
Issue #329 "Fix approval tests": state whether it is now closed and paste the evidence.
Issue #127 (internationalization test should reference the resource name): state disposition.
Issues #334 and #185: both claim prior fixes in the changelog. State what your golden files
  actually show, and whether each should be closed with that evidence or itemized for a
  later run. Documentation is not evidence.

## 10. Pre-existing assertions changed
Expected: NONE. Paste the evidence that `src/test/java` contains additions only.

## 11. Anything contradicting the carried-forward ground truth
State it explicitly with evidence. Do not quietly correct it. Specifically confirm or refute:
the 15/41/16/23/14/5 corpus counts; the 25 TestSuite and 171 TestCase declarations; that
nine named programs are unexercised; that FDTEST01.CUT is an orphan; that `-p FILECOPY`
raises an unhandled FileNotFoundException; the +98 line / +14,933 byte staleness.
Three of those figures were themselves corrections to the ground truth supplied to this
run — 41 copy fixtures rather than 40, 25 TestSuite rather than 22, 171 TestCase rather
than 159 — so report your own measurement of each rather than echoing the list.

## 12. Performance envelope
Command used; suite count; `clean test` measured before and after; elapsed per suite;
total elapsed as a multiple of the pre-change baseline. Widening the harness will move
this, so record it here as the first datapoint later runs compare against.

## 13. Deferred into the next run
Each with the reason it was deferred rather than dropped. At minimum, carry forward every
defect you characterized but did not repair, naming the golden file that records each.

## 14. Reconciliation performed against RUN-1A-HANDOFF.md
- Confirmed the tag `run-1a-approval-harness-repair` and started from it.
- Anchor comparison result.
- Inherited green/red status observed, and confirmation that the inherited red was expected.
- Inherited executed-program count observed (expected 5).
- Every item from that document's section 13, with its disposition in this run.
- Every architecture decision from its section 6, and how this run respected it.
- Any discrepancy between its claims and this run's observations.
```

### 9. Your output contract

- **Commit to this repository.** Your in-repo changes plus `RUN-1B-HANDOFF.md`, and nothing else.
- **Tag the final commit `run-1b-characterization`.** The tag is immutable once created: never move, delete or re-point it, and never rewrite the history it names.
- **Do not commit build output or working-tree churn.** Restore the two tracked archives under `build/` and the file modes of `approvaltest` and `scripts/linux_gnucobol_run_tests` to their committed states before committing. Do not commit `temp/`, `testruns/` or `bin/`. `actual-output.txt` is build output; the **approved baseline** `expected-output.txt` is the artifact you commit.
- **Do not create any document other than `RUN-1B-HANDOFF.md`.** No status reports, no progress summaries, no setup guides, no validation guides. The corpus, the golden files, the filter and the register are code and data, not documents.
- **No temporal planning anywhere** in your handoff — no estimates, no durations, no dates. Sequencing is expressed only as `BLOCKED-BY`. The performance measurements in section 12 are measurements of work performed, which is different and is required.
- **No outreach of any kind.** Do not contact anyone and do not propose doing so. This is a solo-maintainer project with agent support; optimize for automation and minimal human decision points.
- **Label every claim in your handoff** `FACT` with a `file:line` or a retrieved URL, `INFERENCE` with its reasoning stated, or `UNKNOWN`. Never silently upgrade an inference to a fact.
- **Verify and record; never install.** If a tool is present, use it and record its version. If it is absent, record the omission and label the dependent finding `UNKNOWN`. Self-provisioning is not available to you. This applies to `cobc`, to archive tools, to vulnerability scanners and to history-rewriting tools alike.
- **Do not populate any SQLCODE, FILE STATUS or RESP table from recall**, and do not request access to proprietary vendor documentation. The reachable sources, in order, are the in-repo authority first — `EIBResponseTable` with its 76 `put(` insertions resolving to 75 unique condition names at `[src/main/java/org/openmainframeproject/cobolcheck/services/cobolLogic/EIBResponseTable.java:L43-L118]`, and `src/main/cobol/copy/SQLCA.cpy` for the SQLCA field layout — and documented GnuCOBOL `-std=ibm` semantics second, labelled `INFERENCE`. Anything covered by neither is `UNKNOWN`. This is the failure mode this project ranks above every other quality attribute.
- **Read the source rather than the documentation.** Where a document and an executed result disagree, the executed result wins. The upstream wiki documents mock syntax that does not exist: `INPUT-FILE`, `ON OPEN`, `ON READ`, `DATASET` and `CONDITION` return **zero** occurrences across `Keywords` and `Constants`. Treat every document, including this prompt, as claims to be checked against the code.

---
---

# DELIVERABLE C — PROVISIONAL SCOPE SKETCHES, RUNS 2 THROUGH 9

**All eight sketches below are PROVISIONAL and are deliberately not full prompts.**

Each subsequent run's full prompt is generated **after the prior run lands**, because its content depends on what that run actually built — which files moved, which golden files changed, which deferrals it passed forward, and what its handoff recorded as decisions later runs must respect. Writing them now would bake in guesses the work will invalidate. Concretely: Run 3's enumerated surface depends on what Run 2's rename actually renamed; Runs 5, 6 and 7 depend on the exact shape of the extension point Run 4 extracted; and Run 8's mutation corpus depends on which programs Run 1b's characterization actually covered.

What each sketch fixes is the **scope, the labels, the entry precondition, the oracle, the primary risk, the key open question and the architecture decisions it implements** — the parts that follow from the plan rather than from the work. Everything else is expected to change.

---

## C1 — Run 2: Modernization · `S2·PRES` **plus one declared second discipline `S2·CORR`**

**Scope.** The Gradle wrapper from 6.9.4 to the 9.x line, with the daemon on JDK 17 or higher and a toolchain pinning *shipped* bytecode to Java 8, preserving `[build.gradle:L67-L68]`'s `VERSION_1_8` intent by a different mechanism. The dependency changes: drop `org.mockito:mockito-inline` rather than bumping it, because the JDK 21 failure originates in `InlineByteBuddyMockMaker`'s static initializer and the inline mock maker became the default in the successor line; move `org.mockito:mockito-junit-jupiter` from 3.6.28 onto the 5.x line at a version whose bundled Byte Buddy accepts class-file version 65; align the JUnit skew between `[build.gradle:L102]`'s 5.6.1 and `[build.gradle:L103]`'s 5.7.0 by adopting a JUnit bill-of-materials rather than pinning two numbers that drift apart again; bump `jacoco`'s `toolVersion` from `[build.gradle:L27]`'s 0.8.6 to a release supporting the chosen toolchain's class-file version; remove `javax.xml.bind:jaxb-api:2.3.0` at `[build.gradle:L99]` as redundant against the Jakarta artifact and as the copyleft-adjacent artifact **declared and resolved** on the production runtime classpath — note that it is **not** currently packaged into any committed archive, because `configurations.compile` resolves empty, so the removal is dependency and licence hygiene rather than the removal of shipped bytecode, and it becomes hygiene that *matters* the moment the `runtimeClasspath` change below starts packaging the runtime closure; and replace `configurations.compile` with `runtimeClasspath` at `[build.gradle:L138]`, which is proven necessary because `./gradlew dependencies --configuration compile` reports `No dependencies`. **Version pins for the Mockito line, the JUnit bill-of-materials and JaCoCo are deliberately not stated here** — only the constraint each must satisfy — and the resolved pin must be recorded in the handoff rather than guessed. The one pin that *is* fixed, because it was verified: the SBOM plugin `org.cyclonedx.bom` at **3.2.4**, configured with `includeConfigs = ["runtimeClasspath"]` and outputs at `build/reports/sbom/bom.json` and `build/reports/sbom/bom.xml`. Remove all three Sonar surfaces — the plugin at `[build.gradle:L5]`, the configuration block at `[build.gradle:L17-L24]`, and the standalone `sonar-project.properties` — plus the `sonar-scan` script. Wire the coverage gate into `check`, which it currently is not. Apply the identity rename by idempotent script. Delete the committed binaries and junk by ordinary commit, **retaining `expected-output.txt` and `gradle/wrapper/gradle-wrapper.jar`**. Delete the planning run's own artifacts from the working tree in that same ordinary commit — `PROGRAM-PLAN.md` and all twelve paths under `upstream-harvest/`, which are tracked contrary to the planning run's own boundary and which nothing in the product reads — **gated on written human confirmation that they are preserved outside this repository**, since the upstream capture is one-time and non-repeatable; absent that confirmation this half alone defers and the rest of the run proceeds. Fill the governance stubs rather than deleting them: `THIRD_PARTY.md` (15 bytes, "Not applicable."), `SECURITY.md` (23 bytes, "TBD"), `notice.txt` (1 byte), `FAQ.md` (31 bytes) and `COMMITTERS.md` (50 bytes), and supply the root licence file that two vendored typing files reference and which does not exist. Create the architecture-decision-record tree from nothing at the fixed path `docs/adr/NNNN-<slug>.md`, since no `doc`, `docs`, `wiki` or `adr` directory exists anywhere. Write the first release workflow from nothing, since `.github/` holds exactly three files and none publishes a JAR. Unify the three npm manifests, move development dependencies out of the server manifest's `dependencies` block, raise the root `engines.vscode` floor to match the client's actual `^1.52.0`, commit lockfiles by removing `[.gitignore:L28]`, and replace `npm install` with `npm ci` — **manifest hygiene only, none of which changes what the extension does.** No upstream pull request is adopted in this run: the canonical routing matrix in Deliverable A #5 assigns **zero** of the eight here, and in particular pull request #336's infinite-loop timeout goes to Run 3, because a run that terminates instead of hanging is an observable change and this run's discipline forbids one.

**Labels.** `S2·PRES` for the run as a whole, **plus one declared second discipline `S2·CORR`** covering exactly one itemized observable change and nothing else: replacing `configurations.compile` with `runtimeClasspath` at `[build.gradle:L138]`, whose consequence is that the `xml` result format stops throwing. Both label pairs are stated before the run starts, which is what makes the second discipline declared rather than a defect; two undeclared disciplines are the defect. The `html` repair, the exit-status repair, the copybook resolution change and every parsing fix stay in Run 3.

**Entry precondition.** Tag `run-1b-characterization`; full green; the golden-file set byte-stable under the normalization filter; `cobc --version` re-read at the start **matching** the anchor in `RUN-1B-HANDOFF.md`; and a Marketplace publisher personal access token issued under the new `blitzy` publisher, exposed as the correctly-spelled repository secret **`VSCE_PAT`** and superseding the misspelled `VS_CODE_EXTENTION_SECRET` at `[.github/workflows/DeployExtension.yml:L27]` — read only at publish time, never at Gradle configuration time. It is the only human provisioning step in the programme. If it is absent, the extension-publish job is skipped with an explicit message naming it and the handoff records the extension as unpublished; the JAR release is **not** gated on it.

**Success oracle — two oracles, because the run carries two disciplines.** **For the `S2·PRES` primary:** every golden file **byte-identical** after normalization. `clean test` green at 457 or more with zero failures on **both** the JDK 11 and JDK 21 legs — the JDK 21 leg turning green is this run's headline result. The harness compiles and executes a non-zero, stated number of COBOL programs. The fat jar contains the runtime dependencies. The SBOM exists at the two named paths. A release workflow produces a downloadable JAR. The rename commit's diff over `src/test/` is mechanically verifiable as matching only the rename script's token set. **For the extension publish, which depends on the one credential this programme adds:** the workflow reads `VSCE_PAT` — the replacement for the misspelled `VS_CODE_EXTENTION_SECRET` at `[.github/workflows/DeployExtension.yml:L27]` — at **publish time only**, never at Gradle configuration time, and a publish under the new publisher identity resolves to the new extension ID. **If the secret is absent the run does not fail and does not silently appear to succeed:** the publish step is skipped, the skip is stated explicitly in the handoff's public-surface section as an unshipped surface, and every other exit criterion still has to hold — so the run can complete without the credential, but never while pretending the extension was published. **For the declared `S2·CORR` secondary, scoped to the `xml` result format and to `[build.gradle:L138]` alone, three conditions all required:** (i) the fat jar contains the runtime dependencies, asserted as an archive **entry-name set** comparison before and after rather than by timestamp; (ii) a new test drives the `xml` result format end to end on a corpus program, is **required to fail before the change and pass after it**, and asserts a well-formed JUnit XML document with exit 0 and no throw, where before the change it raised `NoClassDefFoundError: javax/xml/bind/JAXBContext` from `[.../Formatter/Formats/XMLFormat.java:L19]`; (iii) the format name, the style enumeration values and the emitted schema are unchanged; `XMLFormat.java` is not edited at all; the `txt`/`directOutput` default path is byte-identical; and the archive entry-name set is compared before and after so the newly packaged runtime closure is an observed addition. The `html` format is **not** repaired here — it throws for a different reason and belongs to Run 3.

**Primary risk.** A packaging change that golden files cannot see. Golden files record the tool's *output*, not the *artifact's* structure, so a jar that silently stops shipping a resource would pass every golden comparison. The mitigation is an explicit archive-entry assertion: the entry **name set** before and after must be compared, as distinct from entry timestamps, which will legitimately change. This matters because the archives are currently non-reproducible in exactly that way — 13 of 200 entries differ from a rebuild by timestamp alone.

**Key open question.** Does the Groovy 4 runtime embedded in the 9.x line still support the `String.execute()` idiom and the relocated comparison helper? Related and unresolved: the reproducible-archive default fixes file modes at 0644 and directories at 0755, which interacts with the configuration key governing generated-file permissions — and the launch scripts the harness invokes must remain executable.

**ADRs implemented.** ADR-07 (rename identifiers), ADR-08 (reject the history purge), ADR-10 (remove Sonar entirely), ADR-12 in part (wire the existing coverage gate into `check`).

---

## C2 — Run 3: Defect correction and upstream pull-request adoption · `S3·CORR` plus a declared `S2·CORR` and `S1·CORR`

**Scope.** **Nine** itemized behaviour corrections, each declared before the run starts, plus the **seven** upstream pull requests the canonical routing matrix in Deliverable A #5 assigns to this run, with author attribution preserved. The corrections: the exit-status repair, introducing a new distinct nonzero code; the `html` output format, repaired by data-transfer-object wiring rather than by any dependency change, because `[.../Formatter/Formats/HTMLFormat.java:L19]`'s `instanceof String` test raises `IncompatibleClassChangeError` at `[:L26-L27]` deliberately; `COPY ... REPLACING`; informational output leaking past a disabled log level; the unhandled crash when a test glob matches no suite; the `-p FILECOPY` case-resolution failure together with the structural non-equivalence of the two approval harnesses; making the working-storage `EXEC SQL INCLUDE` platform branch express its intent; **(h) the extension's unbounded run loop**, adopting #336's configurable timeout and closing issue #287, which is peripheral-surface within this run and declared as such; and **(i) one pre-existing assertion change**, the only one in the programme's first three runs — `MessagesTest.it_retrieves_a_message_with_substitution_values`, whose transcribed English expected value becomes the same message resolved from its bundle key, motivating issue #127. Adoption, reproducing the canonical routing matrix exactly and accounting for all eight pull requests so none goes missing:

| PR | Owning run | Disposition |
| --- | --- | --- |
| **#338** | **Run 1a, not this run** | Superseded by the approval-harness repair; its central change is already in the fork and insufficient alone |
| **#336** | Run 3 | Intent re-applied on the extension manifests as Run 2 leaves them, as an itemized behaviour correction — a run that terminates instead of hanging is observable, which Run 2's preserving discipline forbids. Closes #287 |
| **#330** | Run 3 | Not adopted as authored; intent re-implemented, expanding **in place** and leaving `ExpanderTest` intact, honouring both review objections |
| **#337** | Run 3 | Superseded by #408; fixture and any uncovered test case harvested, neither the jar edit nor the `InterpreterController` churn taken |
| **#408** | Run 3 | Adopted, minus its committed `testruns/testResults.txt`. Closes #335 |
| **#409** | Run 3 | Adopted. Closes #339, advances #306 and #151 |
| **#410** | Run 3 | Superseded by #411 for the code; `FileCopy.cbl` and `FSTEST.cpy` fixtures harvested |
| **#411** | Run 3 | Adopted under a golden file. Advances #206 |

So this run owns **seven** of the eight; only #338 belongs elsewhere, superseded by Run 1a whose repair is its entire substance.

**Labels.** `S3·CORR` for the enumerated core, fully package-qualified in the run's own prompt, plus two declared out-of-enumeration touches: **`S2·CORR`** for `vs-code-extension/**` (correction (h)) and **`S1·CORR`** for the one test source carrying correction (i). Neither is discovered mid-run; both are declared here because work outside an `S3` enumeration is preserving by default and may be corrective only where a defect must genuinely be fixed to land the run.

**Entry precondition.** Tag `run-2-modernization`; full green; golden files byte-stable; anchor matches.

**Success oracle.** Every itemized correction has a test that fails before and passes after. **Every** golden-file change is itemized in advance; an unitemized change is a defect even if it looks like an improvement. `clean test` green with zero failures. The harness compiles and executes a non-zero, stated number of COBOL programs. `src/test/cobol/RETURNCODE/ReturnCode-4.cut` now produces a distinct nonzero **process** exit code — the oracle that already exists in the repository, exercised by nothing today. Per-pull-request disposition recorded with its evidence. **For the declared `S2·CORR` extension touch:** a run that would previously not terminate now terminates at the configured timeout with a diagnostic naming it, the timeout is a new key whose default reproduces current behaviour for any run that completes, and no other extension behaviour changes. **For the declared `S1·CORR` assertion touch:** exactly one assertion changes, in the named class and method; the handoff §10 table carries its old value, its new value and issue #127; and `git diff <start-commit> HEAD -- src/test/java` shows no other modification, rename, deletion or narrowing.

**Primary risk.** The `COPY ... REPLACING` repair spreading beyond its enumeration. The defect has two halves in two files — hardcoded token positions at `[.../features/interpreter/LineRepository.java:L110-L113]` behind a suspect `||` at `L111-L112` where either `REPLACING` or `BY` alone satisfies the guard, and a regex `replaceAll` at `[.../features/interpreter/CopybookExpander.java:L72]` and `[:L79]` that treats pseudo-text as a regular expression. The mitigation is to reuse the tokenizer that already exists rather than write a parser: `services/cobolLogic/replace/` holds six classes with `LEADING`/`TRAILING` and multi-pair support, backed by five test classes, and it is live on the generation path. **Established from its own javadoc rather than assumed: it implements the COBOL `REPLACE` *statement*, not `COPY ... REPLACING`.** The two directives share identical pseudo-text lexis, which is what makes reuse sound — but the distinction must stay explicit in the run's prompt so nobody assumes a drop-in fit. A secondary risk: `LineRepositoryTest.java` is an 18-line empty shell with zero test methods, so one of the two files being repaired has no existing coverage at all.

**Key open question.** What exact value should the new failure exit code take, and does any consumer already attach meaning to a nonzero value other than 8? The plan's position is that a third distinct value is required because `--help` and `--version` already exit 8, so reusing it would make a failed test indistinguishable from a usage message.

**ADRs implemented.** ADR-11 (the exit-status repair as the single deliberate break), ADR-04 in part (`SQLCA.cpy` resolution routed here as branch (ii) with an additive key defaulting to current behaviour).

---

## C3 — Run 4: Stub-to-mock architecture · `S3·PRES`

**Scope.** Extract the stub-emission decision behind a `StubStrategy` interface at a seam of **three methods across two packages**: the predicate `org.openmainframeproject.cobolcheck.services.cobolLogic.Interpreter.shouldLineBeStubbed(CobolLine, State)` at `[.../services/cobolLogic/Interpreter.java:L272-L294]`, emission site 1 `org.openmainframeproject.cobolcheck.features.interpreter.InterpreterController.shouldCurrentLineBeStubbed()` at `[.../features/interpreter/InterpreterController.java:L145-L158]` emitting at `L149-L150`, and emission site 2 `...InterpreterController.shouldCurrentStatementBeStubbed()` at `[:L160-L172]` emitting identically at `L165-L166`. Emission site 2 is the multi-line site and must be in the enumeration or the generalized hook still emits a bare `CONTINUE` for exactly the statements the three later runs handle. Ship **one** strategy, emitting the existing `.`/`CONTINUE` pair. Absorb the parser refactor the extraction requires, addressing the two duplicate refactoring issues without enlarging the three largest classes — 60,602, 38,427 and 26,737 bytes respectively.

**Entry precondition.** Tag `run-3-bugfix-pr-adoption`; full green; golden files byte-stable; anchor matches.

**Success oracle.** **Every golden file byte-identical** after normalization — this run changes structure only. `clean test` green with zero failures. The harness compiles and executes a non-zero, stated number of COBOL programs. A new test demonstrates that an alternative strategy implementation is reachable at **all three** seam points, including the multi-line one.

**Primary risk.** Silently normalising the structural asymmetry in emission site 2. It iterates `reader.getCurrentStatement()` at `L161` but tests `endsInPeriod(reader.getCurrentLine())` at `L164`, and its `return true` at `L168` sits **outside** the `PROCEDURE_DIVISION` guard at `L163`, so it reports "stubbed" while emitting nothing. Both properties determine which lines reach emission, so changing either moves golden files — and this run's whole oracle is that none move. The asymmetry looks like a defect and the temptation to fix it in passing is the single most likely way this run fails.

**Settled by ADR-02, not reopened here.** The strategy is keyed by **statement kind** — batch file I/O, `CALL`, `EXEC SQL`, `EXEC CICS` — because the predicate already discriminates exactly those and nothing else, so keying on them is a pure refactor; a mock-registration lookup was considered and rejected, because it would make the interpreter consult the mock repository at a point where it does not today, inside the one run whose whole oracle is that no golden file moves. This run **implements** that decision and records it in its handoff §6; it does not revisit it.

**Key open question.** How does a strategy, once selected by statement kind, choose among several registered mocks of that *same* kind at run time — by resource name, by signature, by declaration order, or through the existing scope rules that already let a local mock override a global one? This is a question strictly *inside* one strategy rather than about the seam, so it neither affects nor reopens ADR-02. It is settled by the first resource-mocking run and inherited by the other two, and this run's job is only to ensure the seam hands the strategy enough context — the matched statement and its kind — for any of those four answers to remain implementable later.

**ADRs implemented.** ADR-02 (the `StubStrategy` seam), ADR-01 (retain the precompiler model, since this is the run most tempted to violate it), ADR-06 in part (the boundary the generator will later respect).

---

## C4 — Run 5: File I/O and VSAM mocking · `S4·ADD`

**Scope.** A new package for file and VSAM mock types, consuming the strategy seam from the preceding run and registering a `FILE`-typed strategy so a stubbed batch I/O verb dispatches to a mock instead of emitting `CONTINUE`. New `.cut` syntax added **additively** as a new `<TYPE>` value inside the existing `MOCK <TYPE> ... END-MOCK` grammar, reusing the existing verification family unchanged. New configuration keys default to current behaviour. Tests and documentation ship in the same change. **Output surface, declared rather than implied:** if this run adds the first `provisional` register entry it also introduces the opt-in key `test.results.annotate.provisional`, default `false`, together with the annotation schema fixed in A7.5 and golden files for **both** settings — the `false` set must match the inherited golden files byte for byte, and the `true` set is new files rather than modified ones. If the key already exists it is reused unchanged and only the `true`-setting golden files grow. The runtime data layout is not invented: `[src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY:L96]` already declares `==UT==MOCK-FILE VALUE 'FILE'` and `[:L104]` already declares the `==UT==MOCK-FILE-DATA REDEFINES` overlay.

**Entry precondition.** Tag `run-4-stub-mock-architecture`; full green; anchor matches.

**Success oracle.** With `test.results.annotate.provisional` at its `false` default, **all pre-existing golden files byte-identical** — the annotation must not be observable to a user who has not opted in. Then: a corpus program whose read loop **terminates** under a mocked file — proven by a suite that fails or hangs without the mock and passes with it. That is the falsifiable core: today a stubbed `READ` becomes `CONTINUE`, so a read loop cannot reach end-of-file and either never terminates or terminates for the wrong reason. All pre-existing golden files byte-identical. `clean test` green. Executed program count stated. The verified/provisional register updated with only what the corpus demonstrates.

**Primary risk.** Shipping `FILE STATUS` values that no reachable source establishes. No in-repo table of file-status values exists — unlike CICS conditions, which have 75 unique tabulated names across 76 insertions. So every status value beyond what a corpus program actually exercises is `UNKNOWN` and the affected behaviour ships **provisional**. Populating them from recall is the failure this programme ranks above all others.

**Key open question.** How does a mocked file interact with the `FD` and file-section handling that a changelog entry claims was already repaired? The characterization run's golden files are the evidence, not the changelog.

**ADRs implemented.** ADR-03 (additive mock syntax keyed to the shipped typed slots), ADR-04 (correctness assurance with the register), ADR-02 (consumes the seam — and, as the **first** resource-mocking run, **settles the intra-strategy question C3 defers to it**: how a strategy selected by statement kind chooses among several registered mocks of that same kind. Whatever it decides — resource name, signature, declaration order, or the existing scope rules — is recorded in this run's handoff §6 as a decision Runs 6 and 7 inherit rather than re-litigate. It does not reopen ADR-02's keying, which is settled).

---

## C5 — Run 6: SQL and DB2 mocking · `S4·ADD`

**Scope.** A new package for SQL mock types, registering a `SQL`-typed strategy for `EXEC SQL` statements — including **multi-line** ones, which reach emission site 2 of the seam. Support for asserting `SQLCODE`, populating SQLCA fields, and a cursor fetch sequence. Additive syntax and additive configuration keys only. **Output surface, declared rather than implied:** if this run adds the first `provisional` register entry it also introduces the opt-in key `test.results.annotate.provisional`, default `false`, together with the annotation schema fixed in A7.5 and golden files for **both** settings — the `false` set must match the inherited golden files byte for byte, and the `true` set is new files rather than modified ones. If the key already exists it is reused unchanged and only the `true`-setting golden files grow. `[src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY:L99]` already declares `==UT==MOCK-SQL VALUE 'SQL'` and `[:L115]` the `==UT==MOCK-SQL-DATA REDEFINES` overlay, so the data layout is honoured rather than invented. **The full path is load-bearing:** two `CCHECKWS.CPY` files are tracked, they are **not** identical (md5 `09dd62ab27c658e67ec5a7510e7222dc` against `1a4f5941a2ce738326e3404da516ed2a`), and the same line number resolves to different content in each. `[copybooks/CCHECKWS.CPY:L99]` is `10  ==UT==MOCK-PARA-DATA REDEFINES ==UT==MOCK-DATA.` — a `PARA` overlay, not the `SQL` condition — so an abbreviated citation resolved against the legacy tree contradicts this prose. Only the packaged resource above is the shipped, classpath-loaded copy.

**Entry precondition.** Tag `run-5-file-io-vsam-mock`; full green; anchor matches.

**Success oracle.** With `test.results.annotate.provisional` at its `false` default, all pre-existing golden files byte-identical. Then: a corpus program on the live `SQLCA.cpy` resolution path asserting a mocked `SQLCODE` and a cursor fetch sequence. `DB2PROG` is the natural subject: `[src/main/cobol/DB2PROG.cbl:L12]` is `EXEC SQL INCLUDE SQLCA  END-EXEC.` and `[:L13]` is `EXEC SQL INCLUDE TEXEM  END-EXEC.`, and it is exercised end-to-end by nothing today. All pre-existing golden files byte-identical. `clean test` green. Executed program count stated. Register updated.

**Primary risk.** Fabricated `SQLCODE` semantics. `src/main/cobol/copy/SQLCA.cpy` is 24 lines carrying the **field layout only** — `SQLCAID`, `SQLCABC`, `SQLCODE PIC S9(9) USAGE BINARY VALUE 0`, `SQLERRM` with `SQLERRML` and `SQLERRMC`, and `SQLERRP` — and **no value semantics whatsoever**. So the run inherits an explicit `BLOCKED-BY` on `SQLCODE` values, and every value beyond what the corpus exercises is `UNKNOWN` with the affected behaviour shipped **provisional**. The specific temptation to refuse: a third-party archive on a fork contains a complete-looking SQL mock implementation with a 16,152-byte design document — which uses only the literals `0`, `100` and `-001` and **cites no source at all**. It is design input, never authority.

**Key open question.** Whether the user supplies `SQLCODE` values explicitly in the `.cut` file — making them the user's responsibility and the tool's guarantee purely about control flow — or whether the tool ships a named set. The plan's position leans to the former, precisely because it is honest without needing a source; the decision belongs to the run and must be recorded, because it becomes permanent public API.

**ADRs implemented.** ADR-03, ADR-04, ADR-02.

---

## C6 — Run 7: CICS mocking · `S4·ADD`

**Scope.** A new package for CICS mock types, registering a `CICS`-typed strategy for `EXEC CICS` statements including multi-line ones, and **wiring the dead condition table**. `org.openmainframeproject.cobolcheck.services.cobolLogic.EIBResponseTable` performs exactly **76** `put(` insertions at `[.../services/cobolLogic/EIBResponseTable.java:L43-L118]` resolving to **75 unique condition names** — `LENGERR` is inserted twice, at `[:L51]` and `[:L84]` — and is referenced nowhere in `src/main/java` outside its own two files, its only external reference being a test class. **Resolving that duplicate is an itemized part of this run and a precondition of calling the table complete**; see the ADR-04 register entry. `[src/main/resources/org/openmainframeproject/cobolcheck/copybooks/CCHECKWS.CPY:L98]` already declares `==UT==MOCK-CICS VALUE 'CICS'` and `[:L110]` the `==UT==MOCK-CICS-DATA REDEFINES` overlay, itself carrying `==UT==MOCK-CICS-KEYWORDS-KEY PIC X(806)` at `[:L111]`. **Cite the full path, never the basename:** two `CCHECKWS.CPY` files are tracked and their contents diverge, and `[copybooks/CCHECKWS.CPY:L98]` is `15  ==UT==MOCK-CICS-KEYWORDS-KEY PIC X(806).` — the `806`-byte key field inside the overlay, not the `88`-level `==UT==MOCK-CICS` condition this run keys its strategy on. The two are a plausible-looking eleven lines apart, which is exactly how an abbreviated citation misleads. Additive syntax and keys only. **Output surface, declared rather than implied:** if this run adds the first `provisional` register entry it also introduces the opt-in key `test.results.annotate.provisional`, default `false`, together with the annotation schema fixed in A7.5 and golden files for **both** settings — the `false` set must match the inherited golden files byte for byte, and the `true` set is new files rather than modified ones. If the key already exists it is reused unchanged and only the `true`-setting golden files grow.

**Entry precondition.** Tag `run-6-sql-db2-mock`; full green; anchor matches.

**Success oracle.** With `test.results.annotate.provisional` at its `false` default, all pre-existing golden files byte-identical. Then: a corpus program asserting a mocked CICS condition resolved **through** `EIBResponseTable` — so the oracle proves the dead code is now live, not merely that a mock fired. All pre-existing golden files byte-identical. `clean test` green. Executed program count stated. Register updated.

**Primary risk.** The lookup miss presenting as success. `EIBResponseTable.lookup(String)` at `[.../services/cobolLogic/EIBResponseTable.java:L130-L133]` returns a **zero-filled** `EIBResponseCodes` when a condition name is not found. Wired as-is, every unrecognized or misspelled condition would look like a normal completion — a silent wrong answer, which is the exact shape of the risk this programme ranks first. Making the miss explicit is a mandatory part of this run and a register entry in its own right. A second, related temptation to refuse: a third-party archive on a fork defines a CICS response type with **44** enum constants that does not reference the in-repo table's 75 unique conditions at all. Adopting it would both shrink and contradict the only citable authority available.

**Key open question.** Which of the 75 uniquely tabulated conditions are reachable at all under a GnuCOBOL-only target, and which are structurally present but unexercisable? The answer partitions the register between `verified` and `provisional`, and it cannot be settled before the corpus program exists.

**ADRs implemented.** ADR-03, ADR-04, ADR-02.

---

## C7 — Run 8: Test generation at scale · `S4·ADD`

**Scope.** A generator in its own package at the fixed path `src/main/java/io/blitzy/cbltest/generator/`, deliberately bounded away from the three largest existing classes, consuming COBOL source and emitting `.cut` suites as text. It does not merge, interpret or launch; it reads the parse contract and the numeric-field metadata through an explicit read-only contract. **Invoked by one new additive command-line flag that dispatches directly into the generator package and bypasses the `Initializer` → `Generator` → `CobolTestRunner` chain**, per ADR-06 — the shared touch is one entry in the option string at `[.../services/Constants.java:L27-L28]`, one branch in `Initializer.run()` beside the existing `help`/`version` branches, one dispatch branch in `Main.main`, and one help-text line; `ArgumentHandler` itself is not modified. Generation and precompilation are mutually exclusive modes. Plus the mutation harness built **in-repo** at `src/test/mutation/`, with four operators — **statement deletion, comparison-operator inversion, condition negation, literal substitution** — and a **70%** mutation-score floor **enforced in CI** against the characterization corpus. The harness is built rather than adopted because no off-the-shelf COBOL mutation-testing tool exists; that is the only option, not a preference. All four operators are line-level transformations on column-addressable fixed-format source.

**Entry precondition.** Tag `run-7-cics-mock`; full green; anchor matches.

**Success oracle.** Generated suites for a stated set of corpus programs achieve a mutation score of **70% or more**, enforced by a CI check that fails below the floor. All pre-existing golden files byte-identical **with the new flag absent**, which is the whole compatibility claim. **With the flag present, a test asserts the merge, interpret and launch path is not entered** — neither `Generator.prepareAndRunMerge` nor `CobolTestRunner.run` is invoked — which turns ADR-06's boundary into a measurement rather than an intention. `--help` output gains exactly one documented line, checked against the golden help text. `clean test` green with zero failures. Executed program count stated. The performance envelope reported with its suite count, its command, and both the pre-change and post-change `clean test` measurements — an envelope without a measurement method is unfalsifiable.

**Primary risk.** Generating suites that execute code without asserting anything about it. That scores full coverage and detects no fault, which is why the gate is mutation-based rather than coverage-based. The repository already demonstrates the pathology at project level: a coverage gate is defined at `[build.gradle:L30-L60]` with a 13-entry exclusion list that excludes generation-path classes, and it is **not wired into `check`** at all. A second risk specific to this run: the generator reads numeric-field metadata, and a misclassified numeric field silently switches an `EXPECT` from numeric to alphanumeric comparison — a live defect class with an open issue and an upstream pull request both attesting it. The boundary must therefore be an explicit contract the generation tests exercise directly, never shared mutable state.

**Key open question.** Is 70% the right floor for *generated* suites specifically, given that generated and hand-written suites plausibly have different achievable ceilings? The floor is non-relaxable as a criterion; only the number is arguable, and any change must be stated, justified and still enforced in CI.

**ADRs implemented.** ADR-06 (the generator boundary and the `.cut` intermediate), ADR-12 (the in-repo harness, the four operators and the CI-enforced floor), ADR-04 (the register becomes machine-checked here).

---

## C8 — Run 9: z/OS and macOS launcher · `S4·ADD` plus one declared `S2·CORR`

**Scope.** A pluggable launcher behind the existing `ProcessLauncher` abstraction, with a z/OS implementation in its own package that is structurally complete and **labelled unverified**, plus the macOS repair as a **separately declared corrective discipline with its own oracle**. There are **two** dead branches, not one: `org.openmainframeproject.cobolcheck.features.launcher.Launcher.getPlatformSpecificLauncher(Platform)` at `[.../features/launcher/Launcher.java:L45-L70]` initialises `ProcessLauncher launcher = null` at `L46`, and both the `OSX` arm — construction commented out at `L58` — and the `ZOS` arm — construction commented out at `L62` — return `null`. Both are documented together at `[:L43]`, which reads `NOTE: Currently not supporting OSX or ZOS.` What is undocumented is the user-visible consequence: `macos-latest` sits in the CI matrix at `[.github/workflows/VerifyAction.yml:L13]` against a `null` launcher, `[config.properties:L203]` populates `osx.process` with a value unreachable because the launcher is `null` before the key is consulted, and `[config.properties:L206]` leaves `zos.process` empty. Four open issues ask for the z/OS half.

**Entry precondition.** Tag `run-8-test-generation`; full green; anchor matches.

**Success oracle.** Two oracles, because the run has two disciplines. **For the declared corrective half:** the macOS arm returns a working launcher and the CI macOS leg **genuinely executes COBOL programs, stating the count** — falsifiable on a platform CI actually exercises. **For the additive half:** the z/OS arm is structurally complete behind the interface, covered by tests exercising that interface with a test double, and **labelled unverified** in the handoff, the changelog and the user-facing documentation. All pre-existing golden files byte-identical on Linux. `clean test` green with zero failures.

**Primary risk.** Claiming z/OS verification. No z/OS runtime is reachable and no amount of effort changes that, so the only honest deliverable is structural completeness plus an explicit label. The risk is not that the code is wrong; it is that the label erodes — in a release note, in a README line, in a changelog entry — until a user believes it was tested. The register entry and the changelog wording are the mitigation, and they are as much of the deliverable as the code.

**Key open question.** Which z/OS process-launch mechanism to implement, given that two separate open issues request two different ones. Only one is implemented; the choice is recorded with its rejected alternative, and the pluggable interface is what makes the other addable later without revisiting this decision.

**ADRs implemented.** ADR-05 (the z/OS interface boundary, structurally complete and labelled unverified).

---
