# odocs

[![Build Status](https://travis-ci.org/pchudzik/odocs.svg?branch=master)](https://travis-ci.org/pchudzik/odocs)

It will be in browser offline documentation viewer.
There is a lot of similar stuff:
* http://zealdocs.org/
* http://kapeli.com/dash

I've decided to write my own for fun, and maybe just maybe somebody might want to use it.
Currently there is not much to talk about. It's possible to start application on my local machine.
You can upload documentation (in theory any documentation - no external processing is required).
For example you can download angular documentations (provided with angular library itself), upload it and use it.

I've a lot of plans see [roadmap.md](other_file.md)

## technology stack
Yay yet another java webapp with angular frontend ;)
backend is java8 with in H2 database as storage.
Frontend in angular 1.3.
There is a lot of libraries see build.gradle and bower.json for details

## project structure
* /src/main/java and /src/test/java - backend code and tests
* /src/main/js - frontend code

## how to run it
there is no working version yet - it's big under construction thing :)
If you want to take it for test run (It probably won't work on your machine but with little creativity you should be able to run it).
```
./gradlew jettyStart #start backend from project root directory
npm install && bower install && grunt server # start frontend from src/main/js directory
```
for more details about what is required to build this project see [.travis.yml](.travis.yml)
