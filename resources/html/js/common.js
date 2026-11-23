const baseURL = "http://127.0.0.1:10030";
$(document).ajaxError(function(event, xhr, options, exc) {
    if (xhr.status == 400) {
        alert("failed to check the params!");
    } else if (xhr.status == 401) {
        alert("User is not login!");
        location.href = "/blog_login.html";
    }
})

$(document).ajaxSend(function(e, xhr, opt) {
    opt.url = baseURL + opt.url;
    var user_token = localStorage.getItem("user_token");
    // 没有 token 时不要发一个值为 "null" 的请求头
    xhr.setRequestHeader("user_token", user_token);
})

// 从地址栏取 blogId，例如 blog_detail.html?blogId=1
function getBlogId() {
    return new URLSearchParams(location.search).get("blogId");
}

// 解码 JWT 的 payload（只用于前端取当前用户 id，不做校验）
function decodeJwtPayload(token) {
    var part = token.split(".")[1];
    if (!part) {
        return null;
    }
    part = part.replace(/-/g, "+").replace(/_/g, "/");
    while (part.length % 4) {
        part += "=";
    }
    var binary = atob(part);
    var bytes = new Uint8Array(binary.length);
    for (var i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
    }
    return JSON.parse(new TextDecoder("utf-8").decode(bytes));
}

// 当前登录用户 id：优先 localStorage，取不到时从 token 里解。
// 只要 token 还在，即使 loginUserId 被清掉，作者也能看到自己的编辑/删除按钮。
function getLoginUserId() {
    var userId = localStorage.getItem("loginUserId");
    if (userId && userId !== "undefined" && userId !== "null") {
        return String(userId);
    }
    var token = localStorage.getItem("user_token");
    if (!token) {
        return null;
    }
    try {
        var payload = decodeJwtPayload(token);
        return payload && payload.id != null ? String(payload.id) : null;
    } catch (e) {
        return null;
    }
}

// 列表页只做文本展示，简单转义，避免博客内容里的尖括号被当成 HTML
function escapeHtml(str) {
    if (str == null) {
        return "";
    }
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

function getUserInfo(userUrl) {
    $.ajax({
        type: "get",
        url: userUrl,
        success: function(result) {
            if (result != null && result.code == 200 && result.data != null) {
                $(".left .card h3").text(result.data.userName);
                // 只改 GitHub 链接，不要用 $(".left .card a") —— 那会把卡片里其它链接（如「写博客」）
                // 也一并改成 githubUrl
                $(".left .card a.github-link").attr("href", result.data.githubUrl || "#");
            }
        }
    });
}

function logout() {
    // 原来写成了 uesr_token，导致 token 一直留在 localStorage 里
    localStorage.removeItem("user_token");
    localStorage.removeItem("loginUserId");
    location.href = "/blog_login.html";
}

/* 导航栏登录态：未登录显示「登录/注册」，已登录显示「写博客/注销」。
   注意这是纯前端的显隐，真正的权限由网关 AuthFilter 校验。 */
$(function () {
    var logged = !!localStorage.getItem("user_token");
    $(".nav-auth").toggle(logged);
    $(".nav-guest").toggle(!logged);
});
