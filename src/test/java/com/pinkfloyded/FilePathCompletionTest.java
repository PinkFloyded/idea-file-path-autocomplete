package com.pinkfloyded;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

import java.io.File;

public class FilePathCompletionTest extends LightPlatformCodeInsightFixtureTestCase {

    private static final File TEST_DATA_PATH = new File("src/test/testdata/");

    @Override
    protected String getTestDataPath() {
        return TEST_DATA_PATH.getAbsolutePath();
    }

    public void testForwardSlashTriggersCompletion() {
        myFixture.copyDirectoryToProject("container", ".");
//        myFixture.type("/");
//        myFixture.complete(CompletionType.BASIC, 1);
//        List<String> completions = myFixture.getLookupElementStrings();

        String[] expectedPaths = {
                new File("container/abc.txt").getAbsolutePath(),
                new File("container/abcd.txt").getAbsolutePath(),
                new File("container/jeep.txt").getAbsolutePath()
        };

        myFixture.testCompletionVariants("FileTest.java", expectedPaths);
//        myFixture.configureByFile("FileTest.java");
//        assertNotNull(completions);
//        assertOrderedEquals(completions, expectedPaths);
    }
}
