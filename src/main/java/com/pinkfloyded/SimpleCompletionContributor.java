package com.pinkfloyded;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteral;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;


public class SimpleCompletionContributor extends CompletionContributor {

    public SimpleCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement().inside(PlatformPatterns.psiElement(PsiLiteral.class)),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Hello"));
                    }
                }
        );
    }
    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return true;
    }



}
