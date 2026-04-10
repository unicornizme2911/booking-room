document.addEventListener("DOMContentLoaded", () => {
    if (window.initCategoryAvailable) {
        window.initCategoryAvailable();
    }
})

if (!window.BookingState) window.BookingState = {};
if (!window._categoryAbortCtrl)  window._categoryAbortCtrl = null;
if (!window._bookingNights) window._bookingNights = 0;

window.resetBookingState = function () {
    window.BookingState = {};
};

window.initCategoryAvailable = function (state) {
    const checkIn = state?.checkIn || null;
    const checkOut = state?.checkOut || null;
    const nights = state?.nights || 0;

    window._bookingNights = nights;


    if (window._categoryAbortCtrl) {
        window._categoryAbortCtrl.abort();
    }
    window._categoryAbortCtrl = new AbortController();
    const sig = window._categoryAbortCtrl.signal;

    const container = document.querySelector('.category-container')
    if (!container) return;

    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN').format(amount) + ' VND';
    }

    function getTotalDisplay() {
        return document.getElementById('totalAmount');
    }

    function getReserveBtn() {
        return document.getElementById('reserveBtn');
    }

    function updateTotalAmount() {
        const total = Object.values(window.BookingState).reduce((total, booking) => {
            return total + (booking.rooms * booking.price * window._bookingNights);
        },0);

        const display = getTotalDisplay();
        if (display) {
            display.innerText = formatCurrency(total);
        }

        const reserveBtn = getReserveBtn();
        if (reserveBtn) {
            if (total > 0) {
                reserveBtn.classList.remove('hidden');
            } else {
                reserveBtn.classList.add('hidden');
            }
        }
    }

    function selectRoom(categoryId, rooms, price, btn) {
        window.BookingState[categoryId] = { rooms, price };
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18"
                 viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20 6 9 17 4 12"/>
            </svg>
            ${rooms} room(s) selected`;
        updateTotalAmount();
    }

    function removeRoom(categoryId, btn) {
        delete window.BookingState[categoryId];
        btn.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18"
                 viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="9" cy="21" r="1"/>
                <circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            Select Room`;
        updateTotalAmount();
    }

    function restoreSelectionUI() {
        document.querySelectorAll('.btn-select').forEach(btn => {
            const categoryId = btn.dataset.categoryId;
            if (window.BookingState[categoryId]) {
                const rooms = window.BookingState[categoryId].rooms;
                btn.innerHTML = `
                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18"
                         viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polyline points="20 6 9 17 4 12"/>
                    </svg>
                    ${rooms} room(s) selected`;
            }
        });
        updateTotalAmount();
    }

    document.addEventListener('click', function (e) {
        const selectBtn = e.target.closest('.btn-select');

        if (selectBtn) {
            const container = selectBtn.closest(".category-actions");
            if (!container) return;

            const dropdown = container.querySelector(".room-dropdown");
            const available = parseInt(selectBtn.dataset.available);
            const categoryId = selectBtn.dataset.categoryId;
            const price = parseFloat(selectBtn.dataset.price);

            document.querySelectorAll(".room-dropdown").forEach(d => d.classList.add("hidden"));
            dropdown.classList.toggle("hidden");

            dropdown.innerHTML = "";
            if (window.BookingState[categoryId]) {
                const removeDiv = document.createElement('div');
                removeDiv.className = "room-item remove";
                removeDiv.innerText = "Remove Selection";
                removeDiv.onclick = () => {
                    removeRoom(categoryId, selectBtn);
                    dropdown.classList.add("hidden");
                }
                dropdown.appendChild(removeDiv);
            }

            for (let i = 1; i <= available; i++) {
                const div = document.createElement("div");
                div.className = "room-item";
                if (window.BookingState[categoryId]?.rooms === i) {
                    div.classList.add("active");
                }
                div.innerText = `${i} room(s)`;
                div.onclick = () => {
                    selectRoom(categoryId, i, price, selectBtn);
                    dropdown.classList.add("hidden");
                };
                dropdown.appendChild(div);
            }
        } else {
            document.querySelectorAll(".room-dropdown").forEach(d => {
                d.classList.add("hidden");
            });
        }
    });

    const reserveBtn = getReserveBtn();
    if (reserveBtn) {
        reserveBtn.addEventListener('click', function () {
            const categories = Object.keys(window.BookingState).map(categoryId => ({
                categoryId: categoryId,
                rooms: window.BookingState[categoryId].rooms
            }));

            if (categories.length === 0) {
                alert("Please select at least one room");
                return;
            }

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
                    window.BookingState = {};
                    window.location.href = `booking/review?reservationId=${reservation.id}`;
                })
            .catch(err => console.error('Reservation error: ', err));
        }, {signal: sig});
    }

    const modalOverlay = document.getElementById('modalCategoryOverlay');
    const modalClose = document.getElementById('modalClose');
    const mainImage = document.getElementById("mainImage");
    const sideImage1 = document.getElementById("sideImage1");
    const sideImage2 = document.getElementById("sideImage2");
    const hotelTitle = document.getElementById("hotelTitle");
    const hotelDescription = document.getElementById("hotelDescription");
    const hotelPrice = document.getElementById("hotelPrice");
    const viewButtons = document.querySelectorAll('.btn-details');

    viewButtons.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const card = btn.closest('.category-card');
            const id = card.dataset.categoryId;
            try {
                const res = await fetch(`/api/categories/${id}`);
                const data = await res.json();

                hotelTitle.innerText = data.name;
                hotelDescription.innerText = data.description;
                hotelPrice.innerText = new Intl.NumberFormat('vi-VN').format(data.price) + ' VNĐ';

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
                console.error("View category error: ", err);
            }
        }, {signal: sig});
    });

    function closeModal() {
        if (modalOverlay) modalOverlay.classList.remove('active');
        document.body.style.overflow = 'auto';
    }

    if (modalClose) {
        modalClose.addEventListener('click', closeModal, {signal: sig});
    }

    if (modalOverlay) {
        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay) closeModal();
        })
    }

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    function initGallery() {
        const galleries = document.querySelectorAll(".category-gallery");

        galleries.forEach(gallery => {
            const prevBtn = gallery.querySelector('.gallery-arrow.prev');
            const nextBtn = gallery.querySelector('.gallery-arrow.next');
            const dotsContainer = gallery.querySelector('.gallery-nav');
            const photoCount = gallery.querySelector('.photo-count');
            const img = gallery.querySelector('.gallery-img');
            const images = gallery.dataset.images?.split(",").filter(Boolean) || [];

            if (images.length === 0) {
                photoCount.style.display = 'none';
                return;
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
                const countGallery = photoCount?.querySelector('.count-gallery');
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

    function initPagination() {
        const controls = document.querySelector('.pagination-controls');
        if (!controls) return;

        const pFromDate  = controls.dataset.fromDate;
        const pToDate    = controls.dataset.toDate;
        const pRooms     = controls.dataset.rooms;
        const pAdults    = controls.dataset.adults;
        const pChildren  = controls.dataset.children;

        controls.querySelectorAll('.page-nav').forEach(btn => {
            if (btn.classList.contains('disabled') || btn.disabled) return;

            btn.addEventListener('click', () => {
                const targetPage = btn.dataset.page;
                const bookingContainer = document.querySelector('.booking-steps');
                if (!bookingContainer) return;

                const url = `/booking/search?fromDate=${pFromDate}&toDate=${pToDate}`
                    + `&rooms=${pRooms}&adults=${pAdults}&children=${pChildren}`
                    + `&page=${targetPage}`;

                fetch(url)
                .then(res => res.text())
                .then(html => {
                    bookingContainer.innerHTML = html;
                    window.initCategoryAvailable({
                        checkIn:  checkIn,
                        checkOut: checkOut,
                        nights:   nights
                    });
                    bookingContainer.scrollIntoView({ behavior: 'smooth', block: 'start' });
                })
                .catch(err => console.error('Pagination error:', err));
            }, {signal: sig});
        });
    }

    restoreSelectionUI();
    initGallery();
    initPagination();
}