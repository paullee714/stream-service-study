# task manager 1
docker run -d --name flink-taskmanager-1 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink taskmanager

# task manager 2
docker run -d --name flink-taskmanager-2 --link flink-jobmanager:jobmanager -e JOB_MANAGER_RPC_ADDRESS=jobmanager flink taskmanager