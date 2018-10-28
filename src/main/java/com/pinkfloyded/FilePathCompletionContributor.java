package com.pinkfloyded;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;


public class FilePathCompletionContributor extends CompletionContributor {


    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (isAStringLiteral(parameters.getPosition())) {
            VirtualFile parentDirectoryOfCurrentFile = parameters.getOriginalFile()
                    .getVirtualFile()
                    .getParent();
            String parentDirectoryOfCurrentFileStr = parentDirectoryOfCurrentFile == null ? "" :
                    parentDirectoryOfCurrentFile.getCanonicalPath();

            int caretPositionInString = parameters.getOffset() - parameters.getPosition().getTextOffset();
            String queryString = parameters.getPosition()
                    .getText()
                    .substring(1, caretPositionInString);

            FilePathMatcher.aggregateFilePaths(parentDirectoryOfCurrentFileStr, queryString)
                    .forEach(path -> result.withPrefixMatcher(queryString)
                            .addElement(LookupElementBuilder.create(path)));
        }
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

