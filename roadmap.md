# first usable version
* !!! migrate frontend to tabs
* !!! create tests for routing
* !!!! use protractor and start writing automated integration tests
* !!!!! start moving version files using InputStream and OutputStream to avoid out ot memory errors and decrease memory usage
* !!! create start/initial/dashboard page (HOME)
* !!! runnable application (in file db in user home, with database migration scripts, deployment directory).
* !! cleanup not necessary configurations after adding spring boot
* !!! jetty upgrade (or downgrade) - according to guys from jetty version M3 should work better
* !!! create database restrictions and apply @Valid for input beans to avoid invalid objects creation
* !! in manage documentations page remember currently expanded documentations and versions
* !! online documentation repository - store documentations configuration in remote location for easier local deployment
* !! add support for other files then .zip
* !!! introduce notifications (pick existing notification library)
* !!! add progress bar for pending requests
* !!!! create managable service for cleaning temporary data on app start and shutdown
* ! redeploy should use already assigned port (no port change after deploy)
* redeploy should not unizp files if file wasn't modified

# future version
## features
* investigate possibilities to automatically update documentation feed (maybe share from application, automated generator?)
* !! search - index using htmlunit or phantomjs and store collected data for easier search - with prefix search, etc
* ! favorites - add selected pages to favorites
* ! history - documentation view history
* notes and code snippets - user can add notes and snippets
* open documentation in new tab and window
* online viewer support which will allow to store downloaded data in local cache for latter offline access

# technical
* evaluate and maybe try to use http://jscs.info/rules.html
* try to avoid extracting documentations from archives. Maybe it will be possible to serve them from archived files
* lazy deployment - do ont deploy unless needed, and undeploy if all tabs closed?
