
define(['jquery', 'lib/spinner', 'log'], function($, Spinner, log) {
    var theSpin = null;
    return {
        start: function(jqueryElement) {
            if (theSpin) {
                theSpin.stop();
            }
            theSpin = new Spinner({color: "#802B2B"});
            theSpin.spin(jqueryElement.get(0));
            log("start spin");
            return theSpin;
        },
        stop : function() {
            if (theSpin) {
                theSpin.stop();
                theSpin = null;
                log("stop spin");
            }
        }
    }
});