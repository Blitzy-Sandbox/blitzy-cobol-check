import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Executable contract for BuildHelper.compareFiles(String, String, boolean).
 *
 * <p>This class exists because that comparator is the ONLY gate on the output the COBOL
 * approval harness generates. Its previous loop guard advanced both readers inside a
 * short-circuiting "and" operator, so when the shorter stream ended the loop simply exited and
 * control fell through to "return 0" - a match. A zero-byte actual-output.txt was therefore
 * declared identical to the 234-line approved baseline and the build reported PASS having
 * compiled no COBOL at all. The gate was mechanically incapable of failing.
 *
 * <p>The two guarantees the repair depends on are pinned here - a length mismatch fails, and an
 * empty stream fails - together with the pre-existing behaviour that must NOT regress:
 * equal-and-matching, equal-but-differing, an unreadable file, and trimming in both directions.
 *
 * <p><b>Return contract under test:</b> 0 = match, 1 = differ, -1 = error while comparing. The
 * production call site tests "output != 0", so both 1 and -1 are FAIL and only 0 is PASS. That
 * is why the unreadable-file case pins the exact -1 while every other failing case asserts only
 * that the verdict is non-zero: -1 is a contract worth pinning precisely, but the caller already
 * treats it as failure.
 *
 * <p>Every claim about the comparator is asserted on its return value alone. The comparator
 * reports its findings with println only, and capturing stdout is deliberately out of scope - the
 * diagnostics appear in the test log for a human reader without becoming part of the asserted
 * contract. The one assertion that is not a return value guards a fixture precondition rather
 * than the comparator: it proves the unreadable path really is absent before it is compared.
 *
 * <p>The class sits in the default package to match BuildHelper, which stays in the default
 * package so that the call site in build.gradle needs no import.
 *
 * <p>Every test method is declared void rather than def on purpose. JUnit Jupiter's
 * testable-method predicate requires a void return type, and a Groovy def method returns the
 * value of its last expression - such a method is silently NOT discovered, which would let a
 * green build hide missing coverage and reproduce the very vacuous-pass class this test exists
 * to eliminate. The run is verified by asserting the discovered test count, not just the colour.
 */
class BuildHelperTest {

    /**
     * Writes the supplied lines to a throwaway file and returns its absolute path.
     *
     * <p>An empty list yields a genuine zero-byte file, which is the literal production defect.
     * A non-empty list is joined with the platform separator and no terminator is appended, so
     * the fixture has no trailing newline - deliberately mirroring expected-output.txt, which
     * ends without one. BufferedReader.readLine() returns that unterminated final line
     * normally, so this is a faithful fixture rather than a source of spurious mismatches.
     *
     * <p>Explicit temp files are used in preference to JUnit's TempDir extension: TempDir was
     * still marked experimental in the 5.7.0 line and imposes a non-private-field requirement
     * that Groovy's property semantics complicate. This fixture is version-agnostic across the
     * whole 5.x span the build may resolve.
     *
     * <p>Absolute paths are returned so no test depends on the process working directory, and
     * deleteOnExit keeps the run hermetic without inter-test cleanup coupling.
     */
    private static String tempFileWith(List<String> lines) {
        File f = File.createTempFile("buildhelper-", ".txt")
        f.deleteOnExit()
        f.text = lines.isEmpty() ? "" : lines.join(System.lineSeparator())
        return f.absolutePath
    }

    // -------------------------------------------------------------------------------------
    // Mandated guarantee (a): unequal stream lengths must produce a failing verdict - in BOTH
    // directions, because an asymmetry between them would itself be a new defect. Each fixture
    // shares a matching prefix, so the failing verdict can only come from the length
    // difference and not from a content difference.
    // -------------------------------------------------------------------------------------

