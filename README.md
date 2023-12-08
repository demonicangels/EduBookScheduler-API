# EduBook Scheduler Backend
## Docker mysql
To run the database using docker, run the following command from the root folder of the repository:
```
docker compose -f docker-mysql-edubookscheduler/docker-compose.yml up -d
```
(make sure to have the docker service running).
To stop it simply run:
```
docker compose -f docker-mysql-edubookscheduler/docker-compose.yml down
```
(keep in mind that this will remove the container and thus all changes you made to the database,
we can probably do a Dockerfile that runs a mysql command that adds dummy tables for testing)
