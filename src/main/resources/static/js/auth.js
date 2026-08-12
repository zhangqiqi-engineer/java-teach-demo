//登录
document.querySelector("#loginBtn")?.addEventListener("click",async ()=>{
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const msgDom = document.getElementById("msg");
    if(!username || !password){
        msgDom.innerText="用户名密码不能为空";
        return;
    }
    const res = await fetch("/api/auth/login",{
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify({username,password})
    })
    const data = await res.json();
    if(data.code === 200){
        msgDom.innerText="登录成功，跳转到学生管理";
        setTimeout(()=>{
            location.href="/index.html"
        },800)
    }else{
        msgDom.innerText=data.msg;
    }
})

//注册
document.querySelector("#regBtn")?.addEventListener("click",async ()=>{
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const msgDom = document.getElementById("msg");
    if(!username || !password){
        msgDom.innerText="用户名密码不能为空";
        return;
    }
    const res = await fetch("/api/auth/register",{
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify({username,password})
    })
    const data = await res.json();
    if(data.code ===200){
        msgDom.innerText="注册成功，请登录";
        setTimeout(()=>{
            location.href="/login.html"
        },800)
    }else{
        msgDom.innerText=data.msg;
    }
})
