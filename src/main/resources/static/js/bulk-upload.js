(() => {
    const BULK_API_URL = '/api/admin/student/image';

    const fabBulk          = document.getElementById('fabBulk');
    const bulkOverlay      = document.getElementById('bulkOverlay');
    const bulkClose        = document.getElementById('bulkClose');
    const bulkDropzone     = document.getElementById('bulkDropzone');
    const bulkFileInput    = document.getElementById('bulkFileInput');
    const bulkFileList     = document.getElementById('bulkFileList');
    const bulkFileCounter  = document.getElementById('bulkFileCounter');
    const bulkFileCount    = document.getElementById('bulkFileCount');
    const bulkUploadBtn    = document.getElementById('bulkUploadBtn');
    const bulkProgressWrap = document.getElementById('bulkProgressWrap');
    const bulkPbarFill     = document.getElementById('bulkPbarFill');
    const bulkPbarLabel    = document.getElementById('bulkPbarLabel');
    const bulkUploadView   = document.getElementById('bulkUploadView');
    const bulkResultView   = document.getElementById('bulkResultView');
    const bulkResultBody   = document.getElementById('bulkResultBody');
    const bulkResetBtn     = document.getElementById('bulkResetBtn');
    const bulkPanel        = document.getElementById('bulkPanel');

    let fileEntries = [];

    /* ── Open / Close ── */
    fabBulk.addEventListener('click', openPanel);

    bulkClose.addEventListener('click', () => {
        closePanel();
        // refresh image request list on close
        if (typeof loadImageRequest === 'function') loadImageRequest();
    });

    bulkOverlay.addEventListener('click', e => {
        if (e.target === bulkOverlay) {
            closePanel();
            if (typeof loadImageRequest === 'function') loadImageRequest();
        }
    });

    function openPanel() {
        bulkOverlay.classList.add('open');
        document.body.style.overflow = 'hidden';
    }

    function closePanel() {
        bulkOverlay.classList.remove('open');
        document.body.style.overflow = '';
    }

    /* ── Drag & Drop ── */
    bulkDropzone.addEventListener('dragover', e => {
        e.preventDefault();
        bulkDropzone.classList.add('drag-over');
    });
    ['dragleave', 'drop'].forEach(ev =>
        bulkDropzone.addEventListener(ev, () => bulkDropzone.classList.remove('drag-over'))
    );
    bulkDropzone.addEventListener('drop', e => {
        e.preventDefault();
        addFiles([...e.dataTransfer.files]);
    });
    bulkFileInput.addEventListener('change', () => {
        addFiles([...bulkFileInput.files]);
        bulkFileInput.value = '';
    });

    /* ── Add Files ── */
    function addFiles(incoming) {
        incoming.forEach(f => {
            if (!['image/png', 'image/jpeg', 'image/jpg'].includes(f.type)) {
                showSnackbar(`Skipped "${f.name}" — invalid type`, 'warning');
                return;
            }
            const idx = fileEntries.findIndex(e => e.customName === f.name);
            if (idx !== -1) {
                fileEntries[idx] = { file: f, customName: f.name };
                showSnackbar(`Replaced "${f.name}"`, 'success');
            } else {
                fileEntries.push({ file: f, customName: f.name });
            }
        });
        render();
    }

    /* ── Render Chips ── */
    function render() {
        bulkFileList.innerHTML = '';

        const nameCounts = {};
        fileEntries.forEach(e => { nameCounts[e.customName] = (nameCounts[e.customName] || 0) + 1; });

        fileEntries.forEach((entry, i) => {
            const url   = URL.createObjectURL(entry.file);
            const isDup = nameCounts[entry.customName] > 1;
            const chip  = document.createElement('div');
            chip.className = 'bulk-file-chip';
            chip.innerHTML = `
                <img class="bulk-chip-thumb" src="${url}" />
                <div class="bulk-chip-info">
                    <div class="bulk-chip-name-wrap">
                        <span class="bulk-chip-name" id="cn-${i}">${escHtml(entry.customName)}</span>
                        <input class="bulk-chip-name-input" id="ci-${i}" value="${escAttr(entry.customName)}" spellcheck="false" />
                        <button class="bulk-chip-edit-btn" data-action="edit" data-i="${i}" title="Rename">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                            </svg>
                        </button>
                        <button class="bulk-chip-save-btn" id="cs-${i}" data-action="save" data-i="${i}">Save</button>
                        <span class="bulk-chip-dup ${isDup ? 'show' : ''}">duplicate</span>
                    </div>
                    <div class="bulk-chip-size">${fmtSize(entry.file.size)}</div>
                </div>
                <button class="bulk-chip-rm" data-action="remove" data-i="${i}" title="Remove">
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round">
                        <line x1="18" y1="6" x2="6" y2="18"/>
                        <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                </button>`;
            bulkFileList.appendChild(chip);
        });

        bulkFileList.querySelectorAll('[data-action]').forEach(btn =>
            btn.addEventListener('click', handleChipAction)
        );

        fileEntries.forEach((_, i) => {
            const inp = document.getElementById(`ci-${i}`);
            if (inp) {
                inp.addEventListener('keydown', e => {
                    if (e.key === 'Enter') saveRename(i);
                    if (e.key === 'Escape') cancelRename(i);
                });
            }
        });

        bulkFileCount.textContent = fileEntries.length;
        bulkFileCounter.style.display = fileEntries.length > 0 ? 'block' : 'none';
        bulkUploadBtn.disabled = fileEntries.length === 0;
    }

    function handleChipAction(e) {
        const action = e.currentTarget.dataset.action;
        const i      = +e.currentTarget.dataset.i;
        if (action === 'edit')   startRename(i);
        if (action === 'save')   saveRename(i);
        if (action === 'remove') { fileEntries.splice(i, 1); render(); }
    }

    function startRename(i) {
        document.getElementById(`cn-${i}`)?.classList.add('hide');
        document.getElementById(`ci-${i}`)?.classList.add('editing');
        document.getElementById(`cs-${i}`)?.classList.add('show');
        const inp = document.getElementById(`ci-${i}`);
        if (inp) { inp.focus(); inp.select(); }
    }

    function saveRename(i) {
        const inp = document.getElementById(`ci-${i}`);
        if (!inp) return;
        let name = inp.value.trim();
        if (!name) { cancelRename(i); return; }
        if (!name.includes('.')) {
            const ext = fileEntries[i].customName.includes('.')
                ? fileEntries[i].customName.slice(fileEntries[i].customName.lastIndexOf('.'))
                : '.jpg';
            name += ext;
        }
        fileEntries[i].customName = name;
        render();
    }

    function cancelRename(i) {
        document.getElementById(`cn-${i}`)?.classList.remove('hide');
        document.getElementById(`ci-${i}`)?.classList.remove('editing');
        document.getElementById(`cs-${i}`)?.classList.remove('show');
    }

    /* ── Upload ── */
    bulkUploadBtn.addEventListener('click', async () => {
        if (!fileEntries.length) return;

        // Block duplicate names
        const counts = {};
        fileEntries.forEach(e => { counts[e.customName] = (counts[e.customName] || 0) + 1; });
        if (Object.values(counts).some(c => c > 1)) {
            showSnackbar('Duplicate file names found. Please rename them first.', 'warning');
            return;
        }

        const snapshot = fileEntries.map(e => ({
            originalFile: e.file,
            customName:   e.customName,
            enrollmentNo: e.customName.includes('.')
                ? e.customName.slice(0, e.customName.lastIndexOf('.'))
                : e.customName,
        }));

        // Show progress, hide rest of upload view except progress
        bulkDropzone.style.display       = 'none';
        bulkFileList.style.display       = 'none';
        bulkFileCounter.style.display    = 'none';
        bulkUploadBtn.style.display      = 'none';
        bulkProgressWrap.style.display   = 'block';
        setProgress(15, 'Preparing…');

        const fd = new FormData();
        snapshot.forEach(s =>
            fd.append('images', new File([s.originalFile], s.customName, { type: s.originalFile.type }))
        );

        try {
            setProgress(40, 'Sending to server…');
            const res = await fetch(BULK_API_URL, { method: 'POST', body: fd });
            setProgress(85, 'Processing…');

            let data = {};
            try { data = await res.json(); } catch { data = { responses: [] }; }

            setProgress(100, 'Done!');

            setTimeout(() => {
                bulkProgressWrap.style.display = 'none';
                setProgress(0, '');

                if (res.ok) {
                    const br = data.responses || [];
                    const perFile = snapshot.map(s => {
                        const match = br.find(r => r.enrollmentNo?.toLowerCase() === s.enrollmentNo.toLowerCase());
                        return { enrollmentNo: s.enrollmentNo, status: match ? match.status : 'SUCCESS' };
                    });
                    const ok = perFile.filter(r => r.status === 'SUCCESS').length;
                    showResultView(perFile);
                    showSnackbar(`${ok}/${snapshot.length} image${snapshot.length > 1 ? 's' : ''} uploaded successfully`, 'success');
                } else {
                    const msg = data?.message || data?.response || `HTTP ${res.status}`;
                    showResultView(snapshot.map(s => ({ enrollmentNo: s.enrollmentNo, status: msg.toUpperCase() })));
                    showSnackbar(msg, 'error');
                }
            }, 600);

        } catch (err) {
            setProgress(100, 'Error');
            setTimeout(() => {
                bulkProgressWrap.style.display = 'none';
                setProgress(0, '');
                showResultView(snapshot.map(s => ({ enrollmentNo: s.enrollmentNo, status: 'NETWORK ERROR' })));
                showSnackbar(err.message, 'error');
            }, 400);
        }
    });

    function setProgress(pct, label) {
        bulkPbarFill.style.width  = pct + '%';
        bulkPbarLabel.textContent = label;
    }

    /* ── Show Result View ── */
    function showResultView(results) {
        const ok   = results.filter(r => r.status === 'SUCCESS').length;
        const fail = results.length - ok;
        document.getElementById('bulkSumTotal').textContent = results.length;
        document.getElementById('bulkSumOk').textContent    = ok;
        document.getElementById('bulkSumFail').textContent  = fail;

        bulkResultBody.innerHTML = '';
        results.forEach((r, i) => {
            const isOk = r.status === 'SUCCESS';
            const tr   = document.createElement('tr');
            tr.innerHTML = `
                <td>${i + 1}</td>
                <td><div class="bulk-enno">${escHtml(r.enrollmentNo)}</div></td>
                <td>
                    <span class="bulk-badge ${isOk ? 'bulk-badge-ok' : 'bulk-badge-fail'}">${escHtml(r.status)}</span>
                    <div class="bulk-detail-txt">${detailOf(r.status)}</div>
                </td>`;
            bulkResultBody.appendChild(tr);
        });

        bulkUploadView.style.display = 'none';
        bulkResultView.style.display = 'block';
        bulkPanel.scrollTo({ top: 0, behavior: 'smooth' });
    }

    const DETAILS = {
        'SUCCESS':            'Image uploaded successfully and is pending approval.',
        'USER NOT FOUND':     'No student found with this enrollment no',
        'FILE SIZE EXCEEDED': 'File exceeds the 5 MB limit',
        'INVALID FILE TYPE':  'Only JPG, JPEG, PNG are allowed',
        'INVALID FILE NAME':  'Filename missing or has no extension',
        'NETWORK ERROR':      'Could not reach the server',
        'INTERNAL ERROR':     'Unexpected server-side error',
    };
    function detailOf(s) { return DETAILS[s] || s; }

    /* ── Reset ── */
    bulkResetBtn.addEventListener('click', () => {
        fileEntries = [];
        bulkResultBody.innerHTML      = '';
        bulkResultView.style.display  = 'none';
        bulkUploadView.style.display  = 'block';
        bulkDropzone.style.display    = '';
        bulkFileList.style.display    = '';
        bulkUploadBtn.style.display   = '';
        render();
        bulkPanel.scrollTo({ top: 0, behavior: 'smooth' });
    });

    /* ── Helpers ── */
    function fmtSize(b) {
        if (b < 1024)    return b + ' B';
        if (b < 1048576) return (b / 1024).toFixed(1) + ' KB';
        return (b / 1048576).toFixed(1) + ' MB';
    }
    function escHtml(s) {
        return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
    }
    function escAttr(s) { return String(s).replace(/"/g, '&quot;'); }

})();