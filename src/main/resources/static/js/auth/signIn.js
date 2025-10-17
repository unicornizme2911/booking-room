$(document).ready(function () {

    const signInForm = $("#formSignIn");
    const rememberMe = $("#remember-me");
    const showPassword = $('#show-password')
    const email = $("#email");
    const password = $("#password");
    let errorText = $(".error");

    if (localStorage.getItem('emailLogin') !== null && localStorage.getItem('password') !== null) {
        email.val(localStorage.getItem('emailLogin'))
        password.val(localStorage.getItem('password'))
        rememberMe.prop("checked", true)
    }

    signInForm.on("submit", function (e) {
        e.preventDefault()
        // const data = JSON.stringify({email, password});
        $.ajax({
            url: "/api/v1/auth/login",
            type: "POST",
            data: JSON.stringify({email: email.val(), password: password.val()}),
            contentType: "application/json",
            success: function (data) {
                const {access_token, refresh_token} = data;
                localStorage.setItem('token', access_token);
                localStorage.setItem('refreshToken', refresh_token);
                loginSuccess(access_token);

            },
            error: function (error) {
                console.log(error)
                errorText.text(error.responseJSON.message)
                if (error.responseJSON.message === "Bad credentials") {
                    errorText.text("Email or password is incorrect")
                }
            }
        })
    })

    rememberMe.on("change", function (e) {
        if (rememberMe.is(":checked")) {
            localStorage.setItem('emailLogin', email.val())
            localStorage.setItem('password', password.val())
        } else {
            localStorage.removeItem('emailLogin')
            localStorage.removeItem('password')
        }
    })

    showPassword.on("change", function (e) {
        if (showPassword.is(":checked")) {
            password.attr("type", this.checked ? "text" : "password");
        } else {
            password.attr("type", this.checked ? "text" : "password");
        }
    })

    if (localStorage.getItem("email") !== null) {
        email.val(localStorage.getItem("email"));
        localStorage.removeItem("email");
    }

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
