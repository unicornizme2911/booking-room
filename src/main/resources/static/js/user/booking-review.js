// Room tab switching
document.addEventListener('DOMContentLoaded', function () {
    // ── Room tabs ──
    const tabs = document.querySelectorAll('.room-tab');
    const imGuestCheckbox = document.getElementById('im-guest');
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
            tabs.forEach(function (t) { t.classList.remove('room-tab--active'); });
            tab.classList.add('room-tab--active');
        });
    });

    // ── Booker "I'm guest" checkbox fills in guest form from booker fields ──
    if (imGuestCheckbox) {
        imGuestCheckbox.addEventListener('change', function () {
            const bookerName = document.getElementById('booker-name') ? document.getElementById('booker-name').value : '';
            const bookerEmail = document.getElementById('booker-email') ? document.getElementById('booker-email').value : '';
            const guestName = document.getElementById('guest-name');
            const guestEmail = document.getElementById('guest-email');
            if (imGuestCheckbox.checked) {
                if (guestName) guestName.value = bookerName;
                if (guestEmail) guestEmail.value = bookerEmail;
            } else {
                if (guestName) guestName.value = '';
                if (guestEmail) guestEmail.value = '';
            }
        });
    }

    // ── Special request / Room detail toggle (Hide details) ──

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
            // In real Spring Boot: form submission via th:action
            alert('Proceeding to payment...');
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
});
