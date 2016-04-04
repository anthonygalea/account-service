# account-service

A simple service for handling accounts and transfers between the accounts, built using [compojure-api](https://github.com/metosin/compojure-api) and [datomic](http://www.datomic.com/).

[![Dependencies Status](https://jarkeeper.com/anthonygalea/account-service/status.svg)](https://jarkeeper.com/anthonygalea/account-service)

## Usage

* install [datomic-free](https://my.datomic.com/downloads/free)
* add path to datomic in your ~/.lein/profiles.clj
 * {:user {:datomic {:install-location "/usr/local/Cellar/datomic/0.9.5344/libexec"}}}
* lein datomic start
* lein datomic initialize
* lein ring server
* swagger docs: http://localhost:3000/index.html#/

## Tests

* lein midje
