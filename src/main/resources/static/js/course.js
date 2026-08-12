const tipDom = document.getElementById('tip');
const tableBody = document.getElementById('tableBody');
const mask = document.getElementById('mask');
const modalTitle = document.getElementById('modalTitle');
const editIdDom = document.getElementById('editId');
const courseNameDom = document.getElementById('courseName');
const teacherDom = document.getElementById('teacher');
const creditDom = document.getElementById('credit');
const hoursDom = document.getElementById('hours');
const closeMaskBtn = document.getElementById('closeMask');

//搜索框DOM
const qCourseNameDom = document.getElementById('qCourseName');
const qTeacherDom = document.getElementById('qTeacher');
const btnSearch = document.getElementById('btnSearch');
const btnReset = document.getElementById('btnReset');


// 加载课程列表，支持条件搜索
async function loadCourseList() {
    tipDom.innerText = '';
    const courseName = qCourseNameDom.value.trim();
    const teacher = qTeacherDom.value.trim();

    //拼接查询参数
    const params = new URLSearchParams();
    if(courseName) params.append("courseName", courseName);
    if(teacher) params.append("teacher", teacher);

    let url = '/api/course/list';
    if(params.toString()){
        url += "?" + params.toString();
    }

    const res = await fetch(url);
    const json = await res.json();
    if(json.code !== 200) {
        tipDom.innerText = json.message;
        return;
    }
    const list = json.data;
    tableBody.innerHTML = '';
    list.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
              <td>${item.id}</td>
              <td>${item.courseName}</td>
              <td>${item.teacher}</td>
              <td>${item.credit}</td>
              <td>${item.hours}</td>
              <td>
                  <button class="btn btn-secondary btn-sm edit-btn" data-id="${item.id}">编辑</button>
                  <button class="btn btn-danger btn-sm del-btn" data-id="${item.id}">删除</button>
              </td>
          `;
        tableBody.appendChild(tr);
    });
    bindTableEvent();
}

// 表格行事件委托
function bindTableEvent(){
    document.querySelectorAll('.edit-btn').forEach(btn=>{
        btn.onclick = async function(){
            const id = this.dataset.id;
            const res = await fetch(`/api/course/${id}`);
            const json = await res.json();
            if(json.code !==200){
                tipDom.innerText = json.message;
                return;
            }
            const course = json.data;
            modalTitle.innerText = '编辑课程';
            editIdDom.value = course.id;
            courseNameDom.value = course.courseName;
            teacherDom.value = course.teacher;
            creditDom.value = course.credit;
            hoursDom.value = course.hours;
            mask.hidden = false;
        }
    })
    document.querySelectorAll('.del-btn').forEach(btn=>{
        btn.onclick = async function(){
            const id = this.dataset.id;
            if(!confirm('确定删除？')) return;
            const res = await fetch(`/api/course/${id}`, {
                method:'DELETE'
            });
            const json = await res.json();
            tipDom.innerText = json.code===200?'删除成功':json.message;
            loadCourseList();
        }
    })
}

//关闭弹窗
function closeModal(){
    mask.hidden = true;
}

// 打开新增弹窗
document.getElementById('addBtn').onclick = function(){
    modalTitle.innerText = '新增课程';
    editIdDom.value = '';
    courseNameDom.value = '';
    teacherDom.value = '';
    creditDom.value = '';
    hoursDom.value = '';
    mask.hidden = false;
}

document.getElementById('cancelBtn').onclick = closeModal;
closeMaskBtn.onclick = closeModal;

//保存：新增 / 修改
document.getElementById('saveBtn').onclick = async function(){
    const id = editIdDom.value;
    const body = {
        courseName: courseNameDom.value.trim(),
        teacher: teacherDom.value.trim(),
        credit: Number(creditDom.value),
        hours: Number(hoursDom.value)
    };
    let url, method;
    if(id){
        //编辑
        url = '/api/course';
        method = 'PUT';
        body.id = Number(id);
    }else{
        //新增
        url = '/api/course';
        method = 'POST';
    }
    const res = await fetch(url, {
        method: method,
        headers: {
            'Content-Type':'application/json'
        },
        body: JSON.stringify(body)
    });
    const json = await res.json();
    tipDom.innerText = json.code===200?'保存成功':json.message;
    closeModal();
    loadCourseList();
}

//查询按钮
btnSearch.onclick = function (){
    loadCourseList();
}
//重置按钮：清空输入框，重新加载
btnReset.onclick = function (){
    qCourseNameDom.value = '';
    qTeacherDom.value = '';
    loadCourseList();
}

//页面初始化加载列表
loadCourseList();
