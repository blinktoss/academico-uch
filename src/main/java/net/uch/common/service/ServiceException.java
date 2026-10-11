package net.uch.common.service;

/**
 * Base type for business-rule violations raised by the service layer. Being a
 * {@link RuntimeException} means a {@code @Transactional} service method rolls back by default
 * when one is thrown, and JSF managed beans can let it propagate or catch it to map onto an
 * {@code oncomplete} alert - keeping the rendering concern out of the service itself.
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
