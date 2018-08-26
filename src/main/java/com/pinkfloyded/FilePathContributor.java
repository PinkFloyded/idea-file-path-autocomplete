package com.pinkfloyded;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;


public class FilePathContributor extends CompletionContributor {

    public FilePathContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().inside(PlatformPatterns.psiElement(PsiLiteral.class)),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

                        String rawLiteral = parameters.getPosition()
                                .getText()
                                .replaceAll("^\"(.*)\"$", "$1")
                                .replace(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED, "");

                        String resolvedPath = rawLiteral.replaceAll("^~/",
                                System.getProperty("user.home") + "/");

                        FilePathMatcher.match(resolvedPath)
                                .filter(path -> !isHiddenFile(path))
                                .map(Path::toString)
                                .map(pathStr -> rawLiteral.startsWith("~/") ?
                                        pathStr.replace(System.getProperty("user.home"), "~") : pathStr)
                                .forEach(path -> resultSet.withPrefixMatcher(rawLiteral)
                                        .addElement(LookupElementBuilder.create(path)));
                    }
                }
        );
    }

    private static boolean isHiddenFile(Path path) {
        if (path.getFileName() == null) return false;

        return path.getFileName().toString().startsWith(".");
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return typeChar == File.separatorChar;
    }


}

