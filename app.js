/**
 * CN Lab Programs
 * - Single click / tap = Copy code to clipboard
 * - Double click / tap = Open in Code Editor mode
 * - Top-Right Copy button & Top-Left View button on each card (mobile friendly)
 * - Aligned responsive grid
 */

document.addEventListener('DOMContentLoaded', () => {
    const fileGrid = document.getElementById('fileGrid');
    const searchInput = document.getElementById('searchInput');
    const clearSearch = document.getElementById('clearSearch');
    const noResults = document.getElementById('noResults');
    const toast = document.getElementById('toast');
    const toastMsg = document.getElementById('toastMsg');
    const copyHelper = document.getElementById('copyHelper');

    // Code Editor Modal Elements
    const editorModal = document.getElementById('editorModal');
    const editorFileName = document.getElementById('editorFileName');
    const editorFileMeta = document.getElementById('editorFileMeta');
    const editorLineNumbers = document.getElementById('editorLineNumbers');
    const editorCodeBlock = document.getElementById('editorCodeBlock');
    const editorCopyBtn = document.getElementById('editorCopyBtn');
    const editorCloseBtn = document.getElementById('editorCloseBtn');

    let currentOpenProgram = null;
    let toastTimer = null;
    let clickTimeout = null;
    let lastTouchTime = 0;

    // Format bytes to KB/B
    function formatSize(bytes) {
        if (!bytes) return '0 B';
        if (bytes < 1024) return bytes + ' B';
        return (bytes / 1024).toFixed(1) + ' KB';
    }

    // High-resolution Windows Java File Icon SVG
    function getJavaIconSVG() {
        return `
            <svg class="java-icon-svg" viewBox="0 0 54 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                <!-- Drop shadow base -->
                <path d="M5 4C5 2.34315 6.34315 1 8 1H36L49 14V60C49 61.6569 47.6569 63 46 63H8C6.34315 63 5 61.6569 5 60V4Z" 
                      fill="#FFFFFF" stroke="#CBD5E1" stroke-width="1.4"/>
                
                <!-- Folded Corner -->
                <path d="M36 1V12.5C36 13.3284 36.6716 14 37.5 14H49" 
                      fill="#F1F5F9" stroke="#CBD5E1" stroke-width="1.2"/>

                <!-- Subtle File Texture Lines -->
                <rect x="11" y="21" width="22" height="2.2" rx="1" fill="#E2E8F0"/>
                <rect x="11" y="26.5" width="16" height="2.2" rx="1" fill="#E2E8F0"/>

                <!-- Java Coffee Cup Emblem -->
                <g transform="translate(13, 31) scale(0.96)">
                    <!-- Steaming Aroma Curves -->
                    <path d="M12 2C13 4.5 11 7 12 8.5" stroke="#F97316" stroke-width="1.6" stroke-linecap="round"/>
                    <path d="M17 0C18 3.5 15.5 6 17 8" stroke="#EA580C" stroke-width="1.6" stroke-linecap="round"/>
                    
                    <!-- Cup Silhouette -->
                    <path d="M5 9.5H21V16C21 18.5 18.5 20.5 13 20.5C7.5 20.5 5 18.5 5 16V9.5Z" fill="url(#cupGrad)"/>
                    <!-- Cup Handle -->
                    <path d="M21 11.5H23C24.1 11.5 25 12.4 25 13.5C25 14.6 24.1 15.5 23 15.5H21" stroke="#F97316" stroke-width="1.7" stroke-linecap="round"/>
                    <!-- Saucer Base -->
                    <path d="M3 21.5H23" stroke="#DC2626" stroke-width="2" stroke-linecap="round"/>
                </g>

                <!-- Distinct JAVA Red Badge -->
                <rect x="9" y="53.5" width="36" height="7.5" rx="3.75" fill="#E11D48"/>
                <text x="27" y="59.2" fill="#FFFFFF" font-size="5.2" font-family="'Inter', -apple-system, sans-serif" font-weight="800" text-anchor="middle" letter-spacing="0.5">JAVA</text>

                <!-- Gradient Definition -->
                <defs>
                    <linearGradient id="cupGrad" x1="5" y1="9.5" x2="21" y2="20.5" gradientUnits="userSpaceOnUse">
                        <stop stop-color="#F97316"/>
                        <stop offset="1" stop-color="#DC2626"/>
                    </linearGradient>
                </defs>
            </svg>
        `;
    }

    // Java Syntax Highlighter
    function highlightJava(code) {
        if (window.Prism && Prism.languages.java) {
            try {
                return Prism.highlight(code, Prism.languages.java, 'java');
            } catch (e) {
                // fallback below
            }
        }

        let html = code
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;');

        html = html.replace(/(\/\/[^\n]*)/g, '<span class="token comment">$1</span>');
        html = html.replace(/("(?:\\.|[^"\\])*")/g, '<span class="token string">$1</span>');

        const keywords = /\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)\b/g;
        html = html.replace(keywords, '<span class="token keyword">$1</span>');

        const classes = /\b(String|Socket|ServerSocket|DatagramSocket|DatagramPacket|InetAddress|Scanner|BufferedReader|PrintWriter|InputStreamReader|OutputStream|InputStream|FileInputStream|FileOutputStream|BufferedInputStream|BufferedOutputStream|Random|Arrays|System|Integer|File|Exception|IOException)\b/g;
        html = html.replace(classes, '<span class="token class-name">$1</span>');

        html = html.replace(/\b(\d+)\b/g, '<span class="token number">$1</span>');
        html = html.replace(/\b([a-zA-Z_$][a-zA-Z0-9_$]*)(?=\s*\()/g, '<span class="token function">$1</span>');

        return html;
    }

    // Render Grid
    function render(files) {
        fileGrid.innerHTML = '';

        if (!files || files.length === 0) {
            noResults.style.display = 'block';
            fileGrid.style.display = 'none';
            return;
        }

        noResults.style.display = 'none';
        fileGrid.style.display = 'grid';

        files.forEach(file => {
            const card = document.createElement('div');
            card.className = 'file-card';
            card.setAttribute('tabindex', '0');
            card.setAttribute('role', 'button');
            card.setAttribute('title', `${file.name}\n• Click: Copy code\n• Double click or tap view: Open Editor`);

            card.innerHTML = `
                <!-- Action Buttons: Top-Left View & Top-Right Copy -->
                <div class="card-actions">
                    <button class="card-btn view-btn" title="View in code editor" aria-label="View ${file.name}">
                        <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="16 18 22 12 16 6"></polyline>
                            <polyline points="8 6 2 12 8 18"></polyline>
                        </svg>
                    </button>
                    <button class="card-btn copy-btn" title="Copy code" aria-label="Copy ${file.name}">
                        <svg viewBox="0 0 24 24" width="13" height="13" fill="currentColor">
                            <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
                        </svg>
                    </button>
                </div>

                <!-- Medium-Large Java Icon -->
                <div class="icon-wrap">
                    ${getJavaIconSVG()}
                    <div class="copied-pill">
                        <svg viewBox="0 0 24 24" width="12" height="12" fill="currentColor">
                            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                        </svg>
                        <span>Copied!</span>
                    </div>
                </div>

                <!-- File Info (Centered & Aligned) -->
                <div class="file-info">
                    <div class="file-title">${file.name}</div>
                    <div class="file-lines-sub">${file.lines} lines</div>
                </div>
            `;

            const copyBtn = card.querySelector('.copy-btn');
            const viewBtn = card.querySelector('.view-btn');

            // Copy button click
            copyBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                copyCode(file, card, copyBtn);
            });

            // View button click
            viewBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                openCodeEditor(file);
            });

            // Card click / double-click handling
            card.addEventListener('click', (e) => {
                if (e.target.closest('.card-btn')) return;

                if (clickTimeout) {
                    // Double Click Detected!
                    clearTimeout(clickTimeout);
                    clickTimeout = null;
                    openCodeEditor(file);
                } else {
                    // Start timer for single click
                    clickTimeout = setTimeout(() => {
                        clickTimeout = null;
                        copyCode(file, card, copyBtn);
                    }, 240);
                }
            });

            // Mobile double-tap support via touchstart
            card.addEventListener('touchend', (e) => {
                if (e.target.closest('.card-btn')) return;
                const currentTime = new Date().getTime();
                const tapLength = currentTime - lastTouchTime;
                if (tapLength < 280 && tapLength > 0) {
                    // Double Tap on Mobile!
                    e.preventDefault();
                    if (clickTimeout) {
                        clearTimeout(clickTimeout);
                        clickTimeout = null;
                    }
                    openCodeEditor(file);
                }
                lastTouchTime = currentTime;
            });

            // Keyboard navigation
            card.addEventListener('keydown', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    openCodeEditor(file);
                } else if (e.key === ' ') {
                    e.preventDefault();
                    copyCode(file, card, copyBtn);
                }
            });

            fileGrid.appendChild(card);
        });
    }

    // Open Code Editor Mode
    function openCodeEditor(file) {
        currentOpenProgram = file;
        editorFileName.textContent = file.name;
        editorFileMeta.textContent = `${file.lines} lines • ${formatSize(file.size)}`;

        // Build Line Numbers
        const linesCount = file.lines || file.code.split('\n').length;
        let lineNumsHtml = '';
        for (let i = 1; i <= linesCount; i++) {
            lineNumsHtml += `<div>${i}</div>`;
        }
        editorLineNumbers.innerHTML = lineNumsHtml;

        // Highlight Code
        editorCodeBlock.innerHTML = highlightJava(file.code);

        // Show Modal
        editorModal.style.display = 'flex';
        document.body.style.overflow = 'hidden';

        // Synchronize Scroll
        const codeContainer = document.querySelector('.code-container');
        codeContainer.scrollTop = 0;
        codeContainer.scrollLeft = 0;
        codeContainer.onscroll = () => {
            editorLineNumbers.scrollTop = codeContainer.scrollTop;
        };
    }

    // Close Code Editor Mode
    function closeCodeEditor() {
        editorModal.style.display = 'none';
        document.body.style.overflow = '';
        currentOpenProgram = null;
    }

    // Copy to Clipboard Action
    async function copyCode(file, cardEl, copyBtnEl) {
        let success = false;

        if (navigator.clipboard && window.isSecureContext) {
            try {
                await navigator.clipboard.writeText(file.code);
                success = true;
            } catch (err) {
                // fallback below
            }
        }

        if (!success) {
            try {
                copyHelper.value = file.code;
                copyHelper.select();
                copyHelper.setSelectionRange(0, 999999);
                success = document.execCommand('copy');
                copyHelper.blur();
            } catch (err) {
                success = false;
            }
        }

        if (success) {
            document.querySelectorAll('.file-card').forEach(c => c.classList.remove('selected'));
            if (cardEl) {
                cardEl.classList.add('selected');
                cardEl.classList.add('copied');
                setTimeout(() => {
                    cardEl.classList.remove('copied');
                }, 1300);
            }

            if (copyBtnEl) {
                copyBtnEl.classList.add('copied-state');
                setTimeout(() => {
                    copyBtnEl.classList.remove('copied-state');
                }, 1300);
            }

            showToast(`Copied ${file.name} to clipboard!`);
        } else {
            showToast(`Failed to copy ${file.name}`);
        }
    }

    // Toast Notification
    function showToast(message) {
        toastMsg.textContent = message;
        toast.classList.add('show');

        if (toastTimer) clearTimeout(toastTimer);
        toastTimer = setTimeout(() => {
            toast.classList.remove('show');
        }, 2200);
    }

    // Editor Modal Action Listeners
    editorCloseBtn.addEventListener('click', closeCodeEditor);

    editorCopyBtn.addEventListener('click', async () => {
        if (currentOpenProgram) {
            await copyCode(currentOpenProgram, null, null);
            editorCopyBtn.innerHTML = `
                <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                    <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
                </svg>
                <span>Copied!</span>
            `;
            setTimeout(() => {
                editorCopyBtn.innerHTML = `
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor">
                        <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
                    </svg>
                    <span>Copy Code</span>
                `;
            }, 1500);
        }
    });

    editorModal.addEventListener('click', (e) => {
        if (e.target === editorModal) {
            closeCodeEditor();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && editorModal.style.display === 'flex') {
            closeCodeEditor();
        }
    });

    // Search filter
    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim().toLowerCase();
        clearSearch.style.display = query ? 'block' : 'none';

        const filtered = PROGRAMS.filter(p => {
            return p.name.toLowerCase().includes(query) || p.code.toLowerCase().includes(query);
        });
        render(filtered);
    });

    clearSearch.addEventListener('click', () => {
        searchInput.value = '';
        clearSearch.style.display = 'none';
        searchInput.focus();
        render(PROGRAMS);
    });

    // Initial render
    render(PROGRAMS);
});
