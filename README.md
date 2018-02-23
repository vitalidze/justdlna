JustDLNA
========

## Purpose

The main goal is to provide a simple open source implementation of UPNP/DLNA service. From my point of view [current server applications](http://en.wikipedia.org/wiki/List_of_UPnP_AV_media_servers_and_clients#UPnP_AV_media_servers) are over-engineered. This should not be THAT hard to deliver media content over a local network.

## Features

* audio/video/image media server

* no database, no index (except for 'last viewed' folder), all data is read directly from file system

* fully customizable content tree along with special types of nodes

* runs as a service on windows/linux systems (with help of bundled java service wrapper)

* supports binding address configuration on systems with multiple network interfaces

* range header support for full control of media playback (feature of jetty servlet container)

* low memory requirements (for a Java Application, 64M in default configuration)

## Configuration file

TODO

## Building

Checkout project source from github:

    git clone https://github.com/vitalidze/justdlna.git

Project uses [maven assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) to build all distributions. Just run the command below to build .zip and .tar.gz distributions.

    mvn clean assembly:assembly

Archives with distributions will appear under the `target/` folder.

## Installation

### Linux service
TODO

### Windows service
TODO

## Known issues

On a system with multiple network interfaces (physical and/or virtual) by default the UPnP/DLNA service is bound to all available interfaces. However, the content is served for first random network interface. Thus, it is highly recommended to configure application to bind to a single IP address via `ipAddress` configuration element.

## Contacts

* Vitaly Litvak <vitavaque@gmail.com>

* Submit issues to [project issue tracker](https://github.com/vitalidze/justdlna/issues)
