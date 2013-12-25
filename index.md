---
layout: index
---

JustDLNA
========

## Purpose
The main goal is to provide a simple open source implementation of UPNP/DLNA service. From my point of view [current server applications](http://en.wikipedia.org/wiki/List_of_UPnP_AV_media_servers_and_clients#UPnP_AV_media_servers) are over-engineered. This should not be THAT hard to deliver media content over a local network.

## Features
TODO

## Configuration file
TODO

## Building

Checkout project source from github:

    git clone https://github.com/vitalidze/justdlna.git

Project uses [maven assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) to build all distributions. Just run the command below to build .zip, .tar.gz and .tar.bz2 distributions.

    mvn clean assembly:assembly

Archives with distributions will appear under the `target/` folder.

## Installation

### Linux service
TODO

### Windows service
TODO

## Known issues
TODO Write about multi-interface machines

## Contacts

* Vitaly Litvak <vitavaque@gmail.com>

* Submit issues to [project issue tracker](https://github.com/vitalidze/justdlna/issues)
