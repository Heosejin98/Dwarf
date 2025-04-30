package org.dwarf.collector.processor;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import org.dwarf.collector.processor.converter.OtelTraceConverter;
import org.dwarf.core.model.TraceData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceProcessorImpl implements TraceProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TraceProcessorImpl.class);

    @Override
    public void processTraces(ExportTraceServiceRequest serviceRequest) {
        TraceData traceData = OtelTraceConverter.convertToTraceData(serviceRequest);
        logger.debug("Converted to Trace model data {} ", traceData);
    }
}
