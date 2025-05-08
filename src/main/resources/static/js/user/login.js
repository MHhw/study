// static/js/user/login.js

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    if (loginForm) {
        loginForm.addEventListener('submit', function (event) {
            let isValid = true;

            // 간단한 아이디 입력 확인
            if (usernameInput.value.trim() === '') {
                alert('아이디를 입력해주세요.'); // 실제로는 더 나은 피드백 방식 사용
                usernameInput.focus();
                isValid = false;
                event.preventDefault(); // 폼 제출 막기
                return;
            }

            // 간단한 비밀번호 입력 확인
            if (passwordInput.value.trim() === '') {
                alert('비밀번호를 입력해주세요.');
                passwordInput.focus();
                isValid = false;
                event.preventDefault(); // 폼 제출 막기
                return;
            }

            if (isValid) {
                // 실제 로그인 처리는 백엔드에서 수행됩니다.
                // 이 부분은 현재는 시연용으로, 백엔드 로직이 없음을 인지시켜줍니다.
                console.log('폼 제출 시도:', usernameInput.value, passwordInput.value);
                // event.preventDefault(); // 실제 백엔드 연동 전까지는 제출을 막아둘 수 있습니다.
                                        // 주석 해제 시, 폼이 실제로 제출되지 않습니다.
            }
        });
    }

    // Google 로그인 버튼 예시 (실제 OAuth2 연동 필요)
    const googleLoginButton = document.querySelector('.btn-google');
    if (googleLoginButton) {
        googleLoginButton.addEventListener('click', function() {
            // 현재는 백엔드 OAuth2 연동 로직이 없으므로 alert만 띄웁니다.
            // 추후에는 location.href = '/oauth2/authorization/google'; 와 같이 변경됩니다.
            alert('Google 로그인 기능은 현재 개발 중입니다. (백엔드 연동 필요)');
            console.log('Google 로그인 버튼 클릭');
        });
    }

});