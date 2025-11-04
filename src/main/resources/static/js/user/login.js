// static/js/user/login.js

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('loginError');

    if (!loginForm) {
        return;
    }

    loginForm.addEventListener('submit', async function (event) {
        event.preventDefault();

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        if (username.length === 0) {
            showError('아이디를 입력해주세요.');
            usernameInput.focus();
            return;
        }

        if (password.length === 0) {
            showError('비밀번호를 입력해주세요.');
            passwordInput.focus();
            return;
        }

        try {
            const body = new URLSearchParams();
            body.append('username', username);
            body.append('password', password);

            const response = await fetch('/perform_login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body,
            });

            const result = await response.json();

            if (response.ok && result.success && result.data) {
                persistTokens(result.data);
                window.location.href = result.data.redirectUrl ?? '/';
            } else {
                const message = result?.message ?? '로그인에 실패했습니다.';
                showError(message);
            }
        } catch (error) {
            console.error('로그인 요청 중 오류', error);
            showError('로그인 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        }
    });

    const googleLoginButton = document.querySelector('.btn-google');
    if (googleLoginButton) {
        googleLoginButton.addEventListener('click', function() {
            window.location.href = '/oauth2/authorization/google'; // OAuth2 로그인 엔드포인트로 이동
        });
    }

    function persistTokens(data) {
        if (!data) {
            return;
        }

        try {
            // 클라이언트 사이드에서 JWT를 재사용할 수 있도록 로컬 스토리지에 저장합니다.
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            localStorage.setItem('tokenType', data.tokenType ?? 'Bearer');
        } catch (e) {
            console.warn('토큰 저장 중 오류', e);
        }
    }

    function showError(message) {
        if (!errorMessage) {
            alert(message);
            return;
        }

        errorMessage.textContent = message;
        errorMessage.hidden = false;
    }
});