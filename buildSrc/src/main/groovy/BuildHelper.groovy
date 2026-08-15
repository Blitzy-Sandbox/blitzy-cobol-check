// Relocated out of build.gradle so this comparison can be unit-tested.
// Return contract, unchanged: 0 = match, 1 = differ, -1 = error while comparing.
class BuildHelper{
    static int compareFiles(String file1, String file2, boolean trimLines){
        BufferedReader reader1
        BufferedReader reader2
        try{
            reader1 = new BufferedReader(new FileReader(file1))
            reader2 = new BufferedReader(new FileReader(file2))
            int lineCount = 0
            String line1 = ""
            String line2 = ""
            // Both readers are advanced UNCONDITIONALLY, then compared. The previous guard
            // advanced them inside a short-circuiting &&, so when the shorter stream ended
            // the loop exited and control fell through to "return 0" (= match). That is why
            // a zero-byte actual-output.txt was reported identical to the approved baseline.
            while (true){
                line1 = reader1.readLine()
                line2 = reader2.readLine()

                if (line1 == null && line2 == null){
                    // Streams ended together. Refuse to report a match if nothing was ever
                    // compared: this comparator is the only gate on a generated artefact, so
                    // "no lines" must never be reported as "identical".
                    if (lineCount == 0){
                        println("Both ${file1} and ${file2} are empty - nothing was compared")
                        return 1
                    }
                    return 0
                }

                if (line1 == null || line2 == null){
                    // Exactly one stream ended: the files have different line counts.
                    println("Line count mismatch after ${lineCount} matching line(s):")
                    println("   ${file1}: ${line1 == null ? 'end of file' : line1}")
                    println("   ${file2}: ${line2 == null ? 'end of file' : line2}")
                    return 1
                }

                lineCount++
                if (trimLines){
                    line1 = line1.trim()
                    line2 = line2.trim()
                }
                if (!line1.equals(line2)){
                    println("Difference on line ${lineCount}:")
                    println("   ${file1}: ${line1}")
                    println("   ${file2}: ${line2}")
                    return 1
                }
            }
        } catch( Exception e){
            println("Error while comparing: ${e.message}")
            return -1
        } finally{
            // The two closes are nested rather than sequential so that BOTH are always
            // attempted. Written as two statements in a row, a throw from the first close
            // leaves the finally block immediately and the second reader is never closed -
            // a descriptor leaked for the lifetime of a Gradle daemon that outlives the
            // build. The inner finally still lets a failing close propagate, and Groovy
            // 2.5.12 - the version Gradle 6.9.4 embeds - has no try-with-resources.
            try{
                if (reader1 != null) reader1.close()
            } finally{
                if (reader2 != null) reader2.close()
            }
        }
    }
}
