document.addEventListener("DOMContentLoaded", () => {
    if (window.initCategoryAvailable) {
        window.initCategoryAvailable();
    }
})

window.initCategoryAvailable = function (state) {
    const checkIn = state?.checkIn || null;
    const checkOut = state?.checkOut || null;
    const nights = state?.nights || 0;
    console.log(checkIn + "  " + checkOut);
    let totalAmount = 0;
    let bookingState = {};

    const modalOverlay = document.getElementById('modalCategoryOverlay');
    const modalClose = document.getElementById('modalClose');
    const viewButtons = document.querySelectorAll('.btn-details');
    const mainImage = document.getElementById("mainImage");
    const sideImage1 = document.getElementById("sideImage1");
    const sideImage2 = document.getElementById("sideImage2");

    const hotelTitle = document.getElementById("hotelTitle");
    const hotelDescription = document.getElementById("hotelDescription");
    const hotelPrice = document.getElementById("hotelPrice");

    let totalAmountDisplay = document.getElementById('totalAmount');

    const container = document.querySelector('.category-container')
    if (!container) return;

    function formatCurrency(amount) {
        return amount.toLocaleString('vi-VN') + ' VND';
    }

    function initGallery() {
        const galleries = document.querySelectorAll(".category-gallery");

        galleries.forEach(gallery => {
            const prevBtn = gallery.querySelector('.gallery-arrow.prev');
            const nextBtn = gallery.querySelector('.gallery-arrow.next');
            const dotsContainer = gallery.querySelector('.gallery-nav');
            const photoCount = gallery.querySelector('.photo-count');
            const img = gallery.querySelector('.gallery-img');
            const images = gallery.dataset.images?.split(",") || [];
            if (images.length === 0) {
                photoCount.style.display = 'none';
            }
            let currentIndex = 0;

            function updateGallery() {
                if (images.length > 0) {
                    img.src = images[currentIndex];
                    updateDots();
                    updatePhotoCount();
                }
            }

            function updatePhotoCount() {
                const countGallery = photoCount.querySelector('.count-gallery');
                if (!countGallery) return;
                countGallery.textContent = `${currentIndex+1}/${images.length}`;
            }

            function renderDots() {
                dotsContainer.innerHTML = "";
                images.forEach((_, index) => {
                    const dot = document.createElement("span");
                    dot.classList.add("gallery-dot");
                    if (index === currentIndex) dot.classList.add("active");
                    dot.addEventListener("click", () => {
                        currentIndex = index;
                        updateGallery();
                    });
                    dotsContainer.appendChild(dot);
                });
            }

            function updateDots() {
                const dots = dotsContainer.querySelectorAll(".gallery-dot");
                dots.forEach((dot, i) => {
                    dot.classList.toggle("active", i === currentIndex);
                });
            }

            nextBtn.addEventListener("click", (e) => {
                e.stopPropagation();
                currentIndex = (currentIndex + 1) % images.length;
                updateGallery();
            });

            prevBtn.addEventListener("click", (e) => {
                e.stopPropagation();
                currentIndex = (currentIndex - 1 + images.length) % images.length;
                updateGallery();
            });

            renderDots();
            updateGallery();
        })
    }

    function updateTotalAmount() {
        totalAmount = Object.values(bookingState).reduce((total, booking) => {
            return total + (booking.rooms * booking.price * nights);
        },0);
        totalAmountDisplay.innerText = formatCurrency(totalAmount);
        const reserveBtn = document.querySelector('.reserve-btn');
        if (totalAmount > 0) {
            reserveBtn.classList.remove('hidden');
        } else {
            reserveBtn.classList.add('hidden');
        }
    }

    function selectRoom(categoryId, rooms, price, btn) {
        bookingState[categoryId] = {
            rooms,
            price
        };
        btn.innerText = `${rooms} room(s) selected`;
        updateTotalAmount();
    }

    function removeRoom(categoryId, btn) {
        delete bookingState[categoryId];
        btn.innerText = "Select Room";
        updateTotalAmount();
    }

    document.addEventListener('click', function (e) {
        if (e.target.closest('.btn-select')){
            const btn = e.target.closest('.btn-select');
            const container = btn.closest(".category-actions");
            const dropdown = container.querySelector(".room-dropdown");

            const available = parseInt(btn.dataset.available);
            const categoryId = btn.dataset.categoryId;
            const price = parseFloat(btn.dataset.price);

            document.querySelectorAll(".room-dropdown").forEach(d => d.classList.add("hidden"));
            dropdown.classList.toggle("hidden");

            dropdown.innerHTML = "";

            if (bookingState[categoryId]) {
                const removeDiv = document.createElement('div');
                removeDiv.className = "room-item remove";
                removeDiv.innerText = "Remove Selection";
                removeDiv.onclick = () => {
                    removeRoom(categoryId, btn);
                    dropdown.classList.add("hidden");
                }
                dropdown.appendChild(removeDiv);
            }

            for (let i = 1; i <= available; i++) {
                const div = document.createElement("div");
                div.className = "room-item";

                if (bookingState[categoryId]?.rooms === i) {
                    div.classList.add("active");
                }

                div.innerText = `${i} room(s)`;

                div.onclick = () => {
                    selectRoom(categoryId, i, price, btn);
                    dropdown.classList.add("hidden");
                };
                dropdown.appendChild(div);
            }
        } else {
            document.querySelectorAll(".room-dropdown").forEach(d => d.classList.add("hidden"));
        }
    });

    const reserveBtn = document.getElementById('reserveBtn');
    if (reserveBtn) {
        reserveBtn.addEventListener('click', function () {
            const categories = Object.keys(bookingState).map(categoryId => ({
                categoryId: categoryId,
                rooms: bookingState[categoryId].rooms
            }));
            const payload = {
                fromDate: checkIn,
                toDate: checkOut,
                nights: nights,
                categories: categories
            }
            console.log(payload);
            fetch('/booking/review', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            }).then(res => res.json())
                .then(reservation => {
                    window.location.href = `booking/review?reservationId=${reservation.id}`;
                });
        });
    }

    viewButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const card = btn.closest('.category-card');
            const id = card.dataset.categoryId;
            try {
                const res = await fetch(`/api/categories/${id}`);
                const data = await res.json();
                hotelTitle.innerText = data.name;
                hotelDescription.innerText = data.description;
                hotelPrice.innerText = data.price + " VNĐ";

                if (data.images && data.images.length > 0) {
                    mainImage.src = data.images[0];
                    sideImage1.src = data.images[1] || data.images[0];
                    sideImage2.src = data.images[2] || data.images[0];
                }
                const amenitiesContainer = document.querySelector(".amenities-section");
                amenitiesContainer.innerHTML = "";

                data.features.forEach(f => {
                    const div = document.createElement("div");
                    div.className = "amenity-item";
                    div.innerHTML = `
                        <span class="amenity-icon">
                            <i class="fa fa-${f.icon}"></i>
                        </span>
                        <p>${f.name}</p>
                    `;
                    amenitiesContainer.appendChild(div);
                });
                modalOverlay.classList.add("active");
                document.body.overflow = "hidden";
            } catch (err) {
                console.error(err);
            }
        })
    })

    function closeModal() {
        modalOverlay.classList.remove('active');
        document.body.style.overflow = 'auto';
    }

    modalClose.addEventListener('click', closeModal);

    modalOverlay.addEventListener('click', (e) => {
        if (e.target === modalOverlay) {
            closeModal();
        }
    });
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    initGallery();
}