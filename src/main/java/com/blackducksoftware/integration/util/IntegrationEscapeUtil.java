package com.blackducksoftware.integration.util;

import java.util.ArrayList;
import java.util.List;

public class IntegrationEscapeUtil {
    /**
     * Do a poor man's URI escaping. We aren't terribly interested in precision here, or in introducing a library that
     * would do it better.
     */
    public List<String> escapePiecesForUri(final List<String> pieces) {
        final List<String> escapedPieces = new ArrayList<>(pieces.size());
        for (final String piece : pieces) {
            final String escaped = escapeForUri(piece);
            escapedPieces.add(escaped);
        }

        return escapedPieces;
    }

    public String escapeForUri(final String s) {
        if (s == null) {
            return null;
        }

        final String escaped = s.replaceAll("[^A-Za-z0-9]", "_");
        return escaped;
    }

}
