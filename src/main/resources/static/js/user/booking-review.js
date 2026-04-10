// Room tab switching
document.addEventListener('DOMContentLoaded', function () {
    // ── Room tabs ──
    const tabs = document.querySelectorAll('.room-tab');
    const panels = document.querySelectorAll('.room-panel');
    const hideDetailsBtn = document.getElementById('hide-room-details');
    const roomDetailContent = document.getElementById('room-detail-content');
    const hideSpecialBtn = document.getElementById('hide-special-request');
    const specialBody = document.getElementById('special-request-body');
    const applyBtn = document.querySelector('.discount-apply-btn');
    const payBtn = document.querySelector('.payment-btn');
    const phoneInput = document.querySelector(".phone-input");
    const countryInput = document.querySelector(".country-input");

    let expiredAt;
    const reservationId = document.getElementById("reservationId").value;
    const countdownEl = document.getElementById("countdownText");
    const progressBar = document.getElementById("progressBar");
    const modal = document.getElementById("expiredModal");

    // Load Timer
    const savedExpired = sessionStorage.getItem('expiredAt_' + reservationId);
    if (savedExpired) {
        expiredAt = new Date(Number(savedExpired));
    } else {
        const serverExpired = document.getElementById("expiredAt").value;
        expiredAt = new Date(serverExpired);
        sessionStorage.setItem('expiredAt_' + reservationId, expiredAt.getTime());
    }

    // Multi Tab Lock
    const lockKey = "booking_lock_" + reservationId;
    if (localStorage.getItem(lockKey)) {
        alert("Booking already opened in another tab!");
        window.location.href = "/booking";
    }
    localStorage.setItem(lockKey, "locked");
    window.addEventListener("beforeunload", () => {
        localStorage.removeItem(lockKey);
    });

    // Save From Data
    const form = document.getElementById("booking-form");
    form.addEventListener("input", (e) => {
        const data = new FormData(form);
        const obj = Object.fromEntries(data.entries());
        sessionStorage.setItem("booking_form", JSON.stringify(obj));
    })
    const savedForm = sessionStorage.getItem("booking_form");
    if (savedForm) {
        const data = JSON.parse(savedForm);
        Object.keys(data).forEach(key => {
            const el = form.querySelector(`[name="${key}"]`);
            if (el) el.value = data[key];
        })
    }

    // Countdown Time
    const TOTAL_TIME = expiredAt - new Date();
    function handleExpired() {
        payBtn.disabled = true;
        modal.style.display = "block";
        fetch(`api/reservations/cancel/${reservationId}`, {
            method: 'POST',
        }).catch(() => {});
    }

    function updateCountdown() {
        const now = new Date();
        const diff = expiredAt - now;
        console.log(diff);
        if (diff <= 0) {
            handleExpired();
            return;
        }
        const minutes = Math.floor(diff / 1000 / 60);
        const seconds = Math.floor((diff / 1000) % 60);

        countdownEl.innerText = `Time left: ${minutes}:${seconds.toString().padStart(2, '0')}`;
        const percent = (diff / TOTAL_TIME) * 100;
        progressBar.style.width = percent + "%";

        if(percent < 30) progressBar.style.background = "orange";
        if(percent < 10) progressBar.style.background = "red";
    }
    setInterval(updateCountdown, TOTAL_TIME);


    const iti = window.intlTelInput(phoneInput, {
        initialCountry: "vn",
        separateDialCode: true,
        preferredCountries: ["vn", "us", "gb", "jp", "kr"],
    });

    const countryMap = {
        "vietnam": "vn",
        "united states": "us",
        "united kingdom": "gb",
        "japan": "jp",
        "korea": "kr"
    };

    tabs.forEach(function (tab) {
        tab.addEventListener('click', function () {
            const index = this.getAttribute('data-room');
            tabs.forEach(t => t.classList.remove('room-tab--active'));
            this.classList.add('room-tab--active');
            panels.forEach(p => p.style.display = 'none');

            const activePanel = document.getElementById(`room-panel-${index}`);
            if (activePanel) {
                activePanel.style.display = 'block';
            }
        });
    });

    if (hideDetailsBtn && roomDetailContent) {
        hideDetailsBtn.addEventListener('click', function () {
            const isHidden = roomDetailContent.style.display === 'none';
            roomDetailContent.style.display = isHidden ? '' : 'none';
            hideDetailsBtn.querySelector('.toggle-label').textContent = isHidden ? 'Hide details' : 'Show details';
        });
    }

    if (hideSpecialBtn && specialBody) {
        hideSpecialBtn.addEventListener('click', function () {
            const isHidden = specialBody.style.display === 'none';
            specialBody.style.display = isHidden ? '' : 'none';
            hideSpecialBtn.querySelector('.toggle-label').textContent = isHidden ? 'Hide details' : 'Show details';
        });
    }

    // ── Discount code apply ──
    if (applyBtn) {
        applyBtn.addEventListener('click', function () {
            const discountInput = document.querySelector('.discount-input');
            if (discountInput && discountInput.value.trim()) {
                alert('Discount code "' + discountInput.value.trim() + '" applied!');
            }
        });
    }

    // ── Payment button ──
    if (payBtn) {
        payBtn.addEventListener('click', function () {
            // Validate booker name & email
            const bookerName = document.getElementById('booker-name');
            const bookerEmail = document.getElementById('booker-email');
            if (bookerName && !bookerName.value.trim()) {
                bookerName.focus();
                return;
            }
            if (bookerEmail && !bookerEmail.value.trim()) {
                bookerEmail.focus();
                return;
            }
            const data = {
                full_name : bookerName,
                email : bookerEmail.value.trim(),
                phone : phoneInput.value.trim(),
            }
            fetch(`api/reservations/update/${reservationId}`, {
                method: 'POST',
                credentials: 'include',
                body: JSON.stringify(data),
            }).then(res => res.json())
                .then(reservation => {
                    sessionStorage.removeItem("booking_form");
                    sessionStorage.removeItem("expiredAt_" + reservationId);
                    window.location.href = `/booking/payment?reservationId=${reservationId}`;
                }).catch(error => {
                    console.log(error);
            })
        });
    }

    phoneInput.addEventListener("countrychange", function () {
        const data = iti.getSelectedCountryData();
        countryInput.value = data.name;
    });

    countryInput.addEventListener("input", function () {
        const value = countryInput.value.toLowerCase();
        if(countryMap[value]){
            iti.setCountry(countryMap[value]);
        }
    });

    document.getElementById("closeModal").onclick = function () {
        sessionStorage.removeItem("booking_form");
        sessionStorage.removeItem("expiredAt_" + reservationId);
        window.location.href = "/booking";
    }
});
