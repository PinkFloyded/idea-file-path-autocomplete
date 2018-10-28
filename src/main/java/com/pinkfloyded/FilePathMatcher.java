package com.pinkfloyded;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
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


    static Stream<String> aggregateFilePaths(String dirContainingFile, String queryString) {
        File queryFile = new File(queryString);

        if (queryFile.isAbsolute()) {
            return match(queryString).map(Path::toString);
        }

        File relativeDirFile = new File(dirContainingFile, queryString);
        int queryStringLastSlash = queryString.lastIndexOf(File.separatorChar);

        try {
            String canonicalPath = relativeDirFile.getCanonicalPath();
            if (queryString.endsWith(File.separator)) {
                canonicalPath += File.separatorChar;
            }
            return match(canonicalPath)
                    .map(Path::toString)
                    .map(s -> queryString.substring(0, queryStringLastSlash + 1) + getBaseName(s));
        } catch (IOException e) {
            return Stream.empty();
        }
    }


    static String getBaseName(String path) {
        Path pathObj = Paths.get(path);
        return pathObj.getFileName() == null ? "" : pathObj.getFileName().toString();
    }
}
