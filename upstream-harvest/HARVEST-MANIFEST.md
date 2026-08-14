# HARVEST MANIFEST — `openmainframeproject/cobol-check`

Audit record for the one-time upstream capture persisted in `upstream-harvest/`.

Every statement below carries an evidence label. `FACT` is followed by the artifact path, the
`file:line` or the retrieved URL that establishes it. `INFERENCE` is followed by the reasoning that
supports it. `UNKNOWN` means the value was not measured and is not guessed. No label is upgraded
silently, and no figure in this document was derived by hand: all counts were read out of the
artifacts mechanically and reconciled against each artifact's own `_capture.record_counts` and the
real length of each data array before being written here.

---

## 1. Purpose and non-repeatability notice

This is the audit layer over a **one-time, non-repeatable, read-only capture** of
`openmainframeproject/cobol-check`, which is archived and read-only.

`FACT` — the repository is archived: `upstream-harvest/pulls.json` →
`repository_metadata.archived = true`, `repository_metadata.disabled = false`, retrieved from
`https://api.github.com/repos/openmainframeproject/cobol-check`.

`FACT` — the capture holds **45** open issues and **8** open pull requests, measured in section 5 from
`upstream-harvest/issues.json` and `upstream-harvest/pulls.json` and corroborated in section 6.
`INFERENCE` — those 45 issues, those 8 pull requests, the wiki and the upstream CI run history exist
**only** upstream and cannot be reconstructed from the fork; reasoning: GitHub forks copy code and
git history but not issues, pull requests, wikis or Actions run history, so none of that material is
present in the fork to be read locally. Two in-repo observations show how thin the fork's own
inherited evidence is:

- `FACT` — `.github/` holds exactly three workflow files: `.github/workflows/DeployExtension.yml`,
  `.github/workflows/VSCodeBuild.yml`, `.github/workflows/VerifyAction.yml`.
- `FACT` — `.travis.yml:8` (`  - sudo apt -y install gnucobol`) is the only line in `.github/**` or
  `.travis.yml` that installs GnuCOBOL; a case-insensitive search of both for `gnucobol|cobc`
  returns that line and nothing else. The build history that line belonged to is not reachable from
  the fork.

Consequences, stated so no later session repeats work that cannot be repeated:

- The capture **must not be re-scraped**. Later implementation sessions inherit the triaged backlog
  built from these artifacts rather than re-querying an archived repository.
- The artifacts are **planning-session output, and the delivered placement diverges from what the AAP
  requires — recorded here rather than attested away**. `FACT` — AAP §0.5.1.1 and §0.10.2 require the
  planning run's outputs to be written to the session output directory and **never committed to the
  fork**. `FACT` — they are nevertheless tracked in this repository, introduced by five commits that
  touch no path other than `PROGRAM-PLAN.md` and `upstream-harvest/**`, and the count each contributes
  is given so the attestation is arithmetic rather than assertion — `git show --name-status
  --format='' <hash>` is the command behind every figure:
  `0f2cfdb8f34d58325a001baf31e35151e3aa6f29` adds **8** (`pulls.json`, `pr-330-reviews.json`, the six
  `issue-comments/*.json`); `b9a5f01a13788a6c17b62b04dd7a4dbdd1b2aad4` adds **2** (`issues.json`,
  `pr-411-reviews.json`); `af9dfa6ec18ad3d26031319998348796d547692c` adds **1** (this manifest);
  `e5d3108fcc07246dc71a739c03565b623862b399` adds **1** (`PROGRAM-PLAN.md`); and
  `694cd4d37c4407ba8c7963780677244933356542` adds **1** (`capture-envelope.schema.json`). They sit on
  the planning branch `blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36`. `FACT` — 8 + 2 + 1 + 1 + 1 = **13**,
  the whole set and nothing else.
  `FACT` — those five are the commits that **introduced** the artifacts; later commits on the same
  branch **modify** them, including the review-remediation commit that added this very paragraph, whose
  own hash cannot be quoted from inside itself. `694cd4d37c4407ba8c7963780677244933356542` appears in
  both roles: it introduced the envelope schema and modified the other twelve in the same commit.
  `INFERENCE` — an earlier revision of this list named `a8598cc925d15807e15a0a8a7db6d4944185f493` as the
  fifth introducing commit, which was wrong in both directions — `git show --name-status --format=''
  a8598cc925d15807e15a0a8a7db6d4944185f493` reports `M PROGRAM-PLAN.md` alone, so it introduced nothing,
  and the artifact it stood in for was the envelope schema; reasoning: the list was written before the
  schema existed and was not re-derived when it was added, which is why the figures above are now stated
  as arithmetic with the producing command attached rather than as a bare list of hashes.
  `git log --oneline -- PROGRAM-PLAN.md upstream-harvest/`
  lists the full set at any time and is the command to run rather than trusting this list to stay
  exhaustive.
- `FACT` — the fork's **product** branch is clean of them: `origin/Developer` at
  `c79624bd286d5f08f156ccce755bc5d4fffd1909` contains no `PROGRAM-PLAN.md` and no path under
  `upstream-harvest/` (`git cat-file -e origin/Developer:PROGRAM-PLAN.md` exits non-zero;
  `git ls-tree origin/Developer --name-only` lists no `upstream-harvest` entry). `FACT` — no fork
  production, test, build or configuration file was modified in order to produce these artifacts, and
  no write of any kind was issued against the upstream repository. `FACT` — a copy of all thirteen paths
  is additionally written outside the working tree, to the session output directory
  `/tmp/blitzy/session-output/5cc7044b-36be-4042-9618-a49ed8c86f36/` — the location AAP §0.5.1.1 names
  for the planning outputs, referred to throughout this manifest as `<session-export>/`. It was written
  as the last action taken on these artifacts and each of the thirteen was compared byte-for-byte
  against its tracked counterpart, giving `13 of 13 byte-identical`. **The sentence is not the
  evidence; the command is**, and `PROGRAM-PLAN.md` Deliverable A #9 carries it in full together with
  the negative control that proves it can fail — run against the stale export that preceded the refresh
  it reported `0 of 13 byte-identical` and exited non-zero. `INFERENCE` — no sentence inside a file can
  attest to a copy of that same file taken after the sentence was written, and the copy is deliberately
  taken afterwards; reasoning: that is why the durable artifact is a re-runnable check rather than a
  settled claim about a path, and why `MISSING`, `DIVERGED` or an absent directory **is** the answer *no
  verified external copy exists* rather than a reason to assume one. `FACT` — the five
  commits are **not**
  removed from history: AAP §0.5.2 and §0.10.5 exclude history rewriting outright — no
  `git filter-repo`, no `git rebase --root`, no force-push — and the commits are already published, so
  clearing them is a human action on the integration branch rather than an agent action.
  `INFERENCE` — the audit consequence is bounded rather than open: a reader must not conclude from
  these files that they were never committed, and nothing else in this manifest changes on account of
  it; reasoning: every other claim here concerns what was captured and whether it is complete, and
  where the capture is stored alters none of that.
- `FACT` — the same divergence stated as a path set rather than a commit list, because the two are
  checkable by different commands: `git diff c79624bd286d5f08f156ccce755bc5d4fffd1909 --name-status`
  reports exactly thirteen paths, every one with status `A`, with zero deletions and zero modifications to
  any pre-existing file. No insertion count is quoted, because that figure changes whenever one of the
  thirteen is revised and a figure its own document invalidates is worse than none; the stable claims are
  the path set and the absence of any `M` or `D` status. `INFERENCE` — the cause is that the publication
  channel and the prohibition point at the same place: the executing platform publishes a session's
  output only by committing it, and discards anything left uncommitted; reasoning: no session output
  directory is guaranteed to survive this environment, so deleting these files would not relocate them
  but destroy a one-time, non-repeatable capture. Two alternatives were considered and rejected:
  publishing nothing destroys the deliverable the run exists to produce, and rewriting history to erase
  the artifacts after publication is forbidden both by the platform and by AAP §0.10.5, which makes
  recorded hashes immutable. **The one human action that preserves the position: do not merge the
  planning branch into `Developer` or `main`** — read `PROGRAM-PLAN.md` out of it, keep
  `upstream-harvest/`, then delete the branch. Removal of the thirteen paths from the fork's working tree
  is itemized to the modernization run in `PROGRAM-PLAN.md` Deliverable A #9, gated on written human
  confirmation that the capture is preserved elsewhere first. **This disclosure is not a waiver of the
  requirement.** `INFERENCE` — stating the gap does not close it and must not be read as closing it;
  reasoning: what the requirement protects is that no planning artifact becomes part of the product, and
  that outcome rests on the human action above, not on this paragraph. The two things an agent could do
  instead were both worse and were rejected: committing nothing destroys the deliverable, because the
  platform publishes only committed content; and rewriting the branch's history after publication is
  forbidden outright and would invalidate every hash this programme cites.
- Because the capture cannot be repeated, its **completeness record is the only remaining evidence
  that it is complete**. That is what sections 4 through 8 are for.

`FACT` — AAP §0.8.3 states the failure mode this document exists to exclude: an exhausted
unauthenticated quota returns empty response bodies rather than errors, so a capture without record
counts is indistinguishable from a silently truncated one.

Scope boundary: this manifest reports **what was captured and whether it is complete**. It contains
no classification, no ranking, no sequencing and no adjudication of any issue or pull request. That
assessment belongs to `PROGRAM-PLAN.md` Deliverable A #5.

### 1.1 The artifact set is **twelve** files, and every one of them is load-bearing

`FACT` — `upstream-harvest/` holds this manifest, **ten** data captures and the one schema those
captures are validated against:

| # | Path | Role |
| --- | --- | --- |
| 1 | `upstream-harvest/HARVEST-MANIFEST.md` | this completeness record |
| 2 | `upstream-harvest/issues.json` | all 45 open issues |
| 3 | `upstream-harvest/pulls.json` | all 8 open pull requests |
| 4 | `upstream-harvest/pr-330-reviews.json` | the contested DB2 PR's review thread |
| 5 | `upstream-harvest/pr-411-reviews.json` | the newest, never-reviewed PR's thread |
| 6 | `upstream-harvest/issue-comments/53.json` | localization — a declared non-goal |
| 7 | `upstream-harvest/issue-comments/93.json` | flagged by description, not by number |
| 8 | `upstream-harvest/issue-comments/150.json` | flagged by description, not by number |
| 9 | `upstream-harvest/issue-comments/220.json` | GraalVM native image — a declared non-goal |
| 10 | `upstream-harvest/issue-comments/321.json` | Open VSX — the only issue with recent activity |
| 11 | `upstream-harvest/issue-comments/323.json` | IMS support — a declared non-goal |
| 12 | `upstream-harvest/capture-envelope.schema.json` | the normative `_capture` envelope schema all ten captures validate against (§4.4) |

`PROGRAM-PLAN.md` is the thirteenth artifact this planning run produces, but it sits outside
`upstream-harvest/` and so is not one of the twelve counted here.

Four of the six `issue-comments/` captures were named by issue number in the carried-forward ground
truth. `93.json` and `150.json` exist because two of the flagged issues were given only by
description, so their numbers had to be resolved from `issues.json` before their threads could be
fetched. That provenance makes them easy to omit from a scope list, and omitting them would be
wrong: they are **load-bearing** for every section that reasons over the whole directory —
§3.1 (per-artifact inventory), §4 requests 22–27, §5.4 (comment-thread record counts), §7.3
(per-artifact completeness verdicts), §8's `failures=[]` enumeration, both §9.2 resolutions, and
`PROGRAM-PLAN.md` Deliverable A #5's triage of issues #93 and #150.

`INFERENCE` — because this manifest's audit assertions are arithmetic over the entire directory, a
review or acceptance that covers only a subset cannot confirm them; the totals in §5 and the verdict
table in §7.3 are only checkable against all ten data captures. **Neither file may be deleted to fit a
narrower scope list.**

---

## 2. Repository identity (hard-pinned)

| Field | Value | Evidence |
| --- | --- | --- |
| `owner` | `openmainframeproject` | `FACT` — every URL in every artifact's `_capture.calls`; `_capture.repository` on all 10 artifacts |
| `repo` | `cobol-check` | `FACT` — as above |
| `full_name` observed | `openmainframeproject/cobol-check` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.full_name` |
| `archived` observed | `true` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.archived` |
| `disabled` observed | `false` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.disabled` |
| `default_branch` observed | `Developer` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.default_branch` |
| `has_issues` observed | `true` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.has_issues` |
| `fork` observed | `false` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.fork` |
| `open_issues_count` observed | `53` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.open_issues_count` |
| `license` observed | `Apache-2.0` (`spdx_id`) | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.license.spdx_id` |
| `permissions` observed | `{admin: false, maintain: false, push: false, triage: false, pull: true}` | `FACT` — `upstream-harvest/pulls.json` → `repository_metadata.permissions` |

### 2.1 The target had to be pinned because the fork's own documentation names two different upstreams

`FACT` — the working repository links to the **former maintainer's** fork `neopragma/cobol-check` at
four distinct locations:

| Locator | Verbatim reference |
| --- | --- |
| `README.md:38` | `[the wiki](https://github.com/neopragma/cobol-check/wiki/)` |
| `GOVERNANCE.md:15` | `[General Development Guide](https://github.com/neopragma/cobol-check/wiki/General-Development-Guide)` |
| `SUPPORT.md:4` | `[issue](https://github.com/neopragma/cobol-check/issues)` |
| `SUPPORT.md:8` | `[issue](https://github.com/neopragma/cobol-check/issues)` |
| `SUPPORT.md:12` | `[Contributing](https://github.com/neopragma/cobol-check/wiki/Contributing)` |

`FACT` — the same repository names `openmainframeproject/cobol-check` at these locations:

| Locator | Verbatim reference |
| --- | --- |
| `README.md:42` | `https://github.com/openmainframeproject/cobol-check/wiki/Certified-User-Platforms` |
| `README.md:55` | `[issue](https://github.com/openmainframeproject/cobol-check/issues)` |
| `vs-code-extension/package.json:18` | `"url": "https://github.com/openmainframeproject/cobol-check/issues"` |
| `vs-code-extension/package.json:50` | `"url": "https://github.com/openmainframeproject/cobol-check/issues"` |

`FACT` — three further in-repo attestations of the same owner were observed while verifying the
above and are recorded for completeness: `README.md:24` (releases URL), `README.md:26`
(Marketplace item `openmainframeproject.cobol-check-extension`), and
`vs-code-extension/package.json:4` (`"publisher": "openmainframeproject"`).

**`openmainframeproject/cobol-check` is the repository that was harvested.** `FACT` — all 10
artifacts record `_capture.repository = "openmainframeproject/cobol-check"`, and every URL in every
`_capture.calls` entry resolves to that owner and repository or to the credential-scoped
`https://api.github.com/rate_limit` probe. `INFERENCE` — harvesting the `neopragma` form instead
would have produced a plausible-looking but wrong issue and pull-request set, because that
repository is a different GitHub object with its own independent issue tracker; the two names cannot
both be the tracker the 45 open issues live in. The pinning is therefore load-bearing rather than
cosmetic, and it is recorded rather than assumed.

---

## 3. Capture session

### 3.1 Timestamps (ISO-8601 UTC, as recorded by each capture)

`FACT` — `_capture.captured_at_utc` per artifact:

| Artifact | `captured_at_utc` |
| --- | --- |
| `upstream-harvest/issues.json` | `2026-08-14T04:57:59Z` |
| `upstream-harvest/issue-comments/323.json` | `2026-08-14T05:12:50Z` |
| `upstream-harvest/pulls.json` | `2026-08-14T05:12:55Z` |
| `upstream-harvest/issue-comments/93.json` | `2026-08-14T05:14:22Z` |
| `upstream-harvest/issue-comments/150.json` | `2026-08-14T05:14:23Z` |
| `upstream-harvest/issue-comments/220.json` | `2026-08-14T05:14:42.424156Z` |
| `upstream-harvest/issue-comments/321.json` | `2026-08-14T05:15:22Z` |
| `upstream-harvest/issue-comments/53.json` | `2026-08-14T05:16:01Z` |
| `upstream-harvest/pr-330-reviews.json` | `2026-08-14T05:21:15Z` |
| `upstream-harvest/pr-411-reviews.json` | `2026-08-14T05:49:23Z` |

`FACT` — the capture window spans `2026-08-14T04:57:59Z` (earliest) to `2026-08-14T05:49:23Z`
(latest). `FACT` — this manifest was assembled after all ten artifacts existed, on `2026-08-14`,
from their persisted contents only; no endpoint was re-queried to produce it.

