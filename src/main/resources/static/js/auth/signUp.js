$(document).ready(function () {
    const formSignUp = $("#formSignUp")
    let errorText = $(".error")
    const email = $("#email");
    const password = $("#password");
    const confirmPassword = $("#confirmPassword");
    const firstName = $("#firstname");
    const lastName = $("#lastname");

    formSignUp.on("submit", function (e) {
        e.preventDefault()
        const data = JSON.stringify({
            firstname: firstName.val(),
            lastname: lastName.val(),
            email: email.val(),
            password: password.val(),
            confirmPassword: confirmPassword.val(),
        });
        checkPassword()
        console.log(data)
        $.ajax({
            url: "/api/v1/auth/register",
            type: "POST",
            data: data,
            contentType: "application/json",
            success: function (data) {
                console.log(data)
                alert("Sign Up Success")
                window.location.href = "/auth/login";
            },
            error: function (error) {
                console.log(error)
                errorText.text(error.responseJSON.message)
            }
        })
    })

    if (localStorage.getItem("email") !== null) {
        email.val(localStorage.getItem("email"));
        localStorage.removeItem("email");
    }

    function checkPassword() {
        if (password.val() !== confirmPassword.val()) {
            errorText.text("Password and Confirm Password must be same")
            return false
        }
        return true
    }
});