(function() {
  document.getElementById('searchBar').addEventListener('keydown', function(e) {
    if (e.keyCode === 13 && document.getElementById("searchBar").value != "") {
      window.open("https://duckduckgo.com/?q=" + document.getElementById("searchBar").value + "&t=hx&va=g&atb=v289-4gx&ia=web");
      document.getElementById("searchBar").value = "";
    }
  });
})();
