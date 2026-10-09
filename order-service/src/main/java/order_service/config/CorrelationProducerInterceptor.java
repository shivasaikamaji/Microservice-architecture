package order_service.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.MDC;

public class CorrelationProducerInterceptor implements ProducerInterceptor<Object, Object> {

    private static final String HEADER = "X-Correlation-Id";

    @Override
    public ProducerRecord<Object, Object> onSend(ProducerRecord<Object, Object> record) {
        String id = MDC.get("correlationId");
        if (id != null && record.headers().lastHeader(HEADER) == null) {
            record.headers().add(HEADER, id.getBytes(StandardCharsets.UTF_8));
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) { }

    @Override
    public void close() { }

    @Override
    public void configure(Map<String, ?> configs) { }
}