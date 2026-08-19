/**
 * 学生管理前端脚本（原生 JS，无框架）
 * 对应后端接口：/api/students
 * 新增：学生‑课程多对多绑定，列表直接展示已选课程，移除查看课程按钮
 */

const API = '/api/students';
const API_COURSE_REL = '/api/stuCourseRel';
let allCourseList = [];

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
  body.innerHTML = '<tr><td colspan="10" class="empty">加载中...</td></tr>';
  try {
    const data = await request(`${API}?${buildQuery()}`);
    state.total = data.total;
    state.pageNum = data.pageNum;
    renderTable(data.list || []);
    renderPager();
  } catch (e) {
    body.innerHTML = `<tr><td colspan="10" class="empty">${e.message}</td></tr>`;
    toast(e.message, true);
  }
}

function renderTable(list) {
  const body = $('tableBody');
  if (!list.length) {
    //注意 colspan改成10，因为增加一列
    body.innerHTML = '<tr><td colspan="10" class="empty">暂无数据，点击右上角「新增学生」</td></tr>';
    return;
  }
  body.innerHTML = list.map((row) => {
    // 处理课程展示，null/空显示未选课，做html转义
    const courseText = row.courseNames ? escapeHtml(row.courseNames) : "未选课";
    return `
    <tr>
      <td>${row.id}</td>
      <td>${escapeHtml(row.studentNo)}</td>
      <td>${escapeHtml(row.name)}</td>
      <td>${escapeHtml(row.gender || '')}</td>
      <td>${row.age ?? ''}</td>
      <td>${escapeHtml(row.className || '')}</td>
      <td>${escapeHtml(row.phone || '')}</td>
      <td>${courseText}</td>
      <td>${escapeHtml(row.remark || '')}</td>
      <td>
        <div class="ops">
          <button class="btn btn-secondary btn-sm" data-edit="${row.id}">编辑</button>
          <button class="btn btn-danger btn-sm" data-del="${row.id}">删除</button>
        </div>
      </td>
    </tr>`;
  }).join('');
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

// 预加载全部课程
async function loadAllCourse() {
  try {
    const res = await fetch("/api/course/list");
    const json = await res.json();
    if (json.code === 200) {
      allCourseList = json.data;
    }
  } catch (e) {
    toast("课程列表加载失败", true);
  }
}

//渲染课程复选框
function renderCourseCheckbox(selectedIdList = []) {
  const wrap = $('courseCheckWrap');
  wrap.innerHTML = '';
  if (!allCourseList.length) {
    wrap.innerHTML = '<span>暂无课程，请先去课程管理新增课程</span>';
    return;
  }
  allCourseList.forEach(c => {
    const label = document.createElement('label');
    label.style.display = 'flex';
    label.style.gap = '4px';
    label.style.alignItems = 'center';
    label.innerHTML = `
      <input type="checkbox" class="course-chk" value="${c.id}">
      <span>${escapeHtml(c.courseName)}</span>
    `;
    wrap.appendChild(label);
  });
  document.querySelectorAll('.course-chk').forEach(cb => {
    const cid = Number(cb.value);
    cb.checked = selectedIdList.includes(cid);
  });
}

//获取选中课程id数组
function getCheckedCourseIds() {
  const arr = [];
  document.querySelectorAll('.course-chk:checked').forEach(cb => {
    arr.push(Number(cb.value));
  });
  return arr;
}

//保存学生课程绑定
async function saveStudentCourseRel(studentId) {
  const ids = getCheckedCourseIds();
  await request(`${API_COURSE_REL}/save`, {
    method: "POST",
    body: JSON.stringify({
      studentId: studentId,
      courseIdList: ids
    })
  });
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

  if (!student) {
    renderCourseCheckbox([]);
  } else {
    (async () => {
      try {
        const selectedIds = await request(`${API_COURSE_REL}/courseIds?studentId=${student.id}`);
        renderCourseCheckbox(selectedIds);
      } catch (e) {
        renderCourseCheckbox([]);
        toast("回显已选课程失败", true);
      }
    })();
  }
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
    let savedId;
    if (state.editingId) {
      await request(`${API}/${state.editingId}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      savedId = state.editingId;
      toast('修改成功');
    } else {
      const newStu = await request(API, {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      savedId = newStu.id;
      toast('新增成功');
    }
    await saveStudentCourseRel(savedId);
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

  $('tableBody').addEventListener('click', (e) => {
    const editId = e.target.getAttribute('data-edit');
    const delId = e.target.getAttribute('data-del');
    if (editId) editStudent(editId);
    if (delId) deleteStudent(delId);
  });

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
loadAllCourse();
loadList();
