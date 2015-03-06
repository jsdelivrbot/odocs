# first visible version
* apply travis-ci plan
* !!! git init

# future version
## features
* !!! documentation ordering
* !!! create start/initial/dashboard page (HOME)
* !! online documentation repository - store documentations configuration in remote location for easier local deployment
* !! search - index using htmlunit or phantomjs and store collected data for easier search - with prefix search, etc
* ! favorites - add selected pages to favorites
* ! history - documentation view history
* notes and code snippets - user can add notes and snippets
* open documentation in new tab and window
* online viewer support which will allow to store downloaded data in local cache for latter offline access
## improvements
* redeploy should use already assigned port (no port change after deploy)
* redeploy should not unizp files if file wasn't modified
* add support for more then zip files

# technical
* !!!! use protractor and start writing automated integration tests
* !!! runnable application (in file db in user home, with database migration scripts, deployment directory, embedded jetty server).
* !!! jetty upgrade (or downgrade) - according to guys from jetty version M3 should work better
* !!! create database restrictions and apply @Valid for input beans to avoid invalid objects creation
* !! try to apply jooq or queryDSL
* evaluate and maybe try to use http://jscs.info/rules.html
