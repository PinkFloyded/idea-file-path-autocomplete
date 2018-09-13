package com.pinkfloyded;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;


public class FilePathCompletionContributor extends CompletionContributor {

    private static Pattern HOME_PATTERN = Pattern.compile("^~/");
    private static Pattern CURRENT_DIR_PATTERN = Pattern.compile("^[.]/");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (isAStringLiteral(parameters.getPosition())) {
            int caretPositionInString = parameters.getOffset() - parameters.getPosition().getTextOffset();

            String rawLiteral = parameters.getPosition()
                    .getText()
                    .substring(1, caretPositionInString);


            String resolvedPath = HOME_PATTERN.matcher(rawLiteral)
                    .replaceAll(System.getProperty("user.home") + "/");


            PsiDirectory parentDir = parameters.getOriginalFile().getParent();
            final String currentDir = parentDir == null ? "" : parentDir.getVirtualFile().getPath();
            resolvedPath = CURRENT_DIR_PATTERN.matcher(resolvedPath).replaceAll(currentDir + "/");

            FilePathMatcher.match(resolvedPath)
                    .filter(path -> !isHiddenFile(path))
                    .map(FilePathCompletionContributor::mapToString)
                    .map(pathStr -> mapToRawLiteral(pathStr, rawLiteral, currentDir))
                    .forEach(path -> result.withPrefixMatcher(rawLiteral)
                            .addElement(LookupElementBuilder.create(path)));
        }
    }

    private static String mapToString(Path path) {
        return path.toString();
    }

    private static String mapToRawLiteral(String fullPath, String rawLiteral, String currentDir) {
        String path = rawLiteral.startsWith("~/") ?
                fullPath.replace(System.getProperty("user.home"), "~") : fullPath;

        return rawLiteral.startsWith("./") ?
                path.replace(currentDir, ".") : path;
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

