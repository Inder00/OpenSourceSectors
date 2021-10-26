package pl.inder00.opensource.sectors.i18n;

public interface I18n {

    /**
     * Returning a localized message based on locale code
     * @param locale Locale code
     * @param code Message code
     */
    String getLocalizedMessage(String locale, String code);

    /**
     * Add a localized text
     * @param code Locale and message code
     * @param message Message
     */
    void addLocalizedMessage(String code, String message);

    /**
     * Add locale alias
     * @param localeSource
     * @param localeTarget
     */
    void addLocaleAlias( String localeSource, String localeTarget );

}
