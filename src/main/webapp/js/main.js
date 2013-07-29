

require.config({
    baseUrl: "js",
    catchError : {
        define: true
    },
    // 3rd party script alias names (Easier to type "jquery" than "libs/jquery-1.8.2.min")
    paths: {
        "jquery": "lib/jquery-1.10.1.min",
        'bootstrap':"bootstrap/bootstrap",
        'jquery.blockUI' : 'lib/jquery.blockUI',
        'jquery.busy' : 'lib/jquery.busy.min',
        'jquery.dataTables' : 'lib/jquery.dataTables.min',
        'jquery.validate' : 'lib/jquery.validate.min',
        'persona': 'https://login.persona.org/include'

    },

    // Sets the configuration for your third party scripts that are not AMD compatible
    shim: {
        'bootstrap' : ['jquery'],
        'jquery.busy' : ['jquery'],
        'jquery.dataTables' : ['jquery'],
        'jquery.validate' : ['jquery']
    } // end Shim Configuration

});

require(['jquery', 'app', 'bootstrap'], function ($, app) {
    $(document).ready(function() {
        app();
    });
});