`FACT` — recording inconsistency, reported rather than normalised: nine of the ten stamps are at
second resolution and `issue-comments/220.json` alone carries sub-second resolution
(`2026-08-14T05:14:42.424156Z`). `INFERENCE` — this is a serialisation difference between sibling
capture runs, not a data defect; reasoning: the value is a well-formed ISO-8601 UTC instant that
falls inside the capture window established by the other nine, and the artifact it labels
reconciles exactly against its own record counts and against GitHub's independent comment counter
(section 5).

### 3.2 Authentication — derived from the observed ceiling, never from credential presence

`FACT` — every artifact records `_capture.authenticated = true`, and each records that the value was
derived from the **observed** `x-ratelimit-limit` ceiling returned by
`GET https://api.github.com/rate_limit` rather than from the presence of a credential in the process
environment. The authenticated ceiling is 5,000 requests per hour; the unauthenticated ceiling is
60.

`FACT` — the `limit` / `remaining` / `reset` triple recorded verbatim in each artifact's
`_capture.rate_limit_observed`, with the epoch `reset` rendered alongside as UTC for readability:

| Artifact | `limit` | `remaining` | `reset` (epoch) | `reset` (UTC) |
| --- | --- | --- | --- | --- |
| `upstream-harvest/issues.json` | `5000` | `5000` | `1786687078` | `2026-08-14T05:57:58Z` |
| `upstream-harvest/pulls.json` | `5000` | `5000` | `1786687976` | `2026-08-14T06:12:56Z` |
| `upstream-harvest/pr-330-reviews.json` | `5000` | `5000` | `1786688475` | `2026-08-14T06:21:15Z` |
| `upstream-harvest/pr-411-reviews.json` | `5000` | `5000` | `1786690163` | `2026-08-14T06:49:23Z` |
| `upstream-harvest/issue-comments/53.json` | `5000` | `5000` | `1786688161` | `2026-08-14T06:16:01Z` |
| `upstream-harvest/issue-comments/93.json` | `5000` | `5000` | `1786688062` | `2026-08-14T06:14:22Z` |
| `upstream-harvest/issue-comments/150.json` | `5000` | `5000` | `1786688062` | `2026-08-14T06:14:22Z` |
| `upstream-harvest/issue-comments/220.json` | `5000` | `5000` | `1786688081` | `2026-08-14T06:14:41Z` |
| `upstream-harvest/issue-comments/321.json` | `5000` | `5000` | `1786688052` | `2026-08-14T06:14:12Z` |
| `upstream-harvest/issue-comments/323.json` | `5000` | `5000` | `1786687969` | `2026-08-14T06:12:49Z` |

**The run was authenticated.** `FACT` — of the **37** HTTP requests itemised in section 4, **36**
observed `x-ratelimit-limit = 5000` and all 26 non-`/rate_limit` requests observed `5000`; the
thirty-seventh is the deliberate unauthenticated control at `upstream-harvest/issues.json` →
`_capture.calls[1]`, which observed the `60` ceiling and is described in §6.2. `FACT` — across
those 36 authenticated requests the observed `x-ratelimit-remaining` never fell below `4960` and never
exceeded `5000`.

**One canonical total, generated rather than transcribed — and asserted rather than merely printed.**
`FACT` — every request count in this manifest is the count of `_capture.calls` entries across the ten
artifacts, and the command that produces it is the definition rather than a check of it. `FACT` — it
also **exits non-zero if any of the five figures moves**, so the total cannot silently drift away from
the document that quotes it:

```bash
# Canonical request accounting for upstream-harvest/. Run from the repository root.
# Generates the total AND asserts the five figures this section states.
# Exit status is the verdict: 0 the document agrees with the artifacts, 1 it does not.
python3 - <<'PY'
import json, glob, sys
fs = ['upstream-harvest/issues.json', 'upstream-harvest/pulls.json',
      'upstream-harvest/pr-330-reviews.json', 'upstream-harvest/pr-411-reviews.json'] + \
     sorted(glob.glob('upstream-harvest/issue-comments/*.json'))
c = [k for f in fs for k in json.load(open(f, encoding='utf-8'))['_capture']['calls']]
gate = [k for k in c if '/rate_limit' in k['url']]
at5000 = [k for k in c if k.get('x_ratelimit_limit') == 5000]
at60 = [k for k in c if isinstance(k.get('x_ratelimit_limit'), int) and k['x_ratelimit_limit'] <= 60]
methods = sorted({k['method'] for k in c})
print(len(c), 'calls', len(gate), 'rate_limit', len(at5000), 'at the 5000 ceiling', methods)
checks = [('artifacts', len(fs), 10), ('calls', len(c), 37), ('rate_limit rows', len(gate), 11),
          ('rows at the 5000 ceiling', len(at5000), 36), ('rows at a 60 ceiling', len(at60), 1),
          ('methods', methods, ['GET'])]
rc = 0
for label, got, want in checks:
    if got != want:
        print(f'DRIFT  {label}: {got!r}, this section states {want!r}')
        rc = 1
# the single 60-ceiling row must be the declared unauthenticated control: a gate probe that harvests nothing
for k in at60:
    if k['family'] != 'rate_limit' or k['records_persisted'] != 0:
        print(f'UNAUTHENTICATED DATA REQUEST  {k["url"]} family={k["family"]} persisted={k["records_persisted"]}')
        rc = 1
print('VERIFIED' if rc == 0 else 'NOT VERIFIED')
sys.exit(rc)
PY
```

`FACT` — result: `37 calls 11 rate_limit 36 at the 5000 ceiling ['GET']`, `VERIFIED`, exit status `0`.
`FACT` — the assertion is discriminating: the same block run against a copy whose thirty-seventh row was
removed reports `DRIFT calls: 36, this section states 37`, `NOT VERIFIED` and exits `1`. `INFERENCE` — a total is
quoted in exactly one form for that reason; reasoning: a request total is the accounting of every use
of a credential, so two disagreeing figures in one document weaken the very proof the section exists
to give, and generating the figure removes the class of error rather than the instance. `INFERENCE` — the quota therefore never approached exhaustion, so the empty-body
truncation failure mode described in section 1 cannot account for any zero record count in this
capture; reasoning: that failure mode requires `remaining` to reach 0, and the lowest authenticated
reading observed is 4960 of 5000.

**One request observed a ceiling of 60, and it was deliberate.** `FACT` —
`upstream-harvest/issues.json` → `_capture.calls[1]` is a `GET https://api.github.com/rate_limit`
issued **without** the credential as a control, observing `x-ratelimit-limit = 60`,
`x-ratelimit-remaining = 58`, `x-ratelimit-reset = 1786685888` (`2026-08-14T05:38:08Z`), and
returning zero harvest records. `FACT` — that artifact's `_capture.notes` describes it as a
deliberate unauthenticated control GET to the same URL as `calls[0]`, distinguishable from it by the
`x_ratelimit_limit` value. `INFERENCE` — the control **strengthens** the authentication claim rather
than undermining it: the same URL returned 60 without the credential and 5000 with it, which
demonstrates that the credential was honoured rather than silently ignored. It is not a truncation
signal, because `/rate_limit` is not a data endpoint and carries no harvest records.

`FACT` — gate readings and per-request readings differ, and both are recorded as observed rather
than reconciled. Example, from `upstream-harvest/pr-330-reviews.json`:
`_capture.rate_limit_observed.remaining = 5000` with `reset = 1786688475`, while that artifact's
first data request records `x_ratelimit_remaining = 4998` with `reset = 1786688408`. `FACT` — the
artifacts record the two figures as observed at different instants and assign no cause to the gap:
`GET /rate_limit` does not consume core quota, and what consumed the remainder is carried as
`UNKNOWN` (`upstream-harvest/pr-411-reviews.json` → `_capture.notes`;
`upstream-harvest/issue-comments/321.json` → `_capture.notes`).

### 3.3 Tooling — verified and recorded in this container, nothing installed

`FACT` — probed directly in this container while assembling this manifest:

| Tool | Observed | Path | Used for the capture? |
| --- | --- | --- | --- |
| `python3` | `Python 3.13.7` | `/usr/bin/python3` | Yes — `urllib.request` and `json`, standard library only |
| `curl` | `curl 8.14.1 (x86_64-pc-linux-gnu) libcurl/8.14.1 OpenSSL/3.5.3` | `/usr/bin/curl` | Yes, per the artifacts' recorded transport |
| `jq` | `jq-1.8.1` — **PRESENT** | `/usr/bin/jq` | **No** — present but unused |
| `gh` (GitHub CLI) | **ABSENT** from `PATH` | — | No |
| `git` | `git version 2.51.0` | — | Not used for the capture |
| Host OS | `Ubuntu 25.10` (`VERSION_ID="25.10"`) | — | — |

`FACT` — nothing was installed to produce the capture or this manifest. Every artifact records that
same posture — HTTPS with `GET` only, `jq` present-but-unused, `gh` absent, and
`installed_anything` / `nothing_installed` / `tools_installed_during_capture` false or empty. It is
carried as an explicit boolean in four artifacts (`upstream-harvest/pulls.json`,
`upstream-harvest/pr-330-reviews.json`, `upstream-harvest/pr-411-reviews.json`,
`upstream-harvest/issue-comments/321.json`) and as a sentence in the rest.

**The HTTP client, however, is not uniform, and reporting it as uniform would be a false provenance
claim.** `FACT` — per artifact, from the field named in the last column:

| # | Artifact | HTTP client, as recorded at capture | Field carrying it | `_capture.credential_transport_exposure` |
| --- | --- | --- | --- | --- |
| 1 | `upstream-harvest/issues.json` | python3 `urllib.request` | `_capture.tooling.capture_driver`, corroborated by the structured `_capture.tooling.curl_used_for_capture = false`, which records that `curl` did not issue this artifact's requests | `in_process_client_only` |
| 2 | `upstream-harvest/pulls.json` | `curl` for HTTP, python3 for JSON decode | `_capture.tooling.harvest_client`, repeated at `capture_driver` | `UNKNOWN` |
| 3 | `upstream-harvest/pr-330-reviews.json` | `curl` for HTTP, python3 stdlib `json` as serializer | `_capture.tooling.transport` and `serializer`, now also stated at `capture_driver` | `UNKNOWN` |
| 4 | `upstream-harvest/pr-411-reviews.json` | `curl` for HTTP, python3 stdlib `json` as serializer | `_capture.tooling.transport` and `serializer`, now also stated at `capture_driver` | `UNKNOWN` |
| 5 | `upstream-harvest/issue-comments/53.json` | `UNKNOWN` — no client field was recorded at capture | `_capture.tooling.capture_driver`, which now states that `UNKNOWN` explicitly | `UNKNOWN` |
| 6 | `upstream-harvest/issue-comments/93.json` | python3 `urllib.request` | `_capture.tooling.capture_driver` | `in_process_client_only` |
| 7 | `upstream-harvest/issue-comments/150.json` | python3 `urllib.request` | `_capture.tooling.capture_driver` | `in_process_client_only` |
| 8 | `upstream-harvest/issue-comments/220.json` | python3 `urllib.request` | `_capture.tooling.http_client_used`, repeated at `capture_driver` | `in_process_client_only` |
| 9 | `upstream-harvest/issue-comments/321.json` | python3 `urllib.request` | `_capture.tooling.capture_driver` | `in_process_client_only` |
| 10 | `upstream-harvest/issue-comments/323.json` | python3 `urllib.request` | `_capture.tooling.capture_driver` | `in_process_client_only` |

The split is **six** artifacts on python3 `urllib.request`, **three** on curl, and **one**
unrecorded. `UNKNOWN` — which client fetched `upstream-harvest/issue-comments/53.json`: its
`_capture.tooling` records the nothing-installed posture and the observed tool versions but named no
client at capture, and the value is not recoverable after the fact. Note that *every* artifact records
curl's version among the observed tools, whether or not curl issued its requests, which is the likely
origin of an earlier over-broad claim in this section that all ten recorded a `python3` driver.

**Two `capture_driver` values were corrected rather than left standing, and the correction is stated
because rewriting one's own metadata silently is the same defect in a different place.** `FACT` — the
envelope-normalisation change that introduced `capture-envelope.schema.json` made `capture_driver` a
required field and populated it in the **five** artifacts that lacked it — `issues.json`,
`pr-330-reviews.json`, `pr-411-reviews.json`, `issue-comments/220.json` and `issue-comments/53.json`.
In **two** of the five the value was faithful to what the capture had recorded, `issues.json` from its
recorded tooling, which sets `curl_used_for_capture = false`, and `220.json` from its own
`http_client_used`; in the other **three** it was
not, and each is corrected here: `pr-330-reviews.json` and `pr-411-reviews.json` had recorded
`transport = "curl"` with python3
stdlib `json` as their **serializer**, yet were given `capture_driver = "python3 urllib.request
(standard library only)"`, contradicting their own transport field; and `issue-comments/53.json`,
which recorded **no** client field at all, was given the same urllib value. `FACT` — all three now
state what was observed, `53.json` states `UNKNOWN` explicitly, and each carries a
`CREDENTIAL TRANSPORT EXPOSURE` note in its own `_capture.notes` recording the correction. `INFERENCE`
— the harvested payloads are unaffected; reasoning: every edit was confined to the `_capture` audit
envelope, and the canonical digest of each artifact's harvested arrays is unchanged by it.

`INFERENCE` — the mixed transport does not weaken the completeness evidence; reasoning: both clients
issued explicit `GET` requests against the same documented endpoints with the same `Accept` and
`Authorization` header set, and each artifact records its own per-call status codes and rate-limit
headers, so completeness is established per call rather than per client. What the mixed transport
*does* affect is credential exposure, and that is the subject of §3.3.1 rather than a parenthesis
here.

### 3.3.1 Credential transport — what is established, what is not, and the rule for any future capture

**The credential is absent from every artifact at rest, and that is a different claim from never
having been exposed.** `FACT` — a search of all twelve files in this folder for credential material —
`ghp_`, `github_pat_`, `gh[sou]_`, an `Authorization` header value, or any bearer string — returns
nothing, and response headers were captured to a separate dump rather than interleaved into any
response body. `FACT` — the credential held `pull` as its only permission and every one of the 37
itemised requests is a `GET` (§11).

**Whether the credential ever entered a process argument vector is `UNKNOWN` for four artifacts, and
is recorded as such rather than attested away.** `FACT` — `curl -H 'Authorization: …'` places the
header in the process argument vector, where `/proc/<pid>/cmdline` is readable by any process of the
same user for the lifetime of the request; a credential supplied through `curl --config` on a file
descriptor, or set as a header inside an in-process client such as python3 `urllib.request`, does not
appear there. `FACT` — no invocation transcript was retained for any capture, so for the three
curl-transport artifacts the mechanism cannot be distinguished after the fact, and for
`issue-comments/53.json` the client itself was never recorded. `FACT` — those four therefore carry
`_capture.credential_transport_exposure = "UNKNOWN"`, and `upstream-harvest/pulls.json` additionally
had an absolute clause retracted from its own SECRET HYGIENE note — that the credential never appeared
on a command line — because its own `harvest_client` field records curl as the HTTP client, so the
claim was never established by evidence. `INFERENCE` — the residual risk is bounded rather than
nil: any exposure was to processes of the same user inside a single-tenant ephemeral container, for the
duration of a request, of a read-only credential whose only permission is `pull`; reasoning: the
permission set caps the worst case at reading data that is already public, and the archive accepts no
writes from any credential.

**The rule that binds any future authenticated capture in this programme, stated as a rule because
this one cannot be re-run.** A capture may authenticate in exactly one of two ways: an **in-process
HTTPS client** that sets the header inside the capturing process, which is what six of these ten
artifacts did; or a client reading the credential from a **file descriptor or configuration file**
never named on the command line — `curl --config <(printf 'header = "Authorization: Bearer %s"\n'
"$TOKEN")` or the equivalent `--config -` reading the same text from standard input. Passing a credential as a command-line
argument — `-H`, `-u`, `--header`, or an interpolated URL — is prohibited, and so is echoing any
command line that carries one. Each artifact must record **one** transport, must state it in
`capture_driver`, and must classify its exposure in `credential_transport_exposure`, whose three
permitted values are fixed by the envelope schema; `in_process_client_only` may be written only where
the recorded client cannot place a header in an argument vector. `INFERENCE` — the enum exists so that
the unprovable claim is unwritable; reasoning: prose can assert non-exposure without evidence, and a
validated three-value field cannot, so the control survives a session boundary where a convention
would not.

