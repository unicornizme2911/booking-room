$(document).ready(function () {

    const img = $(".img-src")
    const firstName = $("#firstName")
    const lastName = $("#lastName")
    const email = $("#email")
    const gender = $("#gender")
    const contact = $("#contact")
    const address = $("#address")
    const formUpdateUser = $("#formUpdateUser")
    const error = $(".error")

    if (localStorage.getItem("token") == null) {
        window.location.href = "/login"
    } else {
        let token = localStorage.getItem("token")
        console.log(typeof token)
        findUser(token)
        updateUser()

    }

    function findUser(token) {
        $.ajax({
            url: "/api/user/get-user",
            type: "POST",
            data: token,
            contentType: "application/json",
            success: function (data) {
                console.log(data)
                firstName.val(data.firstname)
                lastName.val(data.lastname)
                email.val(data.email)
                gender.val(data.gender)
                contact.val(data.contact)
                address.val(data.address)
                img.attr("src", data.avatar)
            },
            error: function (error) {
                console.log(error)
            }
        })
    }

    function updateUser() {
        formUpdateUser.on("submit", function (e) {
            e.preventDefault()
            $.ajax({
                url: "/api/user/update",
                type: "PUT",
                data: JSON.stringify({
                    firstname: firstName.val(),
                    lastname: lastName.val(),
                    email: email.val(),
                    gender: gender.val(),
                    contact: contact.val(),
                    address: address.val()
                }),
                contentType: "application/json",
                success: function (data) {
                    console.log(data)
                    window.location.href = "/"
                    alert("Update Success")
                },
                error: function (error) {
                    console.log(error)
                    error.text(error.responseJSON.message)
                }
            })
        })
    }
})

