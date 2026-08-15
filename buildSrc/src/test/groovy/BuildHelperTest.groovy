import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Executable contract for {@link BuildHelper#compareFiles(String, String, boolean)}.
 *
 * This class exists because the comparator is the ONLY gate on the COBOL approval harness's
 * generated output. Its previous loop guard advanced both readers inside a short-circuiting
 * &&, so a zero-byte actual-output.txt compared "equal" to the 234-line approved baseline and
 * the build reported PASS having compiled no COBOL at all. The two guarantees that repair
 * depends on - a length mismatch fails, and an empty stream fails - are pinned below, along
 * with the pre-existing behaviour that must NOT regress (equal-and-matching, equal-but-
 * differing, missing file, and trimming in both directions).
 *
 * The class is in the default package to match BuildHelper, which stays default-package so
 * that build.gradle's call site needs no import.
 */
class BuildHelperTest {

    /**
     * Writes the supplied lines to a throwaway file and returns its absolute path.
     *
     * Explicit temp files are used rather than JUnit's @TempDir: @TempDir was still marked
     * experimental in the 5.7.0 line and imposes a non-private-field requirement that Groovy's
     * property semantics complicate, so this fixture is version-agnostic across the 5.x span.
     * Lines are joined with the platform separator and no terminator is appended, mirroring
     * expected-output.txt, which ends WITHOUT a trailing newline - BufferedReader.readLine()
     * returns that unterminated final line normally, so no spurious mismatch arises.
     */
    private static String tempFileWith(List<String> lines) {
        File f = File.createTempFile("buildhelper-", ".txt")
        f.deleteOnExit()
        f.text = lines.isEmpty() ? "" : lines.join(System.lineSeparator())
        return f.absolutePath
    }

    // ---------------------------------------------------------------------------------------
    // Mandated guarantee (a): unequal stream lengths must fail - in BOTH directions, because
    // asymmetry between them would itself be a defect.
    // ---------------------------------------------------------------------------------------

    @Test
    void actualLongerThanExpectedIsReportedAsDifferent() {
        String expected = tempFileWith(["alpha", "beta"])
        String actual = tempFileWith(["alpha", "beta", "gamma"])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "extra trailing lines in the second file must not report a match")
    }

    @Test
    void expectedLongerThanActualIsReportedAsDifferent() {
        String expected = tempFileWith(["alpha", "beta", "gamma"])
        String actual = tempFileWith(["alpha", "beta"])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "a truncated second file must not report a match")
    }

    // ---------------------------------------------------------------------------------------
    // Mandated guarantee (b): an empty stream must fail - on either side. The first case is
    // the literal production defect: a zero-byte actual-output.txt against a populated
    // expected-output.txt used to return 0 (MATCH).
    // ---------------------------------------------------------------------------------------

    @Test
    void emptyActualAgainstPopulatedExpectedIsReportedAsDifferent() {
        String expected = tempFileWith(["TESTSUITE:", "PASS:", "====="])
        String actual = tempFileWith([])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "an empty actual file must never match the approved baseline")
    }

    @Test
    void emptyExpectedAgainstPopulatedActualIsReportedAsDifferent() {
        String expected = tempFileWith([])
        String actual = tempFileWith(["TESTSUITE:", "PASS:", "====="])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "an empty expected file must never match a populated capture")
    }

    // ---------------------------------------------------------------------------------------
    // Both files empty: not a length mismatch, so this is the additional declared behaviour
    // change (ii-b). A comparator that reports "match" having compared nothing is the exact
    // defect class under repair.
    // ---------------------------------------------------------------------------------------

    @Test
    void twoEmptyFilesAreReportedAsDifferentBecauseNothingWasCompared() {
        String expected = tempFileWith([])
        String actual = tempFileWith([])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "no vacuous match is possible when zero lines were compared")
    }

    // ---------------------------------------------------------------------------------------
    // Pre-existing behaviour that must NOT regress.
    // ---------------------------------------------------------------------------------------

    @Test
    void equalLengthIdenticalContentReturnsZero() {
        String expected = tempFileWith(["TESTSUITE:", "PASS:", "====="])
        String actual = tempFileWith(["TESTSUITE:", "PASS:", "====="])

        assertEquals(0, BuildHelper.compareFiles(expected, actual, true))
    }

    @Test
    void equalLengthDifferingContentIsReportedAsDifferent() {
        String expected = tempFileWith(["TESTSUITE:", "PASS:", "====="])
        String actual = tempFileWith(["TESTSUITE:", "**** FAIL:", "====="])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "a content difference at equal length must still be reported")
    }

    @Test
    void missingFileReturnsMinusOne() {
        String expected = tempFileWith(["alpha"])
        String actual = new File(System.getProperty("java.io.tmpdir"),
                "buildhelper-absent-${System.nanoTime()}.txt").absolutePath

        // -1 is the catch arm's contract; the caller's "output != 0" test already treats it
        // as failure, so this pins the error path without changing its meaning.
        assertEquals(-1, BuildHelper.compareFiles(expected, actual, true))
    }

    // ---------------------------------------------------------------------------------------
    // Trimming: the harness passes trimLines = true because the COBOL DISPLAY output is
    // indented, so both directions of the flag are pinned.
    // ---------------------------------------------------------------------------------------

    @Test
    void trimmingEnabledIgnoresSurroundingWhitespace() {
        String expected = tempFileWith([" x "])
        String actual = tempFileWith(["x"])

        assertEquals(0, BuildHelper.compareFiles(expected, actual, true))
    }

    @Test
    void trimmingDisabledHonoursSurroundingWhitespace() {
        String expected = tempFileWith([" x "])
        String actual = tempFileWith(["x"])

        int result = BuildHelper.compareFiles(expected, actual, false)

        assertTrue(result != 0, "with trimLines = false the whitespace difference must be reported")
    }
}