`FACT` — `_capture.tooling` is an **object** in all ten artifacts, and `nothing_installed` is a required
`true` under the envelope schema (§4.4), so the posture above is now mechanically checked rather than
merely asserted. `FACT` — one artifact, `upstream-harvest/issues.json`, had recorded its tooling as a
single prose string instead: its non-duplicative content is retained at
`_capture.tooling.provenance_note` — the jq contradiction and the nothing-installed invariant —
while everything else that string stated is carried by the structured fields alongside it
(`python3_version_observed`, `curl_version_observed`, `curl_used_for_capture`,
`jq_present`/`jq_used`, `gh_present`/`gh_used`, `nothing_installed`), so normalising the shape and
then removing the restatement lost no fact.

**Three contradictions of the supplied tooling ground truth, stated explicitly rather than quietly
corrected** (AAP §0.2.2 requires contradictions to be shown with evidence; §0.10.2 forbids quiet
correction):

| Supplied claim | Observed here | Verdict |
| --- | --- | --- |
| `jq` is absent from the image | `jq-1.8.1` at `/usr/bin/jq` | **CONTRADICTED** — `jq` is present. It was nevertheless not used; the capture used the mandated `python3` (and `curl`) pairing only. |
| `curl` is `8.5.0` | `curl 8.14.1` | **CONTRADICTED** — present, different version. |
| `python3` is `3.12.3` | `Python 3.13.7` | **CONTRADICTED** — present, different version. |
| the `gh` CLI is absent from the image | absent from `PATH` | **CORROBORATED** |

`INFERENCE` — none of the three contradictions affects the validity of the capture; reasoning: all
three concern client-side tooling for an HTTPS `GET`, the observed versions are newer rather than
older than the supplied claims, and the mandated client (`python3`, with `curl` recorded as the
transport) was available and used in both cases. `FACT` — all four sibling top-level artifacts and
all six issue-comment artifacts independently recorded the same three contradictions in their own
`_capture.notes` and `_capture.tooling`, so this manifest reproduces a finding that is already
attested in the evidence rather than introducing a new one.

`FACT` — host OS divergence, recorded and not reconciled: `Ubuntu 25.10` was observed here; the
platform setup document names `Ubuntu 22.04`; AAP §0.10.7 records an observed `Ubuntu 24.04.4 LTS`.
All three values differ. `INFERENCE` — immaterial to an HTTPS `GET` capture, for the same reasoning
as the client-version contradictions above.

### 3.4 Environment activation

`FACT` — `/etc/profile.d/blitzy-cobolcheck-env.sh` is **present** in this container and was sourced
as the literal first command of this run, exiting `0`. Observed exports include
`JDK8_HOME=/usr/lib/jvm/java-8-openjdk-amd64`, `JDK11_HOME=/usr/lib/jvm/java-11-openjdk-amd64`,
`JDK21_HOME=/usr/lib/jvm/java-21-openjdk-amd64`, `JAVA_HOME=$JDK11_HOME`,
`GRADLE_USER_HOME` (a container-local home path, deliberately not reproduced), and a de-duplicated
`COB_CFLAGS=-std=c17 -finline-functions -pipe -Wdate-time -D_FORTIFY_SOURCE=3 -Wno-unused -fsigned-char`
carrying a single `-D_FORTIFY_SOURCE` value.

`FACT` — each capture artifact records the same activation: `upstream-harvest/pulls.json` →
`_capture.tooling.environment_activation`; `upstream-harvest/issue-comments/93.json`,
`150.json`, `323.json` → `_capture.notes` (script present, sourced as the literal first command,
exit status 0); `upstream-harvest/issue-comments/220.json` and `321.json` →
`_capture.tooling.env_activation_script_present = true`.

`FACT` — nothing in this capture depends on the activated toolchain: an HTTPS `GET` needs only
`python3` and `curl`, both already on `PATH`. No value in this manifest is labelled `UNKNOWN` on
account of the activation step, and no probe result predating it is treated as a finding.

---

## 4. Endpoint table — every endpoint called

`FACT` — **37 HTTP requests are itemised** across the ten artifacts' `_capture.calls` arrays, giving
**37 rows**. Every row is read directly out of an artifact; none is reconstructed by hand, and this
table is generated from the artifacts rather than transcribed, so it cannot drift from them.
`FACT` — **four of the 37 records carry `record_status = "PARTIAL"`** and are marked in the table: row
31, whose per-call record was not written at capture time (§4.1), and rows 3, 14 and 37, whose response
bodies were observed but not retained (§4.3). The remaining 33 are `COMPLETE`. `FACT` — row 31 is the one row whose envelope is reconstructed rather than recorded, rebuilt from evidence preserved inside its own artifact and explained in §4.1; it is the reason `321.json` carries a `PARTIAL` audit verdict in §7.3.

