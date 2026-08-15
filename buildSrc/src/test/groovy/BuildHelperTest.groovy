import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

// Test methods must remain void: Groovy def methods are non-void and Jupiter will not discover them.
// Return contract under test: 0 = match, 1 = differ, -1 = error while comparing.
class BuildHelperTest {

    // Fixture contract: an empty list yields a zero-byte file; a non-empty list is joined with no
    // trailing terminator, matching expected-output.txt, which ends without one; the returned path
    // is absolute, so no test depends on the process working directory.
    private static String tempFileWith(List<String> lines) {
        File f = File.createTempFile("buildhelper-", ".txt")
        f.deleteOnExit()
        f.text = lines.isEmpty() ? "" : lines.join(System.lineSeparator())
        return f.absolutePath
    }

    // Matching prefixes isolate EOF-length handling from content comparison.

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

    // Both empty is not a length mismatch, but it must still fail to prevent a zero-comparison match.

    @Test
    void it_fails_when_both_files_are_empty_because_nothing_was_compared() {
        String expected = tempFileWith([])
        String actual = tempFileWith([])

        int result = BuildHelper.compareFiles(expected, actual, true)

        assertTrue(result != 0, "no vacuous match is possible when zero lines were compared")
    }

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
        // A child beneath a regular file cannot exist while the parent remains a file, avoiding
        // collision with independently generated sibling temp names.
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

    // Production passes trimLines=true for indented COBOL output; pin both flag states.

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
