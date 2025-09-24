package com.deloitte.elrr.services.exception;

import java.util.Date;
import java.util.List;

/**
 * @author mnelakurti
 *
 */
public class ELRRErrorDetails {
    /**
     *
     */
    private Date timestamp;
    /**
     *
     */
    private String message;
    /**
     *
     */
    private String path;
    /**
     *
     */
    private List<String> details;
    /**
     *
     * @param argsTimestamp
     * @param argsMessage
     * @param argsPath
     * @param argsDetails
     */
    public ELRRErrorDetails(final Date argsTimestamp, final String argsMessage,
            final String argsPath, final List<String> argsDetails) {
        super();
        this.timestamp = argsTimestamp;
        this.message = argsMessage;
        this.path = argsPath;
        this.details = argsDetails;
    }
    /**
     *
     * @return Date
     */
    public Date getTimestamp() {
        return timestamp;
    }
    /**
     *
     * @return String
     */
    public String getMessage() {
        return message;
    }
    /**
     *
     * @return String
     */
    public String getPath() {
        return path;
    }
    /**
     *
     * @return String
     */
    public List<String> getDetails() {
        return details;
    }
}
