
# Nautilus Video Demo

## Overview

This projects demonstrates how to use several key features of Nautilus to perform real-time video storage and analytics.

## Components

- Pravega: Pravega provides a new storage abstraction - a stream - for continuous and unbounded data.
  A Pravega stream is a durable, elastic, append-only, unbounded sequence of bytes that has good performance and strong consistency.

  Pravega provides dynamic scaling that can increase and decrease parallelism to automatically respond
  to changes in the event rate.

  See <http://pravega.io> for more information.

- Pravega Video Demo: This is a simple command-line application that demonstrates how to write video files to Pravega.
  It also demonstrates how to read the video files from Pravega and decode them.

- Docker: This demo uses Docker and Docker Compose to greatly simplify the deployment of the various
  components on Linux and/or Windows servers, desktops, or even laptops.
  For more information, see <https://en.wikipedia.org/wiki/Docker_(software)>.

## Building and Running the Demo

### Prerequisites

A Linux VM with 8 GB of RAM and 200 GB of storage is recommended.
On Windows, it is recommended to launch an Ubuntu VM and perform Pravega development within it.

### Install Operating System

Install Ubuntu 16.04 LTS. Other operating systems can also be used but the commands below have only been tested
on this version.

### Install Java

```
apt-get install default-jdk
```

### Install IntelliJ

Install from <https://www.jetbrains.com/idea>.
Enable the Lombok plugin.
Enable Annotations (settings -> build, execution, deployment, -> compiler -> annotation processors).

### Install Docker and Docker Compose

See <https://docs.docker.com/install/linux/docker-ce/ubuntu/>
and <https://docs.docker.com/compose/install/>.

### Run Pravega

This will run a development instance of Pravega locally.

In the command below, replace x.x.x.x with the IP address of a local network interface such as eth0.

```
cd pravega
export HOST_IP=x.x.x.x
docker-compose up -d
```

You can view the Pravega logs with `docker-compose logs --follow`.
You can view the stream files stored on HDFS with `docker-compose exec hdfs hdfs dfs -ls -h -R /`.

### Run Demo Application

Obtain some MP4 video files.

```
wget http://techslides.com/demos/sample-videos/small.mp4
wget https://archive.org/download/WildlifeSampleVideo/Wildlife.mp4
```

To write the video files to Pravega, use IntelliJ to run the class `io.pravega.example.video.videodemo.Main` with parameters `--mode write small.mp4 Wildlife.mp4`.
You may specify as many files as you like.
You may run the command multiple times to append the files again.

To read the video files, use the parameters `--mode read`.

# See Also

- <http://pravega.io/>
- <https://github.com/pravega/pravega>
- <https://github.com/pravega/pravega-samples>
- <https://github.com/claudiofahey/nautilus-iiot-demo>
- <https://github.com/claudiofahey/taxidemo>
