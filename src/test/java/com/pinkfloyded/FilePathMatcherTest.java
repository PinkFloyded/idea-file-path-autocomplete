package com.pinkfloyded;

import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.pinkfloyded.FilePathMatcher.getBaseName;
import static com.pinkfloyded.FilePathMatcher.match;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class FilePathMatcherTest {

    private static final File FIXTURE_PATH = new File("src/test/testfixture/");

    @Test
    public void emptyListForNonMatchingPath() {
        assertThat(match("faskdfa;lksjfl;akjfk;ajsafj;aljad;").count(), is(0L));
    }

    @Test
    public void matchesForValidDirectory() {
        Path[] expectedPaths = {
                Paths.get(new File(FIXTURE_PATH, "container/abc.txt").getAbsolutePath()),
                Paths.get(new File(FIXTURE_PATH, "container/abcd.txt").getAbsolutePath()),
                Paths.get(new File(FIXTURE_PATH, "container/jeep.txt").getAbsolutePath())
        };

        assertThat(match(new File(FIXTURE_PATH, "container").getAbsolutePath()).toArray(), is(expectedPaths));
    }


    @Test
    public void matchesPartiallyCompletedFiles() {
        Path[] expectedPaths = new Path[]{
                Paths.get(new File(FIXTURE_PATH, "container/abc.txt").getAbsolutePath()),
                Paths.get(new File(FIXTURE_PATH, "container/abcd.txt").getAbsolutePath())
        };
        assertThat(match(new File(FIXTURE_PATH, "container/ab").getAbsolutePath()).toArray(), is(expectedPaths));
    }

    @Test
    public void getsFileBaseName() {
        assertThat(getBaseName("/var/data/test/testing.txt"), is("testing.txt"));
    }
    @Test
    public void getsDirBaseName() {
        assertThat(getBaseName("/var/data/test/testing"), is("testing"));
    }
}