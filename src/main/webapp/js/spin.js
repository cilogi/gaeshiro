
define(['jquery', 'lib/spinner'], function($, Spinner) {
    var theSpin = null;
    return {
        start: function(jqueryElement) {
            if (theSpin == null) {
                theSpin = new Spinner({color: "#802B2B"});
            }
            theSpin.spin(jqueryElement.get(0));
            return theSpin;
        },
        stop : function() {
            if (theSpin) {
                theSpin.stop();
                theSpin = null;
            }
        }
    }
});