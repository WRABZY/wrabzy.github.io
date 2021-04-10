(function() {
  document.getElementById('searchBar').addEventListener('keydown', function(e) {
    if (e.keyCode === 13 && document.getElementById("searchBar").value != "") {
      window.open("https://yandex.ru/search/?text=" + document.getElementById("searchBar").value);
      document.getElementById("searchBar").value = "";
    }
  });
})();
