class modalManager {
    static modalContainer = null;
    static userRoleChangeHandler = {};
    static willCloseCallback = null;
    static didCloseCallback = null;
    static handleOutsideClick = null;

    /**
     * 모달 생성 및 속성 초기화
     */
    static initialize() {
        if (!this.modalContainer) {
            this.modalContainer = document.createElement("div");
            this.modalContainer.id = "custom-modal-container";
            this.modalContainer.style.position = "fixed";
            this.modalContainer.style.top = "0";
            this.modalContainer.style.left = "0";
            this.modalContainer.style.width = "100vw";
            this.modalContainer.style.height = "100vh";
            this.modalContainer.style.backgroundColor = "rgba(0, 0, 0, 0.5)";
            this.modalContainer.style.borderRadius = "0";
            //this.modalContainer.style.backdropFilter = "blur(10px)";
            this.modalContainer.style.display = "none";
            this.modalContainer.style.alignItems = "center";
            this.modalContainer.style.justifyContent = "center";
            this.modalContainer.style.zIndex = "1000";
            document.body.appendChild(this.modalContainer);
        }
    }

    /**
     * 모달 표시
     */
    static show(config) {
        return new Promise((resolve, reject) => {
            const defaultConfig = {
                title: "",                  // 모달 제목
                html: "",                   // 모달 내용 HTML
                icon: null,                 // 모달 아이콘 (success, error, warning, info)
                allowOutsideClick: true,    // 모달 외부 클릭으로 닫기 허용 여부
                showConfirmButton: true,    // 모달 확인 버튼 표시 여부
                confirmButtonText: "OK",    // 모달 확인 버튼 텍스트
                showCancelButton: false,    // 모달 취소 버튼 표시 여부
                cancelButtonText: "Cancel", // 모달 취소 버튼 텍스트
                showCloseButton: true,      // 모달 닫기 버튼 표시 여부
                preConfirm: null,           // 모달 확인 버튼 클릭 전 실행할 Promise 함수
                preDeny: null,              // 모달 취소 버튼 클릭 전 실행할 Promise 함수
                didOpen: null,              // 모달 열린 후 실행할 콜백 함수
                didClose: null,             // 모달 닫힌 후 실행할 콜백 함수
                didRender: null,            // 모달 렌더링 후 실행할 콜백 함수
                willOpen: null,             // 모달 열리기 전 실행할 콜백 함수
                willClose: null,            // 모달 닫히기 전 실행할 콜백 함수
                callback: null,             // 확인 버튼 콜백 함수
                width: 'auto',              // 모달 너비
                height: 'auto',             // 모달 높이
            };

            const finalConfig = { ...defaultConfig, ...config };

            this.willCloseCallback = finalConfig.willClose;
            this.didCloseCallback = finalConfig.didClose;

            this.initialize();

            const iconHtml = this.getIconHtml(finalConfig.icon);

            this.modalContainer.innerHTML = `
                <div id="custom-modal-container">
                    <div id="custom-modal-container-header">
                        <div style="padding: 0px 20px; display: flex; align-items: center;">
                            ${iconHtml}
                            <h2 style="font-size: 1rem; color: white; margin: 10px;">${finalConfig.title || ""}</h2>
                        </div>
                        ${finalConfig.showCloseButton ? `<button id="custom-modal-close">×</button>` : ''}
                    </div>

                    <div id="custom-modal-container-body">
                        <div style="${isHTML(finalConfig.html) ? `width: ${finalConfig.width}; height: ${finalConfig.height};` : `margin: 15px 0px 15px 15px; width: ${finalConfig.width}; height: ${finalConfig.height};`}">${finalConfig.html || ""}</div>
                        <div id="custom-modal-validation-message"></div>
                    </div>

                    ${
                        `<div id="custom-modal-container-tail">
                            ${finalConfig.showConfirmButton ? `<button id="custom-modal-confirm" style="">${finalConfig.confirmButtonText}</button>` : ''}
                            ${finalConfig.showCancelButton  ? `<button id="custom-modal-cancel" style="">${finalConfig.cancelButtonText}</button>` : ''}
                        </div>`
                    }
                </div>
            `;

            this.modalContainer.style.display = "flex";

            if (finalConfig.allowOutsideClick) {
                if (!modalManager.handleOutsideClick) {
                    modalManager.handleOutsideClick = (e) => {
                        if (e.target === this.modalContainer) {
                            this.close();
                            resolve(false);
                        }
                    };
                }
                this.modalContainer.addEventListener("click", modalManager.handleOutsideClick);
            }

            if (finalConfig.showCloseButton) {
                document.getElementById("custom-modal-close").addEventListener("click", () => {
                    this.close();
                    resolve(false);
                });
            }

            if (finalConfig.showCancelButton) {
                document.getElementById("custom-modal-cancel").addEventListener("click", () => {
                    this.close();
                    resolve(false);
                });
            }

            if (finalConfig.willOpen && typeof finalConfig.willOpen === "function") {
                finalConfig.willOpen();
            }

            requestAnimationFrame(() => {
                if (finalConfig.didOpen && typeof finalConfig.didOpen === "function") {
                    finalConfig.didOpen();
                }
            });

            if (finalConfig.didRender && typeof finalConfig.didRender === "function") {
                finalConfig.didRender();
            }

            const confirmButton = document.getElementById("custom-modal-confirm");
            if (confirmButton) {
                confirmButton.addEventListener("click", () => {
                    let preConfirmResult = finalConfig.preConfirm ? finalConfig.preConfirm() : Promise.resolve();

                    if (!(preConfirmResult instanceof Promise)) {
                        preConfirmResult = Promise.resolve(preConfirmResult);
                    }

                    preConfirmResult
                        .then((result) => {
                            if(result !== false) {
                            //     resolve({ isConfirmed: false });
                            // }
                            // else{
                                this.close();
                                if (finalConfig.callback && typeof finalConfig.callback === "function") {
                                    finalConfig.callback();
                                }

                                resolve({ isConfirmed: true, value: result });
                            }
                        })
                        .catch((err) => {
                            console.error("preConfirm 오류:", err);
                            reject(err);
                        });
                });
            } else {
                resolve({ isConfirmed: true });
            }
        });
    }

