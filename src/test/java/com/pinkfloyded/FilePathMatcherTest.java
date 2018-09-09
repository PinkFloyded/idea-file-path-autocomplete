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

    private static final String FIXTURE_PATH = new File("src/test/fixture/unit").getAbsolutePath();

    @Test
    public void emptyListForNonMatchingPath() {
        assertThat(match("faskdfa;lksjfl;akjfk;ajsafj;aljad;").count(), is(0L));
    }

    @Test
    public void matchesForValidDirectory() {
        Path[] expectedPaths = {
                Paths.get(FIXTURE_PATH + "/container/abc.txt"),
                Paths.get(FIXTURE_PATH + "/container/abcd.txt"),
                Paths.get(FIXTURE_PATH + "/container/jeep.txt"),
        };

        assertThat(match(FIXTURE_PATH + "/container/").toArray(), is(expectedPaths));
    }


    @Test
    public void matchesPartiallyCompletedFiles() {
        Path[] expectedPaths = new Path[]{
                Paths.get(FIXTURE_PATH + "/container/abc.txt"),
                Paths.get(FIXTURE_PATH + "/container/abcd.txt")
        };

        assertThat(match(FIXTURE_PATH + "/container/ab").toArray(), is(expectedPaths));
    }

    @Test
    public void matchesPartiallyCompletedDirectories() {
        Path[] expectedPaths = new Path[]{
                Paths.get(FIXTURE_PATH + "/cont"),
                Paths.get(FIXTURE_PATH + "/container")
        };

        assertThat(match(FIXTURE_PATH + "/con").toArray(), is(expectedPaths));
    }

    @Test
    public void doesntMatchDirContentWithoutTrailingSlash() {
        Path[] expectedPaths = {
                Paths.get(FIXTURE_PATH + "/container"),
        };


        assertThat(match(FIXTURE_PATH + "/cont").toArray(), is(expectedPaths));
    }

    @Test
    public void getsFileBaseName() {
        assertThat(getBaseName("/var/data/test/testing.txt"), is("testing.txt"));
    }

    @Test
    public void getsDirBaseName() {
        assertThat(getBaseName("/var/data/test/testing"), is("testing"));
    }

    @Test
    public void emptyStringForNoBaseName() {
        assertThat(getBaseName("/"), is(""));
    }

}