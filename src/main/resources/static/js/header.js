document.addEventListener('DOMContentLoaded', function() {
  // 모든 링크를 절대 경로로 설정
  var links = document.querySelectorAll('#ftco-navbar .nav-link');
  links.forEach(function(link) {
    if (!link.href.startsWith(window.location.origin)) {
      link.href = window.location.origin + link.getAttribute('href');
    }
  });

  fetch('/api/check-login')
    .then(response => response.json())
    .then(data => {
      var loginItem = document.getElementById("login-item");
      var signupItem = document.getElementById("signup-item");
      var nicknameItem = document.getElementById("nickname-item");
      var bellItem = document.getElementById("bell-item");
      var logoutItem = document.getElementById("logout-item");
      var nickname = document.getElementById("nickname");

      if (data.isLoggedIn && data.nickname && data.userSeq) {
        // 로그인 상태
        loginItem.style.display = "none";
        signupItem.style.display = "none";

        nickname.innerText = data.nickname
        nickname.href = '/user/' + data.userSeq;  // 사용자 상세 페이지 링크 설정
        nicknameItem.style.display = "block";
        bellItem.style.display = "block";
        logoutItem.style.display = "block";
      } else {
        // 비로그인 상태
        loginItem.style.display = "block";
        signupItem.style.display = "block";
        nicknameItem.style.display = "none";
        bellItem.style.display = "none";
        logoutItem.style.display = "none";
      }
    })
    .catch(error => console.error('Error:', error));
});
