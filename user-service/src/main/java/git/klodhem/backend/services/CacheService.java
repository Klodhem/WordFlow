package git.klodhem.backend.services;

public interface CacheService {
    void set(String key, String value);

    String get(String key);
}
