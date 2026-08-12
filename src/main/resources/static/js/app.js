/**
 * 学生管理前端脚本（原生 JS，无框架）
 * 对应后端接口：/api/students
 */

const API = '/api/students';

const state = {
  pageNum: 1,
  pageSize: 10,
  total: 0,
  editingId: null
};

const $ = (id) => document.getElementById(id);

function toast(message, isError = false) {
  const el = $('toast');
  el.textContent = message;
  el.classList.toggle('error', isError);
  el.hidden = false;
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => {
    el.hidden = true;
  }, 2200);
}

async function request(url, options = {}) {
  const resp = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });
  const json = await resp.json();
  if (json.code !== 200) {
    throw new Error(json.message || '请求失败');
  }
  return json.data;
}

function buildQuery() {
  const params = new URLSearchParams({
    pageNum: String(state.pageNum),
    pageSize: String(state.pageSize)
  });
  const name = $('qName').value.trim();
  const studentNo = $('qStudentNo').value.trim();
  const className = $('qClassName').value.trim();
  if (name) params.set('name', name);
  if (studentNo) params.set('studentNo', studentNo);
  if (className) params.set('className', className);
  return params.toString();
}

async function loadList() {
  const body = $('tableBody');
  body.innerHTML = '<tr><td colspan="9" class="empty">加载中...</td></tr>';
  try {
    const data = await request(`${API}?${buildQuery()}`);
    state.total = data.total;
    state.pageNum = data.pageNum;
    renderTable(data.list || []);
    renderPager();
  } catch (e) {
    body.innerHTML = `<tr><td colspan="9" class="empty">${e.message}</td></tr>`;
    toast(e.message, true);
  }
}

function renderTable(list) {
  const body = $('tableBody');
  if (!list.length) {
    body.innerHTML = '<tr><td colspan="9" class="empty">暂无数据，点击右上角「新增学生」</td></tr>';
    return;
  }
  body.innerHTML = list.map((row) => `
    <tr>
      <td>${row.id}</td>
      <td>${escapeHtml(row.studentNo)}</td>
      <td>${escapeHtml(row.name)}</td>
      <td>${escapeHtml(row.gender || '')}</td>
      <td>${row.age ?? ''}</td>
      <td>${escapeHtml(row.className || '')}</td>
      <td>${escapeHtml(row.phone || '')}</td>
      <td>${escapeHtml(row.remark || '')}</td>
      <td>
        <div class="ops">
          <button class="btn btn-secondary btn-sm" data-edit="${row.id}">编辑</button>
          <button class="btn btn-danger btn-sm" data-del="${row.id}">删除</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function renderPager() {
  const pages = Math.max(1, Math.ceil(state.total / state.pageSize));
  $('pageInfo').textContent = `共 ${state.total} 条 · 第 ${state.pageNum}/${pages} 页`;
  $('pageNum').textContent = String(state.pageNum);
  $('btnPrev').disabled = state.pageNum <= 1;
  $('btnNext').disabled = state.pageNum >= pages;
}

function escapeHtml(str) {
  return String(str)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

function openModal(title, student) {
  $('modalTitle').textContent = title;
  state.editingId = student ? student.id : null;
  $('fId').value = student ? student.id : '';
  $('fStudentNo').value = student ? student.studentNo : '';
  $('fName').value = student ? student.name : '';
  $('fGender').value = student ? student.gender : '男';
  $('fAge').value = student && student.age != null ? student.age : '';
  $('fClassName').value = student ? (student.className || '') : '';
  $('fPhone').value = student ? (student.phone || '') : '';
  $('fRemark').value = student ? (student.remark || '') : '';
  $('modal').hidden = false;
}

function closeModal() {
  $('modal').hidden = true;
  state.editingId = null;
}

function collectForm() {
  const ageVal = $('fAge').value.trim();
  return {
    studentNo: $('fStudentNo').value.trim(),
    name: $('fName').value.trim(),
    gender: $('fGender').value,
    age: ageVal === '' ? null : Number(ageVal),
    className: $('fClassName').value.trim() || null,
    phone: $('fPhone').value.trim() || null,
    remark: $('fRemark').value.trim() || null
  };
}

async function saveStudent() {
  const payload = collectForm();
  if (!payload.studentNo || !payload.name) {
    toast('学号和姓名不能为空', true);
    return;
  }
  try {
    if (state.editingId) {
      await request(`${API}/${state.editingId}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      toast('修改成功');
    } else {
      await request(API, {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      toast('新增成功');
    }
    closeModal();
    await loadList();
  } catch (e) {
    toast(e.message, true);
  }
}

async function editStudent(id) {
  try {
    const student = await request(`${API}/${id}`);
    openModal('编辑学生', student);
  } catch (e) {
    toast(e.message, true);
  }
}

async function deleteStudent(id) {
  if (!confirm('确认删除该学生吗？')) return;
  try {
    await request(`${API}/${id}`, { method: 'DELETE' });
    toast('删除成功');
    // 若当前页删空且不是第一页，回退一页
    const remain = state.total - 1;
    const maxPage = Math.max(1, Math.ceil(remain / state.pageSize));
    if (state.pageNum > maxPage) state.pageNum = maxPage;
    await loadList();
  } catch (e) {
    toast(e.message, true);
  }
}

function bindEvents() {
  $('btnAdd').addEventListener('click', () => openModal('新增学生', null));
  $('btnClose').addEventListener('click', closeModal);
  $('btnCancel').addEventListener('click', closeModal);
  $('btnSave').addEventListener('click', saveStudent);
  $('btnSearch').addEventListener('click', () => {
    state.pageNum = 1;
    loadList();
  });
  $('btnReset').addEventListener('click', () => {
    $('qName').value = '';
    $('qStudentNo').value = '';
    $('qClassName').value = '';
    state.pageNum = 1;
    loadList();
  });
  $('btnPrev').addEventListener('click', () => {
    if (state.pageNum > 1) {
      state.pageNum -= 1;
      loadList();
    }
  });
  $('btnNext').addEventListener('click', () => {
    const pages = Math.max(1, Math.ceil(state.total / state.pageSize));
    if (state.pageNum < pages) {
      state.pageNum += 1;
      loadList();
    }
  });

  // 事件委托：表格内编辑/删除
  $('tableBody').addEventListener('click', (e) => {
    const editId = e.target.getAttribute('data-edit');
    const delId = e.target.getAttribute('data-del');
    if (editId) editStudent(editId);
    if (delId) deleteStudent(delId);
  });

  // 回车查询
  ['qName', 'qStudentNo', 'qClassName'].forEach((id) => {
    $(id).addEventListener('keydown', (e) => {
      if (e.key === 'Enter') {
        state.pageNum = 1;
        loadList();
      }
    });
  });
}

bindEvents();
loadList();
