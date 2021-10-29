package pl.inder00.opensource.sectors.i18n;

import java.util.concurrent.ConcurrentHashMap;

public class I18nFactory implements I18n {

    /**
     * Data
     */
    private String defaultLanguage;
    private ConcurrentHashMap<String, String> aliases;
    private ConcurrentHashMap<String, String> messages;

    /**
     * Implementation
     */
    public I18nFactory(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        this.aliases = new ConcurrentHashMap<>();
        this.messages = new ConcurrentHashMap<>();
    }

    @Override
    public String getLocalizedMessage(String locale, String code) {
        return this.messages.getOrDefault(this.aliases.getOrDefault(locale.toLowerCase(), locale.toLowerCase()) + "." + code, this.messages.get(this.defaultLanguage + "." + code));
    }

    @Override
    public void addLocalizedMessage(String code, String message) {
        this.messages.put(code, message);
    }

    @Override
    public void addLocaleAlias(String localeSource, String localeTarget) {
        this.aliases.put(localeTarget, localeSource);
    }
}
