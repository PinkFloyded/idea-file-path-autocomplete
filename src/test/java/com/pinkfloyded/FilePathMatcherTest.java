package com.pinkfloyded;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.pinkfloyded.FilePathMatcher.match;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class FilePathMatcherTest {

    private static final File FIXTURE_PATH = new File("src/test/testfixture/");

    @Test
    public void emptyListForNonMatchingPath() {
        assertThat(match("faskdfa;lksjfl;akjfk;ajsafj;aljad;").size(), is(0));
    }

    @Test
    public void matchesForValidDirectory() {
        String[] expectedPaths = new String[]{
                new File(FIXTURE_PATH, "container/abc.txt").getAbsolutePath(),
                new File(FIXTURE_PATH, "container/jeep.txt").getAbsolutePath()
        };
        assertThat(match(new File(FIXTURE_PATH, "container").getAbsolutePath()),
                is(Arrays.asList(expectedPaths)));
    }
}