    @Test
    void it_fails_when_the_actual_file_has_extra_trailing_lines() {
        String expected = tempFileWith(["alpha", "beta"])
        String actual = tempFileWith(["alpha", "beta", "gamma"])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "extra trailing lines in the actual file must not report a match")
    }

    @Test
    void it_fails_when_the_actual_file_is_truncated() {
        String expected = tempFileWith(["alpha", "beta", "gamma"])
        String actual = tempFileWith(["alpha", "beta"])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "a truncated actual file must not report a match")
    }

    // -------------------------------------------------------------------------------------
    // Mandated guarantee (b): an empty stream must produce a failing verdict - on either side.
    // The first case is the literal production defect: a zero-byte actual-output.txt compared
    // against a populated expected-output.txt used to return 0, meaning MATCH.
    // -------------------------------------------------------------------------------------

    @Test
    void it_fails_when_the_actual_file_is_empty() {
        String expected = tempFileWith(["TESTSUITE:", "PASS:", "====="])
        String actual = tempFileWith([])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "an empty actual file must never match the approved baseline")
    }

    @Test
    void it_fails_when_the_expected_file_is_empty() {
        String expected = tempFileWith([])
        String actual = tempFileWith(["TESTSUITE:", "PASS:", "====="])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "an empty expected file must never match a populated capture")
    }

    // -------------------------------------------------------------------------------------
    // Two empty streams are not a length mismatch, so this pins the additional declared
    // behaviour change: a comparator that reports "match" having compared nothing is the exact
    // defect class under repair, and it is the sole gate on a generated artefact.
    // -------------------------------------------------------------------------------------

    @Test
    void it_fails_when_both_files_are_empty_because_nothing_was_compared() {
        String expected = tempFileWith([])
        String actual = tempFileWith([])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "no vacuous match is possible when zero lines were compared")
    }

    // -------------------------------------------------------------------------------------
    // Pre-existing behaviour that must NOT regress. These cases guard the working path against
    // the repair itself: the repaired loop must still recognise a genuine match, still report a
    // content difference at equal length, and still surrender -1 when a file cannot be read.
    // -------------------------------------------------------------------------------------

    @Test
    void it_reports_a_match_for_identical_files_of_equal_length() {
        List<String> content = ["TESTSUITE:", "     PASS:", "**** FAIL:", "====="]
        String expected = tempFileWith(content)
        String actual = tempFileWith(content)

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertEquals(0, result)
    }

    @Test
    void it_fails_when_content_differs_at_equal_length() {
        String expected = tempFileWith(["TESTSUITE:", "PASS:", "====="])
        String actual = tempFileWith(["TESTSUITE:", "**** FAIL:", "====="])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "a content difference at equal length must still be reported")
    }

    @Test
    void it_returns_minus_one_when_a_file_cannot_be_read() {
        String present = tempFileWith(["alpha"])
        // The unreadable path is derived UNDERNEATH a file that was just created as a regular
        // file, so its absence is structural rather than probabilistic: a regular file cannot
        // hold children, so this path cannot exist and cannot be created even by a concurrent
        // process - the operating system answers ENOTDIR, which surfaces as the
        // FileNotFoundException the comparator's catch arm converts into -1. Suffixing a temp
        // path to form a sibling name would NOT be safe: createTempFile reserves only the path
        // it returns, never a name derived from it, so a stale or concurrently created file at
        // that sibling would silently turn this -1 into 0 or 1 and the failure would say
        // nothing about the contract.
        String unreadable = new File(present, "does-not-exist").absolutePath
        assertTrue(!new File(unreadable).exists(),
                "the fixture path must be absent, or -1 would not mean what this test claims")

        // Both argument orders are pinned, because they are different paths through the
        // comparator's cleanup. Unreadable SECOND: the first reader opens, the second open
        // throws, and the finally arm closes one reader while skipping the one never assigned.
        int unreadableSecond = BuildHelper.compareFiles(present, unreadable, true)

        assertEquals(-1, unreadableSecond, "an unreadable second file must surrender exactly -1")

        // Unreadable FIRST: the first open throws, so neither reader is ever assigned and the
        // finally arm skips both closes. This is the production-relevant direction - it is what
        // happens when expected-output.txt itself is absent - and it must fail identically,
        // because the caller treats -1 as failure exactly as it treats 1.
        int unreadableFirst = BuildHelper.compareFiles(unreadable, present, true)

        assertEquals(-1, unreadableFirst, "an unreadable first file must surrender exactly -1")
    }

    // -------------------------------------------------------------------------------------
    // Trimming is load-bearing rather than decorative: the harness passes trimLines = true
    // because the COBOL DISPLAY output is indented, so both settings of the flag are pinned.
    // -------------------------------------------------------------------------------------

    @Test
    void it_ignores_surrounding_whitespace_when_trimming_is_enabled() {
        String expected = tempFileWith([" x "])
        String actual = tempFileWith(["x"])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertEquals(0, result)
    }

    @Test
    void it_honours_surrounding_whitespace_when_trimming_is_disabled() {
        String expected = tempFileWith([" x "])
        String actual = tempFileWith(["x"])

        int result = BuildHelper.compareFiles(expected, actual, false)

        assertTrue(result != 0, "with trimming disabled the whitespace difference must be reported")
    }
}
