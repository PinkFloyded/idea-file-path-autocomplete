package com.pinkfloyded;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;


public class FilePathContributor extends CompletionContributor {

    private static Pattern HOME_PATTERN = Pattern.compile("^~/");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (isAStringLiteral(parameters.getPosition())) {
            int caretPositionInString = parameters.getOffset() - parameters.getPosition().getTextOffset();

            String rawLiteral = parameters.getPosition()
                    .getText()
                    .substring(1, caretPositionInString);


            String resolvedPath = HOME_PATTERN.matcher(rawLiteral)
                    .replaceAll(System.getProperty("user.home") + "/");

            FilePathMatcher.match(resolvedPath)
                    .filter(path -> !isHiddenFile(path))
                    .map(FilePathContributor::mapToString)
                    .map(pathStr -> rawLiteral.startsWith("~/") ?
                            pathStr.replace(System.getProperty("user.home"), "~") : pathStr)
                    .forEach(path -> result.withPrefixMatcher(rawLiteral)
                            .addElement(LookupElementBuilder.create(path)));
        }
    }

    private static String mapToString(Path path) {
        return path.toString();
    }

    private static boolean isHiddenFile(Path path) {
        if (path.getFileName() == null) return false;

        return path.getFileName().toString().startsWith(".");
    }

    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return typeChar == File.separatorChar;
    }

    private static boolean isAStringLiteral(PsiElement element) {
        String text = element.getText();
        return (text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"));
    }
}