    static showValidationMessage(message) {
        const validationMessageElement = document.getElementById("custom-modal-validation-message");
        if (validationMessageElement) {
            validationMessageElement.textContent = message;
            validationMessageElement.style.display = "flex";
            validationMessageElement.style.alignItems = "center";
            validationMessageElement.style.justifyContent = "center";
        }
    }

    static resetValidationMessage() {
        const validationMessageElement = document.getElementById("custom-modal-validation-message");
        if (validationMessageElement) {
            validationMessageElement.textContent = "";
            validationMessageElement.style.display = "none";
        }
    }

    /**
     * 아이콘 HTML 반환
     * @param {string} iconType - 아이콘 종류 (success, error, info, warning)
     * @returns {string}
     */
    static getIconHtml(iconType) {
        const icons = {
            success: "✔",
            error: "✖",
            info: "ℹ",
            warning: "⚠"
        };

        if (!icons[iconType]) return "";
        return `<div style="font-size: 1rem; color: ${
            iconType === "success"
                ? "green"
                : iconType === "error"
                ? "red"
                : iconType === "info"
                ? "white"
                : "darkorange"
        };">${icons[iconType]}</div>`;
    }

    /**
     * 모달 닫기
     */
    static close() {
        if (this.modalContainer) {
            if (this.willCloseCallback && typeof this.willCloseCallback === "function") {
                this.willCloseCallback();
            }

            this.modalContainer.style.display = "none";
            this.modalContainer.innerHTML = "";

            if (this.didCloseCallback && typeof this.didCloseCallback === "function") {
                this.didCloseCallback();
            }

            if (modalManager.handleOutsideClick) {
                this.modalContainer.removeEventListener("click", modalManager.handleOutsideClick);
            }

            this.willCloseCallback = null;
            this.didCloseCallback = null;
        }
    }
}

function isHTML(str) {
  const htmlTagRegex = /<([a-zA-Z]+)(?:\s[^>]*)?>(.*?)<\/\1>/g;
  return htmlTagRegex.test(str);
}

function alert2(title, html, icon, confirmButtonText, callback) {
    modalManager.show({
        title: title,
        html: html,
        icon: icon,
        confirmButtonText: confirmButtonText,
        callback: callback
    });
}

function alert3(type) {
    let title = "";
    let html = "<div class='flex-column-center custom-padding-20'>";
    let icon = "";

    if (type === "load") {
        title = "loading ...";
        html += `<img src="/images/loading.gif" alt="Loading" /></div>`;
        icon = "info";
    } else if (type === "delete") {
        title = "deleting ...";
        html += `<img src="/images/loading.gif" alt="Loading" /></div>`;
        icon = "warning";
    } else if (type === "save") {
        title = "saving ...";
        html += `<img src="/images/upload.gif" alt="Loading" /></div>`;
        icon = "success";
    } else if (type === "download") {
        title = "downloading ...";
        html += `<img src="/images/download.gif" alt="Loading" /></div>`;
        icon = "info";
    }

    modalManager.show({
        title: title,
        html: html,
        icon: icon,
        allowOutsideClick: false,
        showConfirmButton: false,
        didOpen: () => {
            document.getElementById("custom-modal-close").style.display = 'none';
        }
    });
}

function alert3Close() {
    modalManager.close();
}