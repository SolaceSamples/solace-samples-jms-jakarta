/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 *  Solace Jakarta Messaging Examples: SecureSession
 */

package com.solace.samples.jakarta.messaging.features;

import jakarta.jms.Connection;
import jakarta.jms.DeliveryMode;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

/**
 * Connects to the message broker over TLS and publishes a message to a topic, using the
 * Solace Jakarta Messaging API implementation.
 *
 * The host must use the secure scheme and port, for example smfs://host:55443.
 *
 * A server certificate must be installed on the message broker and TLS must be enabled
 * on the message broker for this sample to work.
 *
 * With certificate validation (the default), the message broker's certificate chain must
 * be signed by one of the root CAs in the trust store passed to this sample. When no
 * trust store is passed, this sample disables certificate validation. Do not disable
 * certificate validation in production.
 *
 * For client certificate authentication, the connection factory also provides
 * setSSLKeyStore, setSSLKeyStorePassword, and setAuthenticationScheme. That flow is not
 * exercised by this sample.
 */
public class SecureSession {

    final String TOPIC_NAME = "T/GettingStarted/pubsub";

    public void run(String... args) throws Exception {

        String[] split = args[1].split("@");

        String host = args[0];
        String vpnName = split[1];
        String username = split[0];
        String password = args[2];
        String trustStore = (args.length > 3) ? args[3] : null;
        String trustStorePassword = (args.length > 4) ? args[4] : null;

        System.out.printf("SecureSession is connecting to Solace messaging at %s...%n", host);

        // Programmatically create the connection factory using default settings
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpnName);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        if (trustStore != null) {
            // Validate the broker's certificate chain against the root CAs in the trust store.
            connectionFactory.setSSLTrustStore(trustStore);
            if (trustStorePassword != null) {
                connectionFactory.setSSLTrustStorePassword(trustStorePassword);
            }
        } else {
            // No trust store was passed, so certificate validation is disabled.
            // Do not disable certificate validation in production.
            System.out.println("No trust store was passed. Certificate validation is disabled.");
            connectionFactory.setSSLValidateCertificate(false);
        }

        // Create connection to the Solace router over TLS
        Connection connection = connectionFactory.createConnection();

        // Create a non-transacted, Auto ACK session.
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.printf("Connected over TLS to the Solace Message VPN '%s' with client username '%s'.%n",
                vpnName, username);

        // Create the publishing topic programmatically
        Topic topic = session.createTopic(TOPIC_NAME);

        // Create the message producer for the created topic
        MessageProducer messageProducer = session.createProducer(topic);

        // Create the message
        TextMessage message = session.createTextMessage("Hello world over TLS!");

        System.out.printf("Sending message '%s' to topic '%s'...%n", message.getText(), topic.getTopicName());

        // Send the message
        messageProducer.send(topic, message, DeliveryMode.NON_PERSISTENT,
                Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

        System.out.println("Sent successfully. Exiting...");

        // Close everything in the order reversed from the opening order
        messageProducer.close();
        session.close();
        connection.close();
    }

    public static void main(String... args) throws Exception {
        if (args.length < 3 || args.length > 5 || args[1].split("@").length != 2) {
            System.out.println("Usage: SecureSession <smfs://host:port> <client-username@message-vpn> "
                    + "<client-password> [trust-store] [trust-store-password]");
            System.out.println();
            System.exit(-1);
        }
        if (args[1].split("@")[0].isEmpty()) {
            System.out.println("No client-username entered");
            System.out.println();
            System.exit(-1);
        }
        if (args[1].split("@")[1].isEmpty()) {
            System.out.println("No message-vpn entered");
            System.out.println();
            System.exit(-1);
        }
        new SecureSession().run(args);
    }
}
