package com.pinkfloyded;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class FilePathMatcher {
    @Nonnull
    static List<String> match(String str) {

        List<String> matches = new ArrayList<>();

        File[] filesInDir = new File(str).listFiles();
        if (filesInDir != null) {
            for (File file : filesInDir) {
                matches.add(file.getAbsolutePath());
            }
        }

        return matches;
    }
}
