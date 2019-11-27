package com.oneandone.iocunit.dbunit.core.parsers;

import java.util.List;
import java.util.Map;

import com.oneandone.iocunit.dbunit.core.resources.Resource;
import com.oneandone.iocunit.dbunit.exception.JsonException;

public interface DatasetParser {

    /**
     * Read File and return representation.
     *
     * @param resource Input resource.
     * @return DataSet representation.
     * @throws JsonException If parse/read operation fail (invalid schema, unreadable file).
     */
    Map<String, List<Map<String, Object>>> parse(Resource resource);
}
