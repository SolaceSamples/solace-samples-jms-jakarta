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
 *  Solace Jakarta Messaging Examples: ActiveFlowIndication
 */

package com.solace.samples.jakarta.messaging.features;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.jms.Connection;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Queue;
import jakarta.jms.Session;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolConsumerEventListener;
import com.solacesystems.jms.SolConsumerEventSource;
import com.solacesystems.jms.SolJmsUtility;
import com.solacesystems.jms.events.SolConsumerEvent;

/**
 * Demonstrates active flow indication events on an exclusive queue, using the Solace
 * Jakarta Messaging API implementation.
 *
 * The sample creates two consumers on the same exclusive queue. The message broker makes
 * the first consumer active (FLOW_ACTIVE) and the second consumer inactive
 * (FLOW_INACTIVE). When the first consumer closes, the message broker promotes the
 * second consumer, which then receives its own FLOW_ACTIVE event.
 *
 * The queue used for messages is created on the message broker if it does not exist.
 * The queue must have exclusive access type for these events to be meaningful.
 */
public class ActiveFlowIndication {

    final String QUEUE_NAME = "Q/tutorial";

    // Counts the messages received by the first consumer
    final AtomicInteger messageCount = new AtomicInteger();

    public void run(String... args) throws Exception {

        String[] split = args[1].split("@");

        String host = args[0];
        String vpnName = split[1];
        String username = split[0];
        String password = args[2];

        System.out.printf("ActiveFlowIndication is connecting to Solace messaging at %s...%n", host);

        // Programmatically create the connection factory using default settings
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setVPN(vpnName);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        // Enables persistent queues or topic endpoints to be created dynamically
        // on the router, used when Session.createQueue() is called below
        connectionFactory.setDynamicDurables(true);

        // Create two connections to the Solace router, one per consumer
        Connection connection1 = connectionFactory.createConnection();
        Connection connection2 = connectionFactory.createConnection();

        // Create a non-transacted, Auto ACK session per connection.
        Session session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Session session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.printf("Connected to the Solace Message VPN '%s' with client username '%s'.%n", vpnName,
                username);

        // Create the queue programmatically and the corresponding router resource
        // will also be created dynamically because DynamicDurables is enabled.
        Queue queue1 = session1.createQueue(QUEUE_NAME);
        Queue queue2 = session2.createQueue(QUEUE_NAME);

        // From the first session, create the first consumer for the queue.
        MessageConsumer consumer1 = session1.createConsumer(queue1);
        consumer1.setMessageListener((Message message) -> {
            messageCount.incrementAndGet();
            System.out.println("First consumer received a message.");
        });

        // Register the active flow indication listener on the first consumer.
        // Note the use of synchronized as onEvent could be invoked from different
        // threads. Refer to the JavaDoc of SolConsumerEventListener.
        SolConsumerEventSource eventSource1 = (SolConsumerEventSource) consumer1;
        eventSource1.setSolConsumerEventListener(new SolConsumerEventListener() {
            @Override
            public synchronized void onEvent(SolConsumerEvent event) {
                System.out.println("From first consumer : " + event.toString());
            }
        });

        // From the second session, create the second consumer for the same queue.
        MessageConsumer consumer2 = session2.createConsumer(queue2);
        SolConsumerEventSource eventSource2 = (SolConsumerEventSource) consumer2;
        eventSource2.setSolConsumerEventListener(new SolConsumerEventListener() {
            @Override
            public synchronized void onEvent(SolConsumerEvent event) {
                System.out.println("From second consumer : " + event.toString());
            }
        });

        // Start receiving messages
        connection1.start();

        System.out.println("Press enter to terminate the first consumer.");
        System.in.read(new byte[80]);

        System.out.println("Number of messages received: " + messageCount.get());

        // Close the first consumer. The message broker promotes the second consumer.
        consumer1.close();
        session1.close();
        connection1.close();

        System.out.println("An active event should be received from the second consumer.");
        System.out.println("Press enter to exit.");
        System.in.read(new byte[80]);

        // Close everything in the order reversed from the opening order
        consumer2.close();
        session2.close();
        connection2.close();
    }

    public static void main(String... args) throws Exception {
        if (args.length != 3 || args[1].split("@").length != 2) {
            System.out.println(
                    "Usage: ActiveFlowIndication <host:port> <client-username@message-vpn> <client-password>");
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
        new ActiveFlowIndication().run(args);
    }
}
