package tbs.framework.sql.enums;

/**
 * The enum Query contrast enum.
 *
 * @author abstergo
 */
public enum QueryContrastEnum {
    
    /**
     * Equal query contrast enum.
     */
    EQUAL, NOT_EQUAL,
    /**
     * Greater query contrast enum.
     */
    GREATER,
    /**
     * Less query contrast enum.
     */
    LESS,
    /**
     * Greater equal query contrast enum.
     */
    GREATER_EQUAL,
    /**
     * Less equal query contrast enum.
     */
    LESS_EQUAL,
    /**
     * Is null query contrast enum.
     */
    IS_NULL,
    /**
     * Is not null query contrast enum.
     */
    IS_NOT_NULL,
    /**
     * Rlike query contrast enum.
     */
    RLIKE,
    /**
     * Llike query contrast enum.
     */
    LLIKE,
    /**
     * Not like query contrast enum.
     */
    NOT_LIKE,
    /**
     * In query contrast enum.
     */
    IN,
    /**
     * Not in query contrast enum.
     */
    NOT_IN
}
