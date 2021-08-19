#!/bin/bash
`docker run -d --name flink-jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager -p 8081:8081 arm64v8/flink jobmanager`