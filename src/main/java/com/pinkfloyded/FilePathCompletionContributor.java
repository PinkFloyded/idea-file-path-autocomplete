package com.pinkfloyded;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public class FilePathCompletionContributor extends CompletionContributor {

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        VirtualFile parentDirectoryOfCurrentFile = parameters.getOriginalFile()
                .getVirtualFile()
                .getParent();

        String queryString = getQueryString(parameters);
        String parentDirectoryOfCurrentFileStr = parentDirectoryOfCurrentFile == null ? "" :
                parentDirectoryOfCurrentFile.getCanonicalPath();


        FilePathMatcher.aggregateFilePaths(parentDirectoryOfCurrentFileStr, queryString)
                .forEach(path -> result.withPrefixMatcher(queryString)
                        .addElement(LookupElementBuilder.create(path)));
    }

    private static String getQueryString(CompletionParameters parameters) {
        int caretPositionInString = parameters.getOffset() - parameters.getPosition().getTextOffset();
        String queryString = parameters.getPosition()
                                       .getText()
                                       .substring(0, caretPositionInString);

        if (queryString.startsWith("'") || queryString.startsWith("\"")) {
            queryString = queryString.substring(1);
        }

        return queryString;
    }

    private static boolean isAStringLiteral(PsiElement element) {
        String text = element.getText();
        return (text.startsWith("\"") && text.endsWith("\"")) ||
               (text.startsWith("'") && text.endsWith("'")) ||
               getPreviousSiblingText(element).equals("\"") && getNextSiblingText(element).equals("\"") ||
               getPreviousSiblingText(element).equals("\'") && getNextSiblingText(element).equals("\'");
    }

    private static String getPreviousSiblingText(PsiElement element) {
        if (element.getPrevSibling() == null) return "";

        return element.getPrevSibling().getText();
    }

    private static String getNextSiblingText(PsiElement element) {
        if (element.getNextSibling() == null) return "";

        return element.getNextSibling().getText();
    }
}

