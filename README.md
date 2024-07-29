# URL Shortener

This project consists of two portions:
- **this project, the url shortener, written in kotlin with spring**
- events server, a dead simple server that record events, written in rust using actix.

The main purpose of this project is to demonstrate a really simple application done within a few hours, not something
that's meant to be deployed on a real production server. 

You have two options to start this server.

1. Following the instructions found in this read me. This is the recommended approach as it have both the shorten url and events server up and running.
2. Using the script `./scripts/start_docker.sh` but this will disable recording events since docker will require some changes for it to talk to localhost.

## How to run the program

This assumes you already have docker installed. If you don't, please install it. 

Now you have to options, either use the `init_db.sh` found in the rust project set up the docker postgres image for you.
It'll also execute all the migrations for the events service. 

> Note: If you don't have docker install and don't care about the events server, all you have to do is run `./scripts/start_docker.sh` or  `./gradlew bootRun` or `./gradlew build && docker-compose up`(or `docker compose up` if you're using a newer version) .

### Manually

Pull the postgres image.
```shell
docker pull postgres
```

Run it in the background:
```shell
docker run --name postgres --publish 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

### Postgres ready

Now that you have postgres ready, you need to migrate the urls database:

`./gradlew flywayClean && ./gradlew flywayMigrate`


### Start the app - Kotlin

Now, all you have to do is:


```./gradlew bootRunDev```  

> NOTE: bootRunDev requires the event server to be up! BEFORE making requests, go to the rust repo to see how to start the rust server!


```./gradlew bootRun```
> Note: Use this command if you don't care about the events server application. This uses an in memory sqlite instead.

### Start the app - Rust

Please refer to the rust repo for a more detail explanation. You need the latest version of rust. 
After installing it, cargo build & cargo run is all you need to do.

## Requests

Here are the list of requests you can do:

### Create a shorter url

URLs are validated hibernate. Hence if you pass a request like:
```shell
curl --location 'localhost:8080/shorten' \
--header 'Content-Type: application/json' \
--data '{
    "url": "foo"
}
'
```
You'll get a bad request.


Instead, once you pass in a valid url, we'll get a shorter version:
```shell
curl --location 'localhost:8080/shorten' \
--header 'Content-Type: application/json' \
--data '{
    "url": "https://www.google.com"
}
'
```

Example response
```shell
{
    "originalUrl": "https://www.google.com",
    "url": "http://localhost:8080/RW45p"
}
```

Copy and paste the `url` in your browser or do a simple curl command:

```shell
curl --location 'localhost:8080/RW45p'
```

You should be redirected to your url.

To see how many clicks a certain short url has, just do:
```shell
curl --location 'localhost:8080/RW45p/clicks'
```
There were plans to check the range, get the top 5 short urls, etc but I didn't want to invest more than
6 hours on this project.

The urls are shorten to 5 characters. Since we're using base62, it gives us 62^5 number of unique strings.
This gives us close to 1 billion new urls. To scale it even more, just increase te number of characters.