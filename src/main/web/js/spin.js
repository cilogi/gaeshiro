
shiro.spin =
(function() {

    var div = $("#spinner").get(0),
        spin = new Spinner({
            color: "#802B2B"
        });

    return {
        start: function() {
            spin.spin(div);
        },
        stop: function() {
            spin.stop();
        }
    }
})();