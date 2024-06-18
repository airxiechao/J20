<div align="center">
	<h1>鲲擎安全态势感知（后端）</h1>
</div>

## Introduction

## Features

## Version
- v0.1 2024-06-07

## Documentation

## Usage

**Environment Preparation**

Make sure your environment meets the following requirements:

- **git**: you need git to clone and manage project versions.
- **jdk**: 11
- **maven**: 3.9.7
- **mysql**: 8
- **flink**: 1.16.3
- **kafka**: 3.7.0
- **minio**
- **elasticsearch**: 8.13.4
- **pcap**
  - linux: libpcap
  - windows: WinPcap or Npcap in WinPcap API-compatible Mode

**Build**
```bash
maven install
```

**Deploy**
- upload `j20-detection-job-boot.jar` to flink.
- upload `j20-allinone-boot.jar` to server of service.
- upload `j20-probe-network.zip` to server of target.

**Config**
- edit `aplication.yml` for `j20-allinone-boot.jar`.
- edit `probe-network.yml` for `j20-probe-network-boot.jar`.

**Start**
- allinone
    ```bash
    java -jar j20-allinone-boot.jar
    ```

- probe-network
    ```bash
    java -jar j20-probe-network-boot.jar
    ```

## Development
Backend is based on `Spring Boot 2.7.18`
## Communication
