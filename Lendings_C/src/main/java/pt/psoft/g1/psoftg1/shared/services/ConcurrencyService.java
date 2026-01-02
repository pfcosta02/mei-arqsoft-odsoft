
package pt.psoft.g1.psoftg1.shared.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConcurrencyService {
    public static final String IF_MATCH = "If-Match";

    /**
     * Extrai versão numérica do If-Match.
     * Aceita: W/"123", "123", 123, *, hash (md5/hex)
     * Retorna: número OU null (para wildcard/hash).
     */
    public Long tryGetNumericVersionFromIfMatch(final String ifMatchHeader) {
        if (ifMatchHeader == null || ifMatchHeader.isBlank() || "null".equalsIgnoreCase(ifMatchHeader)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        String v = ifMatchHeader.trim();
        if (v.startsWith("W/")) v = v.substring(2).trim();
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
            v = v.substring(1, v.length() - 1);
        }
        if ("*".equals(v)) return null;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException ex) {
            return null; // hash não numérico → fallback
        }
    }
}
