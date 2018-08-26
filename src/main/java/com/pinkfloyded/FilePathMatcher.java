package com.pinkfloyded;

import javax.annotation.Nonnull;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

final class FilePathMatcher {
    @Nonnull
    static Stream<Path> match(@Nonnull String str) {

        List<Path> matches = new ArrayList<>();

        File file = new File(str);
        if (str.endsWith(File.separator)) {
            File[] filesInDir = file.listFiles();
            if (filesInDir != null) {
                for (File f : filesInDir) {
                    matches.add(Paths.get(f.getAbsolutePath()));
                }
            }
        } else {
            if (file.getParent() != null) {
                String basename = getBaseName(str);
                File[] matchesInParent = new File(new File(str).getParent())
                        .listFiles((dir, name) -> name.startsWith(basename) && !name.equals(basename));

                if (matchesInParent != null) {
                    for (File f : matchesInParent) {
                        matches.add(Paths.get(f.getAbsolutePath()));
                    }
                }
            }
        }

        return matches.stream().sorted();
    }

    static String getBaseName(String path) {
        Path pathObj = Paths.get(path);
        return pathObj.getFileName() == null ? "" : pathObj.getFileName().toString();
    }
}
