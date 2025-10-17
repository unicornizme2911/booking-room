$(document).ready(function () {
    const formHandleLoginGoogle = $("#formHandleLoginGoogle");
    const queryString = window.location.search;
    const urlParams = new URLSearchParams(queryString);
    const email = urlParams.get('email');

    formHandleLoginGoogle.on("submit", function (e) {
        e.preventDefault()
        $.ajax({
            url: "/api/user/get-user-by-email",
            type: "POST",
            data: email,
            contentType: "application/json",
            success: function (data) {
                console.log(data)
                localStorage.setItem('token', data.access_token);
                window.location.href = "/"
            },
            error: function (error) {
                console.log(error)
                errorText.text(error.responseJSON.message)
            }
        })
    })
})