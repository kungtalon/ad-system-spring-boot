package org.talon.ad.sender.kafka;

import com.google.gson.Gson;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.talon.ad.mysql.dto.IncreRowData;
import org.talon.ad.sender.ISender;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * Created by Zelong
 * On 2022/5/4
 **/
@Slf4j
@Component
@Profile("2222")
public class KafkaSender implements ISender {

    @Value("${adconf.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Gson gson = new Gson();

    @Autowired
    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(IncreRowData rowData) {
        kafkaTemplate.send(
                topic, gson.toJson(rowData)
        );
    }

    @KafkaListener(topics = {"${adconf.kafka.topic}"}, groupId = "ad-search")
    public void processMysqlRowData(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            IncreRowData rowData = gson.fromJson(
                    message.toString(),
                    IncreRowData.class
            );
            log.info("kafka process increRowData: {}" , gson.toJson(rowData));
        }
    }
}
