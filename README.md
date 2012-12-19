grails-remote-methods
=====================

To install plugin add

    compile ':grails-remote-methods:0.1'

to the plugins section of your BuildConfig.groovy.

Usage:

1. Define on gsp (all attributes are optional, you can specify remote methods in controller as static property 'remoteMethods')

        <g:defineRemote controller="index" methods="['user_info']"/>

2. Call any remote method from your js like this:

        index.user_info(
            {test: '123'},
            function(data) {
                console.log(data);
            },
            function() {
              console.log(arguments);
            }
        )

(errror function is optinal)

