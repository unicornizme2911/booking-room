document.addEventListener('DOMContentLoaded', function (e) {
    let checkinDate = new Date();
    checkinDate.setHours(0, 0, 0, 0);

    let checkoutDate = new Date();
    checkoutDate.setDate(checkoutDate.getDate() + 1);
    checkoutDate.setHours(0, 0, 0, 0);

    let leftMonth = new Date(checkinDate.getFullYear(), checkinDate.getMonth(), 1);
    let rightMonth = new Date(checkinDate.getFullYear(), checkinDate.getMonth() + 1, 1);

    let selectionMode = 'checkin';

    let guests = {
        rooms: 1,
        adults: 2,
        children: 0
    };

    let totalAmount = 0;

    let bookingState = {};

    const dateRangeWrapper = document.getElementById('dateRangeWrapper');
    const calendarDropdown = document.getElementById('calendarDropdown');
    const guestsWrapper = document.getElementById('guestsWrapper');
    const guestsDropdown = document.getElementById('guestsDropdown');
    const dropdownOverlay = document.getElementById('dropdownOverlay');

    const checkinDateDisplay = document.getElementById('checkinDate');
    const checkinDayDisplay = document.getElementById('checkinDay');
    const checkoutDateDisplay = document.getElementById('checkoutDate');
    const checkoutDayDisplay = document.getElementById('checkoutDay');
    const nightsCountDisplay = document.getElementById('nightsCount');
    const guestsDisplay = document.getElementById('guestsDisplay');
    let totalAmountDisplay = document.getElementById('totalAmount');
    const dateRangeSummary = document.getElementById('dateRangeSummary');

    const calendarPrev = document.getElementById('calendarPrev');
    const calendarNext = document.getElementById('calendarNext');
    const leftMonthTitle = document.getElementById('leftMonthTitle');
    const rightMonthTitle = document.getElementById('rightMonthTitle');
    const leftMonthDays = document.getElementById('leftMonthDays');
    const rightMonthDays = document.getElementById('rightMonthDays');

    const roomsCountDisplay = document.getElementById('roomsCount');
    const adultsCountDisplay = document.getElementById('adultsCount');
    const childrenCountDisplay = document.getElementById('childrenCount');

    const shortMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const days = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];

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

    function formatDate(date) {
        return `${shortMonths[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
    }

    function formatFullDate(date) {
        return `${days[date.getDay()]}, ${shortMonths[date.getMonth()]} ${date.getDate()}, ${date.getFullYear()}`;
    }

    function formatDay(date) {
        return days[date.getDay()];
    }

    function formatMonthYear(date) {
        return `${shortMonths[date.getMonth()].toUpperCase()} ${date.getFullYear()}`;
    }

    function calculateNights() {
        const diffTime = checkoutDate - checkinDate;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return Math.max(1, diffDays);
    }

    function formatCurrency(amount) {
        return amount.toLocaleString('vi-VN') + ' VND';
    }

    function isSameDay(date1, date2) {
        return date1.getFullYear() === date2.getFullYear() &&
            date1.getMonth() === date2.getMonth() &&
            date1.getDate() === date2.getDate();
    }

    function isDateInRange(date) {
        return date > checkinDate && date < checkoutDate;
    }

    // Update Displays
    function updateDateDisplays() {
        checkinDateDisplay.textContent = formatDate(checkinDate);
        checkinDayDisplay.textContent = formatDay(checkinDate);
        checkoutDateDisplay.textContent = formatDate(checkoutDate);
        checkoutDayDisplay.textContent = formatDay(checkoutDate);

        const nights = calculateNights();
        nightsCountDisplay.textContent = nights;

        // Update summary
        dateRangeSummary.textContent = `${formatFullDate(checkinDate)} - ${formatFullDate(checkoutDate)} (${nights} night(s))`;
    }

    function updateGuestsDisplay() {
        const roomText = guests.rooms === 1 ? 'Room' : 'Rooms';
        const adultText = 'Adult';
        const childText = 'Child';
        guestsDisplay.textContent = `${guests.rooms} ${roomText}, ${guests.adults} ${adultText}, ${guests.children} ${childText}`;

        if (roomsCountDisplay) roomsCountDisplay.textContent = guests.rooms;
        if (adultsCountDisplay) adultsCountDisplay.textContent = guests.adults;
        if (childrenCountDisplay) childrenCountDisplay.textContent = guests.children;

        updateCounterButtons();
    }

    function updateCounterButtons() {
        document.querySelectorAll('.counter-btn.minus').forEach(btn => {
            const type = btn.dataset.type;
            const minValue = type === 'children' ? 0 : 1;
            btn.disabled = guests[type] <= minValue;
        });

        document.querySelectorAll('.counter-btn.plus').forEach(btn => {
            const type = btn.dataset.type;
            const maxValue = type === 'rooms' ? 10 : 20;
            btn.disabled = guests[type] >= maxValue;
        });
    }

    function updateTotalAmount() {
        const nights = calculateNights();
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

    // Calendar Rendering - Monday-based weeks
    function renderCalendar(container, monthDate) {
        container.innerHTML = '';

        const year = monthDate.getFullYear();
        const month = monthDate.getMonth();

        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const totalDays = lastDay.getDate();

        // Get day of week (0=Sunday, 1=Monday, ..., 6=Saturday)
        // Convert to Monday-based (0=Monday, ..., 6=Sunday)
        let startingDay = firstDay.getDay();
        startingDay = startingDay === 0 ? 6 : startingDay - 1;

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        // Empty cells for days before the first day of month
        for (let i = 0; i < startingDay; i++) {
            const emptyDay = document.createElement('div');
            emptyDay.className = 'day empty';
            container.appendChild(emptyDay);
        }

        // Days of the month
        for (let day = 1; day <= totalDays; day++) {
            const dayElement = document.createElement('div');
            dayElement.className = 'day';
            dayElement.textContent = day;

            const currentDate = new Date(year, month, day);
            currentDate.setHours(0, 0, 0, 0);

            // Check day of week for weekend styling (Saturday = 5, Sunday = 6 in Monday-based)
            const dayOfWeek = currentDate.getDay();
            if (dayOfWeek === 0 || dayOfWeek === 6) {
                dayElement.classList.add('weekend');
            }

            // Check if this day is today
            if (isSameDay(currentDate, today)) {
                dayElement.classList.add('today');
            }

            // Check if this is checkin date
            if (isSameDay(currentDate, checkinDate)) {
                dayElement.classList.add('checkin-date');
            }

            // Check if this is checkout date
            if (isSameDay(currentDate, checkoutDate)) {
                dayElement.classList.add('checkout-date');
            }

            // Check if in range between checkin and checkout
            if (isDateInRange(currentDate)) {
                dayElement.classList.add('in-range');
            }

            // Check if this day is disabled (before today)
            if (currentDate < today) {
                dayElement.classList.add('disabled');
            } else {
                dayElement.addEventListener('click', function (e) {
                    e.stopPropagation();
                    handleDateSelection(currentDate);
                });
            }

            container.appendChild(dayElement);
        }
    }

    function handleDateSelection(selectedDate) {

        if (selectionMode === 'checkin') {
            checkinDate = new Date(selectedDate);
            if (checkoutDate <= checkinDate) {
                checkoutDate = new Date(checkinDate);
                checkoutDate.setDate(checkoutDate.getDate() + 1);
            }

            selectionMode = 'checkout';
        } else {
            if (selectedDate <= checkinDate) {
                checkinDate = new Date(selectedDate);
                checkoutDate = new Date(checkinDate);
                checkoutDate.setDate(checkoutDate.getDate() + 1);
                selectionMode = 'checkout';
            } else {
                checkoutDate = new Date(selectedDate);
                selectionMode = 'checkin';
                closeAllDropdowns();
            }
        }

        updateDateDisplays();
        updateCalendars();
        updateTotalAmount();
    }

    function updateCalendars() {
        leftMonthTitle.textContent = formatMonthYear(leftMonth);
        rightMonthTitle.textContent = formatMonthYear(rightMonth);

        renderCalendar(leftMonthDays, leftMonth);
        renderCalendar(rightMonthDays, rightMonth);
    }

    function closeAllDropdowns() {
        if (calendarDropdown) calendarDropdown.classList.remove('show');
        if (guestsDropdown) guestsDropdown.classList.remove('show');
        selectionMode = 'checkin';
    }

    function showCalendarDropdown() {
        closeAllDropdowns();
        updateCalendars();
        calendarDropdown.classList.add('show');
    }

    function showGuestsDropdown() {
        closeAllDropdowns();
        guestsDropdown.classList.add('show');
    }

    function showLoading(container) {
        container.innerHTML = `
            <div class="loading">
                <div class="spinner"></div>
                <p>Loading rooms...</p>
            </div>
        `;
    }

    if (dateRangeWrapper) {
        dateRangeWrapper.addEventListener('click', function (e) {
            if (e.target.closest('.nav-btn')) return;
            if (e.target.closest('.day')) return;
            if (calendarDropdown.classList.contains('show')) {
                closeAllDropdowns();
            } else {
                showCalendarDropdown();
            }
        });
    }

    if (calendarPrev) {
        calendarPrev.addEventListener('click', function (e) {
            e.stopPropagation();
            leftMonth.setMonth(leftMonth.getMonth() - 1);
            rightMonth.setMonth(rightMonth.getMonth() - 1);
            updateCalendars();
        });
    }

    if (calendarNext) {
        calendarNext.addEventListener('click', function (e) {
            e.stopPropagation();
            leftMonth.setMonth(leftMonth.getMonth() + 1);
            rightMonth.setMonth(rightMonth.getMonth() + 1);
            updateCalendars();
        });
    }

    if (guestsWrapper) {
        guestsWrapper.addEventListener('click', function (e) {
            if (e.target.closest('.counter-btn')) return;
            if (e.target.closest('.apply-guests-btn')) return;

            if (guestsDropdown.classList.contains('show')) {
                closeAllDropdowns();
            } else {
                showGuestsDropdown();
            }
        });
    }

    document.querySelectorAll('.counter-btn').forEach(btn => {
        btn.addEventListener('click', function (e) {
            e.stopPropagation();
            const type = this.dataset.type;
            const isPlus = this.classList.contains('plus');

            if (isPlus) {
                const maxValue = type === 'rooms' ? 10 : 20;
                if (guests[type] < maxValue) {
                    guests[type]++;
                }
            } else {
                const minValue = type === 'children' ? 0 : 1;
                if (guests[type] > minValue) {
                    guests[type]--;
                }
            }
            updateGuestsDisplay();
        });
    });

    const applyGuestsBtn = document.getElementById('applyGuests');
    if (applyGuestsBtn) {
        applyGuestsBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            closeAllDropdowns();
        });
    }

    const searchBtn = document.getElementById('searchCategories');
    if (searchBtn) {
        searchBtn.addEventListener('click', function () {
            const checkin = checkinDate.toISOString().split('T')[0];
            const checkout = checkoutDate.toISOString().split('T')[0];
            const container = document.querySelector('.booking-steps');
            if (!container) return;
            showLoading(container);
            fetch(`/booking/search?fromDate=${checkin}&toDate=${checkout}&rooms=${guests.rooms}&adults=${guests.adults}&children=${guests.children}`, {
                method: 'GET'
            })
                .then(res => res.text())
                .then(html => {
                    container.innerHTML = html;
                    totalAmountDisplay = document.getElementById('totalAmount');
                    const reserveBtn = document.getElementById('reserveBtn');
                    if (reserveBtn) {
                        reserveBtn.addEventListener('click', function () {
                            const categories = Object.keys(bookingState).map(categoryId => ({
                                categoryId: categoryId,
                                rooms: bookingState[categoryId].rooms
                            }));
                            const payload = {
                                fromDate: checkin,
                                toDate: checkout,
                                nights: calculateNights(),
                                categories: categories
                            }
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
                    initGallery();
                });
        });
    }

    if (dropdownOverlay) {
        dropdownOverlay.addEventListener('click', function () {
            closeAllDropdowns();
        });
    }

    document.addEventListener('click', function (e) {
        if (!e.target.closest('.date-range-picker') &&
            !e.target.closest('.guests-wrapper') &&
            !e.target.closest('.calendar-dropdown') &&
            !e.target.closest('.guests-dropdown')
        ) {
            closeAllDropdowns();
        }
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

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
            closeAllDropdowns();
        }
    });

    initGallery();
    updateDateDisplays();
    updateGuestsDisplay();
    updateTotalAmount();
});
