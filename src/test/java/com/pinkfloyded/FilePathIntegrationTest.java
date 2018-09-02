package com.pinkfloyded;

import com.google.common.io.Files;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import kotlin.text.Charsets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class FilePathIntegrationTest extends CodeInsightFixtureTestCase {

    private static final File FIXTURE_PATH = new File("src/test/fixture/integration");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.getTempDirFixture().findOrCreateDir("holder");
        myFixture.addFileToProject("holder/abc.txt", "");
        myFixture.addFileToProject("holder/abcd.txt", "");
    }

    public void testCompletesFilePaths() throws IOException {

        configureFromFile("TriggerAutoPopup.java", JavaFileType.INSTANCE, myFixture.getTempDirPath() + "/holder/");

        List<String> expectedPaths = Stream.of(
                new File(myFixture.getTempDirPath(), "holder/abc.txt"),
                new File(myFixture.getTempDirPath(), "holder/abcd.txt")
        ).map(File::getAbsolutePath).collect(Collectors.toList());

        List<String> actualPaths = Stream.of(myFixture.completeBasic())
                .map(LookupElement::getLookupString)
                .collect(Collectors.toList());

        assertOrderedEquals(actualPaths, expectedPaths);
    }

    private void configureFromFile(String localRelativePath,
                                   FileType fileType,
                                   String insertionString) throws IOException {
        File localFile = new File(FIXTURE_PATH, localRelativePath);
        String configString = Files.asCharSource(localFile, Charsets.UTF_8).read()
                .replace("<insert-string-here>", insertionString);
        myFixture.configureByText(fileType, configString);
    }
}
