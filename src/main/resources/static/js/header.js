document.addEventListener('DOMContentLoaded', function() {
  fetch('/api/check-login')
    .then(response => response.json())
    .then(data => {
      var loginItem = document.getElementById("login-item");
      var signupItem = document.getElementById("signup-item");
      var nicknameItem = document.getElementById("nickname-item");
      var bellItem = document.getElementById("bell-item");
      var chatItem = document.getElementById("chat-item");
      var logoutItem = document.getElementById("logout-item");
      var nickname = document.getElementById("nickname");
      var chatIcon = document.getElementById("chat-icon");
      var chatIconContainer = document.getElementById("chat-icon-container");

      if (data.isLoggedIn && data.nickname && data.userSeq) {
        loginItem.style.display = "none";
        signupItem.style.display = "none";

        nickname.innerText = data.nickname;
        nickname.href = '/user/' + data.userSeq;
        nicknameItem.style.display = "block";
        bellItem.style.display = "block";
        chatItem.style.display = "block";
        logoutItem.style.display = "block";

        chatIcon.addEventListener('click', function() {
          const sender = data.nickname;
          const receiver = "";
          openChatWindow(sender, receiver);
        });

        connectWebSocket(data.nickname);

        // 벨 아이콘 클릭 시 읽지 않은 메시지 모달 표시
        document.getElementById('bell-icon').addEventListener('click', function() {
          fetchUnreadMessages();
        });

        // 드래그 기능 활성화
        enableDrag(chatIconContainer);

        // 이전 위치 로드
        loadChatIconPosition();
      } else {
        loginItem.style.display = "block";
        signupItem.style.display = "block";
        nicknameItem.style.display = "none";
        bellItem.style.display = "none";
        chatItem.style.display = "none";
        logoutItem.style.display = "none";
      }
    })
    .catch(error => console.error('Error:', error));
});

function enableDrag(element) {
  let isDragging = false;
  let startX, startY, initialX, initialY;

  element.addEventListener('mousedown', (e) => {
    isDragging = true;
    startX = e.clientX;
    startY = e.clientY;
    initialX = element.offsetLeft;
    initialY = element.offsetTop;
    element.style.cursor = 'grabbing';
    document.body.style.userSelect = 'none'; // 텍스트 선택 비활성화
  });

  document.addEventListener('mousemove', (e) => {
    if (isDragging) {
      let deltaX = e.clientX - startX;
      let deltaY = e.clientY - startY;
      element.style.left = initialX + deltaX + 'px';
      element.style.top = initialY + deltaY + 'px';
    }
  });

  document.addEventListener('mouseup', () => {
    if (isDragging) {
      isDragging = false;
      element.style.cursor = 'grab';
      saveChatIconPosition(element.style.left, element.style.top);
      document.body.style.userSelect = ''; // 텍스트 선택 활성화
    }
  });
}

function saveChatIconPosition(left, top) {
  localStorage.setItem('chatIconLeft', left);
  localStorage.setItem('chatIconTop', top);
}

function loadChatIconPosition() {
  const left = localStorage.getItem('chatIconLeft');
  const top = localStorage.getItem('chatIconTop');

  if (left !== null && top !== null) {
    const chatIconContainer = document.getElementById('chat-icon-container');
    chatIconContainer.style.left = left;
    chatIconContainer.style.top = top;
  }
}
