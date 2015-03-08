# first usable version
* !!!! use protractor and start writing automated integration tests
* !!! create start/initial/dashboard page (HOME)
* !!! runnable application (in file db in user home, with database migration scripts, deployment directory, embedded jetty server).
* !!! jetty upgrade (or downgrade) - according to guys from jetty version M3 should work better
* !!! create database restrictions and apply @Valid for input beans to avoid invalid objects creation
* !! in manage documentations page remember currently expanded documentations and versions
* !! online documentation repository - store documentations configuration in remote location for easier local deployment
* !! add support for other files then .zip
* ! redeploy should use already assigned port (no port change after deploy)
* redeploy should not unizp files if file wasn't modified

# future version
## features
* !! search - index using htmlunit or phantomjs and store collected data for easier search - with prefix search, etc
* ! favorites - add selected pages to favorites
* ! history - documentation view history
* notes and code snippets - user can add notes and snippets
* open documentation in new tab and window
* online viewer support which will allow to store downloaded data in local cache for latter offline access

# technical
* evaluate and maybe try to use http://jscs.info/rules.html
