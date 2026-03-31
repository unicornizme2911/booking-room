$(document).ready(function (e) {
    const btnPerson = $('.btn-person')
    const btnCheck = $('#btn-check')
    const checkToken = $('.check-token')
    const ulDisplay = $('.ul-display')
    const checkAuth = $('.check-auth')

    function redirectIfNotLogin(url) {
        $.ajax({
            url: "/api/v1/auth/me",
            type: "GET",
            xhrFields: {
                withCredentials: true
            },
            success: function () {
                window.location.href = url
            },
            error: function () {
                window.location.href = "/auth/login"
            }
        })

    }
    checkAuth.on('click', (e) => {
        e.preventDefault()
        redirectIfNotLogin('/cart')
    })

    checkToken.on('click', function (e) {
        e.preventDefault()
        $.ajax({
            url: "/api/v1/auth/me",
            type: "GET",
            xhrFields: {
                withCredentials: true
            },
            success: function () {
                ulDisplay.removeClass('d-none')
            },
            error: function () {
                window.location.href = "/auth/login"
            }
        })
    })

    btnCheck.on('click', function (e) {
        e.preventDefault()
        redirectIfNotLogin('/user/account-setting')
    })

    btnPerson.on('click', function (e) {
        e.preventDefault()
        $.ajax({
            url: '/api/v1/auth/logout',
            type: 'POST',
            xhrFields: {
                withCredentials: true
            },
            success: function () {
                window.location.href = '/auth/login'
            },
            error: function (error) {
                console.log(error)
            }
        })
    })
})
