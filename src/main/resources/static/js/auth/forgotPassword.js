$(document).ready(function () {
    const formForgotPass = $('#formForgotPass')
    const formVerifyCode = $('#formVerifyCode')
    const formEmailForgotPass = $('#formEmailForgotPass')
    const email = $('#emailForgotPass')
    const code = $('#code')
    const errorText = $('.error')
    const password = $('#password')
    const confirmPassword = $('#confirmPassword')

    sendMail()
    verifyCode()
    changePassword()


    function sendMail() {
        formEmailForgotPass.on('submit', function (event) {
            event.preventDefault()
            $.ajax({
                url: '/api/v1/auth/send-mail',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    email: email.val()
                }),
                success: function (data) {
                    console.log(data)
                    errorText.addClass('d-none')
                    formEmailForgotPass.addClass('d-none')
                    formVerifyCode.removeClass('d-none')
                },
                error: function (error) {
                    console.log(error)
                    console.log(error.responseJSON.message)
                    errorText.removeClass('d-none')
                    errorText.text(error.responseJSON.message)
                }
            })
        })
    }

    function verifyCode() {
        formVerifyCode.on('submit', function (event) {
            event.preventDefault()
            $.ajax({
                url: '/api/v1/auth/verify-token',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    token: code.val(),
                }),
                success: function (data) {
                    console.log(data)
                    errorText.addClass('d-none')
                    formVerifyCode.addClass('d-none')
                    formForgotPass.removeClass('d-none')
                },
                error: function (error) {
                    console.log(error)
                    console.log(error.responseJSON.message)
                    if (code.val().length <= 6) {
                        errorText.removeClass('d-none')
                        errorText.text('Token must be 6 characters')
                    }
                    errorText.removeClass('d-none')
                    errorText.text(error.responseJSON.message)
                }
            })
        })
    }

    function changePassword() {
        formForgotPass.on('submit', function (event) {
            event.preventDefault()
            $.ajax({
                url: '/api/v1/auth/change-password',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    email: email.val(),
                    password: password.val(),
                    confirmPassword: confirmPassword.val()
                }),
                success: function (data) {
                    console.log(data)
                    errorText.addClass('d-none')
                    window.location.href = '/auth/login'
                },
                error: function (error) {
                    console.log(error)
                    console.log(error.responseJSON.message)
                    errorText.removeClass('d-none')
                    errorText.text(error.responseJSON.message)
                }
            })
        })
    }
})