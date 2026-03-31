$(document).ready(function () {

    const signInForm = $("#formSignIn");
    const rememberMe = $("#remember-me");
    const showPassword = $('#show-password')
    const email = $("#email");
    const password = $("#password");
    let errorText = $(".error");

    if (localStorage.getItem('emailLogin') !== null) {
        email.val(localStorage.getItem('emailLogin'))
        rememberMe.prop("checked", true)
    }

    signInForm.on("submit", function (e) {
        e.preventDefault()
        $.ajax({
            url: "/api/v1/auth/login",
            type: "POST",
            data: JSON.stringify({
                email: email.val(),
                password: password.val()
            }),
            contentType: "application/json",
            success: function () {
                if (rememberMe.is(":checked")) {
                    localStorage.setItem('emailLogin', email.val())
                } else {
                    localStorage.removeItem('emailLogin');
                }
                window.location.href = "/";
            },
            error: function (error) {
                console.log(error)
                if (error.responseJSON?.message === "Bad credentials") {
                    errorText.text("Email or password is incorrect")
                }else {
                    errorText.text(error.responseJSON?.message || "Login failed");
                }
            }
        })
    })

    rememberMe.on("change", function (e) {
        if (rememberMe.is(":checked")) {
            localStorage.setItem('emailLogin', email.val())
        } else {
            localStorage.removeItem('emailLogin')
        }
    })

    showPassword.on("change", function (e) {
        password.attr("type", this.checked ? "text" : "password");
    })

    function loginSuccess(token) {
        if (token) {
            $.ajax({
                url: "/api/v1/auth/login-success",
                type: "POST",
                data: token,
                contentType: "application/json",
                headers: {
                    'Authorization': 'Bearer ' + token,
                },
                success: function (data) {
                    console.log(data)
                    window.location.href = data;
                },
                error: function (error) {
                    console.log(error)
                    window.location.href = '/auth/login'
                }
            })
        } else {
            window.location.href = '/auth/login'
        }
    }
})
