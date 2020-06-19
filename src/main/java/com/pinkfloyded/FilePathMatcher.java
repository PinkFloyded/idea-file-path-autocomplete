package com.pinkfloyded;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
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
        if (str.endsWith(File.separator) || str.endsWith("/")) {
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
        int queryStringLastSlash = getLastIndexOfSeparator(queryString);

        if (queryFile.isAbsolute()) {
            return match(queryString).map(Path::toString).map(s -> preserveOriginalPrefix(queryString, s, queryStringLastSlash));
        } else if (queryString.startsWith("~")) {
            String canonicalPath = new File(System.getProperty("user.home") + queryString.substring(1)).getPath();
            if (queryString.endsWith(File.separator) || queryString.endsWith("/")) {
                canonicalPath += File.separatorChar;
            }

            return match(canonicalPath)
                    .map(Path::toString)
                    .map(s -> preserveOriginalPrefix(queryString, s, queryStringLastSlash));
        }

        File relativeDirFile = new File(dirContainingFile, queryString);
        try {
            String canonicalPath = relativeDirFile.getCanonicalPath();
            if (queryString.endsWith(File.separator) || queryString.endsWith("/")) {
                canonicalPath += File.separatorChar;
            }
            return match(canonicalPath).map(Path::toString)
                                       .map(s -> preserveOriginalPrefix(queryString, s, queryStringLastSlash));
        } catch (IOException e) {
            return Stream.empty();
        }
    }

    private static int getLastIndexOfSeparator(String path) {
        int separatorIndex = path.lastIndexOf(File.separatorChar);
        int forwardSlashIndex = path.lastIndexOf("/");

        return Math.max(separatorIndex, forwardSlashIndex);
    }

    private static String preserveOriginalPrefix(String originalQueryString, String resolvedPath, int queryStringLastSlash) {
        return originalQueryString.substring(0, queryStringLastSlash + 1) + getBaseName(resolvedPath);
    }

    static String getBaseName(String path) {
        try{
            Path pathObj = Paths.get(path);
            return pathObj.getFileName() == null ? "" : pathObj.getFileName().toString();
        } catch (InvalidPathException e) {
            return "";
        }
    }
}
