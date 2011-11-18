

(function( $ ){

  /* DROPDOWN PLUGIN DEFINITION
   * ========================== */

  $.fn.dropdown = function ( selector ) {
    return this.each(function () {
        $(selector || d, this).live('click', function (e) {
          var li = $(this).parent('li')
            , isActive = li.hasClass('open')

          clearMenus(true)
          !isActive && li.toggleClass('open')
          return false
        })
    })
  }

  /* APPLY TO STANDARD DROPDOWN ELEMENTS
   * =================================== */

  var d = 'a.menu, .dropdown-toggle'

  function clearMenus(forceClick) {
    $(d).each(function() {
        if (forceClick || !$(this).hasClass("no-click")) {
            $(this).parent('li').removeClass('open');
        }
    });
  }

  $(function () {
    $('html').bind("click", clearMenus(false));
    $('body').dropdown( '[data-dropdown] a.menu, [data-dropdown] .dropdown-toggle' )
  })

})( window.jQuery || window.ender );
