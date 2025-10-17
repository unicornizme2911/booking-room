$(document).ready(function (e) {
    const btnPerson = $('.btn-person')
    const btnCheck = $('#btn-check')
    const checkToken = $('.check-token')
    const ulDisplay = $('.ul-display')
    const checkAuth = $('.check-auth')
    const checkOrder = $('.check-order')

    checkAuth.on('click', (e) => {
        e.preventDefault()
        if (localStorage.getItem("token") === null) {
            window.location.href = '/auth/login'
        } else {
            window.location.href = '/cart'
        }
    })

    checkToken.on('click', (e) => {
        e.preventDefault()
        if (localStorage.getItem("token") === null) {
            window.location.href = '/auth/login'
        } else {
           ulDisplay.removeClass('d-none')
        }
    })


    btnCheck.on('click', (e) => {
        e.preventDefault()
        if (localStorage.getItem("token") !== null) {
            window.location.href = '/user/account-setting'
        } else {
            window.location.href = '/auth/login'
        }
    })

    btnPerson.on('click', (e) => {
        e.preventDefault()
        if (localStorage.getItem("token") !== null) {
            $.ajax({
                url: '/api/v1/auth/logout',
                type: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem("token")
                },
                success: function (data) {
                    console.log(data)
                    localStorage.removeItem("token")
                    localStorage.removeItem("refreshToken")
                    window.location.href = '/auth/login'
                },
                error: function (error) {
                    console.log(error)
                }
            })
        } else {
            window.location.href = '/'
        }
    })
})
