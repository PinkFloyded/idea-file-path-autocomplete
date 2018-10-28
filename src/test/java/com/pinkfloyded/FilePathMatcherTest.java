package com.pinkfloyded;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.pinkfloyded.FilePathMatcher.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;


public class FilePathMatcherTest {

    private static final String FIXTURE_PATH = new File("src/test/fixture/unit").getAbsolutePath();
    private static final Logger logger = Logger.getInstance(FilePathMatcherTest.class);

    @Test
    public void emptyListForNonMatchingPath() {
        assertThat(match("faskdfa;lksjfl;akjfk;ajsafj;aljad;").count(), is(0L));
    }

    @Test
    public void matchesForValidDirectory() {
        Path[] expectedPaths = {
                Paths.get(FIXTURE_PATH + "/container/abc.txt"),
                Paths.get(FIXTURE_PATH + "/container/abcd.txt"),
                Paths.get(FIXTURE_PATH + "/container/dir1"),
                Paths.get(FIXTURE_PATH + "/container/jeep.txt"),
        };

        assertThat(match(FIXTURE_PATH + "/container" + File.separatorChar).toArray(), is(expectedPaths));
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
    public void aggregateFilePathsAggregatesFilesContainingDirectory() {
        String[] expected = {
                "xyz.txt"
        };
        String[] actual = aggregateFilePaths(Paths.get(FIXTURE_PATH + "/container/dir1").toString(),
                "xy").toArray(String[]::new);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void aggregateFilePathsRelativePreviousDirectory() {
        String[] expected = {
                "../abc.txt",
                "../abcd.txt",
                "../dir1",
                "../jeep.txt"
        };
        String[] actual = aggregateFilePaths(Paths.get(FIXTURE_PATH + "/container/dir1").toString(),
                "../").toArray(String[]::new);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void aggregateFilePathsRelativeComplexPath() {
        String[] expected = {
                "../../cont/../container/abc.txt",
                "../../cont/../container/abcd.txt",
        };
        String[] actual = aggregateFilePaths(Paths.get(FIXTURE_PATH + "/container/dir1").toString(),
                "../../cont/../container/ab").toArray(String[]::new);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void aggregateFilePathsHomeDir() {
        String dirName = UUID.randomUUID().toString();
        Path dirInHomeDir = Paths.get(System.getProperty("user.home"), dirName);


        try {
            Files.createDirectory(dirInHomeDir);
            Path a = Paths.get(dirInHomeDir.toString(), "a.txt");
            Path b = Paths.get(dirInHomeDir.toString(), "b.txt");
            Path c = Paths.get(dirInHomeDir.toString(), "c.txt");

            Files.createFile(a);
            Files.createFile(b);
            Files.createFile(c);

            String[] expected = {
                    "~/" + dirName + "/a.txt",
                    "~/" + dirName + "/b.txt",
                    "~/" + dirName + "/c.txt",
            };
            String[] actual = aggregateFilePaths("",
                    "~/" + dirName + "/").toArray(String[]::new);

            assertArrayEquals(expected, actual);
        } catch (SecurityException | IOException e) {
            logger.warn("Error while setting up fixture for home directory test", e);
        } finally {
            if (Files.exists(dirInHomeDir)) {
                try {
                    FileUtils.deleteDirectory(dirInHomeDir.toFile());
                } catch (IOException e) {
                    logger.warn("Error deleting home fixture: " + dirInHomeDir, e);
                }
            }
        }

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