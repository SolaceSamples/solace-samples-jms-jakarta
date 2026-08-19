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
 *  Solace Jakarta Messaging Examples: QueueBrowser
 */

package com.solace.samples.jakarta.messaging.features;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import jakarta.jms.Connection;
import jakarta.jms.Message;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

/**
 * Browses messages on a queue without consuming them, using the Solace Jakarta Messaging API
 * implementation. Run QueueProducer first to place messages on the queue.
 *
 * The queue used for messages is created on the message broker if it does not exist.
 */
public class QueueBrowser {

    final String QUEUE_NAME = "Q/tutorial";

    public void run(String... args) throws Exception {

        String[] split = args[1].split("@");

        String host = args[0];
        String vpnName = split[1];
        String username = split[0];
        String password = args[2];

        System.out.printf("QueueBrowser is connecting to Solace messaging at %s...%n", host);

        // Programmatically create the connection factory using default settings
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpnName);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        // Enables persistent queues or topic endpoints to be created dynamically
        // on the router, used when Session.createQueue() is called below
        connectionFactory.setDynamicDurables(true);

        // Create connection to the Solace router
        Connection connection = connectionFactory.createConnection();

        // Create a non-transacted, Auto ACK session.
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.printf("Connected to the Solace Message VPN '%s' with client username '%s'.%n", vpnName,
                username);

        // Create the queue programmatically and the corresponding router resource
        // will also be created dynamically because DynamicDurables is enabled.
        Queue queue = session.createQueue(QUEUE_NAME);

        // From the session, create a browser for the queue.
        jakarta.jms.QueueBrowser browser = session.createBrowser(queue);

        connection.start();

        // Iterate over the messages on the queue.
        // enumeration.hasMoreElements() returns true only if there are
        // messages in the local queue. If there are no local messages and
        // messages are in flight from the broker, it will return false.
        // So it does not necessarily mean that the broker queue is empty.
        // If you want to browse every message on a queue, it is better to use
        // enumeration.nextElement(). It keeps returning messages until
        // the local queue is empty and it has not received a message for 10 seconds.
        Enumeration<?> enumeration = browser.getEnumeration();
        int count = 0;
        while (true) {
            try {
                Message message = (Message) enumeration.nextElement();
                count++;
                if (message instanceof TextMessage) {
                    System.out.printf("Browsed TextMessage: '%s'%n", ((TextMessage) message).getText());
                } else {
                    System.out.printf("Browsed Message:%n%s%n", SolJmsUtility.dumpMessage(message));
                }
            } catch (NoSuchElementException e) {
                break;
            }
        }
        System.out.printf("Browsed %d messages on queue '%s'. Browsing does not consume messages.%n",
                count, QUEUE_NAME);

        connection.stop();
        // Close everything in the order reversed from the opening order
        browser.close();
        session.close();
        connection.close();
    }

    public static void main(String... args) throws Exception {
        if (args.length != 3 || args[1].split("@").length != 2) {
            System.out.println("Usage: QueueBrowser <host:port> <client-username@message-vpn> <client-password>");
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
        new QueueBrowser().run(args);
    }
}
