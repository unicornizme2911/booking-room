$(document).ready(function () {

    // render categories
    $.ajax({
        url: '/api/categories/list',
        method: 'GET',
        contentType: 'application/json',
        success: function (data) {
            // console.log(data);
            const template = data.content.slice(0, 3).map((category, index) => {
                return `
                    <div class="category-item col-md-3 col-sm-6 col-10" style="max-width: 20rem" data-id="${category.id}">
                        <p class="category-name fw-bold mt-3">${category.name}</p>
                    </div>             
                `;
            })
            $('.categories__items').append(template);
            categoryClick();
        },
        error: function (error) {
            console.log(error)
        }
    })

    // click category
    function categoryClick() {
        $('.category-item').on('click', function (e) {
            const id = $(this).data('id');
            console.log(id);
            window.location.href = `/products?type=${id}`;
        })
    }

    // click more products
    $('.products__list--button').on('click', function (e) {
        window.location.href = '/products';
    })
});