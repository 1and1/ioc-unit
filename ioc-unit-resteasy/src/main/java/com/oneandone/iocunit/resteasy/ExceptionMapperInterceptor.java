package com.oneandone.iocunit.resteasy;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author aschoerk
 */
public class ExceptionMapperInterceptor implements ExceptionMapper {

    private final ExceptionMapper exceptionMapper;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExceptionMapperInterceptor(ExceptionMapper exceptionMapper) {
        this.exceptionMapper = exceptionMapper;
    }

    @Override
    public Response toResponse(final Throwable throwable) {
        logger.info(exceptionMapper.getClass().getCanonicalName() + " maps ",throwable);
        return exceptionMapper.toResponse(throwable);
    }
}