Ordering convention, stated so the numbering is reproducible: artifacts in the order
`issues.json`, `pulls.json`, `pr-330-reviews.json`, `pr-411-reviews.json`, then
`issue-comments/` ascending by issue number; within an artifact, `_capture.calls` index order.
`FACT` — each artifact's `_capture.calls` array is recorded in the order the requests were issued
(`upstream-harvest/issues.json` → `_capture.notes`: "`calls[]` lists every HTTP request issued while
producing this artifact, in the order issued").

Column notes, stated once because three of them used to mean different things in different artifacts.
`records returned` is the number of top-level records in the response payload — the array length for a
collection endpoint and `1` for a single document, including `/rate_limit`, which returns one quota
document. `records persisted` is how many of those records are stored in the artifact named in the last
column; it is `0` for `/rate_limit`, whose payload is transport metadata rather than harvested data,
and `0` for a row whose body was not retained. `response body` reports retention: *retained* means the
payload is stored verbatim in that artifact, *metadata only* means the salient rate-limit triple is
recorded in `_capture.rate_limit_observed`, and ***NOT RETAINED*** means the response was observed but
its body is persisted nowhere (§4.3). Both count columns and this convention are fixed by
`upstream-harvest/capture-envelope.schema.json` and validated against it (§4.4). `Link: rel="next"
followed?` reports the observed `link_next` value.

`FACT` — every list request in this capture was sent with `per_page=100`, visible in the URL of every
list row below, and every one of the 37 itemised requests recorded `link_next = null`, meaning no
further page existed to follow. `FACT` — single-object requests (`/repos/{owner}/{repo}`,
`/issues/{n}`, `/pulls/{n}`) and `/rate_limit` return one document rather than a collection and are not
paginated; each carries `paginated = false` in its call record, so a `null` `link_next` on those rows
means *not applicable* rather than *exhausted*.

| # | Method + full URL | HTTP status | records returned | records persisted | response body | Link: rel="next" followed? | x-ratelimit-limit | x-ratelimit-remaining | artifact written |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issues.json` |
| 2 | `GET https://api.github.com/rate_limit` — **unauthenticated control** | 200 | 1 | 0 | metadata only | n/a — not paginated | 60 | 58 | `upstream-harvest/issues.json` |
| 3 | `GET https://api.github.com/repos/openmainframeproject/cobol-check` | 200 | 1 | 0 | **NOT RETAINED** | n/a — single object | 5000 | 4984 | `upstream-harvest/issues.json` |
| 4 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues?state=open&per_page=100` | 200 | 53 | 53 | retained | exhausted — `link_next=null`, no next page | 5000 | 4983 | `upstream-harvest/issues.json` |
| 5 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/pulls.json` |
| 6 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls?state=open&per_page=100` | 200 | 8 | 8 | retained | exhausted — `link_next=null`, no next page | 5000 | 4979 | `upstream-harvest/pulls.json` |
| 7 | `GET https://api.github.com/repos/openmainframeproject/cobol-check` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4978 | `upstream-harvest/pulls.json` |
| 8 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/pr-330-reviews.json` |
| 9 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/330/reviews?per_page=100` | 200 | 2 | 2 | retained | exhausted — `link_next=null`, no next page | 5000 | 4998 | `upstream-harvest/pr-330-reviews.json` |
| 10 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/330/comments?per_page=100` | 200 | 3 | 3 | retained | exhausted — `link_next=null`, no next page | 5000 | 4997 | `upstream-harvest/pr-330-reviews.json` |
| 11 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/330/comments?per_page=100` | 200 | 5 | 5 | retained | exhausted — `link_next=null`, no next page | 5000 | 4996 | `upstream-harvest/pr-330-reviews.json` |
| 12 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/330` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4995 | `upstream-harvest/pr-330-reviews.json` |
| 13 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/pr-411-reviews.json` |
| 14 | `GET https://api.github.com/repos/openmainframeproject/cobol-check` | 200 | 1 | 0 | **NOT RETAINED** | n/a — single object | 5000 | 4991 | `upstream-harvest/pr-411-reviews.json` |
| 15 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/411/reviews?per_page=100` | 200 | 0 | 0 | retained | exhausted — `link_next=null`, no next page | 5000 | 4990 | `upstream-harvest/pr-411-reviews.json` |
| 16 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/411/comments?per_page=100` | 200 | 0 | 0 | retained | exhausted — `link_next=null`, no next page | 5000 | 4989 | `upstream-harvest/pr-411-reviews.json` |
| 17 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/411/comments?per_page=100` | 200 | 0 | 0 | retained | exhausted — `link_next=null`, no next page | 5000 | 4988 | `upstream-harvest/pr-411-reviews.json` |
| 18 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/pulls/411` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4987 | `upstream-harvest/pr-411-reviews.json` |
| 19 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/53.json` |
| 20 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/53/comments?per_page=100` | 200 | 0 | 0 | retained | exhausted — `link_next=null`, no next page | 5000 | 4961 | `upstream-harvest/issue-comments/53.json` |
| 21 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/53` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4960 | `upstream-harvest/issue-comments/53.json` |
| 22 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/93.json` |
| 23 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/93/comments?per_page=100` | 200 | 0 | 0 | retained | exhausted — `link_next=null`, no next page | 5000 | 4977 | `upstream-harvest/issue-comments/93.json` |
| 24 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/93` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4976 | `upstream-harvest/issue-comments/93.json` |
| 25 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/150.json` |
| 26 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/150/comments?per_page=100` | 200 | 3 | 3 | retained | exhausted — `link_next=null`, no next page | 5000 | 4975 | `upstream-harvest/issue-comments/150.json` |
| 27 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/150` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4974 | `upstream-harvest/issue-comments/150.json` |
| 28 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/220.json` |
| 29 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/220/comments?per_page=100` | 200 | 3 | 3 | retained | exhausted — `link_next=null`, no next page | 5000 | 4973 | `upstream-harvest/issue-comments/220.json` |
| 30 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/220` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4972 | `upstream-harvest/issue-comments/220.json` |
| 31 | `GET https://api.github.com/rate_limit` | `UNKNOWN` — not recorded | `UNKNOWN` — not recorded | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/321.json` |
| 32 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/321/comments?per_page=100` | 200 | 4 | 4 | retained | exhausted — `link_next=null`, no next page | 5000 | 4965 | `upstream-harvest/issue-comments/321.json` |
| 33 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/321` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4964 | `upstream-harvest/issue-comments/321.json` |
| 34 | `GET https://api.github.com/rate_limit` | 200 | 1 | 0 | metadata only | n/a — not paginated | 5000 | 5000 | `upstream-harvest/issue-comments/323.json` |
| 35 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/323/comments?per_page=100` | 200 | 5 | 5 | retained | exhausted — `link_next=null`, no next page | 5000 | 4982 | `upstream-harvest/issue-comments/323.json` |
| 36 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues/323` | 200 | 1 | 1 | retained | n/a — single object | 5000 | 4981 | `upstream-harvest/issue-comments/323.json` |
| 37 | `GET https://api.github.com/repos/openmainframeproject/cobol-check/issues?state=open&per_page=100` | 200 | 53 | 0 | **NOT RETAINED** | exhausted — `link_next=null`, no next page | 5000 | 4980 | `upstream-harvest/issue-comments/323.json` |

### 4.1 Row 31 — a request whose per-call record was not written, now itemised as `PARTIAL`

`FACT` — the authentication-gate request belonging to
`upstream-harvest/issue-comments/321.json` was issued but no per-call record was written for it at
capture time: that artifact's `_capture.calls` array held exactly two entries, the comments listing and
the issue detail, and was the only one in this folder carrying no `/rate_limit` entry. `FACT` — the same
artifact records `_capture.rate_limit_observed = {"limit": 5000, "remaining": 5000,
"reset": 1786688052}`, and its `_capture.notes` describe an authentication gate probe against
`GET https://api.github.com/rate_limit` returning HTTP 200 with `x-ratelimit-limit` 5000 corroborated by
`resources.core.limit = 5000` in the response body.

`INFERENCE` — the request was therefore issued; reasoning: a `limit`/`remaining`/`reset` triple cannot
be observed without issuing the request that returns those headers.

`FACT` — it is now itemised as **row 31**, and as `calls[0]` of that artifact, with
`record_status = "PARTIAL"`. Its three rate-limit values are re-recorded from
`_capture.rate_limit_observed` and that artifact's own notes, which record them as observed at this
gate; the call record states that provenance in its `record_provenance` field. `FACT` — its `http_status` and
`records_returned` are `null` and are named in `unknown_fields`, because those two values are recorded
nowhere in the evidence. `INFERENCE` — it preceded the two harvesting calls, because the notes record
the gate as read immediately before harvesting; `UNKNOWN` — its exact ordinal position in the sequence
is not independently recorded.

`FACT` — nothing on that record is taken from another artifact and no value is invented: the
`/rate_limit` readings held by sibling captures belong to their own gate probes at their own instants
and were not substituted. `FACT` — `upstream-harvest/issue-comments/321.json` therefore carries
`audit_status = "PARTIAL"` and an `_capture.omissions` entry of kind `per_call_record_not_written`.
Omitting the row would have understated the request count; inventing a status would have been
fabrication; declaring the artifact `COMPLETE` while a record is admittedly incomplete would have been
the same failure one level up. `FACT` — the harvested comment payload is unaffected: 4 comments, and
`issue.comments = 4` from a different endpoint agrees (§5.4, §7.3).

`FACT` — the reconstructed row carries its own provenance on the call record: a single
`record_provenance` field naming every recovered value and its basis, including where the three
rate-limit values came from, and an `unrecoverable_fields` entry for the record count. `INFERENCE` —
the request nonetheless succeeded, even though its status is recorded nowhere; reasoning: this endpoint
returns a body carrying `resources.core.limit` only on success, an unauthenticated or rejected call
returns a different body shape and no such field, and the notes record that field as read from the
body — which is why the status is labelled `UNKNOWN` in the data and the success is carried as an
inference rather than written into `http_status`, a field that holds observations only. `INFERENCE` —
no value for the record count is entailed either, so none was chosen; reasoning: sibling captures
`53.json` and `220.json` record `null` for this same endpoint while `323.json` records `1`, so the
siblings do not agree and nothing follows from them. `INFERENCE` — re-issuing the request could not
have helped; reasoning: a fresh call returns a different `remaining` at a different instant and would
be evidence about a new request rather than about the one this artifact describes.

### 4.2 Endpoint families and distinct URLs

`FACT` — the 37 itemised requests resolve to **24 distinct URLs**, distributed as follows. The family
names are the canonical `_capture.calls[].family` values fixed by
`upstream-harvest/capture-envelope.schema.json`; where a capture had recorded a different label, the
original is preserved on the call record in `family_label_as_originally_recorded`.

| Family (`family` value) | Itemised requests | Detail |
| --- | --- | --- |
| `rate_limit` | 11 | rows 1, 2, 5, 8, 13, 19, 22, 25, 28, 31, 34. Row 2 is the unauthenticated control; `issues.json` is the only artifact carrying two; row 31 is the `PARTIAL` record described in §4.1 |
| `repository` | 3 | rows 3, 7, 14 — repository-object probes recording `archived`, `default_branch`, `open_issues_count` and `permissions`. Only row 7's body is retained; rows 3 and 14 are ***NOT RETAINED*** (§4.3) |
| `issues_list` | 2 | rows 4 and 37, each returning 53 entries. Row 4 is the canonical retained capture; row 37 is a duplicate whose body is ***NOT RETAINED*** (§4.3) |
| `pulls_list` | 1 | row 6, returning 8 records |
| `pull_reviews` | 2 | rows 9 (#330) and 15 (#411) |
| `pull_review_comments` | 2 | rows 10 (#330) and 16 (#411) |
| `issue_comments` | 8 | rows 11 (#330), 17 (#411), 20 (#53), 23 (#93), 26 (#150), 29 (#220), 32 (#321), 35 (#323) |
| `pull` | 2 | rows 12 (#330) and 18 (#411) |
| `issue` | 6 | rows 21, 24, 27, 30, 33, 36 — one per harvested issue |

`FACT` — itemised requests per artifact: `issues.json` 4 · `pulls.json` 3 ·
`pr-330-reviews.json` 5 · `pr-411-reviews.json` 6 · `issue-comments/53.json` 3 ·
`issue-comments/93.json` 3 · `issue-comments/150.json` 3 · `issue-comments/220.json` 3 ·
`issue-comments/321.json` 3 · `issue-comments/323.json` 4. Total 37.

`FACT` — the `records_returned` field previously carried three different conventions across sibling
captures on the `/rate_limit` rows alone — `0` in rows 1 and 2, `null` in four rows, `1` in four others —
and the per-call rate-limit headers were serialised as strings in three artifacts and as integers in the
rest. `FACT` — all of it is now normalised to the single convention stated above and enforced by
`upstream-harvest/capture-envelope.schema.json`: every `/rate_limit` row reads `records_returned = 1`
and `records_persisted = 0`, every rate-limit header value is an integer, and the one exception is row
31, whose `records_returned` is `null` and named in `unknown_fields` because it was never recorded. The
divergence is recorded here rather than quietly erased. `FACT` — the page-loss reconciliation in section
7 runs on `records_persisted` over the data-family calls, which is the identity that has to hold for the
capture to be complete.

### 4.3 Response-body retention — three responses were observed but are persisted nowhere

`FACT` — 34 of the 37 rows have their payload accounted for: 23 are retained verbatim in the artifact
named in the last column, and 11 are `/rate_limit` responses whose salient triple is recorded in
`_capture.rate_limit_observed` (23 + 11 = 34). `FACT` — **three rows are not**, and each is marked
***NOT RETAINED*** in the table, carries `record_status = "PARTIAL"` on its call record and appears in
its artifact's `_capture.omissions`:

| Row | Request | Artifact that would have held it | What survives | Why it is not reconstructed |
| --- | --- | --- | --- | --- |
| 3 | `GET /repos/openmainframeproject/cobol-check` | `upstream-harvest/issues.json` | Only the fields quoted in that artifact's `_capture.notes`: `full_name`, `archived`, `disabled`, `has_issues`, `default_branch`, `open_issues_count` | Re-querying an archived, read-only repository is prohibited. `upstream-harvest/pulls.json` → `repository_metadata` is a **different response captured at a different instant** and must not be substituted for it |
| 14 | `GET /repos/openmainframeproject/cobol-check` | `upstream-harvest/pr-411-reviews.json` | Only the fields quoted in that artifact's `_capture.notes`: `archived`, `disabled`, `default_branch`, `permissions` | Same: no re-query, and no substitution from `pulls.json` |
| 37 | `GET /repos/.../issues?state=open&per_page=100` | `upstream-harvest/issue-comments/323.json` | Only the two flagged-issue resolution fields quoted in that artifact's notes; the call itself recorded 53 entries returned | Nothing is lost to the programme, because row 4 is the canonical retained capture of the same endpoint in `upstream-harvest/issues.json` — read that instead. This duplicate body is not reconstructed |

`FACT` — the affected artifacts are therefore `issues.json`, `pr-411-reviews.json` and
`issue-comments/323.json`, each carrying `audit_status = "PARTIAL"`. `INFERENCE` — the consequence is
bounded and worth stating precisely: what is lost is the ability to re-derive the repository-object
corroboration *from these artifacts alone*, not the harvested issue, pull-request, review or comment
data, none of which came from these three responses; reasoning: rows 3 and 14 were corroboration probes
whose observed fields survive as quoted values, and row 37 duplicates a retained capture.

### 4.4 One envelope schema, and the validation that enforces it

`FACT` — `upstream-harvest/capture-envelope.schema.json` (JSON Schema draft-07,
`envelope_schema_version` `1.2.0`) is the single normative shape of the `_capture` envelope, and every
artifact in this folder points at it through `_capture.envelope_schema`. `FACT` — it exists because the
ten captures were written by separate sessions and drifted in four ways now corrected: `tooling` was a
string in one artifact and an object in the other nine; per-call rate-limit values were strings in three
and integers in seven; `records_returned` carried three different meanings on `/rate_limit` rows; and
`family` appeared only on the pull-request review calls.

`FACT` — the schema constrains the audit envelope **only**. The harvested GitHub payloads — `issues`,
`pulls`, `reviews`, `review_comments`, `issue_comments`, `issue`, `pull_request`,
`repository_metadata` — are deliberately left unconstrained so that no schema rule can ever motivate
editing raw third-party data.

`FACT` — the schema makes an unlabelled gap invalid rather than merely discouraged: a `null`
`http_status` or `records_returned` forces `record_status = "PARTIAL"` and the field's name in
`unknown_fields`; `response_body_retention = "not_retained"` forces `records_persisted = 0`,
`response_body_location = null` and `PARTIAL`; `retained_verbatim` requires a location; `method` is
fixed to `GET`; every `notes` entry must open with `FACT`, `INFERENCE` or `UNKNOWN`; and
`tooling.nothing_installed` must be `true`.

`FACT` — **the schema also makes a self-contradictory envelope invalid, which through
`envelope_schema_version` `1.1.0` it did not.** Versions `1.0.0` and `1.1.0` constrained the type, value
and vocabulary of each field *in isolation*, so an envelope whose fields disagreed with one another
still validated. Nine cross-field invariants close that, each carrying its own `description` in the
schema, six on `definitions.capture` and three on `definitions.call`:

| # | Invariant | What it makes unwritable |
| --- | --- | --- |
| **C1** | `authenticated: true` requires `rate_limit_observed.limit > 60` | An artifact claiming authentication beside a falsified 60-request ceiling. `authenticated` is *derived* from the observed ceiling (§3.2) — a fact the property's own description stated and nothing enforced |
| **C2** | `authenticated: false` requires `limit <= 60` **and** `audit_status = "PARTIAL"` | An unauthenticated capture claiming COMPLETE. §1 records the failure mode: an exhausted unauthenticated quota returns empty bodies rather than errors, so completeness cannot be established from inside such a capture |
| **C3** | `audit_status = "COMPLETE"` requires every call `COMPLETE` and `omissions` empty | A downgraded call record, or an added recording gap, sitting under an untouched artifact-level verdict. This is `audit_status`'s own documented definition, now checked |
| **C4** | `audit_status = "PARTIAL"` requires at least one itemised `omission` | A blanket PARTIAL that withholds what is missing. Every PARTIAL arises from a null field, an unretained body or an unwritten call record, and the `omission` vocabulary covers exactly those |
| **C5** | A data-family call that retained its body and returned **zero** records requires a `notes` entry opening `FACT: ZERO-RECORD CORROBORATION` or `UNKNOWN: ZERO-RECORD CORROBORATION` | A silently truncated page presented as an empty thread. The two produce byte-identical artifacts — HTTP 200, an empty array, a zero count — so the distinction cannot be read out of the shape and must be asserted with its evidence |
| **C6** | An `UNKNOWN`-prefixed zero-record classification requires `audit_status = "PARTIAL"` | Doubt recorded in one field and contradicted by the verdict in another |
| **K5** | `record_status = "COMPLETE"` requires a 2xx `http_status` | A record keeping its COMPLETE verdict over a `401`, `403` or `404`. Before `1.2.0`, `http_status` was typed integer-or-null and accepted any integer |
| **K6** | `response_body_retention = "retained_verbatim"` requires a 2xx `http_status` | A payload presented as harvested data when the response that carried it was not a success. Stated separately from K5 because the two are independently falsifiable |
| **K7** | A non-`rate_limit` family observing `x_ratelimit_limit <= 60` requires `record_status = "PARTIAL"` | A *data* request made without the credential counted as COMPLETE evidence. The `rate_limit` family is excluded deliberately: `upstream-harvest/issues.json` → `_capture.calls[1]` is the declared unauthenticated control, whose whole purpose is to observe the 60 ceiling, and it harvests nothing (§6.2) |

`FACT` — **two invariants of the same family are deliberately not in the schema, and the boundary is
stated here rather than left for a reader to assume**: reconciling `record_counts` and per-call
`records_persisted` against the real length of each persisted data array, and comparing
`records_persisted` against `records_returned` on the same call. `FACT` — neither is expressible in
JSON Schema draft-07, which can compare a value against a constant but not against a sibling property
or against the length of an array in another subtree. `INFERENCE` — they are therefore enforced by the
validation gate below rather than dropped; reasoning: an arithmetic identity that no check performs is
a convention, and the whole point of this section is that the envelope's claims are machine-checked —
so the honest arrangement is a schema that carries every rule it can express and a gate that carries
the two it cannot, with this paragraph naming which is which so nobody assumes the schema checks
arithmetic it cannot see.

**Two design decisions taken here, each recorded with the alternative rejected and the reason, because a
conflict between two candidate designs is resolved rather than left open.** `INFERENCE` — **C5 requires
the classification unconditionally rather than accepting `audit_status = "PARTIAL"` as a substitute.**
Rejected alternative: allow either. Reasoning: `audit_status` is a statement about *audit metadata*,
and four artifacts already carry `PARTIAL` for reasons that say nothing about any zero (§7.3), so a
falsification could satisfy the rule by flipping a field that does not address the question. Requiring
the statement, in either the `FACT` or the `UNKNOWN` form, is the same device
`credential_transport_exposure` already uses: a three-valued classification makes an unprovable claim
unwritable, where prose could assert it without evidence (§3.3.1). `INFERENCE` — **the version was bumped to
`1.2.0` rather than these constraints being added under `1.1.0`.** Rejected alternative: tighten
silently. Reasoning: this section calls the schema at a stated version the single normative shape, so
tightening it under an unchanged version number would make that statement false for every reader who
validated against `1.1.0` — the identical reasoning recorded for the `1.0.0` → `1.1.0` bump below.

`FACT` — **version `1.1.0` adds exactly two normative constraints to `1.0.0` and changes nothing else**,
both of them controls that a convention could not carry across a session boundary. First,
`credential_transport_exposure` is **required**, with the three permitted values
`in_process_client_only`, `argv_exposed` and `UNKNOWN`, so an artifact can neither omit the credential's
transport exposure nor assert non-exposure it cannot evidence — §3.3.1 states the rule and the four
artifacts that carry `UNKNOWN`. Second, `notes` must **contain** an entry opening
`FACT: PUBLIC-DATA GOVERNANCE`, so the handling, retention, disposal and secondary-use contract in
section 12 travels with the data it governs rather than living only in this manifest. `INFERENCE` — the
version was bumped rather than the constraints being slipped into `1.0.0`; reasoning: this manifest calls
the schema at a stated version the single normative shape, so tightening it silently under the same
version number would make that statement false for every reader who validated against the earlier one.

**The validation gate, and it is a gate rather than a report.** `FACT` — validation is performed against
all ten artifacts with the validator present in this container (`jsonschema` 4.26.0, verified rather
than installed). `FACT` — the command below **exits non-zero on any violation**, so it composes into a
precondition for a later run instead of merely printing a number a reader has to interpret. `INFERENCE`
— the exit status is the load-bearing part; reasoning: an earlier revision of this section documented a
`python3 -c "…print(…)"` form whose status reflected only interpreter success, so it reported violations
correctly and could not fail — and a check that cannot fail is not a control, it is a comment. Run it
from the repository root:

```bash
# Envelope validation gate for upstream-harvest/. Run from the repository root.
# Layer 1: every artifact against capture-envelope.schema.json (JSON Schema draft-07).
# Layer 2: the two cross-field identities draft-07 cannot express - counts against real
#          array lengths, and records_persisted against records_returned on the same call.
# Exit status is the verdict: 0 verified, 1 not. Nothing is written and nothing is installed.
python3 - <<'PY'
import json, glob, os, sys
from jsonschema import Draft7Validator as V

H = 'upstream-harvest'
FILES = ['issues.json', 'pulls.json', 'pr-330-reviews.json', 'pr-411-reviews.json'] + \
        sorted('issue-comments/' + os.path.basename(p)
               for p in glob.glob(os.path.join(H, 'issue-comments', '*.json')))
# record_counts keys whose name does not match the payload key they count.
ALIAS = {'issues.json': {'entries_total': 'issues'}, 'pulls.json': {'pulls_total': 'pulls'}}
# declared expectation -> the measured count it claims to corroborate.
EXPECT = {'issues.json': [('aap_expected_open_issues', 'issues_excluding_pull_requests')],
          'pulls.json': [('aap_expected_open_pulls', 'pulls_total')],
          'pr-330-reviews.json': [('aap_expected_review_comments', 'review_comments')]}

def reconcile(name, doc):
    c, bad = doc['_capture'], []
    rc = c['record_counts']
    data = {k: v for k, v in doc.items() if k != '_capture'}
    size = {k: (len(v) if isinstance(v, list) else 1) for k, v in data.items()}
    persisted = {}
    for i, k in enumerate(c['calls']):
        loc = k.get('response_body_location')
        if loc is None or loc.startswith('_capture'):
            if k['records_persisted'] != 0:
                bad.append(f'{name} calls[{i}] persists {k["records_persisted"]} with no in-artifact location')
            continue
        persisted[loc] = persisted.get(loc, 0) + k['records_persisted']
    for loc, n in persisted.items():                      # page-loss check
        if loc not in size:
            bad.append(f'{name} calls claim location {loc!r}, absent from the artifact')
        elif size[loc] != n:
            bad.append(f'{name} {loc}: {n} record(s) claimed persisted, {size[loc]} present')
    for k in data:                                        # no unclaimed payload
        if k not in persisted:
            bad.append(f'{name} payload key {k!r} is claimed by no call')
    for i, k in enumerate(c['calls']):                    # sibling comparison
        if isinstance(k['records_returned'], int) and k['records_persisted'] > k['records_returned']:
            bad.append(f'{name} calls[{i}] persists {k["records_persisted"]} of {k["records_returned"]} returned')
    for key, val in rc.items():                           # counts against measured lengths
        if isinstance(val, bool) or not isinstance(val, int):
            continue
        tgt = ALIAS.get(name, {}).get(key, key if key in size else None)
        if tgt is not None and val != size[tgt]:
            bad.append(f'{name} record_counts.{key}={val}, {tgt} holds {size[tgt]}')
    if name == 'issues.json':                             # the three numbers of section 5.1
        pr = sum(1 for e in doc['issues'] if 'pull_request' in e)
        if rc.get('entries_with_pull_request_key') != pr:
            bad.append(f'{name} entries_with_pull_request_key={rc.get("entries_with_pull_request_key")}, measured {pr}')
        if rc.get('issues_excluding_pull_requests') != len(doc['issues']) - pr:
            bad.append(f'{name} issues_excluding_pull_requests={rc.get("issues_excluding_pull_requests")}, measured {len(doc["issues"]) - pr}')
    if name == 'pr-411-reviews.json':
        tot = sum(rc.get(k, 0) for k in ('reviews', 'review_comments', 'issue_comments'))
        if rc.get('total') != tot:
            bad.append(f'{name} record_counts.total={rc.get("total")}, sum is {tot}')
    if name == 'pr-330-reviews.json' and rc.get('actual_review_comments') != size.get('review_comments'):
        bad.append(f'{name} actual_review_comments={rc.get("actual_review_comments")}, review_comments holds {size.get("review_comments")}')
    for exp, meas in EXPECT.get(name, []):                # declared expectation vs measurement
        if exp in rc and meas in rc and rc.get('corroborates_expected') is not None:
            if (rc[exp] == rc[meas]) != bool(rc['corroborates_expected']):
                bad.append(f'{name} corroborates_expected={rc["corroborates_expected"]} but {exp}={rc[exp]} vs {meas}={rc[meas]}')
    if c['authenticated'] != (c['rate_limit_observed']['limit'] > 60):
        bad.append(f'{name} authenticated={c["authenticated"]} with an observed ceiling of {c["rate_limit_observed"]["limit"]}')
    return bad

schema = json.load(open(os.path.join(H, 'capture-envelope.schema.json'), encoding='utf-8'))
V.check_schema(schema)
v, errs, bad = V(schema), 0, []
for f in FILES:
    doc = json.load(open(os.path.join(H, f), encoding='utf-8'))
    for e in v.iter_errors(doc):
        errs += 1
        print(f'SCHEMA  {f} {list(e.absolute_path)}: {e.message[:160]}')
    bad += reconcile(f, doc)
for b in bad:
    print(f'RECONCILE  {b}')
print(f'{errs} schema error(s) and {len(bad)} reconciliation failure(s) across {len(FILES)} artifacts '
      f'at envelope_schema_version {schema["definitions"]["capture"]["properties"]["envelope_schema_version"]["const"]}')
ok = (errs == 0 and not bad and len(FILES) == 10)
print('VERIFIED' if ok else 'NOT VERIFIED')
sys.exit(0 if ok else 1)
PY
```

`FACT` — result as run here: `0 schema error(s) and 0 reconciliation failure(s) across 10 artifacts at
envelope_schema_version 1.2.0`, `VERIFIED`, exit status `0`, and the schema itself passes draft-07
meta-validation.

`FACT` — **the gate is discriminating rather than decorative, established by running it against
deliberately falsified copies** — copies only, never these artifacts in place. Each row below was
produced by mutating one field in a disposable copy of `upstream-harvest/` and re-running the block
above verbatim:

| Falsification | Caught by | Exit |
| --- | --- | --- |
| `rate_limit_observed.limit` `5000` → `60`, `authenticated` left `true` | C1 **and** the gate identity | `1` |
| the same plus every per-call `x_ratelimit_limit` → `60` | C1, K7 and the gate identity | `1` |
| `authenticated` → `false` with `audit_status` forced to `COMPLETE` | C2 twice — the ceiling and the status — plus C3 twice, on the now-contradicted call record and on the non-empty `omissions`, plus the gate identity | `1` |
| a data call's `http_status` `200` → `401`, `record_status` left `COMPLETE` | K5 and K6 | `1` |
| a payload array emptied and `record_counts` zeroed, `audit_status` untouched | **reconciliation only** — no schema rule fires | `1` |
| the same, additionally zeroing that call's `records_returned` and `records_persisted` | C5 — the arithmetic now closes, so the missing classification is what catches it | `1` |
| `record_counts.entries_total` `53` → `45` against an array of 53 | reconciliation | `1` |
| the `ZERO-RECORD CORROBORATION` note removed from `issue-comments/53.json`; and the same note downgraded from `FACT` to `UNKNOWN` while `audit_status` stayed `COMPLETE` | C5 and C6 respectively | `1` |
| `method` `GET` → `POST`; `url` → another host; `nothing_installed` → `false`; `credential_transport_exposure` removed; an invented `audit_status` value; `records_persisted` → `9999`; a non-date `captured_at_utc`; the wrong `repository`; an emptied `calls[]`; the governance note removed; an unlabelled note; `envelope_schema_version` → `1.0.0` | the pre-existing `1.1.0` constraints, unchanged | `1` |

`INFERENCE` — the fifth and sixth rows are why this section documents two layers rather than one;
reasoning: the fifth is invisible to every schema rule and visible to the arithmetic, the sixth is the
reverse, and a falsification that survives one layer is caught by the other only because both exist.
`FACT` — the pristine artifacts produce `VERIFIED` and exit `0`, so the gate distinguishes the delivered
capture from all twenty-one falsifications rather than merely rejecting everything. `FACT` — the twenty-one
are the counted contents of the table above: seven single-field falsifications, two exercising the
zero-record classification, and the twelve grouped in the final row.

---

## 5. Per-artifact record counts

Every count below was read as the real length of the named data array and cross-checked against the
same artifact's `_capture.record_counts`. All cross-checks agreed; no count in this section is a
restatement of a declared figure alone.

| Artifact | Data key | Record count | Expected | Corroborated? |
| --- | --- | --- | --- | --- |
| `upstream-harvest/issues.json` | `issues` — raw entries returned | **53** | not stated | `FACT` — raw total; see §5.1 for why it exceeds 45 |
| `upstream-harvest/issues.json` | entries carrying a `pull_request` key | **8** | not stated | `FACT` — the pull requests the `/issues` endpoint also serves |
| `upstream-harvest/issues.json` | issues **excluding** pull requests | **45** | **45** | **CORROBORATED** |
| `upstream-harvest/pulls.json` | `pulls` | **8** | **8** | **CORROBORATED** |
| `upstream-harvest/pulls.json` | `repository_metadata` | **1** object | not stated | `FACT` — verbatim `GET /repos/{owner}/{repo}` payload |
| `upstream-harvest/pr-330-reviews.json` | `reviews` | **2** | not stated | `FACT` — from `/pulls/330/reviews` |
| `upstream-harvest/pr-330-reviews.json` | `review_comments` | **3** | not stated | `FACT` — from `/pulls/330/comments` (inline family) |
| `upstream-harvest/pr-330-reviews.json` | `issue_comments` | **5** | **5** | **CORROBORATED** — this is the family that returned 5; see §5.2 |
| `upstream-harvest/pr-330-reviews.json` | `pull_request` | **1** object | not stated | `FACT` — verbatim `GET /pulls/330` payload |
| `upstream-harvest/pr-411-reviews.json` | `reviews` | **0** | **0** (never reviewed) | **CORROBORATED** — HTTP 200, zero records |
| `upstream-harvest/pr-411-reviews.json` | `review_comments` | **0** | **0** (never reviewed) | **CORROBORATED** — HTTP 200, zero records |
| `upstream-harvest/pr-411-reviews.json` | `issue_comments` | **0** | **0** (never reviewed) | **CORROBORATED** — HTTP 200, zero records |
| `upstream-harvest/pr-411-reviews.json` | `pull_request` | **1** object | not stated | `FACT` — verbatim `GET /pulls/411` payload |
| `upstream-harvest/issue-comments/53.json` | `issue_comments` | **0** | not stated | `FACT` — HTTP 200, zero records; GitHub's own `issue.comments` counter is also 0 |
| `upstream-harvest/issue-comments/93.json` | `issue_comments` | **0** | not stated | `FACT` — HTTP 200, zero records; GitHub's own `issue.comments` counter is also 0 |
| `upstream-harvest/issue-comments/150.json` | `issue_comments` | **3** | not stated | `FACT` — GitHub's own `issue.comments` counter is also 3 |
| `upstream-harvest/issue-comments/220.json` | `issue_comments` | **3** | not stated | `FACT` — GitHub's own `issue.comments` counter is also 3 |
| `upstream-harvest/issue-comments/321.json` | `issue_comments` | **4** | not stated | `FACT` — GitHub's own `issue.comments` counter is also 4 |
| `upstream-harvest/issue-comments/323.json` | `issue_comments` | **5** | not stated | `FACT` — GitHub's own `issue.comments` counter is also 5 |

### 5.1 `issues.json` — the three numbers, so no reader mistakes one for another

`FACT` — `GET /repos/{owner}/{repo}/issues` returns **both issues and pull requests**; entries
carrying a `pull_request` key are pull requests. `FACT` — measured on
`upstream-harvest/issues.json`:

| Quantity | Value |
| --- | --- |
| raw entries returned and persisted | **53** |
| entries carrying a `pull_request` key | **8** — numbers `330, 336, 337, 338, 408, 409, 410, 411` |
| issues excluding pull requests | **45** |

`FACT` — the arithmetic closes: `45 + 8 = 53`. `FACT` — all 53 entries carry `state = "open"`.
`INFERENCE` — a raw total of 53 against an expected 45 open issues is therefore the **expected**
arithmetic and not a discrepancy; reasoning: the 8-entry difference is exactly the set of open pull
request numbers, which the same endpoint also serves and which `upstream-harvest/pulls.json`
independently captures as the complete open pull-request set.

`FACT` — second, independent corroboration that no page was lost:
`upstream-harvest/pulls.json` → `repository_metadata.open_issues_count = 53`, which equals the raw
entry total exactly. `INFERENCE` — the correct cross-check is against the raw total rather than
against 45, because GitHub's `open_issues_count` counts open pull requests as well as open issues;
reasoning: the two figures agree at 53 only under that reading, and they do agree.

### 5.2 `pr-330-reviews.json` — the supplied figure of 5 inline review comments is contradicted

**The comparison, stated plainly and in one direction: expected inline review comments 5, actual
inline review comments 3. CONTRADICTED.** `FACT` — the supplied ground truth records five review
comments on pull request 330. `FACT` — the endpoint that returns review comments on a pull request is
`GET /repos/{owner}/{repo}/pulls/330/comments`, and it returned **3**. `FACT` —
`upstream-harvest/pr-330-reviews.json` → `_capture.record_counts` records this as
`aap_expected_review_comments: 5`, `actual_review_comments: 3`,
`expected_vs_actual_verdict: "CONTRADICTED …"` and `corroborates_expected: false`, naming the endpoint
in `aap_expected_review_comments_endpoint`. `UNKNOWN` — what became of the two inline records the
supplied figure implies: no deletion, resolution or hidden-record signal appears in any captured
payload, and an archived repository cannot be re-read to establish one.

A pull-request thread spans three distinct endpoint families, and only a per-family table can show
which one substantiates which claim.

| Endpoint family | Full URL | Records |
| --- | --- | --- |
| Review submissions | `https://api.github.com/repos/openmainframeproject/cobol-check/pulls/330/reviews?per_page=100` | **2** |
| Inline review comments | `https://api.github.com/repos/openmainframeproject/cobol-check/pulls/330/comments?per_page=100` | **3** |
| Conversation comments | `https://api.github.com/repos/openmainframeproject/cobol-check/issues/330/comments?per_page=100` | **5** |

**Two other captured counts also equal 5, and neither is offered as a substitute for the inline
count.** `FACT` — the conversation family `GET /issues/330/comments` returned 5, recorded as
`issue_comments: 5` and named descriptively in `endpoint_matching_expected_count: "issue_comments"`.
`FACT` — `reviews` (2) plus `review_comments` (3) also sums to 5. `INFERENCE` — neither observation
corroborates the supplied figure, and treating either as corroboration would substitute one endpoint
family for another; reasoning: the supplied phrase names review comments, the endpoint that serves
review comments returned 3, and a coincidence of arithmetic in two other families says nothing about
what that endpoint returned. `FACT` — conversation comments are therefore **not** relabelled as
inline review comments anywhere in this capture, and `endpoint_matching_expected_count` is retained
only as a descriptive field naming which family happens to equal 5.

`FACT` — independent corroboration from GitHub's own counters in the captured `pull_request` payload:
`review_comments = 3` and `comments = 5`, agreeing exactly with the per-family record counts.

`FACT` — review submission states observed: `{"CHANGES_REQUESTED": 1, "COMMENTED": 1}`. The
`CHANGES_REQUESTED` submission carries `user.login = "Rune-Christensen"`,
`submitted_at = "2023-10-24T12:30:27Z"`, and both submissions carry `body = ""`.
`FACT` — distinct `user.login` values across all three families: `["Rune-Christensen",
"samdion1994"]`. These are recorded as presence and attribution only; no assessment of the thread's
content appears in this manifest.

### 5.3 `pr-411-reviews.json` — an empty result is HTTP 200 with zero records, not a failure

| Endpoint family | Full URL | HTTP status | Records |
| --- | --- | --- | --- |
| Review submissions | `https://api.github.com/repos/openmainframeproject/cobol-check/pulls/411/reviews?per_page=100` | **200** | **0** |
| Inline review comments | `https://api.github.com/repos/openmainframeproject/cobol-check/pulls/411/comments?per_page=100` | **200** | **0** |
| Conversation comments | `https://api.github.com/repos/openmainframeproject/cobol-check/issues/411/comments?per_page=100` | **200** | **0** |

`FACT` — `upstream-harvest/pr-411-reviews.json` → `_capture.record_counts` records
`reviews: 0`, `review_comments: 0`, `issue_comments: 0`, `total: 0`,
`empty_families: ["reviews", "review_comments", "issue_comments"]` and
`all_calls_http_200: true`; `_capture.failures` is `[]`.

`FACT` — all three keys are present as **empty arrays** rather than omitted. `INFERENCE` — that
distinction is load-bearing: a missing key would say nothing was queried, whereas an empty array
says the query ran and returned nothing; reasoning: each family has its own HTTP 200 row in section
4 (rows 15, 16, 17), so the query provably ran.

`FACT` — independent corroboration from GitHub's own counters in the captured `pull_request` payload:
`review_comments = 0`, `comments = 0`, `requested_reviewers` empty, `requested_teams` empty.
`INFERENCE` — the zeros are an observation about the pull request and not an artifact of quota
exhaustion; reasoning: those three requests observed `x-ratelimit-limit = 5000` with
`x-ratelimit-remaining` of 4990, 4989 and 4988 respectively, so the empty-body failure mode that
requires an exhausted quota cannot apply, and GitHub's own counters agree at zero.

### 5.4 `issue-comments/` — one row per file present in the directory

`FACT` — the directory contains **six** files. Four correspond to issue numbers carried in the
ground truth; two — `upstream-harvest/issue-comments/93.json` and
`upstream-harvest/issue-comments/150.json` — were added to record the resolution of flags given only
by description (see section 9). Those two are part of the twelve-file artifact set declared in §1.1
and are load-bearing for the totals in this section and the verdicts in §7.3; the row counts below
cannot be reconciled without them.

| File | Issue | Harvested title (verbatim) | Comments captured | GitHub's own `issue.comments` counter |
| --- | --- | --- | --- | --- |
| `upstream-harvest/issue-comments/53.json` | #53 | `Review and correct localized messages` | **0** | 0 |
| `upstream-harvest/issue-comments/93.json` | #93 | `Add autoincremented build numbers to the gradle build` | **0** | 0 |
| `upstream-harvest/issue-comments/150.json` | #150 | `Produce Sonarqube formatted output for test results.` | **3** | 3 |
| `upstream-harvest/issue-comments/220.json` | #220 | `Build cobol-check as GraalVM native-image` | **3** | 3 |
| `upstream-harvest/issue-comments/321.json` | #321 | `FR: please co-publish vscode extension on Open VSX Registry` | **4** | 4 |
| `upstream-harvest/issue-comments/323.json` | #323 | `IMS` | **5** | 5 |

`FACT` — for all six files the captured array length equals GitHub's own `issue.comments` counter in
the same artifact's `issue` payload, and the filename number equals `_capture.issue_number`.
`FACT` — all six carry `_capture.failures = []`. `FACT` — all six `issue` payloads carry no
`pull_request` key, so all six are issues rather than pull requests served through the issues
endpoints. `INFERENCE` — the counter agreement is a genuinely independent completeness check;
reasoning: the counter is computed server-side and returned by a different endpoint
(`/issues/{n}`) than the one that returned the comment records (`/issues/{n}/comments`), so
agreement between them cannot be produced by a client-side error in either.

---

## 6. Ground-truth corroboration

Each carried-forward claim is stated with its verdict and the evidence that settles it. Contradictions
are shown explicitly with evidence rather than quietly corrected.

| # | Carried-forward claim | Verdict | Evidence |
| --- | --- | --- | --- |
| 1 | 45 open issues | **CORROBORATED** | `FACT` — `upstream-harvest/issues.json`: 53 raw entries, 8 carrying a `pull_request` key, **45** issues excluding pull requests; `_capture.record_counts.issues_excluding_pull_requests = 45` |
| 2 | 8 open pull requests | **CORROBORATED** | `FACT` — `upstream-harvest/pulls.json`: `len(pulls) = 8`, numbers `330, 336, 337, 338, 408, 409, 410, 411`; `_capture.record_counts.pulls_total = 8` |
| 3 | All 8 pull requests target `Developer` | **CORROBORATED** | `FACT` — `base.ref = "Developer"` on 8 of 8; `_capture.record_counts.base_ref_counts = {"Developer": 8}`. Two independent corroborations: `repository_metadata.default_branch = "Developer"`, and `.github/workflows/VerifyAction.yml:4-5` triggering on `pull_request:` / `branches: [ main,Developer ]` |
| 4 | All 8 locked by the archive | **CORROBORATED, with the two mechanisms kept distinct** | `FACT` — per-pull-request `locked = true` on 8 of 8, **and** `repository_metadata.archived = true`. See §6.1 |
| 5 | #330/#336/#337/#338 originate on upstream itself | **CORROBORATED** | `FACT` — observed `head.repo.full_name = "openmainframeproject/cobol-check"` for all four |
| 6 | #408/#409/#410 originate from `generalmotors/openmainframeproject-cobol-check` | **CORROBORATED** | `FACT` — observed `head.repo.full_name = "generalmotors/openmainframeproject-cobol-check"` for all three |
| 7 | #411 originates from `Living-Mainframe/cobol-check` | **CORROBORATED** | `FACT` — observed `head.repo.full_name = "Living-Mainframe/cobol-check"`; `head.ref = "fix-copybook-expansion-WSsection"`; `head.sha = "e88bc1a0b82d92c17c4c2ee73bab588f0b2d813d"` |
| 8 | #53 localization is present in the capture | **CORROBORATED** | `FACT` — `upstream-harvest/issues.json` and `issue-comments/53.json`: title `Review and correct localized messages`, labels `["enhancement", "help wanted", "good first issue"]`, `state = "open"` |
| 9 | #220 GraalVM native image is present | **CORROBORATED** | `FACT` — title `Build cobol-check as GraalVM native-image`, labels `[]`, `state = "open"` |
| 10 | #321 Open VSX is present | **CORROBORATED** | `FACT` — title `FR: please co-publish vscode extension on Open VSX Registry`, labels `[]`, `state = "open"` |
| 11 | #323 IMS is present | **CORROBORATED** | `FACT` — title `IMS`, labels `[]`, `state = "open"` |
| 12 | The overlap pair #337 / #408 exists as open pull requests | **CORROBORATED** | `FACT` — both numbers present in `upstream-harvest/pulls.json` with `state = "open"` |
| 13 | The overlap pair #410 / #411 exists as open pull requests | **CORROBORATED** | `FACT` — both numbers present in `upstream-harvest/pulls.json` with `state = "open"` |
| 14 | #330 has five review comments | **CONTRADICTED — expected inline review comments 5, actual 3** | `FACT` — `review_comments = 3` from `/pulls/330/comments`, the endpoint that serves review comments. `FACT` — two other families coincidentally total 5 and neither is offered as a substitute: `issue_comments = 5` from `/issues/330/comments`, and `reviews` (2) + `review_comments` (3). `UNKNOWN` — the fate of the two implied inline records. See §5.2 |
| 15 | #411 was never reviewed | **CORROBORATED** | `FACT` — all three families returned HTTP 200 with zero records; GitHub's own counters agree at zero. See §5.3 |
| 16 | The session observed an unauthenticated ceiling of 60 | **CONTRADICTED for the capture; the value was observed only on a deliberate control probe** | See §6.2 |
| 17 | `jq` and the `gh` CLI are absent from the image | **PARTLY CONTRADICTED — `jq` is present** | See §3.3 |
| 18 | `curl` is 8.5.0 and `python3` is 3.12.3 | **CONTRADICTED — both present at newer versions** | See §3.3 |

Scope note, not a measurement: the rows above concerning the four declared non-goal issues and the
two overlap pairs are **presence reporting**. They record that those numbers exist in the capture and
what their harvested titles and labels are, nothing more. No verdict, ranking, sequencing or
adjudication is attached to any of them here; that belongs to `PROGRAM-PLAN.md` Deliverable A #5.

### 6.1 Lock state — two different mechanisms that happen to agree

`FACT` — every one of the 8 captured pull requests carries `locked = true`, and
`repository_metadata.archived = true`.

`INFERENCE` — these are two distinct mechanisms and are reported distinctly rather than one being
inferred from the other; reasoning: archiving imposes read-only status on an entire repository,
whereas the per-pull-request `locked` flag is an independently settable conversation lock, so either
could in principle be set without the other. `FACT` — the honest-if-divergent case did not arise
here: both flags are set, so they agree, and `upstream-harvest/pulls.json` → `_capture.notes[3]`
records that had `locked` been `false` while `archived` was `true`, that would have been recorded as
such rather than smoothed over.

**Both raw artifacts now agree with this explanation.** `FACT` — an earlier revision of
`upstream-harvest/pr-411-reviews.json` → `_capture.notes[12]` closed with the clause "the locked flag
is the archive's doing", which asserted a causal relationship this section explicitly declines to
draw. That note now states the same two-mechanism distinction in the same terms: `locked = true` and
`archived = true` as observed values, the reasoning that GitHub permits a locked conversation on an
unarchived repository and an archived repository whose pull requests are unlocked, and `UNKNOWN` for
which mechanism set `locked` and when. `INFERENCE` — co-occurrence of two independently settable
flags cannot establish that one caused the other, so the only defensible record is both observations
plus an explicit `UNKNOWN` for the cause.

`FACT` — a third, stronger observation is available from the API itself:
`repository_metadata.permissions = {admin: false, maintain: false, push: false, triage: false,
pull: true}`. `INFERENCE` — write access to this repository was not merely prohibited by instruction
but unavailable to the credential used; reasoning: with `pull` as the only granted permission there
is no permission under which a write request could have succeeded.

### 6.2 The rate-limit ceiling — a contradiction that must be stated in both directions

`FACT` — the supplied ground truth for this artifact records that the session observed a core rate
limit of 60, i.e. unauthenticated. **OBSERVED**: every artifact records
`_capture.rate_limit_observed.limit = 5000` with `remaining = 5000`, and 36 of the 37 itemised
requests observed `x-ratelimit-limit = 5000`, including **all 26** non-`/rate_limit` requests.
The capture was authenticated.

`FACT` — the value 60 **was** observed, exactly once: `upstream-harvest/issues.json` →
`_capture.calls[1]`, a deliberate unauthenticated control `GET https://api.github.com/rate_limit`
with `x-ratelimit-limit = 60`, `x-ratelimit-remaining = 58`, `x-ratelimit-reset = 1786685888` and zero
harvest records persisted (row 2 of section 4).

`INFERENCE` — both statements are true and neither cancels the other: the ceiling of 60 belongs to a
control request issued *without* the credential in order to demonstrate that the credential was
honoured on the requests that mattered; reasoning: the same URL returned 60 unauthenticated and 5000
authenticated within the same capture, which is precisely the discriminating observation the control
exists to produce. `INFERENCE` — the consequence is the single most important statement about this
capture's trustworthiness: the empty-body truncation failure mode requires an exhausted
unauthenticated quota, and no data request in this capture ran under one, so every zero record count
recorded in section 5 is an observation about the upstream repository rather than an artifact of
quota exhaustion.

---

## 7. Completeness and truncation assessment

### 7.1 Pagination

`FACT` — the default page size for GitHub list endpoints is 30. Every list request in this capture
was sent with `per_page=100` explicitly — visible in the URL of rows 4, 6, 9, 10, 11, 15, 16, 17,
20, 23, 26, 29, 32, 35 and 37 of section 4.

`FACT` — `link_next` is `null` on **all 37** itemised requests, so no `Link: rel="next"` relation was
left unfollowed anywhere in the capture. `FACT` — the 15 rows listed immediately above are the
collection endpoints, where `null` means the relation was followed to exhaustion; the remaining 22 rows
carry `paginated = false` and are not paginated at all. `FACT` — every artifact's `_capture.notes`
records that the `Link rel="next"` relation was traversed to exhaustion and that the final response
carried no such relation.

`FACT` — page-loss reconciliation over the data endpoints: for every artifact, the sum of
`records_persisted` across its data requests equals the real length of the corresponding data array,
per family where families are recorded. `FACT` — the one row where returned and persisted differ is
row 37, the duplicate open-issues listing whose body was not retained (§4.3): it returned 53 and
persisted 0, and the canonical capture of those same 53 entries is row 4.

| Artifact | Reconciliation | Result |
| --- | --- | --- |
| `upstream-harvest/issues.json` | `issues`: Σ 53 vs `len` 53 | equal |
| `upstream-harvest/pulls.json` | `pulls`: Σ 8 vs `len` 8 | equal |
| `upstream-harvest/pr-330-reviews.json` | `reviews` 2 vs 2 · `review_comments` 3 vs 3 · `issue_comments` 5 vs 5 | equal |
| `upstream-harvest/pr-411-reviews.json` | `reviews` 0 vs 0 · `review_comments` 0 vs 0 · `issue_comments` 0 vs 0 | equal |
| `upstream-harvest/issue-comments/53.json` | `issue_comments`: Σ 0 vs `len` 0 | equal |
| `upstream-harvest/issue-comments/93.json` | `issue_comments`: Σ 0 vs `len` 0 | equal |
| `upstream-harvest/issue-comments/150.json` | `issue_comments`: Σ 3 vs `len` 3 | equal |
| `upstream-harvest/issue-comments/220.json` | `issue_comments`: Σ 3 vs `len` 3 | equal |
| `upstream-harvest/issue-comments/321.json` | `issue_comments`: Σ 4 vs `len` 4 | equal |
| `upstream-harvest/issue-comments/323.json` | `issue_comments`: Σ 5 vs `len` 5 | equal |

`INFERENCE` — no page was silently truncated anywhere in the capture; reasoning: a lost page would
produce either a `records_returned` sum exceeding the persisted array length on a retained family, or a
non-null `link_next` on a final response. Neither appears, in any artifact.

### 7.2 The silent-truncation signature

`FACT` — the signature to look for is an HTTP 200 response carrying an **empty body**, which is what
an exhausted unauthenticated quota returns instead of an error.

`FACT` — no non-2xx status appears anywhere: **36 of the 37** itemised requests recorded HTTP 200, and
the 37th is row 31, whose status is `UNKNOWN` because it was never recorded (§4.1) — not because a
non-2xx status was observed. `INFERENCE` — row 31 was not a truncated response either; reasoning: its
rate-limit ceiling reads 5000, so the quota-exhaustion mechanism that produces empty bodies was not
engaged at that instant, and the values it did yield were read from a decoded response body. `FACT` — no
zero-length response body is recorded in any artifact, and every artifact's `_capture.failures` is
`[]`. `FACT` — `upstream-harvest/pr-411-reviews.json` → `_capture.notes` records that each of that
pull request's three family responses carried an empty **JSON array literal** as its body.
`INFERENCE` — an empty JSON array is not an empty body, and the distinction is the whole point:
the former is a well-formed response saying "no records exist", the latter is the truncation
signature; reasoning: an empty body cannot be decoded as JSON at all, whereas these bodies decoded
successfully into empty arrays whose lengths were then recorded and reconciled.

`FACT` — **five** data requests returned zero records: rows 15, 16 and 17 (the three #411 families) and
rows 20 and 23 (the #53 and #93 comment endpoints). Under the normalised convention of §4 a
repository-object probe returns one document rather than zero records, so row 3 is no longer counted
here; its `records_persisted` is 0 for a different reason, which is that its body was not retained
(§4.3). `INFERENCE` — every one of the five zero-record readings is independently corroborated by a
server-side counter returned from a different endpoint: `pull_request.review_comments = 0` and
`pull_request.comments = 0` for #411, and `issue.comments = 0` for both #53 and #93; reasoning: a
truncated capture would show a non-zero server-side counter beside a zero captured array, and no
artifact shows that pattern.

`FACT` — was a ceiling of 60 observed? Yes, on exactly one request, and it was the deliberate
unauthenticated control described in §6.2. `FACT` — authenticated `x-ratelimit-remaining` across the
capture ranged from **4960** to **5000** of 5000. `INFERENCE` — the quota never came near exhaustion,
so the truncation mechanism was never engaged; reasoning: the mechanism requires `remaining` to reach
zero and the lowest authenticated reading observed is 4960.

### 7.3 Per-artifact verdict

Two dimensions are judged separately, because they fail for different reasons and a reader needs both.
**Harvested payload** answers "is the data this artifact exists to hold complete?" **Audit metadata**
answers "is the record of how it was obtained complete?" `FACT` — the audit verdict is the artifact's own
`_capture.audit_status`, which is `PARTIAL` whenever any of its call records is `PARTIAL`.

| Artifact | Harvested payload | Audit metadata | Basis |
| --- | --- | --- | --- |
| `upstream-harvest/issues.json` | **COMPLETE** | **PARTIAL** | `FACT` — HTTP 200, `link_next=null`, Σ persisted 53 = `len` 53, `failures=[]`; `INFERENCE` — corroborated independently by `open_issues_count = 53`. `FACT` — PARTIAL audit: the row-3 repository-probe body is not retained (§4.3), recorded in `_capture.omissions` |
| `upstream-harvest/pulls.json` | **COMPLETE** | **COMPLETE** | `FACT` — HTTP 200, `link_next=null`, Σ persisted 8 = `len` 8, `failures=[]`, 8 of 8 expected, `repository_metadata` retained verbatim, `omissions=[]` |
| `upstream-harvest/pr-330-reviews.json` | **COMPLETE**, and it **contradicts** the supplied count | **COMPLETE** | `FACT` — HTTP 200 on all five requests, `link_next=null`, all three families reconciled, `omissions=[]`; `INFERENCE` — corroborated by the payload's own counters (3 and 5). `FACT` — separately from completeness, the supplied ground truth expects **5 inline review comments** and the inline endpoint `pulls/330/comments` returned **3**, so the expected figure is contradicted for the family it names (§5.2, §6 row 14). Completeness and agreement-with-the-ground-truth are different claims and only the first holds |
| `upstream-harvest/pr-411-reviews.json` | **COMPLETE** (of an empty thread) | **PARTIAL** | `FACT` — HTTP 200 on all six requests, `all_calls_http_200 = true`, `failures=[]`; `INFERENCE` — zeros corroborated by the payload's own counters, and quota exhaustion excluded by §7.2. `FACT` — PARTIAL audit: the row-14 repository-probe body is not retained (§4.3) |
| `upstream-harvest/issue-comments/53.json` | **COMPLETE** | **COMPLETE** | `FACT` — HTTP 200, zero records, `issue.comments = 0`, `omissions=[]` |
| `upstream-harvest/issue-comments/93.json` | **COMPLETE** | **COMPLETE** | `FACT` — HTTP 200, zero records, `issue.comments = 0`, `omissions=[]` |
| `upstream-harvest/issue-comments/150.json` | **COMPLETE** | **COMPLETE** | `FACT` — HTTP 200, 3 records, `issue.comments = 3`, `omissions=[]` |
| `upstream-harvest/issue-comments/220.json` | **COMPLETE** | **COMPLETE** | `FACT` — HTTP 200, 3 records, `issue.comments = 3`, `omissions=[]` |
| `upstream-harvest/issue-comments/321.json` | **COMPLETE** | **PARTIAL** | `FACT` — payload COMPLETE: HTTP 200, 4 records, `issue.comments = 4`. `FACT` — PARTIAL audit: the authentication-gate record was not written at capture time and is itemised as row 31 with `record_status = "PARTIAL"` and two `unknown_fields` (§4.1) |
| `upstream-harvest/issue-comments/323.json` | **COMPLETE** | **PARTIAL** | `FACT` — payload COMPLETE: HTTP 200, 5 records, `issue.comments = 5`. `FACT` — PARTIAL audit: the row-37 duplicate open-issues body is not retained (§4.3); the canonical capture of that endpoint is `issues.json` |

`FACT` — no artifact is `FAILED`, and every artifact's harvested payload is `COMPLETE`. `FACT` — **four
artifacts carry `audit_status = "PARTIAL"`**: `issues.json`, `pr-411-reviews.json`,
`issue-comments/321.json` and `issue-comments/323.json`. The earlier statement that no artifact was
`PARTIAL` did not hold and is corrected here rather than left standing.

`INFERENCE` — the two axes must not be collapsed into a single verdict; reasoning: a missing record
*about* a request — a gate probe's status and count, or a probe body observed but not stored — is not a
record *of* harvested data, so it cannot remove or alter any comment, issue or pull request in the
payload. `FACT` — `321.json`'s payload is corroborated independently: 4 records returned,
`record_counts.issue_comments = 4`, GitHub's own `issue.comments` counter = 4, and
`_capture.failures = []`. `INFERENCE` — the classification is nonetheless not softened to `COMPLETE`
with a caveat; reasoning: this document's own purpose is to be the sole surviving evidence that a
non-repeatable capture is complete, and a verdict that reads `COMPLETE` while one required per-call
record was never written would overstate exactly the thing the document exists to establish. `FACT` —
`recovery_action_taken = "NONE"` in that artifact, which records why in `recovery_action_refused`: a
request issued now would carry a different timestamp and a different remaining count and so would not
be the missing record, and fabricating the two values is refused.

`FACT` — three known limits on what this capture covers, recorded so that completeness is not
overclaimed. The upstream **wiki** was not captured by any request in section 4. The upstream **CI
run history** was not captured by any request in section 4. **Pull-request patch or diff content**
was not captured; `upstream-harvest/pulls.json` → `_capture.notes` records that no patch files and no
fetched refs are stored in these artifacts. `UNKNOWN` — whether
`Living-Mainframe/cobol-check` holds commits beyond pull request #411 is not established by this
capture; `upstream-harvest/pulls.json` and `pr-411-reviews.json` both record that question as
deliberately unanswered here, and both persist `head.sha = e88bc1a0b82d92c17c4c2ee73bab588f0b2d813d`
as the anchor against which it can be settled in a working clone.

---

## 8. Failures and omissions

**Failures: none. Omissions: four, listed below.** The two are different things and are answered
separately, because a section that reports "none" to both when four recording gaps exist is worse than
no section at all.

### 8.1 Failures — none

`FACT` — no request failed. **36 of the 37** itemised requests in section 4 returned HTTP 200; the 37th
is row 31, whose status was never recorded and is `UNKNOWN` rather than non-2xx (§4.1). `FACT` —
`_capture.failures` is the empty array `[]` in all ten artifacts: `issues.json`, `pulls.json`,
`pr-330-reviews.json`, `pr-411-reviews.json`, and `issue-comments/53.json`, `93.json`, `150.json`,
`220.json`, `321.json`, `323.json`.

`FACT` — there is therefore no attempted-and-failed URL to list, and no HTTP status or rate-limit
header triple to record for one. This is stated as "none" by observation rather than left out.

### 8.2 Omissions — four recording gaps, each carried in the artifact's own `_capture.omissions`

`FACT` — none of these is a request failure, and none is recoverable: re-querying an archived,
read-only repository is prohibited, so every gap is left as `UNKNOWN`/`PARTIAL` rather than
reconstructed or filled with a plausible value. `INFERENCE` — none of the four affects any count in
section 5; reasoning: gap 1 concerns a `/rate_limit` gate probe, which returns quota state rather than
harvested data, and gaps 2 through 4 concern corroboration probes and one duplicate listing whose
canonical capture is retained elsewhere — so no comment, issue, pull request or review record depends on
any of them. What they affect is the *provenance* claim, which is why this section does not report
"none".

| # | Artifact | Kind | What is missing | What survives | Consequence |
| --- | --- | --- | --- | --- | --- |
| 1 | `issue-comments/321.json` | `per_call_record_not_written` | The per-call record of the authentication-gate request: HTTP status, record count and exact ordinal position | `_capture.rate_limit_observed` and the artifact's notes, from which row 31 is itemised with `record_status = "PARTIAL"` and both fields in `unknown_fields`, and `_capture.provenance_envelope.unrecorded_requests[0]`, which carries the two unmeasured values as the literal string `"UNKNOWN"` | `audit_status = "PARTIAL"`. The harvested comment payload is unaffected: 4 records, `issue.comments = 4` |
| 2 | `issues.json` | `response_body_not_retained` | The raw `GET /repos/openmainframeproject/cobol-check` response object of row 3 | Six fields quoted in that artifact's notes: `full_name`, `archived`, `disabled`, `has_issues`, `default_branch`, `open_issues_count` | `audit_status = "PARTIAL"`. The repository-object corroboration cannot be re-derived from this artifact alone; `pulls.json` → `repository_metadata` is a different response and is not substituted |
| 3 | `pr-411-reviews.json` | `response_body_not_retained` | The raw `GET /repos/openmainframeproject/cobol-check` response object of row 14 | Four fields quoted in that artifact's notes: `archived`, `disabled`, `default_branch`, `permissions` | `audit_status = "PARTIAL"`. Same limit as above; the empty-review-thread result itself is unaffected |
| 4 | `issue-comments/323.json` | `response_body_not_retained` | The raw 53-entry open-issues list response body of row 37, a duplicate call | The two flagged-issue resolution fields quoted in that artifact's notes, plus the recorded count of 53 returned | `audit_status = "PARTIAL"`. Nothing is lost to the programme: row 4 in `issues.json` is the canonical retained capture of the same endpoint |

`FACT` — two serialisation inconsistencies are also on record and are recording variations rather than
failures or omissions: the one sub-second timestamp noted in §3.1, and the three different conventions
for `records_returned` on `/rate_limit` rows noted in §4.2 — the latter now normalised and
schema-enforced. Neither affects any count in section 5.

---

## 9. Flagged-issue resolution for `issue-comments/`

`FACT` — the directory holds six files, listed with their counts in §5.4. Four are named **by number**
in the carried-forward ground truth; two exist because a flag given only **by description** was
resolved to a number by citing a harvested title.

Both resolutions below rest on captures that a scope list built from the numbered flags alone would
omit: `upstream-harvest/issue-comments/93.json` and `upstream-harvest/issue-comments/150.json` are the
two files the description-based flags resolved to, and they are the evidence for the resolutions
themselves. They are declared load-bearing in §1.1 and **may not be deleted to fit a shorter list**.

### 9.1 Given by number — no resolution step required

| Issue | Harvested title (verbatim) | Harvested labels | Capture file |
| --- | --- | --- | --- |
| **#53** | `Review and correct localized messages` | `["enhancement", "help wanted", "good first issue"]` | `upstream-harvest/issue-comments/53.json` |
| **#220** | `Build cobol-check as GraalVM native-image` | `[]` | `upstream-harvest/issue-comments/220.json` |
| **#321** | `FR: please co-publish vscode extension on Open VSX Registry` | `[]` | `upstream-harvest/issue-comments/321.json` |
| **#323** | `IMS` | `[]` | `upstream-harvest/issue-comments/323.json` |

`FACT` — all four numbers are present in `upstream-harvest/issues.json` among the 45 true issues,
each with `state = "open"` and with no `pull_request` key, and each was independently re-read from
`GET /repos/openmainframeproject/cobol-check/issues/{n}` (rows 21, 30, 32 and 35 of section 4) with
the same title. This is presence reporting: the numbers exist in the capture and these are their
harvested titles and labels. Nothing further is asserted about them here.

### 9.2 Given by description — resolved to a number by citation only

| Description in the ground truth | Status | Issue | Cited harvested title (verbatim) | Cited harvested labels | Capture file |
| --- | --- | --- | --- | --- | --- |
| the SonarQube-output issue | **RESOLVED** | **#150** | `Produce Sonarqube formatted output for test results.` | `["enhancement", "good first issue"]` | `upstream-harvest/issue-comments/150.json` |
| the build-numbers issue | **RESOLVED** | **#93** | `Add autoincremented build numbers to the gradle build` | `["enhancement", "good first issue", "infrastructure"]` | `upstream-harvest/issue-comments/93.json` |

`FACT` — both mappings are recorded in `upstream-harvest/issue-comments/323.json` →
`_capture.flagged_issue_resolution`, each with `status: "RESOLVED"`, its `issue_number`, its
`cited_title`, its `cited_labels`, its `capture_file` and its evidence. Both were re-verified here
directly against `upstream-harvest/issues.json`: #150 and #93 are present among the 45 true issues
with exactly those titles and labels.

**#150 — the citation, and why it is unambiguous.** `FACT` — a case-insensitive scan of all 45
harvested true-issue titles and labels for `/sonar|static[- ]?analys|code quality|quality gate/`
returned exactly one candidate, #150. `FACT` — the harvested title itself carries the
SonarQube-formatted-output wording that the description denotes, so the citation is the title rather
than an interpretation of it. `FACT` — the title agrees across two independent requests, the list
endpoint (rows 4 and 36) and `GET /issues/150` (row 27). `INFERENCE` — a single candidate means no
ambiguity has to be adjudicated; reasoning: there is no second harvested title that the description
could denote.

**#93 — the citation, and the near-miss recorded rather than hidden.** `FACT` — the harvested title
contains the literal phrase "build numbers", which is the citation this resolution rests on. `FACT` —
a deliberately broad scan for
`/build[\s\-_]*number|\bversion|\bversioning|release[\s\-_]*number|semver|auto[\s\-_]?increment|\bnumbering/`
returned two candidates: #93 and #343, whose harvested title is verbatim
`FreeBSD version in wiki does not make sens`. `FACT` — a scan narrowed to numbering of the product's
builds or releases,
`/build[\s\-_]*number|release[\s\-_]*number|version[\s\-_]*number|auto[\s\-_]?increment/`, returned
only #93. `INFERENCE` — #343 matches on the bare word "version" alone and its harvested title scopes
it to a FreeBSD release recorded in the project wiki, so it is not a second plausible reading of the
description; reasoning stated with both candidates quoted so the determination stays checkable
without re-querying the archive. `FACT` — the title of #93 agrees across the list endpoint (rows 4
and 36) and `GET /issues/93` (row 24).

`FACT` — in-repo material independently shows the condition the build-numbers description denotes:
`CHANGELOG.md:23` reads `## \[0.2.19\] 2025-11-26` and `CHANGELOG.md:26` reads
`## \[0.2.19\] 2025-10-24` — two entries carrying the same version number. This is recorded as
corroborating context for the description, not as the citation; the citation is the harvested title.

### 9.3 Unresolved

**None.** `FACT` — every flag carried in the ground truth for this directory, whether given by number
or by description, resolved to a harvested number with a cited title. No entry is `UNRESOLVED`, and
no file in `issue-comments/` was created for an unresolved or guessed number: all six filenames match
their artifact's own `_capture.issue_number` and all six numbers are present among the 45 harvested
true issues.

---

## 10. Standards applied

`FACT` — `review_rules` returns exactly `"No user rules provided."` The project's on-disk rules
document is therefore **empty**: it contains no rule text at all.

Consequences, stated so no reader goes looking for something that is not there:

- **No user-specified rule governs this capture or this manifest.** None is cited here, because there
  is none to cite, and none was invented.
- The absence of rules is **not** treated as licence to hold the work to a weaker standard.
  Enterprise-standard best practice applies instead, and this document is where that is demonstrated
  rather than asserted:

| Standard | How this manifest honours it |
| --- | --- |
| **Evidence over assertion** | Every statement carries `FACT` with an artifact path, `file:line` or retrieved URL; `INFERENCE` with its reasoning; or `UNKNOWN`. No label is silently upgraded. The genuinely unmeasured values in this document — the HTTP status and record count of row 31, the HTTP client of `issue-comments/53.json`, and the credential's argument-vector exposure for the four artifacts named in §3.3.1 — are labelled `UNKNOWN` rather than filled in, and every authored note in all ten sibling captures opens with its own evidence label so no authored claim can be mistaken for captured data. Three `capture_driver` values that an earlier normalisation had asserted without observing are corrected in §3.3 rather than left standing. |
| **Honest completeness, judged on two axes** | A complete harvested payload does not make an incomplete audit record complete. §7.3 verdicts each artifact on both axes, four artifacts carry `audit_status = "PARTIAL"`, §8 lists all four omissions in full, and the earlier claims that no artifact was `PARTIAL` and that omissions were "none" are corrected rather than left standing. |
| **One checked schema instead of ten conventions, and checks that can fail** | `upstream-harvest/capture-envelope.schema.json` fixes the envelope shape, the count convention and the retention vocabulary; nine cross-field invariants at `1.2.0` additionally make a self-contradictory envelope invalid, and the two identities draft-07 cannot express are carried by the validation gate beside it rather than dropped. All ten artifacts validate with zero errors and zero reconciliation failures, and **every documented check in this manifest exits non-zero on violation** — §3.2's request accounting, §4.4's envelope gate and §12's governance-note enforcement — so each composes into a precondition instead of printing a figure a reader must interpret. Nineteen falsified copies were each rejected with a non-zero exit while the delivered artifacts pass (§4.4). The schema deliberately does not constrain harvested GitHub payloads, so no schema rule can ever motivate editing raw third-party data. |
| **No fabricated data** | Every count in sections 4, 5 and 7 was read mechanically out of the artifacts and reconciled against both `_capture.record_counts` and the real length of each data array before being written. Where an artifact recorded `null`, `null` is reproduced. |
| **Auditable reproducibility** | Section 4 names every request with its full URL, status, record count, pagination state and rate-limit headers, in a stated reproducible order; section 7 shows the reconciliation arithmetic; section 3 records the tool versions and the exact activation command, so the audit can be re-performed against the artifacts without re-querying the archive. |
| **Contradictions stated, never quietly corrected** | Six divergences from the supplied ground truth are reported with evidence and without smoothing: the expected count of five inline review comments on #330 is contradicted at 3 (§5.2, §6 row 14), the planning artifacts are tracked in the fork although the requirement places them outside it (§1, §11), `jq` is present (§3.3), `curl` and `python3` differ in version (§3.3), the host OS differs from two separate documented values (§3.3), and the rate-limit ceiling of 60 belongs only to a deliberate control probe (§6.2). |
| **Secret hygiene, claimed only as far as it is checkable** | No credential **value**, prefix or fragment appears anywhere in this document, in any filename in `upstream-harvest/`, or in any artifact field, and the credential is referred to only by role. Request-header text *is* quoted — in §3.3.1's transport rule and again in §11, which restates it — always with the value elided or replaced by a conversion specifier, so no occurrence carries a credential. §11 states that as a property of every occurrence rather than as a count, carries the gate that re-derives it, and records that an earlier revision of both statements asserted the absolute form and was falsified by this document itself. |
| **Credential exposure classified, never attested away** | §3.3.1 separates two claims that are easy to conflate: the credential is absent from every artifact **at rest**, which is checkable, and whether it ever entered a process argument vector, which for four artifacts is not. Those four carry `credential_transport_exposure = "UNKNOWN"`, one over-broad clause asserting non-exposure was retracted, and the schema's three-value enum makes the unprovable claim unwritable in future. The transport rule that binds any future authenticated capture is stated as a rule, because this one cannot be re-run. |
| **Public data governed by purpose and lifetime, not by redaction** | §12 bounds purpose, access, publication, retention, disposal, deleted-upstream-content handling and secondary use for the contributor data these artifacts retain, and prohibits profiling, enrichment, outreach and model training on it. It licenses no edit to any harvested payload, and its presence is enforced per artifact by the schema rather than by convention. |
| **Read-only least privilege** | The credential carried `pull` as its only granted permission (`repository_metadata.permissions`), so write access was unavailable rather than merely forbidden. See section 11. |
| **Verify and record, never install** | Every tool version in §3.3 was probed in this container rather than transcribed, and nothing was installed to produce either the capture or this manifest. |
| **Scope discipline** | This manifest reports what was captured and whether it is complete. It contains no classification, no ranking, no sequencing and no adjudication; those belong to `PROGRAM-PLAN.md` Deliverable A #5. |

`FACT` — the binding constraints this capture was executed under are prompt-level constraints from
the Agent Action Plan (§0.2.2, §0.5.1.1, §0.8.3, §0.10.1, §0.10.2, §0.10.3), not user-specified rules.
`INFERENCE` — the distinction matters operationally rather than academically: prompt-level
constraints are not retrievable through the rules mechanism, so a later session that queries
`review_rules` will correctly find nothing and must carry those constraints forward from the plan
document instead; reasoning: `review_rules` reads the on-disk rules document, and that document is
empty.

---

## 11. Read-only attestation

`openmainframeproject/cobol-check` is **archived and read-only**. `FACT` —
`upstream-harvest/pulls.json` → `repository_metadata.archived = true`, `disabled = false`, retrieved
from `https://api.github.com/repos/openmainframeproject/cobol-check`.

**Every call was a `GET`.** `FACT` — all 37 requests itemised in section 4 carry `method = "GET"` in
their `_capture.calls` entry, including row 31, the `GET https://api.github.com/rate_limit` whose
per-call record was reconstructed from retained evidence rather than invented (§4.1). `FACT` — the
envelope schema fixes `method` to the constant `"GET"`, so a non-`GET` record could not validate.
`FACT` — every artifact attests the same in its own `_capture.notes`, and
`upstream-harvest/pulls.json` → `_capture.tooling.http_methods_issued` records exactly `["GET"]`.

**No write of any kind was attempted.** `FACT` — no `POST`, `PATCH`, `PUT` or `DELETE` request was
issued against `openmainframeproject/cobol-check` by any code path in this capture. No push, no
comment, no reply, no review submission, no reaction, no label, no assignment, no reopen, no issue
edit and no attempt of any kind. `FACT` —
`upstream-harvest/issue-comments/93.json`, `150.json` and `323.json` each record that the capture
driver contains no code path capable of issuing `POST`, `PATCH`, `PUT` or `DELETE`, and
`upstream-harvest/pr-411-reviews.json` records that the transport was invoked with an explicit `GET`
method on every call so that no other verb could be constructed.

**The privilege actually held made writing impossible.** `FACT` —
`repository_metadata.permissions = {admin: false, maintain: false, push: false, triage: false,
pull: true}`. `INFERENCE` — the read-only guarantee therefore rests on the observed privilege as well
as on the code path; reasoning: with `pull` as the only granted permission, no write request could
have succeeded even had one been constructed.

**Where these artifacts actually live, stated against the AAP requirement rather than in place of it.**
`FACT` — AAP §0.5.1.1 and §0.10.2 require the planning run's outputs to be written to the session
output directory and never committed to the fork. `FACT` — the delivered placement diverges: this
manifest and its sibling captures are tracked in the repository on the planning branch
`blitzy-5cc7044b-36be-4042-9618-a49ed8c86f36`, thirteen paths in all; section 1 lists the five commit
hashes that introduced them and the commands that establish it. `FACT` — they are **not**
merged into the fork's product branch: `origin/Developer` at
`c79624bd286d5f08f156ccce755bc5d4fffd1909` contains no `PROGRAM-PLAN.md` and no `upstream-harvest/`
path. `FACT` — no production, test, build or configuration file of the fork was modified in order to
produce them, and a copy of all thirteen paths is additionally written outside the working tree, to the
session output directory `/tmp/blitzy/session-output/5cc7044b-36be-4042-9618-a49ed8c86f36/`, each of
the thirteen compared byte-for-byte against its tracked counterpart at `13 of 13 byte-identical`.
`INFERENCE` — that result is quoted as an observation rather than as a standing property, and the check
that produces it is carried in `PROGRAM-PLAN.md` Deliverable A #9 for anyone to re-run; reasoning: the
equality holds only until one of the thirteen is revised without the copy being refreshed, so a claim
that cannot be re-derived is worth less than the command that re-derives it. `UNKNOWN` — whether that
directory outlives this workspace: it is container-local, and nothing observable from inside the
container establishes what is retained outside it. **The authoritative copy is the committed branch
content**, because that is what the platform publishes; the external copy exists so that the capture
survives the branch being read and deleted, and it is not a substitute for a copy kept somewhere that
outlives the container. `FACT` — history is not rewritten to remove them, because AAP §0.5.2 and
§0.10.5 exclude history rewriting outright; the residual placement gap is carried openly here instead
of being attested away. **Recording it is not a waiver of it.** `INFERENCE` — the disclosure changes
the audit position and nothing else; reasoning: the requirement is that these files never enter the
product, and that outcome still depends on the human action stated below rather than on anything this
document says about it.

`FACT` — the same position stated as the commands that check it: `origin/Developer` still resolves to
`c79624bd286d5f08f156ccce755bc5d4fffd1909`, `git diff --name-only c79624bd286d5f08f156ccce755bc5d4fffd1909`
returns only `PROGRAM-PLAN.md` and paths under `upstream-harvest/`, and the same diff with
`--name-status` reports those thirteen paths all at status `A` — so no planning artifact reached the fork's
default branch and no fork production, test, build or configuration file was modified in order to
produce them. **Human action required: do not merge the planning branch into `Developer` or `main`.**
Read `PROGRAM-PLAN.md` out of it and copy `upstream-harvest/` somewhere that outlives this workspace —
the container-local `<session-export>/` copy is not that place — then delete the branch. The full
statement, with the rejected alternatives, the mechanical cause and the run the removal is itemized
to, is in section 1 of this manifest and in `PROGRAM-PLAN.md` Deliverable A #9.

**Secret hygiene, stated as the property that is true and checkable rather than the absolute that is
not.** `FACT` — no credential **value**, prefix or fragment appears in this document, in any filename in
`upstream-harvest/`, or in any field of any artifact, and the credential is referred to here only by
role. `FACT` — request-header text **is** quoted in this document, and an earlier revision of this
paragraph asserted that none was: §3.3.1's credential-transport rule quotes both the prohibited form,
`curl -H 'Authorization: …'`, and the permitted one built with `printf` — the first with the value
elided to `…`, the second with a conversion specifier standing in for it. The false clause is corrected
here rather than quietly dropped, and the same clause is corrected in §10's standards table.

`FACT` — **the claim is a property of every occurrence rather than a count of them**, and the property is
this: wherever the header appears in this document it is either named in prose or quoted with its value
elided or replaced by a conversion specifier, and no occurrence carries a credential. `INFERENCE` — a
total is deliberately not quoted; reasoning: this paragraph and the command below both contain the
header's name, so any figure would be changed by the very text that states it — which is how the
absolute claim came to be false in the first place — and a figure its own document invalidates is worth
less than a property the document cannot falsify by discussing itself. `FACT` — the property is
generated rather than asserted, and the command is written out so it can be re-derived instead of
trusted:

```bash
# Secret-hygiene gate for this manifest. Run from the repository root.
# Asserts a property, not a count: every occurrence of the header is either named in prose
# or quoted with its value elided, and no credential-shaped token appears anywhere in the file.
# Exit status is the verdict: 0 the paragraph above holds, 1 it does not. Reads only.
python3 - <<'PY'
import re, sys
p = 'upstream-harvest/HARVEST-MANIFEST.md'
lines = open(p, encoding='utf-8').read().splitlines()
HEADER = 'Auth' + 'orization'                      # split so this line is not itself an occurrence
hits = [(i + 1, l) for i, l in enumerate(lines) if HEADER in l]
quoted = [(n, l) for n, l in hits if re.search(r"(-H '|header = \")" + HEADER, l)]
unredacted = [(n, l) for n, l in quoted if not ('…' in l or '%s' in l)]
# credential shapes: GitHub token prefixes, a bearer with a literal value, basic-auth userinfo, PEM
SHAPES = [r'gh[pousr]_[A-Za-z0-9]{10,}', r'github_pat_[A-Za-z0-9_]{10,}',
          r'Bearer\s+(?!%s|…)[A-Za-z0-9_\-]{16,}', r'https://[^/\s]+:[A-Za-z0-9_\-]{16,}@',
          r'-----BEGIN [A-Z ]*PRIVATE KEY-----']
leaks = [(i + 1, s) for i, l in enumerate(lines) for s in SHAPES if re.search(s, l)]
for n, l in hits:
    print(f'{p}:{n}  ' + ('QUOTED, value elided' if (n, l) in quoted else 'named in prose only'))
print(f'{len(hits)} occurrence(s); {len(quoted)} quote header text; '
      f'{len(unredacted)} unredacted; {len(leaks)} credential-shaped token(s)')
for n, l in unredacted:
    print(f'UNREDACTED HEADER  {p}:{n}')
for n, s in leaks:
    print(f'CREDENTIAL SHAPE   {p}:{n} matched /{s}/')
ok = not unredacted and not leaks
print('VERIFIED' if ok else 'NOT VERIFIED')
sys.exit(0 if ok else 1)
PY
```

`FACT` — result as run here: every occurrence classified, `0 unredacted`, `0 credential-shaped token(s)`,
`VERIFIED`, exit status `0`. `FACT` — the gate is discriminating: given a copy with a literal
`Bearer` value substituted into the transport rule it reports `UNREDACTED HEADER` and
`CREDENTIAL SHAPE`, prints `NOT VERIFIED` and exits `1`. `INFERENCE` — the narrowed claim is worth more
than the absolute it replaces; reasoning: the security property that matters is that no credential value
exists anywhere, which is checkable and holds, whereas the absolute form was falsified by the same
document that asserted it — and a `FACT` label on a statement its own file contradicts weakens every
other label in the document.

---

## 12. Public-data handling, retention and secondary use

**What this section is, and why it constrains use rather than evidence.** These artifacts retain public
GitHub records about identifiable people — `user.login`, profile and avatar URLs, author association,
created and updated timestamps, and the full text of issue, review and comment bodies — verbatim,
because fidelity to a one-time capture of an archived repository is the whole point of keeping them.
`INFERENCE` — the correct control for that data is therefore a bound on *purpose, access, lifetime and
onward use*, never redaction; reasoning: redacting an audit copy destroys the property that makes it
audit evidence, and every artifact says so in its own `PUBLIC-DATA GOVERNANCE` note. Every rule below
binds this programme's later runs and is carried into `PROGRAM-PLAN.md` so no run has to come back here
to find it.

**1. Purpose limitation.** The data may be used for exactly two purposes: building and auditing the
backlog triage in `PROGRAM-PLAN.md` Deliverable A #5, and establishing the provenance and completeness
of the capture itself. `FACT` — the artifacts already carry a matching `SCOPE` note stating they hold no
classification, no ranking and no adjudication. Any other use requires a decision recorded outside this
document by the human maintainer.

**2. Access and publication boundary.** The artifacts sit in a private planning branch and are read by
the maintainer and by the agent sessions this programme runs. No part of this data may be republished —
not in the fork's product branches, not in release notes, not in documentation, not in the changelog,
and not in any issue text the programme files. `INFERENCE` — where a triage entry needs to cite an
upstream record it cites the **number** and, where fidelity requires it, the raw title, never a
contributor's profile data; reasoning: a number and a title carry the technical content, and identity
adds nothing a triage decision needs. **Author attribution is the single, deliberate exception**, in
one direction only: the pull-request adoption work must preserve each contribution's author
attribution in the commit that re-applies it, because that is a licence obligation
(`PROGRAM-PLAN.md` §0.10.5's "do not touch" set) rather than a secondary use.

**3. Retention trigger and disposal.** Retention is bounded by purpose, not by a period — this
programme records no dates or durations anywhere. The capture is retained while any run still consumes
the triage or the provenance record: `FACT` — that is up to and including the modernization run, which
is where the deletion of these thirteen paths from the fork's working tree is itemized (§1 and
`PROGRAM-PLAN.md` Deliverable A #9). `INFERENCE` — after that deletion the surviving copies are the
branch history, which the same section requires be discarded rather than merged, and whatever copy was
taken off this workspace; reasoning: the `<session-export>/` directory is container-local, so it cannot
be assumed to be one of them. Whatever copy does survive is retained only until the maintainer confirms
the backlog triage no longer needs re-checking, after which it is deleted. **The disposal rule has three
hard preconditions that must hold together, the second already recorded as a blocking open question**:
the equality check in `PROGRAM-PLAN.md` Deliverable A #9 must be re-run in the session performing the
deletion and must report `VERIFIED`, **and** a human must confirm in writing that a copy exists outside
this workspace, **and** the envelope validation gate in §4.4 must be re-run in that session against the
copy being preserved and must exit `0`. `INFERENCE` — no one of the three suffices: a check whose target
dies with the container proves only that a copy existed at that instant, a confirmation resting on a
sentence in a document proves nothing at all, and a copy that is present but internally contradictory is
not evidence of anything; reasoning: the three failure modes are independent, so only the conjunction
closes all of them. `INFERENCE` — the third precondition is bound here, to the deletion, rather than to
continuous integration; reasoning: these thirteen paths are themselves deleted by the modernization run,
so a CI job asserting over them would be a check a later run must remove and would gate the product on
planning artifacts no product code reads — whereas the moment the in-repository copy is destroyed is
exactly the moment the surviving copy's integrity stops being re-derivable. A `MISSING` row, a `DIVERGED`
row, an absent directory or a `NOT VERIFIED` from the §4.4 gate **is** the answer *no verified copy
exists*, and the deletion does not proceed. The capture cannot be repeated against an archived
repository. `INFERENCE` — deleting the last copy of unrepeatable evidence to
satisfy a hygiene rule would be a worse outcome than retaining it; reasoning: the data is public, the
exposure is unchanged by our copy existing, and the evidence is not recoverable.

**4. Deleted or edited upstream content.** `INFERENCE` — a contributor may delete or edit an upstream
comment after this capture, so a record here can outlive its source; reasoning: the capture is a
point-in-time copy and the upstream repository, though archived, still permits account-level deletion.
The rule that follows: this capture is **evidence of what the API returned at its recorded instant**,
never a statement of what a person currently says. Where a specific record's continued retention is
challenged, the record is removed from every copy still under this programme's control — the
`<session-export>/` copy while it exists, and any copy taken off this workspace — and the removal is
noted in the `_capture.omissions` array of a superseding artifact; the in-repository copies cannot be
edited retroactively, because history rewriting is excluded outright, which is one more reason the
branch must be deleted rather than merged. No run may quote a comment body into a user-facing
artifact, so no such quotation can outlive its source.

**5. Prohibited secondary uses, stated as a closed list of things not to do.** No profiling or
inference about any individual, including activity patterns, employer, location or availability. No
enrichment or cross-referencing of contributor identities against any other dataset, registry or
social platform. No outreach of any kind to any contributor, maintainer, company or foundation — which
is already an absolute programme constraint and is repeated here because contact details are precisely
what this data would enable. No training, fine-tuning or embedding of any model on this data. No
aggregate statistic about individuals published anywhere. `FACT` — the harvest itself issued only
`GET` requests and wrote nothing upstream (§11), so nothing in this capture has ever been used to
contact anyone.

**6. What this section does not license.** It does not license editing, redacting, reformatting,
truncating or "cleaning" any harvested payload. `FACT` — the envelope schema constrains the audit
envelope only and leaves the payloads unconstrained precisely so that no rule can motivate editing
third-party data (§4.4). A governance obligation is discharged by controlling use and lifetime; if a
rule and the evidence appear to conflict, the evidence stands and the conflict is recorded.

**7. Enforcement, so this outlives one reading.** `FACT` — every one of the ten capture artifacts
carries a `FACT: PUBLIC-DATA GOVERNANCE` note pointing at this section, and `capture-envelope.schema.json`
at `envelope_schema_version` `1.2.0` **requires** that note to be present, so a validator rather than a
convention keeps the contract attached to the data. `FACT` — the command below **names the artifacts that
lack it and exits non-zero**, so the obligation is a gate rather than a tally:

```bash
# Governance-note enforcement for upstream-harvest/. Run from the repository root.
# Exit status is the verdict: 0 every artifact carries the contract, 1 one or more does not.
python3 - <<'PY'
import json, glob, sys
fs = ['upstream-harvest/issues.json', 'upstream-harvest/pulls.json',
      'upstream-harvest/pr-330-reviews.json', 'upstream-harvest/pr-411-reviews.json'] + \
     sorted(glob.glob('upstream-harvest/issue-comments/*.json'))
missing = [f for f in fs
           if not any(n.startswith('FACT: PUBLIC-DATA GOVERNANCE')
                      for n in json.load(open(f, encoding='utf-8'))['_capture']['notes'])]
print(len(fs) - len(missing), 'of', len(fs), 'artifacts carry the governance note')
for f in missing:
    print(f'NO GOVERNANCE NOTE  {f}')
ok = not missing and len(fs) == 10
print('VERIFIED' if ok else 'NOT VERIFIED')
sys.exit(0 if ok else 1)
PY
```

`FACT` — result: `10 of 10 artifacts carry the governance note`, `VERIFIED`, exit status `0`. `FACT` — run
against a copy with the note removed from one artifact it prints that artifact's path, `NOT VERIFIED`,
and exits `1`.

---

*End of manifest. Sections 1 through 12 complete, covering the **twelve**-file artifact set enumerated
in §1.1. Every count above was read out of the artifacts in `upstream-harvest/` and reconciled against
them, and section 4's endpoint table is generated from them rather than transcribed; the request total
is generated too, from `_capture.calls`, and appears in one form only (§3.2). **Four** values are not
recorded anywhere in the evidence and are labelled `UNKNOWN`: the HTTP status and the record count of
row 31 in section 4, the HTTP client of `upstream-harvest/issue-comments/53.json` in §3.3, and whether
the credential entered a process argument vector for the four artifacts classified `UNKNOWN` at
`_capture.credential_transport_exposure` in §3.3.1. Of the ten data captures, **none failed** and every
harvested payload is **COMPLETE**, while **four** carry `audit_status = "PARTIAL"` on audit metadata
alone — the four recording omissions are listed in full in §8.2 — and all ten validate against
`upstream-harvest/capture-envelope.schema.json` at `envelope_schema_version` `1.2.0` with zero errors
and zero reconciliation failures, under a gate that exits non-zero on either
(§4.4). The public-data handling, retention and secondary-use contract that governs every record kept
here is section 12, and it is enforced per artifact by the schema rather than by convention. The
artifacts' placement, the statement that disclosing it waives nothing, and the one human action that
preserves the position are stated in section 1 and again in section 11.*
