const gridManager = {
    initialConfigs: {},

    /**
     * Grid 데이터를 서버에서 불러와 그리드를 생성하거나 업데이트
     * @param {Object} config - Grid 설정
     * @returns {Promise<any>}
     */
    grid: function (config) {
        return new Promise((resolve, reject) => {
            const defaultConfig = {
                url: "",                   // url
                columns: [],               // toast ui column
                complexColumns: "",        // toast ui complex column
                checkbox: false,           // checkbox 여부
                data: "",                  // 그 외 파라미터 (검색 등)
                gridElementId: "grid",     // 기본 그리드 DOM ID
                tableCountId: "",          // 기본 그리드 카운트 표시 DOM ID
                paginationOption: false,   // 페이지네이션 활성화 여부
                itemsPerPage: 50,          // 페이지네이션 개수 (기본 50)
                rowNum: false,             // 행 번호 표시 여부 추가
                method: 'post',            // method 타입
                dynamicHeight: true,       // 그리드 높이 동적 설정
                scrollY: true,             // y축 스크롤 설정
            };

            const finalConfig = { ...defaultConfig, ...config };
            const { gridElementId } = finalConfig;

            if (!gridElementId) {
                reject('gridElementId가 필요합니다.');
                return;
            }

            // 그리드 초기 설정 저장
            this.initialConfigs[gridElementId] = finalConfig;

            // 그리드 테이블 및 그리드 페이지네이션 DOM 요소 생성 또는 확인
            this._ensureGridElements(gridElementId, finalConfig.paginationOption);

            // 그리드 생성/업데이트
            this._loadGridData(finalConfig)
                .then(resolve)
                .catch(reject);
        });
    },

    /**
     * 처음 페이지 렌더링 시 테이블 및 페이지네이션 요소가 존재하지 않는지 확인 후 생성
     * @param {String} gridElementId - Grid의 ID를 기반으로 DOM 요소 생성
     * @param {boolean} paginationOption - Grid 페이지네이션 활성화 여부
     */
    _ensureGridElements: function (gridElementId, paginationOption) {
        const tableId = `${gridElementId}Table`;
        const paginationId = `${gridElementId}Pagination`;

        const parentElement = document.getElementById(gridElementId); // 부모 요소 가져오기
        if (!parentElement) {
            console.error(`부모 엘리먼트 "${gridElementId}"이(가) 존재하지 않습니다.`);
            return;
        }

        // 그리드 테이블 요소가 존재하지 않을 경우 생성
        if (!document.getElementById(tableId)) {
            const tableElement = document.createElement('div');
            tableElement.id = tableId;
            parentElement.appendChild(tableElement);
        }

        // 페이지네이션 요소가 존재하지 않고, 페이지네이션 옵션이 활성화된 경우 생성
        if (!document.getElementById(paginationId) && paginationOption) {
            const paginationElement = document.createElement('div');
            paginationElement.id = paginationId;
            paginationElement.className = 'tui-pagination';
            parentElement.appendChild(paginationElement);
        }
    },

    /**
     * 페이지네이션 초기화
     * @param {String} gridElementId - Grid의 ID를 기반으로 Pagination 초기화
     * @param {Object} paginationOptions - 페이지네이션 설정 값({ totalItems, itemsPerPage, currentPage })
     */
    _initPagination: function (gridElementId, paginationOptions) {
        const { totalItems, itemsPerPage, currentPage } = paginationOptions;
        const paginationId = `${gridElementId}Pagination`;
        const paginationElement = document.getElementById(paginationId);

        if (!paginationElement) {
            console.error(`Pagination 엘리먼트 "${paginationId}"가 존재하지 않습니다.`);
            return;
        }

        let pagination = this.initialConfigs[gridElementId]?.pagination;
        if (!pagination) {
            pagination = new tui.Pagination(paginationElement, {
                totalItems: totalItems || 0,            // 전체 아이템 수
                itemsPerPage: itemsPerPage || 10,       // 한 페이지에 보일 아이템 수
                page: currentPage || 1,                 // 초기 페이지
                centerAlign: true,                      // 페이지네이션 중앙 정렬
            });

            this.initialConfigs[gridElementId].pagination = pagination;

            pagination.on('afterMove', (event) => {
                const newPage = event.page;
                this._onPageChange(gridElementId, newPage);
            });
        } else {

        }
    },


    /**
     * 페이지 변경 시 데이터를 로드하고 Grid 업데이트
     * @param {String} gridElementId - Grid의 ID
     * @param {Number} currentPage - 현재 페이지 번호
     */
    _onPageChange: function (gridElementId, currentPage) {
        const initialConfig = this.initialConfigs[gridElementId];

        if (!initialConfig) {
            console.error(`"${gridElementId}"에 대한 초기 설정이 존재하지 않습니다.`);
            return;
        }

        const data = initialConfig.data;
        // console.log(data);

        const updatedConfig = {
            ...initialConfig,
            data: data,
            page: currentPage,
        };

        // 데이터를 로드하고 Grid 업데이트
        this._loadGridData(updatedConfig)
            .then((result) => {
                // console.log(`"${gridElementId}" 그리드가 페이지 ${currentPage}로 업데이트되었습니다.`);
            })
            .catch((err) => {
                console.error(`"${gridElementId}" 페이지 업데이트 중 오류 발생:`, err);
            });
    },

    /**
     * Grid 데이터를 업데이트 (특정 gridElementId에 대해 초기 설정 유지하며 일부 검색 조건 변경)
     * @param {String} gridElementId - 업데이트할 grid의 DOM ID
     * @param {String|Object} data - 추가로 서버로 전달할 데이터
     * @returns {Promise<any>}
     */
    updateGrid: function (gridElementId, data= "") {
        return new Promise((resolve, reject) => {

            const initialConfig = this.initialConfigs[gridElementId];
            if (!initialConfig) {
                reject(`gridElementId "${gridElementId}"에 대한 초기 설정이 존재하지 않습니다.`);
                return;
            }

            // 현재 그리드의 페이지네이션 상태 추적
            const pagination = this.initialConfigs[gridElementId]?.pagination;
            let currentPage = 1;

            if (pagination) {
                currentPage = pagination.getCurrentPage();
            }

            // 초기 설정 업데이트
            this.initialConfigs[gridElementId] = {
                ...initialConfig,
                data,
                page: currentPage
            };

            const updatedConfig = {
                ...initialConfig,
                data,
                page: currentPage
            };

            // 그리드 업데이트
            this._loadGridData(updatedConfig)
                .then((result) => {
                    const totalItems = result.total;
                    if (pagination) {
                        pagination.reset(totalItems);
                        pagination.movePageTo(currentPage);
                    }

                    resolve(result);
                })
                .catch(reject);
        });
    },

    /**
     * 내부적으로 AJAX 요청을 통해 데이터를 로드하고 그리드 갱신
     * @param {Object} config - 서버 요청 및 Grid 갱신 설정
     * @returns {Promise<any>}
     */
    _loadGridData: function (config) {
        return new Promise((resolve, reject) => {

            let pagination = config.paginationOption;
            let currentPage;
            let itemsPerPage;
            let start;
            let data = "";
            if (config.method.toLowerCase() == "post"){
                data = { data : config.data };
            }

            // 페이지네이션 옵션이 true 일 때 요청 파라미터 추가
            if (pagination) {
                currentPage = config.page || 1; // 현재 페이지 (기본값: 1)
                itemsPerPage = config.itemsPerPage || 10; // 페이지당 항목 수 (기본값: 10)
                start = (currentPage - 1) * itemsPerPage;

                data.start = start;
                data.page = itemsPerPage;
            }

            $.ajax({
                url: config.url,
                method: config.method,
                contentType: 'application/json',
                data: JSON.stringify(data),
                dataType: 'JSON',
                success: (res) => {
                    const rowData = res.rows;
                    const total = res.total;

                    // 전체 개수 업데이트
                    if (total !== undefined && config.tableCountId !== "") {
                        const tableCountElement = document.getElementById(config.tableCountId);
                        if (tableCountElement) {
                            tableCountElement.textContent = `전체 개수 : ${total}개`;
                        }

                        if (pagination) {
                            this._initPagination(config.gridElementId, {
                                totalItems: total,
                                itemsPerPage,
                                currentPage,
                            });
                        }
                    }

                    // 그리드 설정
                    const gridElement = document.getElementById(`${config.gridElementId}Table`);
                    if (gridElement) {
                        if (gridElement.gridInstance) {
                            // 그리드 업데이트
                            const allRowElements = document.querySelectorAll(`#${config.gridElementId}Table [data-row-key]`);
                            allRowElements.forEach((cell) => {
                                cell.classList.remove('tui-selected-row');
                            });
                            gridElement.gridInstance.resetData(rowData);
                        } else {
                            // 그리드 초기화
                            const gridOptions = {
                                el: gridElement,
                                scrollX: false,
                                scrollY: config.scrollY,
                                data: rowData,
                                columns: config.columns,
                                rowKey: 'id',
                            };

                            if (config.checkbox || config.rowNum) {
                                gridOptions.rowHeaders = [];
                                if (config.checkbox) {
                                    gridOptions.rowHeaders.push('checkbox');
                                }
                                if (config.rowNum) {
                                    gridOptions.rowHeaders.push('rowNum');
                                }
                            }

                            if (Array.isArray(config.complexColumns)) {
                                gridOptions.header = {
                                    height: 65,
                                    complexColumns: config.complexColumns,
                                };
                            }

                            gridElement.gridInstance = new tui.Grid(gridOptions);

                            gridElement.gridInstance.on('check', (ev) => {
                                const rowElements = document.querySelectorAll(`#${config.gridElementId}Table [data-row-key="${ev.rowKey}"]`);
                                rowElements.forEach((cell) => {
                                    cell.classList.add('tui-selected-row');
                                });
                            });

                            gridElement.gridInstance.on('uncheck', (ev) => {
                                const rowElements = document.querySelectorAll(`#${config.gridElementId}Table [data-row-key="${ev.rowKey}"]`);
                                rowElements.forEach((cell) => {
                                    cell.classList.remove('tui-selected-row');
                                });
                            });

                            gridElement.gridInstance.on('checkAll', () => {
                                const allRowElements = document.querySelectorAll(`#${config.gridElementId}Table [data-row-key]`);
                                allRowElements.forEach((cell) => {
                                    cell.classList.add('tui-selected-row');
                                });
                            });

                            gridElement.gridInstance.on('uncheckAll', () => {
                                const allRowElements = document.querySelectorAll(`#${config.gridElementId}Table [data-row-key]`);
                                allRowElements.forEach((cell) => {
                                    cell.classList.remove('tui-selected-row');
                                });
                            });
                        }

                        if(config.dynamicHeight) {
                            this._setGridHeight(config.gridElementId);
                        }

                        resolve({
                            instance: gridElement.gridInstance,
                            rows: rowData,
                            total: total,
                        });
                    } else {
                        reject('그리드를 렌더링할 요소를 찾을 수 없습니다.');
                    }
                },
                error: () => {
                    alert2('알림', '데이터를 불러오는 중 오류가 발생했습니다.', 'info', '확인');
                    reject('AJAX 요청 에러');
                },
            });
        });
    },

    /**
     * 브라우저 창 크기에 따라 그리드 높이 계산 (window 함수를 사용해서 창 크기가 변경될 떄마다 함수 호출)
     * @param {String} gridElementId - Grid의 ID
     */
    _setGridHeight: function (gridElementId) {
        const gridElement = document.getElementById(`${gridElementId}Table`);
        if (gridElement && gridElement.gridInstance) {
            const gridTop = gridElement.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;
            const remainingHeight = windowHeight - gridTop - 80;
            const minHeight = 200;
            const gridHeight = Math.max(remainingHeight, minHeight);

            gridElement.style.height = `${gridHeight}px`;
            gridElement.gridInstance.setHeight(gridHeight);
        }
    },

    /**
     * 특정 그리드의 모든 행 데이터 반환
     * @param {String} gridElementId - 데이터를 가져올 grid의 DOM ID
     * @returns {Array<Object>|null} - 모든 행 데이터 배열 (그리드 인스턴스 없거나 데이터 없으면 null 반환)
     */
    getData: function (gridElementId) {
        const gridElement = document.getElementById(`${gridElementId}Table`);

        if (!gridElement || !gridElement.gridInstance) {
            console.error(`gridElementId "${gridElementId}"에 해당하는 그리드가 존재하지 않습니다.`);
            return null;
        }

        const gridInstance = gridElement.gridInstance;
        const rowCount = gridInstance.getRowCount();
        const allRows = [];

        for (let i = 0; i < rowCount; i++) {
            allRows.push(gridInstance.getRow(i));
        }

        return allRows.length > 0 ? allRows : null;
    },

    /**
     * 특정 그리드에서 선택된 체크박스 Row 정보를 반환
     * @param {String} gridElementId - 데이터를 가져올 grid의 DOM ID
     * @returns {Array<Object>} - 선택된 행 데이터 배열
     */
    getCheckedRows: function (gridElementId) {
        const gridElement = document.getElementById(`${gridElementId}Table`);
        if (gridElement && gridElement.gridInstance) {
            return gridElement.gridInstance.getCheckedRows();
        } else {
            console.error(`gridElementId "${gridElementId}"에 해당하는 그리드가 존재하지 않습니다.`);
            return [];
        }
    },

    /**
     * 특정 그리드에서 선택된 라디오 Row 정보를 반환
     * @param {String} gridElementId - 데이터를 가져올 grid의 DOM ID
     * @param {String} rowIndex - 선택된 row의 index
     * @returns {Object|null} - 선택된 행 데이터 배열
     */
    getRadioRows: function (gridElementId, rowIndex) {
        const gridElement = document.getElementById(`${gridElementId}Table`);

        if (!gridElement || !gridElement.gridInstance) {
            console.error(`gridElementId "${gridElementId}"에 해당하는 그리드가 존재하지 않습니다.`);
            return null;
        }

        const gridInstance = gridElement.gridInstance;
        const rowKey = gridInstance.getIndexOfRow(rowIndex);

        if (rowKey === -1 || rowKey === undefined) {
            console.error(`rowIndex ${rowIndex}에 해당하는 행을 찾을 수 없습니다.`);
            return null;
        }

        return gridInstance.getRow(rowKey);
    },


    /**
     * 특정 그리드에서 수정된 행 데이터 반환
     * @param {String} gridElementId - 데이터를 가져올 grid의 DOM ID
     * @returns {Object} - { createdRows: [], updatedRows: [], deletedRows: [] }
     */
    getModifiedRows: function (gridElementId) {
        const gridElement = document.getElementById(`${gridElementId}Table`);

        if (!gridElement || !gridElement.gridInstance) {
            console.error(`gridElementId "${gridElementId}"에 해당하는 그리드가 존재하지 않습니다.`);
            return { createdRows: [], updatedRows: [] };
        }

        const gridInstance = gridElement.gridInstance;
        return gridInstance.getModifiedRows();
    },

    /**
     * 클릭된 요소 기준으로 그리드의 Row 데이터를 반환
     * @param {String} gridElementId - 데이터를 가져올 grid의 DOM ID
     * @param {Event|HTMLElement} target - 클릭 이벤트 객체나 기준이 될 HTMLElement
     * @returns {Object|null} - 관련 행의 데이터 (존재하지 않을 경우 null 반환)
     */
    getRowDataByEvent: function (gridElementId, target) {
        const gridElement = document.getElementById(`${gridElementId}Table`);

        if (!gridElement || !gridElement.gridInstance) {
            console.error(`gridElementId "${gridElementId}"에 해당하는 그리드가 존재하지 않습니다.`);
            return null;
        }

        const gridInstance = gridElement.gridInstance;
        let targetElement;

        if (target && 'target' in target) {
            targetElement = target.target;
        } else if (target instanceof HTMLElement) {
            targetElement = target;
        } else {
            console.error('target은 jQuery Event 객체나 HTMLElement여야 합니다.');
            return null;
        }

        const $target = $(targetElement);
        const rowIndex = $target.closest('td').attr('data-row-key');
        /*
        const rowKey = gridInstance.getIndexOfRow(rowIndex);

        if (rowKey === -1) {
            console.error('해당 행의 rowKey를 찾을 수 없습니다.');
            return null;
        }


        return gridInstance.getRow(rowKey);
        */
        return gridInstance.getRow(parseInt(rowIndex, 10));
    },

};

// 브라우저 창 크기 변경 시 높이 재설정
$(window).on('resize', function () {
    for (const gridElementId in gridManager.initialConfigs) {
        if(gridManager.initialConfigs[gridElementId].dynamicHeight) {
            gridManager._setGridHeight(gridElementId);
        }
    }
});