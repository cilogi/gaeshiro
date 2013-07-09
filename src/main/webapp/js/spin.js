
shiro.spin =
(function() {
    var spin = null;

    return {
        start: function(jqueryElement) {
            if (spin == null) {
                spin = new Spinner({
                    color: "#802B2B"
                });
                spin.spin(jqueryElement.get(0));
             }
           return spin;
        },
        stop : function() {
            if (spin) {
                spin.stop();
                spin = null;
            }
        }
    }
})();