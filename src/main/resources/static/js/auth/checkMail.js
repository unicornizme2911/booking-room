$(document).ready(function () {
const formEmail = $('#formEmailCheckUser')

    formEmail.on('submit', function (event) {
        event.preventDefault()

        const email = formEmail.find("#email").val();
        console.log(email)
        $.ajax({
            url: '/api/v1/auth/check-email',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(email),
            success: function (data) {
                console.log(data)
                localStorage.setItem('email', email)
                window.location.href = '/auth/login'
            },
            error: function (error) {
                console.log(error)
                localStorage.setItem('email', email)
                window.location.href = '/auth/register'
            }
        })
    })
})