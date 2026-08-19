# Getting Started Examples
## Solace Jakarta Messaging API

These samples use the [Jakarta Messaging API](https://jakarta.ee/specifications/messaging/) (`jakarta.jms` namespace) via Solace's `sol-jms-jakarta` library.

> **You can find the legacy `javax.jms` samples** at the [`javax.jms-implementation`](https://github.com/SolaceSamples/solace-samples-jms-jakarta/tree/javax.jms-implementation) branch.

The "Getting Started" tutorials will get you up to speed and sending messages with Solace technology as quickly as possible. There are three ways you can get started:

- Follow [these instructions](https://cloud.solace.com/learn/group_getting_started/ggs_signup.html) to quickly spin up a cloud-based Solace messaging service for your applications.
- Follow [these instructions](https://docs.solace.com/Solace-SW-Broker-Set-Up/Setting-Up-SW-Brokers.htm) to start the Solace VMR in leading Clouds, Container Platforms or Hypervisors. The tutorials outline where to download and how to install the Solace VMR.
- If your company has Solace message routers deployed, contact your middleware team to obtain the host name or IP address of a Solace message router to test against, a username and password to access it, and a VPN in which you can produce and consume messages.

## Contents

This repository contains code and matching tutorial walk throughs for basic Solace messaging patterns. For a nice introduction to the Solace API and associated tutorials, check out the [Solace JMS Tutorials](https://tutorials.solace.dev/jms/).

See the individual tutorials for details:

- [Publish/Subscribe](https://tutorials.solace.dev/jms/publish-subscribe/): Learn how to set up pub/sub messaging on a Solace VMR.
- [Persistence](https://tutorials.solace.dev/jms/persistence-with-queues/): Learn how to set up persistence for guaranteed delivery.
- [Request/Reply](https://tutorials.solace.dev/jms/request-reply/): Learn how to set up request/reply messaging.
- [Confirmed Delivery](https://tutorials.solace.dev/jms/confirmed-delivery/): Learn how to confirm that your messages are received by a Solace message router.
- [Topic to Queue Mapping](https://tutorials.solace.dev/jms/topic-to-queue-mapping/): Learn how to map existing topics to Solace queues.
- [Obtaining JMS objects using JNDI](https://tutorials.solace.dev/jms/using-jndi/): Learn how to use JNDI as a way to create JMS objects.
- [Obtaining JMS objects using external service JNDI](https://tutorials.solace.dev/jms/using-external-jndi/): Learn how to use external JNDI service as a way to create JMS objects.

## Prerequisites

This tutorial requires the Solace Jakarta Messaging API library (`sol-jms-jakarta`). There are multiple options for getting it.
### Option 1: Download the Jakarta Messaging API library
Download from [here](https://solace.com/downloads/?fwp_downloads=solace-apis). The Jakarta Messaging API is distributed as a zip file containing the required jars, API documentation, and examples.

### Option 2: Using it with Gradle
`implementation("com.solacesystems:sol-jms-jakarta:10.30.1")`
or `implementation group: 'com.solacesystems', name: 'sol-jms-jakarta', version: '10.30.1'`

### Option 3: Using it with Maven
```
<dependency>
   <groupId>com.solacesystems</groupId>
   <artifactId>sol-jms-jakarta</artifactId>
   <version>10.30.1</version>
</dependency>
```

## Build the Samples

Just clone and build. For example:

  1. clone this GitHub repository
  1. `./gradlew assemble`

## Running the Samples

To try individual samples, build the project from source and then run samples like the following:

    ./build/staged/bin/topicPublisher <host:port> <client-username@message-vpn> <client-password>

The individual tutorials linked above provide full details which can walk you through the samples, what they do, and how to correctly run them to explore Solace messaging.

## Exploring the Samples

### Setting up your preferred IDE

Using a modern Java IDE provides cool productivity features like auto-completion, on-the-fly compilation, assisted refactoring and debugging which can be useful when you're exploring the samples and even modifying the samples. Follow the steps below for your preferred IDE.

#### Using Eclipse

To generate Eclipse metadata (.classpath and .project files), do the following:

    ./gradlew eclipse

Once complete, you may then import the projects into Eclipse as usual:

 *File -> Import -> Existing projects into workspace*

Browse to the *'solace-samples-jms-jakarta'* root directory. All projects should import
free of errors.

#### Using IntelliJ IDEA

To generate IDEA metadata (.iml and .ipr files), do the following:

    ./gradlew idea

## Schema Registry Samples
This repository also includes samples demonstrating serialization and deserialization (SERDES) with Solace Schema Registry support:

- Avro SERDES: Learn how to serialize and deserialize messages using Avro schemas
- JSON Schema SERDES: Learn how to serialize and deserialize messages using JSON Schema validation

For detailed information about SERDES samples, Schema Registry setup, and schema upload instructions, see the [SERDES README](src/main/java/com/solace/samples/jakarta/messaging/features/serdes/README.md).

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Authors

See the list of [contributors](https://github.com/SolaceSamples/solace-samples-jms-jakarta/contributors) who participated in this project.

## License

This project is licensed under the Apache License, Version 2.0. - See the [LICENSE](LICENSE) file for details.

## Resources

For more information try these resources:

- The Solace Developer Portal website at: https://solace.dev
- Ask the https://solace.community
- Solace API Tutorials @ https://tutorials.solace.dev
