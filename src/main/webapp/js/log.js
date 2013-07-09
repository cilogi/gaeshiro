
var shiro = shiro || {};

(function() {
    shiro.log = function() {
        if((typeof console !== 'undefined')) {
            console.log(Array.prototype.slice.call(arguments, 0));
        }
    }
})();

