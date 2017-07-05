package io.confluent.examples.streams.interactivequeries.kafkamusic;

/**
 * Copyright (c) Ericsson AB, 2016.
 * <p/>
 * All Rights Reserved. Reproduction in whole or in part is prohibited
 * without the written consent of the copyright owner.
 * <p/>
 * ERICSSON MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ERICSSON SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * <p/>
 * User: eurbmod
 * Date: 2017-07-04
 */

import io.confluent.examples.streams.utils.SpecificAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;

import java.util.*;
import java.util.concurrent.Future;

import io.confluent.examples.streams.avro.PlayEvent;
import io.confluent.examples.streams.avro.Song;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaMusicExampleDriver {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaMusicExample.class);

    public static void main(String[] args) throws Exception {
        final String bootstrapServers = args.length > 0 ? args[0] : "localhost:9092";
        final String schemaRegistryUrl = args.length > 1 ? args[1] : "http://localhost:8081";
        System.out.println("Connecting to Kafka cluster via bootstrap servers " + bootstrapServers);
        System.out.println("Connecting to Confluent schema registry at " + schemaRegistryUrl);
        final List<Song> songs = Arrays.asList(new Song(1L,
                        "Fresh Fruit For Rotting Vegetables",
                        "Dead Kennedys",
                        "Chemical Warfare",
                        "Punk"),
                new Song(2L,
                        "We Are the League",
                        "Anti-Nowhere League",
                        "Animal",
                        "Punk"),
                new Song(3L,
                        "Live In A Dive",
                        "Subhumans",
                        "All Gone Dead",
                        "Punk"),
                new Song(4L,
                        "PSI",
                        "Wheres The Pope?",
                        "Fear Of God",
                        "Punk"),
                new Song(5L,
                        "Totally Exploited",
                        "The Exploited",
                        "Punks Not Dead",
                        "Punk"),
                new Song(6L,
                        "The Audacity Of Hype",
                        "Jello Biafra And The Guantanamo School Of "
                                + "Medicine",
                        "Three Strikes",
                        "Punk"),
                new Song(7L,
                        "Licensed to Ill",
                        "The Beastie Boys",
                        "Fight For Your Right",
                        "Hip Hop"),
                new Song(8L,
                        "De La Soul Is Dead",
                        "De La Soul",
                        "Oodles Of O's",
                        "Hip Hop"),
                new Song(9L,
                        "Straight Outta Compton",
                        "N.W.A",
                        "Gangsta Gangsta",
                        "Hip Hop"),
                new Song(10L,
                        "Fear Of A Black Planet",
                        "Public Enemy",
                        "911 Is A Joke",
                        "Hip Hop"),
                new Song(11L,
                        "Curtain Call - The Hits",
                        "Eminem",
                        "Fack",
                        "Hip Hop"),
                new Song(12L,
                        "The Calling",
                        "Hilltop Hoods",
                        "The Calling",
                        "Hip Hop")

        );

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        final CachedSchemaRegistryClient schemaRegistry =
                new CachedSchemaRegistryClient(schemaRegistryUrl, 100);

        final Map<String, String> serdeProps = Collections.singletonMap("schema.registry.url", schemaRegistryUrl);

        final SpecificAvroSerializer<PlayEvent>
                playEventSerialzier = new SpecificAvroSerializer<>(schemaRegistry, serdeProps);
        playEventSerialzier.configure(serdeProps, false);

        final SpecificAvroSerializer<Song> songSerializer = new SpecificAvroSerializer<>(schemaRegistry, serdeProps);
        songSerializer.configure(serdeProps, false);

        final KafkaProducer<String, PlayEvent> playEventProducer =
                new KafkaProducer<>(props, Serdes.String().serializer(), playEventSerialzier);

        final KafkaProducer<Long, Song> songProducer =
                new KafkaProducer<>(props, new LongSerializer(), songSerializer);

        songs.forEach(song -> {
            LOG.info("Writing song information for '" + song.getName() + "' to input topic " +
                    KafkaMusicExample.SONG_FEED);
            songProducer.send(new ProducerRecord<>(KafkaMusicExample.SONG_FEED, song.getId(), song));
        });

        songProducer.close();
        final long duration = 60 * 1000L;
        final Random random = new Random();

        // send a play event every 100 milliseconds
        while (true) {
            final Song song = songs.get(random.nextInt(songs.size()));
            LOG.info("Writing play event for song " + song.getName() + " to input topic " + KafkaMusicExample.PLAY_EVENTS);
            Future<RecordMetadata> l =
                    playEventProducer.send( new ProducerRecord<>(KafkaMusicExample.PLAY_EVENTS,
                            "uk",
                            new PlayEvent(song.getId(), duration)));
            Thread.sleep(100L);
        }
    }

}
