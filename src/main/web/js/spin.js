
shiro.spin =
(function() {

    return {
        start: function(jqueryElement) {
            var spin = new Spinner({
                color: "#802B2B"
            });
            spin.spin(jqueryElement.get(0));
            return spin;
        }
    }
})();