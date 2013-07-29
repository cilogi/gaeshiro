
define([], function() {
    return function() {
        if((typeof console !== 'undefined')) {
            console.log(Array.prototype.slice.call(arguments, 0));
        }
    }
});

