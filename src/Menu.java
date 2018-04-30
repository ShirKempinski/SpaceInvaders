/**
 * Menu.
 * @param <T> type.
 */
public interface Menu<T> extends Animation {

    /**
     * addSelection.
     * @param key - key
     * @param message - message
     * @param returnVal - the return value
     */
    void addSelection(String key, String message, T returnVal);

    /**
     * return status.
     * @return the user's selection.
     */
    T getStatus();
}